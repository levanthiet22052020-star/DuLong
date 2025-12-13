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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> list;
    private OnQuantityChangedListener onQuantityChanged; // Callback để báo Activity cập nhật tiền

    public interface OnQuantityChangedListener {
        void onQuantityChanged();
    }

    public CartAdapter(List<Product> list, OnQuantityChangedListener onQuantityChanged) {
        this.list = list;
        this.onQuantityChanged = onQuantityChanged;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName;
        TextView tvPrice;

        // Ánh xạ các nút tăng giảm mới thêm vào layout
        ImageView btnMinus;
        ImageView btnPlus;
        TextView tvQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProductCart);
            tvName = itemView.findViewById(R.id.tvNameCart);
            tvPrice = itemView.findViewById(R.id.tvPriceCart);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = list.get(position);

        holder.tvName.setText(product.getName());

        DecimalFormat formatter = new DecimalFormat("#,###");
        holder.tvPrice.setText(formatter.format(product.getPrice()) + "đ");

        // Hiển thị số lượng
        holder.tvQuantity.setText(String.valueOf(product.getQuantity()));

        Glide.with(holder.itemView.getContext())
                .load(product.getImage())
                .placeholder(R.drawable.ic_logo_only)
                .error(R.drawable.ic_logo_only)
                .into(holder.img);

        // --- XỬ LÝ SỰ KIỆN ---

        // Nút CỘNG (+)
        holder.btnPlus.setOnClickListener(v -> {
            product.setQuantity(product.getQuantity() + 1); // Tăng số lượng
            holder.tvQuantity.setText(String.valueOf(product.getQuantity())); // Cập nhật text ngay lập tức
            if (onQuantityChanged != null) {
                onQuantityChanged.onQuantityChanged(); // Báo ra ngoài để tính lại tổng tiền
            }
        });

        // Nút TRỪ (-)
        holder.btnMinus.setOnClickListener(v -> {
            if (product.getQuantity() > 1) {
                product.setQuantity(product.getQuantity() - 1); // Giảm số lượng
                holder.tvQuantity.setText(String.valueOf(product.getQuantity()));
                if (onQuantityChanged != null) {
                    onQuantityChanged.onQuantityChanged(); // Báo ra ngoài để tính lại tổng tiền
                }
            } else {
                // Tùy chọn: Nếu giảm về 0 thì có thể hỏi xóa sản phẩm
                // Hiện tại ta chỉ cho giảm đến 1 thôi
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}