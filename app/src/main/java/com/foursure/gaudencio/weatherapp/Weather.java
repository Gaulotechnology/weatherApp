package com.foursure.gaudencio.weatherapp;
/**
 * @author Gaudencio Solivatore
 * @created December 2020
 */
public class Weather {
    public  static String matric ="&units=metric";
    int date;
    String humidity;
    String minTemp;
    String maxTemp;
    String temperature;
    String description;

    public static String get_symbol(int choice){
        if(choice==1 ){
            return " \u2103";
        }
        else{
            return " \u2109";
        }
    }




    public Weather(int date, String humidity, String minTemp, String maxTemp, String temperature, String description) {
        this.date = date;
        this.humidity = humidity;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.temperature = temperature;
        this.description = description;
    }

    public Weather(int date, String humidity, String minTemp, String maxTemp, String temperature) {
        this.date = date;
        this.humidity = humidity;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.temperature = temperature;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}