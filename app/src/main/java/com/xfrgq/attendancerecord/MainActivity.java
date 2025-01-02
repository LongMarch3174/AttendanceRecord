package com.xfrgq.attendancerecord;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the logo
        ImageView logo = findViewById(R.id.logo);
        logo.setImageResource(R.drawable.logo); // Replace with your actual logo resource

        // Set up the welcome message
        TextView welcomeMessage = findViewById(R.id.welcome_message);
        welcomeMessage.setText("Welcome to Attendance App");

        // Set up the login button
        Button loginButton = findViewById(R.id.welcome_button);
        loginButton.setOnClickListener(v -> {
            // Navigate to the LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
