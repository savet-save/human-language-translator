package com.example.humanlanguagetranslator.fragment;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = "EXTRA_DATE";

    private static final String TAG = "DatePickerFragment";
    private static final String ARG_DATE = "ARG_DATE";

    private DatePicker mDatePicker;

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
        View datePickerView = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date_picker, null);
        DatePickerInit(datePickerView);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.date_picker_title)
                .setView(datePickerView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date =  new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, date);
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

    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
