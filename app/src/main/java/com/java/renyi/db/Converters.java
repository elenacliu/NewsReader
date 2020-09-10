package com.java.renyi.db;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<String> fromUrlString(String urlString) {
        List<String> urls = new ArrayList<>();
        urls.addAll(Arrays.asList(urlString.split(" ")));
        return urls;
//        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static String urlsToString(List<String> urls) {
        StringBuffer builder = new StringBuffer();
        int size = urls.size();
        for (int i = 0; i < size; i++) {
            if (i != 0)
                builder.append(" ".concat(urls.get(i)));
            else
                builder.append(urls.get(i));
        }
        return builder.toString();
    }
}
