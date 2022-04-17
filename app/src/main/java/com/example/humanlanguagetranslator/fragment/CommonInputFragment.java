package com.example.humanlanguagetranslator.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.data.WordJsonDefine;
import com.example.humanlanguagetranslator.util.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CommonInputFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private static final String TAG = "CommonInputFragment";
    private static final String DEFAULT_TITLE = "title";

    //arguments
    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_INPUT_TYPE = "ARG_INPUT_TYPE";
    private static final String ARG_HINT = "ARG_HINT";
    private static final String ARG_HAS_CONTENT = "ARG_HAS_CONTENT";
    private static final String ARG_DATE = "ARG_DATE";
    private static final String ARG_REQUEST_ID = "ARG_REQUEST_ID";
    private static final String ARG_SELECT_ALL_ITEMS = "ARG_SELECT_ALL_ITEMS";
    private static final String ARG_SELECT_ITEM = "ARG_SELECT_ITEM";

    //result
    public static final String RESULT_DATE_KEY = "RESULT_DATE_KEY";
    public static final String RESULT_TEXT_KEY = "RESULT_TEXT_KEY";
    public static final String RESULT_SELECT_KEY = "RESULT_SELECT_KEY";

    private DatePicker mDatePicker;
    private EditText mTextInput;
    private FragmentActivity mFragmentActivity;
    private InputType mInputType;
    private String mRequestId;
    private Spinner mSpinner;


    @NonNull
    public static CommonInputFragment newTextInstance(String title, String hint, String hasContent, String requestId) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_HINT, hint);
        args.putSerializable(ARG_INPUT_TYPE, InputType.TEXT_INPUT);
        args.putString(ARG_REQUEST_ID, requestId);
        args.putString(ARG_HAS_CONTENT, hasContent);

        CommonInputFragment fragment = new CommonInputFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CommonInputFragment newDatePickerInstance(String title, Date date, String requestId) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putSerializable(ARG_DATE, date);
        args.putSerializable(ARG_INPUT_TYPE, InputType.DATE_PICKER);
        args.putString(ARG_REQUEST_ID, requestId);

        CommonInputFragment fragment = new CommonInputFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CommonInputFragment newSelectInstance(String title, String[] items, int selectItem,String requestId) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putStringArray(ARG_SELECT_ALL_ITEMS, items);
        args.putInt(ARG_SELECT_ITEM ,selectItem);
        args.putSerializable(ARG_INPUT_TYPE, InputType.SPINNER);
        args.putString(ARG_REQUEST_ID, requestId);

        CommonInputFragment fragment = new CommonInputFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mFragmentActivity = requireActivity();
        Bundle arguments = getArguments();
        String title = DEFAULT_TITLE;
        if (null != arguments) {
            title = arguments.getString(ARG_TITLE);
        }
        View view = LayoutInflater.from(mFragmentActivity)
                .inflate(R.layout.dialog_common_input, null);
        initView(view, arguments);
        return new AlertDialog.Builder(mFragmentActivity)
                .setCancelable(true)
                .setTitle(title)
                .setView(view)
                .setIcon(R.drawable.ic_input_dailog)
                .setPositiveButton(android.R.string.ok, this)
                .create();
    }

    private void initView(View view, Bundle arguments) {
        if (null != arguments) {
            mInputType = (InputType) arguments.getSerializable(ARG_INPUT_TYPE);
            mInputType = (mInputType == null ? InputType.TEXT_INPUT : mInputType); // not equal null
            mRequestId = arguments.getString(ARG_REQUEST_ID); // nullable

        }
        mDatePicker = view.findViewById(R.id.date_picker_dialog);
        mTextInput = view.findViewById(R.id.text_input_dialog);
        mSpinner = view.findViewById(R.id.select_dialog);
        switch (mInputType) {
            case DATE_PICKER:
                mDatePicker.setVisibility(View.VISIBLE);
                mTextInput.setVisibility(View.GONE);
                mSpinner.setVisibility(View.GONE);
                datePickerInit(arguments);
                break;
            case SPINNER:
                mSpinner.setVisibility(View.VISIBLE);
                mTextInput.setVisibility(View.GONE);
                mDatePicker.setVisibility(View.GONE);
                spinnerInit(arguments);
                break;
            case TEXT_INPUT:
            default:
                mTextInput.setVisibility(View.VISIBLE);
                mDatePicker.setVisibility(View.GONE);
                mSpinner.setVisibility(View.GONE);
                textInputInit(arguments);
                break;
        }
    }

    private void spinnerInit(Bundle arguments) {
        String[] items = null;
        int selection = 0;
        if (arguments != null) {
            items =  arguments.getStringArray(ARG_SELECT_ALL_ITEMS);
            selection = arguments.getInt(ARG_SELECT_ITEM);
        }
        MySpinnerAdapter spinnerAdapter = new MySpinnerAdapter(getActivity());
        if (items != null) {
            spinnerAdapter.setShowData(items);
            mSpinner.setSelection(selection);
        }
        mSpinner.setAdapter(spinnerAdapter);
    }

    private void textInputInit(Bundle arguments) {
        String hint = null;
        String hasContent = null;
        if (arguments != null) {
            hint = arguments.getString(ARG_HINT);
            hasContent = arguments.getString(ARG_HAS_CONTENT);
        }
        if (!Utils.isEmptyString(hint)) {
            Utils.logDebug(TAG, "args has hint : " + hint);
            mTextInput.setHint(hint);
        }
        if (!Utils.isEmptyString(hasContent)) {
            Utils.logDebug(TAG, "args has content : " + hasContent.hashCode());
            mTextInput.setText(hasContent);
        }
    }

    private void datePickerInit(Bundle arguments) {
        Date date = null;
        if (arguments != null) {
            date = (Date) arguments.getSerializable(ARG_DATE);
        }
        if (date != null) {
            Utils.logDebug(TAG, "args has date data");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            mDatePicker.init(year, month, day, null);
        }
    }

    private void sendResult() {
        if (Utils.isEmptyString(mRequestId)) {
            Utils.logDebug(TAG, "waring : request id is null, not return result!");
            return;
        }
        Bundle result = getResultBundle();
        mFragmentActivity.getSupportFragmentManager().setFragmentResult(mRequestId, result);
    }

    private Bundle getResultBundle() {
        Bundle result = new Bundle();
        switch (mInputType) {
            case DATE_PICKER:
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();
                Date date =  new GregorianCalendar(year, month, day).getTime();
                result.putSerializable(RESULT_DATE_KEY, date);
                break;
            case SPINNER:
                int selected = mSpinner.getSelectedItemPosition();
                result.putInt(RESULT_SELECT_KEY, selected);
                break;
            case TEXT_INPUT:
            default:
                result.putString(RESULT_TEXT_KEY, mTextInput.getText().toString());
                break;
        }
        return result;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == Dialog.BUTTON_POSITIVE) {
            sendResult();
            Utils.logDebug(TAG, " select : " + mSpinner.getSelectedItemPosition());
        }
    }

    public enum InputType {
        TEXT_INPUT,
        DATE_PICKER,
        SPINNER
    }

    public static class MySpinnerAdapter extends BaseAdapter {
        private static final String TAG = "MySpinnerAdapter";
        private final Context mContext;
        private String[] mShowData;

        public MySpinnerAdapter(Context context) {
            mContext = context;
        }

        public void setShowData(String[] showData) {
            mShowData = showData;
        }

        @Override
        public int getCount() {
            return mShowData.length;
        }

        @Override
        public Object getItem(int position) {
            return mShowData[position % mShowData.length];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == mContext) {
                Utils.outLog(TAG, "can't init View : Context is null");
                return null;
            }
            // 针对convertView做一个简单的优化
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.list_itme_spinner, null);
            }
            TextView name = convertView.findViewById(R.id.item_show_text);
            name.setText(mShowData[position % mShowData.length]);
            return convertView;
        }
    }

    /**
     * for enum InputType
     */
    public static class InputViewType {
        private static final String TAG = "InputTypeView";

        private final View mView;
        private final InputType mInputType;
        private String mTitle;
        private String mHint;
        private String mHasContent;
        private Date mDate;
        private int mSelectItem;
        private String[] mSpinnerAllItem;

        public InputViewType(View view, InputType inputType) {
            mView = view;
            mInputType = inputType;
            mSelectItem = 0;
        }

        public View getView() {
            return mView;
        }

        public InputType getInputType() {
            return mInputType;
        }

        public InputViewType setTextInputInfo(String title) {
            mTitle = title;
            mHint = WordJsonDefine.Explain.getExplainValue(title);
            if (mView instanceof TextView) {
                mHasContent = ((TextView) mView).getText().toString();
            }
            return this;
        }

        public InputViewType setDatePickerInfo(String title, Date date) {
            mTitle = title;
            mDate = date;
            return this;
        }

        public InputViewType setSpinnerInfo(String title, String[] items, int showItem) {
            mTitle = title;
            mSpinnerAllItem = items;
            mSelectItem = showItem;
            return this;
        }

        @Nullable
        public String getTitle() {
            return mTitle;
        }

        @Nullable
        public String getHint() {
            return mHint;
        }

        @Nullable
        public String getHasContent() {
            return mHasContent;
        }

        @Nullable
        public Date getDate() {
            return mDate;
        }

        public String[] getSpinnerAllItem() {
            return mSpinnerAllItem;
        }

        public int getSelectItem() {
            return mSelectItem;
        }
    }
}