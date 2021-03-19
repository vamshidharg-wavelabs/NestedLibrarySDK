package com.example.nestedlibrarysdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.example.explibrarychatsdk.WavelabsChatActivity;
import com.example.explibrarychatsdk.create_user.CreateUser;

public class MainActivity extends AppCompatActivity {

    EditText user_id;
    public static String USERNAME = "";
    public static String USER_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_id = findViewById(R.id.user_id);
    }

    public void arteriaLogin(View view) {
        USER_ID = user_id.getText().toString();

        if(!USER_ID.isEmpty()){
            WavelabsChatActivity.launchChatScreen(MainActivity.this, USER_ID);
        }else{
            if(USER_ID.isEmpty()) user_id.setError("Please provide User ID");
        }
    }

    public void createNewUser(View view) {
        startActivity(new Intent(MainActivity.this, CreateUser.class));
    }
}