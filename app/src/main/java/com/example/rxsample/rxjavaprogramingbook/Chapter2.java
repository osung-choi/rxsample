package com.example.rxsample.rxjavaprogramingbook;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

public class Chapter2 {
    private final static String TAG = Chapter2.class.getSimpleName();

    public static Disposable CreateSample() {
        Observable<Integer> source = Observable.create(emitter -> {
            emitter.onNext(100);
            emitter.onNext(200);
            emitter.onNext(300);
            emitter.onComplete();
        });

        return source.subscribe(value -> Log.d(TAG, value+""));
    }

    public static Disposable FromArraySample() {
        Integer[] arr = {100,200,300};
        Observable<Integer> source = Observable.fromArray(arr);

        return source.subscribe(value -> Log.d(TAG, value+""));
    }

    public static Disposable FromIteratorSample() {
        List<String> names = new ArrayList<>();
        names.add("홍길이");
        names.add("동길이");
        names.add("상길이");

        Observable<String> source = Observable.fromIterable(names);
        return source.subscribe(name -> Log.d(TAG, name));
    }

    public static Disposable CustomFromMapSample() {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "가");
        map.put(2, "나");
        map.put(3, "다");

        Observable<String> source = Chapter2.fromMap(map);
        return source.subscribe(name -> Log.d(TAG, name));
    }

    public static Disposable FromCallableSample() {
        Callable<String> callable = () -> {
            Thread.sleep(1000);
            return "Hello callable";
        };

        Observable<String> source = Observable.fromCallable(callable);
        return source.subscribe(text -> Log.d(TAG, text));
    }

    public static Disposable FromFutureSample() {
        Future<String> future = Executors.newSingleThreadExecutor().submit(() -> {
            Thread.sleep(1000);
            return "Hello future";
        });

        Observable<String> source = Observable.fromFuture(future);
        return source.subscribe(text -> Log.d(TAG,text));
    }



    public static <T,K> Observable<K> fromMap(Map<T,K> map) {
        List<K> mapList = new ArrayList<>();
        Set keySet = map.keySet();

        Iterator iterator = keySet.iterator();
        while(iterator.hasNext()) {
            mapList.add(map.get(iterator.next()));
        }

        return Observable.fromIterable(mapList);
    }

    public static void AsyncSubjectSample() {
        AsyncSubject<String> subject = AsyncSubject.create();
        subject.subscribe(data -> Log.d(TAG, "1 "+ data));
        subject.onNext("1");
        subject.onNext("3");
        subject.subscribe(data -> Log.d(TAG, "2 "+ data));
        subject.onNext("5");
        subject.onComplete();
    }

    public static void AsyncSubjectSubscribeSample() {
        Float[] tempArray = {10.1f, 13.4f, 12.5f};
        Observable<Float> source = Observable.fromArray(tempArray);

        AsyncSubject<Float> subject = AsyncSubject.create();
        subject.subscribe(temp -> Log.d(TAG, "temp : " + temp));

        source.subscribe(subject);
    }

    public static void BehavoirSubjectSample() {
        BehaviorSubject<String> subject = BehaviorSubject.createDefault("가");
        subject.subscribe(data -> Log.d(TAG,"subscribe 1 => " + data));
        subject.onNext("나");
        subject.onNext("다");
        subject.subscribe(data -> Log.d(TAG,"subscribe 2 => " + data));
        subject.onNext("라");
        subject.onComplete();
    }

    public static void PublishSubjectSample() {
        PublishSubject<String> subject = PublishSubject.create();
        subject.subscribe(data -> Log.d(TAG, "subscribe 1 => " + data));
        subject.onNext("가");
        subject.onNext("나");
        subject.subscribe(data -> Log.d(TAG, "subscribe 2 => " + data));
        subject.onNext("다");
    }

    public static void ReplaySubjectSample() {
        ReplaySubject<String> subject = ReplaySubject.create();
        subject.onNext("가");
        subject.onNext("나");
        subject.subscribe(data -> Log.d(TAG, "subscribe 1 => " + data));
        subject.onNext("다");
        subject.subscribe(data -> Log.d(TAG, "subscribe 2 => " + data));
        subject.onNext("라");
    }

    public static void ConnectableObservableSample() {
        String[] dt = {"1", "2", "3"};
        Observable<String> balls = Observable.interval(100L, TimeUnit.MILLISECONDS)
                .map(Long::intValue)
                .map(i -> dt[i])
                .take(dt.length);

        ConnectableObservable<String> source = balls.publish();
        source.subscribe(data -> Log.d(TAG, "subscribe 1 => " + data));
        source.subscribe(data -> Log.d(TAG, "subscribe 2 => " + data));
        source.connect();

        try {
            Thread.sleep(250);
            source.subscribe(data -> Log.d(TAG, "subscribe 3 => " + data));
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
