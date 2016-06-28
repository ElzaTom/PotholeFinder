package com.example.elzatom.potholefinder.views;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.elzatom.potholefinder.R;
import com.example.elzatom.potholefinder.volley.VolleySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class DetailedReportActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener{

    private GoogleMap googleMap;
    EditText editTextDescription;
    int user ;
    double latitude,longitude;
    TextView textViewDescription;
    ImageView potholeImage;
    String description,imageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_report);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!= null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        editTextDescription = (EditText) findViewById(R.id.editTextdec);

        potholeImage = (ImageView)findViewById(R.id.imageViewPothole);
        textViewDescription = (TextView) findViewById(R.id.textViewlocation);
        Intent intent = getIntent();
        editTextDescription.setInputType(InputType.TYPE_NULL);
        Bundle bundle = intent.getExtras();
        String descSubstring = bundle.getString("desciption");
        if (descSubstring.length()>300){
            description = descSubstring.substring(0, 300);
        }
        description=descSubstring;
        latitude = bundle.getDouble("latitude", 0);
        longitude = bundle.getDouble("longitude", 0);
        user = bundle.getInt("user", 0);
        imageType = bundle.getString("type");
        editTextDescription.setText(description);
        if(imageType != "none") {
            showImage();
        }
        showLocation();
        editTextDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(editTextDescription.getWindowToken(), 0);
            }

        });
    }

    public void showLocation(){
        String streetName= "";
        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.maplocation)).getMap();


            }
            LatLng position = new LatLng(latitude, longitude);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if(null!=listAddresses&&listAddresses.size()>0){
                     streetName = listAddresses.get(0).getAddressLine(1);
                    textViewDescription.setText(streetName);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            googleMap.addMarker(new MarkerOptions().position(position).title(streetName));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
            googleMap.setOnMarkerClickListener(this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showImage(){

        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        Response.Listener<Bitmap> success = new Response.Listener<Bitmap>() {
            public void onResponse(Bitmap response) {

                potholeImage.setImageBitmap( Bitmap.createScaledBitmap(response, 130, 130, false));
            }
        };
        Response.ErrorListener failure = new Response.ErrorListener() {
            public void onErrorResponse(VolleyError error) {

            }
        };

        ImageRequest postRequest = new ImageRequest("http://bismarck.sdsu.edu/city/image?id="+user,
                success, 0, 0, ImageView.ScaleType.CENTER_INSIDE, null, failure);

        requestQueue.add(postRequest);
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
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

}
