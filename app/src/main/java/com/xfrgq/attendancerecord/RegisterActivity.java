package com.xfrgq.attendancerecord;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.xfrgq.attendancerecord.HttpRequestHelper;

import org.json.JSONObject;

import java.net.HttpURLConnection;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("注册");
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        EditText usernameEditText = findViewById(R.id.register_username);
        EditText passwordEditText = findViewById(R.id.register_password);
        Button registerButton = findViewById(R.id.register_submit_button);

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> registerUser(username, password)).start();
        });
    }

    private void registerUser(String username, String password) {
        try {
            JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", password);

            HttpURLConnection connection = HttpRequestHelper.createPostRequest("/register", json);
            String response = HttpRequestHelper.getResponse(connection);

            JSONObject jsonResponse = new JSONObject(response);
            runOnUiThread(() -> {
                if ("success".equals(jsonResponse.optString("status"))) {
                    Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
        }
    }
}