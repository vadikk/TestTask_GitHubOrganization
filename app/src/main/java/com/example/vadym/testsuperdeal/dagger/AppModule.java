package com.example.vadym.testsuperdeal.dagger;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private GitHubApplication application;

    public AppModule(GitHubApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application getApplication(){
        return application;
    }
}
