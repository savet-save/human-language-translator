package com.example.humanlanguagetranslator.fragment;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.data.Dictionary;
import com.example.humanlanguagetranslator.data.VerifiedInfo;
import com.example.humanlanguagetranslator.data.Word;
import com.example.humanlanguagetranslator.data.WordJsonDefine;
import com.example.humanlanguagetranslator.helper.FileHelper;
import com.example.humanlanguagetranslator.helper.ImageHelper;
import com.example.humanlanguagetranslator.util.GlobalHandler;
import com.example.humanlanguagetranslator.util.Utils;
import com.example.humanlanguagetranslator.view.GifView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class WordFragment extends Fragment {
    private static final String ARGS_WORD_ID = "ARGS_WORD_ID";
    private static final String ARGS_MODIFY_MODE = "ARGS_MODIFY_MODE";
    private static final String TAG = "WordFragment";
    private static final String COMMON_INPUT_DIALOG_TAG = "InputDialog";

    /**
     * not null after onCreate()
     */
    private Word mWord;
    private TextView mSynonymText;
    private TextView mVerifiedDateText;
    private OnWordUpdatedCallback mCallback;
    /**
     * init after onCreate()
     */
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
    private boolean isModifyMode;
    private List<CommonInputFragment.InputViewType> mViewList;
    private CommonInputFragment.InputViewType mSelectNamesHelper;

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARGS_WORD_ID, null == mWord ? null : mWord.getId());
        outState.putBoolean(ARGS_MODIFY_MODE, isModifyMode);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle arguments = getArguments();
        if (null == arguments) {
            //for check save state
            arguments = savedInstanceState;
        }
        if (null != arguments) {
            UUID wordId = (UUID) arguments.getSerializable(ARGS_WORD_ID);
            mWord = Dictionary.getInstance().getWord(wordId);
            isModifyMode = arguments.getBoolean(ARGS_MODIFY_MODE);
        }

        if (null == mWord) {
            mWord = new Word(); // must not is null
        }

        mRequestImage = new ImageHelper.RequestImage(getActivity(), mWord, false) {
            @Override
            public void updateImage(byte[] data) {
                myUpdateImage(data);
            }
        };

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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (menu.hasVisibleItems()) {
            menu.clear(); // keep is empty
            Utils.logDebug(TAG, "clean menu items");
        }
        inflater.inflate(R.menu.tool_word_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case Utils.ID_MENU_SAVE:
                String errorInfo = null;
                if (mWord.getWordType() != WordJsonDefine.WordType.VERIFIED) {
                    errorInfo = getString(R.string.not_save_info);
                } else {
                    errorInfo = getString(R.string.not_save_info_with_verified);
                }
                if (!mWord.checkWordValidity()) {
                    Toast.makeText(getActivity(), errorInfo, Toast.LENGTH_SHORT)
                            .show();
                    break;
                }
                Dictionary.getInstance().addWord(mWord);
                saveToFileInBackground();
                if (isModifyMode) {
                    buildModeChange(false);
                }
                break;
            case Utils.ID_MENU_BUILD:
                buildModeChange(!isModifyMode);
                break;
            case Utils.ID_MENU_SELECT_NAMES:
                if (null == mFragmentManager || null == mSelectNamesHelper) {
                    Utils.logDebug(TAG, "warning : mWord , mFragmentManager or mSelectNamesHelper is null");
                    break;
                }
                CommonInputFragment dialog = CommonInputFragment.newSelectInstance(mSelectNamesHelper.getTitle(),
                        mSelectNamesHelper.getSpinnerAllItem(),
                        mSelectNamesHelper.getSelectItem(),
                        mSelectNamesHelper.getId());
                dialog.show(mFragmentManager, COMMON_INPUT_DIALOG_TAG);
                break;
            case Utils.ID_MENU_DELETE:
                Utils.logDebug(TAG, "remove word : " + mWord.getId());
                if (Dictionary.getInstance().removeWord(mWord)) {
                    saveToFileInBackground();
                }
                FragmentActivity activity = getActivity();
                if (activity != null) {
                    activity.onBackPressed(); // destroy self
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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

        initSelectNamesMenuHelper();

        initImageHint(getActivity());

        try {
            updateViewList();
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
        setAllOnClick();
        setAllOnLongClick();
        setAllResultListener();

        if (!isModifyMode) {
            setAllClickable(false);
        }

        //set show data
        updateAllUI();

        return layout;
    }

    private void setAllOnLongClick() {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            Utils.outLog(TAG, "setAllOnLongClick fail : can't get activity");
            return;
        }

        for (CommonInputFragment.InputViewType viewType : mViewList) {
            View view = viewType.getView();
            view.setOnLongClickListener((v -> {
                String text = null;
                if (view instanceof TextView) {
                    text = ((TextView) view).getText().toString();
                } else { // only image not is TextView on here
                    text = mWord.getPictureLink();
                }
                if (Utils.isEmptyString(text)) { //not need copy empty string
                    return false;
                }
                Utils.putSystemClipboard(activity, text);
                Toast.makeText(activity, R.string.copy_success, Toast.LENGTH_SHORT).show();
                return true;
            }));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateTitle();
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

    private void updateViewList() throws Exception {
        if (null == mWord) { // There has to be one Word
            Utils.outLog(TAG, "initViewList() error : word is null");
            throw new Exception("mWord is null");
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
        cleanSelectNamesListener();
    }

    private void buildModeChange(boolean enable) {
        isModifyMode = enable;
        setAllClickable(isModifyMode);
        updateAllUI();
    }

    private void saveToFileInBackground() {
        GlobalHandler.getInstance().post2BackgroundHandler(new Runnable() {
            @Override
            public void run() {
                Utils.logDebug(TAG, "save word to dictionary.json file..");
                FileHelper.saveFile(Dictionary.getInstance().toJsonString().getBytes(StandardCharsets.UTF_8),
                        Dictionary.DICTIONARY_FILE_NAME,
                        getActivity(),
                        FileHelper.SaveDir.JSON_DATE);
            }
        });
    }

    private void updateTitle() {
        FragmentActivity activity = getActivity();
        if (null == activity) {
            Utils.outLog(TAG, "updateTitle fail : can't get activity");
            return;
        }
        if (isModifyMode) {
            activity.setTitle(getString(R.string.modifying_word));
        } else if (!Utils.isEmptyString(mWord.getContent())) {
            activity.setTitle(mWord.getContent());
        } else {
            activity.setTitle(R.string.app_title);
        }
    }

    // only need call once on created
    private void initSelectNamesMenuHelper() {
        List<String> wordsNameList = Dictionary.getInstance().getWordsNameList();
        if (null == wordsNameList) {
            Utils.logDebug(TAG, "initSelectNamesMenuHelper fail : can't getWordsNameList from Dictionary");
            return;
        }
        if (null == mFragmentManager) {
            Utils.logDebug(TAG, "initSelectNamesMenuHelper fail  : mFragmentManager is null");
            return;
        }
        mSelectNamesHelper = new CommonInputFragment.InputViewType(mTypeText, CommonInputFragment.InputType.SPINNER)
                .setSpinnerInfo(getString(R.string.select_names), wordsNameList.toArray(new String[0]), mWord.getNameListIndex())
                .setSaveDataCallback(bundle -> {
                    if (null == bundle) {
                        Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
                        return;
                    }
                    int index = bundle.getInt(CommonInputFragment.RESULT_SELECT_KEY);
                    mWord.setNameListIndex(index);
                });
        mFragmentManager.setFragmentResultListener(mSelectNamesHelper.getId(),
                this,
                (requestKey, result) -> {
                    Utils.logDebug(TAG, "requestKey : " + requestKey);
                    String string = result.getString(CommonInputFragment.RESULT_TEXT_KEY);
                    Date date = (Date) result.getSerializable(CommonInputFragment.RESULT_DATE_KEY);
                    int index = result.getInt(CommonInputFragment.RESULT_SELECT_KEY);
                    ArrayList<String> arrayList = result.getStringArrayList(CommonInputFragment.RESULT_INPUT_ARRAY_CONTENT);
                    Utils.logDebug(TAG, "index :" + index);

                    mSelectNamesHelper.updateCache(string, date, index, arrayList);
                    CommonInputFragment.InputViewType.SaveDataCallback callback = mSelectNamesHelper.getSaveDataCallback();
                    if (null != callback) {
                        callback.saveData(result);
                    }
                });
    }

    private void cleanSelectNamesListener() {
        if (null == mFragmentManager || null == mSelectNamesHelper) {
            return;
        }
        mFragmentManager.clearFragmentResultListener(mSelectNamesHelper.getId());
    }

    // only need call once on created
    private void initImageHint(FragmentActivity activity) {
        if (null == activity) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return;
        }
        if (Utils.isZhEnv(activity)) {
            Resources res = activity.getResources();
            Drawable myImage = ResourcesCompat.getDrawable(res, R.drawable.ic_set_image_zh, null);
            mImageView.setImageDrawable(myImage);
        }
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
        setViewVisibility(view, isGone);
        view.setText(value);
    }


    /**
     * set view is gone
     *
     * @param view   view
     * @param isGone is gone
     */
    private void setViewVisibility(View view, boolean isGone) {
        if (null == view) {
            Utils.outLog(TAG, Utils.OutLogType.PARAMETER_NULL_WARNING);
            return;
        }
        if (isGone) {
            view.setVisibility(View.GONE);
        }
    }

    private void updateWordListUI() {
        if (null != mCallback) {
            mCallback.onWordUpdated(mWord);
        }
    }

    private void updateAllUI() {
        if (null == mWord) {
            Utils.logDebug(TAG, "update UI fail : mWord is null");
            return;
        }
        Utils.logDebug(TAG, "call updateAllUI(), is Modify Mode : " + isModifyMode);

        setTextShowUI(mContentText, mWord.getContent(), false);

        String formatSynonym = mWord.getFormatSynonym(null);
        String synonym = getString(R.string.format_word_synonym, formatSynonym);
        setTextShowUI(mSynonymText, synonym, Utils.isEmptyString(formatSynonym));

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
        setTextShowUI(mVerifiedDateText, getString(R.string.verified_date) + verifiedDate,
                !verifiedInfo.getVerifiedTimeValid());

        calendar.setTime(verifiedInfo.getEarliestTime());
        String earliestDate = getString(R.string.format_word_data,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        setTextShowUI(mEarliestDateText, getString(R.string.earliest_date) + earliestDate,
                !verifiedInfo.getEarliestTimeValid());

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

        setViewVisibility(mImageView, Utils.isEmptyString(mWord.getPictureLink()));
        setViewVisibility(mGifView, Utils.isEmptyString(mWord.getPictureLink()));
        byte[] imageData = Dictionary.getInstance().getImageData(mWord.getId());
        if (imageData != null) {
            myUpdateImage(imageData);
        } else {
            requestNewImage();
        }

        if (isModifyMode) {
            for (CommonInputFragment.InputViewType inputViewType : mViewList) {
                View view = inputViewType.getView();
                if (view instanceof TextView) {
                    String text = ((TextView) view).getText().toString();
                    if (Utils.isEmptyString(text)) {
                        text = inputViewType.getHasContent();
                    }
                    ((TextView) view).setText(Utils.isEmptyString(text) ? inputViewType.getTitle() : text);
                    view.setVisibility(View.VISIBLE);
                } else if (mImageView != null && mImageView.getVisibility() == View.GONE) {
                    mImageView.setVisibility(View.VISIBLE); // for set link
                }
            }
        }

        updateTitle();
    }

    private void requestNewImage() {
        Utils.logDebug(TAG, "requestNewImage");
        GlobalHandler.getInstance().post2BackgroundHandler(mRequestImage);
    }

    public static WordFragment newInstance(@Nullable Word word, boolean isAddWord) {
        Bundle args = new Bundle();
        args.putSerializable(ARGS_WORD_ID, null == word ? null : word.getId());
        args.putBoolean(ARGS_MODIFY_MODE, isAddWord);

        WordFragment wordFragment = new WordFragment();
        wordFragment.setArguments(args);
        return wordFragment;
    }
}
