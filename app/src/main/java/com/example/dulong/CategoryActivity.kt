package com.example.dulong

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryActivity : AppCompatActivity() {

    private lateinit var rcvUserProducts: RecyclerView
    private lateinit var userProductAdapter: UserProductAdapter
    private val listProduct = mutableListOf<Product>()

    // Ánh xạ các view tìm kiếm
    private lateinit var adminSearchBar: EditText
    private lateinit var adminBtnSearch: ImageView
    private lateinit var adminBtnCart: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_category)

        // Xử lý System Bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Ánh xạ View
        rcvUserProducts = findViewById(R.id.rcvUserProducts)
        adminSearchBar = findViewById(R.id.adminSearchBar)
        adminBtnSearch = findViewById(R.id.adminBtnSearch)
        adminBtnCart = findViewById(R.id.adminBtnCart)

        // 2. Setup RecyclerView (Grid 2 cột)
        userProductAdapter = UserProductAdapter(listProduct)
        rcvUserProducts.layoutManager = GridLayoutManager(this, 2)
        rcvUserProducts.adapter = userProductAdapter

        // 3. Load danh sách sản phẩm ban đầu (Lấy tất cả)
        fetchProducts(null)

        // 4. XỬ LÝ TÌM KIẾM
        adminBtnSearch.setOnClickListener {
            val keyword = adminSearchBar.text.toString().trim()
            if (keyword.isNotEmpty()) {
                fetchProducts(keyword) // Gọi API với từ khóa
            } else {
                fetchProducts(null) // Nếu rỗng thì load lại tất cả
            }
        }

        // 5. XỬ LÝ CHUYỂN GIỎ HÀNG
        adminBtnCart.setOnClickListener {
            val intent = Intent(this@CategoryActivity, CardActivity::class.java)
            startActivity(intent)
        }
    }

    // --- HÀM GỌI API ---
    private fun fetchProducts(searchKeyword: String?) {
        // searchKeyword truyền vào tham số 'search', type để null
        RetrofitClient.instance.getListProduct(type = null, search = searchKeyword)
            .enqueue(object : Callback<ProductResponse> {
                override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                    if (response.isSuccessful && response.body()?.status == true) {
                        listProduct.clear()
                        response.body()?.data?.let { listProduct.addAll(it) }
                        userProductAdapter.notifyDataSetChanged()

                        if (listProduct.isEmpty()) {
                            Toast.makeText(this@CategoryActivity, "Không tìm thấy sản phẩm nào", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                    Toast.makeText(this@CategoryActivity, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                }
            })
    }

    // --- ADAPTER CHO USER (Viết Inner Class cho gọn) ---
    inner class UserProductAdapter(private val list: List<Product>) :
        RecyclerView.Adapter<UserProductAdapter.UserProdViewHolder>() {

        inner class UserProdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val img: ImageView = view.findViewById(R.id.imgProduct)
            val name: TextView = view.findViewById(R.id.tvName)
            val price: TextView = view.findViewById(R.id.tvPrice)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserProdViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_product, parent, false)
            return UserProdViewHolder(view)
        }

        override fun onBindViewHolder(holder: UserProdViewHolder, position: Int) {
            val item = list[position]
            holder.name.text = item.name
            holder.price.text = "${item.price} đ"

            // Nếu bạn dùng thư viện Glide hoặc Picasso để load ảnh:
            // Glide.with(holder.itemView).load(item.image).into(holder.img)
        }

        override fun getItemCount() = list.size
    }
}