package com.example.nestedlibrarysdk;

import android.app.Application;
import android.util.Log;

import com.example.explibrarychatsdk.Wavelabs;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CUSTOM_APP_LOG", "app module");
        Wavelabs.setAppConstants(this, AppConstants.APP_ID, AppConstants.REGION, AppConstants.AUTH_KEY);
    }
}
