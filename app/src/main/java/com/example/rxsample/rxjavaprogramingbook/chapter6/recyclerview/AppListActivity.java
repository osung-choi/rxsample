package com.example.rxsample.rxjavaprogramingbook.chapter6.recyclerview;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;

import com.example.rxsample.R;
import com.example.rxsample.databinding.ActivityAppListBinding;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AppListActivity extends AppCompatActivity {

    public ObservableArrayList<AppList> appList = new ObservableArrayList<>();
    private ActivityAppListBinding binding;
    private AppListAdapter mAdapter;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_list);
        binding.setBinding(this);

        mAdapter = new AppListAdapter();
        binding.appList.setAdapter(mAdapter);

        List<AppList> list = new ArrayList<>();

//        compositeDisposable.add(
//                getItemObservable()
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(item -> list.add(item),
//                                e -> e.printStackTrace(),
//                                () -> appList.addAll(list))
//        );

        compositeDisposable.add(
                getItemObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(item -> appList.addAll(list)));

        compositeDisposable.add(
                mAdapter.getPublishSubject()
                .subscribe(item ->
                        Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show()
                )
            );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private Observable<AppList> getItemObservable() {
        PackageManager pm = getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        return Observable.fromIterable(pm.queryIntentActivities(i, 0))
                .sorted(new ResolveInfo.DisplayNameComparator(pm))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(item -> {
                    Drawable image = item.activityInfo.loadIcon(pm);
                    String title = item.activityInfo.loadLabel(pm).toString();
                    return new AppList(image, title);
                });
    }
}
