package com.example.vadym.testsuperdeal.retrofit;

import com.example.vadym.testsuperdeal.model.GitHubInfo;
import com.example.vadym.testsuperdeal.model.GitHubRepository;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GitHubApi {

    @GET("orgs/{name}")
    Observable<GitHubInfo> getGitHubResponce(@Path("name") String nameOrganization);

    @GET("orgs/{name}/repos?per_page=10")
    Flowable<List<GitHubRepository>> getGitHubRepository(@Path("name") String name, @Query("page") int page);
}
