package com.xfrgq.attendancerecord;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.xfrgq.attendancerecord.HttpRequestHelper;

import org.json.JSONObject;

import java.net.HttpURLConnection;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("修改个人资料");
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        TextView usernameEditText = findViewById(R.id.edit_username);
        EditText emailEditText = findViewById(R.id.edit_email);
        Button saveButton = findViewById(R.id.save_button);

        // 获取传递的数据
        String currentUsername = getIntent().getStringExtra("username");
        String currentEmail = getIntent().getStringExtra("email");

        if (currentUsername != null) {
            usernameEditText.setText(currentUsername);
        }
        if (currentEmail != null) {
            emailEditText.setText(currentEmail);
        }

        saveButton.setOnClickListener(v -> {
            // 获取用户输入的内容
            String updatedUsername = usernameEditText.getText().toString().trim();
            String updatedEmail = emailEditText.getText().toString().trim();

            if (updatedUsername.isEmpty() || updatedEmail.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "用户名或邮箱不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> saveUserData(updatedUsername, updatedEmail)).start();
        });
    }

    private void saveUserData(String username, String email) {
        try {
            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("username", username);
            jsonRequest.put("email", email);

            HttpURLConnection connection = HttpRequestHelper.createPostRequest("/updateUserInfo", jsonRequest);
            String response = HttpRequestHelper.getResponse(connection);

            JSONObject jsonResponse = new JSONObject(response);
            runOnUiThread(() -> {
                if ("success".equals(jsonResponse.optString("status"))) {
                    Toast.makeText(EditProfileActivity.this, "个人资料已更新", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, jsonResponse.optString("message"), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "网络错误", Toast.LENGTH_SHORT).show());
        }
    }
}
