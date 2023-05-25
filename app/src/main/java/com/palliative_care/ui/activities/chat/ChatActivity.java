package com.palliative_care.ui.activities.chat;

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
import com.palliative_care.databinding.ActivityChatBinding;
import com.palliative_care.helpers.Constants;
import com.palliative_care.helpers.Functions;
import com.palliative_care.model.User;
import com.palliative_care.ui.adapters.ContactAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class ChatActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    ActivityChatBinding binding;
    String userId = Functions.getUserId();
    ContactAdapter adapter;
    ArrayList<User> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {

        binding.appbar.title.setText(getString(R.string.chats));
        binding.appbar.back.setOnClickListener(v -> finish());
        binding.swipeRefresh.setOnRefreshListener(this);
        adapter = new ContactAdapter();
        adapter.setListener(model -> {
            Intent intent = new Intent(ChatActivity.this, MessagesActivity.class);
            intent.putExtra(Constants.TYPE_MODEL, model);
            startActivity(intent);
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        getChatsRequest();
    }

    private void getChatsRequest() {
        binding.swipeRefresh.setRefreshing(false);
        binding.stateful.showLoading();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_CONTACTS).child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            list.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                User model = data.getValue(User.class);
                                list.add(model);
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
                        binding.stateful.showError(error.getMessage(), v -> getChatsRequest());
                    }
                });
    }

    @Override
    public void onRefresh() {
        getChatsRequest();
    }

}