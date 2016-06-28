package com.example.elzatom.potholefinder.views;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.elzatom.potholefinder.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!= null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);

        }
    }
    public void addReport(View button){
        Intent addReportIntent = new Intent(this,AddReportActivity.class);
        startActivity(addReportIntent);

    }
    public void showReports(View button){

        Intent listViewIntent = new Intent(this,ShowReportsActivity.class);
        listViewIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(listViewIntent);
        overridePendingTransition (0, 0);


    }
}
