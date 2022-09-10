package com.serhatozturk.instagramclone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.serhatozturk.instagramclone.databinding.RecyclerRowBinding;
import com.serhatozturk.instagramclone.model.post;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private ArrayList<post> postArrayList;

    public PostAdapter(ArrayList<post>postArrayList){
        this.postArrayList=postArrayList;
    }


    @NonNull
    @Override

    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerRowBinding recyclerRowBinding= RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new PostHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        holder.recyclerRowBinding.recyclerUserEmailText.setText(postArrayList.get(position).email);
        holder.recyclerRowBinding.recyclerCommentText.setText(postArrayList.get(position).comment);
      //  holder.recyclerRowBinding.recyclerUserEmailText.setText(postArrayList.get(position).downloadUrl);
        Picasso.get().load(postArrayList.get(position).downloadUrl).into(holder.recyclerRowBinding.recyclerViewImageView);



    }

    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        RecyclerRowBinding recyclerRowBinding;



        public PostHolder(RecyclerRowBinding recyclerRowBinding) {


            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding=recyclerRowBinding;
        }
    }


}
