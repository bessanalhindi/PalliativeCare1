package com.palliative_care.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.palliative_care.base.BaseFragment;
import com.palliative_care.databinding.FragmentInterestsBinding;
import com.palliative_care.helpers.Constants;
import com.palliative_care.helpers.Functions;
import com.palliative_care.model.Category;
import com.palliative_care.ui.activities.post.PostsActivity;
import com.palliative_care.ui.adapters.InterestsAdapter;
import com.palliative_care.ui.interfaces.CategoryInterface;

import java.util.ArrayList;
import java.util.Collections;

public class InterestsFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    public InterestsFragment() {
        // Required empty public constructor
    }

    FragmentInterestsBinding binding;
    ArrayList<Category> list = new ArrayList<>();
    InterestsAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInterestsBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
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
        binding.swipeRefresh.setOnRefreshListener(this);
        adapter = new InterestsAdapter();
        adapter.setListener(new CategoryInterface() {
            @Override
            public void onItemClicked(Category model) {
                Intent intent = new Intent(requireActivity(), PostsActivity.class);
                intent.putExtra(Constants.TYPE_MODEL, model);
                startActivity(intent);
            }

            @Override
            public void onSubscribe(Category model) {
                FirebaseDatabase.getInstance().getReference(Constants.TABLE_USERS)
                        .child(Functions.getUserId()).child(Constants.COLUMN_INTERESTS).child(model.getId())
                        .removeValue();
                FirebaseDatabase.getInstance().getReference(Constants.TABLE_CATEGORIES)
                        .child(model.getId()).child(Constants.COLUMN_USERS).child(Functions.getUserId())
                        .removeValue();

                Functions.unsubscribeFromTopic(requireActivity(), binding.getRoot(), model.getTopic(), model.getTitle());
                adapter.removeItem(model);
            }
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        getInterestsRequest();
    }

    private void getInterestsRequest() {
        binding.swipeRefresh.setRefreshing(false);
        binding.stateful.showLoading();
        FirebaseDatabase.getInstance().getReference(Constants.TABLE_USERS)
                .child(Functions.getUserId()).child(Constants.COLUMN_INTERESTS)
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
                        binding.stateful.showError(error.getMessage(), v -> getInterestsRequest());
                    }
                });
    }

    @Override
    public void onRefresh() {
        getInterestsRequest();
    }
}