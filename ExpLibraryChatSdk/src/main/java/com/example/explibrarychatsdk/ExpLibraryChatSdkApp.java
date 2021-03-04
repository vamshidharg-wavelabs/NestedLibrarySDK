package com.example.explibrarychatsdk;

import android.app.Application;
import android.util.Log;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.ui_settings.UIKitSettings;
import com.example.explibrarychatsdk.constants.AppConfig;

public class ExpLibraryChatSdkApp extends Application {

    private static final String TAG = "MY_TESTING_LOGS";

    @Override
    public void onCreate() {
        super.onCreate();

        initCometChat();
    }

    public void initCometChat(){
        AppSettings appSettings=new AppSettings.AppSettingsBuilder().subscribePresenceForAllUsers().setRegion(AppConfig.REGION).build();
        CometChat.init(this, AppConfig.APP_ID,appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String successMessage) {
                UIKitSettings.setAuthKey(AppConfig.AUTH_KEY);
                CometChat.setSource("ui-kit","android","java");
                Log.d(TAG, "Initialization completed successfully");
            }
            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Initialization failed with exception: " + e.getMessage());
            }
        });
    }

    public static void d(String message){
        Log.d(TAG, message);
    }
}
