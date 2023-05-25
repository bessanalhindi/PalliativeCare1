package com.palliative_care.ui.activities.other;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.palliative_care.R;
import com.palliative_care.base.BaseActivity;
import com.palliative_care.databinding.ActivityMainBinding;
import com.palliative_care.model.Category;
import com.palliative_care.ui.fragments.CategoryFragment;
import com.palliative_care.ui.fragments.InterestsFragment;
import com.palliative_care.ui.fragments.SettingFragment;

import java.util.function.BiConsumer;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();
    }

    private void initView() {
        initBottomBar();
        subscribeToTopic();
    }

    private void initBottomBar() {
        replaceFragment(new CategoryFragment(), R.string.categories);
        binding.bottomBar.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_categories:
                    itemHome = 1;
                    replaceFragment(new CategoryFragment(), R.string.categories);
                    return true;
                case R.id.nav_interests:
                    itemHome = 2;
                    replaceFragment(new InterestsFragment(), R.string.interests);
                    return true;
                case R.id.nav_settings:
                    itemHome = 2;
                    replaceFragment(new SettingFragment(), R.string.settings);
                    return true;
            }
            return false;
        });
        binding.bottomBar.setOnItemReselectedListener(item -> {
        });
    }

    private void replaceFragment(Fragment fragment, int title) {
        binding.appbar.title.setText(getString(title));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.host_fragment, fragment);
        transaction.addToBackStack(fragment.getClass().getSimpleName());
        transaction.commit();
    }

    private void subscribeToTopic() {
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
                    FirebaseMessaging.getInstance().subscribeToTopic(categoryObject.getTopic());
                }
            });

        }
    }

    private Toast backToasty;
    private long backPressedTime;
    private int itemHome = 1;

    @Override
    public void onBackPressed() {
        if (itemHome != 1) {
            binding.bottomBar.setSelectedItemId(R.id.nav_categories);
            replaceFragment(new CategoryFragment(), R.string.app_name);
        } else {
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

}