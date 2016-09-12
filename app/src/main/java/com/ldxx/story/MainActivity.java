package com.ldxx.story;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ldxx.story.adapter.StoryAdapter;
import com.ldxx.story.bean.Story;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
            .setDbName("story.db");
    private RecyclerView list;
    private StoryAdapter adapter;
    private List<Story> data = new ArrayList<>();
    private DbManager db;
    private static final int PAGE_SIZE = 10;
    private int pageNum = 0;

    private ResultHandler handler = new ResultHandler(MainActivity.this);

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        db = x.getDb(daoConfig);
        bindEvent();

    }

    private void loadStory() {
        pageNum++;
        //显示ProgressDialog
        dialog = ProgressDialog.show(MainActivity.this, "Loading...", "Please wait...", true, false);
        final Message msg = new Message();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Story> data = db.selector(Story.class).offset((pageNum - 1) * PAGE_SIZE)
                            .limit(PAGE_SIZE).findAll();
                    if (data != null && !data.isEmpty()) {
                        msg.what = 1;
                        msg.obj = data;

                    } else {
                        msg.what = 0;
                    }
                    handler.sendMessage(msg);
                } catch (DbException e) {
                    Log.e(TAG, "run: " + e.getMessage(), e);
                    msg.what = -1;
                    handler.sendMessage(msg);
                }
            }
        }).start();

    }

    private class ResultHandler extends Handler {
        private WeakReference<MainActivity> re;

        ResultHandler(MainActivity a) {
            re = new WeakReference<>(a);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity a = re.get();
            if (msg.what == 1) {
                List<Story> data = (List<Story>) msg.obj;
                a.setDate(data);
            } else if (msg.what == 0) {
                Toast.makeText(a, "未查询到结果", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(a, "查询出错", Toast.LENGTH_SHORT).show();
            }

            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    private void setDate(List<Story> data) {
        adapter.addData(data);
    }


    private void bindEvent() {
        adapter = new StoryAdapter(data);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);
        View view = getLayoutInflater().inflate(R.layout.load_more,
                (ViewGroup) list.getParent(), false);
        adapter.setLoadingView(view);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadStory();
            }
        });

        list.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter bAdapter, View view, int position) {
                //toastUtil.showToast(adapter.getData().get(position).getName(), Toast.LENGTH_SHORT);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("id", adapter.getData().get(position).getPid());
                startActivity(intent);
            }
        });
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        list = (RecyclerView) findViewById(R.id.list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
