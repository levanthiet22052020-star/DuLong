package com.example.dulong

import android.content.Intent
import android.os.Bundle
import android.view.View // [Mới] Import View để dùng Visible/Gone
import android.widget.ImageView // [Mới] Import ImageView
import android.widget.LinearLayout // [Mới] Import LinearLayout
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var rcvHot: RecyclerView
    private lateinit var rcvSale: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        // 1. Ánh xạ View
        rcvHot = findViewById(R.id.rcvHot)
        rcvSale = findViewById(R.id.rcvSale)

        // 2. Gọi hàm lấy dữ liệu
        getProducts("hot", rcvHot)
        getProducts("sale", rcvSale)
    }

    private fun getProducts(type: String, recyclerView: RecyclerView) {
        // Gọi API thông qua RetrofitClient
        RetrofitClient.instance.getListProduct(type, "")
            .enqueue(object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>
                ) {
                    if (response.isSuccessful) {
                        val productResponse = response.body()
                        if (productResponse != null && productResponse.status) {
                            // Lấy danh sách sản phẩm từ server
                            val listProduct = productResponse.data

                            // Setup RecyclerView (2 cột)
                            val adapter = ProductAdapter(listProduct, this@HomeActivity)
                            recyclerView.layoutManager = GridLayoutManager(this@HomeActivity, 2)
                            recyclerView.adapter = adapter
                        }
                    } else {
                        Toast.makeText(this@HomeActivity, "Lỗi tải dữ liệu: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    Log.e("HomeActivity", "Lỗi: ${t.message}")
                    Toast.makeText(this@HomeActivity, "Lỗi kết nối server", Toast.LENGTH_SHORT).show()
                }
            })

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

        btnCart.setOnClickListener {
            val intent = Intent(this@HomeActivity, CardActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnProfile.setOnClickListener {
            val intent = Intent(this@HomeActivity, UserProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        categoryOverlay.setOnClickListener {
            categoryOverlay.visibility = View.GONE
        }
    }
}