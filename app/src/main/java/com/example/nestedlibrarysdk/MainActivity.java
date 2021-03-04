package com.example.nestedlibrarysdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.explibrarychatsdk.NestedLibrary;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NestedLibrary.d("NestedLibrary");
    }
}