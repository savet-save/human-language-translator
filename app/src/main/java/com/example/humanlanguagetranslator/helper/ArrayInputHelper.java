package com.example.humanlanguagetranslator.helper;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.util.Utils;

import java.util.ArrayList;

public class ArrayInputHelper {
    private static class ItemHolder extends RecyclerView.ViewHolder {

        private final TextView mTitleView;
        private final EditText mEditText;

        public ItemHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_array_input, parent, false));
            mTitleView = itemView.findViewById(R.id.array_input_item_title);
            mEditText = itemView.findViewById(R.id.array_input_item_edit);
        }

        public void bind(String title, String editContent) {
            mTitleView.setText(title);
            if (!Utils.isEmptyString(editContent)) {
                mEditText.setText(editContent);
            }
        }

        public void setOnTextChangedListener(TextWatcher textWatcher) {
            mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        mEditText.addTextChangedListener(textWatcher);
                    } else {
                        mEditText.removeTextChangedListener(textWatcher);
                    }
                }
            });
        }

        public String getText() {
            return mEditText.getText().toString();
        }
    }

    private static class FooterHolder extends RecyclerView.ViewHolder {
        public FooterHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final String TAG = "ItemAdapter";

        private final Context mContext;
        private final ArrayList<String> mDatas;
        private View mFooterView;

        private static final int ITEM_TYPE_NORMAL = 0;
        private static final int ITEM_TYPE_FOOTER = 2;

        public ItemAdapter(Context context, ArrayList<String> hasContent) {
            mContext = context;
            mDatas = hasContent;
            mFooterView = null;
        }

        public ArrayList<String> getInputArray() {
            return mDatas;
        }

        public void addItem(String content) {
            int oldCount = getItemCount();
            mDatas.add(content);
            notifyItemRangeChanged(oldCount, getItemCount());
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (ITEM_TYPE_FOOTER == viewType) {
                return new FooterHolder(mFooterView);
            }
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            return new ItemHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (isFooterType(position)) {
                return;
            }
            // ITEM_TYPE_NORMAL
            ((ItemHolder) holder).bind(String.valueOf(position + 1), mDatas.get(position));
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s != null) {
                        mDatas.set(holder.getAdapterPosition(), s.toString());
                    }
                }
            };
            ((ItemHolder) holder).setOnTextChangedListener(textWatcher);
        }

        private int getFooterPosition() {
            return (getItemCount() - 1);
        }

        private boolean isFooterType(int position) {
            return (getFooterPosition() == position);
        }

        public boolean hasFooterView() {
            return (null != mFooterView);
        }

        public void setFooterView(View footerView) {
            mFooterView = footerView;
            notifyItemChanged(getFooterPosition());
        }

        @Override
        public int getItemViewType(int position) {
            if (isFooterType(position)) {
                return ITEM_TYPE_FOOTER;
            }
            return ITEM_TYPE_NORMAL;
        }

        @Override
        public int getItemCount() {
            int footerNum = hasFooterView() ? 1 : 0;
            return mDatas.size() + footerNum;
        }
    }
}
