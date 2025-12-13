package com.example.dulong.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.model.AddressModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private List<AddressModel> list;
    private OnAddressActionListener listener;

    // Interface cho các hành động với địa chỉ
    public interface OnAddressActionListener {
        void onEditAddress(AddressModel model);
        void onDeleteAddress(AddressModel model);
        void onSetDefaultAddress(AddressModel model);
    }

    // Constructor
    public AddressAdapter(List<AddressModel> list, OnAddressActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ánh xạ layout item_address (sẽ tạo ở bước 2)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressModel model = list.get(position);

        if (model == null) return;

        // Hiển thị thông tin địa chỉ
        String displayName = model.getFullName() != null ? model.getFullName() : model.getName();
        holder.tvName.setText(displayName != null ? displayName : "Địa chỉ");
        holder.tvCustomerName.setText(displayName != null ? displayName : "");
        holder.tvPhone.setText("• " + model.getPhone());
        holder.tvAddress.setText(model.getFullAddress());

        // Hiển thị nhãn "Mặc định" nếu có
        if (model.isDefault()) {
            holder.tvDefault.setVisibility(View.VISIBLE);
            holder.btnSetDefault.setVisibility(View.GONE);
        } else {
            holder.tvDefault.setVisibility(View.GONE);
            holder.btnSetDefault.setVisibility(View.VISIBLE);
        }

        // Sự kiện nút Sửa
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditAddress(model);
            }
        });

        // Sự kiện nút Xóa
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteAddress(model);
            }
        });

        // Sự kiện nút Đặt Mặc Định
        holder.btnSetDefault.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSetDefaultAddress(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCustomerName, tvPhone, tvAddress, tvDefault;
        MaterialButton btnEdit, btnDelete, btnSetDefault;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvAddressName);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvPhone = itemView.findViewById(R.id.tvCustomerPhone);
            tvAddress = itemView.findViewById(R.id.tvFullAddress);
            tvDefault = itemView.findViewById(R.id.tvDefaultBadge);
            btnEdit = itemView.findViewById(R.id.btnEditAddress);
            btnDelete = itemView.findViewById(R.id.btnDeleteAddress);
            btnSetDefault = itemView.findViewById(R.id.btnSetDefault);
        }
    }
}