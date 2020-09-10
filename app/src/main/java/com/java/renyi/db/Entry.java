package com.java.renyi.db;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import com.alibaba.fastjson.*;
import com.java.renyi.LDA.me.xiaosheng.lda.HanLDA;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



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
    public String seg_text;
    public boolean viewed = false;
    public String category;
    public List<String> urls;
    public String cluster;
    public Entry(String _id) { this._id = _id;}


    public Entry(String type, JSONObject json, boolean getClusterImmediate) {
        _id = json.getString("_id");
        title = json.getString("title");
        content = json.getString("content");
        seg_text = json.getString("seg_text");
        // source needs special treatment in paper and events
        if ("news".equals(type)) {
            source = json.getString("source");
        } else if ("paper".equals(type)) {
            source = processAuthor(json.getString("authors"));
        }
        time = proccessDate(json.getString("date"));
        this.type = json.getString("type");

        if (getClusterImmediate)
            cluster = getCluster();
        else
            cluster = "";
        viewed = false;
        category = json.getString("category");
        JSONArray urlArr = json.getJSONArray("urls");
        int urlArrLength = urlArr.size();
        urls = new ArrayList<>();
        for (int i = 0; i < urlArrLength; i++) {
            urls.add(urlArr.getString(0));
        }
    }

    public void modifyCluster() {
        cluster = getCluster();
    }

    private static boolean isChinese(String str){
        String regEx = "[\\u4e00-\\u9fa5]+";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.find();
    }

    static int splitCnt = 0;
    private String getSplit(String s) {
        if (splitCnt < 3 ) {
            Log.e("splitStart"+splitCnt, EntryRepository.getTimeMilli());
        }
        List<Term> terms = ToAnalysis.parse(s).getTerms();
        if (splitCnt < 3 ) {
            Log.e("splitFinish"+splitCnt, EntryRepository.getTimeMilli());
        }
        splitCnt+=1;
        StringBuffer result = new StringBuffer("");
        for (Term t: terms) {
            result.append(" ".concat(t.getName()));
        }
//        Log.e("isChinese", result.toString());
        return result.toString().trim();
    }

    private String split(String s) {
        if (!isChinese(s)) {
//            Log.e("notChinese", s);
            return s;
        }
//        return s.seg_text;
        return getSplit(s);
    }
    private String getCluster() {
        String result = seg_text;
        if (result == null)
            return "";
        double [] topicDistribution = HanLDA.inferenceFromString(EntryRepository.getHDA(), result,false);
        return getMaxTopic(topicDistribution);
    }

    private String getMaxTopic(double[] distribution) {
        int maxID = 0;
        double maxVal = distribution[0];
        int length = distribution.length;
        for (int i = 1; i < length; i++) {
            if (distribution[i] > maxVal) {
                maxVal = distribution[i];
                maxID = i;
            }
        }

        // Use event-5-lts.model
//        Log.e("maxID = ", maxID+"");
        if (maxID == 0)
            return "病毒研究";
        else if (maxID == 1)
            return "疫情形势";
        else if (maxID == 2)
            return "病毒研究";
        else if (maxID == 3)
            return "疫苗药物";
        else if (maxID == 4)
            return "患者治疗";
        return "topic" + maxID;
    }

    private String processAuthor(String jsonString) {
        JSONArray jarr = JSON.parseArray(jsonString);
        StringBuilder authors = new StringBuilder("");
        int length = jarr.size();
        for (int i = 0; i < length; i++) {
            if (i != 0)
                authors.append(" ,");
            authors.append(((JSONObject)(jarr.get(i))).getString("name"));
        }
        return authors.toString();
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
//        String ret = "+id:"+_id+"\ntype:"+type + " \ntitle:" + title
////                + "\ncontent:" + content
//                + " \ntime:" + time
//                + "\nviewed:"+ viewed;
////                + "\nsource:" + source;
        String ret = "id:" + _id + "\ntitle:" + title
                + "\ncontent:" + content
                + "\ntime:"+time
                +"\n"+cluster;
        return ret;
    }
}

