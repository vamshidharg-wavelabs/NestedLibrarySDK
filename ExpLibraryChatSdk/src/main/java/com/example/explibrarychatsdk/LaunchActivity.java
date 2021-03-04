package com.example.explibrarychatsdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    public void cometChatLogin(View view) {
        Toast.makeText(this, "Login to CometChat", Toast.LENGTH_SHORT).show();
    }
}