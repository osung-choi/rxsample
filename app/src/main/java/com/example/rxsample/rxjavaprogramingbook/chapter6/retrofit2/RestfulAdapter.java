package com.example.rxsample.rxjavaprogramingbook.chapter6.retrofit2;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestfulAdapter {
    private static final String BASE_URL = "https://api.github.com/";

    private RestfulAdapter() {} //Singleton

    public GithubServiceApi getSimpleApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        return retrofit.create(GithubServiceApi.class);
    }

    private static class Singleton {
        private static final RestfulAdapter instance = new RestfulAdapter();
    }

    public static RestfulAdapter getInstance() {
        return Singleton.instance;
    }
}
