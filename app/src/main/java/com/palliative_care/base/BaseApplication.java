package com.palliative_care.base;

import android.app.Application;

import com.orhanobut.hawk.Hawk;
import com.palliative_care.helpers.LocaleUtils;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Hawk.init(this).build();
        LocaleUtils.onCreate(this, "ar");
    }

}
