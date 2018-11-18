package com.example.words;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.words.wordcontract.Words;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class WordDetailFragment extends Fragment {
    private static final String TAG="myTag";
    public static final String ARG_ID = "id";

    private String mID;//单词主键
    private OnFragmentInteractionListener mListener;//Fragment所在的Activity


    public static WordDetailFragment newInstance(String wordID) {
        WordDetailFragment fragment = new WordDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID, wordID);
        fragment.setArguments(args);
        return fragment;
    }

    public WordDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mID = getArguments().getString(ARG_ID);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_word_detail, container, false);
        Log.v(TAG,mID);

        WordsDB wordsDB=WordsDB.getWordsDB();

        if(wordsDB!=null && mID!=null){
            TextView textViewWord=(TextView)view.findViewById(R.id.word);
            TextView textViewWordMeaning=(TextView)view.findViewById(R.id.wordmeaning);
            TextView textViewWordSample=(TextView)view.findViewById(R.id.wordsample);

            Words.WordDescription item=wordsDB.getSingleWord(mID);
            if(item!=null){
                textViewWord.setText(item.word);
                textViewWordMeaning.setText(item.meaning);
                textViewWordSample.setText(item.sample);
            }
            else{
                textViewWord.setText("");
                textViewWordMeaning.setText("");
                textViewWordSample.setText("");
            }

        }
        return view;
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onWordDetailClick(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (OnFragmentInteractionListener) getActivity();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {

        public void onWordDetailClick(Uri uri);

    }

}