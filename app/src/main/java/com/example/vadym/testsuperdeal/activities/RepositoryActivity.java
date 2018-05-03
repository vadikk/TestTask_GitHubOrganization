package com.example.vadym.testsuperdeal.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.vadym.testsuperdeal.R;
import com.example.vadym.testsuperdeal.databinding.ActivityRepositoryBinding;
import com.example.vadym.testsuperdeal.model.GitHubRepository;
import com.example.vadym.testsuperdeal.recycler.repository.DividerItem;
import com.example.vadym.testsuperdeal.recycler.repository.RepositoryAdapter;
import com.example.vadym.testsuperdeal.retrofit.GitHubRetrofit;
import com.example.vadym.testsuperdeal.util.GitHubError;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class RepositoryActivity extends AppCompatActivity {

    public static final String ORG_NAME = "name";
    public static final String ORG_LOGIN = "login";
    public static final String REPOS_COUNT = "count";
    private ActivityRepositoryBinding binding;
    private RepositoryAdapter adapter;
    private CompositeDisposable compositeDisposable;
    private GitHubError error;
    private String name;
    private String count;
    private String login;
    private boolean isLoading = false;
    private int page = 1;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_repository);

        actionBar = getSupportActionBar();
        if (actionBar == null)
            return;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString(ORG_NAME);
            count = bundle.getString(REPOS_COUNT);
            login = bundle.getString(ORG_LOGIN);
        }
        if (name != null)
            actionBar.setTitle(getResources().getString(R.string.title, name, count));
        else
            actionBar.setTitle(getResources().getString(R.string.title, login, count));

        compositeDisposable = new CompositeDisposable();
        error = new GitHubError();
        binding.setError(error);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recycler.setLayoutManager(manager);

        binding.recycler.addItemDecoration(new DividerItem(this));

        adapter = new RepositoryAdapter(this);
        binding.recycler.setAdapter(adapter);

        loadData(login, page);

        binding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isLoading) return;

                int lastElement = manager.findLastCompletelyVisibleItemPosition();
                int loadedItemCount = adapter.getItemCount();
                int loadShouldStartPosition = (int) (loadedItemCount * 0.8);

                if (loadShouldStartPosition <= lastElement && loadedItemCount < Integer.valueOf(count)) {
                    page++;
                    isLoading = true;
                }

                if (isLoading) {
                    loadData(login, page);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadData(String nameOrg, int page) {
        error.isLoading.set(true);

        Flowable flowable = GitHubRetrofit.getRetrofit().getGitHubRepository(nameOrg, page)
                .doOnComplete(() -> {
                    if (binding.getError().isError.get())
                        binding.getError().isError.set(false);
                })
                .filter(gitHubRepositiries -> !gitHubRepositiries.isEmpty())
                .delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .flatMapCompletable(this::addToAdapter).toFlowable()
                .onErrorReturn(throwable -> {
                    showErrorView(throwable.getMessage());
                    return true;
                });
        Disposable disposable = flowable.subscribe();
        compositeDisposable.add(disposable);
    }

    private Completable addToAdapter(List<GitHubRepository> info) {
        return Completable.fromAction(() -> {
            error.isLoading.set(false);
            adapter.addAllToList(info);
            isLoading = false;
        });
    }

    private void showErrorView(String text) {
        adapter.clear();
        error.setErrorText(text);
        error.isError.set(true);
        error.isLoading.set(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (compositeDisposable != null)
            compositeDisposable.clear();
    }
}
