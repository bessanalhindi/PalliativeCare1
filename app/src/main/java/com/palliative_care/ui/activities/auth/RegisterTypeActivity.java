package com.palliative_care.ui.activities.auth;

import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.palliative_care.R;
import com.palliative_care.base.BaseActivity;
import com.palliative_care.databinding.ActivityRegisterTypeBinding;
import com.palliative_care.helpers.Constants;

public class RegisterTypeActivity extends BaseActivity {

    ActivityRegisterTypeBinding binding;
    String typeSelected = Constants.TYPE_PATIENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterTypeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        binding.appbar.title.setText(getString(R.string.signup));
        binding.appbar.back.setOnClickListener(v -> onBackPressed());
        binding.next.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra(Constants.TYPE, typeSelected);
            startActivity(intent);
        });
        switchChooseAccountType();
    }

    private void switchChooseAccountType() {

        binding.doctor.setOnClickListener(v -> {
            typeSelected = Constants.TYPE_DOCTOR;
            binding.doctorImg.setBackgroundResource(R.drawable.shape_bg_chose_account_selected);
            binding.doctorChecked.setVisibility(View.VISIBLE);
            font(binding.doctorTitle, R.font.cairo_bold);

            binding.patientImg.setBackgroundResource(R.drawable.shape_bg_chose_account);
            binding.patientChecked.setVisibility(View.INVISIBLE);
            font(binding.patientTitle, R.font.cairo_regular);
        });

        binding.patient.setOnClickListener(v -> {
            typeSelected = Constants.TYPE_PATIENT;
            binding.doctorImg.setBackgroundResource(R.drawable.shape_bg_chose_account);
            binding.doctorChecked.setVisibility(View.INVISIBLE);
            font(binding.doctorTitle, R.font.cairo_regular);

            binding.patientImg.setBackgroundResource(R.drawable.shape_bg_chose_account_selected);
            binding.patientChecked.setVisibility(View.VISIBLE);
            font(binding.patientTitle, R.font.cairo_bold);
        });
    }

    private void font(TextView textView, int font) {
        Typeface typeface = ResourcesCompat.getFont(this, font);
        textView.setTypeface(typeface);
    }

}