package com.example.rxsample.rxjavaprogramingbook.Util;

import android.util.Log;

public class DLog {
    public static void d(Object obj) {
        Log.d("Chapter",getThreadName() + " | value = " + obj);
    }

    private static String getThreadName() {
        String threadName = Thread.currentThread().getName();
        if (threadName.length() > 30) {
            threadName = threadName.substring(0, 30) + "...";
        }
        return threadName;
    }
}
