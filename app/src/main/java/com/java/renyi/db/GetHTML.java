package com.java.renyi.db;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetHTML {
    // get json string from url
    public static String getHTML(String urlToRead)  {
        StringBuilder result = new StringBuilder();
        try {
            Log.e("getting HTML", "in getting");
            URL url = new URL(urlToRead);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (Exception e) {
            Log.e("getHTML error", e.toString());
        }
        return result.toString();
    }
}
