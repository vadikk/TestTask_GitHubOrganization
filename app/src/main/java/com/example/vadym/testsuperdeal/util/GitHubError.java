package com.example.vadym.testsuperdeal.util;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.ObservableBoolean;

import com.android.databinding.library.baseAdapters.BR;

public class GitHubError extends BaseObservable {

    private String errorText;

    @Bindable
    public final ObservableBoolean isError = new ObservableBoolean(false);
    @Bindable
    public final ObservableBoolean isLoading = new ObservableBoolean(false);


    public GitHubError() {
    }

    @Bindable
    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
        notifyPropertyChanged(BR.errorText);
    }
}
