package com.example.rxsample.rxjavaprogramingbook;

import android.util.Log;
import android.util.Pair;

import com.example.rxsample.rxjavaprogramingbook.Util.Cp3SellingSampleData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class Chapter3 {
    private final static String TAG = Chapter3.class.getSimpleName();

    public static Disposable MapSample() {
        String[] balls = {"1", "2", "3", "5" };
        Observable<String> source = Observable.fromArray(balls)
                .map(ball -> ball + "<>");

        return source.subscribe(data -> Log.d(TAG, "data : " + data));
    }

    public static Disposable FlatMapSample() {
        Function<String, Observable<String>> getDoubleDiamonds
                = ball -> Observable.just(ball + "<>", ball + "<>");

        String[] balls = {"1", "3", "5"};
        Observable<String> source = Observable.fromArray(balls)
                .flatMap(getDoubleDiamonds);

        return source.subscribe(data -> Log.d(TAG, "data : " + data));
    }

    public static Disposable FlatMapInlineSample() {
        String[] balls = {"1", "3", "5"};

        Observable<String> source = Observable.fromArray(balls)
                .flatMap(ball -> Observable.just(ball+"<>", ball+"<>"));

        return source.subscribe(data -> Log.d(TAG, "data : " + data));
    }

    public static Disposable GuGuDanSample() {
        Integer i = new Integer(2);
        Observable<Integer> source = Observable.range(1,9);

        return source.subscribe(data -> Log.d(TAG, i + " * " + data + "= " + i*data));
    }

    public static Disposable GuGuDanSample2() {
        Integer i = new Integer(2);
        Function<Integer, Observable<String>> gugudan =
                num -> Observable.range(1,9)
                        .map(dan -> num + " * " + dan + "= " + num*dan);

        Observable<String> source = Observable.just(i)
                .flatMap(gugudan);

        return source.subscribe(data -> Log.d(TAG, data));
    }

    public static Disposable GuGuDanSample3() {
        Integer i = new Integer(2);

        Observable<String> source = Observable.just(i)
                .flatMap(num -> Observable.range(1,9)
                .map(dan -> num + " * " + dan + "= " + num*dan));

        return source.subscribe(data -> Log.d(TAG,data));
    }

    public static Disposable GuGuDanSample4() {
        Integer i = new Integer(2);

        Observable<String> source = Observable.just(i)
                .flatMap(dan -> Observable.range(1, 9),
                        (dan, num) -> dan + " * " + num + "= " + num*dan);

        return source.subscribe(data -> Log.d(TAG,data));
    }

    public static Disposable FilterSample() {
        String[] obj = {"1 CIRCLE", "2 DIAMOND", "3 CIRCLE"};

        Observable<String> source = Observable.fromArray(obj)
                .filter(data -> data.endsWith("CIRCLE"));

        return source.subscribe(data -> Log.d(TAG, data));
    }

    public static Disposable ReduceSample() {
        String[] balls = {"1", "3", "5"};

        Maybe<String> source = Observable.fromArray(balls)
                .reduce((ball1, ball2) -> ball2 + "(" + ball1 + ")");

        return source.subscribe(data -> Log.d(TAG, data));
    }

    public static Disposable SellingSample() {
        List<Cp3SellingSampleData> sellingList = new ArrayList<>();
        sellingList.add(new Cp3SellingSampleData("TV", 2500));
        sellingList.add(new Cp3SellingSampleData("Camera", 300));
        sellingList.add(new Cp3SellingSampleData("TV", 1600));
        sellingList.add(new Cp3SellingSampleData("Phone", 800));

        Maybe<Integer> source = Observable.fromIterable(sellingList)
                .filter(data -> data.getProduct().equals("TV"))
                .map(data -> data.getPrice())
                .reduce((p1, p2) -> p1+p2);

        return source.subscribe(price -> Log.d(TAG, "TV : " + price));
    }

    public static Disposable SellingPairSample() {
        List<Pair<String, Integer>> sellingList = new ArrayList<>();
        sellingList.add(Pair.create("TV", 2500));
        sellingList.add(Pair.create("Camera", 300));
        sellingList.add(Pair.create("TV", 1600));
        sellingList.add(Pair.create("Phone", 800));

        Maybe<Integer> source = Observable.fromIterable(sellingList)
                .filter(data -> data.first.equals("TV"))
                .map(data -> data.second)
                .reduce((p1, p2) -> p1+p2);

        return source.subscribe(price -> Log.d(TAG, "TV : " + price));
    }

}
