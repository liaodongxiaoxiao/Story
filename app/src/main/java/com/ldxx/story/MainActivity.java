package com.ldxx.story;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.ldxx.story.adapter.StoryAdapter;
import com.ldxx.story.bean.Story;
import com.ldxx.story.utils.SharedPreferencesUtils;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
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
    private static final int PAGE_SIZE = 50;
    private int pageNum = 0;

    private ResultHandler handler = new ResultHandler(MainActivity.this);

    //private ProgressDialog dialog;

    private ItemTouchHelper mItemTouchHelper;
    private ItemDragAndSwipeCallback mItemDragAndSwipeCallback;

    private boolean isDesc;

    private SharedPreferencesUtils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        utils = new SharedPreferencesUtils(this);
        isDesc = utils.isDesc();
        initView();
        db = x.getDb(daoConfig);
        bindEvent();

    }

    private void loadStory() {
        pageNum++;
        //显示ProgressDialog
        //dialog = ProgressDialog.show(MainActivity.this, "Loading...", "Please wait...", true, false);
        final Message msg = new Message();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Selector<Story> selector =
                            db.selector(Story.class).where("favorite_flag", "=", "0")
                                    .or("favorite_flag", "=", null).offset((pageNum - 1) * PAGE_SIZE)
                                    .limit(PAGE_SIZE);
                    /*if (!isDesc) {
                        selector.orderBy("date_time", false);
                    } else {*/
                        selector.orderBy("date_time");
                    //}

                    List<Story> data = selector.findAll();

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
                adapter.loadComplete();
            } else {
                Toast.makeText(a, "查询出错", Toast.LENGTH_SHORT).show();
                adapter.loadComplete();
            }

            /*if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }*/
        }
    }

    private void setDate(List<Story> data) {
        adapter.addData(data);
    }


    private void bindEvent() {
        adapter = new StoryAdapter(data);

        mItemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(mItemDragAndSwipeCallback);
        mItemTouchHelper.attachToRecyclerView(list);

        mItemDragAndSwipeCallback.setDragMoveFlags(ItemTouchHelper.RIGHT);
        mItemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.END);
        //adapter.enableSwipeItem();
        //adapter.setOnItemSwipeListener(onItemSwipeListener);
        adapter.enableDragItem(mItemTouchHelper);
        adapter.setOnItemDragListener(listener);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

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

        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDesc = !isDesc;
                utils.saveIsDesc(isDesc);
                pageNum = 0;
                loadStory();
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
            startActivity(new Intent(MainActivity.this, CollectionActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private OnItemDragListener listener = new OnItemDragListener() {
        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.e(TAG, "drag start");
            BaseViewHolder holder = ((BaseViewHolder) viewHolder);
            holder.setTextColor(R.id.title, Color.WHITE);
            ((CardView) viewHolder.itemView).setCardBackgroundColor(
                    ContextCompat.getColor(MainActivity.this, R.color.color_light_blue));
        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {
            Log.e(TAG, "move from: " + source.getAdapterPosition() + " to: " + target.getAdapterPosition());
        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, final int pos) {
            Log.e(TAG, "drag end");
            //viewHolder.itemView.setVisibility(View.GONE);
            adapter.remove(pos);
            new AlertDialog.Builder(MainActivity.this).setTitle("提醒").setMessage("确定要删除这个故事吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialog,
                                int which) {
                            Story story = adapter.getItem(pos);
                            try {
                                db.delete(Story.class, WhereBuilder.b("pid", "=", story.getPid()));

                                Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            } catch (DbException e) {
                                Toast.makeText(MainActivity.this, "删除失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialog,
                                int which) {
                            // 这里点击取消之后可以进行的操作
                        }
                    }).show();
            //BaseViewHolder holder = ((BaseViewHolder) viewHolder);
            //holder.setTextColor(R.id.title, Color.BLACK);
            //((CardView) viewHolder.itemView).setCardBackgroundColor(Color.WHITE);
        }
    };

    /*private OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.d(TAG, "view swiped start: " + pos);
            BaseViewHolder holder = ((BaseViewHolder) viewHolder);
            holder.setTextColor(R.id.title, Color.WHITE);
            ((CardView) viewHolder.itemView).setCardBackgroundColor(Color.YELLOW);
        }

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.d(TAG, "View reset: " + pos);
            BaseViewHolder holder = ((BaseViewHolder) viewHolder);
            holder.setTextColor(R.id.title, Color.BLACK);
            ((CardView) viewHolder.itemView).setCardBackgroundColor(Color.WHITE);
        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.d(TAG, "View Swiped: " + pos);
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
            canvas.drawColor(ContextCompat.getColor(MainActivity.this, R.color.color_light_blue));
            canvas.drawText("Just some text", 0, 40, paint);
        }
    };*/


}
