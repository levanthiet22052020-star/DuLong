package com.example.dulong.activity.cart

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dulong.R
import com.example.dulong.activity.user.AddressListActivity
import com.example.dulong.activity.user.OrderDetailActivity
import com.example.dulong.activity.utils.CartManager
import com.example.dulong.adapter.CartAdapter
import com.example.dulong.api.RetrofitClient
import com.example.dulong.model.AddressListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.Random

class CheckoutActivity : AppCompatActivity() {

    private lateinit var tvReceiverNamePhone: TextView
    private lateinit var tvReceiverAddress: TextView
    private lateinit var tvSubTotal: TextView
    private lateinit var tvFinalTotal: TextView
    private lateinit var tvBottomTotal: TextView
    private lateinit var rvCheckoutItems: RecyclerView
    private lateinit var btnPlaceOrder: Button
    private lateinit var btnSelectAddress: LinearLayout
    private lateinit var sharedPreferences: SharedPreferences

    // Biến lưu địa chỉ đã chọn
    private var selectedAddressString: String = ""
    private var selectedNamePhone: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        initViews()

        // Setup Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarCheckout)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationOnClickListener { finish() }

        // 1. Hiển thị danh sách sản phẩm
        val cartItems = CartManager.getCartItems()

        // --- SỬA LỖI Ở ĐÂY ---
        // Truyền thêm lambda { updateTotalPrice() } vào Adapter
        val adapter = CartAdapter(cartItems) {
            updateTotalPrice()
        }

        rvCheckoutItems.layoutManager = LinearLayoutManager(this)
        rvCheckoutItems.adapter = adapter

        // 2. Tính toán tiền lần đầu
        updateTotalPrice()

        // 3. Lấy địa chỉ mặc định
        loadDefaultAddress()

        // 4. Sự kiện chọn địa chỉ
        btnSelectAddress.setOnClickListener {
            val intent = Intent(this, AddressListActivity::class.java)
            intent.putExtra("isSelectMode", true)
            startActivityForResult(intent, 100)
        }

        // 5. SỰ KIỆN ĐẶT HÀNG
        btnPlaceOrder.setOnClickListener {
            if (selectedAddressString.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn địa chỉ nhận hàng", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // A. Tạo mã đơn hàng ngẫu nhiên
            val randomOrderId = "SPVN${Random().nextInt(90000000) + 10000000}"

            // Lấy tổng tiền hiện tại để lưu
            val total = CartManager.getTotalPrice()
            val formatter = DecimalFormat("#,###")
            val totalString = "${formatter.format(total)}đ"

            // B. LƯU THÔNG TIN VÀO BỘ NHỚ
            val orderPrefs = getSharedPreferences("ORDER_INFO", Context.MODE_PRIVATE)
            val editor = orderPrefs.edit()
            editor.putString("ORDER_ID", randomOrderId)
            editor.putString("ORDER_STATUS", "Đang giao hàng")
            editor.putString("ORDER_TOTAL", totalString)
            editor.putString("ORDER_ADDRESS", selectedAddressString)
            editor.apply()

            // C. Chuyển sang màn hình Chi tiết đơn hàng
            val intent = Intent(this, OrderDetailActivity::class.java)
            intent.putExtra("order_name_phone", selectedNamePhone)
            intent.putExtra("order_address", selectedAddressString)
            intent.putExtra("order_total", total)
            intent.putExtra("order_id", randomOrderId)

            startActivity(intent)
            finish()
        }
    }

    private fun initViews() {
        tvReceiverNamePhone = findViewById(R.id.tvReceiverNamePhone)
        tvReceiverAddress = findViewById(R.id.tvReceiverAddress)
        tvSubTotal = findViewById(R.id.tvSubTotal)
        tvFinalTotal = findViewById(R.id.tvFinalTotal)
        tvBottomTotal = findViewById(R.id.tvBottomTotal)
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        btnSelectAddress = findViewById(R.id.btnSelectAddress)
        sharedPreferences = getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE)
    }

    // Hàm cập nhật tổng tiền lên giao diện
    private fun updateTotalPrice() {
        val total = CartManager.getTotalPrice()
        val formatter = DecimalFormat("#,###")
        val totalString = "${formatter.format(total)}đ"

        tvSubTotal.text = totalString
        tvFinalTotal.text = totalString
        tvBottomTotal.text = totalString
    }

    private fun loadDefaultAddress() {
        val userId = sharedPreferences.getString("USER_ID", "") ?: return

        RetrofitClient.instance.getAddresses(userId).enqueue(object : Callback<AddressListResponse> {
            override fun onResponse(call: Call<AddressListResponse>, response: Response<AddressListResponse>) {
                if (response.isSuccessful && response.body()?.data != null) {
                    val list = response.body()!!.data
                    val defaultAddr = list.find { it.isDefault } ?: list.firstOrNull()

                    if (defaultAddr != null) {
                        selectedNamePhone = "${defaultAddr.name} (${defaultAddr.phone})"
                        selectedAddressString = defaultAddr.address
                        tvReceiverNamePhone.text = selectedNamePhone
                        tvReceiverAddress.text = selectedAddressString
                    }
                }
            }
            override fun onFailure(call: Call<AddressListResponse>, t: Throwable) { }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val name = data.getStringExtra("name")
            val phone = data.getStringExtra("phone")
            val addr = data.getStringExtra("address")

            selectedNamePhone = "$name ($phone)"
            selectedAddressString = addr ?: ""

            tvReceiverNamePhone.text = selectedNamePhone
            tvReceiverAddress.text = selectedAddressString
        }
    }
}