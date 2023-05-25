package com.palliative_care.ui.activities.post;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.palliative_care.R;
import com.palliative_care.base.BaseActivity;
import com.palliative_care.databinding.ActivityPostsBinding;
import com.palliative_care.helpers.Constants;
import com.palliative_care.helpers.Functions;
import com.palliative_care.model.Category;
import com.palliative_care.model.Post;
import com.palliative_care.ui.activities.chat.GroupMessagesActivity;
import com.palliative_care.ui.adapters.PostAdapter;
import com.palliative_care.ui.interfaces.PostInterface;

import java.util.ArrayList;

@SuppressLint("SetTextI18n")
public class PostsActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    ActivityPostsBinding binding;
    ArrayList<Post> list = new ArrayList<>();
    PostAdapter adapter;
    Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        category = (Category) getIntent().getSerializableExtra(Constants.TYPE_MODEL);

        binding.appbar.title.setText(getString(R.string.posts_) + " " + category.getTitle());
        binding.swipeRefresh.setOnRefreshListener(this);
        adapter = new PostAdapter(this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        getPostsRequest();
        onViewClicked();

        if (category.getUsers() != null) {
            if (category.getUsers().containsValue(Functions.getUserId())) {
                binding.chat.setVisibility(View.VISIBLE);
            } else {
                binding.chat.setVisibility(View.GONE);
            }
        } else {
            binding.chat.setVisibility(View.GONE);
        }
    }

    private void onViewClicked() {
        binding.appbar.back.setOnClickListener(v -> finish());
        binding.chat.setOnClickListener(v -> {
            Intent intent = new Intent(PostsActivity.this, GroupMessagesActivity.class);
            intent.putExtra(Constants.TYPE_MODEL, category);
            startActivity(intent);
        });
        adapter.setListener(new PostInterface() {
            @Override
            public void onItemClicked(Post model) {
                Intent intent = new Intent(PostsActivity.this, PostDetailsActivity.class);
                intent.putExtra(Constants.TYPE_MODEL, model);
                startActivity(intent);
            }

            @Override
            public void onMessage(Post model) {

            }
        });
    }

    private void getPostsRequest() {
        binding.swipeRefresh.setRefreshing(false);
        binding.stateful.showLoading();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_POSTS)
                .orderByChild("categoryId").equalTo(category.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            list.clear();
                            for (DataSnapshot issue : snapshot.getChildren()) {
                                Post post = issue.getValue(Post.class);
                                list.add(post);
                            }
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