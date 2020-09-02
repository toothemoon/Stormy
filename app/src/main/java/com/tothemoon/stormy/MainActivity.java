package com.tothemoon.stormy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
// test
    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather currentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiKey = "53fc5fbef8790c35e60848ea7a452e0a";
        double latitude = 51.462688;
        double longitude = 0.106340;

        String forecastURL = "https://api.darksky.net/forecast/"
                + apiKey + "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(forecastURL)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();

                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {

                            currentWeather = getCurrentDetails(jsonData);

                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "IO Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception caught: ", e);
                    }
                }
            });
        }
        Log.d(TAG, "Main UI code is running!");
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {

            JSONObject forecast = new JSONObject(jsonData);

            String timezone = forecast.getString("timezone");
            Log.i(TAG, "From JSON: " + timezone);

            JSONObject currently = forecast.getJSONObject("currently");

            CurrentWeather currentWeather = new CurrentWeather();

            currentWeather.setHumidity(currently.getDouble("humidity"));
            currentWeather.setTime(currently.getLong("time"));
            currentWeather.setTime(currently.getLong("time"));
            currentWeather.setIcon(currently.getString("icon"));
            currentWeather.setLocationLabel("Welling, London");
            currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
            currentWeather.setSummary(currently.getString("summary"));
            currentWeather.setTemperature(currently.getDouble("temperature"));
            currentWeather.setTimeZone(timezone);

            Log.d(TAG, currentWeather.getFormattedTime());

            return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;

        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        else {
            //Toast.makeText(this, R.string.network_unavailable_message,
              //      Toast.LENGTH_LONG).show();
            alertUserAboutNetwork();
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(),"error_dialog");
    }

    private void alertUserAboutNetwork() {
        NetworkErrorDialog dialog = new NetworkErrorDialog();
        dialog.show(getFragmentManager(),"Network Error");
    }
}