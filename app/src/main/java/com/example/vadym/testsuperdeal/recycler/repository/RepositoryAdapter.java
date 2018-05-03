package com.example.vadym.testsuperdeal.recycler.repository;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.vadym.testsuperdeal.R;
import com.example.vadym.testsuperdeal.databinding.RepositoryRowBinding;
import com.example.vadym.testsuperdeal.model.GitHubRepository;

import java.util.ArrayList;
import java.util.List;

public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryViewHolder> {

    private List<GitHubRepository> list = new ArrayList<>();
    private Context context;

    public RepositoryAdapter(Context context) {
        this.context = context;
    }

    public void addAllToList(List<GitHubRepository> lists) {
        int startPos = getItemCount();
        notifyItemRangeInserted(startPos, lists.size());
        list.addAll(lists);
    }

    public void clear() {
        notifyItemRangeRemoved(0, getItemCount());
        list.clear();
    }

    @NonNull
    @Override
    public RepositoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RepositoryRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.repository_row, parent, false);
        return new RepositoryViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RepositoryViewHolder holder, int position) {
        GitHubRepository repositiry = list.get(holder.getAdapterPosition());
        Animation shake = AnimationUtils.loadAnimation(context,R.anim.shake);
        if (repositiry != null) {
            holder.setBinding(repositiry);
            holder.itemView.setOnClickListener(view -> {
                holder.itemView.startAnimation(shake);
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
