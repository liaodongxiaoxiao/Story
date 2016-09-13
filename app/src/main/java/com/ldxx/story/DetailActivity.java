package com.ldxx.story;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ldxx.story.bean.Story;
import com.ldxx.story.utils.DateUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

public class DetailActivity extends AppCompatActivity {

    private TextView content;
    private ActionBar actionBar;
    public static final String FONT_SIZE = "font_size";
    private DbManager db;
    private Story story;

    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String id = getIntent().getStringExtra("id");
        flag = getIntent().getIntExtra("flag", 0);
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

        SharedPreferences p = this.getPreferences(MODE_PRIVATE);
        float fontSize = p.getFloat(FONT_SIZE, 16);
        content.setTextSize(fontSize);
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
        SharedPreferences p = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = p.edit();
        editor.putFloat(FONT_SIZE, textSize);
        editor.apply();
        content.setTextSize(textSize);
    }
}
