package com.example.vadym.testsuperdeal.retrofit;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GitHubRetrofit {

    private static Retrofit retrofit;

    public static GitHubApi getRetrofit(){
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(GitHubOkHttpClient.getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();

        return retrofit.create(GitHubApi.class);
    }
}
