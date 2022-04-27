package com.example.humanlanguagetranslator.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.data.WordJsonDefine;
import com.example.humanlanguagetranslator.helper.ArrayInputHelper;
import com.example.humanlanguagetranslator.util.Utils;

import java.util.ArrayList;
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
    public static final String ARG_ARRAY_LIST_CONTENT = "ARG_ARRAY_LIST_CONTENT";

    //result
    /**
     * return type : Date
     */
    public static final String RESULT_DATE_KEY = "RESULT_DATE_KEY";
    /**
     * return type : String
     */
    public static final String RESULT_TEXT_KEY = "RESULT_TEXT_KEY";
    /**
     * return type : int
     */
    public static final String RESULT_SELECT_KEY = "RESULT_SELECT_KEY";
    /**
     * return type : ArrayList&lt;String&gt;
     */
    public static final String RESULT_INPUT_ARRAY_CONTENT = "RESULT_INPUT_ARRAY_CONTENT";


    private DatePicker mDatePicker;
    private EditText mTextInput;
    private FragmentActivity mFragmentActivity;
    private InputType mInputType;
    private String mRequestId;
    private Spinner mSpinner;
    private View mArrayInputLayout;
    private RecyclerView mArrayInputRecycler;

    @NonNull
    private static CommonInputFragment getCommonInputFragmentWithArguments(Bundle args) {
        CommonInputFragment fragment = new CommonInputFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    public static CommonInputFragment newTextInstance(String title, String hint, String hasContent, String requestId) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_HINT, hint);
        args.putSerializable(ARG_INPUT_TYPE, InputType.TEXT_INPUT);
        args.putString(ARG_REQUEST_ID, requestId);
        args.putString(ARG_HAS_CONTENT, hasContent);

        return getCommonInputFragmentWithArguments(args);
    }

    public static CommonInputFragment newDatePickerInstance(String title, Date date, String requestId) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putSerializable(ARG_DATE, date);
        args.putSerializable(ARG_INPUT_TYPE, InputType.DATE_PICKER);
        args.putString(ARG_REQUEST_ID, requestId);

        return getCommonInputFragmentWithArguments(args);
    }

    public static CommonInputFragment newSelectInstance(String title, String[] items, int selectItem, String requestId) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putStringArray(ARG_SELECT_ALL_ITEMS, items);
        args.putInt(ARG_SELECT_ITEM, selectItem);
        args.putSerializable(ARG_INPUT_TYPE, InputType.SPINNER);
        args.putString(ARG_REQUEST_ID, requestId);

        return getCommonInputFragmentWithArguments(args);
    }

    public static CommonInputFragment newArrayTextInputInstance(String title, ArrayList<String> content, String requestId) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putStringArrayList(ARG_ARRAY_LIST_CONTENT, content);
        args.putSerializable(ARG_INPUT_TYPE, InputType.ARRAY_TEXT_INPUT);
        args.putString(ARG_REQUEST_ID, requestId);

        return getCommonInputFragmentWithArguments(args);
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
        View view = initView(arguments);
        return new AlertDialog.Builder(mFragmentActivity)
                .setCancelable(true)
                .setTitle(title)
                .setView(view)
                .setIcon(R.drawable.ic_input_dailog)
                .setPositiveButton(android.R.string.ok, this)
                .create();
    }

    private View initView(Bundle arguments) {
        View view = null;
        if (null != arguments) {
            mInputType = (InputType) arguments.getSerializable(ARG_INPUT_TYPE);
            mInputType = (mInputType == null ? InputType.TEXT_INPUT : mInputType); // not equal null
            mRequestId = arguments.getString(ARG_REQUEST_ID); // nullable

        }
        Utils.logDebug(TAG, "common dialog type : " + mInputType);
        view = LayoutInflater.from(mFragmentActivity)
                .inflate(R.layout.dialog_common_input, null);
        mDatePicker = view.findViewById(R.id.date_picker_dialog);
        mTextInput = view.findViewById(R.id.text_input_dialog);
        mSpinner = view.findViewById(R.id.select_dialog);
        mArrayInputLayout = view.findViewById(R.id.array_text_input_dialog);
        switch (mInputType) {
            case DATE_PICKER:
                mDatePicker.setVisibility(View.VISIBLE);
                datePickerInit(arguments);
                break;
            case SPINNER:
                mSpinner.setVisibility(View.VISIBLE);
                spinnerInit(arguments);
                break;
            case ARRAY_TEXT_INPUT:
                mArrayInputLayout.setVisibility(View.VISIBLE);
                arrayInputInit(arguments);
                break;
            case TEXT_INPUT:
            default:
                mTextInput.setVisibility(View.VISIBLE);
                textInputInit(arguments);
                break;
        }
        return view;
    }

    private void arrayInputInit(Bundle arguments) {
        ArrayList<String> arrayList = null;
        if (null != arguments) {
            arrayList = arguments.getStringArrayList(ARG_ARRAY_LIST_CONTENT);
            Utils.removeEmptyItem(arrayList); // keep input clean
        }
        if (null == arrayList) {
            arrayList = new ArrayList<>(); // must has
        }
        if (arrayList.size() == 0) {
            arrayList.add(""); // At least one
        }


        ArrayInputHelper.ItemAdapter itemAdapter = new ArrayInputHelper.ItemAdapter(getActivity(), arrayList);
        mArrayInputRecycler = mArrayInputLayout.findViewById(R.id.array_text_input_recycler);

        FragmentActivity activity = getActivity();
        mArrayInputRecycler.setLayoutManager(new LinearLayoutManager(activity));
        mArrayInputRecycler.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);


        View footerView = LayoutInflater.from(getActivity())
                .inflate(R.layout.item_array_input_footer, mArrayInputRecycler, false);
        itemAdapter.setFooterView(footerView);
        Button addButton = footerView.findViewById(R.id.array_input_item_add);
        addButton.setOnClickListener(v -> {
            itemAdapter.addItem("");
        });

        mArrayInputRecycler.setAdapter(itemAdapter);
    }

    private void spinnerInit(Bundle arguments) {
        String[] items = null;
        int selection = 0;
        if (arguments != null) {
            items = arguments.getStringArray(ARG_SELECT_ALL_ITEMS);
            selection = arguments.getInt(ARG_SELECT_ITEM);
        }
        MySpinnerAdapter spinnerAdapter = new MySpinnerAdapter(getActivity());
        if (items != null) {
            spinnerAdapter.setShowData(items);
        }

        mSpinner.setAdapter(spinnerAdapter);
        if (items != null) {
            mSpinner.setSelection(selection); // must call after setAdapter
        }
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
                Date date = new GregorianCalendar(year, month, day).getTime();
                result.putSerializable(RESULT_DATE_KEY, date);
                break;
            case SPINNER:
                int selected = mSpinner.getSelectedItemPosition();
                result.putInt(RESULT_SELECT_KEY, selected);
                break;
            case ARRAY_TEXT_INPUT:
                ArrayInputHelper.ItemAdapter adapter = (ArrayInputHelper.ItemAdapter) mArrayInputRecycler.getAdapter();
                ArrayList<String> inputContent = null;
                if (null != adapter) {
                    inputContent = adapter.getInputArray();
                }
                if (null == inputContent) {
                    inputContent = new ArrayList<>();
                } else {
                    Utils.removeEmptyItem(inputContent); // keep output clean
                }
                result.putStringArrayList(RESULT_INPUT_ARRAY_CONTENT, inputContent);
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
        }
    }

    // if add item, need add to getResultBundle() and initView()
    public enum InputType {
        TEXT_INPUT,
        DATE_PICKER,
        SPINNER,
        ARRAY_TEXT_INPUT
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
        private final String mId;
        private String mTitle;
        private String mHint;
        private String mHasContent;
        private Date mDate;
        private int mSelectItem;
        private String[] mSpinnerAllItem;
        private SaveDataCallback mSaveDataCallback;
        private ArrayList<String> mContentList;

        public interface SaveDataCallback {
            void saveData(Bundle bundle);
        }

        public InputViewType(View view, InputType inputType) {
            mId = Utils.getNotRepeatId();
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

        public InputViewType setTextInputInfo(String title, String hasContent) {
            mTitle = title;
            mHint = WordJsonDefine.Explain.getExplainValue(title);
            mHasContent = hasContent;
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

        public InputViewType setArrayTextInputInfo(String title, ArrayList<String> contentList) {
            mTitle = title;
            mContentList = contentList;
            return this;
        }

        public InputViewType setSaveDataCallback(SaveDataCallback saveDataCallback) {
            mSaveDataCallback = saveDataCallback;
            return this;
        }

        public void updateCache(String hasContent, Date date, int showItem, ArrayList<String> contentList) {
            mHasContent = hasContent;
            mDate = date;
            mSelectItem = showItem;
            mContentList = contentList;
        }

        @Nullable
        public SaveDataCallback getSaveDataCallback() {
            return mSaveDataCallback;
        }

        public String getId() {
            return mId;
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

        @Nullable
        public ArrayList<String> getArrayContent() {
            return mContentList;
        }

        public String[] getSpinnerAllItem() {
            return mSpinnerAllItem;
        }

        public int getSelectItem() {
            Utils.logDebug(TAG, "mSelectItem : " + mSelectItem);
            return mSelectItem;
        }
    }
}