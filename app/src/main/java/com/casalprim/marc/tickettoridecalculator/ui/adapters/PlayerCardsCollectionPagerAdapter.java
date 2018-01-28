package com.casalprim.marc.tickettoridecalculator.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.casalprim.marc.tickettoridecalculator.ui.fragments.PlayerCardsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marc on 17/01/18.
 */

public class PlayerCardsCollectionPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();


    public PlayerCardsCollectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
        this.notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void notifyFragments() {
        for (Fragment fragment : mFragmentList) {
            if (fragment instanceof PlayerCardsFragment) {
                ((PlayerCardsFragment) fragment).notifyChangeInDataset();
            }
        }
    }
}
