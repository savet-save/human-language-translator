package com.example.humanlanguagetranslator.activity;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.util.Utils;

public abstract class SingleFragmentActivity extends Easy2ManagerActivity {

    protected static final int DEFAULT_FRAGMENT_RESOURCES_ID = R.id.default_fragment;

    protected  abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(DEFAULT_FRAGMENT_RESOURCES_ID);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(DEFAULT_FRAGMENT_RESOURCES_ID, fragment)
                    .commit();
        }
    }

    protected void reloadShowFragment() {
        Fragment fragment = createFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(DEFAULT_FRAGMENT_RESOURCES_ID, fragment)
                .commit();
    }

}
