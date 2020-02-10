package com.plutonem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.button.MaterialButton;
import com.plutonem.order_option.PlutonemCdetailAttributeFragment;

public class Plutonem_cdetail extends AppCompatActivity implements Plutonem_cdetail_card_fragment.OnFragmentInteractionListener {

    /**
     * The number of pages to show.
     */
    private static final int NUM_PAGES = 1;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.plutonem_activity_cdetail);
        }
        catch (Exception ex)
        {
            Log.e("Error on", ex.getMessage());
        }

        // Instantiate a ViewPager and a PagerAdapter.

        try {
            mPager = findViewById(R.id.plutonem_cdetail_viewpager);
            pagerAdapter = new PlutonemCdetailPagerAdapter(getSupportFragmentManager());
            mPager.setAdapter(pagerAdapter);
        }
        catch (Exception ex)
        {
            Log.e("Error on", ex.getMessage());
        }

        BottomAppBar bar = findViewById(R.id.plutonem_cdetail_bottomappbar);
        setSupportActionBar(bar);

        MaterialButton materialButton = findViewById(R.id.plutonem_cdetail_button);
        materialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(), "请私信我们下单哈 : 13581509626", Toast.LENGTH_SHORT).show();
//                PlutonemCdetailAttributeFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
                new PlutonemCdetailAttributeFragment().show(getSupportFragmentManager(),"dialog");
            }
        });

//        FloatingActionButton fab = findViewById(R.id.plutonem_cdetail_floatingactionbutton_shop);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                PlutonemCdetailAttributeFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
//            }
//        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.plutonem_activity_cdetail, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.store:
//                Toast.makeText(this, "store is clicked!", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.customer_service:
//                Toast.makeText(this, "customer service is clicked", Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.add_to_shopping_cart:
//                Toast.makeText(this, "add to shopping cart is clicked", Toast.LENGTH_SHORT).show();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 2 Plutonem_cdetail_card_fragment objects, in
     * sequence.
     */
    private class PlutonemCdetailPagerAdapter extends FragmentStatePagerAdapter {

        public PlutonemCdetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

//            return new Plutonem_cdetail_card_fragment();

            return Plutonem_cdetail_card_fragment.newInstance("", "");
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

//    @Override
//    public void onAttachFragment(Fragment fragment) {
//        if (fragment instanceof Plutonem_cdetail_card_fragment) {
//            Plutonem_cdetail_card_fragment commodityDetailPageCardFragment = (Plutonem_cdetail_card_fragment) fragment;
//            commodityDetailPageCardFragment.setOnHeadlineSelectedListener(this);
//        }
//    }

    public void onFragmentInteraction(Uri uri) { }
}
