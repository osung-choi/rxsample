package com.example.rxsample.rxjavaprogramingbook.chapter6.retrofit2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.rxsample.R;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class RetrofitActivity extends AppCompatActivity {
    private final String TAG = RetrofitActivity.class.getSimpleName();
    private CompositeDisposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        disposable = new CompositeDisposable();

        RestfulAdapter api = RestfulAdapter.getInstance();
        Observable<List<Contributor>> observable =
                api.getSimpleApi().getObContributors("android", "RxJava");

        disposable.add(
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<List<Contributor>>() {
                            @Override
                            public void onNext(List<Contributor> contributors) {
                                for(Contributor c : contributors) {
                                    Log.d(TAG,c.toString());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {
                                Log.d(TAG,"onComplete");
                            }
                        })
        );
    }
}
