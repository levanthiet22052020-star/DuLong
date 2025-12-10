package com.example.dulong.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.model.AddressModel;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private List<AddressModel> list;
    private OnItemClickListener listener; // Biến lưu listener

    // 1. ĐỊNH NGHĨA INTERFACE (Đây là phần bạn đang thiếu)
    public interface OnItemClickListener {
        void onItemClick(AddressModel model);
    }

    // 2. Constructor nhận vào List và Listener
    public AddressAdapter(List<AddressModel> list, OnItemClickListener listener) {
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

        holder.tvName.setText(model.getName());
        holder.tvPhone.setText(model.getPhone());
        holder.tvAddress.setText(model.getAddress());

        // Hiển thị nhãn "Mặc định" nếu có
        if (model.isDefault()) {
            holder.tvDefault.setVisibility(View.VISIBLE);
        } else {
            holder.tvDefault.setVisibility(View.GONE);
        }

        // 3. BẮT SỰ KIỆN CLICK VÀO CẢ DÒNG (ItemView)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(model); // Gọi hàm của interface
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvAddress, tvDefault;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvDefault = itemView.findViewById(R.id.tvDefault);
        }
    }
}