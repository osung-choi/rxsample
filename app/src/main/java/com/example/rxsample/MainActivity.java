package com.example.rxsample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;

import android.os.Bundle;

import com.example.rxsample.databinding.ActivityMainBinding;
import com.example.rxsample.rxjavaprogramingbook.Chapter7;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ObservableField<String> result = new ObservableField<>();
    public ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setBinding(this);

        Chapter8.OnBackpressureLatestSample();
//        addDisposable(Chapter7.debounceSample());
    }

    private void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.dispose();
    }
}
