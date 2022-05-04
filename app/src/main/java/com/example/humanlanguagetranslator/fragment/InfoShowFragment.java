package com.example.humanlanguagetranslator.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.util.Utils;

public class InfoShowFragment extends DialogFragment {

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_CONTENT = "ARG_CONTENT";
    private static final String ARG_COPY_CONTENT = "ARG_COPY_CONTENT";

    private static final String DEFAULT_TITLE = "info";
    private static final String DEFAULT_CONTENT = "content";
    private static final int MAX_TOAST_TEXT_HEAD_LENGTH = 6;

    @NonNull
    public static InfoShowFragment newInstance(String title, String content, String copyContent) {

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);
        args.putString(ARG_COPY_CONTENT, copyContent);

        InfoShowFragment fragment = new InfoShowFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FragmentActivity fragmentActivity = requireActivity();
        Bundle arguments = getArguments();
        String title = DEFAULT_TITLE;
        String content = DEFAULT_CONTENT;
        String copyContent = null;
        if (null != arguments) {
            title = arguments.getString(ARG_TITLE);
            content = arguments.getString(ARG_CONTENT);
            copyContent = arguments.getString(ARG_COPY_CONTENT);
        }
        View view = LayoutInflater.from(fragmentActivity)
                .inflate(R.layout.dialog_info_show, null);
        infoShowInit(view, content, copyContent);
        return new AlertDialog.Builder(fragmentActivity)
                .setCancelable(true)
                .setTitle(title)
                .setView(view)
                .setIcon(R.drawable.ic_dialog_info)
                .create();
    }

    private void infoShowInit(View view, String content, String copyContent) {
        TextView textView = view.findViewById(R.id.info_show_content_text);
        textView.setText(content);

        textView.setOnLongClickListener((v) -> {
            FragmentActivity activity = getActivity();

            if (copyContent != null && activity != null) {
                Utils.putSystemClipboard(activity, copyContent);
                Toast.makeText(activity, getToastText(copyContent), Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

    }

    private String getToastText(String text) {
        if (text.length() > MAX_TOAST_TEXT_HEAD_LENGTH) {
            text = text.substring(0, MAX_TOAST_TEXT_HEAD_LENGTH) + "...";
        }
        return getString(R.string.content_copy, text);
    }
}
