package com.palliative_care.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.palliative_care.R;
import com.palliative_care.databinding.ItemNotificationBinding;
import com.palliative_care.helpers.Functions;
import com.palliative_care.model.Notification;

import java.util.ArrayList;

@SuppressLint("NotifyDataSetChanged")
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    ArrayList<Notification> list;

    public void setData(ArrayList<Notification> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public NotificationAdapter() {
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification model = list.get(position);
        String createAt = Functions.formatTime(model.getCreateAt());
        holder.binding.title.setText(model.getTitle());
        holder.binding.content.setText(model.getContent());
        holder.binding.time.setText(createAt);
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;

        private NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNotificationBinding.bind(itemView);
        }
    }

}