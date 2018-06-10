package com.example.vadym.testsuperdeal.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.RelativeLayout;

import com.example.vadym.testsuperdeal.R;
import com.example.vadym.testsuperdeal.dagger.DaggerGitHubActivityComponent;
import com.example.vadym.testsuperdeal.dagger.GitHubApplication;
import com.example.vadym.testsuperdeal.dagger.GitHubModule;
import com.example.vadym.testsuperdeal.databinding.ActivityMainBinding;
import com.example.vadym.testsuperdeal.mvvm.GitHubViewModel;
import com.example.vadym.testsuperdeal.mvvm.IView;
import com.example.vadym.testsuperdeal.util.OnRepositoryClickListener;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements OnRepositoryClickListener, IView {

    @Inject
    GitHubViewModel viewModel;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        DaggerGitHubActivityComponent.builder()
                .gitHubComponent(GitHubApplication.get(this).getGitHubComponent())
                .gitHubModule(new GitHubModule(this))
                .build()
                .inject(this);

        binding.setViewModel(viewModel);
        binding.setError(viewModel.model.getError());
        binding.editText.addTextChangedListener(viewModel.textWatcher);

        viewModel.setiView(this);
        viewModel.adapter.setOnRepositoryListener(this);
        viewModel.subscribeUIMovie();
    }

    @Override
    public void setTextViewParams(int pos) {
        RelativeLayout.LayoutParams rel = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rel.setMargins(0, pos, 0, 0);
        binding.title.setGravity(Gravity.CENTER_HORIZONTAL);
        binding.title.setLayoutParams(rel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.onDestroy();
    }

    @Override
    public void onClick() {
        Intent intent = new Intent(MainActivity.this, RepositoryActivity.class);
        intent.putExtra(RepositoryActivity.ORG_NAME, GitHubViewModel.name);
        intent.putExtra(RepositoryActivity.REPOS_COUNT, GitHubViewModel.count);
        intent.putExtra(RepositoryActivity.ORG_LOGIN, GitHubViewModel.login);
        startActivity(intent);
    }
}
