package com.example.lab6_20211602_iot;

import android.app.Application;
import com.facebook.FacebookSdk;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id));

        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

    }
}
