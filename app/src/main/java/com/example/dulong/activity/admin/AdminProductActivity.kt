package com.example.dulong.activity.admin

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dulong.R
import com.example.dulong.adapter.AdminProductAdapter
import com.example.dulong.adapter.CategoryAdapter
import com.example.dulong.api.RetrofitClient
import com.example.dulong.model.CategoryResponse
import com.example.dulong.model.GeneralResponse
import com.example.dulong.model.Product
import com.example.dulong.model.ProductBody
import com.example.dulong.model.ProductResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProductActivity : AppCompatActivity() {

    // --- Khai báo View ---
    private lateinit var btnMenu: ImageView
    private lateinit var edtSearch: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var btnAddProduct: Button
    private lateinit var rcvProducts: RecyclerView

    // Phần Menu Dropdown
    private lateinit var menuDropdown: CardView
    private lateinit var rcvCategories: RecyclerView
    private lateinit var bottomNav: LinearLayout

    // Biến dữ liệu
    private var productList: MutableList<Product> = mutableListOf()
    private lateinit var adminAdapter: AdminProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_product)

        initViews()
        setupEvents()

        // Tải dữ liệu ban đầu
        loadAllProducts()
        loadCategories()
    }

    private fun initViews() {
        btnMenu = findViewById(R.id.adminBtnMenu)
        edtSearch = findViewById(R.id.adminSearchBar)
        btnSearch = findViewById(R.id.adminBtnSearch)
        btnAddProduct = findViewById(R.id.adminBtnAddProduct)
        rcvProducts = findViewById(R.id.rcvProducts)
        menuDropdown = findViewById(R.id.adminMenuDropdown)
        rcvCategories = findViewById(R.id.rcvCategories)
        bottomNav = findViewById(R.id.adminBottomNav)
    }

    private fun setupEvents() {
        // 1. Menu Toggle
        btnMenu.setOnClickListener {
            if (menuDropdown.visibility == View.VISIBLE) {
                menuDropdown.visibility = View.GONE
            } else {
                menuDropdown.visibility = View.VISIBLE
                menuDropdown.bringToFront()
            }
        }

        // 2. Tìm kiếm
        btnSearch.setOnClickListener {
            val keyword = edtSearch.text.toString().trim()
            performSearch(keyword, null)
            hideKeyboard()
            menuDropdown.visibility = View.GONE
        }

        // 3. Thêm sản phẩm (Mở Dialog)
        btnAddProduct.setOnClickListener {
            showAddProductDialog()
        }

        // 4. Click ra ngoài để đóng menu
        rcvProducts.setOnTouchListener { _, _ ->
            if (menuDropdown.visibility == View.VISIBLE) {
                menuDropdown.visibility = View.GONE
            }
            false
        }
    }

    // ==========================================
    // PHẦN 1: TẢI DỮ LIỆU & TÌM KIẾM
    // ==========================================

    private fun loadCategories() {
        RetrofitClient.instance.getCategories().enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    val listCat = response.body()!!.data

                    // Xử lý sự kiện khi bấm vào một dòng danh mục
                    val adapter = CategoryAdapter(listCat) { category ->

                        // 1. Điền tên danh mục vào ô tìm kiếm
                        edtSearch.setText(category.name)

                        // 2. Ẩn menu dropdown
                        menuDropdown.visibility = View.GONE

                        // 3. Ẩn bàn phím (để nhìn thấy danh sách ngay)
                        hideKeyboard()

                        // 4. [QUAN TRỌNG] Gọi tìm kiếm NGAY LẬP TỨC
                        // Truyền cả tên (keyword) và ID để đảm bảo tìm chính xác nhất
                        performSearch(category.name, category._id)
                    }

                    rcvCategories.layoutManager = LinearLayoutManager(this@AdminProductActivity)
                    rcvCategories.adapter = adapter
                }
            }
            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) { }
        })
    }

    private fun loadAllProducts() {
        performSearch(null, null)
    }

    private fun performSearch(keyword: String?, categoryId: String?) {
        RetrofitClient.instance.getListProduct(null, keyword, categoryId).enqueue(object :
            Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    productList = response.body()!!.data.toMutableList()
                    setupRecyclerView(productList)
                } else {
                    Toast.makeText(this@AdminProductActivity, "Không tải được dữ liệu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Toast.makeText(this@AdminProductActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView(list: List<Product>) {
        adminAdapter = AdminProductAdapter(
            list,
            onEdit = { product -> handleEditProduct(product) },
            onDelete = { product -> confirmDeleteProduct(product) }
        )
        rcvProducts.layoutManager = LinearLayoutManager(this@AdminProductActivity)
        rcvProducts.adapter = adminAdapter
    }



    private fun showAddProductDialog() {
        // Nạp layout dialog
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        // Ánh xạ view trong dialog
        val edtName = dialogView.findViewById<EditText>(R.id.edtName)
        val edtPrice = dialogView.findViewById<EditText>(R.id.edtPrice)
        val edtImage = dialogView.findViewById<EditText>(R.id.edtImage)
        val edtType = dialogView.findViewById<EditText>(R.id.edtType)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        // Sự kiện nút Lưu
        btnSave.setOnClickListener {
            val name = edtName.text.toString().trim()
            val priceStr = edtPrice.text.toString().trim()
            val image = edtImage.text.toString().trim()
            val type = edtType.text.toString().trim()

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên và giá", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull() ?: 0.0

            // Tạo object ProductBody
            val newProduct = ProductBody(name, price, image, type)

            // Gọi API Thêm
            createProductAPI(newProduct, dialog)
        }

        dialog.show()
    }

    private fun createProductAPI(productBody: ProductBody, dialog: Dialog) {
        RetrofitClient.instance.addProduct(productBody).enqueue(object : Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@AdminProductActivity, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss() // Đóng popup
                    loadAllProducts() // Load lại danh sách
                } else {
                    val msg = response.body()?.message ?: "Thêm thất bại"
                    Toast.makeText(this@AdminProductActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                Toast.makeText(this@AdminProductActivity, "Lỗi: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // ==========================================
    // PHẦN 3: SỬA SẢN PHẨM
    // ==========================================

    private fun handleEditProduct(product: Product) {
        showEditProductDialog(product)
    }

    private fun showEditProductDialog(product: Product) {
        // Tái sử dụng layout dialog thêm
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)

        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()

        // Ánh xạ View
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val edtName = dialogView.findViewById<EditText>(R.id.edtName)
        val edtPrice = dialogView.findViewById<EditText>(R.id.edtPrice)
        val edtImage = dialogView.findViewById<EditText>(R.id.edtImage)
        val edtType = dialogView.findViewById<EditText>(R.id.edtType)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        // --- ĐIỀN DỮ LIỆU CŨ ---
        tvTitle.text = "Cập Nhật Sản Phẩm"
        btnSave.text = "Cập nhật"

        edtName.setText(product.name)
        edtPrice.setText(String.format("%.0f", product.price)) // Chuyển số thành chuỗi, bỏ số thập phân thừa
        edtImage.setText(product.image ?: "")
        edtType.setText(product.type ?: "")

        // Xử lý nút Cập nhật
        btnSave.setOnClickListener {
            val name = edtName.text.toString().trim()
            val priceStr = edtPrice.text.toString().trim()
            val image = edtImage.text.toString().trim()
            val type = edtType.text.toString().trim()

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Tên và giá không được để trống", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val price = priceStr.toDoubleOrNull() ?: 0.0
            val updateBody = ProductBody(name, price, image, type)

            // Gọi API Update
            updateProductAPI(product._id, updateBody, dialog)
        }

        dialog.show()
    }

    private fun updateProductAPI(id: String, productBody: ProductBody, dialog: Dialog) {
        Toast.makeText(this, "Đang cập nhật...", Toast.LENGTH_SHORT).show()

        RetrofitClient.instance.updateProduct(id, productBody).enqueue(object :
            Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@AdminProductActivity, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    loadAllProducts()
                } else {
                    val msg = response.body()?.message ?: "Cập nhật thất bại"
                    Toast.makeText(this@AdminProductActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                Toast.makeText(this@AdminProductActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun confirmDeleteProduct(product: Product) {
        AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa '${product.name}'?")
            .setPositiveButton("Xóa") { _, _ ->
                deleteProductAPI(product)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteProductAPI(product: Product) {
        // [QUAN TRỌNG] Đã sửa DeleteResponse thành GeneralResponse
        RetrofitClient.instance.deleteProduct(product._id).enqueue(object :
            Callback<GeneralResponse> {
            override fun onResponse(call: Call<GeneralResponse>, response: Response<GeneralResponse>) {
                if (response.isSuccessful && response.body()?.status == true) {
                    Toast.makeText(this@AdminProductActivity, response.body()?.message ?: "Đã xóa", Toast.LENGTH_SHORT).show()
                    loadAllProducts() // Load lại danh sách sau khi xóa
                } else {
                    val msg = response.body()?.message ?: "Xóa thất bại"
                    Toast.makeText(this@AdminProductActivity, msg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<GeneralResponse>, t: Throwable) {
                Toast.makeText(this@AdminProductActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- TIỆN ÍCH ---
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}