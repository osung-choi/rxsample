package com.example.rxsample.rxjavaprogramingbook;

import android.util.Log;

import com.example.rxsample.rxjavaprogramingbook.Util.CommonUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import hu.akarnokd.rxjava3.math.MathFlowable;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.schedulers.Timed;

public class Chapter4 {
    private static final String TAG = Chapter4.class.getSimpleName();

    public static Disposable IntervalSample() {
        Observable<Long> source = Observable.interval(100L, TimeUnit.MILLISECONDS)
                .map(data -> (data + 1) * 100)
                .take(5);

        return source.subscribe(data -> Log.d(TAG, data+""));
    }

    public static Disposable TimerSample() {
        Observable<Long> source = Observable.timer(100L, TimeUnit.MILLISECONDS)
                .map(notUsed -> System.currentTimeMillis());

        return source.subscribe(time -> Log.d(TAG, time + ""));
    }

    public static Disposable RangeSample() {
        Observable<Integer> source = Observable.range(1,10)
                .filter(value -> value % 2 == 0);

        return source.subscribe(data -> Log.d(TAG, data + ""));
    }

    public static Disposable IntervalRangeSample() {
        Observable<Long> source = Observable.intervalRange(1,
                5,
                100L,
                100L,
                TimeUnit.MILLISECONDS);

        return source.subscribe(data -> Log.d(TAG,data+""));
    }


    static Iterator<String> colors = Arrays.asList("1", "3", "5", "7").iterator();

    public static void DeferSample() {
        Callable<Observable<String>> supplier = () -> getObservable();
        Observable<String> source = Observable.defer(supplier);

        source.subscribe(val -> Log.d(TAG, "Sub #1 : " + val));
        source.subscribe(val -> Log.d(TAG, "Sub #2 : " + val));

        Log.d(TAG, "\n---------------\n");

        Observable<String> source1 = getObservable();

        source1.subscribe(val -> Log.d(TAG, "Sub #3 : " + val));
        source1.subscribe(val -> Log.d(TAG, "Sub #4 : " + val));
    }

    private static Observable<String> getObservable() {
        if(colors.hasNext()) {
            String color = colors.next();
            return Observable.just(color + " BALL" , color + " RECTANGLE", color + " PENTAGON");
        }

        return Observable.empty();
    }

    public static Disposable RepeatSample() {
        String[] balls = {"1", "3", "5"};

        Observable<String> source = Observable.fromArray(balls)
                .repeat(3);

        return source.doOnComplete(() -> Log.d(TAG, "onComplete"))
                .subscribe(data -> Log.d(TAG, "data : " + data));
    }

    public static Disposable ConcatMapSample() {
        String[] balls = {"1", "3", "5"};

        Observable<String> source = Observable.interval(1000L, TimeUnit.MILLISECONDS)
                .map(Long::intValue)
                .map(idx -> balls[idx])
                .take(balls.length)
                .concatMap(ball -> Observable.interval(1500L, TimeUnit.MILLISECONDS)
                .map(notUsed -> ball+ "<>")
                .take(2));

        return source.subscribe(result -> Log.d(TAG, result));
    }

    public static Disposable SwitchMapSample() {
        String[] balls = {"1", "3", "5"};

        Observable<String> source = Observable.interval(1000L, TimeUnit.MILLISECONDS)
                .map(Long::intValue)
                .map(idx -> balls[idx])
                .take(balls.length)
                .doOnNext(ball -> Log.d(TAG, ball)) //DEBUG 용도
                .switchMap(ball -> Observable.interval(1500L, TimeUnit.MILLISECONDS)
                        .map(notUsed -> ball+ "<>")
                        .take(2));

        return source.subscribe(result -> Log.d(TAG, result));
    }

    public static Disposable GroupBySample() {
        String[] objs = {"6", "4", "2-T", "2", "6-T", "4-T"};

        Observable<GroupedObservable<String, String>> source =
                Observable.fromArray(objs)
                .groupBy(CommonUtils::getShape);

        return source.subscribe(obj ->
            obj.subscribe(val ->
                    Log.d(TAG, "Group : " + obj.getKey() + "\t Value : " + val)
            )
        );
    }

    public static Disposable ScanSample() {
        String[] balls = {"1", "3", "5"};

        Observable<String> source = Observable.fromArray(balls)
                .scan((ball1, ball2) -> ball2 + "(" + ball1 + ")");

        return source.subscribe(data -> Log.d(TAG, data));
    }

    public static Disposable ZipSample() {
        Observable<Integer> source = Observable.zip(
                Observable.just(100, 200, 300, 400),
                Observable.just(10, 20, 30),
                Observable.just(1, 2, 3),
                (a, b, c) -> a+b+c
        );

        return source.subscribe(result -> Log.d(TAG, "result : " + result));
    }

    public static Disposable ZipIntervalSample() {
        Observable<String> source = Observable.zip(
                Observable.just("RED", "GREEN", "BLUE"),
                Observable.interval(2000L, TimeUnit.MILLISECONDS),
                (value, time) -> value
        );

        return source.subscribe(value -> Log.d(TAG, "value : " + value));
    }

    public static Disposable ZipWithSample() {
        Observable<Integer> source = Observable.zip(
                Observable.just(100, 200, 300, 400),
                Observable.just(10, 20, 30),
                (a, b) -> a+b)
                .zipWith(Observable.just(1,2,3), (ab, c) -> ab + c);

        return source.subscribe(result -> Log.d(TAG, "result : " + result));
    }

    public static Disposable CombineLatestSample() {
        String[] data1 = {"6", "7", "4", "2"};
        String[] data2 = {"DIAMOND", "STAR", "PENTAGON"};

        Observable<String> source = Observable.combineLatest(
            Observable.fromArray(data1)
                .zipWith(Observable.interval(200L, TimeUnit.MILLISECONDS),
                        (data, notUsed) -> data),
            Observable.fromArray(data2)
                .zipWith(Observable.interval(150L, TimeUnit.MILLISECONDS),
                        (data, notUsed) -> data),
            (num, type) -> num + "-" + type);


        return source.subscribe(result -> Log.d(TAG, result));
    }

    public static Disposable MergeSample() {
        String[] data1 = {"1", "3"};
        String[] data2 = {"2", "4", "6"};

        Observable<String> source = Observable.merge(
                Observable.interval(0L, 100L, TimeUnit.MILLISECONDS)
                        .map(Long::intValue)
                        .map(idx -> data1[idx])
                        .take(data1.length),
                Observable.interval(50L, TimeUnit.MILLISECONDS)
                        .map(Long::intValue)
                        .map(idx -> data2[idx])
                        .take(data2.length)
        );

        return source.subscribe(data -> Log.d(TAG, "value : " + data));
    }

    public static Disposable ConcatSample() {
        Action onCompleteAction = () -> Log.d(TAG, "onComplete()");

        String[] data1 = {"1", "3", "5"};
        String[] data2 = {"2", "4", "6"};

        Observable<String> source1 = Observable.fromArray(data1)
                .doOnComplete(onCompleteAction);

        Observable<String> source2 = Observable.interval(100L, TimeUnit.MILLISECONDS)
                .map(Long::intValue)
                .map(idx -> data2[idx])
                .take(data2.length)
                .doOnComplete(onCompleteAction);

        Observable<String> source = Observable.concat(source1, source2)
                .doOnComplete(onCompleteAction);

        return source.subscribe(data -> Log.d(TAG, data));
    }

    public static Disposable AmbSample() {
        String[] data1 = {"1", "3", "5"};
        String[] data2 = {"2-R", "4-R"};

        List<Observable<String>> sources = Arrays.asList(
                Observable.fromArray(data1)
                        .delay(100L, TimeUnit.MILLISECONDS)
                        .doOnComplete(() -> Log.d(TAG, "Observable 1 : onComplete()")),
                Observable.fromArray(data2)
                        .doOnComplete(() -> Log.d(TAG, "Observable 2 : onComplete()"))
        );

        Observable<String> source = Observable.amb(sources)
                .doOnComplete(() -> Log.d(TAG, "Result : onComplete()"));

        return source.subscribe(data -> Log.d(TAG, "data : " + data));
    }

    public static Disposable TakeUntilSample() {
        String[] data = {"1","2","3","4","5","6"};
        Observable<String> source = Observable.fromArray(data)
                .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS),
                        (val, notUsed) -> val)
                .takeUntil(Observable.timer(500L, TimeUnit.MILLISECONDS))
                .doOnComplete(() -> Log.d(TAG,"onComplete"));

        return source.subscribe(val -> Log.d(TAG, "data : " + val));
    }

    public static Disposable SkipUntilSample() {
        String[] data = {"1","2","3","4","5","6"};
        Observable<String> source = Observable.fromArray(data)
                .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS),
                        (val, notUsed) -> val)
                .skipUntil(Observable.timer(500L, TimeUnit.MILLISECONDS))
                .doOnComplete(() -> Log.d(TAG,"onComplete"));

        return source.subscribe(val -> Log.d(TAG, "data : " + val));
    }

    public static Disposable AllSample() {
        String[] data = {"1","2","3","4","5","6"};

        Single<Boolean> source = Observable.fromArray(data)
                .map(CommonUtils::getShape) //BALL 이라는 문자를 리
                .all("BALL"::equals);

        return source.subscribe(result -> Log.d(TAG, "result : " + result));
    }

    public static void RxJava2ExtensionsSample() {
        Integer[] data = {1, 2, 3, 4};

        Single<Long> source = Observable.fromArray(data)
                .count();
        source.subscribe(count -> Log.d(TAG, "count : " + count));

        Flowable.fromArray(data)
             .to(MathFlowable::max)
             .subscribe(max -> Log.d(TAG, "max : " + max));

        Flowable.fromArray(data)
                .to(MathFlowable::min)
                .subscribe(max -> Log.d(TAG, "max : " + max));

        Flowable<Integer> flowable = Flowable.fromArray(data)
                .to(MathFlowable::sumInt);
        flowable.subscribe(sum -> Log.d(TAG, "sum : " + sum));

        Flowable<Double> flowable2 = Observable.fromArray(data)
                .toFlowable(BackpressureStrategy.BUFFER)
                .to(MathFlowable::averageDouble);
        flowable.subscribe(average -> Log.d(TAG, "average : " + average));
    }

    public static Disposable DelaySample() {
        String[] data = {"1","2","3","4"};

        Observable<String> source = Observable.fromArray(data)
                .delay(1000L , TimeUnit.MILLISECONDS);

        return source.subscribe(result -> Log.d(TAG, "result : " + result));
    }

    public static Disposable TimeIntervalSample() {
        String[] data = {"1","3","7"};

        Observable<Timed<String>> source = Observable.fromArray(data)
                .delay(item -> {
                    CommonUtils.doSomething();
                    return Observable.just(item);
                })
                .timeInterval();

        return source.subscribe(time -> Log.d(TAG, "Timed : " + time));
    }
}
