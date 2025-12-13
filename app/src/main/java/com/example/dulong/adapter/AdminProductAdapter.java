package com.example.dulong.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dulong.R;
import com.example.dulong.model.Product;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder> {

    private List<Product> productList;
    private OnProductActionListener onEdit;   // Callback khi bấm Sửa
    private OnProductActionListener onDelete; // Callback khi bấm Xóa

    public interface OnProductActionListener {
        void onProductAction(Product product);
    }

    public AdminProductAdapter(List<Product> productList, OnProductActionListener onEdit, OnProductActionListener onDelete) {
        this.productList = productList;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
    }

    public class AdminProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName;
        TextView tvPrice;
        ImageView btnEdit;
        ImageView btnDelete;

        public AdminProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            btnEdit = itemView.findViewById(R.id.adminBtnEdit);
            btnDelete = itemView.findViewById(R.id.adminBtnDelete);
        }

        public void bind(Product product) {
            tvName.setText(product.getName());
            // Format giá tiền (ví dụ thêm đ)
            tvPrice.setText(String.format("%,.0f đ", product.getPrice()));

            // Load ảnh dùng Glide (nếu có thư viện) hoặc set ảnh mặc định
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImage())
                        .placeholder(R.drawable.ic_house) // Ảnh chờ
                        .error(R.drawable.ic_house)       // Ảnh lỗi
                        .into(imgProduct);
            }

            // Sự kiện bấm nút Sửa
            btnEdit.setOnClickListener(v -> {
                if (onEdit != null) {
                    onEdit.onProductAction(product);
                }
            });

            // Sự kiện bấm nút Xóa
            btnDelete.setOnClickListener(v -> {
                if (onDelete != null) {
                    onDelete.onProductAction(product);
                }
            });
        }
    }

    @NonNull
    @Override
    public AdminProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_product, parent, false);
        return new AdminProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminProductViewHolder holder, int position) {
        holder.bind(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}