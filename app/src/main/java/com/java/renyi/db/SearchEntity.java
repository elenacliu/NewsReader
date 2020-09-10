package com.java.renyi.db;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchEntity {
    Float hot;
    String label;
    String url;
    String intro;
    HashMap<String, String> property;
    List<HashMap<String, String>> relation;
    String img;

    public Float getHot() {
        return hot;
    }

    public String getLabel() {
        return label;
    }

    public String getUrl() {
        return url;
    }

    public String getIntro() {
        return intro;
    }

    public HashMap<String, String> getProperty() {
        return property;
    }

    public List<HashMap<String, String>> getRelation() {
        return relation;
    }

    public String getImg() {
        return img;
    }

    SearchEntity(JSONObject o) {
        hot = o.getFloat("hot");
        label = o.getString("label");
        url = o.getString("url");
        img = o.getString("img");
        property = new HashMap<String, String>();
        relation = new ArrayList<HashMap<String, String>>();
        JSONObject info = o.getJSONObject("abstractInfo");
        String baidu = info.getString("baidu");
        String zhwiki = info.getString("zhwiki");
        String enwiki = info.getString("enwiki");
        if (baidu.length() > 0) {
            intro = baidu;
        } else {
            if (zhwiki.length() > 0) {
                intro = zhwiki;
            } else {
                intro = enwiki;
            }
        }
        JSONObject covid = info.getJSONObject("COVID");
        JSONObject properties = covid.getJSONObject("properties");
        Set<String> propertiesKey = properties.keySet();
        for (String k: propertiesKey) {
            property.put(k, properties.getString(k));
        }
        JSONArray relations = covid.getJSONArray("relations");
        int relationsLength = relations.size();
        for (int i = 0; i < relationsLength; i++) {
            JSONObject r = (JSONObject)relations.get(i);
            HashMap<String, String> currentRelation = new HashMap<String, String>();
            currentRelation.put("relation", r.getString("relation"));
            currentRelation.put("url", r.getString("url"));
            currentRelation.put("label", r.getString("label"));
            currentRelation.put("forward", r.getString("forward"));
            relation.add(currentRelation);
        }
    }


    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("hot:" + hot + "\nlabel:" + label
                + "\nurl:" + url + "\nintro:" + intro
                + "\nimg" + img);
        ret.append("\nproperty:");
        ret.append(showMap(property));
        ret.append("\nrelations:");
        for (HashMap<String, String> h: relation) {
            ret.append(showMap(h));
        }
        return ret.toString();
    }

    public static String showMap(HashMap<String, String> property) {
        StringBuilder result = new StringBuilder("");
        for (Map.Entry<String, String> entry : property.entrySet()) {
            result.append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
        }
        return result.toString();
    }
}
