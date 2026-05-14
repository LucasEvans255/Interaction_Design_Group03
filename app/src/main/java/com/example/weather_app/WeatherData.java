package com.example.weather_app;

import android.util.Log;

import com.openmeteo.sdk.Variable;
import com.openmeteo.sdk.VariableWithValues;
import com.openmeteo.sdk.VariablesSearch;
import com.openmeteo.sdk.VariablesWithTime;
import com.openmeteo.sdk.WeatherApiResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import okhttp3.OkHttpClient;
import okhttp3.Response;

public class WeatherData {
    private static Double longitude = 52.1951;
    private static Double latitude = 0.1313;

    // Temp, hourly, celsius

    // Apparent temperature

    // Precipitation chance, hourly

    // Weather code, hourly

    // Wind speed, hourly

    // Wind direction, hourly


    // UV index, daily

    // Sunrise, daily

    // Sunset, daily


    // Air quality, current

    // Also mapping to codes themselves

    public static void PrintData() {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                byte[] responseIN = null;
                String URL = "https://api.open-meteo.com/v1/forecast?"
                        + "format=flatbuffers" + "&"
                        + String.format("latitude=%f", WeatherData.latitude) + "&"
                        + String.format("longitude=%f", WeatherData.longitude) + "&"
                        + "daily=uv_index_max,sunset,sunrise" + "&"
                        + "hourly=temperature_2m,precipitation_probability,weather_code,wind_direction_10m,wind_speed_10m,apparent_temperature";

                Log.d("BUTTONS", URL);

                // String mUrl = "https://api.open-meteo.com/v1/forecast?latitude=49.70808&longitude=8.08829&timezone=Europe/Berlin&minutely_15=temperature_2m,weathercode&format=flatbuffers";
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(URL).method("GET", null)
                        .build();

                OkHttpClient client = new OkHttpClient();

                try (Response response = client.newCall(request).execute()) {
                    responseIN = response.body().bytes();
                } catch (IOException e) {
                    Log.d("BUTTONS", e.getMessage());
                    return;
                }

                ByteBuffer buffer = ByteBuffer.wrap(responseIN).order(ByteOrder.LITTLE_ENDIAN);
                WeatherApiResponse mApiResponse = WeatherApiResponse.getRootAsWeatherApiResponse((ByteBuffer) buffer.position(4));

                VariablesWithTime hourly =  mApiResponse.hourly();
                VariableWithValues temp = new VariablesSearch(hourly).variable(Variable.temperature).altitude(2).first();

                for (int i = 0; i < temp.valuesLength(); i++) {
                    Log.d("BUTTONS", "" + i);
                    Log.d("BUTTONS", "" + temp.values(i));
                }

                // Step 4 : get the minutely15 block
//                VariablesWithTime minutely15 =  mApiResponse.minutely15();
//
//                VariableWithValues temperature2m = new VariablesSearch(minutely15)
//                        .variable(Variable.temperature)
//                        .altitude(2)
//                        .first();
//                VariableWithValues wmo = new VariablesSearch(minutely15)
//                        .variable(Variable.weather_code)
//                        .first();
//
//                for ( int vl = 0; vl < temperature2m.valuesLength(); vl ++)
//                    Log.d("BUTTONS", "Temperature and wmo at index : " + vl + " -> " + temperature2m.values(vl) + " / " + wmo.values(vl));

                buffer.clear();
            }
        });
        thread.start();
    }

}
