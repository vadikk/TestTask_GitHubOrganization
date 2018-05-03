package com.example.vadym.testsuperdeal.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.RelativeLayout;

import com.example.vadym.testsuperdeal.R;
import com.example.vadym.testsuperdeal.databinding.ActivityMainBinding;
import com.example.vadym.testsuperdeal.model.GitHubInfo;
import com.example.vadym.testsuperdeal.recycler.main.GitHubAdapter;
import com.example.vadym.testsuperdeal.retrofit.GitHubRetrofit;
import com.example.vadym.testsuperdeal.room.GitHubListModel;
import com.example.vadym.testsuperdeal.util.GitHubError;
import com.example.vadym.testsuperdeal.util.OnRepositoryClickListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity implements OnRepositoryClickListener {

    private ActivityMainBinding binding;
    private GitHubAdapter adapter;
    private CompositeDisposable compositeDisposable;
    private GitHubError error;
    private RelativeLayout relativeLayout;
    private GitHubListModel viewModel;
    private boolean isLoadToBD = true;

    private String name;
    private String login;
    private String count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(GitHubListModel.class);

        compositeDisposable = new CompositeDisposable();
        relativeLayout = binding.relative;

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.recycler.setLayoutManager(manager);

        adapter = new GitHubAdapter();
        adapter.setOnRepositoryListener(this);
        binding.recycler.setAdapter(adapter);

        error = new GitHubError();
        binding.setError(error);
        binding.editText.addTextChangedListener(new TextWatcher() {

            Timer t = new Timer();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() >= 3) {
                    setTextViewParams(30);

                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            loadData(editable.toString());
                        }
                    };

                    t.cancel();
                    t = new Timer();
                    t.schedule(task, 1000);


                } else if (binding.editText.getText().length() <= 2) {
                    t.cancel();
                    setTextViewParams(65);
                    binding.getError().isError.set(false);
                    adapter.clear();
                }
            }
        });
        subscribeUIMovie();

    }

    private void subscribeUIMovie() {
        Flowable getList = viewModel.getOrganizations()
                .flatMap(Flowable::just)
                .flatMap(Flowable::fromIterable)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable(this::addAllToAdapter).toFlowable();

        Disposable disposable1 = getList.subscribe();
        compositeDisposable.add(disposable1);

    }

    private void setTextViewParams(int pos) {
        RelativeLayout.LayoutParams rel = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rel.setMargins(0, pos, 0, 0);
        binding.title.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.title.setLayoutParams(rel);
    }

    private void loadData(String query) {
        error.isLoading.set(true);

        Observable flowable = GitHubRetrofit.getRetrofit().getGitHubResponce(query)
                .doOnNext(info -> {
                    login = info.getLogin();
                    name = info.getName();
                    count = info.getRepos();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    if (binding.getError().isError.get())
                        binding.getError().isError.set(false);
                })
                //   .delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .flatMapCompletable(this::addToAdapter).toObservable()
                .onErrorReturn(throwable -> {
                    showErrorView(throwable.getMessage());
                    return true;
                });

        Disposable disposable = flowable.subscribe();
        compositeDisposable.add(disposable);
    }

    private Completable addAllToAdapter(GitHubInfo list) {
        return Completable.fromAction(() -> {
            if (isLoadToBD) {
                isLoadToBD = false;
                adapter.clear();
                adapter.addGitInfo(list);

                login = list.getLogin();
                name = list.getName();
                count = list.getRepos();
            }
        });
    }

    private Completable addToAdapter(GitHubInfo info) {
        return Completable.fromAction(() -> {
            adapter.clear();
            adapter.addGitInfo(info);
            viewModel.deleteAll();
            viewModel.insertItem(info);

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (compositeDisposable != null)
            compositeDisposable.clear();
    }

    private void showErrorView(String text) {
        adapter.clear();
        error.setErrorText(text);
        error.isError.set(true);
    }

    @Override
    public void onClick() {
        Intent intent = new Intent(MainActivity.this, RepositoryActivity.class);
        intent.putExtra(RepositoryActivity.ORG_NAME, name);
        intent.putExtra(RepositoryActivity.REPOS_COUNT, count);
        intent.putExtra(RepositoryActivity.ORG_LOGIN, login);
        startActivity(intent);
    }
}
