package com.example.vadym.testsuperdeal.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.vadym.testsuperdeal.R;
import com.example.vadym.testsuperdeal.dagger.DaggerGitHubActivityComponent;
import com.example.vadym.testsuperdeal.dagger.GitHubApplication;
import com.example.vadym.testsuperdeal.dagger.GitHubModule;
import com.example.vadym.testsuperdeal.databinding.ActivityRepositoryBinding;
import com.example.vadym.testsuperdeal.mvvm.GitHubViewModel;
import com.example.vadym.testsuperdeal.recycler.repository.DividerItem;

import javax.inject.Inject;

public class RepositoryActivity extends AppCompatActivity {

    public static final String ORG_NAME = "name";
    public static final String ORG_LOGIN = "login";
    public static final String REPOS_COUNT = "count";

    @Inject
    GitHubViewModel viewModel;

    private ActivityRepositoryBinding binding;
    private String name;
    private String count;
    private String login;
    private int page = 1;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_repository);

        DaggerGitHubActivityComponent.builder()
                .gitHubComponent(GitHubApplication.get(this).getGitHubComponent())
                .gitHubModule(new GitHubModule(this))
                .build()
                .inject(this);

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

        binding.setViewModel(viewModel);
        binding.setError(viewModel.model.getError());
        binding.recycler.addItemDecoration(new DividerItem(this));

        viewModel.setAllCount(count);
        viewModel.loadData(login, page);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }
}
