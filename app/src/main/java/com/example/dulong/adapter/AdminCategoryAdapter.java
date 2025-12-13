package com.example.dulong.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dulong.R;
import com.example.dulong.model.Category;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private OnCategoryActionListener onEdit;
    private OnCategoryActionListener onDelete;

    public interface OnCategoryActionListener {
        void onCategoryAction(Category category);
    }

    public AdminCategoryAdapter(List<Category> categoryList, OnCategoryActionListener onEdit, OnCategoryActionListener onDelete) {
        this.categoryList = categoryList;
        this.onEdit = onEdit;
        this.onDelete = onDelete;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;
        ImageView btnEdit;
        ImageView btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            btnEdit = itemView.findViewById(R.id.btnEditCategory);
            btnDelete = itemView.findViewById(R.id.btnDeleteCategory);
        }

        public void bind(Category category) {
            tvCategoryName.setText(category.getName());

            btnEdit.setOnClickListener(v -> {
                if (onEdit != null) {
                    onEdit.onCategoryAction(category);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (onDelete != null) {
                    onDelete.onCategoryAction(category);
                }
            });
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}