package com.palliative_care.ui.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.palliative_care.R;
import com.palliative_care.databinding.ItemPostBinding;
import com.palliative_care.helpers.Functions;
import com.palliative_care.model.Post;
import com.palliative_care.ui.interfaces.PostInterface;

import java.util.ArrayList;

@SuppressLint("NotifyDataSetChanged")
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    Activity context;
    PostInterface listener;
    ArrayList<Post> list;

    public void setData(ArrayList<Post> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setListener(PostInterface listener) {
        this.listener = listener;
    }

    public PostAdapter(Activity context) {
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post model = list.get(position);
        String createAt = Functions.formatTime(model.getCreateAt());
        holder.binding.userName.setText(model.getDoctorName());
        holder.binding.time.setText(createAt);
        holder.binding.categoryName.setText(model.getCategoryName());
        holder.binding.title.setText(model.getTitle());
        holder.binding.content.setText(model.getContent());
        if (model.getFile().isEmpty()) {
            holder.binding.attache.setVisibility(View.GONE);
        } else {
            holder.binding.attache.setVisibility(View.VISIBLE);
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClicked(model));
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ItemPostBinding binding;

        private PostViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemPostBinding.bind(itemView);
        }
    }

}