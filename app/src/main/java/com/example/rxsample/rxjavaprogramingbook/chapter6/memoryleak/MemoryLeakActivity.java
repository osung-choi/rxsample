package com.example.rxsample.rxjavaprogramingbook.chapter6.memoryleak;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.rxsample.R;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class MemoryLeakActivity extends RxAppCompatActivity {

    private Disposable disposable;
    private TextView textView;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_leak);

        textView = findViewById(R.id.textView);

        DisposableObserver<String> observer = new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                textView.setText(s);
            }

            @Override
            public void onError(Throwable e) { }

            @Override
            public void onComplete() { }
        };

        disposable = Observable.<String>create(emitter -> {
            emitter.onNext("hello world");
            emitter.onComplete();
        }).subscribeWith(observer);

        Observable.<String>create(emitter -> {
            emitter.onNext("hello world");
            emitter.onComplete();
        })
                .compose(bindToLifecycle())
                //.compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(textView::setText);

        disposable = Observable.<String>create(emitter -> {
            emitter.onNext("hello world");
            emitter.onComplete();
        }).subscribe(textView::setText);

        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(disposable.isDisposed()) {
            disposable.dispose();
        }

        if(compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
            //compositeDisposable.clear();
        }
    }
}
