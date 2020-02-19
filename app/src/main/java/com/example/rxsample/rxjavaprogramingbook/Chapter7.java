package com.example.rxsample.rxjavaprogramingbook;

import android.util.Log;

import androidx.databinding.ObservableField;

import com.example.rxsample.rxjavaprogramingbook.Util.CommonUtils;
import com.example.rxsample.rxjavaprogramingbook.Util.DLog;
import com.example.rxsample.rxjavaprogramingbook.Util.OkHttpHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class Chapter7 {
    private static final String TAG = Chapter7.class.getSimpleName();

    public static Disposable doOnEventSample() {
        String[] objs = {"1", "3", "5"};

        return Observable.fromArray(objs)
                .doOnNext(data -> Log.d(TAG, "onNext : " + data))
                .doOnComplete(() -> Log.d(TAG, "onComplete"))
                .doOnError(t -> t.printStackTrace())
                .subscribe(DLog::d);
    }

    public static Disposable doOnEachSample() {
        String[] data = {"ONE", "TWO", "THREE"};

        return Observable.fromArray(data)
                .doOnEach(noti -> {
                    if(noti.isOnNext()) Log.d(TAG, "onNext : " + noti.getValue());
                    if(noti.isOnError()) noti.getError().printStackTrace();
                    if(noti.isOnComplete()) Log.d(TAG, "onComplete");
                }).subscribe(DLog::d);
    }

    public static void doOnSubscribeSample() throws InterruptedException {
        String[] objs = {"1", "3", "5", "7", "9"};
        Disposable d = Observable.fromArray(objs)
                .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a,b) -> a)
                .doOnSubscribe(disposable -> DLog.d("onSubscribe"))
                .doOnDispose(() -> DLog.d("onDispose"))
                .subscribe(DLog::d);

        Thread.sleep(200);
        d.dispose();
        Thread.sleep(300);
    }

    public static void doOnLifeCycleSample() throws InterruptedException {
        String[] objs = {"1", "3", "5", "7", "9"};
        Disposable d = Observable.fromArray(objs)
                .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a,b) -> a)
                .doOnLifecycle(
                        disposable -> DLog.d("onSubscribe()"),
                        () -> DLog.d("onDispose()")
                )
                .subscribe(DLog::d);

        Thread.sleep(200);
        d.dispose();
        Thread.sleep(300);
    }

    public static Disposable doOnTerminateSample() {
        String[] objs = {"1", "3", "5"};

        return Observable.fromArray(objs)
                .doOnTerminate(() -> DLog.d("onTerminate"))
                .doOnComplete(() -> DLog.d("onComplete"))
                .subscribe(DLog::d);
    }

    public static Disposable onErrorReturnSample() {
        String[] objs = {"70", "88", "$100", "93", "83"}; //$100이 에러 데이터

        Observable<Integer> source = Observable.fromArray(objs)
                .map(data -> Integer.parseInt(data))
                .onErrorReturn(throwable -> {
                    if(throwable instanceof NumberFormatException) {
                        throwable.printStackTrace();
                    }

                    return -1;
                });

        return source.subscribe(DLog::d);
    }

    public static Disposable onErrorReturnItemSample() {
        String[] objs = {"70", "88", "$100", "93", "83"}; //$100이 에러 데이터

        Observable<Integer> source = Observable.fromArray(objs)
                .map(data -> Integer.parseInt(data))
                .onErrorReturnItem(-1);

        return source.subscribe(DLog::d);
    }

    public static Disposable onErrorResumeNextSample() {
        String[] objs = {"100", "200", "A300", "400"}; //A300은 에러 데이터

        Observable<Integer> onParseError = Observable.defer(() -> {
            Log.d(TAG, "send email to administrator");
            return Observable.just(-1);
        }).subscribeOn(Schedulers.io());

        Observable<Integer> source = Observable.fromArray(objs)
                .map(obj -> Integer.parseInt(obj))
                .onErrorResumeNext(onParseError);

        return source.subscribe(data -> {
            if(data < 0) {
                Log.d(TAG, "Wrong Data found");
                return;
            }

            Log.d(TAG, "data : " + data);
        });
    }

    public static Disposable retrySample() {
        final int RETRY_MAX = 5;
        final int RETRY_DELAY = 1000;
        String url = "https://api.github.com/zenasd";
        Observable<String> source = Observable.just(url)
                .map(OkHttpHelper::get)
                .retry((retryCnt, e) -> {
                    Thread.sleep(RETRY_DELAY);
                    return retryCnt < RETRY_MAX? true:false;
                })
                .onErrorReturn(e -> "-500");

        return source.subscribe(DLog::d);
    }

    public static Disposable retryUntilSample() {
        String url = "http://api.github.com/zenasd";
        Observable<String> source = Observable.just(url)
                .map(OkHttpHelper::get)
                .subscribeOn(Schedulers.io())
                .retryUntil(() -> {
                    if(CommonUtils.isNetworkAvailable()) {
                        return true;
                    }

                    Thread.sleep(1000);

                    return false;
                });

        return source.subscribe(DLog::d);
    }

    public static void retryWhenSample() {
        Observable.<String>create(e -> {
            e.onError(new RuntimeException("always fails"));
        }).retryWhen(throwableObservable ->
            throwableObservable.zipWith(Observable.range(1,3), (n,i) -> i)
                    .flatMap(i -> {
                        Log.d(TAG,"delay retry by : " + i);
                        return Observable.timer(i, TimeUnit.SECONDS);
                    })).blockingForEach(DLog::d);
    }

    public static Disposable sampleSample() {
        String[] data = {"1", "7", "2", "3", "6"};

        Observable<String> earlySource = Observable.fromArray(data)
                .take(4)
                .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a,b) -> a);

        Observable<String> lateSource = Observable.just(data[4])
                .zipWith(Observable.timer(300L, TimeUnit.MILLISECONDS), (a,b) -> a);

        Observable<String> source = Observable.concat(earlySource, lateSource)
                .sample(300L, TimeUnit.MILLISECONDS, true);

        return source.subscribe(DLog::d);
    }

    public static Disposable bufferSample() {
        String[] data = {"1", "2", "3", "4", "5", "6"};

        Observable<String> earlySource = Observable.fromArray(data)
                .take(3)
                .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a,b) -> a);

        Observable<String> middleSource = Observable.just(data[3])
                .zipWith(Observable.interval(300L, TimeUnit.MILLISECONDS), (a,b) -> a);

        Observable<String> lateSource = Observable.just(data[4], data[5])
                .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a,b) -> a);

        Observable<List<String>> source = Observable.concat(earlySource, middleSource, lateSource)
                .buffer(3);

        return source.subscribe(DLog::d);
    }

    public static Disposable throttleFirstSample() {
        String[] data = {"1", "2", "3", "4", "5", "6"};

        Observable<String> earlySource = Observable.just(data[0])
                .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a,b) -> a);

        Observable<String> middleSource = Observable.just(data[1])
                .zipWith(Observable.interval(300L, TimeUnit.MILLISECONDS), (a,b) -> a);

        Observable<String> lateSource = Observable.just(data[2], data[3], data[4], data[5])
                .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a,b) -> a);

        Observable<String> source = Observable.concat(earlySource, middleSource, lateSource)
                .throttleFirst(200L, TimeUnit.MILLISECONDS);

        return source.subscribe(DLog::d);
    }

    public static Disposable windowSample() {
        String[] data = {"1", "2", "3", "4", "5", "6"};

        Observable<String> earlySource = Observable.fromArray(data)
                .take(3)
                .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a,b) -> a);

        Observable<String> middleSource = Observable.just(data[3])
                .zipWith(Observable.interval(300L, TimeUnit.MILLISECONDS), (a,b) -> a);

        Observable<String> lateSource = Observable.just(data[4], data[5])
                .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (a,b) -> a);

        Observable<Observable<String>> source = Observable.concat(earlySource, middleSource, lateSource)
                .window(3);

        return source.subscribe(observable -> {
            Log.d(TAG,"new Observable start");
            observable.subscribe(DLog::d);
        });
    }

    public static Disposable debounceSample() {
        String[] data = {"1", "2", "3", "5"};

        Observable<String> source = Observable.concat(
                Observable.timer(100L, TimeUnit.MILLISECONDS).map(i -> data[0]),
                Observable.timer(300L, TimeUnit.MILLISECONDS).map(i -> data[1]),
                Observable.timer(100L, TimeUnit.MILLISECONDS).map(i -> data[2]),
                Observable.timer(300L, TimeUnit.MILLISECONDS).map(i -> data[3]))
                .debounce(200L, TimeUnit.MILLISECONDS);

        return source.subscribe(DLog::d);
    }
}
