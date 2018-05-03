package com.example.vadym.testsuperdeal.recycler.repository;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.vadym.testsuperdeal.databinding.RepositoryRowBinding;
import com.example.vadym.testsuperdeal.model.GitHubRepository;

public class RepositoryViewHolder extends RecyclerView.ViewHolder {

    private RepositoryRowBinding binding;

    public RepositoryViewHolder(View itemView) {
        super(itemView);
        binding = DataBindingUtil.bind(itemView);
    }

    public void setBinding(GitHubRepository repository) {
        binding.setRepository(repository);
        if (repository.getDescription() != null)
            binding.getRepository().isShow.set(true);
        else
            binding.getRepository().isShow.set(false);
    }
}
