package com.example.dulong.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> list;
    private OnCategoryClickListener onClick;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(List<Category> list, OnCategoryClickListener onClick) {
        this.list = list;
        this.onClick = onClick;
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCategoryName);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tái sử dụng file item_category.xml bạn đã có
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category item = list.get(position);
        holder.tvName.setText(item.getName());
        holder.itemView.setOnClickListener(v -> {
            if (onClick != null) {
                onClick.onCategoryClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}