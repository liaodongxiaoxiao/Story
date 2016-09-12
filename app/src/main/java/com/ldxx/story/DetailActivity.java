package com.ldxx.story;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ldxx.story.bean.Story;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

public class DetailActivity extends AppCompatActivity {

    private TextView content;
    private ActionBar actionBar;

    DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
            .setDbName("story.db");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String id = getIntent().getStringExtra("id");
        initView();

        queryData(id);

    }

    private void queryData(String id) {
        DbManager db = x.getDb(daoConfig);
        try {
            Story story = db.selector(Story.class).where("pid", "=", id).findFirst();
            if (story != null) {
                //toolbar.setTitle(story.getTitle());
                if(actionBar!=null){
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        content = (TextView) findViewById(R.id.content);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
