package com.example.elzatom.potholefinder.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.elzatom.potholefinder.R;
import com.example.elzatom.potholefinder.volley.VolleySingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;


public class AddReportActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_TAKE_PHOTO = 100;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private ImageView imgPreview;
    private EditText userId,description;
    private TextView textVolleyError;
    private double latitude, longitude ;
    private boolean isImageCaptured = false;
    private Location mLastLocation;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_report);
        imgPreview = (ImageView) findViewById(R.id.imageView);
        userId = (EditText) findViewById(R.id.editText);
        description = (EditText) findViewById(R.id.editText2);
        textVolleyError =(TextView)findViewById(R.id.textVolleyError);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!= null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Get the location manager
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();

        }
    }
    public void addPhoto(View button){

        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Please allow Camera premission to Application ", Toast.LENGTH_SHORT).show();

            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = imageFile();
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            }
        }
    }

    public String filename() {
        return "JPEG_FILE.jpg";
    }
    private File imageFile() {
        File externalFilesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if ( externalFilesDir == null ) return null;
        return new File(externalFilesDir, filename());
    }
    public void submit(View button){

        displayLocation();
        if (isValueCheck() && mLastLocation != null) {
                postDataToServer();

            }


    }

    private void displayLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getApplicationContext(),
                    "Please allow location premission to Application", Toast.LENGTH_SHORT)
                    .show();
        }else {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();

            } else {

                Toast.makeText(getApplicationContext(),
                        "Please enable location", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void cancel(View button){
        finish();

    }

    public void postDataToServer (){

        JSONObject data = new JSONObject();
        try {

            data.put("user", userId.getText());
            data.put("longitude", longitude);

            if(isImageCaptured){
                BitmapDrawable drawable = (BitmapDrawable) imgPreview.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                image = Base64.encodeToString(byteArray, Base64.NO_WRAP);
                data.put("image", image);
                data.put("imagetype", "jpg");

            }else {
                data.put("imagetype", "none");
            }
            data.put("latitude", latitude);
            data.put("type", "street");
            data.put("description", description.getText());

        }catch (JSONException e) {
            e.printStackTrace();

        }
        RequestQueue requestQueue = VolleySingleton.getInstance().getRequestQueue();
        Response.Listener<JSONObject> success = new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                textVolleyError.setVisibility(View.GONE);
                String id ="";
                try {
                     id = String.valueOf(response.get("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Successfully Added. Report ID: " + id, Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        Response.ErrorListener failure = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleVolleyError(error);

            }
        };
        JsonObjectRequest postRequest = new JsonObjectRequest("http://bismarck.sdsu.edu/city/report", data, success, failure);
        requestQueue.add(postRequest);


    }
    public boolean isValueCheck(){

        if (userId.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Please enter the UserId", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }else if (description.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Please enter the Description", Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
       return true;
    }

    private boolean isDeviceSupportCamera() {

        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                File photoFile = imageFile();
                if (photoFile != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // downsizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 8;
                    Uri fileUri;
                    fileUri= Uri.fromFile(imageFile());
                    final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                            options);
                    imgPreview.setImageDrawable(null);
                    imgPreview.setImageBitmap(bitmap);
                    isImageCaptured= true;

                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                isImageCaptured= false;
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                isImageCaptured= false;
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();

            }
            return false;
        }
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        displayLocation();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }
    private void handleVolleyError(VolleyError error) {
        textVolleyError.setVisibility(View.VISIBLE);
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            textVolleyError.setText(R.string.error_timeout);

        } else if (error instanceof AuthFailureError) {
            textVolleyError.setText(R.string.error_auth_failure);
            //TODO
        } else if (error instanceof ServerError) {
            textVolleyError.setText(R.string.error_auth_failure);
            //TODO
        } else if (error instanceof NetworkError) {
            textVolleyError.setText(R.string.error_network);
            //TODO
        } else if (error instanceof ParseError) {
            textVolleyError.setText(R.string.error_parser);
            //TODO
        }
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
