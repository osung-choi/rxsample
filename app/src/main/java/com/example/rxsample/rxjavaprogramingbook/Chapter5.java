package com.example.rxsample.rxjavaprogramingbook;

import com.example.rxsample.rxjavaprogramingbook.Util.DLog;
import com.example.rxsample.rxjavaprogramingbook.Util.OkHttpHelper;
import com.example.rxsample.rxjavaprogramingbook.Util.WeatherUtil;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static java.lang.Thread.sleep;

public class Chapter5 {
    private static final String TAG = Chapter5.class.getSimpleName();

    public static Disposable SchedulerSimpleSample() {
        String[] objs = {"1", "2", "3"};
        Observable<String> source = Observable.fromArray(objs)
                .doOnNext(data -> DLog.d("Original data : " + data))
                //.subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .map(obj -> obj + "-S");

        return source.subscribe(DLog::d);
    }

    public static void NewThreadSample() {
        String[] objs = {"1", "3", "5"};
        Observable.fromArray(objs)
                .doOnNext(data -> DLog.d(data))
                .map(data -> "<<"+data+">>")
                .subscribeOn(Schedulers.newThread())
                .subscribe(DLog::d);

        Observable.fromArray(objs)
                .doOnNext(data -> DLog.d(data))
                .map(data -> "##"+data+"##")
                .subscribeOn(Schedulers.newThread())
                .subscribe(DLog::d);
    }

    public static void ComputationThreadSample() {
        String[] objs = {"1", "3", "5"};

        Observable<String> source = Observable.fromArray(objs)
                .zipWith(Observable.interval(100L, TimeUnit.MILLISECONDS), (obj, time) -> obj);

        source.map(item -> "<<" + item + ">>")
                .subscribeOn(Schedulers.computation())
                .subscribe(DLog::d);

        source.map(item -> "##" + item + "##")
                .subscribeOn(Schedulers.computation())
                .subscribe(DLog::d);
    }

    public static Disposable IOThreadSample() {
        String root = "/Users/cos";
        File[] files = new File(root).listFiles();
        Observable<String> source = Observable.fromArray(files)
                .filter(f -> !f.isDirectory())
                .map(f -> f.getAbsolutePath())
                .subscribeOn(Schedulers.io());

        return source.subscribe(file -> DLog.d(file));
    }

    public static void TrampolineThreadSample() {
        String[] objs = {"1", "3", "5"};
        Observable<String> source = Observable.fromArray(objs);

        source.subscribeOn(Schedulers.trampoline())
                .map(data -> "<<"+data+">>")
                .subscribe(DLog::d);

        source.subscribeOn(Schedulers.trampoline())
                .map(data -> "##"+data+"##")
                .subscribe(DLog::d);
    }

    public static void ExecutorThreadSample() {
        final int THREAD_NUM = 2;

        String[] objs = {"1", "3", "5"};
        Observable<String> source = Observable.fromArray(objs);
        Executor executor = Executors.newFixedThreadPool(THREAD_NUM);

        source.subscribeOn(Schedulers.from(executor))
                .subscribe(DLog::d);

        source.subscribeOn(Schedulers.from(executor))
                .subscribe(DLog::d);
    }

    public static void RxNetworkSample() {
        final String FIRST_URL = "https://api.github.com/zen";
        final String SECOND_URL = "https://api.github.com/zen";

        Observable<String> source = Observable.just(FIRST_URL)
                .subscribeOn(Schedulers.io())
                .map(OkHttpHelper::get)
                .concatWith(Observable.just(SECOND_URL)
                    .map(OkHttpHelper::get));

        source.subscribe(DLog::d);
    }

    public static Disposable RxNetworkZipSample() {
        final String FIRST_URL = "https://api.github.com/zen";
        final String SECOND_URL = "https://api.github.com/zen";

        Observable<String> source = Observable.just(FIRST_URL)
                .subscribeOn(Schedulers.io())
                .map(OkHttpHelper::get);

        Observable<String> source2 = Observable.just(SECOND_URL)
                .subscribeOn(Schedulers.io())
                .map(OkHttpHelper::get);

        return Observable.zip(source, source2, (a, b) -> "\n"+ a + "\n" + b)
                .subscribe(DLog::d);
    }

    public static void WeatherSample() {
        final String URL = "https://api.openweathermap.org/data/2.5/weather?q=Korea&APPID=ca32956e2f8864bb71a04d468c9040e0";

        Observable<String> source = Observable.just(URL)
                .subscribeOn(Schedulers.io())
                .map(OkHttpHelper::get)
                .observeOn(Schedulers.newThread())
                .share();

        source.map(WeatherUtil::parseTemperature).subscribe(DLog::d);
        source.map(WeatherUtil::parseCityName).subscribe(DLog::d);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        source.map(WeatherUtil::parseCountry).subscribe(DLog::d);

    }

    public static void ShapeSample() throws InterruptedException {
        Observable<Long> threeRandoms = Observable.interval(1,TimeUnit.SECONDS)
                .share();

        threeRandoms.take(4).subscribe(i -> DLog.d("Observer 1 : " + i));

        sleep(3000);
        threeRandoms.take(2).subscribe(i -> DLog.d("Observer 2 : " + i));

        sleep(3000);
        threeRandoms.take(3).subscribe(i -> DLog.d("Observer 4 : " + i));
    }

    public static void Shape2Sample() throws InterruptedException {
        Long[] num = {1L,2L,3L};

        Observable<Long> threeRandoms = Observable.fromArray(num)
                .map(data -> plus(data))
//                .subscribeOn(Schedulers.newThread())
                .share();

        threeRandoms.subscribe(i->DLog.d("Observer 1 : " + i));
        threeRandoms.subscribe(i->DLog.d("Observer 2 : " + i));

        sleep(3000);

        threeRandoms.subscribe(i->DLog.d("Observer 4 : " + i));
    }

    static int i=1;
    public static Long plus(Long num) {
        return num + (i++);
    }
}
