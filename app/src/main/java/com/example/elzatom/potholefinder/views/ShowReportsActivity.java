package com.example.elzatom.potholefinder.views;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.elzatom.potholefinder.R;
import com.example.elzatom.potholefinder.adapter.PagerAdapter;
import com.example.elzatom.potholefinder.fragments.MapViewFragment;
import com.example.elzatom.potholefinder.fragments.TableViewReportFragment;
import com.example.elzatom.potholefinder.model.Pothole;

import java.util.List;

public class ShowReportsActivity extends ActionBarActivity implements TableViewReportFragment.OnPotholeSelectedListener,MapViewFragment.OnMarkerSelectedListener {


    String TabFragmentB;
    public void setTabFragmentB(String t){
        TabFragmentB = t;
    }
    public String getTabFragmentB(){
        return TabFragmentB;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reports);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("ListView"));
        tabLayout.addTab(tabLayout.newTab().setText("MapView"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onPotholeSelected(int id, List<Pothole> listPothole) {
        Intent detailViewIntent = new Intent(this,DetailedReportActivity.class);

        detailViewIntent.putExtra("user", listPothole.get(id).getId());
        detailViewIntent.putExtra("latitude", listPothole.get(id).getLatitude());
        detailViewIntent.putExtra("longitude",listPothole.get(id).getLongitude());
        detailViewIntent.putExtra("desciption", listPothole.get(id).getDescription());
        detailViewIntent.putExtra("type", listPothole.get(id).getImageType());
        startActivity(detailViewIntent);

    }
    @Override
    public void onMarkerSelected(int id, List<Pothole> listPothole) {

        Intent detailViewIntent = new Intent(this,DetailedReportActivity.class);

        detailViewIntent.putExtra("user",listPothole.get(id).getId());
        detailViewIntent.putExtra("type",listPothole.get(id).getImageType());
        detailViewIntent.putExtra("latitude", listPothole.get(id).getLatitude());
        detailViewIntent.putExtra("longitude",listPothole.get(id).getLongitude());
        detailViewIntent.putExtra("desciption",listPothole.get(id).getDescription());
        startActivity(detailViewIntent);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                return true;
            default:
                break;
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}