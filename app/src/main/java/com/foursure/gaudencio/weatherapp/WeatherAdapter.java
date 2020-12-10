package com.foursure.gaudencio.weatherapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;




import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
/**
 * @author Gaudencio Solivatore
 * @created December 2020
 */
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.MyViewHolder>  {

    private List<Weather> weatherList;
    private Context context;
    private  JSONArray jsonArray;
    private int dated;
    private MapsActivity mp = new MapsActivity();
    private int symbol_status;
    private SharedPreferences prefs;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView humidity, temperature, minTemp, maxTemp, dateView;
        private RelativeLayout relativeLayout;
        private ImageView buttonViewOption;
        private ImageView weather;

        public MyViewHolder(View view) {
            super(view);
            weather = view.findViewById(R.id.weather);
            humidity = (TextView) view.findViewById(R.id.humView);
            dateView= (TextView) view.findViewById(R.id.dateView);
            temperature = (TextView) view.findViewById(R.id.tempView);
            maxTemp = (TextView) view.findViewById(R.id.maxView);
            minTemp = (TextView) view.findViewById(R.id.minView);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout2);
            buttonViewOption = (ImageView) itemView.findViewById(R.id.row_more);
            buttonViewOption.setVisibility(View.GONE);

        }
    }


    public WeatherAdapter(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate( R.layout.public_weather_list_row, parent, false);
        context = parent.getContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        symbol_status = Integer.parseInt(prefs.getString("key_metric_value", "1"));
        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Weather weather = weatherList.get(position);
        String description = weather.getDescription();
        int thisDated = weather.getDate();
        long unix_seconds = thisDated;
        //convert seconds to milliseconds
        Date date = new Date(unix_seconds*1000L);
        // format of the date
        SimpleDateFormat jdf = new SimpleDateFormat ("yyyy-MM-dd");
        jdf.applyPattern("EEEE, dd MMM yyyy");
        //jdf.setTimeZone( TimeZone.getTimeZone("GMT-4"));
        String java_date = jdf.format(date);
        holder.humidity.setText(String.valueOf((weather.getHumidity())) + "%");
        holder.temperature.setText(String.valueOf(weather.getTemperature()) + Weather.get_symbol(symbol_status));
        holder.maxTemp.setText(String.valueOf(weather.getMaxTemp()) + Weather.get_symbol(symbol_status));
        holder.minTemp.setText(String.valueOf(weather.getMinTemp()) + Weather.get_symbol(symbol_status));
        holder.dateView.setText(java_date);
        holder.weather.setImageResource(getIcon(description));
        Log.i("TAG", description);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dated=weather.getDate();
                Intent intent  = new Intent(context,MainActivity.class);
                context.startActivity(intent);


               // Toast.makeText(view.getContext(),"click on item: "+ clients.getName(),Toast.LENGTH_LONG).show();
            }
        });

    }

    public int getIcon(String condition){
        int icon;
        switch (condition){
            case "clear sky":
                icon = R.drawable.clear_sky;
                break;
            case "light rain":
                icon = R.drawable.light_rain;
                break;
            case "few clouds":
                icon =R.drawable.few_clouds;
                break;
            case "scattered clouds":
                icon = R.drawable.scattered_clouds;
                break;
            case "broken clouds":
                icon = R.drawable.broken_clouds;
                break;
            case "shower rain":
                icon = R.drawable.shower_rain;
                break;
            case "rain":
                icon = R.drawable.rain;
                break;
            case "thunderstorm":
                icon = R.drawable.thunderstorm;
                break;
            case "snow":
                icon = R.drawable.snow;
                break;
            case "mist":
                icon = R.drawable.mist;
                break;


            default:
                icon = R.drawable.light_rain;
                break;
        }



        return  icon;
    }


    @Override
    public int getItemCount() {
        return weatherList.size();
    }
}
