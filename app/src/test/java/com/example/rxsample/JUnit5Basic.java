package com.example.rxsample;

import android.util.Log;

import com.example.rxsample.rxjavaprogramingbook.Util.DLog;
import com.example.rxsample.rxjavaprogramingbook.Util.OkHttpHelper;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class JUnit5Basic{
    private final String TAG = JUnit5Basic.class.getSimpleName();

    @DisplayName("test Observable.interval() wrong")
    @Test
    @Disabled //테스트코드를 비활성화 하는 경우 추가
    void testIntervalWrongWay() {
        Observable<Integer> source = Observable.interval(100L, TimeUnit.MILLISECONDS)
                .take(5)
                .map(Long::intValue);

        source.doOnNext(DLog::d)
                .test().assertResult(0,1,2,3,4);
    }

    @DisplayName("test Observable.interval() normal")
    @Test
    void testIntervalNormalWay() {
        Observable<Integer> source = Observable.interval(100L, TimeUnit.MILLISECONDS)
                .take(5)
                .map(Long::intValue);

        source.test()
                .awaitDone(1L, TimeUnit.SECONDS)
                .assertResult(0,1,2,3,4);
    }

    @DisplayName("TestAsyncExample")
    @Test
    @Disabled
    void testAsyncExample() {
        final String URL = "https://api.github.com/users/yudong80";
        Observable<String> source = Observable.just(URL)
                .subscribeOn(Schedulers.io())
                .map(OkHttpHelper::get)
                .observeOn(Schedulers.newThread());

        String expected = ""; //통신 결과 Json 파싱

        source.test()
                .awaitDone(3L, TimeUnit.SECONDS)
                .assertResult(expected);
    }


}