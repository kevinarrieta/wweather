package com.example.kevinarrieta.wweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.kevinarrieta.wweather.service.ServiceHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    private String TAG = "com.wweather";
    private String API_KEY = "c76d2638abbfd2e0d9013002390fd2b0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "The awesome is coming...");


        TextView tv1 = (TextView) findViewById(R.id.city_view);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Nobile-Regular.ttf");
        tv1.setTypeface(face);
        Log.i(TAG, tv1.getTypeface().toString() + "hhhhhh");

        // Create an instance of GoogleAPIClient.

        if (mGoogleApiClient == null) {
            Log.i(TAG, "Google API Client is null");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void getTheWeatherMan(double lat, double lon){

        ServiceHandler handler = new ServiceHandler();
        //Map<String, Object> params = new ArrayMap<String, Object>();
        //params.put("title", "foo");
        //params.put("body", "bar");
        //params.put("userId", "1");
        handler.addHeader("Content-Type","Application/json");
        handler.objectRequest("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&APPID=" + API_KEY, Request.Method.GET,
                null, JSONObject.class, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.getMessage());
                    }
                });

    }

    @Override
    protected void onStart() {
        Log.i(TAG, "on start");
        mGoogleApiClient.connect();

        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "on connected");
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){

            Log.i(TAG, "PERMISSION GRANTED");

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
                //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
                Log.i(TAG, String.valueOf(mLastLocation.getLatitude()) + String.valueOf(mLastLocation.getLongitude()));
                getTheWeatherMan(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }

        }else{
            Log.i(TAG, "PERMISSION not GRANTED");
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
