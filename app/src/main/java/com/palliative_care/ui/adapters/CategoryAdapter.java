package com.palliative_care.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.palliative_care.R;
import com.palliative_care.databinding.ItemCategoryBinding;
import com.palliative_care.helpers.Functions;
import com.palliative_care.model.Category;
import com.palliative_care.ui.interfaces.CategoryInterface;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NotifyDataSetChanged")
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> implements Filterable {

    CategoryInterface listener;
    ArrayList<Category> list;
    List<Category> filterList;

    public void setData(ArrayList<Category> list) {
        this.list = list;
        filterList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public void setListener(CategoryInterface listener) {
        this.listener = listener;
    }

    public ArrayList<Category> getData() {
        return list;
    }

    public CategoryAdapter() {
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category model = list.get(position);
        holder.binding.title.setText(model.getTitle());
        holder.binding.description.setText(model.getDescription());
        holder.itemView.setOnClickListener(v -> listener.onItemClicked(model));
        holder.binding.subscribe.setOnClickListener(v -> listener.onSubscribe(model));

        if (model.getUsers() != null) {
            if (model.getUsers().containsValue(Functions.getUserId())) {
                holder.binding.subscribe.setVisibility(View.GONE);
            } else {
                holder.binding.subscribe.setVisibility(View.VISIBLE);
            }
        } else {
            holder.binding.subscribe.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;

        private CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCategoryBinding.bind(itemView);
        }
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private final Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Category> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(filterList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Category item : filterList) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}