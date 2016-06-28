package com.example.elzatom.potholefinder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.example.elzatom.potholefinder.fragments.MapViewFragment;
import com.example.elzatom.potholefinder.fragments.TableViewReportFragment;

/**
 * Created by elzatom on 3/5/16.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                TableViewReportFragment listTab = new TableViewReportFragment();
                return listTab;
            case 1:
                MapViewFragment mapTab = new MapViewFragment();

                return mapTab;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
