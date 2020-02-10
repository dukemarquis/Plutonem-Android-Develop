package com.plutonem;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.plutonem.Components.ExoPlayerComponent;
import com.plutonem.models.Ex_Plutonem_MediaObject;
import com.plutonem.utilities.Ex_Plutonem_Resources;

import java.util.ArrayList;
import java.util.Arrays;

//class PlayerListener implements LifecycleObserver {
//    private SimpleExoPlayer Player;
//    public PlayerListener(Lifecycle lifecycle, SimpleExoPlayer Player) {
//        this.Player = Player;
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    void stop() {
//        // stop the playback
//        this.Player.setPlayWhenReady(false);
//    }
//}

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Plutonem_cdetail_card_fragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Plutonem_cdetail_card_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Plutonem_cdetail_card_fragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

//    private static final String TAG = "Plutonem_cdetail_card";

//    private enum VolumeState {ON, OFF};

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
//    private PlayerListener mPlayerListener;

    // ui
//    private PlayerView videoSurfaceView;
//    private SimpleExoPlayer videoPlayer;
    private ImageView thumbnail;
    private ProgressBar progressBar;
    private FrameLayout frameLayout;

    // vars
    private ArrayList<Ex_Plutonem_MediaObject> mediaObjects = new ArrayList<>();
    private Context context;
//    private boolean isVideoViewAdded;
    private RequestManager requestManager;

    // controlling playback state
//    private VolumeState volumeState;

    public Plutonem_cdetail_card_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Plutonem_cdetail_card_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Plutonem_cdetail_card_fragment newInstance(String param1, String param2) {

        Plutonem_cdetail_card_fragment fragment = new Plutonem_cdetail_card_fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;

//        Plutonem_cdetail_card_fragment f = new Plutonem_cdetail_card_fragment();
//
//        // Supply num input as an argument.
//        Bundle args = new Bundle();
//        args.putInt("num", num);
//        f.setArguments(args);
//
//        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        TextView textView = new TextView(getActivity());
//        textView.setText(R.string.hello_blank_fragment);
//        return textView;

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_cdetail_card, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        thumbnail = view.findViewById(R.id.plutonem_fragment_cdetail_card_imageview_thumbnail);
        progressBar = view.findViewById(R.id.plutonem_fragment_cdetail_card_imageview_progressbar);
        frameLayout = view.findViewById(R.id.plutonem_fragment_cdetail_card_framelayout);

        mediaObjects = new ArrayList<Ex_Plutonem_MediaObject>(Arrays.asList(Ex_Plutonem_Resources.MEDIA_OBJECTS));

        this.requestManager = initGlide();
        this.requestManager
                .load(mediaObjects.get(0).getThumbnail())
                .into(thumbnail);

        getLifecycle().addObserver(new ExoPlayerComponent(context, frameLayout, progressBar, thumbnail, mediaObjects));

//        initCdetailCardVideo();

//        mPlayerListener = new PlayerListener(getLifecycle(), this.videoPlayer);
    }

//    private void initCdetailCardVideo() {
//
//        this.context = getActivity().getApplicationContext();
//
//        videoSurfaceView = new PlayerView(this.context);
//        videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
//
//        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
//        TrackSelection.Factory videoTrackSelectionFactory =
//                new AdaptiveTrackSelection.Factory(bandwidthMeter);
//        TrackSelector trackSelector =
//                new DefaultTrackSelector(videoTrackSelectionFactory);
//
//        // 2. Create the player
//        videoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
//        // Bind the player to the view.
//        videoSurfaceView.setUseController(false);
//        videoSurfaceView.setPlayer(videoPlayer);
//        setVolumeControl(VolumeState.ON);
//
//        videoPlayer.addListener(new Player.EventListener() {
//            @Override
//            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
//
//            }
//
//            @Override
//            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//
//            }
//
//            @Override
//            public void onLoadingChanged(boolean isLoading) {
//
//            }
//
//            @Override
//            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                switch (playbackState) {
//
//                    case Player.STATE_BUFFERING:
//                        Log.e(TAG, "onPlayerStateChanged: Buffering video.");
//                        if (progressBar != null) {
//                            progressBar.setVisibility(VISIBLE);
//                        }
//
//                        break;
//                    case Player.STATE_ENDED:
//                        Log.d(TAG, "onPlayerStateChanged: Video ended.");
//                        videoPlayer.seekTo(0);
//                        break;
//                    case Player.STATE_IDLE:
//
//                        break;
//                    case Player.STATE_READY:
//                        Log.e(TAG, "onPlayerStateChanged: Ready to play.");
//                        if (progressBar != null) {
//                            progressBar.setVisibility(GONE);
//                        }
//                        if(!isVideoViewAdded){
//                            addVideoView();
//                        }
//                        break;
//                    default:
//                        break;
//                }
//            }
//
//            @Override
//            public void onRepeatModeChanged(int repeatMode) {
//
//            }
//
//            @Override
//            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
//
//            }
//
//            @Override
//            public void onPlayerError(ExoPlaybackException error) {
//
//            }
//
//            @Override
//            public void onPositionDiscontinuity(int reason) {
//
//            }
//
//            @Override
//            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//
//            }
//
//            @Override
//            public void onSeekProcessed() {
//
//            }
//        });
//
//        playVideo();
//    }

//    public void playVideo() {
//
//        if (videoSurfaceView == null) {
//            return;
//        }
//
//        videoSurfaceView.setPlayer(videoPlayer);
//
//        // we do not need the Volume function, so disable it.
//
////        viewHolderParent.setOnClickListener(videoViewClickListener);
//
//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
//                context, utilities.getUserAgent(context, "RecyclerView VideoPlayer"));
//        String mediaUrl = mediaObjects.get(0).getMedia_url();
//        if (mediaUrl != null) {
//            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(Uri.parse(mediaUrl));
//            videoPlayer.prepare(videoSource);
//            videoPlayer.setPlayWhenReady(true);
//        }
//    }

//    private void setVolumeControl(VolumeState state){
//        volumeState = state;
//        if(state == VolumeState.OFF){
//            videoPlayer.setVolume(0f);
//            animateVolumeControl();
//        }
//        else if(state == VolumeState.ON){
//            videoPlayer.setVolume(1f);
//            animateVolumeControl();
//        }
//    }

//    private void animateVolumeControl(){
//        if(volumeControl != null){
//            volumeControl.bringToFront();
//            if(volumeState == VolumeState.OFF){
//                requestManager.load(R.drawable.ic_volume_off_grey_24dp)
//                        .into(volumeControl);
//            }
//            else if(volumeState == VolumeState.ON){
//                requestManager.load(R.drawable.ic_volume_up_grey_24dp)
//                        .into(volumeControl);
//            }
//            volumeControl.animate().cancel();
//
//            volumeControl.setAlpha(1f);
//
//            volumeControl.animate()
//                    .alpha(0f)
//                    .setDuration(600).setStartDelay(1000);
//        }
//    }

//    private void addVideoView(){
//        frameLayout.addView(videoSurfaceView);
//        isVideoViewAdded = true;
//        videoSurfaceView.requestFocus();
//        videoSurfaceView.setVisibility(VISIBLE);
//        videoSurfaceView.setAlpha(1);
//        thumbnail.setVisibility(GONE);
//    }

    private RequestManager initGlide(){

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

//    @Override
//    public void onResume() {
//
//        if (videoPlayer !=null) {
//            videoPlayer.setPlayWhenReady(true);
//        }
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//
//        if (videoPlayer != null) {
//            videoPlayer.setPlayWhenReady(false);
//        }
//        super.onPause();
//    }


//    @Override
//    public void onDestroy() {
//
////        if (videoPlayer != null) {
////            videoPlayer.release();
////            videoPlayer = null;
////        }
//        super.onDestroy();
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
