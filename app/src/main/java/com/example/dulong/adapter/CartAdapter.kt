package com.example.dulong.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dulong.R
import com.example.dulong.activity.utils.CartManager
import com.example.dulong.model.Product
import java.text.DecimalFormat

class CartAdapter(
    private val list: List<Product>,
    private val onQuantityChanged: () -> Unit // Callback để báo Activity cập nhật tiền
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgProductCart)
        val tvName: TextView = itemView.findViewById(R.id.tvNameCart)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPriceCart)

        // Ánh xạ các nút tăng giảm mới thêm vào layout
        val btnMinus: ImageView = itemView.findViewById(R.id.btnMinus)
        val btnPlus: ImageView = itemView.findViewById(R.id.btnPlus)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = list[position]

        holder.tvName.text = product.name

        val formatter = DecimalFormat("#,###")
        holder.tvPrice.text = "${formatter.format(product.price)}đ"

        // Hiển thị số lượng
        holder.tvQuantity.text = product.quantity.toString()

        Glide.with(holder.itemView.context)
            .load(product.image)
            .placeholder(R.drawable.ic_logo_only)
            .error(R.drawable.ic_logo_only)
            .into(holder.img)

        // --- XỬ LÝ SỰ KIỆN ---

        // Nút CỘNG (+)
        holder.btnPlus.setOnClickListener {
            product.quantity++ // Tăng số lượng
            holder.tvQuantity.text = product.quantity.toString() // Cập nhật text ngay lập tức
            onQuantityChanged() // Báo ra ngoài để tính lại tổng tiền
        }

        // Nút TRỪ (-)
        holder.btnMinus.setOnClickListener {
            if (product.quantity > 1) {
                product.quantity-- // Giảm số lượng
                holder.tvQuantity.text = product.quantity.toString()
                onQuantityChanged() // Báo ra ngoài để tính lại tổng tiền
            } else {
                // Tùy chọn: Nếu giảm về 0 thì có thể hỏi xóa sản phẩm
                // Hiện tại ta chỉ cho giảm đến 1 thôi
            }
        }
    }

    override fun getItemCount(): Int = list.size
}