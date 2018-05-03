package com.example.vadym.testsuperdeal.recycler.main;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.vadym.testsuperdeal.R;
import com.example.vadym.testsuperdeal.databinding.RowLayoutBinding;
import com.example.vadym.testsuperdeal.model.GitHubInfo;
import com.example.vadym.testsuperdeal.util.OnRepositoryClickListener;

import java.util.ArrayList;
import java.util.List;

public class GitHubAdapter extends RecyclerView.Adapter<GitHubViewHolder> {

    private List<GitHubInfo> list = new ArrayList<>();
    private OnRepositoryClickListener listener;

    public GitHubAdapter() {
    }

    public void setOnRepositoryListener(OnRepositoryClickListener listener) {
        this.listener = listener;
    }

    public void addAllToList(List<GitHubInfo> lists) {
        int startPos = getItemCount();
        notifyItemRangeInserted(startPos, lists.size());
        list.addAll(lists);
    }

    public void addGitInfo(GitHubInfo info) {
        notifyItemInserted(getItemCount() - 1);
        list.add(info);
    }

    public void clear() {
        notifyItemRangeRemoved(0, getItemCount());
        list.clear();
    }

    @NonNull
    @Override
    public GitHubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RowLayoutBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_layout, parent, false);
        return new GitHubViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull GitHubViewHolder holder, int position) {
        GitHubInfo info = list.get(holder.getAdapterPosition());
        if (info != null) {
            holder.setBinding(info);
            if (holder.binding != null)
                holder.binding.setClick(this::onClickRepository);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void onClickRepository() {
        if (listener != null) listener.onClick();
    }
}
