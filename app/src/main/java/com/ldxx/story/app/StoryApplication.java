package com.ldxx.story.app;

import android.app.Application;

import org.xutils.x;

/**
 * Created by wangzhuo-neu
 * on 2016/9/12.
 */

public class StoryApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }
}
