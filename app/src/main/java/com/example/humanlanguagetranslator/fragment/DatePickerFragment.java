package com.example.humanlanguagetranslator.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    public static final String DATE_KEY = "EXTRA_DATE";
    public static final String REQUEST_DATE_KEY = "Get Date";

    private static final String TAG = "DatePickerFragment";
    private static final String ARG_DATE = "ARG_DATE";

    private DatePicker mDatePicker;
    private FragmentActivity mFragmentActivity;

    public static DatePickerFragment newInstance(Date date) {
        DatePickerFragment fragment = new DatePickerFragment();

        if (null != date) {
            Bundle args = new Bundle();
            args.putSerializable(ARG_DATE, date);
            fragment.setArguments(args);
        }

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mFragmentActivity = requireActivity();
        View datePickerView = LayoutInflater.from(mFragmentActivity)
                .inflate(R.layout.dialog_date_picker, null);
        DatePickerInit(datePickerView);

        return new AlertDialog.Builder(mFragmentActivity)
                .setTitle(R.string.date_picker_title)
                .setView(datePickerView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date =  new GregorianCalendar(year, month, day).getTime();
                        sendResult(date);
                    }
                }).create();
    }

    private void DatePickerInit(View root) {
        mDatePicker = (DatePicker)root.findViewById(R.id.date_picker_dialog);
        Bundle arguments = getArguments();
        Date date = null;
        if (null != arguments) {
            date = (Date)arguments.getSerializable(ARG_DATE);
        }
        if (null != date) {
            Utils.logDebug(TAG, "args has date data");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            mDatePicker.init(year, month, day, null);
        }
    }

    private void sendResult(Date date) {
        Bundle result = new Bundle();
        result.putSerializable(DATE_KEY, date);
        mFragmentActivity.getSupportFragmentManager().setFragmentResult(REQUEST_DATE_KEY, result);
    }
}
