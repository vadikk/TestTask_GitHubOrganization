package com.example.vadym.testsuperdeal.mvvm;

import android.arch.lifecycle.ViewModel;
import android.databinding.BindingAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;

import com.example.vadym.testsuperdeal.model.GitHubInfo;
import com.example.vadym.testsuperdeal.model.GitHubRepository;
import com.example.vadym.testsuperdeal.recycler.main.GitHubAdapter;
import com.example.vadym.testsuperdeal.recycler.repository.RepositoryAdapter;
import com.example.vadym.testsuperdeal.retrofit.GitHubRetrofit;
import com.example.vadym.testsuperdeal.util.MyTextWatcher;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class GitHubViewModel extends ViewModel {

    public static String name;
    public static String login;
    public static String count;

    private IView iView;
    private boolean isLoadToBD = true;
    private int page = 1;
    private String allCount;

    @Inject
    public Model model;
    @Inject
    public GitHubAdapter adapter;
    @Inject
    public LinearLayoutManager manager;
    @Inject
    public RepositoryAdapter adapterRepository;
    @Inject
    CompositeDisposable compositeDisposable;

    public MyTextWatcher textWatcher = new MyTextWatcher() {

        Timer t = new Timer();

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.length() >= 3) {
                iView.setTextViewParams(30);

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        loadData(editable.toString());
                    }
                };

                t.cancel();
                t = new Timer();
                t.schedule(task, 1000);


            } else if (editable.length() <= 2) {
                t.cancel();
                iView.setTextViewParams(125);
                model.getError().isError.set(false);
                adapter.clear();
            }
        }
    };

    public RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (model.getError().isLoading.get()) return;

            int lastElement = manager.findLastCompletelyVisibleItemPosition();
            int loadedItemCount = adapterRepository.getItemCount();
            int loadShouldStartPosition = (int) (loadedItemCount * 0.8);

            if (loadShouldStartPosition <= lastElement && loadedItemCount < Integer.valueOf(allCount)) {
                page++;
                model.getError().isLoading.set(true);
            }

            if (model.getError().isLoading.get()) {
                loadData(login, page);
            }
        }
    };

    @Inject
    public GitHubViewModel(Model model, LinearLayoutManager manager, GitHubAdapter adapter,
                           RepositoryAdapter adapterRepository, CompositeDisposable compositeDisposable) {
        this.model = model;
        this.adapter = adapter;
        this.compositeDisposable = compositeDisposable;
        this.manager = manager;
        this.adapterRepository = adapterRepository;
    }

    @BindingAdapter("bind:items")
    public static void setAdapters(RecyclerView view, RecyclerView.Adapter adapter) {
        view.setAdapter(adapter);
    }

    @BindingAdapter("bind:manager")
    public static void setManager(RecyclerView view, LinearLayoutManager manager1) {
        view.setLayoutManager(manager1);
    }

    @BindingAdapter("bind:addScrollListener")
    public static void setScrollListener(RecyclerView view, RecyclerView.OnScrollListener listener) {
        view.addOnScrollListener(listener);
    }

    public void setiView(IView iView) {
        this.iView = iView;
    }

    public void setAllCount(String number) {
        allCount = number;
    }

    public void subscribeUIMovie() {
        Flowable getList = model.getDbViewModel().getOrganizations()
                .flatMap(Flowable::just)
                .flatMap(Flowable::fromIterable)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapCompletable(this::addFromBDToAdapter).toFlowable();

        Disposable disposable1 = getList.subscribe();
        compositeDisposable.add(disposable1);

    }

    private void loadData(String query) {
        model.getError().isLoading.set(true);

        Observable flowable = GitHubRetrofit.getRetrofit().getGitHubResponce(query)
                .doOnNext(info -> {
                    login = info.getLogin();
                    name = info.getName();
                    count = info.getRepos();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    if (model.getError().isError.get())
                        model.getError().isError.set(false);
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

    public void loadData(String nameOrg, int pageNumber) {
        model.getError().isLoading.set(true);

        Flowable flowable = GitHubRetrofit.getRetrofit().getGitHubRepository(nameOrg, pageNumber)
                .doOnComplete(() -> {
                    if (model.getError().isError.get())
                        model.getError().isError.set(false);
                })
                .filter(gitHubRepositiries -> !gitHubRepositiries.isEmpty())
                .delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .flatMapCompletable(this::addToAdapterRepository).toFlowable()
                .onErrorReturn(throwable -> {
                    showErrorViewRepository(throwable.getMessage());
                    return true;
                });
        Disposable disposable = flowable.subscribe();
        compositeDisposable.add(disposable);
    }

    private void showErrorView(String text) {
        adapter.clear();
        model.getError().setErrorText(text);
        model.getError().isError.set(true);
    }

    private void showErrorViewRepository(String text) {
        adapterRepository.clear();
        model.getError().setErrorText(text);
        model.getError().isError.set(true);
        model.getError().isLoading.set(false);
    }

    private Completable addToAdapter(GitHubInfo info) {
        return Completable.fromAction(() -> {
            adapter.clear();
            adapter.addGitInfo(info);
            model.getDbViewModel().deleteAll();
            model.getDbViewModel().insertItem(info);

        });
    }

    private Completable addToAdapterRepository(List<GitHubRepository> info) {
        return Completable.fromAction(() -> {
            model.getError().isLoading.set(false);
            adapterRepository.addAllToList(info);
        });
    }

    private Completable addFromBDToAdapter(GitHubInfo list) {
        return Completable.fromAction(() -> {
            if (isLoadToBD) {
                isLoadToBD = false;
                this.adapter.clear();
                this.adapter.addGitInfo(list);

                login = list.getLogin();
                name = list.getName();
                count = list.getRepos();
            }
        });
    }

    public void onDestroy() {
        if (compositeDisposable != null)
            compositeDisposable.clear();
    }
}
