package com.example.vadym.testsuperdeal.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.vadym.testsuperdeal.model.GitHubInfo;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class GitHubListModel extends AndroidViewModel {

    private GitHubDB db;

    public GitHubListModel(@NonNull Application application) {
        super(application);

        db = GitHubDB.getInstance(getApplication());
    }

    public Flowable<List<GitHubInfo>> getOrganizations() {
        return db.gitHubDao().getAll();
    }

    public void insertItem(GitHubInfo info) {
        Completable.fromAction(() -> {
            db.gitHubDao().insert(info);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public void deleteAll() {
        Completable.fromAction(() -> db.gitHubDao().deleteAll())
                .subscribeOn(Schedulers.io()).subscribe();
    }
}
