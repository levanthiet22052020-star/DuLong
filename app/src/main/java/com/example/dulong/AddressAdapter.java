package com.example.dulong;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<AddressModel> list;
    private OnAddressClickListener listener;

    public interface OnAddressClickListener {
        void onClick(AddressModel model);
    }

    public AddressAdapter(List<AddressModel> list, OnAddressClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressModel model = list.get(position);

        // Gán dữ liệu đúng UI mới
        holder.tvName.setText(model.getName());
        holder.tvPhone.setText(model.getPhone());
        holder.tvAddress.setText(model.getAddress());

        holder.itemView.setOnClickListener(v -> listener.onClick(model));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvPhone, tvAddress;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvAddress = itemView.findViewById(R.id.tvAddress);
        }
    }
}
