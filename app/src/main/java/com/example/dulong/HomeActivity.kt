package com.example.dulong

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
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

    // View cho trang chủ
    private lateinit var rcvHot: RecyclerView
    private lateinit var rcvSale: RecyclerView
    private lateinit var layoutHomeContent: ScrollView

    // View cho tìm kiếm
    private lateinit var edtSearch: EditText
    private lateinit var btnSearchIcon: ImageView
    private lateinit var layoutSearchResult: LinearLayout
    private lateinit var rcvSearch: RecyclerView
    private lateinit var tvSearchResultCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupListeners()

        // Load dữ liệu mặc định cho trang chủ
        getProducts("hot", rcvHot)
        getProducts("sale", rcvSale)
    }

    private fun initViews() {
        // Ánh xạ trang chủ
        rcvHot = findViewById(R.id.rcvHot)
        rcvSale = findViewById(R.id.rcvSale)
        layoutHomeContent = findViewById(R.id.layoutHomeContent)

        // Ánh xạ tìm kiếm
        edtSearch = findViewById(R.id.edtSearch)
        btnSearchIcon = findViewById(R.id.btnSearchIcon)
        layoutSearchResult = findViewById(R.id.layoutSearchResult)
        rcvSearch = findViewById(R.id.rcvSearch)
        tvSearchResultCount = findViewById(R.id.tvSearchResultCount)
    }

    private fun setupListeners() {
        // 1. Xử lý nút tìm kiếm (Kính lúp)
        btnSearchIcon.setOnClickListener {
            val keyword = edtSearch.text.toString().trim()
            handleSearch(keyword)
        }

        // 2. Xử lý nút "Search" trên bàn phím ảo
        edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val keyword = edtSearch.text.toString().trim()
                handleSearch(keyword)
                true
            } else {
                false
            }
        }

        // 3. Navigation và Menu (Giữ code cũ của bạn)
        val btnMenu = findViewById<ImageView>(R.id.btnMenu)
        val btnCart = findViewById<ImageView>(R.id.btnCart)
        val btnProfile = findViewById<ImageView>(R.id.btnProfile)
        val categoryOverlay = findViewById<LinearLayout>(R.id.categoryOverlay)

        btnMenu.setOnClickListener {
            if (categoryOverlay.visibility == View.VISIBLE) {
                categoryOverlay.visibility = View.GONE
            } else {
                categoryOverlay.visibility = View.VISIBLE
                categoryOverlay.bringToFront()
            }
        }

        categoryOverlay.setOnClickListener {
            categoryOverlay.visibility = View.GONE
        }

        btnCart.setOnClickListener {
            val intent = Intent(this@HomeActivity, CardActivity::class.java)
            startActivity(intent)
        }

        btnProfile.setOnClickListener {
            val intent = Intent(this@HomeActivity, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleSearch(keyword: String) {
        // Ẩn bàn phím sau khi bấm tìm
        hideKeyboard()

        if (keyword.isEmpty()) {
            // Nếu ô trống, quay về trang chủ
            layoutSearchResult.visibility = View.GONE
            layoutHomeContent.visibility = View.VISIBLE
            return
        }

        // Gọi API tìm kiếm
        // Tham số type = null (tìm tất cả), search = keyword
        RetrofitClient.instance.getListProduct(null, keyword)
            .enqueue(object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>
                ) {
                    if (response.isSuccessful) {
                        val productResponse = response.body()
                        if (productResponse != null && productResponse.status) {
                            val listData = productResponse.data

                            // Hiển thị kết quả
                            showSearchResults(listData)
                        } else {
                            Toast.makeText(this@HomeActivity, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@HomeActivity, "Lỗi server: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    Log.e("HomeActivity", "Search Error: ${t.message}")
                    Toast.makeText(this@HomeActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showSearchResults(list: List<Product>) {
        // Ẩn trang chủ, hiện trang tìm kiếm
        layoutHomeContent.visibility = View.GONE
        layoutSearchResult.visibility = View.VISIBLE

        tvSearchResultCount.text = "Tìm thấy ${list.size} sản phẩm"

        // Setup RecyclerView
        val adapter = ProductAdapter(list, this@HomeActivity)
        rcvSearch.layoutManager = GridLayoutManager(this@HomeActivity, 2)
        rcvSearch.adapter = adapter
    }

    // Hàm ẩn bàn phím ảo
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    // Hàm lấy danh sách Hot/Sale (Giữ code cũ)
    private fun getProducts(type: String, recyclerView: RecyclerView) {
        RetrofitClient.instance.getListProduct(type, "")
            .enqueue(object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>
                ) {
                    if (response.isSuccessful) {
                        val productResponse = response.body()
                        if (productResponse != null && productResponse.status) {
                            val adapter = ProductAdapter(productResponse.data, this@HomeActivity)
                            recyclerView.layoutManager = GridLayoutManager(this@HomeActivity, 2)
                            recyclerView.adapter = adapter
                        }
                    }
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    Log.e("HomeActivity", "Lỗi: ${t.message}")
                }
            })
    }
}