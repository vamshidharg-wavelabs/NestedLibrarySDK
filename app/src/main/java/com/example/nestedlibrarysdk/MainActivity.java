package com.example.nestedlibrarysdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.explibrarychatsdk.LaunchActivity;
import com.example.explibrarychatsdk.LoginClass;

public class MainActivity extends AppCompatActivity {

    EditText username, user_id;
    public static String USERNAME = "";
    public static String USER_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        username = findViewById(R.id.username);
        user_id = findViewById(R.id.user_id);
    }

    public void arteriaLogin(View view) {
//        USERNAME = username.getText().toString();
        USER_ID = user_id.getText().toString();

        if(!USER_ID.isEmpty()){
            LoginClass.launchCometChat(MainActivity.this, USERNAME, USER_ID);
        }else{
//            if(USERNAME.isEmpty()) username.setError("Please provide Username");
            if(USER_ID.isEmpty()) user_id.setError("Please provide User ID");
        }
    }
}