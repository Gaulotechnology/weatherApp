package com.foursure.gaudencio.weatherapp;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * @author Gaudencio Solivatore
 * @created December 2020
 */
public class WeatherActivity extends AppCompatActivity {

    Context context;
    double lat;
    double lon;
    TextView temperature;
    TextView humidity;
    TextView minTemp;
    TextView maxtemp;
    TextView place;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        place = (TextView)findViewById(R.id.textviewPlace);
        temperature = (TextView)findViewById(R.id.textviewTemp);
        humidity = (TextView)findViewById(R.id.textviewHumidity);
        minTemp = (TextView)findViewById(R.id.textviewMinTemp);
        maxtemp = (TextView)findViewById(R.id.textviewMaxTemp);

        Intent intent = getIntent();

        lat = intent.getDoubleExtra("latitude",0);
        lon = intent.getDoubleExtra("longitude",0);
        Log.e("Weather",lat + " " + lon);
        context = getApplicationContext();
        getWeatherData();

    }

    public void getWeatherData(){

       String server_url  = "http://api.openweathermap.org/data/2.5/weather?lat=" + Double.toString(lat) + Weather.matric + "&lon=" +  Double.toString(lon)+ "&APPID=ba6f50c8f8128fa2cce45dc13d58ed2c";
        Log.e("RegisterActivity",server_url);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET,
                server_url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("weather",response.toString());

                        try {
                            JSONObject weather= response.getJSONObject("main");

                            place.setText(weather.optString("name"));
                           temperature.setText(weather.getString("temp"));
                           humidity.setText(weather.getString("humidity"));
                           minTemp.setText(weather.getString("temp_min"));
                           maxtemp.setText(weather.getString("temp_max"));

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

        RequestQueueFactory.getInstance(context).addToRequestQueue(stringRequest);
    }


}
