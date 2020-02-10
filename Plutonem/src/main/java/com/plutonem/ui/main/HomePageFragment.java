package com.plutonem.ui.main;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.plutonem.HomePageFragmentPage;
import com.plutonem.R;


public class HomePageFragment extends Fragment {
    protected static final int TAB_COUNT = 1;
    public static final int TAB_POSITION_ALL = 0;

    private static final String KEY_LAST_TAB_POSITION = "lastTabPosition";

    private TabLayout mTabLayout;
    private int mLastTabPosition;

    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            setSelectedTab(savedInstanceState.getInt(KEY_LAST_TAB_POSITION, TAB_POSITION_ALL));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_page_fragment, container, false);

        mTabLayout = view.findViewById(R.id.tab_layout);
        mTabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                mLastTabPosition = tab.getPosition();
            }

            @Override
            public void onTabUnselected(Tab tab) {
            }

            @Override
            public void onTabReselected(Tab tab) {
            }
        });

        ViewPager viewPager = view.findViewById(R.id.plutonem_hpage_viewpager);
        viewPager.setAdapter(new HomePageFragmentAdapter(getChildFragmentManager()));
        viewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.margin_extra_large));
        mTabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setSelectedTab(mLastTabPosition);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_LAST_TAB_POSITION, mLastTabPosition);
        super.onSaveInstanceState(outState);
    }

    private void setSelectedTab(int position) {
        mLastTabPosition = position;

        if (mTabLayout != null) {
            TabLayout.Tab tab = mTabLayout.getTabAt(mLastTabPosition);

            if (tab != null) {
                tab.select();
            }
        }
    }

    private class HomePageFragmentAdapter extends FragmentPagerAdapter {
        HomePageFragmentAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return TAB_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            return HomePageFragmentPage.newInstance(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case TAB_POSITION_ALL:
                    return getString(R.string.everything);
                default:
                    return super.getPageTitle(position);
            }
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            try {
                super.restoreState(state, loader);
            } catch (IllegalStateException exception) {

            }
        }
    }
}
