package com.palliative_care.ui.activities.doctor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.palliative_care.R;
import com.palliative_care.base.BaseActivity;
import com.palliative_care.databinding.ActivityPostDoctorBinding;
import com.palliative_care.helpers.Constants;
import com.palliative_care.model.Category;
import com.palliative_care.model.Post;
import com.palliative_care.ui.activities.chat.GroupMessagesActivity;
import com.palliative_care.ui.activities.post.PostDetailsActivity;
import com.palliative_care.ui.activities.post.PostsActivity;
import com.palliative_care.ui.adapters.doctor.PostDoctorAdapter;
import com.palliative_care.ui.interfaces.PostInterface;

import java.util.ArrayList;
import java.util.Collections;

@SuppressLint("SetTextI18n")
public class PostDoctorActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    ActivityPostDoctorBinding binding;
    ArrayList<Post> list = new ArrayList<>();
    PostDoctorAdapter adapter;
    Category model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        model = (Category) getIntent().getSerializableExtra(Constants.TYPE_MODEL);

        binding.appbar.title.setText(getString(R.string.posts_) + " " + model.getTitle());
        binding.swipeRefresh.setOnRefreshListener(this);
        adapter = new PostDoctorAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        getPostsRequest();
        onViewClick();
    }

    private void onViewClick() {
        binding.appbar.back.setOnClickListener(v -> finish());
        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPostActivity.class);
            intent.putExtra(Constants.TYPE_FROM, Constants.TYPE_ADD);
            intent.putExtra(Constants.TYPE_MODEL, model);
            startActivity(intent);
        });
        binding.chat.setOnClickListener(v -> {
            Intent intent = new Intent(this, GroupMessagesActivity.class);
            intent.putExtra(Constants.TYPE_MODEL, model);
            startActivity(intent);
        });
        adapter.setListener(new PostInterface() {
            @Override
            public void onItemClicked(Post model) {
                Intent intent = new Intent(PostDoctorActivity.this, PostDetailsActivity.class);
                intent.putExtra(Constants.TYPE_MODEL, model);
                startActivity(intent);
            }

            @Override
            public void onMessage(Post model) {
                Intent intent = new Intent(PostDoctorActivity.this, AddPostActivity.class);
                intent.putExtra(Constants.TYPE_FROM, Constants.TYPE_EDIT);
                intent.putExtra(Constants.TYPE_MODEL, model);
                startActivity(intent);
            }
        });
    }

    private void getPostsRequest() {
        binding.swipeRefresh.setRefreshing(false);
        binding.stateful.showLoading();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_POSTS)
                .orderByChild("categoryId").equalTo(model.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            list.clear();
                            for (DataSnapshot issue : snapshot.getChildren()) {
                                Post post = issue.getValue(Post.class);
                                list.add(post);
                            }
                            Collections.reverse(list);
                            adapter.setData(list);
                            binding.stateful.showContent();
                        } else {
                            binding.stateful.showEmpty();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        binding.stateful.showError(error.getMessage(), v -> getPostsRequest());
                    }
                });
    }

    @Override
    public void onRefresh() {
        getPostsRequest();
    }
}