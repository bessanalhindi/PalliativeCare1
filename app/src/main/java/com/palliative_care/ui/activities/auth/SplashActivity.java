package com.palliative_care.ui.activities.auth;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.orhanobut.hawk.Hawk;
import com.palliative_care.R;
import com.palliative_care.base.BaseActivity;
import com.palliative_care.helpers.Constants;
import com.palliative_care.helpers.Functions;
import com.palliative_care.helpers.LocaleUtils;
import com.palliative_care.ui.activities.doctor.DoctorActivity;
import com.palliative_care.ui.activities.other.MainActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LocaleUtils.setLocale(this, "ar");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private void initView() {
        Log.e("response", "getLanguage: " + LocaleUtils.getLanguage(this));
        new Handler().postDelayed(() -> {
            if (Hawk.get(Constants.TYPE_IS_LOGIN, false)) {
                if (Functions.getUser().getType().equals(Constants.TYPE_DOCTOR)) {
                    startActivity(DoctorActivity.class);
                } else {
                    startActivity(MainActivity.class);
                }
            } else {
                startActivity(LoginActivity.class);
            }
            finish();
        }, 2000);
    }
}