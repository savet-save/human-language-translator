package com.example.humanlanguagetranslator.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.data.Dictionary;
import com.example.humanlanguagetranslator.data.Word;
import com.example.humanlanguagetranslator.fragment.WordFragment;
import com.example.humanlanguagetranslator.fragment.WordListFragment;
import com.example.humanlanguagetranslator.util.GlobalHandler;
import com.example.humanlanguagetranslator.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class WordListActivity extends SingleFragmentActivity
        implements WordListFragment.OnItemSelectedCallback, WordFragment.OnWordUpdatedCallback {
    public static final String EXTRA_WORD_LIST = "EXTRA_WORD_LIST";

    private static final String TAG = "WordListActivity";

    @Override
    protected Fragment createFragment() {
        return WordListFragment.newInstance(getIntent());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEnvironment();
    }

    /**
     * init application environment support
     */
    private void initEnvironment() {
//        Utils.enableStrictMode(false);
        GlobalHandler.getInstance();
        Dictionary.getInstance().init(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_master_detail;
    }

    @Override
    public void onItemSelected(Word word, boolean isAddMode) {
        if (Utils.isDualPane(this)) {
            Intent intent = WordPagerActivity.newIntent(this, word.getId(), isAddMode);
            startActivity(intent);
        } else {
            Fragment newDetail = WordFragment.newInstance(word.getId(), isAddMode);

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

    /**
     * gets an intent to display the words
     * @param packagerContext context
     * @param words must is ArrayList, if not show define words
     * @return intent
     */
    public static Intent newIntent(Context packagerContext, List<Word> words) {
        Intent intent = new Intent(packagerContext, WordListActivity.class);
        if (words instanceof ArrayList) {
            Utils.logDebug(TAG, "extra add words");
            intent.putParcelableArrayListExtra(EXTRA_WORD_LIST, (ArrayList<? extends Parcelable>) words);
        }
        return intent;
    }
}
