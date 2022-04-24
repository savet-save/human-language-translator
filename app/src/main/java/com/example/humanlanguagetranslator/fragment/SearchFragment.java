package com.example.humanlanguagetranslator.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.activity.WordListActivity;
import com.example.humanlanguagetranslator.activity.WordPagerActivity;
import com.example.humanlanguagetranslator.data.Dictionary;
import com.example.humanlanguagetranslator.data.SearchHistory;
import com.example.humanlanguagetranslator.data.Word;
import com.example.humanlanguagetranslator.util.Utils;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.List;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private RecyclerView mHistoryRecyclerView;
    private HistoryAdapter mHistoryAdapter;
    private Button mCancelButton;
    private SearchView mSearchView;
    private boolean needHoldFlag = true;
    private Button mCleanButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_serach_word, container, false);

        mHistoryRecyclerView = (RecyclerView) view.findViewById(R.id.history_info_layout);
        mHistoryRecyclerView.setLayoutManager(getLayoutManager());

        mCancelButton = (Button) view.findViewById(R.id.search_cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchView = view.findViewById(R.id.search_word_view);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mCleanButton = (Button) view.findViewById(R.id.clean_history_button);
        mCleanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchHistory.getInstance(getActivity()).cleanHistory();
                updateUI();
            }
        });

        updateUI();

        return view;
    }

    private void startSearch(String query) {
        List<Word> filterResult = Dictionary.getInstance().getFilterResult(query);
        for (Word word : filterResult) {
            Utils.logDebug(TAG, "search result : " + word.getContent());
        }
        Intent intent = null;
        if (filterResult.size() == 1) {
            intent = WordPagerActivity.newIntent(getActivity(), filterResult.get(0).getId(), false);
            Utils.logDebug(TAG, "start word pager");
        } else {
            intent = WordListActivity.newIntent(getActivity(), filterResult);
            Utils.logDebug(TAG, "start word list");
        }
        SearchHistory.getInstance(getActivity()).putHistory(query);
        startActivity(intent);
        needHoldFlag = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (!needHoldFlag) {
            // not need hold
            finish();
        }
    }

    /**
     * finish this activity
     */
    private void finish() {
        FragmentActivity activity = getActivity();
        if (null != activity) {
            activity.finish();
        } else {
            Utils.outLog(TAG, "can't get activity for SearchFragment");
        }
    }

    /**
     * Update the current activity display list item
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateUI() {
        if (null == mHistoryAdapter) {
            mHistoryAdapter = new HistoryAdapter(SearchHistory.getInstance(getActivity()).getHistory());
            mHistoryRecyclerView.setAdapter(mHistoryAdapter);
        } else {
            //TODO need optimize
            mHistoryAdapter.notifyDataSetChanged();
        }
    }

    private FlexboxLayoutManager getLayoutManager() {
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getActivity());
        layoutManager.setFlexDirection(FlexDirection.ROW);//从左往右, 从上到下
        layoutManager.setFlexWrap(FlexWrap.WRAP);//自动换行
        layoutManager.setJustifyContent(JustifyContent.FLEX_START);//左对齐
        return layoutManager;
    }

    private class HistoryHolder extends RecyclerView.ViewHolder {

        private final Button mButton;

        public HistoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_history, parent, false));
            mButton = itemView.findViewById(R.id.history_button);
        }

        public void bind(String history) {
            mButton.setText(history);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startSearch(history);
                }
            });
        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter<SearchFragment.HistoryHolder> {
        private List<String> mHistory;

        public HistoryAdapter(List<String> history) {
            mHistory = history;
        }

        @NonNull
        @Override
        public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new HistoryHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
            String history = mHistory.get(position);
            Utils.logDebug(TAG, "is " + position);
            holder.bind(history);
        }

        @Override
        public int getItemCount() {
            return mHistory.size();
        }
    }
}
