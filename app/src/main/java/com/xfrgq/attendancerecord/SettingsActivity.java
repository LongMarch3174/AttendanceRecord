package com.xfrgq.attendancerecord;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 设置 Toolbar 作为返回按钮
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 启用返回箭头
            getSupportActionBar().setTitle("设置");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // 返回到上一界面
        return true;
    }
}
