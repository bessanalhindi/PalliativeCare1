package com.palliative_care.ui.activities.doctor;

import android.os.Bundle;

import com.palliative_care.R;
import com.palliative_care.base.BaseActivity;
import com.palliative_care.databinding.ActivityProfileDoctorBinding;
import com.palliative_care.helpers.Functions;
import com.palliative_care.model.ProfileSection;
import com.palliative_care.model.User;
import com.palliative_care.ui.activities.auth.ProfileActivity;
import com.palliative_care.ui.activities.chat.ChatActivity;
import com.palliative_care.ui.adapters.ProfileAdapter;

import java.util.ArrayList;

public class ProfileDoctorActivity extends BaseActivity {

    ActivityProfileDoctorBinding binding;
    ProfileAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileDoctorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        binding.appbar.back.setOnClickListener(v -> finish());
        binding.appbar.title.setText(getString(R.string.my_profile));

        ArrayList<ProfileSection> list = new ArrayList<>();
        User user = Functions.getUser();
        String userName = user.getFirstName() + " " + user.getFatherName() + " " + user.getFamilyName();
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
        list.add(new ProfileSection(getString(R.string.logout), 0, position -> {
            Functions.dialog(this, getString(R.string.logout), getString(R.string.logout_message), v -> {
                Functions.logout(this);
            });
        }));
        adapter.setData(list);
    }
}