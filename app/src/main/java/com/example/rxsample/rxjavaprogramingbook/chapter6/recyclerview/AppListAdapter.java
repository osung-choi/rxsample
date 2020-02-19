package com.example.rxsample.rxjavaprogramingbook.chapter6.recyclerview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rxsample.databinding.ItemAppListBinding;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {
    private List<AppList> items;
    //PublishSubject 는 create 함수를 통하여 onNext, onComplete, onError을 직접 호출
    //뜨거운 Observable 인 PublishSubject를 활용하는 이유는 구독자가 없더라도 실시간으로 처리되어 소비해야 하는 Click 이벤트의 특성 때문이다.
    private PublishSubject<AppList> mPublishSubject;

    public AppListAdapter() {
        mPublishSubject = PublishSubject.create();
    }

    public PublishSubject<AppList> getPublishSubject() {
        return mPublishSubject;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAppListBinding binding = ItemAppListBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppList appList = items.get(position);
        holder.bind(appList);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @BindingAdapter("item")
    public static void bindItem(RecyclerView recyclerView, ObservableArrayList<AppList> items) {
        ((AppListAdapter)recyclerView.getAdapter()).setItem(items);
    }

    private void setItem(List<AppList> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ItemAppListBinding binding;

        public ViewHolder(ItemAppListBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(AppList appList) {
            binding.setItem(appList);
            getClickObserver(appList).subscribe(mPublishSubject);
        }

        public Observable<AppList> getClickObserver(AppList item) {
            return Observable.create(
                    e -> itemView.setOnClickListener(
                            view -> e.onNext(item)
                    )
            );
        }
    }
}
