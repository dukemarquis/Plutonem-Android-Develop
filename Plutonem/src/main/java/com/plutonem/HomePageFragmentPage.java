package com.plutonem;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.plutonem.Ex_Plutonem_VideoPlayerRecyclerAdapter.onNoteListener;
import com.plutonem.models.Ex_Plutonem_MediaObject;
import com.plutonem.utilities.Ex_Plutonem_Resources;

import java.util.ArrayList;
import java.util.Arrays;

import static com.plutonem.ui.main.HomePageFragment.TAB_POSITION_ALL;

public class HomePageFragmentPage extends Fragment implements onNoteListener {
    private static final String KEY_TAB_POSITION = "tabPosition";

    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private Ex_Plutonem_VideoPlayerRecyclerAdapter mAdapter;
    private Ex_Plutonem_VideoPlayerRecyclerView mRecyclerView;
    private ArrayList<Ex_Plutonem_MediaObject> mMediaObjects;
    private int mTabPosition;

    public static Fragment newInstance(int position) {
        HomePageFragmentPage fragment = new HomePageFragmentPage();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TAB_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setAdapter(getAdapter());

        if (savedInstanceState != null) {
            mTabPosition = savedInstanceState.getInt(KEY_TAB_POSITION, TAB_POSITION_ALL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hpage_dashboard, container, false);

        if (getArguments() != null) {
            mTabPosition = getArguments().getInt(KEY_TAB_POSITION, TAB_POSITION_ALL);
        }

        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView = view.findViewById(R.id.homepage_list);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mRecyclerView.setMediaObjects(getMediaObjects());

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_TAB_POSITION, mTabPosition);
        super.onSaveInstanceState(outState);
    }

    private ArrayList<Ex_Plutonem_MediaObject> getMediaObjects() {
        if (mMediaObjects == null) {
            mMediaObjects = new ArrayList<>(Arrays.asList(Ex_Plutonem_Resources.MEDIA_OBJECTS));
        }

        return mMediaObjects;
    }

    private RequestManager initGlide() {

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.white_background)
                .error(R.drawable.white_background);

        return Glide.with(this)
                .setDefaultRequestOptions(options);
    }

    private Ex_Plutonem_VideoPlayerRecyclerAdapter getAdapter() {
        if (mAdapter == null) {
            mAdapter = new Ex_Plutonem_VideoPlayerRecyclerAdapter(getMediaObjects(), initGlide(), this);
        }

        return mAdapter;
    }

    @Override
    public void onDestroy() {
        if (mRecyclerView != null)
            mRecyclerView.releasePlayer();
        super.onDestroy();
    }

    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(getActivity(), Plutonem_cdetail.class);
        startActivity(intent);
    }
}
