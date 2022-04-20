package com.example.humanlanguagetranslator.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.data.Dictionary;
import com.example.humanlanguagetranslator.data.VerifiedInfo;
import com.example.humanlanguagetranslator.data.Word;
import com.example.humanlanguagetranslator.data.WordJsonDefine;
import com.example.humanlanguagetranslator.helper.ImageHelper;
import com.example.humanlanguagetranslator.util.GlobalHandler;
import com.example.humanlanguagetranslator.util.Utils;
import com.example.humanlanguagetranslator.view.GifView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class WordFragment extends Fragment {
    private static final String ARGS_WORD_ID = "args_word_id";
    private static final String TAG = "WordFragment";
    private static final String COMMON_INPUT_DIALOG_TAG = "InputDialog";
    private static final String ARGS_ADD_MODE = "ARGS_ADD_MODE";

    private Word mWord;
    private TextView mSynonymText;
    private Button mDateButton;
    private TextView mVerifiedDateText;
    private OnWordUpdatedCallback mCallback;
    @Nullable
    private FragmentManager mFragmentManager;
    private TextView mTypeText;
    private TextView mTranslationText;
    private TextView mContentText;
    private TextView mQuarryText;
    private TextView mExampleText;
    private TextView mEarliestDateText;
    private TextView mEarliestAddrText;
    private TextView mVerifiedOtherText;
    private TextView mAuthorText;
    private TextView mRestorersText;
    private ImageView mImageView;
    private GifView mGifView;
    private ImageHelper.RequestImage mRequestImage;
    private boolean isAddMode;
    private List<CommonInputFragment.InputViewType> mViewList;

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
            isAddMode = arguments.getBoolean(ARGS_ADD_MODE);
            if (mWord != null) {
                mRequestImage = new ImageHelper.RequestImage(getActivity(), mWord, false) {
                    @Override
                    public void updateImage(byte[] data) {
                        myUpdateImage(data);
                    }
                };
            }
        }
        FragmentActivity activity = getActivity();
        if (null != activity) {
            mFragmentManager = activity.getSupportFragmentManager();
        }
    }

    private void myUpdateImage(byte[] data) {
        if (ImageHelper.ImageType.GIF == mRequestImage.getImageType()) {
            Movie movie = Movie.decodeByteArray(data, 0, data.length);
            GlobalHandler.getInstance().post2UIHandler(new Runnable() {
                @Override
                public void run() {
                    mGifView.setMovie(movie);
                    mGifView.setVisibility(View.VISIBLE);
                    mImageView.setVisibility(View.GONE);
                }
            });
        } else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (null == bitmap) {
                Utils.outLog(TAG, "decode bitmap fail from Byte Array");
            }
            GlobalHandler.getInstance().post2UIHandler(new Runnable() {
                @Override
                public void run() {
                    mImageView.setImageBitmap(bitmap);
                    mGifView.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_word_details, container, false);
        mContentText = layout.findViewById(R.id.details_word_content_text);
        mSynonymText = layout.findViewById(R.id.details_word_synonym_text);
        mTypeText = layout.findViewById(R.id.details_word_type_text);
        mTranslationText = layout.findViewById(R.id.details_word_translation_text);
        mQuarryText = layout.findViewById(R.id.details_word_quarry_text);
        mExampleText = layout.findViewById(R.id.details_word_example_text);
        mVerifiedDateText = layout.findViewById(R.id.details_word_verified_date_text);
        mEarliestDateText = layout.findViewById(R.id.details_word_earliest_date_text);
        mEarliestAddrText = layout.findViewById(R.id.details_word_earliest_addr_text);
        mVerifiedOtherText = layout.findViewById(R.id.details_word_verified_other_text);
        mAuthorText = layout.findViewById(R.id.details_word_author_text);
        mRestorersText = layout.findViewById(R.id.details_word_restorers_text);
        mImageView = layout.findViewById(R.id.details_word_item_image);
        mGifView = layout.findViewById(R.id.details_word_item_gif_image);

        updateViewList();
        setAllOnClick();
        setAllResultListener();

        if (!isAddMode) {
            setAllClickable(false);
        }

        //only test
        mDateButton = layout.findViewById(R.id.details_word_set_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAddMode = !isAddMode;
                setAllClickable(isAddMode);
                updateAllUI();
            }
        });

        //set show data
        updateAllUI();

        return layout;
    }

    private void setAllClickable(boolean mode) {
        for (CommonInputFragment.InputViewType inputViewType : mViewList) {
            View view = inputViewType.getView();
            if (mode != view.isClickable()) {
                view.setClickable(mode);
            }
        }
    }

    // call must is meaningful after call initViewList
    private void setAllResultListener() {
        if (null == mFragmentManager) {
            Utils.logDebug(TAG, "setAllResultListener() fail : mFragmentManager is null");
            return;
        }
        for (CommonInputFragment.InputViewType inputViewType : mViewList) {
            mFragmentManager.setFragmentResultListener(inputViewType.getId(),
                    this,
                    (requestKey, result) -> {
                        Utils.logDebug(TAG, "requestKey : " + requestKey);
                        String string = result.getString(CommonInputFragment.RESULT_TEXT_KEY);
                        Date date = (Date) result.getSerializable(CommonInputFragment.RESULT_DATE_KEY);
                        int type = result.getInt(CommonInputFragment.RESULT_SELECT_KEY);
                        ArrayList<String> arrayList = result.getStringArrayList(CommonInputFragment.RESULT_INPUT_ARRAY_CONTENT);
                        Utils.logDebug(TAG, "text :" + string);
                        Utils.logDebug(TAG, "date :" + date);
                        Utils.logDebug(TAG, "type :" + type);
                        Utils.logDebug(TAG, "array : " + arrayList);

                        inputViewType.updateCache(string, date, type, arrayList);
                        CommonInputFragment.InputViewType.SaveDataCallback callback = inputViewType.getSaveDataCallback();
                        if (null != callback) {
                            callback.saveData(result);
                        }
                        updateAllUI();
                        updateWordListUI();
                    });
        }
    }

    // call must is meaningful after call initViewList
    private void cleanAllResultListener() {
        if (null == mFragmentManager) {
            Utils.logDebug(TAG, "cleanAllResultListener() fail : mFragmentManager is null");
            return;
        }
        for (CommonInputFragment.InputViewType inputViewType : mViewList) {
            mFragmentManager.clearFragmentResultListener(inputViewType.getId());
        }
    }


    private void setAllOnClick() {
        if (null == mWord) {
            Utils.logDebug(TAG, "setAllOnClick() fail : mWord is null");
            return;
        }
        for (CommonInputFragment.InputViewType inputViewType : mViewList) {
            View view = inputViewType.getView();
            view.setClickable(true);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewClick(inputViewType);
                }
            });
        }
    }

    private void onViewClick(CommonInputFragment.InputViewType inputViewType) {
        if (null == mFragmentManager) {
            Utils.outLog(TAG, "can't get FragmentActivity");
            return;
        }
        CommonInputFragment.InputType inputType = inputViewType.getInputType();
        if (null == inputType) {
            return;
        }
        CommonInputFragment dialog;
        switch (inputType) {
            case TEXT_INPUT:
                dialog = CommonInputFragment.
                        newTextInstance(inputViewType.getTitle(),
                                inputViewType.getHint(),
                                inputViewType.getHasContent(),
                                inputViewType.getId());
                break;
            case DATE_PICKER:
                dialog = CommonInputFragment.
                        newDatePickerInstance(inputViewType.getTitle(),
                                inputViewType.getDate(),
                                inputViewType.getId());
                break;
            case SPINNER:
                dialog = CommonInputFragment.
                        newSelectInstance(inputViewType.getTitle(),
                                inputViewType.getSpinnerAllItem(),
                                inputViewType.getSelectItem(),
                                inputViewType.getId());
                break;
            case ARRAY_TEXT_INPUT:
                dialog = CommonInputFragment.
                        newArrayTextInputInstance(inputViewType.getTitle(),
                                inputViewType.getArrayContent(),
                                inputViewType.getId());
                break;
            default:
                Utils.logDebug(TAG, "need implementation : " + inputType);
                return;
        }
        dialog.show(mFragmentManager, COMMON_INPUT_DIALOG_TAG);
    }

    private void updateViewList() {
        if (null == mWord) { // There has to be one Word
            mWord = new Word();
            Dictionary.getInstance().addWord(mWord);
            Utils.outLog(TAG, "initViewList() warning : word is null , create a new Word");
        }

        if (null == mViewList) {
            mViewList = new ArrayList<>();
            mViewList.add(new CommonInputFragment.InputViewType(mContentText, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.WORD_KEY, mWord.getContent())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        String content = bundle.getString(CommonInputFragment.RESULT_TEXT_KEY);
                        mWord.setContent(content);
                    }));

            mViewList.add(new CommonInputFragment.InputViewType(mSynonymText, CommonInputFragment.InputType.ARRAY_TEXT_INPUT)
                    .setArrayTextInputInfo(WordJsonDefine.Explain.SYNONYM_KEY, mWord.getArraySynonym())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        ArrayList<String> content = bundle.getStringArrayList(CommonInputFragment.RESULT_INPUT_ARRAY_CONTENT);
                        mWord.setSynonym(content);
                    }));

            mViewList.add(new CommonInputFragment.InputViewType(mTypeText, CommonInputFragment.InputType.SPINNER)
                    .setSpinnerInfo(WordJsonDefine.Explain.TYPE_KEY, WordJsonDefine.WordType.getNames(), mWord.getWordType().ordinal())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        int order = bundle.getInt(CommonInputFragment.RESULT_SELECT_KEY);
                        WordJsonDefine.WordType[] values = WordJsonDefine.WordType.values();
                        if (order >= 0 && order < values.length) {
                            mWord.setWordType(values[order]);
                        }
                    }));

            mViewList.add(new CommonInputFragment.InputViewType(mTranslationText, CommonInputFragment.InputType.ARRAY_TEXT_INPUT)
                    .setArrayTextInputInfo(WordJsonDefine.Explain.TRANSLATION_KEY, mWord.getTranslations())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        ArrayList<String> content = bundle.getStringArrayList(CommonInputFragment.RESULT_INPUT_ARRAY_CONTENT);
                        mWord.setTranslations(content);
                    }));

            mViewList.add(new CommonInputFragment.InputViewType(mQuarryText, CommonInputFragment.InputType.ARRAY_TEXT_INPUT)
                    .setArrayTextInputInfo(WordJsonDefine.Explain.QUARRY_KEY, mWord.getQuarries())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        ArrayList<String> content = bundle.getStringArrayList(CommonInputFragment.RESULT_INPUT_ARRAY_CONTENT);
                        mWord.setQuarries(content);
                    }));

            mViewList.add(new CommonInputFragment.InputViewType(mExampleText, CommonInputFragment.InputType.ARRAY_TEXT_INPUT)
                    .setArrayTextInputInfo(WordJsonDefine.Explain.EXAMPLE_KEY, mWord.getExamples())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        ArrayList<String> content = bundle.getStringArrayList(CommonInputFragment.RESULT_INPUT_ARRAY_CONTENT);
                        mWord.setExamples(content);
                    }));

            VerifiedInfo verifiedInfo = mWord.getVerifiedInfo();
            mViewList.add(new CommonInputFragment.InputViewType(mVerifiedDateText, CommonInputFragment.InputType.DATE_PICKER)
                    .setDatePickerInfo(WordJsonDefine.Explain.VERIFIED_TIME_KEY, verifiedInfo.getVerifiedTime())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        Date date = (Date) bundle.getSerializable(CommonInputFragment.RESULT_DATE_KEY);
                        verifiedInfo.setVerifiedTime(date);
                    }));

            mViewList.add(new CommonInputFragment.InputViewType(mEarliestDateText, CommonInputFragment.InputType.DATE_PICKER)
                    .setDatePickerInfo(WordJsonDefine.Explain.EARLIEST_TIME_KEY, verifiedInfo.getEarliestTime())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        Date date = (Date) bundle.getSerializable(CommonInputFragment.RESULT_DATE_KEY);
                        verifiedInfo.setEarliestTime(date);
                    }));

            mViewList.add(new CommonInputFragment.InputViewType(mEarliestAddrText, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.EARLIEST_ADDR_KEY, verifiedInfo.getEarliestAddr())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        String info = bundle.getString(CommonInputFragment.RESULT_TEXT_KEY);
                        verifiedInfo.setEarliestAddr(info);
                    }));

            mViewList.add(new CommonInputFragment.InputViewType(mVerifiedOtherText, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.OTHER_KEY, verifiedInfo.getOther())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        String info = bundle.getString(CommonInputFragment.RESULT_TEXT_KEY);
                        verifiedInfo.setOther(info);
                    }));

            mViewList.add(new CommonInputFragment.InputViewType(mAuthorText, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.AUTHOR_KEY, mWord.getAuthor())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        String info = bundle.getString(CommonInputFragment.RESULT_TEXT_KEY);
                        mWord.setAuthor(info);
                    }));

            mViewList.add(new CommonInputFragment.InputViewType(mRestorersText, CommonInputFragment.InputType.ARRAY_TEXT_INPUT)
                    .setArrayTextInputInfo(WordJsonDefine.Explain.RESTORERS_KEY, mWord.getRestorers())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        ArrayList<String> content = bundle.getStringArrayList(CommonInputFragment.RESULT_INPUT_ARRAY_CONTENT);
                        mWord.setRestorers(content);
                    }));

            mViewList.add(new CommonInputFragment.InputViewType(mImageView, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.PICTURE_LINK, mWord.getPictureLink())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        Utils.logDebug(TAG, "set pic link");
                        String info = bundle.getString(CommonInputFragment.RESULT_TEXT_KEY);
                        mWord.setPictureLink(info);
                    }));

            mViewList.add(new CommonInputFragment.InputViewType(mGifView, CommonInputFragment.InputType.TEXT_INPUT)
                    .setTextInputInfo(WordJsonDefine.Explain.PICTURE_LINK, mWord.getPictureLink())
                    .setSaveDataCallback(bundle -> {
                        if (null == bundle) {
                            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                            return;
                        }
                        Utils.logDebug(TAG, "set pic link");
                        String info = bundle.getString(CommonInputFragment.RESULT_TEXT_KEY);
                        mWord.setPictureLink(info);
                    }));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cleanAllResultListener();
    }

    /**
     * set show text in view
     *
     * @param view   TextView
     * @param value  view text
     * @param isGone if true set view is gone(not visibility)
     */
    private void setTextShowUI(TextView view, String value, boolean isGone) {
        if (null == view) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return;
        }
        if (isGone) {
            view.setVisibility(View.GONE);
        }
        view.setText(value);

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
        mFragmentManager.setFragmentResultListener("test" + mWord.getId(),
                this,
                (requestKey, result) -> {
                    String text = result.getString(CommonInputFragment.RESULT_TEXT_KEY);
                    Utils.logDebug(TAG, "text :" + text);
                    mDateButton.setText(text);
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
        mFragmentManager.clearFragmentResultListener("test" + mWord.getId());
    }

    private void updateAllUI() {
        if (null == mWord) {
            Utils.logDebug(TAG, "update UI fail : mWord is null");
            return;
        }
        Utils.logDebug(TAG, "call updateAllUI(), isAddMode : " + isAddMode);

        setTextShowUI(mContentText, mWord.getContent(), false);

        String formatSynonym = mWord.getFormatSynonym(null);
        String synonym = getString(R.string.format_word_synonym, formatSynonym);
        setTextShowUI(mSynonymText, synonym, Utils.isEmptyString(formatSynonym));
        Utils.logDebug(TAG, synonym);

        String type = getString(R.string.format_word_type, mWord.getWordType().getName());
        setTextShowUI(mTypeText, type, false);

        setTextShowUI(mTranslationText, Utils.getFormatString(mWord.getTranslations()), false);

        String quarries = Utils.getFormatString(mWord.getQuarries());
        setTextShowUI(mQuarryText, quarries, Utils.isEmptyString(quarries));
        setTextShowUI(mExampleText, Utils.getFormatString(mWord.getExamples()), false);

        VerifiedInfo verifiedInfo = mWord.getVerifiedInfo();
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(verifiedInfo.getVerifiedTime());
        String verifiedDate = getString(R.string.format_word_data,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        Utils.logDebug(TAG, "verifiedDate :" + verifiedDate + " valid : " + verifiedInfo.isValid());
        setTextShowUI(mVerifiedDateText, getString(R.string.verified_date) + verifiedDate,
                !verifiedInfo.isValid());

        calendar.setTime(verifiedInfo.getEarliestTime());
        String earliestDate = getString(R.string.format_word_data,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        setTextShowUI(mEarliestDateText, getString(R.string.earliest_date) + earliestDate,
                !verifiedInfo.isValid());

        String earliestAddr = verifiedInfo.getEarliestAddr();
        setTextShowUI(mEarliestAddrText, getString(R.string.earliest_addr) + earliestAddr,
                Utils.isEmptyString(earliestAddr));

        String verifiedOther = verifiedInfo.getOther();
        setTextShowUI(mVerifiedOtherText, verifiedOther, Utils.isEmptyString(verifiedOther));

        String author = mWord.getAuthor();
        setTextShowUI(mAuthorText, getString(R.string.word_author) + author,
                Utils.isEmptyString(author));

        String restorers = Utils.getFormatString(mWord.getRestorers());
        setTextShowUI(mRestorersText, getString(R.string.word_restorers) + restorers,
                Utils.isEmptyString(restorers));


        byte[] imageData = Dictionary.getInstance().getImageData(mWord.getId());
        if (imageData != null) {
            myUpdateImage(imageData);
        } else {
            requestNewImage();
        }

        if (isAddMode) {
            for (CommonInputFragment.InputViewType inputViewType : mViewList) {
                View view = inputViewType.getView();
                if (view instanceof TextView) {
                    String text = ((TextView) view).getText().toString();
                    if (Utils.isEmptyString(text)) {
                        text = inputViewType.getHasContent();
                    }
                    ((TextView) view).setText(Utils.isEmptyString(text) ? inputViewType.getTitle() : text);
                    view.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void requestNewImage() {
        GlobalHandler.getInstance().post2BackgroundHandler(mRequestImage);
    }

    public static WordFragment newInstance(UUID wordId, boolean isAddMode) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_WORD_ID, wordId);
        args.putBoolean(ARGS_ADD_MODE, isAddMode);

        WordFragment wordFragment = new WordFragment();
        wordFragment.setArguments(args);
        return wordFragment;
    }
}
