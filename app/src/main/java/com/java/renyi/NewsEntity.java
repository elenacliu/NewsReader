package com.java.renyi;

import android.content.ContentValues;
import android.content.Entity;

import java.io.Serializable;

/**
 * A demo
 * 数据库的新闻实体
 */
public class NewsEntity implements Serializable {
    private String title;
    private String content;
    private String time;
    private String source;
    private boolean isViewed;
    private String url;

    public NewsEntity(String title, String content, String time, String source, boolean isViewed, String url) {
        this.title = title;
        this.content = content;
        this.time = time;
        this.source = source;
        this.isViewed = isViewed;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isViewed() {
        return isViewed;
    }

    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }
}
