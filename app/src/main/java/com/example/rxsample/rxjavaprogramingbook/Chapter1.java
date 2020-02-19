package com.example.rxsample.rxjavaprogramingbook;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class Chapter1 {
    private final static String TAG = Chapter1.class.getSimpleName();

    public static Disposable JustExample() {
        return Observable.just("Hello", "RxJava2")
                .subscribe(text -> Log.d(TAG,text));
    }
}
