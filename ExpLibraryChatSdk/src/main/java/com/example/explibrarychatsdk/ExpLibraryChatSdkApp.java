package com.example.explibrarychatsdk;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

public class ExpLibraryChatSdkApp extends Application {

    private static final String TAG = "MY_TESTING_LOGS";

    @Override
    public void onCreate() {
        super.onCreate();


    }

    public static void d(String message){
        Log.d(TAG, message);
    }

    public static void launchActivity(){

    }
}
