package com.java.renyi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * 辅助类
 * 检查网络情况
 */

public class NetworkUtil {
    /**
     * 检查网络是否可用
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager =  (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isAvailable()) {
            return false;
        }

        return true;
    }
}
