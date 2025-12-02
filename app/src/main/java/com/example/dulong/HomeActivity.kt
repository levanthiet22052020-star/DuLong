package com.example.dulong

import android.content.Context
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    // --- View Trang Chủ ---
    private lateinit var rcvHot: RecyclerView
    private lateinit var rcvSale: RecyclerView
    private lateinit var scrollHomeContent: ScrollView // Nội dung chính (Hot/Sale)

    // --- View Tìm Kiếm / Lọc ---
    private lateinit var layoutSearchResults: LinearLayout // Khung kết quả tìm kiếm
    private lateinit var rcvSearchResults: RecyclerView
    private lateinit var tvSearchResultLabel: TextView
    private lateinit var edtSearch: EditText
    private lateinit var btnSearchIcon: ImageView

    // --- Menu & Nav ---
    private lateinit var categoryOverlay: LinearLayout
    private lateinit var btnMenu: ImageView
    private lateinit var btnCart: ImageView
    private lateinit var btnProfile: ImageView
    private lateinit var btnHome: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupEvents()

        // Load dữ liệu mặc định (Trang chủ)
        loadHomeProducts()
    }

    private fun initViews() {
        // Trang chủ
        rcvHot = findViewById(R.id.rcvHot)
        rcvSale = findViewById(R.id.rcvSale)
        scrollHomeContent = findViewById(R.id.scrollHomeContent)

        // Tìm kiếm
        layoutSearchResults = findViewById(R.id.layoutSearchResults)
        rcvSearchResults = findViewById(R.id.rcvSearchResults)
        tvSearchResultLabel = findViewById(R.id.tvSearchResultLabel)
        edtSearch = findViewById(R.id.edtSearch)
        btnSearchIcon = findViewById(R.id.btnSearchIcon)

        // Menu & Nav
        categoryOverlay = findViewById(R.id.categoryOverlay)
        btnMenu = findViewById(R.id.btnMenu)
        btnCart = findViewById(R.id.btnCart)
        btnProfile = findViewById(R.id.btnProfile)
        btnHome = findViewById(R.id.btnHome)
    }

    private fun setupEvents() {
        // 1. Bấm nút Menu (3 gạch) -> Hiện/Ẩn danh mục
        btnMenu.setOnClickListener {
            if (categoryOverlay.visibility == View.VISIBLE) {
                categoryOverlay.visibility = View.GONE
            } else {
                categoryOverlay.visibility = View.VISIBLE
                categoryOverlay.bringToFront() // Đưa lên trên cùng
            }
        }

        // 2. Bấm nút Search -> Tìm theo từ khóa trong ô nhập
        btnSearchIcon.setOnClickListener {
            val keyword = edtSearch.text.toString().trim()
            if (keyword.isNotEmpty()) {
                performSearch(keyword)
                hideKeyboard()
                categoryOverlay.visibility = View.GONE
            }
        }

        // 3. Xử lý bấm vào từng Danh mục (Lọc sản phẩm)
        setupCategoryClick(R.id.cat_yonex, "Yonex")
        setupCategoryClick(R.id.cat_lining, "Lining")
        setupCategoryClick(R.id.cat_vector, "Vector")
        setupCategoryClick(R.id.cat_mizuno, "Mizuno")
        setupCategoryClick(R.id.cat_flypower, "Flypower")

        setupCategoryClick(R.id.cat_day_yonex, "Dây Yonex")
        setupCategoryClick(R.id.cat_day_lining, "Dây Lining")
        setupCategoryClick(R.id.cat_day_kizuna, "Kizuna")

        setupCategoryClick(R.id.cat_cau_vnb, "Cầu VNB")
        setupCategoryClick(R.id.cat_cau_vinastar, "Vina Star")
        setupCategoryClick(R.id.cat_cau_basao, "Ba Sao")

        setupCategoryClick(R.id.cat_ao, "Áo")
        setupCategoryClick(R.id.cat_quan, "Quần")

        // 4. Navigation
        btnCart.setOnClickListener { startActivity(Intent(this, CardActivity::class.java)) }
        btnProfile.setOnClickListener { startActivity(Intent(this, UserProfileActivity::class.java)) }

        // Bấm nút Home -> Quay lại trang chủ (Ẩn tìm kiếm)
        btnHome.setOnClickListener {
            showHomeLayout()
        }
    }

    // Hàm gán sự kiện cho từng dòng danh mục
    private fun setupCategoryClick(viewId: Int, keyword: String) {
        findViewById<TextView>(viewId).setOnClickListener {
            // 1. Điền từ khóa vào ô tìm kiếm để user biết
            edtSearch.setText(keyword)
            // 2. Gọi hàm tìm kiếm
            performSearch(keyword)
            // 3. Ẩn menu đi
            categoryOverlay.visibility = View.GONE
        }
    }

    // Hàm thực hiện tìm kiếm/lọc
    private fun performSearch(keyword: String) {
        // Ẩn trang chủ, hiện trang kết quả
        scrollHomeContent.visibility = View.GONE
        layoutSearchResults.visibility = View.VISIBLE
        tvSearchResultLabel.text = "Đang tìm kiếm '$keyword'..."

        // Gọi API: type=null, search=keyword
        RetrofitClient.instance.getListProduct(null, keyword).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val list = response.body()!!.data

                    if (list.isEmpty()) {
                        tvSearchResultLabel.text = "Không tìm thấy sản phẩm nào cho '$keyword'"
                    } else {
                        tvSearchResultLabel.text = "Tìm thấy ${list.size} sản phẩm"
                    }

                    // Hiển thị kết quả
                    val adapter = ProductAdapter(list, this@HomeActivity)
                    rcvSearchResults.layoutManager = GridLayoutManager(this@HomeActivity, 2)
                    rcvSearchResults.adapter = adapter
                } else {
                    tvSearchResultLabel.text = "Lỗi tìm kiếm"
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Hàm quay lại trang chủ
    private fun showHomeLayout() {
        layoutSearchResults.visibility = View.GONE
        scrollHomeContent.visibility = View.VISIBLE
        edtSearch.setText("") // Xóa ô tìm kiếm
        hideKeyboard()
    }

    // Load dữ liệu ban đầu (Hot & Sale)
    private fun loadHomeProducts() {
        loadProductByType("hot", rcvHot)
        loadProductByType("sale", rcvSale)
    }

    private fun loadProductByType(type: String, recyclerView: RecyclerView) {
        // API search rỗng ("") để lấy theo type
        RetrofitClient.instance.getListProduct(type, "").enqueue(object : Callback<ProductResponse> {
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

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}