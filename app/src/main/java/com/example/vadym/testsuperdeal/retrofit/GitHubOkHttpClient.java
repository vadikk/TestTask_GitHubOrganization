package com.example.vadym.testsuperdeal.retrofit;

import android.util.Log;

import com.example.vadym.testsuperdeal.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class GitHubOkHttpClient {

    private static final int TIMEOUT_CONNECTION = 10;

    public static OkHttpClient getOkHttpClient() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        httpClient.connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS);
        httpClient.addInterceptor(loggingInterceptor);

//        Log.d("TAG", "httpLog " + loggingInterceptor.toString());
////        Log.d("TAG", "httpLog " + loggingInterceptor.getLevel());

        return httpClient.build();
    }
}
