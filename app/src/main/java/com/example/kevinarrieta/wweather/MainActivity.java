package com.example.kevinarrieta.wweather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.kevinarrieta.wweather.service.ServiceHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.vision.text.Text;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    private String TAG = "com.wweather";
    private String API_KEY = "c76d2638abbfd2e0d9013002390fd2b0";

    TextView title;
    TextView city;
    TextView temp;
    TextView hume;
    TextView sunrise;
    TextView sunset;
    EditText cityInput;
    Button searchBt;

    TextView date1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "The awesome is coming...");

        title = (TextView) findViewById(R.id.title);
        city = (TextView) findViewById(R.id.city);
        temp = (TextView) findViewById(R.id.temp);
        hume = (TextView) findViewById(R.id.hume);
        sunrise = (TextView) findViewById(R.id.sunrise);
        sunset = (TextView) findViewById(R.id.sunset);
        cityInput = (EditText) findViewById(R.id.city_input);
        searchBt = (Button) findViewById(R.id.searchBt);
        date1 = (TextView) findViewById(R.id.date1);

        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        title.setTypeface(font);
        city.setTypeface(font);

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

        Toast.makeText(this, "Obteniendo tu ubicación...",  Toast.LENGTH_LONG).show();

        final Context context = this;

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

                        try {

                            Date dSunrise = new java.util.Date(response.getJSONObject("sys").getInt("sunrise") * 1000);
                            String fSunrise = new SimpleDateFormat("hh:mm").format(dSunrise);

                            Date dSunset = new java.util.Date(response.getJSONObject("sys").getInt("sunset") * 1000);
                            String fSunset = new SimpleDateFormat("hh:mm").format(dSunset);

                            Calendar c = Calendar.getInstance();
                            String fDate = new SimpleDateFormat("MMMM d, yyyy").format(c.getTime());

                            city.setText(response.getString("name"));
                            temp.setText(response.getJSONObject("main").getDouble("temp") + "");
                            hume.setText(response.getJSONObject("main").getDouble("humidity") + "");
                            sunrise.setText(fSunrise);
                            sunset.setText(fSunset);
                            date1.setText(fDate);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, e.getMessage());
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.e(TAG, error.getMessage());
                        Toast.makeText(context, "Ooops! Un error ha ocurrido al consultar el servicio del clima...",  Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getTheWeatherMan(String cityQ) {

        Toast.makeText(this, "Obteniendo tu ubicación...",  Toast.LENGTH_LONG).show();

        final Context context = this;

        try {
            String query = URLEncoder.encode(cityQ, "utf-8");

            ServiceHandler handler = new ServiceHandler();
            handler.addHeader("Content-Type","Application/json");
            handler.objectRequest("http://api.openweathermap.org/data/2.5/weather?q=" + query + "&APPID=" + API_KEY, Request.Method.GET,
                    null, JSONObject.class, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i(TAG, response.toString());
                            try {
                                Date dSunrise = new java.util.Date(response.getJSONObject("sys").getInt("sunrise") * 1000);
                                String fSunrise = new SimpleDateFormat("hh:mm").format(dSunrise);

                                Date dSunset = new java.util.Date(response.getJSONObject("sys").getInt("sunset") * 1000);
                                String fSunset = new SimpleDateFormat("hh:mm").format(dSunset);

                                city.setText(response.getString("name"));
                                temp.setText(response.getJSONObject("main").getDouble("temp") + "");
                                hume.setText(response.getJSONObject("main").getDouble("humidity") + "");
                                sunrise.setText(fSunrise);
                                sunset.setText(fSunset);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(TAG, e.getMessage());
                            }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(context, "Ooops! Un error ha ocurrido al consultar el servicio del clima...",  Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void searchHandler(View v){
        getTheWeatherMan(cityInput.getText().toString());
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
        Toast.makeText(this, "Ooops! Metimos la pata, la conexión no se pudo establecer...",  Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "Ooops! Al parecer no nos has dado los permisos para acceder a tu ubicación...",  Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Ooops! Metimos la pata, la conexión no se ha suspendido...",  Toast.LENGTH_SHORT).show();
    }
}
