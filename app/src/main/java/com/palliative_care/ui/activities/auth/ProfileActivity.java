package com.palliative_care.ui.activities.auth;

import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.hawk.Hawk;
import com.palliative_care.R;
import com.palliative_care.base.BaseActivity;
import com.palliative_care.databinding.ActivityProfileBinding;
import com.palliative_care.helpers.Constants;
import com.palliative_care.helpers.Functions;
import com.palliative_care.model.User;

public class ProfileActivity extends BaseActivity {

    ActivityProfileBinding binding;
    User user = Functions.getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {

        binding.birthDate.setOnClickListener(v -> datePickerDialog(binding.birthDate));
        binding.appbar.back.setOnClickListener(v -> finish());
        binding.appbar.title.setText(getString(R.string.my_profile));

        binding.firstName.setText(user.getFirstName());
        binding.fatherName.setText(user.getFatherName());
        binding.familyName.setText(user.getFamilyName());
        binding.birthDate.setText(user.getDateOfBirth());
        binding.address.setText(user.getAddress());
        binding.phone.setText(user.getPhone());
        binding.email.setText(user.getEmail());
        binding.accountType.setText(user.getType());

        binding.save.setOnClickListener(v -> {
            if (Functions.isInternetAvailable(this)) {
                Functions.getDeviceToken(this::updateProfileRequest);
            } else {
                Functions.snackBar(this, binding.getRoot());
            }
        });
    }

    private void updateProfileRequest(String deviceToken) {
        if (isNotEmpty(binding.tvFirstName, binding.firstName)
                && isNotEmpty(binding.tvFatherName, binding.fatherName)
                && isNotEmpty(binding.tvFamilyName, binding.familyName)
                && isNotEmpty(binding.tvBirthDate, binding.birthDate)
                && isNotEmpty(binding.tvAddress, binding.address)
                && isNotEmpty(binding.tvPhone, binding.phone)
                && isNotEmpty(binding.tvEmail, binding.email)
                && isValidEmail(binding.tvEmail, binding.email)
        ) {
            showCustomProgress();
            user.setFirstName(getText(binding.firstName));
            user.setFatherName(getText(binding.fatherName));
            user.setFamilyName(getText(binding.familyName));
            user.setAddress(getText(binding.address));
            user.setDateOfBirth(getText(binding.birthDate));
            user.setPhone(getText(binding.phone));
            user.setDeviceToken(deviceToken);
            FirebaseDatabase.getInstance()
                    .getReference(Constants.TABLE_USERS).child(user.getId())
                    .setValue(user).addOnCompleteListener(task -> {
                        dismissCustomProgress();
                        if (task.isSuccessful()) {
                            Hawk.put(Constants.TYPE_USER, user);
                            Hawk.put(Constants.TYPE_DEVICE_TOKEN, user.getDeviceToken());
                            Hawk.put(Constants.TYPE_IS_LOGIN, true);
                            finish();
                        } else {
                            Functions.snackBar(this, binding.getRoot(), getString(R.string.error));
                        }
                    });
        }
    }
}