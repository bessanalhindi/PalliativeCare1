package com.palliative_care.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.palliative_care.R;
import com.palliative_care.databinding.ItemSectionProfileBinding;
import com.palliative_care.model.ProfileSection;

import java.util.ArrayList;

@SuppressLint("NotifyDataSetChanged")
public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder> {

    private ArrayList<ProfileSection> list;

    public void setData(ArrayList<ProfileSection> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public ProfileAdapter() {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section_profile, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProfileSection profileSection = list.get(position);
        holder.binding.tvTitle.setText(profileSection.getTitle());
        holder.binding.icon.setImageResource(profileSection.getIcon());
        holder.itemView.setOnClickListener(v -> {
            profileSection.getItemListener().onClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 20;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ItemSectionProfileBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSectionProfileBinding.bind(itemView);
        }
    }
}
