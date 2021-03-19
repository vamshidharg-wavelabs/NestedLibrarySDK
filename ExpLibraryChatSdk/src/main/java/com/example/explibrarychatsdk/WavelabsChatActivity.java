package com.example.explibrarychatsdk;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.example.explibrarychatsdk.constants.AppConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class WavelabsChatActivity {

    public static final String TAG = "LoginClass";
    private static String token;

    public static void launchChatScreen(Activity MainActivity, String UID) {
        CometChat.login(UID, AppConfig.AUTH_KEY, new CometChat.CallbackListener<User>(){

            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "Login Successful: " + user.toString());

//                Intent intent = new Intent();
//                intent.setClass(MainActivity, CometChatUI.class);
//                MainActivity.startActivity(intent);

                token = MyFirebaseMessagingService.token;
                if(token == null)
                    fetchFirebaseToken(MainActivity, UID);
                else
                    registerPushNotifications(MainActivity, token, UID);
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Login failed with exception: " + e.getMessage());
            }
        });
    }

    private static void fetchFirebaseToken(Activity MainActivity, String UID){
        //@Provided by Google docs
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                token = task.getResult();
                Log.d(TAG, token);
                registerPushNotifications(MainActivity, token, UID);
            }
        });
    }

    private static void registerPushNotifications(Activity MainActivity, String token, String uid){
        CometChat.registerTokenForPushNotification(token, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e( "onSuccessPN: ",s );

                Intent intent = new Intent();
                intent.setClass(MainActivity, CometChatUI.class);
                MainActivity.startActivity(intent);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e("onErrorPN: ",e.getMessage() );
            }
        });
    }

    public static User getLoggedInUser(){
        return CometChat.getLoggedInUser();
    }
}
