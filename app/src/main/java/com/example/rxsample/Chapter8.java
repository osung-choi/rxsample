package com.example.rxsample;

import android.util.Log;

import com.example.rxsample.rxjavaprogramingbook.Util.DLog;

import io.reactivex.BackpressureOverflowStrategy;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class Chapter8 {
    private static final String TAG = Chapter8.class.getSimpleName();

    public static void OnBackpressureBufferSample() {
        Flowable.range(1, 50_000_000)
                .onBackpressureBuffer(128, () -> {}, BackpressureOverflowStrategy.DROP_OLDEST)
                .observeOn(Schedulers.computation())
                .subscribe(data -> {
                    Thread.sleep(100);
                    DLog.d(data);
                }, err -> Log.d(TAG, err.toString()));
    }

    public static void OnBackpressureDropSample() {
        Flowable.range(1, 50_000_000)
                .onBackpressureDrop()
                .observeOn(Schedulers.computation())
                .subscribe(data -> {
                    Thread.sleep(100);
                    DLog.d(data);
                }, err -> Log.d(TAG, err.toString()));
    }

    public static void OnBackpressureLatestSample() {
        Flowable.range(1, 50_000_000)
                .onBackpressureLatest()
                .observeOn(Schedulers.computation())
                .subscribe(data -> {
                    Thread.sleep(100);
                    DLog.d(data);
                }, err -> Log.d(TAG, err.toString()));
    }
}
