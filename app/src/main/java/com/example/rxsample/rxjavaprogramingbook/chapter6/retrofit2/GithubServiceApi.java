package com.example.rxsample.rxjavaprogramingbook.chapter6.retrofit2;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GithubServiceApi {
    @GET("repos/{owner}/{repo}/contributors")
    Observable<List<Contributor>> getObContributors(
            @Path("owner") String owner, @Path("repo") String repo);
}
