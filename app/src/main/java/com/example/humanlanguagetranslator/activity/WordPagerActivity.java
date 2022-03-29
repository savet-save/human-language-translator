package com.example.humanlanguagetranslator.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.Utils;
import com.example.humanlanguagetranslator.fragment.WordFragment;
import com.example.humanlanguagetranslator.data.Dictionary;
import com.example.humanlanguagetranslator.data.Word;

import java.util.List;
import java.util.UUID;

public class WordPagerActivity extends Easy2ManagerActivity {

    private static final String EXTRA_WORD_ID = "EXTRA_WORD_ID";
    private static final String TAG = "WordPagerActivity";

    private ViewPager mViewPager;
    private List<Word> mWords;

    public static Intent newIntent(Context packagerContext, UUID wordId) {
        Intent intent = new Intent(packagerContext, WordPagerActivity.class);
        intent.putExtra(EXTRA_WORD_ID, wordId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_pager);

        mViewPager = (ViewPager) findViewById(R.id.word_view_pager);

        mWords = Dictionary.getInstance().getWords();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager,
                FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                Word word = mWords.get(position);
                Utils.logDebug(TAG, "current : " + position);
                return WordFragment.newInstance(word.getId());
            }

            @Override
            public int getCount() {
                return mWords.size();
            }
        });

        UUID wordId = (UUID) getIntent().getSerializableExtra(EXTRA_WORD_ID);
        int position = Dictionary.getInstance().getPosition(wordId);
        if (Dictionary.NOT_FOUND_POSITION != position) {
            mViewPager.setCurrentItem(position);//need use after setAdapter()
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
