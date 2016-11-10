package com.ldxx.story.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.ldxx.story.DetailActivity;
import com.ldxx.story.R;
import com.ldxx.story.adapter.StoryAdapter;
import com.ldxx.story.bean.Story;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzhuo-neu
 * on 2016/11/10.
 */

public abstract class StoryListFragment extends Fragment {

    protected static final String TAG = "StoryListFragment";
    private StoryAdapter adapter;
    private RecyclerView list;

    private List<Story> data = new ArrayList<>();
    private ItemTouchHelper mItemTouchHelper;
    private ItemDragAndSwipeCallback mItemDragAndSwipeCallback;

    protected DbManager db;
    protected static final int PAGE_SIZE = 50;
    protected int pageNum = 0;
    protected ResultHandler handler;

    public StoryListFragment() {
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("story.db");
        this.db  = x.getDb(daoConfig);
        handler = new ResultHandler(this);
    }


    private void initAdapter(RecyclerView list) {
        adapter = new StoryAdapter(data);

        mItemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(mItemDragAndSwipeCallback);
        mItemTouchHelper.attachToRecyclerView(list);

        mItemDragAndSwipeCallback.setDragMoveFlags(ItemTouchHelper.RIGHT);
        mItemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.END);
        adapter.enableDragItem(mItemTouchHelper);
        adapter.setOnItemDragListener(listener);
        View view = getLayoutInflater(null).inflate(R.layout.load_more,
                (ViewGroup) list.getParent(), false);
        adapter.setLoadingView(view);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadStory();
            }
        });

    }

    abstract void loadStory();

    protected void initRecyclerView(View parent, int recyclerViewId) {
        list = (RecyclerView) parent.findViewById(recyclerViewId);

        initAdapter(list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        list.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void SimpleOnItemClick(BaseQuickAdapter bAdapter, View view, int position) {
                //toastUtil.showToast(adapter.getData().get(position).getName(), Toast.LENGTH_SHORT);
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra("id", adapter.getData().get(position).getPid());
                startActivity(intent);
            }
        });

        list.setLayoutManager(layoutManager);
        list.setAdapter(adapter);
    }

    protected void setData(List<Story> data) {
        adapter.addData(data);
    }

    protected void loadComplete() {
        adapter.loadComplete();
    }


    protected class ResultHandler extends Handler {
        private WeakReference<StoryListFragment> re;

        ResultHandler(StoryListFragment a) {
            re = new WeakReference<>(a);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            StoryListFragment a = re.get();
            if (msg.what == 1) {
                List<Story> data = (List<Story>) msg.obj;
                a.setData(data);
            } else if (msg.what == 0) {
                Toast.makeText(getContext(), "未查询到结果", Toast.LENGTH_SHORT).show();
                loadComplete();
            } else {
                Toast.makeText(getContext(), "查询出错", Toast.LENGTH_SHORT).show();
                loadComplete();
            }
        }
    }

    private OnItemDragListener listener = new OnItemDragListener() {
        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {
            Log.e(TAG, "drag start");
            BaseViewHolder holder = ((BaseViewHolder) viewHolder);
            holder.setTextColor(R.id.title, Color.WHITE);
            ((CardView) viewHolder.itemView).setCardBackgroundColor(
                    ContextCompat.getColor(getContext(), R.color.color_light_blue));
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
            new AlertDialog.Builder(getContext()).setTitle("提醒").setMessage("确定要删除这个故事吗？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialog,
                                int which) {
                            Story story = adapter.getItem(pos);
                            try {
                                db.delete(Story.class, WhereBuilder.b("pid", "=", story.getPid()));

                                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                            } catch (DbException e) {
                                Toast.makeText(getContext(), "删除失败：" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        }
    };
}
