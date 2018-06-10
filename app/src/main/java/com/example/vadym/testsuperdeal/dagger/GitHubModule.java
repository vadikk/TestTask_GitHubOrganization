package com.example.vadym.testsuperdeal.dagger;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.example.vadym.testsuperdeal.mvvm.GitHubViewModel;
import com.example.vadym.testsuperdeal.mvvm.Model;
import com.example.vadym.testsuperdeal.recycler.main.GitHubAdapter;
import com.example.vadym.testsuperdeal.recycler.repository.RepositoryAdapter;
import com.example.vadym.testsuperdeal.room.DBViewModel;
import com.example.vadym.testsuperdeal.util.GitHubError;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class GitHubModule {

    private AppCompatActivity activity;

    public GitHubModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Provides
    AppCompatActivity getActivity() {
        return activity;
    }

    @Provides
    DBViewModel getViewModel() {
        return ViewModelProviders.of(activity).get(DBViewModel.class);
    }

    @Provides
    GitHubError getError() {
        return new GitHubError();
    }

    @Provides
    Model getModel(DBViewModel dbViewModel, GitHubError error) {
        return new Model(dbViewModel, error);
    }

    @Provides
    LinearLayoutManager getManager(Application application) {
        Context context = (Context) application;
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        return manager;
    }

    @Provides
    GitHubAdapter getAdapter() {
        return new GitHubAdapter();
    }

    @Provides
    RepositoryAdapter getRepositoryAdapter(Context context) {
        return new RepositoryAdapter(context);
    }

    @Provides
    CompositeDisposable getCompositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    GitHubViewModel getGitHubViewModel(Model model, LinearLayoutManager manager,
                                       GitHubAdapter adapter, RepositoryAdapter adapterRepository, CompositeDisposable compositeDisposable) {
        return new GitHubViewModel(model, manager, adapter, adapterRepository, compositeDisposable);
    }


}
