package com.palliative_care.ui.activities.other;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.palliative_care.R;
import com.palliative_care.base.BaseActivity;
import com.palliative_care.databinding.ActivityNotificationBinding;
import com.palliative_care.helpers.Constants;
import com.palliative_care.helpers.Functions;
import com.palliative_care.model.Notification;
import com.palliative_care.ui.adapters.NotificationAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    ActivityNotificationBinding binding;
    ArrayList<Notification> list = new ArrayList<>();
    NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {

        binding.appbar.title.setText(getString(R.string.notifications));
        binding.appbar.back.setOnClickListener(v -> finish());

        binding.swipeRefresh.setOnRefreshListener(this);
        adapter = new NotificationAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        geNotificationRequest();
    }

    private void geNotificationRequest() {
        binding.swipeRefresh.setRefreshing(false);
        binding.stateful.showLoading();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_NOTIFICATIONS)
                .orderByChild("userId").equalTo(Functions.getUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            list.clear();
                            for (DataSnapshot issue : snapshot.getChildren()) {
                                Notification model = issue.getValue(Notification.class);
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
                        binding.stateful.showError(error.getMessage(), v -> geNotificationRequest());
                    }
                });
    }

    @Override
    public void onRefresh() {
        geNotificationRequest();
    }
}