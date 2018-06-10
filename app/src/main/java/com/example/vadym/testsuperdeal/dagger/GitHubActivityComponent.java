package com.example.vadym.testsuperdeal.dagger;

import com.example.vadym.testsuperdeal.activities.MainActivity;
import com.example.vadym.testsuperdeal.activities.RepositoryActivity;

import dagger.Component;

@ActivityScope
@Component(dependencies = GitHubComponent.class,modules = GitHubModule.class)
public interface GitHubActivityComponent {
    void inject(MainActivity activity);
    void inject(RepositoryActivity activity);
}
