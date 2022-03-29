package com.example.humanlanguagetranslator.activity;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.Utils;
import com.example.humanlanguagetranslator.fragment.WordFragment;
import com.example.humanlanguagetranslator.fragment.WordListFragment;
import com.example.humanlanguagetranslator.data.Word;

public class WordListActivity extends SingleFragmentActivity
        implements WordListFragment.OnItemSelectedCallback, WordFragment.OnWordUpdatedCallback {
    private static final String TAG = "WordListActivity";

    @Override
    protected Fragment createFragment() {
        return new WordListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_master_detail;
    }

    @Override
    public void onItemSelected(Word word) {
        if (Utils.isDualPane(this)) {
            Intent intent = WordPagerActivity.newIntent(this, word.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = WordFragment.newInstance(word.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }

    @Override
    public void onWordUpdated(Word word) {
        WordListFragment listFragment = (WordListFragment)getSupportFragmentManager()
                .findFragmentById(R.id.default_fragment);
        if ( null == listFragment) {
            Utils.outLog(TAG, "not find word list fragment");
            return;
        }
        listFragment.updateUI();
    }
}
