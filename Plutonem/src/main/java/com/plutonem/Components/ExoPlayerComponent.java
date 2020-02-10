package com.plutonem.Components;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.plutonem.models.Ex_Plutonem_MediaObject;

import java.util.ArrayList;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class ExoPlayerComponent implements LifecycleObserver {

    private static final String TAG = "ExoPlayer";

    // ui
    private ProgressBar progressBar;
    private FrameLayout frameLayout;
    private ImageView thumbnail;
    private PlayerView videoSurfaceView;
    private SimpleExoPlayer videoPlayer;

    // vars
    private final Context context;
    private boolean isVideoViewAdded;
    private ArrayList<Ex_Plutonem_MediaObject> mediaObjects = new ArrayList<>();

    public ExoPlayerComponent(Context context, FrameLayout frameLayout, ProgressBar progressBar, ImageView thumbnail, ArrayList<Ex_Plutonem_MediaObject> mediaObjects) {
        this.context = context;
        this.frameLayout = frameLayout;
        this.progressBar = progressBar;
        this.thumbnail = thumbnail;
        this.mediaObjects = mediaObjects;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart(){
        if (videoPlayer == null) {
            initExoPlayer();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume(){
        if (videoPlayer != null && videoPlayer.getPlaybackState() == STATE_READY) {
            videoPlayer.setPlayWhenReady(true);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause(){
        if (videoPlayer != null && videoPlayer.getPlaybackState() == STATE_READY) {
            videoPlayer.setPlayWhenReady(false);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(){
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
    }

    private void initExoPlayer(){
        videoSurfaceView = new PlayerView(this.context);
        videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        // 2. Create the player
        videoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        // Bind the player to the view.
        videoSurfaceView.setUseController(false);
        videoSurfaceView.setPlayer(videoPlayer);

        videoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {

                    case Player.STATE_BUFFERING:
                        Log.e(TAG, "onPlayerStateChanged: Buffering video.");
                        if (progressBar != null) {
                            progressBar.setVisibility(VISIBLE);
                        }

                        break;
                    case Player.STATE_ENDED:
                        Log.d(TAG, "onPlayerStateChanged: Video ended.");
                        videoPlayer.seekTo(0);
                        break;
                    case Player.STATE_IDLE:

                        break;
                    case STATE_READY:
                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");
                        if (progressBar != null) {
                            progressBar.setVisibility(GONE);
                        }
                        if(!isVideoViewAdded){
                            addVideoView();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        playVideo();
    }

    private void playVideo() {

        if (videoSurfaceView == null) {
            return;
        }

        videoSurfaceView.setPlayer(videoPlayer);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                context, Util.getUserAgent(context, "RecyclerView VideoPlayer"));
        String mediaUrl = mediaObjects.get(0).getMedia_url();
        if (mediaUrl != null) {
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(mediaUrl));
            videoPlayer.prepare(videoSource);
            videoPlayer.setPlayWhenReady(true);
        }
    }

    private void addVideoView(){
        frameLayout.addView(videoSurfaceView);
        isVideoViewAdded = true;
        videoSurfaceView.requestFocus();
        videoSurfaceView.setVisibility(VISIBLE);
        videoSurfaceView.setAlpha(1);
        thumbnail.setVisibility(GONE);
    }
}
