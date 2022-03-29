package com.example.humanlanguagetranslator.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.humanlanguagetranslator.ActivityManager;

public class Easy2ManagerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在活动管理器添加当前Activity
        ActivityManager.addActivity(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //从活动管理器删除当前Activity
        ActivityManager.removeActivity(this);
    }
}
