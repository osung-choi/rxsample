package com.example.rxsample.rxjavaprogramingbook.chapter6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import android.os.Bundle;
import android.util.Log;

import com.example.rxsample.R;
import com.example.rxsample.databinding.ActivityAsyncTaskBinding;
import com.example.rxsample.rxjavaprogramingbook.Util.DLog;

import java.util.concurrent.TimeUnit;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Chapter6ExampleActivity extends AppCompatActivity {

    private final String TAG = "Chapter6";
    ActivityAsyncTaskBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_async_task);
        Log.d(TAG,"onCreate");

        startPollingV2();

        Log.d(TAG,"onDestroy");
    }

    private void initRxAsync() {
        Observable.just("Hello", "rx", "world")
                .reduce((x, y) -> x + " " + y)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        text -> binding.tvMsg.setText(text),
                        throwable -> throwable.printStackTrace(),
                        () -> Log.d(TAG, "done")
                );
    }

    private void startPollingV1() {
        Observable<String> ob = Observable.interval(3, TimeUnit.SECONDS)
                .flatMap(o -> Observable.just("polling # 1 " + o));

        ob.subscribeOn(Schedulers.io())
                .doOnNext(DLog::d)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(DLog::d);
    }

    private void startPollingV2() {
        Observable<String> ob = Observable.just("polling # 2")
                .repeatWhen(o -> o.delay(3, TimeUnit.SECONDS));

        ob.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(DLog::d);
    }
}
