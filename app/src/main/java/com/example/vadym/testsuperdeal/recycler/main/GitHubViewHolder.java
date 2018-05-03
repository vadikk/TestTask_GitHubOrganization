package com.example.vadym.testsuperdeal.recycler.main;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.vadym.testsuperdeal.databinding.RowLayoutBinding;
import com.example.vadym.testsuperdeal.model.GitHubInfo;
import com.squareup.picasso.Picasso;

public class GitHubViewHolder extends RecyclerView.ViewHolder {

    public final RowLayoutBinding binding;

    public GitHubViewHolder(View itemView) {
        super(itemView);
        this.binding = DataBindingUtil.bind(itemView);
    }

    @BindingAdapter("bind:imageUrl")
    public static void loadImage(ImageView imageView, String url) {
        Picasso.with(imageView.getContext()).load(url).into(imageView);
    }

    public void setBinding(GitHubInfo info) {
        binding.setGit(info);
    }
}
