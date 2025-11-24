package com.example.dulong

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProductActivity : AppCompatActivity() {

    private lateinit var rcvCategories: RecyclerView
    private lateinit var rcvProducts: RecyclerView
    private lateinit var adminMenuDropdown: View

    // Adapter
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapter: ProductAdapter

    private val listCategory = mutableListOf<Category>()
    private val listProduct = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_product)

        // Ánh xạ
        val adminBtnMenu = findViewById<ImageView>(R.id.adminBtnMenu)
        adminMenuDropdown = findViewById(R.id.adminMenuDropdown)
        rcvCategories = findViewById(R.id.rcvCategories)
        rcvProducts = findViewById(R.id.rcvProducts)
        val btnAddProduct = findViewById<View>(R.id.adminBtnAddProduct) // Nút thêm

        // 1. Setup RecyclerView Danh mục
        categoryAdapter = CategoryAdapter(listCategory)
        rcvCategories.layoutManager = LinearLayoutManager(this)
        rcvCategories.adapter = categoryAdapter

        // 2. Setup RecyclerView Sản phẩm
        productAdapter = ProductAdapter(listProduct,
            onEditClick = { product ->
                // Bấm sửa -> Gọi hàm hiện Dialog kèm dữ liệu cũ
                showAddOrUpdateDialog(product)
            },
            onDeleteClick = { product ->
                // Bấm xóa -> Gọi hàm xác nhận xóa
                confirmDelete(product)
            }
        )
        rcvProducts.layoutManager = LinearLayoutManager(this)
        rcvProducts.adapter = productAdapter

        // 3. Xử lý click Menu
        adminBtnMenu.setOnClickListener {
            if (adminMenuDropdown.visibility == View.VISIBLE) {
                adminMenuDropdown.visibility = View.GONE
            } else {
                adminMenuDropdown.visibility = View.VISIBLE
                adminMenuDropdown.bringToFront()
                fetchCategories()
            }
        }

        // 4. Xử lý nút Thêm (Gọi hàm Dialog với tham số null)
        btnAddProduct.setOnClickListener {
            showAddOrUpdateDialog(null)
        }

        // Load danh sách ban đầu
        fetchProducts()
    }

    // --- HÀM HIỆN DIALOG THÊM/SỬA ---
    private fun showAddOrUpdateDialog(product: Product?) {
        // Tạo Dialog từ file xml dialog_add_product
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val edtName = dialogView.findViewById<EditText>(R.id.edtName)
        val edtPrice = dialogView.findViewById<EditText>(R.id.edtPrice)
        val edtImage = dialogView.findViewById<EditText>(R.id.edtImage)
        val edtType = dialogView.findViewById<EditText>(R.id.edtType)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        // Tạo AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // Kiểm tra: Nếu product != null => Là SỬA => Điền sẵn dữ liệu
        if (product != null) {
            tvTitle.text = "Cập nhật sản phẩm"
            edtName.setText(product.name)
            edtPrice.setText(product.price.toString())
            edtImage.setText(product.image ?: "")
            edtType.setText(product.type ?: "normal")
        } else {
            tvTitle.text = "Thêm sản phẩm mới"
        }

        // Xử lý nút Lưu
        btnSave.setOnClickListener {
            val name = edtName.text.toString()
            val priceStr = edtPrice.text.toString()
            val image = edtImage.text.toString()
            val type = edtType.text.toString()

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên và giá", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull() ?: 0.0

            // Tạo đối tượng Body để gửi lên server
            val body = ProductBody(name, price, image, type)

            if (product == null) {
                // --- LOGIC THÊM MỚI ---
                createProduct(body, dialog)
            } else {
                // --- LOGIC CẬP NHẬT ---
                updateProduct(product._id, body, dialog)
            }
        }

        dialog.show()
    }

    // Gọi API Thêm
    private fun createProduct(body: ProductBody, dialog: AlertDialog) {
        RetrofitClient.instance.addProduct(body).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@AdminProductActivity, "Thêm thành công", Toast.LENGTH_SHORT).show()
                    fetchProducts() // Load lại danh sách
                    dialog.dismiss() // Tắt dialog
                } else {
                    Toast.makeText(this@AdminProductActivity, "Thêm thất bại", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                Toast.makeText(this@AdminProductActivity, "Lỗi server: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Gọi API Sửa
    private fun updateProduct(id: String, body: ProductBody, dialog: AlertDialog) {
        RetrofitClient.instance.updateProduct(id, body).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@AdminProductActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                    fetchProducts()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this@AdminProductActivity, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                Toast.makeText(this@AdminProductActivity, "Lỗi server", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- CÁC HÀM CŨ (GIỮ NGUYÊN) ---
    private fun fetchCategories() {
        RetrofitClient.instance.getListCategory().enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    listCategory.clear()
                    response.body()?.data?.let { listCategory.addAll(it) }
                    categoryAdapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {}
        })
    }

    // --- 2. GỌI API LẤY SẢN PHẨM ---
    private fun fetchProducts() {
        // Sửa dòng này: truyền thêm null vào tham số thứ 2
        // getListProduct(type, search)
        RetrofitClient.instance.getListProduct(null, null).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    listProduct.clear()
                    response.body()?.data?.let { listProduct.addAll(it) }
                    productAdapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Toast.makeText(this@AdminProductActivity, "Lỗi mạng sản phẩm", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmDelete(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa ${product.name}?")
            .setPositiveButton("Xóa") { _, _ ->
                RetrofitClient.instance.deleteProduct(product._id).enqueue(object : Callback<GeneralResponse> {
                    override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                        if (response.isSuccessful && response.body()?.status == true) {
                            Toast.makeText(this@AdminProductActivity, "Đã xóa", Toast.LENGTH_SHORT).show()
                            fetchProducts()
                        }
                    }
                    override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {}
                })
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    // --- ADAPTERS (GIỮ NGUYÊN NHƯ CŨ) ---
    inner class CategoryAdapter(private val list: List<Category>) : RecyclerView.Adapter<CategoryAdapter.CatViewHolder>() {
        inner class CatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvCategoryName)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
            return CatViewHolder(view)
        }
        override fun onBindViewHolder(holder: CatViewHolder, position: Int) {
            holder.tvName.text = list[position].name
        }
        override fun getItemCount() = list.size
    }

    inner class ProductAdapter(
        private val list: List<Product>,
        private val onEditClick: (Product) -> Unit,
        private val onDeleteClick: (Product) -> Unit
    ) : RecyclerView.Adapter<ProductAdapter.ProdViewHolder>() {

        inner class ProdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvName: TextView = view.findViewById(R.id.tvProductName)
            val tvPrice: TextView = view.findViewById(R.id.tvProductPrice)
            val btnDelete: ImageView = view.findViewById(R.id.adminBtnDelete)
            val btnEdit: ImageView = view.findViewById(R.id.adminBtnEdit)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin_product, parent, false)
            return ProdViewHolder(view)
        }

        override fun onBindViewHolder(holder: ProdViewHolder, position: Int) {
            val item = list[position]
            holder.tvName.text = item.name
            holder.tvPrice.text = "${item.price} đ"
            holder.btnDelete.setOnClickListener { onDeleteClick(item) }
            holder.btnEdit.setOnClickListener { onEditClick(item) }
        }

        override fun getItemCount() = list.size
    }
}