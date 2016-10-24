package com.ldxx.story;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
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
        content = (TextView) findViewById(R.id.content);

        content.setTextSize(utils.getFontSize());

        ViewTreeObserver vto = content.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = content.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
                int lines = content.getLineCount();
                Log.e(TAG, "lines: " + lines);
                int height = content.getHeight();
                int scrollY = content.getScrollY();
                Log.e(TAG, "height: "+height +"scrollY:"+scrollY);
                Layout layout = content.getLayout();

                int firstVisibleLineNumber = layout.getLineForVertical(scrollY);
                int lastVisibleLineNumber = layout.getLineForVertical(height + scrollY);
                Log.e(TAG, "onGlobalLayout: " + firstVisibleLineNumber + " " + lastVisibleLineNumber);

            }
        });

        final ScrollView scrollView = (ScrollView) findViewById(R.id.content_detail);
        content.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int y = scrollView.getScrollY();
                Log.e(TAG, "onScrollChanged: " + y);
            }
        });

        //content.setSc
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
