package com.example.vadym.testsuperdeal.mvvm;

import com.example.vadym.testsuperdeal.room.DBViewModel;
import com.example.vadym.testsuperdeal.util.GitHubError;

import javax.inject.Inject;

public class Model {

    private DBViewModel dbViewModel;
    private GitHubError error;

    @Inject
    public Model(DBViewModel dbViewModel, GitHubError error) {
        this.dbViewModel = dbViewModel;
        this.error = error;
    }

    public GitHubError getError() {
        return error;
    }

    public DBViewModel getDbViewModel() {
        return dbViewModel;
    }

}
