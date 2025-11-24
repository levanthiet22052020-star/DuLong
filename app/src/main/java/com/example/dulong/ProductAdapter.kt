package com.example.dulong

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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

        // Format tiền tệ
        val formatter = DecimalFormat("#,###")
        val formattedPrice = "${formatter.format(product.price)}đ"
        holder.tvPrice.text = formattedPrice

        // Load ảnh bằng Glide
        Glide.with(context)
            .load(product.image)
            .placeholder(R.drawable.ic_logo_only)
            .error(R.drawable.ic_logo_only)
            .into(holder.imgProduct)

        // --- BẮT SỰ KIỆN CLICK VÀO SẢN PHẨM ---
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java)
            // Gửi dữ liệu sản phẩm qua activity chi tiết
            intent.putExtra("PRODUCT_NAME", product.name)
            intent.putExtra("PRODUCT_PRICE", formattedPrice)
            intent.putExtra("PRODUCT_IMAGE_URL", product.image)
            intent.putExtra("PRODUCT_DESCRIPTION", product.description) // Giả sử model Product có trường description
            context.startActivity(intent)
        }
        // -----------------------------------------
    }

    override fun getItemCount(): Int = list.size
}