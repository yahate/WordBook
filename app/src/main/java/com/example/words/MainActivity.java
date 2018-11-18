package com.example.words;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
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

public class MainActivity extends AppCompatActivity
        implements WordItemFragment.OnFragmentInteractionListener,
        WordDetailFragment.OnFragmentInteractionListener {

    private static final String TAG = "myTag";
    private String clientID="stx";
    private String clientSecret="123456";
    private String reply=null;

    private TextView textView=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InsertDialog();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WordsDB wordsDB=WordsDB.getWordsDB();
        if (wordsDB != null)
            wordsDB.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        switch (id) {
            case R.id.action_search:

                SearchDialog();
                return true;
            case R.id.action_insert:

                InsertDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void RefreshWordItemFragment() {
        WordItemFragment wordItemFragment = (WordItemFragment) getFragmentManager().findFragmentById(R.id.wordslist);
        wordItemFragment.refreshWordsList();
    }


    private void RefreshWordItemFragment(String strWord) {
        WordItemFragment wordItemFragment = (WordItemFragment) getFragmentManager().findFragmentById(R.id.wordslist);
        wordItemFragment.refreshWordsList(strWord);
    }


    private void InsertDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
        new AlertDialog.Builder(this)
                .setTitle("新增单词")//标题
                .setView(tableLayout)//设置视图

                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strWord = ((EditText) tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strMeaning = ((EditText) tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strSample = ((EditText) tableLayout.findViewById(R.id.txtSample)).getText().toString();


                        WordsDB wordsDB=WordsDB.getWordsDB();
                        wordsDB.Insert(strWord, strMeaning, strSample);

                        RefreshWordItemFragment();

                    }
                })

                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create()//创建对话框
                .show();//显示对话框

    }


    //删除对话框
    private void DeleteDialog(final String strId) {
        new AlertDialog.Builder(this).setTitle("删除单词")
                .setMessage("是否真的删除单词?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //既可以使用Sql语句删除，也可以使用使用delete方法删除
                WordsDB wordsDB=WordsDB.getWordsDB();
                wordsDB.DeleteUseSql(strId);

                //单词已经删除，更新显示列表
                RefreshWordItemFragment();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        }).create().show();
    }

    //修改对话框
    private void UpdateDialog(final String strId, final String strWord, final String strMeaning, final String strSample) {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.insert, null);
        ((EditText) tableLayout.findViewById(R.id.txtWord)).setText(strWord);
        ((EditText) tableLayout.findViewById(R.id.txtMeaning)).setText(strMeaning);
        ((EditText) tableLayout.findViewById(R.id.txtSample)).setText(strSample);
        new AlertDialog.Builder(this)
                .setTitle("修改单词")//标题
                .setView(tableLayout)//设置视图
                //确定按钮及其动作
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String strNewWord = ((EditText) tableLayout.findViewById(R.id.txtWord)).getText().toString();
                        String strNewMeaning = ((EditText) tableLayout.findViewById(R.id.txtMeaning)).getText().toString();
                        String strNewSample = ((EditText) tableLayout.findViewById(R.id.txtSample)).getText().toString();

                        //既可以使用Sql语句更新，也可以使用使用update方法更新
                        WordsDB wordsDB=WordsDB.getWordsDB();
                        wordsDB.UpdateUseSql(strId, strNewWord, strNewMeaning, strNewSample);

                        //单词已经更新，更新显示列表
                        RefreshWordItemFragment();
                    }
                })
                //取消按钮及其动作
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create()//创建对话框
                .show();//显示对话框
    }

    //查找对话框
    private void SearchDialog() {
        final TableLayout tableLayout = (TableLayout) getLayoutInflater().inflate(R.layout.searchterm, null);
        new AlertDialog.Builder(this)
                .setTitle("查找单词")//标题
                .setView(tableLayout)//设置视图
                //确定按钮及其动作
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String txtSearchWord = ((EditText) tableLayout.findViewById(R.id.txtSearchWord)).getText().toString();

                        //单词已经插入到数据库，更新显示列表
                        RefreshWordItemFragment(txtSearchWord);
                    }
                })
                //取消按钮及其动作
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()//创建对话框
                .show();//显示对话框

    }

    @Override
    public void onWordDetailClick(Uri uri) {

    }

    @Override
    public void onWordItemClick(String id) {
            Intent intent = new Intent(MainActivity.this,WordDetailActivity.class);
            intent.putExtra(WordDetailFragment.ARG_ID, id);
            startActivity(intent);

    }


    private void ChangeWordDetailFragment(String id){
        Bundle arguments = new Bundle();
        arguments.putString(WordDetailFragment.ARG_ID, id);
        Log.v(TAG, id);

        WordDetailFragment fragment = new WordDetailFragment();
        fragment.setArguments(arguments);
        getFragmentManager().beginTransaction().replace(R.id.worddetail, fragment).commit();
    }

    @Override
    public void onDeleteDialog(String strId) {
        DeleteDialog(strId);
    }

    @Override
    public void onUpdateDialog(String strId) {
        WordsDB wordsDB=WordsDB.getWordsDB();
        if (wordsDB != null && strId != null) {
            Words.WordDescription item = wordsDB.getSingleWord(strId);
            if (item != null) {
                UpdateDialog(strId, item.word, item.meaning, item.sample);
            }

        }
    }




}
