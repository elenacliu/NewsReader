package com.java.renyi.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.alibaba.fastjson.*;

import java.io.Serializable;
import java.util.HashMap;

@Entity(tableName = "entry_table")
public class Entry implements Serializable {
    @PrimaryKey
    @NonNull
    private String _id;
    public String title;
    public String content;
    public String source;
    public String time;
    public String type;
    public boolean viewed = false;
    public String category;
    public String urls;
    public Entry(String _id) { this._id = _id;}
    public Entry(String type, JSONObject json) {
        _id = json.getString("_id");
        title = json.getString("title");
        content = json.getString("content");
        // source needs special treatment in paper and events
        if ("news".equals(type)) {
            source = json.getString("source");
        } else if ("paper".equals(type)) {
            source = json.getString("authors");
        }
        // TODO : which date should be selected, json.getDate()?
        time = proccessDate(json.getString("date"));
        this.type = json.getString("type");
        viewed = false;
        category = json.getString("category");
        urls = json.getString("urls");
    }

    private String proccessDate(String date) {
        String[] info = date.split(" ");
        HashMap<String, String> strHashMap = new HashMap<String, String>();
        strHashMap.put("Jan", "01");
        strHashMap.put("Feb", "02");
        strHashMap.put("Mar", "03");
        strHashMap.put("Apr", "04");
        strHashMap.put("May", "05");
        strHashMap.put("Jun", "06");
        strHashMap.put("Jul", "07");
        strHashMap.put("Aug", "08");
        strHashMap.put("Sep", "09");
        strHashMap.put("Oct", "10");
        strHashMap.put("Nov", "11");
        strHashMap.put("Dec", "12");
        int year = Integer.parseInt(info[3]);
        int month = Integer.parseInt(strHashMap.get(info[2]));
        int day = Integer.parseInt(info[1]);
        String [] timeInfo = info[4].split(":");
        int hour = Integer.parseInt(timeInfo[0]) + 8;
        int minute = Integer.parseInt(timeInfo[1]);
        int second = Integer.parseInt(timeInfo[2]);
        if (hour >= 24) {
            hour -= 24;
            day += 1;
            if (day > 30) {
                day -= 30;
                month += 1;
                if (month > 12) {
                    month -= 12;
                    year += 1;
                }
            }
        }
        String res = year + "/" + String.format("%02d", month) + "/" + String.format("%02d", day) + "/" + String.format("%02d", hour) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
        return res;
    }

    @NonNull
    public String get_id() { return _id; }

    @NonNull
    @Override
    public String toString() {
//        String ret = "+id:"+_id+"\ntype:"+type + " \ntitle:" + title + " \ncontent:" + content + " \n source:" + source
//                    + " \ntime:" + time + " \nviewed:" + viewed + " \n category:" + category + "\nurls:" + urls;
//        String ret = "type:"+type + " \ntitle:" + title + " \ntime:" + time;
//        String ret = "type:"+type + " \ntitle:" + title + " \ntime:" + time + "\nviewed:"+ viewed;
        String ret = "+id:"+_id+"\ntype:"+type + " \ntitle:" + title + " \ntime:" + time + "\nviewed:"+ viewed;

        return ret;
    }
}
