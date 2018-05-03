package com.example.vadym.testsuperdeal.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.vadym.testsuperdeal.model.GitHubInfo;

@Database(entities = GitHubInfo.class, version = 1)
public abstract class GitHubDB extends RoomDatabase {

    private static final String DB_NAME = "gitDB.db";
    private static GitHubDB instance;

    public static GitHubDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, GitHubDB.class, DB_NAME).build();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    public abstract GitHubDao gitHubDao();
}
