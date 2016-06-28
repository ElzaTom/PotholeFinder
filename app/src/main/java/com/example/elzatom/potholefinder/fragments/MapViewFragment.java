package com.example.elzatom.potholefinder.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;import com.example.elzatom.potholefinder.R;
import com.example.elzatom.potholefinder.model.Pothole;
import com.example.elzatom.potholefinder.views.ShowReportsActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;

public class MapViewFragment extends Fragment implements GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    MapView mMapView;
    List<Pothole> listPothole = new ArrayList<>();
    String myTag;
    int markerId;
    OnMarkerSelectedListener mListener;


    public MapViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
         myTag = getTag();
        ((ShowReportsActivity)getActivity()).setTabFragmentB(myTag);
        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        googleMap = mMapView.getMap();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.7152778, -117.1563889),10));
            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnMarkerSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnURLSelectedListener");
        }

    }
    public interface OnMarkerSelectedListener {
        void onMarkerSelected(int id,List<Pothole> listPothole);

    }

    public void addMarker(double lat, double log) {

        LatLng sydney = new LatLng(lat, log);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions().position(sydney).title(String.valueOf(markerId)));
        googleMap.setOnMarkerClickListener(this);

    }

    public void addPotholeToMap(Pothole pothole){

        listPothole.add(pothole);
        if (pothole.getLatitude() != 0 && pothole.getLongitude() != 0) {

            addMarker(pothole.getLatitude(), pothole.getLongitude());
        }
        markerId++;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mListener.onMarkerSelected(Integer.parseInt(marker.getTitle()), listPothole);
        return true;
    }
}
