package com.example.elzatom.potholefinder.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;

import com.example.elzatom.potholefinder.R;
import com.example.elzatom.potholefinder.adapter.CustomListAdapter;
import com.example.elzatom.potholefinder.model.Pothole;
import com.example.elzatom.potholefinder.views.ShowReportsActivity;
import com.example.elzatom.potholefinder.volley.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class TableViewReportFragment extends Fragment  {

    OnPotholeSelectedListener mListener;
    List<Pothole> listPothole = new ArrayList<Pothole>();
    ProgressDialog progress;
    private ListView listView;
    private CustomListAdapter adapter;
    Activity activityFrag;
    String TabOfFragmentB;
    private RequestQueue requestQueue;
    ThreadForFetch thread;
    JSONArray response;
    int batchNumber=0;
    public TableViewReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.list);
        adapter = new CustomListAdapter(getActivity(), listPothole);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

                mListener.onPotholeSelected(position, listPothole);
            }
        });
        thread = new ThreadForFetch();
        thread.execute();
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
        return view;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activityFrag= activity;
        try {
            mListener = (OnPotholeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnURLSelectedListener");
        }
    }

    public interface OnPotholeSelectedListener {
        void onPotholeSelected(int id,List<Pothole> listPothole);
    }
    private class ThreadForFetch extends AsyncTask<Void, JSONArray, Void> {

        private VolleySingleton volleySingleton;

        public ThreadForFetch() {

            volleySingleton = VolleySingleton.getInstance();
            requestQueue = volleySingleton.getRequestQueue();
        }
        @Override
        protected Void doInBackground(Void... params) {


            response = sendJsonRequest(requestQueue);
            publishProgress(response);
            while (response!= null && response.length()!= 0) {
                response = sendJsonRequest(requestQueue);
                publishProgress(response);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(JSONArray... values) {
            super.onProgressUpdate(values);
            parseJSONResponse(values[0]);
            adapter.notifyDataSetChanged();
            progress.dismiss();
        }
    }

    private JSONArray sendJsonRequest(RequestQueue requestQueue) {
         response = null;
        RequestFuture<JSONArray> requestFuture = RequestFuture.newFuture();

        JsonArrayRequest request = new JsonArrayRequest("http://bismarck.sdsu.edu/city/batch?type=street&size=10&batch-number="+batchNumber,
                 requestFuture, requestFuture);
        request.setTag("My Tag");
        requestQueue.add(request);
         batchNumber++;
        try {
            response = requestFuture.get(15, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
           e.printStackTrace();


        } catch (ExecutionException e) {
            e.printStackTrace();


        } catch (TimeoutException e) {
            e.printStackTrace();
            System.out.println("Elza bad4");

        }
               return response;
    }

    private List<Pothole> parseJSONResponse(JSONArray response) {
        List<Pothole> potholeList = new ArrayList<Pothole>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy, hh:mm a");

        try {

            if (response!= null && response.length() != 0) {
                for (int i = 0; i < response.length(); i++) {

                        JSONObject jObj = response.getJSONObject(i);
                        Pothole pothole = new Pothole();
                        pothole.setId(Integer.parseInt(jObj.getString("id")));
                        pothole.setDescription(jObj.getString("description"));
                        pothole.setImageType(jObj.getString("imagetype"));
                        pothole.setType(jObj.getString("type"));
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                    pothole.setCreatedDate(dateFormat.parse(jObj.getString("created")));

                        pothole.setLatitude(jObj.getDouble("latitude"));
                        pothole.setLongitude(jObj.getDouble("longitude"));
                        listPothole.add(pothole);
                        if (getActivity() != null) {
                            TabOfFragmentB = ((ShowReportsActivity) getActivity()).getTabFragmentB();

                            MapViewFragment fragmentB = (MapViewFragment) getActivity()
                                    .getSupportFragmentManager()
                                    .findFragmentByTag(TabOfFragmentB);

                            fragmentB.addPotholeToMap(pothole);
                        }

                        Collections.sort(listPothole, new Comparator<Pothole>() {
                            @Override
                            public int compare(Pothole r1, Pothole r2) {
                                return r2.getCreatedDate().compareTo(r1.getCreatedDate());
                            }
                        });

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return potholeList;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.cancel(true);
        cancelPendingRequests();
    }

    public void cancelPendingRequests() {
        if (requestQueue != null) {
            requestQueue.cancelAll("My Tag");
        }
    }

}