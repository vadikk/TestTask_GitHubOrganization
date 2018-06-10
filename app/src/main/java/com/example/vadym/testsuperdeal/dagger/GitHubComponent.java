package com.example.vadym.testsuperdeal.dagger;

import android.app.Application;
import android.content.Context;

import com.example.vadym.testsuperdeal.activities.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ContextModule.class})
public interface GitHubComponent {
    void inject(GitHubApplication application);

    Application getApplication();
    Context getContext();
}
