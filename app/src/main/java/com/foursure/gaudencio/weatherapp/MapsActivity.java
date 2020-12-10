package com.foursure.gaudencio.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.net.Uri;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import static com.foursure.gaudencio.weatherapp.Weather.matric;


/**
 * @author Gaudencio Solivatore
 */
public class MapsActivity extends FragmentActivity implements Target, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,GoogleMap.InfoWindowAdapter
        {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    private static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 11;
    private double currentLat;
    private double currentLon;
    private String condition;
    private String temparature;
    private String humidity;
    private String minTemp1;
    private String maxTemp1;
    private Button menu;
    private LinearLayout menuc;
    private SharedPreferences prefs;
    private int symbol_status;


            private static final String TAG = "MapsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        menu = findViewById(R.id.menu);
        menuc = findViewById(R.id.menuc);
        //mapView = (MapView) findViewById(R.id.map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
        symbol_status = Integer.parseInt(prefs.getString("key_metric_value", "1"));

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, SettingsPrefActivity.class));
            }
        });
        menuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, SettingsPrefActivity.class));
            }
        });


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(MapsActivity.this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }


    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    public String get_matric_ur(){

        symbol_status= Integer.parseInt(prefs.getString("key_metric_value", "1"));
         if(symbol_status==0){
             return "";

         }else{
             return matric;
         }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(this);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        prefs = PreferenceManager.getDefaultSharedPreferences(MapsActivity.this);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng loc) {
                Log.e(TAG,"Clicked on map");
                //LatLng loc = new LatLng(lat, lng);
                currentLat = loc.latitude;
                currentLon = loc.longitude;
                reverse_GeoCoder();
                getWeatherData(loc);
                //getForecast();
                getDaily();

                //BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.light_rain);

                LatLng currentLocation = new LatLng(currentLat, currentLon);


            }
        });
    }

    
    public  BitmapDescriptor getIcon(String condition){
        BitmapDescriptor icon;
        switch (condition){
            case "clear sky":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.clear_sky);
                break;
            case "light rain":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.light_rain);
                break;
            case "few clouds":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.few_clouds);
                break;
            case "scattered clouds":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.scattered_clouds);
                break;
            case "broken clouds":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.broken_clouds);
                break;
            case "shower rain":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.shower_rain);
                break;
            case "rain":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.rain);
                break;
            case "thunderstorm":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.thunderstorm);
                break;
            case "snow":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.snow);
                break;
            case "mist":
                icon = BitmapDescriptorFactory.fromResource(R.drawable.mist);
                break;


            default:
                icon = BitmapDescriptorFactory.fromResource(R.drawable.light_rain);
                break;
        }



        return  icon;
    }


    public void getForecast(){
        String url = "http://api.openweathermap.org/data/2.5/forecast?lat=" + Double.toString(currentLat) + get_matric_ur() + "&lon=" +  Double.toString(currentLon)+ "&APPID=ba6f50c8f8128fa2cce45dc13d58ed2c";
        Log.e("TAG", url);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    Log.e("TAG", jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject weather = jsonArray.getJSONObject(i);
                        String dt = weather.getString("dt");
                        //JSONObject jsonObject2 = new JSONObject("main);
                        //humidity= weather.getString("humidity");
                        //minTemp1=weather.getString("temp_min");
                        //maxTemp1=weather.getString("temp_max");

                        JSONObject fetch = weather.getJSONObject("main");

                            temparature = fetch.getString("temp");
                            humidity= fetch.getString("humidity");
                            minTemp1=fetch.getString("temp_min");
                            maxTemp1=fetch.getString("temp_max");

                            Log.e("TAG", dt + ">>>>" + "Temp" + temparature +
                                    "Humidity" + humidity
                                    + "Min Temp" +  minTemp1 +
                                    "Max Temp " + maxTemp1);





                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("TAG", "Exception" + e.getMessage() + ">>>>");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG", "Volley Error" + error.getMessage() + ">>>>");
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
    public void getDaily(){
        String url = "http://api.openweathermap.org/data/2.5/onecall?lat=" + Double.toString(currentLat) + get_matric_ur() + "&exclude=hourly&lon=" +  Double.toString(currentLon)+ "&APPID=ba6f50c8f8128fa2cce45dc13d58ed2c";
        Log.e("TAG", url);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("TAG", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("daily");
                            Log.e("TAG", jsonArray.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject weather = jsonArray.getJSONObject(i);
                                String dt = weather.getString("dt");
                                JSONObject temp = weather.getJSONObject("temp");
                                temparature = temp.getString("day");
                                humidity= weather.getString("humidity");
                                minTemp1=temp.getString("min");
                                maxTemp1=temp.getString("max");
                                JSONArray jsonArray1 = weather.getJSONArray("weather");
                                Log.e("TAG", jsonArray.toString());
                                condition="few clouds";
                                for (int a = 0; a < jsonArray1.length(); a++) {
                                    JSONObject test = jsonArray1.getJSONObject(a);
                                    condition =  test.getString("description");
                                    Log.i("TAG>>", test.getString("description"));
                                }

                                Log.e("TAG", dt + ">>>>" + "Temp" + temparature +
                                        "Humidity" + humidity
                                        + "Min Temp" +  minTemp1 +
                                        "Max Temp " + maxTemp1);





                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.i("TAG", "Exception" + e.getMessage() + ">>>>");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("TAG", "Volley Error" + error.getMessage() + ">>>>");
                    }
                });
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }


    public void getWeatherData(final LatLng loc){

                String server_url  = "http://api.openweathermap.org/data/2.5/weather?lat=" + Double.toString(currentLat) + get_matric_ur()  + "&lon=" +  Double.toString(currentLon)+ "&APPID=ba6f50c8f8128fa2cce45dc13d58ed2c";
                Log.e("RegisterActivity",server_url);
                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET,
                        server_url,null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONObject weather= response.getJSONObject("main");

                                    temparature = weather.getString("temp");
                                    humidity= weather.getString("humidity");
                                    minTemp1=weather.getString("temp_min");
                                    maxTemp1=weather.getString("temp_max");
                                    JSONArray jsonArray = response.getJSONArray("weather");
                                    Log.e("TAG", jsonArray.toString());
                                    condition="few clouds";
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject test = jsonArray.getJSONObject(i);
                                        condition =  test.getString("description");
                                        Log.i("TAG", test.getString("description"));
                                    }

                                    mMap.addMarker(new MarkerOptions().position(loc).title("New Marker").icon(getIcon(condition)));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            Intent intent = new Intent(MapsActivity.this,WeatherPage.class);
                                            intent.putExtra("latitude",currentLat);
                                            intent.putExtra("longitude",currentLon);
                                            startActivity(intent);
                                            return false;
                                        }
                                    });

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                            } catch (Exception e1) {
                                // Couldn't properly decode data to string
                                Toast.makeText(getApplicationContext(),"An error occured while creating the account. Please try again later",Toast.LENGTH_SHORT).show();
                                e1.printStackTrace();
                            }
                        }
                    }
                });

                RequestQueueFactory.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
            }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },
                    MY_PERMISSION_ACCESS_COARSE_LOCATION );
        }

        float zoomLevel = 8.0f;
       @SuppressLint("MissingPermission") Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(TAG,mLastLocation.getLatitude() + " , " +mLastLocation.getLongitude());
         currentLat = mLastLocation.getLatitude();
         currentLon = mLastLocation.getLongitude();
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.light_rain);
            LatLng currentLocation = new LatLng(currentLat, currentLon);
            getWeatherData(currentLocation);
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("My Location").icon(icon));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,zoomLevel));



        }else{
            Log.d(TAG,"Receiving nul for location");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "+ connectionResult.getErrorCode());
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        //return null;
        return prepareInfoView(marker);
    }

    @SuppressLint("SetTextI18n")
    private View prepareInfoView(Marker marker){
        //prepare InfoView programmatically
        LinearLayout infoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setLayoutParams(infoViewParams);

        ImageView infoImageView = new ImageView(MapsActivity.this);
        //Drawable drawable = getResources().getDrawable(R.mipmap.ic_launcher);
        Drawable drawable = getResources().getDrawable(android.R.drawable.ic_dialog_map);
        infoImageView.setImageDrawable(drawable);
        infoView.addView(infoImageView);

        LinearLayout subInfoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        subInfoView.setLayoutParams(subInfoViewParams);

        TextView subInfoLat = new TextView(MapsActivity.this);
        subInfoLat.setText("Temperature : " + temparature + Weather.get_symbol(symbol_status));
        TextView subInfoLnt = new TextView(MapsActivity.this);
        subInfoLnt.setText("Humidity :  " + humidity +  "%");
        TextView minTemp = new TextView(MapsActivity.this);
        minTemp.setText("Min Temp :  " + minTemp1 + Weather.get_symbol(symbol_status));
        TextView maxTemp = new TextView(MapsActivity.this);
        maxTemp.setText("Max Temp :  " + maxTemp1 + Weather.get_symbol(symbol_status));
        subInfoView.addView(subInfoLat);
        subInfoView.addView(subInfoLnt);
        subInfoView.addView(minTemp);
        subInfoView.addView(maxTemp);
        infoView.addView(subInfoView);

        return infoView;
    }
            public void searchLocation(View view) {
                EditText locationSearch = (EditText) findViewById(R.id.searchAdd);
                String location = locationSearch.getText().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    currentLat = address.getLatitude();
                    currentLon = address.getLongitude();
                    reverse_GeoCoder();
                    getWeatherData(latLng);
                    //getForecast();
                    getDaily();

                }
            }


            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

            public void reverse_GeoCoder(){
                Geocoder gc = new Geocoder(getApplicationContext());
                if(gc.isPresent()){
                    List<Address> list = null;
                    try {
                        list = gc.getFromLocation(currentLat, currentLon,1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = list.get(0);
                    StringBuffer str = new StringBuffer();
                    str.append("Name: " + address.getLocality() + "\n");
                    str.append("Sub-Admin Ares: " + address.getSubAdminArea() + "\n");
                    str.append("Admin Area: " + address.getAdminArea() + "\n");
                    str.append("Country: " + address.getCountryName() + "\n");
                    str.append("Country Code: " + address.getCountryCode() + "\n");
                    String strAddress = str.toString();
                    Log.i("CON", strAddress);
                }
            }
        }
