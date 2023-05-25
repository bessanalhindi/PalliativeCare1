package com.palliative_care.base;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.palliative_care.R;
import com.palliative_care.helpers.Functions;
import com.palliative_care.helpers.LocaleUtils;
import com.palliative_care.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class BaseActivity extends AppCompatActivity {


    protected User user = Functions.getUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.onCreate(this, "ar");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocaleUtils.onCreate(this, "ar");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LocaleUtils.onCreate(this, "ar");
    }

    protected Dialog dialog;

    protected void showCustomProgress() {
        if (dialog == null) {
            dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            dialog.setContentView(R.layout.content_loading_screen);
            dialog.setCancelable(true);
            dialog.show();
        } else {
            dialog.show();
        }
    }

    protected void dismissCustomProgress() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    protected void statusBarColor(int color) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, color));
    }

    protected void statusBarsLight(boolean isLight, View view) {
        WindowCompat.getInsetsController(getWindow(), view).setAppearanceLightStatusBars(isLight);
    }

    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    protected void datePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int cYear = calendar.get(Calendar.YEAR);
        int cMonth = calendar.get(Calendar.MONTH);
        int cDay = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                R.style.Theme_ChampionsAcademy_StyleMaterialCalendar, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            editText.setText(sdf.format(calendar.getTime()));
        }, cYear, cMonth, cDay);
        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
    }

    protected boolean isNotEmpty(TextInputLayout textLayout, TextInputEditText editText) {
        if (Objects.requireNonNull(editText.getText()).toString().trim().isEmpty()) {
            textLayout.setError("");
            textLayout.setError(getString(R.string.invalid_field));
            textLayout.setErrorEnabled(true);
            return false;
        } else {
            textLayout.setErrorEnabled(false);
            return true;
        }
    }

    protected boolean isValidEmail(TextInputLayout textLayout, TextInputEditText editText) {
        if (!Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(editText.getText()).toString()).matches()) {
            textLayout.setError(getString(R.string.invalid_email));
            textLayout.setErrorEnabled(true);
            return false;
        } else {
            textLayout.setErrorEnabled(false);
            return true;
        }
    }

    protected boolean isValidPassword(TextInputLayout textLayout, TextInputEditText editText) {
        if (Objects.requireNonNull(editText.getText()).toString().trim().length() >= 6) {
            textLayout.setErrorEnabled(false);
            return true;
        } else {
            textLayout.setError(getString(R.string.invalid_password));
            textLayout.setErrorEnabled(true);
            return false;
        }
    }

    protected boolean isChecked(CheckBox checkBox, View view) {
        if (checkBox.isChecked()) {
            view.setVisibility(View.GONE);
            return true;
        } else {
            view.setVisibility(View.VISIBLE);
            return false;
        }
    }

    protected String getText(TextInputEditText editText) {
        return Objects.requireNonNull(editText.getText()).toString().trim();
    }
    protected String getText(EditText editText) {
        return Objects.requireNonNull(editText.getText()).toString().trim();
    }
}
