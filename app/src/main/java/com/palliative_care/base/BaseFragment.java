package com.palliative_care.base;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.palliative_care.helpers.LocaleUtils;

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleUtils.onCreate(requireActivity(), "ar");
    }

    @Override
    public void onResume() {
        super.onResume();
        LocaleUtils.onCreate(requireActivity(), "ar");
    }

    protected void startActivity(Class<?> activity) {
        Intent intent = new Intent(requireActivity(), activity);
        startActivity(intent);
    }

}
