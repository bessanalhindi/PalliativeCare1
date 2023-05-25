package com.palliative_care.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.palliative_care.R;
import com.palliative_care.base.BaseFragment;
import com.palliative_care.databinding.FragmentSettingBinding;
import com.palliative_care.helpers.Functions;
import com.palliative_care.model.Category;
import com.palliative_care.model.ProfileSection;
import com.palliative_care.model.User;
import com.palliative_care.ui.activities.auth.ProfileActivity;
import com.palliative_care.ui.activities.chat.ChatActivity;
import com.palliative_care.ui.activities.other.NotificationActivity;
import com.palliative_care.ui.adapters.ProfileAdapter;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class SettingFragment extends BaseFragment {

    public SettingFragment() {
        // Required empty public constructor
    }

    FragmentSettingBinding binding;
    ProfileAdapter adapter;
    ArrayList<ProfileSection> list = new ArrayList<>();
    User user = Functions.getUser();
    String userName = user.getFirstName() + " " + user.getFatherName() + " " + user.getFamilyName();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {

        binding.userName.setText(userName);
        adapter = new ProfileAdapter();
        binding.recyclerview.setAdapter(adapter);
        binding.recyclerview.setHasFixedSize(true);
        list.add(new ProfileSection(getString(R.string.my_profile), 0, position -> {
            startActivity(ProfileActivity.class);
        }));
        list.add(new ProfileSection(getString(R.string.chats), 0, position -> {
            startActivity(ChatActivity.class);
        }));
        list.add(new ProfileSection(getString(R.string.notifications), 0, position -> {
            startActivity(NotificationActivity.class);
        }));
        list.add(new ProfileSection(getString(R.string.logout), 0, position -> {
            Functions.dialog(requireActivity(), getString(R.string.logout), getString(R.string.logout_message), v -> {
                unSubscribeToTopic();
                Functions.logout(requireActivity());
            });
        }));
        adapter.setData(list);
    }

    private void unSubscribeToTopic() {
        if (user.getInterests() != null) {
            user.getInterests().forEach(new BiConsumer<String, Object>() {
                @Override
                public BiConsumer<String, Object> andThen(BiConsumer<? super String, ? super Object> after) {
                    return BiConsumer.super.andThen(after);
                }

                @Override
                public void accept(String s, Object object) {
                    Gson gson = new Gson();
                    String json = gson.toJson(object);
                    Category categoryObject = gson.fromJson(json, Category.class);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(categoryObject.getTopic());
                }
            });

        }
    }

}