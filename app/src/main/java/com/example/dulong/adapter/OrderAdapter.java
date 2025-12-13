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

import java.text.DecimalFormat;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Product> list;

    public OrderAdapter(List<Product> list) {
        this.list = list;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName;
        TextView tvPrice;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgOrderProduct);
            tvName = itemView.findViewById(R.id.tvOrderProductName);
            tvPrice = itemView.findViewById(R.id.tvOrderProductPrice);
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_product, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Product product = list.get(position);
        holder.tvName.setText(product.getName());

        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvPrice.setText(formatter.format(product.getPrice()) + "đ");

        Glide.with(holder.itemView.getContext())
                .load(product.getImage())
                .placeholder(R.drawable.ic_logo_only)
                .into(holder.img);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}