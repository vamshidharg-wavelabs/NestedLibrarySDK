package com.example.arteria;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.cometchat.pro.core.CometChat;
import com.example.explibrarychatsdk.WavelabsChatActivity;

public class ChatLaunch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_launch);
    }

    public void launchChatApp(View view) {
        String UID = "";
        if(CometChat.getLoggedInUser() != null){
            UID = CometChat.getLoggedInUser().getUid();
        }else{
            UID = MainActivity.USER_ID;
        }
        WavelabsChatActivity.launchChatScreen(ChatLaunch.this, UID);
    }
}