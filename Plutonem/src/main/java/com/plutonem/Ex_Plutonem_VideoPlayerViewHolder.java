package com.plutonem;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.plutonem.models.Ex_Plutonem_MediaObject;

public class Ex_Plutonem_VideoPlayerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView commodity_name, commodity_price;
    private View parent;
    private Ex_Plutonem_VideoPlayerRecyclerAdapter.onNoteListener onNoteListener;
    ImageView thumbnail, volumeControl;
    ProgressBar progressBar;
    RequestManager requestManager;


    Ex_Plutonem_VideoPlayerViewHolder(@NonNull View itemView, Ex_Plutonem_VideoPlayerRecyclerAdapter.onNoteListener onNoteListener) {

        super(itemView);
        parent = itemView;
        FrameLayout media_container = itemView.findViewById(R.id.media_container);
        thumbnail = itemView.findViewById(R.id.thumbnail);
        progressBar = itemView.findViewById(R.id.progressBar);
        volumeControl = itemView.findViewById(R.id.volume_control);
        commodity_name = itemView.findViewById(R.id.plutonem_hpage_card_textview_cname);
        commodity_price = itemView.findViewById(R.id.plutonem_hpage_card_textview_cprice);

        this.onNoteListener = onNoteListener;
        itemView.setOnClickListener(this);
    }

    void onBind(Ex_Plutonem_MediaObject mediaObject, RequestManager requestManager) {
        this.requestManager = requestManager;
        parent.setTag(this);
        commodity_name.setText(mediaObject.getCommodity_name());
        commodity_price.setText(mediaObject.getCommodity_price());
        this.requestManager
                .load(mediaObject.getThumbnail())
                .into(thumbnail);
    }

    @Override
    public void onClick(View view) {
        onNoteListener.onNoteClick(getAdapterPosition());
    }
}