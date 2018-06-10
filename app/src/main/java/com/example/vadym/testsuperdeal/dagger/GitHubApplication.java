package com.example.vadym.testsuperdeal.dagger;

import android.app.Application;
import android.content.Context;

public class GitHubApplication extends Application {

    private GitHubComponent gitHubComponent;

    public static GitHubApplication get(Context context){
        return (GitHubApplication)context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        gitHubComponent = DaggerGitHubComponent.builder()
                .appModule(new AppModule(this))
                .contextModule(new ContextModule(this))
                .build();
    }

    public GitHubComponent getGitHubComponent() {
        return gitHubComponent;
    }
}
