package com.example.explibrarychatsdk;

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

public class LaunchActivity extends AppCompatActivity {

    public static final String TAG = "LauchActivity";
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        username = findViewById(R.id.username);
    }

    public void cometChatLogin(View view) {
        if(username.getText().toString() != "") {
            proceedToLogin(username.getText().toString());
            Toast.makeText(this, "Login to CometChat", Toast.LENGTH_SHORT).show();
        }else{
            username.setError("Please enter username");
        }
    }

    private void proceedToLogin(String UID){
        CometChat.login(UID, AppConfig.AUTH_KEY, new CometChat.CallbackListener<User>(){

            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "Login Successful: " + user.toString());
                startActivity(new Intent(LaunchActivity.this, CometChatUI.class));
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "Login failed with exception: " + e.getMessage());
            }
        });
    }
}