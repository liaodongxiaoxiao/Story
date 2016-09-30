package com.ldxx.story.adapter;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ldxx.story.R;
import com.ldxx.story.bean.Story;

import java.util.List;

/**
 * Created by wangzhuo-neu
 * on 2016/9/12.
 */

public class StoryAdapter extends BaseItemDraggableAdapter<Story> {
    public StoryAdapter(List<Story> data) {
        super(R.layout.item_menu_list, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Story story) {
        baseViewHolder.setText(R.id.title, story.getTitle())
                .setText(R.id.date, story.getDate_time());
    }

}
