package com.example.vadym.testsuperdeal.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.vadym.testsuperdeal.model.GitHubInfo;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface GitHubDao {

    @Insert(onConflict = REPLACE)
    void insert(GitHubInfo info);

    @Update
    void update(GitHubInfo info);

    @Query("SELECT * FROM org")
    Flowable<List<GitHubInfo>> getAll();

    @Query("DELETE FROM org")
    void deleteAll();

}
