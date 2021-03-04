package com.example.nestedlibrarysdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.explibrarychatsdk.LaunchActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void launchLibrary(View view) {
        startActivity(new Intent(MainActivity.this, LaunchActivity.class));
    }
}