package com.example.nestedlibrarysdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.example.explibrarychatsdk.WavelabsChatActivity;
import com.example.explibrarychatsdk.constants.AppConfig;
import com.example.explibrarychatsdk.create_user.CreateUser;

public class MainActivity extends AppCompatActivity {

    EditText _user_id;
    public static String USERNAME = "";
    public static String USER_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _user_id = findViewById(R.id.view_user_id);
    }

    public void arteriaLogin(View view) {
        USER_ID = _user_id.getText().toString();

        if(USER_ID.isEmpty()){
            if(USER_ID.isEmpty()) _user_id.setError("Please provide User ID");
        }else{
            WavelabsChatActivity.launchChatScreen(MainActivity.this, USER_ID);
//            login();
        }
    }

    public void createNewUser(View view) {
        startActivity(new Intent(MainActivity.this, CreateUser.class));
    }
}