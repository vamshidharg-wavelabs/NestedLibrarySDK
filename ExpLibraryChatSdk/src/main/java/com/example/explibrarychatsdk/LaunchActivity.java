package com.example.explibrarychatsdk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.example.explibrarychatsdk.constants.AppConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class LaunchActivity extends AppCompatActivity {

    public static final String TAG = "LauchActivity";
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

//        username = findViewById(R.id.username);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras != null){
            String username = intent.getStringExtra("USERNAME");
            String uid = intent.getStringExtra("UID");

            proceedToLogin(uid);
        }
    }

    private void proceedToLogin(String UID){
        CometChat.login(UID, AppConfig.AUTH_KEY, new CometChat.CallbackListener<User>(){

            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "Login Successful: " + user.toString());
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Login failed with exception: " + e.getMessage());
            }
        });
    }
}

//android:name=".ExpLibraryChatSdkApp"