package com.plutonem;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.plutonem.models.Ex_Plutonem_MediaObject;

import java.util.ArrayList;

public class Ex_Plutonem_VideoPlayerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Ex_Plutonem_MediaObject> mediaObjects;
    private RequestManager requestManager;
    private onNoteListener mOnNoteListener;

    Ex_Plutonem_VideoPlayerRecyclerAdapter(ArrayList<Ex_Plutonem_MediaObject> mediaObjects, RequestManager requestManager, Ex_Plutonem_VideoPlayerRecyclerAdapter.onNoteListener onNoteListener) {
        this.mediaObjects = mediaObjects;
        this.requestManager = requestManager;
        this.mOnNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new Ex_Plutonem_VideoPlayerViewHolder(
                LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hpage_card, viewGroup, false), mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((Ex_Plutonem_VideoPlayerViewHolder)viewHolder).onBind(mediaObjects.get(i), requestManager);
    }

    @Override
    public int getItemCount() {
        return mediaObjects.size();
    }

    public interface onNoteListener {
        void onNoteClick(int position);
    }
}
