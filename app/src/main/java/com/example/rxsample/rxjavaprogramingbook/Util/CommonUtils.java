package com.example.rxsample.rxjavaprogramingbook.Util;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

public class CommonUtils {
    public static String getShape(String obj) {
        if(obj == null || obj.equals("")) return "NO-SHAPE";
        if(obj.endsWith("-H")) return "HEXAGON";
        if(obj.endsWith("-O")) return "OCTAGON";
        if(obj.endsWith("-R")) return "RECTANGLE";
        if(obj.endsWith("-T")) return "TRIANGLE";
        if(obj.endsWith("<>")) return "DIAMOND";
        return "BALL";
    }

    public static void doSomething() {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNetworkAvailable() {
//        try {
//            Log.d("chapter","isNetworkAvailable");
//            return InetAddress.getByName("www.google.com").isReachable(1000);
//        }catch (IOException e) {
//
//        }

        return false;
    }
}
