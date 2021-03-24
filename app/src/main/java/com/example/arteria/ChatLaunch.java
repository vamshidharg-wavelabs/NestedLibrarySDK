package com.example.arteria;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.explibrarychatsdk.WavelabsChatActivity;

public class ChatLaunch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_launch);
    }

    public void launchChatApp(View view) {
        WavelabsChatActivity.launchChatScreen(ChatLaunch.this, MainActivity.USER_ID);
    }
}