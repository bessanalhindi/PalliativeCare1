package com.palliative_care.ui.activities.auth;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.hawk.Hawk;
import com.palliative_care.R;
import com.palliative_care.base.BaseActivity;
import com.palliative_care.databinding.ActivityRegisterBinding;
import com.palliative_care.helpers.Constants;
import com.palliative_care.helpers.Functions;
import com.palliative_care.model.User;
import com.palliative_care.ui.activities.doctor.DoctorActivity;
import com.palliative_care.ui.activities.other.MainActivity;

import java.util.Objects;

public class RegisterActivity extends BaseActivity {

    ActivityRegisterBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        type = getIntent().getStringExtra(Constants.TYPE);
        binding.accountType.setText(type);
        binding.appbar.title.setText(getString(R.string.signup));
        binding.birthDate.setOnClickListener(v -> datePickerDialog(binding.birthDate));
        binding.register.setOnClickListener(v -> {
            if (Functions.isInternetAvailable(this)) {
                Functions.getDeviceToken(this::registerRequest);
            } else {
                Functions.snackBar(this, binding.getRoot());
            }
        });
    }

    private void registerRequest(String deviceToken) {
        if (isNotEmpty(binding.tvFirstName, binding.firstName)
                && isNotEmpty(binding.tvFatherName, binding.fatherName)
                && isNotEmpty(binding.tvFamilyName, binding.familyName)
                && isNotEmpty(binding.tvBirthDate, binding.birthDate)
                && isNotEmpty(binding.tvAddress, binding.address)
                && isNotEmpty(binding.tvPhone, binding.phone)
                && isNotEmpty(binding.tvEmail, binding.email)
                && isValidEmail(binding.tvEmail, binding.email)
                && isNotEmpty(binding.tvPassword, binding.password)
                && isValidPassword(binding.tvPassword, binding.password)
                && isNotEmpty(binding.tvConfirmPassword, binding.confirmPassword)
                && isValidPassword(binding.tvConfirmPassword, binding.confirmPassword)
                && isChecked(binding.checkbox, binding.termsError)
        ) {
            showCustomProgress();
            mAuth.createUserWithEmailAndPassword(
                    getText(binding.email),
                    getText(binding.password)
            ).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    User user = new User();
                    user.setId(userId);
                    user.setFirstName(getText(binding.firstName));
                    user.setFatherName(getText(binding.fatherName));
                    user.setFamilyName(getText(binding.familyName));
                    user.setAddress(getText(binding.address));
                    user.setDateOfBirth(getText(binding.birthDate));
                    user.setPhone(getText(binding.phone));
                    user.setEmail(getText(binding.email));
                    user.setPassword(getText(binding.password));
                    user.setType(getText(binding.accountType));
                    user.setDeviceToken(deviceToken);
                    setUserDataRequest(user);
                } else {
                    dismissCustomProgress();
                    Functions.snackBar(this, binding.getRoot(), getString(R.string.error));
                }
            });
        }
    }

    private void setUserDataRequest(User user) {
        FirebaseDatabase.getInstance()
                .getReference(Constants.TABLE_USERS).child(user.getId())
                .setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Hawk.put(Constants.TYPE_USER, user);
                        Hawk.put(Constants.TYPE_DEVICE_TOKEN, user.getDeviceToken());
                        Hawk.put(Constants.TYPE_IS_LOGIN, true);
                        if (Objects.requireNonNull(user).getType().equals(Constants.TYPE_DOCTOR)) {
                            startActivity(DoctorActivity.class);
                        } else {
                            startActivity(MainActivity.class);
                        }
                        finish();
                    } else {
                        Functions.snackBar(this, binding.getRoot(), getString(R.string.error));
                    }
                });
    }

}