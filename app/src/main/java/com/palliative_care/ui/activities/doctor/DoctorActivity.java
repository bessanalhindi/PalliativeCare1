package com.palliative_care.ui.activities.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.palliative_care.R;
import com.palliative_care.base.BaseActivity;
import com.palliative_care.databinding.ActivityDoctorBinding;
import com.palliative_care.helpers.Constants;
import com.palliative_care.model.Category;
import com.palliative_care.ui.adapters.doctor.CategoryDoctorAdapter;
import com.palliative_care.ui.interfaces.CategoryInterface;

import java.util.ArrayList;
import java.util.Collections;

public class DoctorActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toast backToasty;
    private long backPressedTime;
    ActivityDoctorBinding binding;
    ArrayList<Category> list = new ArrayList<>();
    CategoryDoctorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        binding.appbar.title.setText(getText(R.string.categories));
        binding.swipeRefresh.setOnRefreshListener(this);
        adapter = new CategoryDoctorAdapter();
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        onViewClick();
        getCategoriesRequest();
    }

    private void onViewClick() {
        binding.appbar.menu.setOnClickListener(v -> {
            startActivity(ProfileDoctorActivity.class);
        });
        binding.fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddCategoryActivity.class);
            intent.putExtra(Constants.TYPE_FROM, Constants.TYPE_ADD);
            startActivity(intent);
        });
        binding.search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable text) {
                adapter.getFilter().filter(text.toString());
                if (adapter.getData().isEmpty()) {
                    binding.stateful.showEmpty();
                } else {
                    binding.stateful.showContent();
                }
            }
        });
        adapter.setListener(new CategoryInterface() {
            @Override
            public void onItemClicked(Category model) {
                Intent intent = new Intent(DoctorActivity.this, PostDoctorActivity.class);
                intent.putExtra(Constants.TYPE_MODEL, model);
                startActivity(intent);
            }

            @Override
            public void onSubscribe(Category model) {
                Intent intent = new Intent(DoctorActivity.this, AddCategoryActivity.class);
                intent.putExtra(Constants.TYPE_FROM, Constants.TYPE_EDIT);
                intent.putExtra(Constants.TYPE_MODEL, model);
                startActivity(intent);
            }
        });
    }

    private void getCategoriesRequest() {
        binding.swipeRefresh.setRefreshing(false);
        binding.stateful.showLoading();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_CATEGORIES)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()) {
                            list.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {
                                Category category = data.getValue(Category.class);
                                list.add(category);
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
                        binding.stateful.showError(error.getMessage(), v -> getCategoriesRequest());
                    }
                });
    }

    @Override
    public void onRefresh() {
        getCategoriesRequest();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToasty.cancel();
            super.onBackPressed();
            return;
        } else {
            backToasty = Toast.makeText(this, getString(R.string.back_exit), Toast.LENGTH_SHORT);
            backToasty.show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}