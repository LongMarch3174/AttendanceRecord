package com.xfrgq.attendancerecord;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xfrgq.attendancerecord.HttpRequestHelper;

import org.json.JSONObject;

import java.net.HttpURLConnection;

public class ProfileFragment extends Fragment {

    private static final String SHARED_PREFS = "userPrefs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView usernameText = view.findViewById(R.id.username_text);
        TextView emailText = view.findViewById(R.id.email_text);
        ImageView avatarImage = view.findViewById(R.id.avatar_image);
        Button settingsButton = view.findViewById(R.id.settings_button);
        Button editProfileButton = view.findViewById(R.id.edit_profile_button);
        Button logoutButton = view.findViewById(R.id.logout_button);

        // 获取传递过来的用户名并显示
        String username = requireActivity().getIntent().getStringExtra("username");
        if (username != null) {
            usernameText.setText(username);
        }

        // 从服务端获取用户数据
        fetchUserInfo(usernameText, emailText, avatarImage, username);

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        });

        editProfileButton.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
            String savedUsername = sharedPreferences.getString("username", "");
            String email = sharedPreferences.getString("email", "");

            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            intent.putExtra("username", savedUsername);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            logout();
        });

        return view;
    }

    private void fetchUserInfo(TextView usernameText, TextView emailText, ImageView avatarImage, String passedUsername) {
        new Thread(() -> {
            try {
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("username", passedUsername);

                HttpURLConnection connection = HttpRequestHelper.createPostRequest("/fetchUserInfo", jsonRequest);
                String response = HttpRequestHelper.getResponse(connection);

                JSONObject jsonResponse = new JSONObject(response);
                if ("success".equals(jsonResponse.optString("status"))) {
                    String username = jsonResponse.optString("username");
                    String email = jsonResponse.optString("email");
                    int avatarResource = R.drawable.default_avatar; // 可以从服务器返回头像路径处理

                    // 保存用户数据到 SharedPreferences
                    SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", username);
                    editor.putString("email", email);
                    editor.apply();

                    // 更新 UI
                    requireActivity().runOnUiThread(() -> {
                        usernameText.setText(username);
                        emailText.setText(email);
                        avatarImage.setImageResource(avatarResource);
                    });
                } else {
                    requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "获取用户信息失败", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "网络错误", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void logout() {
        // 提示用户注销成功
        Toast.makeText(getActivity(), "已注销", Toast.LENGTH_SHORT).show();

        // 清空 SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // 返回登录界面
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
