package com.example.humanlanguagetranslator.fragment;


import android.content.Context;
import android.os.Bundle;
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

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.Utils;
import com.example.humanlanguagetranslator.data.Dictionary;
import com.example.humanlanguagetranslator.data.Word;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class WordFragment extends Fragment {
    private static final String ARGS_WORD_ID = "args_word_id";
    private static final String TAG = "WordFragment";
    private static final String DIALOG_DATE_TAG = "DialogDate";

    private static final int REQUEST_DATE = 0;

    private Word mWord;
    private TextView mSynonymText;
    private Button mDateButton;
    private TextView mDateText;
    private SimpleDateFormat formatDate;
    private OnWordUpdatedCallback mCallback;
    private FragmentManager mFragmentManager;
    private TextView mTypeText;
    private TextView mTranslationText;
    private TextView mContentText;
    private TextView mQuarryText;
    private TextView mExampleText;

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
        // init data show format
        formatDate = new SimpleDateFormat("yyyy '" + getString(R.string.year)
                + "' MM '" + getString(R.string.month)
                + "' dd '" + getString(R.string.day) + "'", Locale.getDefault());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_details, container, false);
        mContentText = (TextView) view.findViewById(R.id.details_word_content_text);
        mSynonymText = (TextView) view.findViewById(R.id.details_word_synonym_text);
        mTypeText = (TextView) view.findViewById(R.id.details_word_type_text);
        mDateText = (TextView) view.findViewById(R.id.details_word_date);
        mTranslationText = (TextView) view.findViewById(R.id.details_word_translation_text);
        mQuarryText = (TextView) view.findViewById(R.id.details_word_quarry_text);
        mExampleText = (TextView) view.findViewById(R.id.details_word_example_text);

        // init set data button
        mDateButton = (Button) view.findViewById(R.id.details_word_set_date);
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

        //set show data
        if (null != mWord) {
            setTextShowUI(mContentText, mWord.getContent(), false);
            setTextShowUI(mSynonymText,
                    getJointString(getString(R.string.word_synonym) + " : ", mWord.getFormatSynonym(null)),
                    true);
            setTextShowUI(mTypeText,
                    getJointString(getString(R.string.word_type) + " : ", mWord.getWordType().getName()),
                    false);
            setTextShowUI(mTranslationText, Utils.getFormatString(mWord.getTranslations()), false);
            setTextShowUI(mQuarryText, Utils.getFormatString(mWord.getQuarries()), true);
            setTextShowUI(mExampleText, Utils.getFormatString(mWord.getExamples()), false);
            updateDateUI();
        }

        return view;
    }

    /**
     * joint String
     * @param hand hand
     * @param append append
     * @return  <p> if hand or append is null, return null </p>
     * <p> if append is empty, return null </p>
     * <p> other hand and append String </p>
     */
    @Nullable
    private String getJointString(String hand, String append) {
        if (null == append || null == hand) {
            return null;
        }
        if (append.isEmpty()) {
            return null;
        }
        return hand + append;
    }

    /**
     *  set show text in view
     * @param view TextView
     * @param value view text
     * @param isGone if value is null or empty, whether set view is gone(not visibility)
     */
    private void setTextShowUI(TextView view, String value, boolean isGone) {
        if (null == view) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return;
        }
        if ((null == value || value.isEmpty()) && isGone) {
            view.setVisibility(View.GONE);
        } else {
            view.setText(value);
        }
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
        if (mDateText != null) {
            mDateText.setText(date);
        }
    }

    public static WordFragment newInstance(UUID wordId) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_WORD_ID, wordId);

        WordFragment wordFragment = new WordFragment();
        wordFragment.setArguments(args);
        return wordFragment;
    }
}
