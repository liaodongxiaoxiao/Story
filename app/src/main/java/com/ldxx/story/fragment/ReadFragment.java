package com.ldxx.story.fragment;


import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ldxx.story.R;
import com.ldxx.story.bean.Story;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.List;

/**
 *
 */
public class ReadFragment extends StoryListFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_read, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView(view, R.id.list);
    }

    @Override
    void loadStory() {
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
                                    .or("favorite_flag", "=", null)
                                    .and("percent","=",100).offset((pageNum - 1) * PAGE_SIZE)
                                    .limit(PAGE_SIZE);
                    /*if (!isDesc) {
                        selector.orderBy("date_time", false);
                    } else {
                        selector.orderBy("date_time");
                    }*/

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
}
