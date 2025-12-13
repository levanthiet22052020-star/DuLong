package com.example.dulong.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.model.OrderHistoryModel;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private List<OrderHistoryModel> list;
    private OnOrderClickListener onClick;

    public interface OnOrderClickListener {
        void onOrderClick(OrderHistoryModel order);
    }

    public OrderHistoryAdapter(List<OrderHistoryModel> list, OnOrderClickListener onClick) {
        this.list = list;
        this.onClick = onClick;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId;
        TextView tvStatus;
        TextView tvProductName;
        TextView tvDate;
        TextView tvTotalPrice;
        Button btnDetail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderNumber);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvProductName = itemView.findViewById(R.id.tvItemCount);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnDetail = itemView.findViewById(R.id.btnViewDetails);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderHistoryModel item = list.get(position);

        // Null safety checks
        if (holder.tvOrderId != null) {
            holder.tvOrderId.setText(item.getDisplayOrderId());
        }
        
        if (holder.tvStatus != null) {
            String statusDisplay = item.getStatusDisplay();
            holder.tvStatus.setText(statusDisplay);
            
            // Đổi màu trạng thái
            if ("Hoàn thành".equals(statusDisplay) || "delivered".equals(item.getStatus())) {
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            } else if ("Đã hủy".equals(statusDisplay) || "cancelled".equals(item.getStatus())) {
                holder.tvStatus.setTextColor(Color.parseColor("#F44336"));
            } else if ("Đang giao".equals(statusDisplay) || "shipping".equals(item.getStatus())) {
                holder.tvStatus.setTextColor(Color.parseColor("#FF9800"));
            } else {
                holder.tvStatus.setTextColor(Color.parseColor("#009688"));
            }
        }
        
        if (holder.tvProductName != null) {
            String productName = item.getProductName();
            holder.tvProductName.setText(productName != null ? productName : "Không có sản phẩm");
        }
        
        if (holder.tvDate != null) {
            String date = item.getDate();
            holder.tvDate.setText(date != null ? date : "N/A");
        }
        
        if (holder.tvTotalPrice != null) {
            String totalPrice = item.getTotalPrice();
            holder.tvTotalPrice.setText(totalPrice != null ? totalPrice : "0đ");
        }

        if (holder.btnDetail != null) {
            holder.btnDetail.setOnClickListener(v -> {
                if (onClick != null) {
                    onClick.onOrderClick(item);
                }
            });
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (onClick != null) {
                onClick.onOrderClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}