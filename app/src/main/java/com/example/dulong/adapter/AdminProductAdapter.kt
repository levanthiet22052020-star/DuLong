package com.example.dulong.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // Đảm bảo bạn đã thêm Glide vào build.gradle
import com.example.dulong.R
import com.example.dulong.model.Product

class AdminProductAdapter(
    private val productList: List<Product>,
    private val onEdit: (Product) -> Unit,   // Callback khi bấm Sửa
    private val onDelete: (Product) -> Unit  // Callback khi bấm Xóa
) : RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder>() {

    inner class AdminProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        val tvName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val btnEdit: ImageView = itemView.findViewById(R.id.adminBtnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.adminBtnDelete)

        fun bind(product: Product) {
            tvName.text = product.name
            // Format giá tiền (ví dụ thêm đ)
            tvPrice.text = String.format("%,.0f đ", product.price)

            // Load ảnh dùng Glide (nếu có thư viện) hoặc set ảnh mặc định
            if (!product.image.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(product.image)
                    .placeholder(R.drawable.ic_house) // Ảnh chờ
                    .error(R.drawable.ic_house)       // Ảnh lỗi
                    .into(imgProduct)
            }

            // Sự kiện bấm nút Sửa
            btnEdit.setOnClickListener {
                onEdit(product)
            }

            // Sự kiện bấm nút Xóa
            btnDelete.setOnClickListener {
                onDelete(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_product, parent, false)
        return AdminProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size
}