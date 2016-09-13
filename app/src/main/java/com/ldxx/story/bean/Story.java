package com.ldxx.story.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by wangzhuo-neu on 2016/9/12.
 */
@Table(name = "story")
public class Story {
    @Column(name = "pid", isId = true)
    private String pid;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "date_time")
    private String date_time;
    @Column(name = "favorite_flag")
    private String favorite_flag;
    @Column(name = "favorite_time")
    private Date favorite_time;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getFavorite_flag() {
        return favorite_flag;
    }

    public void setFavorite_flag(String favorite_flag) {
        this.favorite_flag = favorite_flag;
    }

    public Date getFavorite_time() {
        return favorite_time;
    }

    public void setFavorite_time(Date favorite_time) {
        this.favorite_time = favorite_time;
    }
}
