package com.example.justgranite;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

public class TabNavigationFragment extends Fragment {

    RiverSectionPagerAdapter riverSectionPagerAdapter;
    ViewPager viewPager;

    public static TabNavigationFragment newInstance() {
        return new TabNavigationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_navigation_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        riverSectionPagerAdapter = new RiverSectionPagerAdapter(getChildFragmentManager());
        viewPager = view.findViewById(R.id.pager);
        viewPager.setAdapter(riverSectionPagerAdapter);

        // set the tab to the default layout
        TinyDB tinyDB = new TinyDB(getContext());
        int defaultPosition;
        try {
            defaultPosition = tinyDB.getInt("defaultGauge");
            viewPager.setCurrentItem(defaultPosition);
        }
        catch (NullPointerException e){
            // default position doesn't exist so just do nothing and stay at the first tab
        }

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // store current tab as default choice for widget
                TinyDB tinyDB = new TinyDB(getContext());
                int currentPosition = tab.getPosition();
                tinyDB.putInt("defaultGauge",currentPosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public class RiverSectionPagerAdapter extends FragmentPagerAdapter {

        public RiverSectionPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new RiverSectionFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return RiverSectionJsonUtil.getCount(getContext());
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            RiverSection riverSection = RiverSectionJsonUtil.getRiverSection(getContext(), position);
            return riverSection.getSection_name();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
