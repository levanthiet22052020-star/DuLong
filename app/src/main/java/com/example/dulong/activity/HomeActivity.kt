package com.example.dulong.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dulong.R
import com.example.dulong.activity.cart.CardActivity
import com.example.dulong.activity.user.UserProfileActivity
import com.example.dulong.adapter.CategoryAdapter
import com.example.dulong.adapter.ProductAdapter
import com.example.dulong.api.RetrofitClient
import com.example.dulong.model.CategoryResponse
import com.example.dulong.model.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    // --- View Trang Chủ ---
    private lateinit var rcvHot: RecyclerView
    private lateinit var rcvSale: RecyclerView
    private lateinit var scrollHomeContent: ScrollView

    // --- View Tìm Kiếm / Lọc ---
    private lateinit var layoutSearchResults: LinearLayout
    private lateinit var rcvSearchResults: RecyclerView
    private lateinit var tvSearchResultLabel: TextView
    private lateinit var edtSearch: EditText
    private lateinit var btnSearchIcon: ImageView

    // --- Menu & Nav & Danh Mục ---
    private lateinit var categoryOverlay: LinearLayout
    private lateinit var rcvCategories: RecyclerView
    private lateinit var btnMenu: ImageView
    private lateinit var btnCart: ImageView
    private lateinit var btnProfile: ImageView
    private lateinit var btnHome: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupEvents()

        // 1. Load sản phẩm trang chủ (Hot/Sale)
        loadHomeProducts()

        // 2. Load danh mục từ Server
        loadCategories()
    }

    private fun initViews() {
        // Ánh xạ View Trang chủ
        rcvHot = findViewById(R.id.rcvHot)
        rcvSale = findViewById(R.id.rcvSale)
        scrollHomeContent = findViewById(R.id.scrollHomeContent)

        // Ánh xạ View Tìm kiếm
        layoutSearchResults = findViewById(R.id.layoutSearchResults)
        rcvSearchResults = findViewById(R.id.rcvSearchResults)
        tvSearchResultLabel = findViewById(R.id.tvSearchResultLabel)
        edtSearch = findViewById(R.id.edtSearch)
        btnSearchIcon = findViewById(R.id.btnSearchIcon)

        // Ánh xạ Menu & Nav
        categoryOverlay = findViewById(R.id.categoryOverlay)
        rcvCategories = findViewById(R.id.rcvCategories) // RecyclerView trong menu sổ xuống

        btnMenu = findViewById(R.id.btnMenu)
        btnCart = findViewById(R.id.btnCart)
        btnProfile = findViewById(R.id.btnProfile)
        btnHome = findViewById(R.id.btnHome)
    }

    private fun setupEvents() {
        // --- Sự kiện Menu: Ẩn/Hiện danh mục ---
        btnMenu.setOnClickListener {
            if (categoryOverlay.visibility == View.VISIBLE) {
                categoryOverlay.visibility = View.GONE
            } else {
                categoryOverlay.visibility = View.VISIBLE
                categoryOverlay.bringToFront()

                // Nếu chưa có dữ liệu danh mục thì load lại
                if (rcvCategories.adapter == null || rcvCategories.adapter!!.itemCount == 0) {
                    loadCategories()
                }
            }
        }

        // --- Sự kiện Tìm kiếm bằng từ khóa (nhập tay) ---
        btnSearchIcon.setOnClickListener {
            val keyword = edtSearch.text.toString().trim()
            if (keyword.isNotEmpty()) {
                // Gọi tìm kiếm với keyword, categoryId = null
                performSearch(keyword, null)

                // Ẩn bàn phím và menu
                hideKeyboard()
                categoryOverlay.visibility = View.GONE
            }
        }

        // --- Navigation ---
        btnCart.setOnClickListener { startActivity(Intent(this, CardActivity::class.java)) }
        btnProfile.setOnClickListener { startActivity(Intent(this, UserProfileActivity::class.java)) }

        // Nút Home: Quay về màn hình chính
        btnHome.setOnClickListener {
            showHomeLayout()
        }
    }

    private fun loadCategories() {
        RetrofitClient.instance.getCategories().enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val listCat = response.body()!!.data

                    // Setup Adapter
                    val adapter = CategoryAdapter(listCat) { category ->
                        // === XỬ LÝ KHI BẤM VÀO 1 DANH MỤC ===

                        // 1. Hiển thị tên danh mục lên ô tìm kiếm (để người dùng biết đang xem gì)
                        edtSearch.setText(category.name)

                        // 2. Gọi API lọc sản phẩm theo ID danh mục (keyword để null)
                        // Đây là mấu chốt để sửa lỗi "tìm không liên quan"
                        performSearch(null, category._id)

                        // 3. Ẩn menu danh mục đi
                        categoryOverlay.visibility = View.GONE
                        hideKeyboard()
                    }

                    // Hiển thị dạng lưới 3 cột
                    rcvCategories.layoutManager = GridLayoutManager(this@HomeActivity, 3)
                    rcvCategories.adapter = adapter
                }
            }
            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                // Có thể log lỗi
            }
        })
    }

    /**
     * Hàm tìm kiếm chung: Hỗ trợ tìm theo tên HOẶC theo ID danh mục
     */
    private fun performSearch(keyword: String?, categoryId: String?) {
        // Ẩn nội dung trang chủ, hiện layout kết quả
        scrollHomeContent.visibility = View.GONE
        layoutSearchResults.visibility = View.VISIBLE

        // Cập nhật dòng thông báo trạng thái
        if (categoryId != null) {
            tvSearchResultLabel.text = "Đang lọc theo danh mục..."
        } else {
            tvSearchResultLabel.text = "Đang tìm kiếm: '$keyword'..."
        }

        // Gọi API với 3 tham số: type=null, search=keyword, categoryId=categoryId
        RetrofitClient.instance.getListProduct(null, keyword, categoryId).enqueue(object :
            Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val list = response.body()!!.data

                    if (list.isEmpty()) {
                        tvSearchResultLabel.text = "Không tìm thấy sản phẩm nào."
                    } else {
                        tvSearchResultLabel.text = "Tìm thấy ${list.size} sản phẩm."
                    }

                    // Hiển thị kết quả ra RecyclerView
                    val adapter = ProductAdapter(list, this@HomeActivity)
                    rcvSearchResults.layoutManager = GridLayoutManager(this@HomeActivity, 2)
                    rcvSearchResults.adapter = adapter
                } else {
                    tvSearchResultLabel.text = "Lỗi khi tải dữ liệu."
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
                tvSearchResultLabel.text = "Lỗi kết nối."
            }
        })
    }

    // Quay lại giao diện trang chủ ban đầu
    private fun showHomeLayout() {
        layoutSearchResults.visibility = View.GONE
        scrollHomeContent.visibility = View.VISIBLE
        edtSearch.setText("") // Xóa ô tìm kiếm
        hideKeyboard()
        categoryOverlay.visibility = View.GONE
    }

    // Load sản phẩm Hot/Sale mặc định
    private fun loadHomeProducts() {
        loadProductByType("hot", rcvHot)
        loadProductByType("sale", rcvSale)
    }

    // Helper load theo type
    private fun loadProductByType(type: String, recyclerView: RecyclerView) {
        // Tham số search và categoryId để trống
        RetrofitClient.instance.getListProduct(type, null, null).enqueue(object :
            Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val list = response.body()!!.data
                    val adapter = ProductAdapter(list, this@HomeActivity)
                    recyclerView.layoutManager = GridLayoutManager(this@HomeActivity, 2)
                    recyclerView.adapter = adapter
                }
            }
            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {}
        })
    }

    // Ẩn bàn phím ảo
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}