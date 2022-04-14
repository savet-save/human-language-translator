package com.example.humanlanguagetranslator.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.humanlanguagetranslator.R;

public class InfoShowFragment extends DialogFragment {

    private static final String ARG_TITLE = "ARG_TITLE";
    private static final String ARG_CONTENT = "ARG_CONTENT";

    private static final String DEFAULT_TITLE = "info";
    private static final String DEFAULT_CONTENT = "content";

    @NonNull
    public static InfoShowFragment newInstance(String title, String content) {

        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_CONTENT, content);

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
        if (null != arguments) {
             title = arguments.getString(ARG_TITLE);
             content = arguments.getString(ARG_CONTENT);
        }
        View view = LayoutInflater.from(fragmentActivity)
                .inflate(R.layout.fragment_info_show, null);
        infoShowInit(view, content);
        return new AlertDialog.Builder(fragmentActivity)
                .setCancelable(true)
                .setTitle(title)
                .setView(view)
                .setIcon(R.drawable.ic_dialog_info)
                .create();
    }

    private void infoShowInit(View view, String content) {
        TextView textView = view.findViewById(R.id.info_show_content_text);
        textView.setText(content);
    }
}
