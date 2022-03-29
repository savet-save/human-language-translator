package com.example.humanlanguagetranslator.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.humanlanguagetranslator.fragment.SearchFragment;

public class SearchActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new SearchFragment();
    }

    public static Intent newInstance(Context packagerContext) {
        Intent intent = new Intent(packagerContext, SearchActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setContentViewBefore() {
        super.setContentViewBefore();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }
}
