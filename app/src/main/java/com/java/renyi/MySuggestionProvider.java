package com.java.renyi;

import android.content.SearchRecentSuggestionsProvider;

/**
 * 近期查询建议的内容提供程序，必须是SearchRecentSuggestionsProvider的实现
 */
public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.java.renyi";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider() {
        // 传递搜索授权方名称和一种数据库模式
        setupSuggestions(AUTHORITY, MODE);
    }
}
