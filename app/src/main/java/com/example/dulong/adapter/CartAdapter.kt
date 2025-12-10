package com.example.dulong.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dulong.R
import com.example.dulong.model.Product
import java.text.DecimalFormat

class CartAdapter(private val list: List<Product>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgProductCart)
        val tvName: TextView = itemView.findViewById(R.id.tvNameCart)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPriceCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = list[position]

        // Gán tên
        holder.tvName.text = product.name

        // Format giá tiền (ví dụ: 100000 -> 100.000đ)
        val formatter = DecimalFormat("#,###")
        holder.tvPrice.text = "${formatter.format(product.price)}đ"

        // Load ảnh bằng Glide
        Glide.with(holder.itemView.context)
            .load(product.image)
            .placeholder(R.drawable.ic_logo_only) // Đảm bảo bạn có icon này trong drawable hoặc đổi tên khác
            .error(R.drawable.ic_logo_only)
            .into(holder.img)
    }

    override fun getItemCount(): Int = list.size
}