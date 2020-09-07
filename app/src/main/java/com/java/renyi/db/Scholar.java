package com.java.renyi.db;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Scholar {
    String avatar;
    Boolean bind;
    String id;
    Float activity;
    Integer citations;
    Float diversity;
    Integer gindex;
    Integer hindex;
    Float newStar;   // newStar,risingStar are the same, we only use newStar
    Integer pubs;
    Float sociability;
    String name;
    String name_zh;
    Integer num_followed;
    Integer num_viewed;

    // These tags can be null in some scholars
    String [] tags;
    Integer [] tags_score;

    // PassedAway and NotPassedAway Profiles Are Different
    HashMap<String, String> profile;
    // PassedAway: affiliation, bio, edu, position, passaway_day, passaway_month,passaway_year, passaway_reason, org, org_zh, email, note,  phone,
    // Living: affiliation(affiliation_zh,) , bio, edu, position, work, note,homepage,  (academic_type (eg:杰出青年学者))(address,) (email, email_cr, fax,)(phone)

    Integer score;
    String sourcetype;
    Integer index;
    Integer tab;
    Boolean is_passedaway;

    Scholar(JSONObject o) {
        avatar = o.getString("avatar");
        bind = o.getBoolean("bind");
        id = o.getString("id");
        JSONObject indices = o.getJSONObject("indices");
        activity = indices.getFloat("activity");
        citations = indices.getInteger("citations");
        diversity = indices.getFloat("diversity");
        gindex = indices.getInteger("gindex");
        hindex = indices.getInteger("hindex");
        newStar = indices.getFloatValue("newStar");
        pubs = indices.getInteger("pubs");
        sociability = indices.getFloat("sociability");
        name = o.getString("name");
        name_zh = o.getString("name_zh");
        num_followed = o.getInteger("num_followed");
        num_viewed = o.getInteger("num_viewed");
        JSONObject prof = o.getJSONObject("profile");
        profile = new HashMap<String, String>();
        Set<String> profKeys = prof.keySet();
        for (String key : profKeys) {
            profile.put(key, prof.getString(key));
        }
        score = o.getInteger("score");
        sourcetype = o.getString("sourcetype");
        index = o.getInteger("index");
        tab = o.getInteger("tab");
        is_passedaway = o.getBoolean("is_passedaway");
        JSONArray tagsArr = o.getJSONArray("tags");
        JSONArray tagsScoreArr = o.getJSONArray("tags_score");
        if (tagsArr != null)
        {
            int tagLength = tagsArr.size();
            tags = new String[tagLength];
            tags_score = new Integer[tagLength];
            for (int i = 0; i < tagLength; i++) {
                tags[i] = tagsArr.getString(i);
                tags_score[i] = tagsScoreArr.getInteger(i);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("name:" + name
                +"\nname_zh" + name_zh
                + "\nindex:" + index
                +"\navatar:" + avatar
                +"\nbind:" + bind
                +"\nid:" + id
                +"\nactivity:" + activity
                +"\ncitations:" + citations
                +"\ndiversity:" + diversity
                +"\ngindex:" + gindex
                +"\nhindex:" + hindex
                +"\nnewStar/risingStar" + newStar
                +"\npubs:" + pubs
                +"\nsociability:"+sociability
                +"\nnum_followed:"+num_followed
                +"\nnum_viewed:"+num_viewed);
        for (Map.Entry<String, String> entry : profile.entrySet()) {
            result.append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");
        }
        String rest = "\nscore:" + score
                + "\nsourcetype:" + sourcetype
                + "\ntab:" + tab
                + "\nis_passedaway:" + is_passedaway + "\n";
        result.append(rest);
        return result.toString();
    }
}
