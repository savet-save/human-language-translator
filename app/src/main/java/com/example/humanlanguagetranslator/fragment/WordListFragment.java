package com.example.humanlanguagetranslator.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.humanlanguagetranslator.R;
import com.example.humanlanguagetranslator.activity.SearchActivity;
import com.example.humanlanguagetranslator.activity.WordListActivity;
import com.example.humanlanguagetranslator.data.Dictionary;
import com.example.humanlanguagetranslator.data.Word;
import com.example.humanlanguagetranslator.helper.ImageHelper;
import com.example.humanlanguagetranslator.util.GlobalHandler;
import com.example.humanlanguagetranslator.util.Utils;
import com.example.humanlanguagetranslator.view.GifView;

import java.util.ArrayList;
import java.util.List;


public class WordListFragment extends Fragment {

    private static final String TAG = "WordListFragment";
    private static final String ARG_WORD_LIST = "ARG_WORD_LIST";
    private static final String SHOW_INFO_TAG = "SHOW_INFO_TAG";

    private RecyclerView mWordRecyclerView;
    private WordAdapter mWordAdapter;
    private OnItemSelectedCallback mOnCallback;
    private Bundle mCreateArguments;
    private FragmentManager mFragmentManager;

    public interface OnItemSelectedCallback {
        void onItemSelected(Word word, boolean isAddMode);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnCallback = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnItemSelectedCallback) {
            mOnCallback = (OnItemSelectedCallback) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word_list, container, false);
        FragmentActivity activity = getActivity();
        if (null != activity) {
            mWordRecyclerView = (RecyclerView) view.findViewById(R.id.word_recycler_view);
            mWordRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
            //设置每一项之间的分割线
            mWordRecyclerView.addItemDecoration(new DividerItemDecoration(activity,
                    DividerItemDecoration.VERTICAL));

            //设置显示的内容
            setShowWord(mCreateArguments.getParcelableArrayList(ARG_WORD_LIST));

            mFragmentManager = activity.getSupportFragmentManager();
            updateUI();
        }
//        Utils.logDebug(TAG, "dictionary : \n" + Dictionary.getInstance().toJsonString());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void dealWithSelectedCallback(Word word, boolean isAddMode) {
        if (null != mOnCallback) {
            mOnCallback.onItemSelected(word, isAddMode);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case Utils.ID_ADD_WORD:
                Word word = new Word();
                Dictionary.getInstance().addWord(word);
                dealWithSelectedCallback(word, true);
                break;
            case Utils.ID_SEARCH_WORD:
                Intent intent = SearchActivity.newInstance(getActivity());
                startActivity(intent);
                break;
            case Utils.ID_MENU_ABOUT:
                Utils.logDebug(TAG, "about");
                InfoShowFragment.newInstance(getString(R.string.about_info), getString(R.string.about_info_content))
                        .show(mFragmentManager, SHOW_INFO_TAG);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mCreateArguments = getArguments();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.tool_word_list, menu);

        updateAppTitle(getString(R.string.app_title));
    }

    /**
     * Update the current activity display title
     */
    private void updateAppTitle(CharSequence title) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar supportActionBar = null;
        if (null != activity) {
            supportActionBar = activity.getSupportActionBar();
        }
        if (null != supportActionBar) {
            supportActionBar.setTitle(title);
        }
    }

    /**
     * <p> Update the current activity display list item </p>
     * <p> Warning : Must be call after mWordRecyclerView init </p>
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateUI() {
        if (null == mWordAdapter) {
            mWordAdapter = new WordAdapter(Dictionary.getInstance().getWords());
            mWordRecyclerView.setAdapter(mWordAdapter);
        } else {
            //TODO need optimize
            mWordAdapter.notifyDataSetChanged();
        }
    }

    /**
     * set list show content
     * <p> Warning : Must be call after mWordRecyclerView init </p>
     *
     * @param words show words, show default words if is null
     */
    public void setShowWord(@Nullable List<Word> words) {
        if (null == words) {
            return;
        }

        if (null == mWordAdapter) {
            mWordAdapter = new WordAdapter((words));
            mWordRecyclerView.setAdapter(mWordAdapter);
        } else {
            mWordAdapter.setWords(words);
            updateUI();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private class WordHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private static final String TAG = "WordHolder";
        private final ImageView mImageView;
        private final GifView mGifView;
        private final TextView mTitleView;
        private final TextView mTranslationView;
        private Word mWord;
        private ImageHelper.RequestImage mRequestImage;

        public WordHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_word, parent, false));
            itemView.setOnClickListener(this);
            mImageView = (ImageView) itemView.findViewById(R.id.word_item_image);
            mGifView = (GifView) itemView.findViewById(R.id.word_item_gif_image);
            mTitleView = (TextView) itemView.findViewById(R.id.word_item_title_text);
            mTranslationView = (TextView) itemView.findViewById(R.id.word_item_content_text);
        }

        public void bind(Word word, @Nullable byte[] imageData) {
            if (word == null) {
                Utils.outLog(TAG, "bind: word is null");
                return;
            }
            mWord = word;
            mRequestImage = new ImageHelper.RequestImage(getActivity(),
                    mWord.getPictureLink(),
                    mWord.getId(),
                    mWord.getContent(),
                    false) {
                @Override
                public void updateImage(byte[] data) {
                    myUpdateImage(data);
                }
            };
            mTitleView.setText(mWord.getContent() == null ? "is null?" : mWord.getContent());
            mTranslationView.setText(Utils.getFormatString(mWord.getTranslations()));
            if (imageData != null) {
                myUpdateImage(imageData);
            } else {
                requestNewImage();
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
                        mImageView.setVisibility(View.INVISIBLE); // because is ConstraintLayout
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
                        mImageView.setVisibility(View.VISIBLE);
                        mGifView.setVisibility(View.INVISIBLE);// because is ConstraintLayout
                    }
                });
            }
        }

        private void requestNewImage() {
            GlobalHandler.getInstance().post2BackgroundHandler(mRequestImage);
        }

        @Override
        public void onClick(View v) {
            dealWithSelectedCallback(mWord, false);
        }
    }

    private class WordAdapter extends RecyclerView.Adapter<WordHolder> {

        private List<Word> mWords;

        public List<Word> getWords() {
            return mWords;
        }

        public void setWords(List<Word> words) {
            mWords = words;
        }

        public WordAdapter(List<Word> words) {
            mWords = words;
        }

        @NonNull
        @Override
        public WordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new WordHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull WordHolder holder, int position) {
            Word word = mWords.get(position);
            holder.bind(word, Dictionary.getInstance().getImageData(word.getId()));
        }

        @Override
        public int getItemCount() {
            return mWords.size();
        }
    }

    public static WordListFragment newInstance(Intent intent) {
        Bundle bundle = new Bundle();
        WordListFragment wordListFragment = new WordListFragment();
        if (null != intent) {
            ArrayList<Parcelable> words = intent.getParcelableArrayListExtra(WordListActivity.EXTRA_WORD_LIST);
            bundle.putParcelableArrayList(ARG_WORD_LIST, words);
            wordListFragment.setArguments(bundle);
        }
        return wordListFragment;
    }
}
