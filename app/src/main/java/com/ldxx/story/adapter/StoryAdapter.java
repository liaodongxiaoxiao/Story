package com.ldxx.story.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ldxx.story.R;
import com.ldxx.story.bean.Story;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by wangzhuo-neu
 * on 2016/9/12.
 */

public class StoryAdapter extends BaseQuickAdapter<Story> {
    public StoryAdapter(List<Story> data) {
        super(R.layout.item_menu_list, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Story story) {
        baseViewHolder.setText(R.id.title, story.getTitle())
                .setText(R.id.date, dateToString(story.getDate_time()));
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:dd", Locale.getDefault());
        return sdf.format(date);
    }
}
