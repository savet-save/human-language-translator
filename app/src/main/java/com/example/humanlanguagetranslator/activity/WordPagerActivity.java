package com.example.humanlanguagetranslator.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

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

    private ViewPager2 mViewPager2;
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

        mViewPager2 = (ViewPager2) findViewById(R.id.word_view_pager);

        mWords = Dictionary.getInstance().getWords();
        mViewPager2.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return mWords.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Word word = mWords.get(position);
                Utils.logDebug(TAG, "create word pager, position : " + position);
                return WordFragment.newInstance(word.getId());
            }
        });

        UUID wordId = (UUID) getIntent().getSerializableExtra(EXTRA_WORD_ID);
        int position = Dictionary.getInstance().getPosition(wordId);
        if (Dictionary.NOT_FOUND_POSITION != position) {
            mViewPager2.setCurrentItem(position, false);//need use after setAdapter()
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
