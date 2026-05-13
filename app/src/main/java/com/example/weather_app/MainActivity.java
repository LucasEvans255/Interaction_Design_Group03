package com.example.weather_app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.openmeteo.sdk.VariablesSearch;
import com.openmeteo.sdk.VariableWithValues;
import com.openmeteo.sdk.Variable;
import com.openmeteo.sdk.Aggregation;
import com.openmeteo.sdk.VariablesWithTime;
import com.openmeteo.sdk.WeatherApiResponse;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button button = (Button) findViewById(R.id.first_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        byte[] responseIN = null;
                        String mUrl = "https://api.open-meteo.com/v1/forecast?latitude=49.70808&longitude=8.08829&timezone=Europe/Berlin&minutely_15=temperature_2m,weathercode&format=flatbuffers";
                        okhttp3.Request request = new okhttp3.Request.Builder()
                                .url(mUrl).method("GET", null)
                                .build();

                        OkHttpClient client = new OkHttpClient();

                        try (Response response = client.newCall(request).execute()) {
                            responseIN = response.body().bytes();
                        } catch (IOException e) {
                            Log.d("BUTTONS", e.getMessage());
                            return;
                        }

                        // Step 2 : Use Binary Response buffer and convert it to ByteBuffer
                        ByteBuffer buffer = ByteBuffer.wrap(responseIN).order(ByteOrder.LITTLE_ENDIAN);

                        // Step 3 : create the ApiResponse Instance
                        // Note : The first 4 bytes interpret the length of the datablock
                        WeatherApiResponse mApiResponse = WeatherApiResponse.getRootAsWeatherApiResponse((ByteBuffer) buffer.position(4));

                        // Step 4 : get the minutely15 block
                        VariablesWithTime minutely15 =  mApiResponse.minutely15();

                        VariableWithValues temperature2m = new VariablesSearch(minutely15)
                                        .variable(Variable.temperature)
                                        .altitude(2)
                                        .first();
                        VariableWithValues wmo = new VariablesSearch(minutely15)
                                        .variable(Variable.weather_code)
                                        .first();

                        for ( int vl = 0; vl < temperature2m.valuesLength(); vl ++)
                            Log.d("BUTTONS", "Temperature and wmo at index : " + vl + " -> " + temperature2m.values(vl) + " / " + wmo.values(vl));

                        buffer.clear();
                    }
                });
                thread.start();
            }
        });
    }
}
