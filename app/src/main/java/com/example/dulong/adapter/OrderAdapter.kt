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

class OrderAdapter(private val list: List<Product>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgOrderProduct)
        val tvName: TextView = itemView.findViewById(R.id.tvOrderProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvOrderProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order_product, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val product = list[position]
        holder.tvName.text = product.name

        val formatter = DecimalFormat("#,###")
        holder.tvPrice.text = "${formatter.format(product.price)}đ"

        Glide.with(holder.itemView.context)
            .load(product.image)
            .placeholder(R.drawable.ic_logo_only)
            .into(holder.img)
    }

    override fun getItemCount(): Int = list.size
}