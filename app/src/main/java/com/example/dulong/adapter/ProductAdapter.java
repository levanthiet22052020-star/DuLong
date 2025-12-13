package com.example.dulong.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dulong.R;
import com.example.dulong.activity.product.ProductDetailActivity;
import com.example.dulong.model.Product;

import java.text.DecimalFormat;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> list;
    private Context context;

    public ProductAdapter(List<Product> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName;
        TextView tvPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tvName.setText(product.getName());

        // Format tiền tệ hiển thị danh sách
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedPrice = formatter.format(product.getPrice()) + "đ";
        holder.tvPrice.setText(formattedPrice);

        // Load ảnh
        Glide.with(context)
                .load(product.getImage())
                .placeholder(R.drawable.ic_logo_only) // Đảm bảo bạn có ảnh này trong drawable
                .error(R.drawable.ic_logo_only)
                .into(holder.imgProduct);

        // --- SỬA ĐỔI CHÍNH Ở ĐÂY ---
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);

            // Gửi TOÀN BỘ object Product sang màn hình chi tiết
            intent.putExtra("PRODUCT_DATA", product);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}