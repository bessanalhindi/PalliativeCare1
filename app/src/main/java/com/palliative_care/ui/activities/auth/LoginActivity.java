package com.palliative_care.ui.activities.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.hawk.Hawk;
import com.palliative_care.R;
import com.palliative_care.base.BaseActivity;
import com.palliative_care.databinding.ActivityLoginBinding;
import com.palliative_care.helpers.Constants;
import com.palliative_care.helpers.Functions;
import com.palliative_care.model.User;
import com.palliative_care.ui.activities.doctor.DoctorActivity;
import com.palliative_care.ui.activities.other.MainActivity;

import java.util.Objects;

public class LoginActivity extends BaseActivity {

    ActivityLoginBinding binding;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        statusBarColor(R.color.white);
        statusBarsLight(true, binding.logo);
        binding.signUp.setOnClickListener(v -> startActivity(RegisterTypeActivity.class));
        binding.login.setOnClickListener(v -> {
            if (Functions.isInternetAvailable(this)) {
                Functions.getDeviceToken(this::loginRequest);
            } else {
                Functions.snackBar(this, binding.getRoot());
            }
        });
    }

    private void loginRequest(String deviceToken) {
        if (isNotEmpty(binding.tvEmail, binding.email)
                && isValidEmail(binding.tvEmail, binding.email)
                && isNotEmpty(binding.tvPassword, binding.password)
                && isValidPassword(binding.tvPassword, binding.password)
        ) {
            showCustomProgress();
            mAuth.signInWithEmailAndPassword(
                    getText(binding.email),
                    getText(binding.password)
            ).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    getUserData(userId, deviceToken);
                } else {
                    dismissCustomProgress();
                    Functions.snackBar(this, binding.getRoot(), getString(R.string.error));
                }
            });
        }
    }

    private void getUserData(String userId, String deviceToken) {
        FirebaseDatabase.getInstance().getReference()
                .child(Constants.TABLE_USERS).child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        dismissCustomProgress();
                        if (snapshot.exists()) {
                            Functions.pushDeviceToken(userId, deviceToken);
                            User user = snapshot.getValue(User.class);
                            Hawk.put(Constants.TYPE_USER, user);
                            Hawk.put(Constants.TYPE_DEVICE_TOKEN, deviceToken);
                            Hawk.put(Constants.TYPE_IS_LOGIN, true);
                            if (Objects.requireNonNull(user).getType().equals(Constants.TYPE_DOCTOR)) {
                                startActivity(DoctorActivity.class);
                            } else {
                                startActivity(MainActivity.class);
                            }
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        dismissCustomProgress();
                    }
                });

    }

}