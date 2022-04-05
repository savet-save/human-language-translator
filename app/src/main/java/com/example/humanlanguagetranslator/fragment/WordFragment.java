package com.example.humanlanguagetranslator.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.Utils;
import com.example.humanlanguagetranslator.data.Dictionary;
import com.example.humanlanguagetranslator.data.Word;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class WordFragment extends Fragment {
    private static final String ARGS_WORD_ID = "args_word_id";
    private static final String TAG = "WordFragment";
    private static final String DIALOG_DATE_TAG = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private Word mWord;
    private TextView mWordTitle;
    private Button mDateButton;
    private TextView mDateText;
    private SimpleDateFormat formatDate;
    private OnWordUpdatedCallback mCallback;
    private FragmentManager mFragmentManager;

    public interface OnWordUpdatedCallback {
        void onWordUpdated(Word word);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnWordUpdatedCallback) {
            mCallback = (OnWordUpdatedCallback) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (null != arguments) {
            UUID wordId = (UUID) arguments.getSerializable(ARGS_WORD_ID);
            mWord = Dictionary.getInstance().getWord(wordId);
        }
        FragmentActivity activity = getActivity();
        if (null != activity) {
            mFragmentManager = activity.getSupportFragmentManager();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_word_details, container, false);
        mWordTitle = v.findViewById(R.id.details_word_title_text);
        // init data show format
        formatDate = new SimpleDateFormat("yyyy '" + getString(R.string.year)
                + "' MM '" + getString(R.string.month)
                + "' dd '" + getString(R.string.day) + "'", Locale.getDefault());

        // set date
        mDateText = (TextView) v.findViewById(R.id.details_word_date);
        updateDateUI();

        // init set data button
        mDateButton = (Button) v.findViewById(R.id.details_word_set_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mFragmentManager) {
                    Utils.outLog(TAG, "can't get FragmentActivity");
                    return;
                }
                DatePickerFragment dialog = DatePickerFragment.newInstance(null);
                dialog.show(mFragmentManager, DIALOG_DATE_TAG);

            }
        });
        if (null != mWord) {
            mWordTitle.setText(mWord.getContent());
        }
        return v;
    }

    private void updateWordListUI() {
        if (null != mCallback) {
            mCallback.onWordUpdated(mWord);
        }
    }

    // 可见时
    @Override
    public void onResume() {
        super.onResume();
        if (null == mFragmentManager) {
            return;
        }
        Utils.logDebug(TAG, "set Fragment Result Listener id : "
                + (mWord == null ? "null word" : mWord.getId()));
        mFragmentManager.setFragmentResultListener(DatePickerFragment.REQUEST_DATE_KEY,
                getViewLifecycleOwner(),
                (requestKey, result) -> {
                    if (!DatePickerFragment.REQUEST_DATE_KEY.equals(requestKey)) {
                        Utils.logDebug(TAG, "not equals");
                        return;
                    }
                    Date date = (Date) result.getSerializable(DatePickerFragment.DATE_KEY);
                    Utils.logDebug(TAG, "date :" + date);
                    mWord.setFirstDate(date);
                    updateDateUI();
                    updateWordListUI();
                });
    }

    // 不可见时
    @Override
    public void onPause() {
        super.onPause();
        if (null == mFragmentManager) {
            return;
        }
        Utils.logDebug(TAG, "clear Fragment Result Listener id : "
                + (mWord == null ? "null word" : mWord.getId()));
        mFragmentManager.clearFragmentResultListener(DatePickerFragment.REQUEST_DATE_KEY);
    }

    private void updateDateUI() {
        String date = formatDate.format(mWord.getFirstDate());
        mDateText.setText(date);
    }

    public static WordFragment newInstance(UUID wordId) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_WORD_ID, wordId);

        WordFragment wordFragment = new WordFragment();
        wordFragment.setArguments(args);
        return wordFragment;
    }
}
