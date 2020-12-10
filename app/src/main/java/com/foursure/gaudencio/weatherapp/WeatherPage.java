package com.foursure.gaudencio.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.foursure.gaudencio.weatherapp.Weather.matric;

/**
 * @author Gaudencio Solivatore
 * @created December 2020
 */
public class WeatherPage extends AppCompatActivity {


    private List<Weather> weatherList = new ArrayList<>();
    private   WeatherAdapter adapter;
    private EditText search;
    private RecyclerView recyclerView;
    private String humidity, temparature, maxTemp, minTemp;
    private double currentLat=0.0, currentLon=0.0;
    private String condition;
    private SharedPreferences prefs;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_weather_page);
        Intent intent = getIntent();
        currentLat = intent.getDoubleExtra("latitude",0);
        currentLon = intent.getDoubleExtra("longitude",0);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getDaily();
        // Inflate the layout for this fragment
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        search=(EditText) findViewById(R.id.search);
        adapter = new WeatherAdapter(weatherList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        search.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {


                adapter = new WeatherAdapter(weatherList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
        });


    }
    public void getDaily(){
        String url = "http://api.openweathermap.org/data/2.5/onecall?lat=" + Double.toString(currentLat) + get_matric_ur()  + "&exclude=hourly&lon=" +  Double.toString(currentLon)+ "&APPID=ba6f50c8f8128fa2cce45dc13d58ed2c";
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
                        int dt = weather.getInt("dt");
                        JSONObject temp = weather.getJSONObject("temp");
                        temparature = temp.getString("day");
                        humidity= weather.getString("humidity");
                        minTemp=temp.getString("min");
                        maxTemp=temp.getString("max");
                        JSONArray jsonArray1 = weather.getJSONArray("weather");
                        Log.e("TAG", jsonArray.toString());
                        condition="few clouds";
                        for (int a = 0; a < jsonArray1.length(); a++) {
                            JSONObject test = jsonArray1.getJSONObject(a);
                            condition =  test.getString("description");
                            Log.i("TAG>>", test.getString("description"));
                        }

                        weatherList.add(new Weather(dt,humidity,minTemp, maxTemp,temparature, condition));

                        Log.e("TAG", dt + ">>>>" + "Temp" + temparature +
                                "Humidity" + humidity
                                + "Min Temp" +  minTemp +
                                "Max Temp " + maxTemp);





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
    public String get_matric_ur(){

        int mtr = Integer.parseInt(prefs.getString("key_metric_value", "1"));
        if(mtr==0){
            return "";

        }else{
            return matric;
        }

    }


}