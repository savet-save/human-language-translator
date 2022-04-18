package com.example.humanlanguagetranslator.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.util.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ArrayInputHelper {
    public static class ItemHolder extends RecyclerView.ViewHolder{

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

        public String getText() {
            return mEditText.getText().toString();
        }
    }

    public static class ItemAdapter extends RecyclerView.Adapter<ArrayInputHelper.ItemHolder> {

        private final Context mContext;
        private final ArrayList<String> mArrayList;
        private final List<ItemHolder> mHoldList = new LinkedList<>();

        public ItemAdapter(Context context, ArrayList<String> arrayList) {
            mContext = context;
            mArrayList = arrayList;
        }

        public ArrayList<String> getInputArray() {
            ArrayList<String> contentList = new ArrayList<>();
            for (ItemHolder holder : mHoldList) {
                contentList.add(holder.getText());
            }
            return contentList;
        }

        public void addItem(String content) {
            mArrayList.add(content);
            notifyItemRangeChanged(0, mArrayList.size());
        }

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            ItemHolder itemHolder = new ItemHolder(layoutInflater, parent);
            itemHolder.setIsRecyclable(false);
            mHoldList.add(itemHolder);
            return itemHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
            holder.bind(String.valueOf(position + 1), mArrayList.get(position));
        }

        @Override
        public int getItemCount() {
            return mArrayList.size();
        }
    }
}
