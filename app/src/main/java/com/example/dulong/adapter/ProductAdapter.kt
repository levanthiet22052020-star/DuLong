package com.example.dulong.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dulong.activity.product.ProductDetailActivity
import com.example.dulong.R
import com.example.dulong.model.Product
import java.text.DecimalFormat

class ProductAdapter(private val list: List<Product>, private val context: Context) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = list[position]
        holder.tvName.text = product.name

        // Format tiền tệ hiển thị danh sách
        val formatter = DecimalFormat("#,###")
        val formattedPrice = "${formatter.format(product.price)}đ"
        holder.tvPrice.text = formattedPrice

        // Load ảnh
        Glide.with(context)
            .load(product.image)
            .placeholder(R.drawable.ic_logo_only) // Đảm bảo bạn có ảnh này trong drawable
            .error(R.drawable.ic_logo_only)
            .into(holder.imgProduct)

        // --- SỬA ĐỔI CHÍNH Ở ĐÂY ---
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)

            // Gửi TOÀN BỘ object Product sang màn hình chi tiết
            intent.putExtra("PRODUCT_DATA", product)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = list.size
}