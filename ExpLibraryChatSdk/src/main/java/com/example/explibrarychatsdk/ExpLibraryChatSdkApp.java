package com.example.explibrarychatsdk;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.ui_settings.UIKitSettings;
import com.example.explibrarychatsdk.constants.AppConfig;

public class ExpLibraryChatSdkApp extends Application {

    private static final String TAG = "MY_TESTING_LOGS";
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("CUSTOM_APP_LOG", "ExpLibraryChatSdkApp module");
        initCometChat();
        createNotificationChannel();
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

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "channel1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "channel2",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel1.setDescription("This is Channel 2");


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }

    }
}
