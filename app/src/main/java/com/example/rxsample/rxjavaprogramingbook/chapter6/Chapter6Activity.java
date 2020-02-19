package com.example.rxsample.rxjavaprogramingbook.chapter6;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;

import androidx.databinding.DataBindingUtil;

import com.example.rxsample.R;
import com.example.rxsample.databinding.ActivityChapter6Binding;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

public class Chapter6Activity extends RxAppCompatActivity {
    private final String TAG = Chapter6Activity.class.getSimpleName();
    ActivityChapter6Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chapter6);
        binding.setBinding(this);

//        getObervable()
//                .debounce(500, TimeUnit.MILLISECONDS)
//                .filter(s -> !TextUtils.isEmpty(s))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(getObserver());

        RxTextView.textChangeEvents(binding.edtSearch)
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter(s -> !TextUtils.isEmpty(s.getText().toString()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> Log.d(TAG, s.getText().toString()));
    }

    private Observable<String> getObervable() {
        return Observable.create(emitter -> binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emitter.onNext(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }));
    }

    private DisposableObserver<CharSequence> getObserver() {
        return new DisposableObserver<CharSequence>() {
            @Override
            public void onNext(CharSequence charSequence) {
                Log.d(TAG,"Search : " + charSequence.toString());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }
}
