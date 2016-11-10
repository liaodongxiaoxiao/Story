package com.ldxx.story;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ScrollingView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldxx.story.bean.Story;
import com.ldxx.story.utils.DateUtils;
import com.ldxx.story.utils.SharedPreferencesUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    private static final String LINE = "LINE_KEY";
    //当前最上边的行号
    private int currentTopLine;

    //单行行高
    int lineHeight;
    //本篇文章共多少行
    int lines;
    //当前可见范围能显示多少行
    int perPageLine;

    private ScrollView scrollView;
    private ProgressBar bar;
    private TextView content;
    private ActionBar actionBar;
    private DbManager db;
    private Story story;

    private int flag;

    private SharedPreferencesUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String id = getIntent().getStringExtra("id");
        flag = getIntent().getIntExtra("flag", 0);
        utils = new SharedPreferencesUtils(this);

        initDB();
        initView();

        queryData(id);

    }

    private void initDB() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("story.db");
        db = x.getDb(daoConfig);
    }

    private void queryData(String id) {

        try {
            story = db.selector(Story.class).where("pid", "=", id).findFirst();
            if (story != null) {
                //toolbar.setTitle(story.getTitle());
                if (actionBar != null) {
                    actionBar.setTitle(story.getTitle());
                }
                content.setText(story.getContent());
                currentTopLine = story.getTop_line();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void initView() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                collectionOrNot(view);
            }
        });
        scrollView = (ScrollView) findViewById(R.id.content_detail);
        bar = (ProgressBar) findViewById(R.id.bar);
        content = (TextView) findViewById(R.id.content);

        content.setTextSize(utils.getFontSize());

        ViewTreeObserver vto = content.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = content.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
                lines = content.getLineCount();
                Log.e(TAG, "lines: " + lines);
                lineHeight = content.getLineHeight();
                perPageLine = scrollView.getHeight() / lineHeight;
                scroll();
                if(currentTopLine==0){
                    setBarProgress(perPageLine);
                }

            }
        });

        content.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int y = scrollView.getScrollY();
                int line = (int) Math.ceil((scrollView.getHeight() + y) / lineHeight);
                currentTopLine = line - perPageLine;
                setBarProgress(line);
            }
        });

        //content.setSc
    }

    @Override
    protected void onResume() {
        super.onResume();
        //currentTopLine = preferences.getInt(LINE, 0);
        //Log.e(TAG, "onStart: " + currentTopLine);

    }
    @Override
    protected void onPause() {
        super.onPause();
        //SharedPreferences.Editor editor = preferences.edit();
        //Log.e(TAG, "onStop: " + currentTopLine);
        //editor.putInt(LINE, currentTopLine);
        //editor.apply();
        try {
            story.setTop_line(currentTopLine);
            story.setPercent(bar.getProgress());
            db.saveOrUpdate(story);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public void setBarProgress(int line) {
        bar.setProgress((int) ((line * 1d / lines) * 100));
    }

    private void scroll() {
        if (currentTopLine > 0) {
            content.post(new Runnable() {
                @Override
                public void run() {
                    //int y = content.getLayout().getLineTop(currentTopLine);
                    int y = currentTopLine * lineHeight;
                    Log.e(TAG, "run: " + y);
                    scrollView.smoothScrollTo(0, y);
                }
            });

        }
    }



    private void collectionOrNot(View view) {

        try {
            if (story != null) {
                if (flag == 0) {
                    story.setFavorite_flag("1");
                    story.setFavorite_time(DateUtils.getCurrentDate());

                    db.saveOrUpdate(story);
                    Snackbar.make(view, "收藏成功", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    flag = 1;

                } else {
                    story.setFavorite_flag("0");
                    story.setFavorite_time(null);

                    db.saveOrUpdate(story);
                    Snackbar.make(view, "取消收藏成功", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    flag = 0;
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.text_size_normal) {
            changeTextSize(16);
        } else if (item.getItemId() == R.id.text_size_middle) {
            changeTextSize(18);
        } else if (item.getItemId() == R.id.text_size_large) {
            changeTextSize(20);
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeTextSize(float textSize) {
        utils.saveFontSize(textSize);
        content.setTextSize(textSize);
    }
}
