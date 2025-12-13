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

public class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.OrderViewHolder> {

    private List<OrderHistoryModel> orderList;
    private OnOrderActionListener onStatusUpdate;

    public interface OnOrderActionListener {
        void onOrderAction(OrderHistoryModel order);
    }

    public AdminOrderAdapter(List<OrderHistoryModel> orderList, OnOrderActionListener onStatusUpdate) {
        this.orderList = orderList;
        this.onStatusUpdate = onStatusUpdate;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId;
        TextView tvCustomerInfo;
        TextView tvStatus;
        TextView tvProductName;
        TextView tvDate;
        TextView tvTotalPrice;
        Button btnUpdateStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderNumber);
            tvCustomerInfo = itemView.findViewById(R.id.tvCustomerName);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvProductName = itemView.findViewById(R.id.tvItemCount);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            btnUpdateStatus = itemView.findViewById(R.id.btnUpdateStatus);
        }

        public void bind(OrderHistoryModel order) {
            tvOrderId.setText("Mã: " + order.getId());
            tvCustomerInfo.setText("Khách hàng: " + order.getProductName()); // Tạm thời dùng productName
            tvStatus.setText(order.getStatus());
            tvProductName.setText(order.getProductName());
            tvDate.setText("Ngày: " + order.getDate());
            tvTotalPrice.setText(order.getTotalPrice());

            // Đổi màu trạng thái
            switch (order.getStatus()) {
                case "Hoàn thành":
                    tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                    break;
                case "Đã hủy":
                    tvStatus.setTextColor(Color.parseColor("#F44336"));
                    break;
                case "Đang giao hàng":
                    tvStatus.setTextColor(Color.parseColor("#FF9800"));
                    break;
                default:
                    tvStatus.setTextColor(Color.parseColor("#009688"));
                    break;
            }

            btnUpdateStatus.setOnClickListener(v -> {
                if (onStatusUpdate != null) {
                    onStatusUpdate.onOrderAction(order);
                }
            });
        }
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orderList.get(position));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}