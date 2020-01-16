package com.example.lookout.ui.notifications;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lookout.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

public class NearestActivity extends AppCompatActivity {

    private String myIP, myCity, myState, myCountry, weatherIconCode, category;
    private double temperature, humidity, windSpeed, windDirection, cityLatitude, cityLongitude;
    private int pressure, aqi;
    private TextView City, State, Country, Temperature, Pressure, Humidity, WindSpeed, WindDirection, Aqi, Category, WeatherText;
    private ImageView WeatherIcon, Face, OtherSideFaceColor, AtmosphereCardColor, SuggestionIcon1, SuggestionIcon2, SuggestionIcon3, SuggestionIcon4;
    private ProgressBar nearestProgress;
    private Button goToMap;
    private static final String MY_IP_URL = "https://api.ipify.org?format=json";
    private static final String MY_NEAREST_URL = "https://api.airvisual.com/v2/nearest_city?key=9a11661d-a1a4-4629-8030-3669adaade7d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest);

        Temperature = findViewById(R.id.temperature_value);
        Pressure = findViewById(R.id.pressure_value);
        Humidity = findViewById(R.id.humidity_value);
        WindSpeed = findViewById(R.id.windspeed_value);
        WindDirection = findViewById(R.id.winddirection_value);
        Aqi = findViewById(R.id.aqi_value);
        Category = findViewById(R.id.category);
        City = findViewById(R.id.city_value);
        State = findViewById(R.id.state_value);
        Country = findViewById(R.id.country_value);
        WeatherIcon = findViewById(R.id.weather_icon);
        WeatherText = findViewById(R.id.weather_text);
        Face = findViewById(R.id.ic_face);
        OtherSideFaceColor = findViewById(R.id.other_side_face_color);
        AtmosphereCardColor = findViewById(R.id.atmosphere_card_color);
        SuggestionIcon1 = findViewById(R.id.suggestionIcon1);
        SuggestionIcon2 = findViewById(R.id.suggestionIcon2);
        SuggestionIcon3 = findViewById(R.id.suggestionIcon3);
        SuggestionIcon4 = findViewById(R.id.suggestionIcon4);
        nearestProgress = findViewById(R.id.nearest_progress_bar);

        IPHttpRequest requestIP = new IPHttpRequest();
        requestIP.execute();

        NearestHttpRequest requestNearest = new NearestHttpRequest();
        requestNearest.execute();

        goToMap = findViewById(R.id.gotomap);
    }

    public class IPHttpRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;
            try {
                url = new URL(MY_IP_URL);
            } catch (MalformedURLException exception) {
                Log.e("errorTag", "Error with creating URL", exception);
                return null;
            }

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e("errorTag", "Error in request");
            }
            return jsonResponse;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse;
            HttpURLConnection urlConnection;
            InputStream inputStream;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readInputStream(inputStream);

            return jsonResponse;
        }

        private String readInputStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }

            JSONObject parentObject;
            try {
                parentObject = new JSONObject(output.toString());
                myIP = parentObject.getString("ip");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return output.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    public class NearestHttpRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;

            try {
                url = new URL(MY_NEAREST_URL);
            } catch (MalformedURLException exception) {
                Log.e("errorTag", "Error with creating URL", exception);
                return null;
            }

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e("errorTag", "Error in request");
            }
            return jsonResponse;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse;
            HttpURLConnection urlConnection;
            InputStream inputStream;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("x-forwarded-for", myIP);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readInputStream(inputStream);

            return jsonResponse;
        }

        private String readInputStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }

            JSONObject parentObject;
            JSONObject dataObject;
            JSONObject locationObject;
            JSONArray coordinateArray;
            JSONObject weatherObject;
            JSONObject pollutionObject;
            JSONObject currentObject;
            try {
                parentObject = new JSONObject(output.toString());
                dataObject = parentObject.getJSONObject("data");
                myCity = dataObject.getString("city");
                myState = dataObject.getString("state");
                myCountry = dataObject.getString("country");
                locationObject = dataObject.getJSONObject("location");
                coordinateArray = locationObject.getJSONArray("coordinates");
                cityLongitude = coordinateArray.getDouble(0);
                cityLatitude = coordinateArray.getDouble(1);

                currentObject = dataObject.getJSONObject("current");
                weatherObject = currentObject.getJSONObject("weather");
                temperature = weatherObject.getDouble("tp");
                pressure = weatherObject.getInt("pr");
                humidity = weatherObject.getDouble("hu");
                windSpeed = weatherObject.getDouble("ws");
                windDirection = weatherObject.getDouble("wd");
                weatherIconCode = weatherObject.getString("ic");

                pollutionObject = currentObject.getJSONObject("pollution");
                aqi = pollutionObject.getInt("aqius");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return output.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            nearestProgress.setVisibility(View.GONE);

            if ((aqi > 0) && (aqi <= 50)) {
                category = getString(R.string.good);
                Face.setImageResource(R.drawable.ic_face_green);
                Face.setBackgroundColor(getResources().getColor(R.color.ic_green));
                OtherSideFaceColor.setBackgroundColor(getResources().getColor(R.color.ic_green));
                AtmosphereCardColor.setBackgroundColor(getResources().getColor(R.color.ic_green));
                SuggestionIcon1.setImageResource(R.drawable.ic_health_sport_green);
                SuggestionIcon2.setImageResource(R.drawable.ic_health_window_green);
                SuggestionIcon3.setVisibility(View.GONE);
                SuggestionIcon4.setVisibility(View.GONE);
            } else if ((aqi > 50) && (aqi <= 100)) {
                category = getString(R.string.moderate);
                Face.setImageResource(R.drawable.ic_face_yellow);
                Face.setBackgroundColor(getResources().getColor(R.color.ic_yellow));
                OtherSideFaceColor.setBackgroundColor(getResources().getColor(R.color.ic_yellow));
                AtmosphereCardColor.setBackgroundColor(getResources().getColor(R.color.ic_yellow));
                SuggestionIcon1.setImageResource(R.drawable.ic_health_sport_orange);
                SuggestionIcon2.setImageResource(R.drawable.ic_health_window_green);
                SuggestionIcon3.setImageResource(R.drawable.ic_health_mask_orange);
                SuggestionIcon4.setVisibility(View.GONE);
            } else if ((aqi > 100) && (aqi <= 150)) {
                category = getString(R.string.unhealhy_for_sensitive_groups);
                Face.setImageResource(R.drawable.ic_face_orange);
                Face.setBackgroundColor(getResources().getColor(R.color.ic_orange));
                OtherSideFaceColor.setBackgroundColor(getResources().getColor(R.color.ic_orange));
                AtmosphereCardColor.setBackgroundColor(getResources().getColor(R.color.ic_orange));
                SuggestionIcon1.setImageResource(R.drawable.ic_health_sport_red);
                SuggestionIcon2.setImageResource(R.drawable.ic_health_window_red);
                SuggestionIcon3.setImageResource(R.drawable.ic_health_mask_orange);
                SuggestionIcon4.setImageResource(R.drawable.ic_health_airpurifier_red);
            } else if ((aqi > 150) && (aqi <= 200)) {
                category = getString(R.string.unhealthy);
                Face.setImageResource(R.drawable.ic_face_red);
                Face.setBackgroundColor(getResources().getColor(R.color.ic_red));
                OtherSideFaceColor.setBackgroundColor(getResources().getColor(R.color.ic_red));
                AtmosphereCardColor.setBackgroundColor(getResources().getColor(R.color.ic_red));
                SuggestionIcon1.setImageResource(R.drawable.ic_health_sport_red);
                SuggestionIcon2.setImageResource(R.drawable.ic_health_window_red);
                SuggestionIcon3.setImageResource(R.drawable.ic_health_mask_red);
                SuggestionIcon4.setImageResource(R.drawable.ic_health_airpurifier_red);
            } else if ((aqi > 200) && (aqi <= 300)) {
                category = getString(R.string.very_unhealthy);
                Face.setImageResource(R.drawable.ic_face_maroon);
                Face.setBackgroundColor(getResources().getColor(R.color.ic_maroon));
                OtherSideFaceColor.setBackgroundColor(getResources().getColor(R.color.ic_maroon));
                AtmosphereCardColor.setBackgroundColor(getResources().getColor(R.color.ic_maroon));
                SuggestionIcon1.setImageResource(R.drawable.ic_health_sport_red);
                SuggestionIcon2.setImageResource(R.drawable.ic_health_window_red);
                SuggestionIcon3.setImageResource(R.drawable.ic_health_mask_red);
                SuggestionIcon4.setImageResource(R.drawable.ic_health_airpurifier_red);
            } else if (aqi > 300) {
                category = getString(R.string.hazardous);
                Face.setImageResource(R.drawable.ic_face_purple);
                Face.setBackgroundColor(getResources().getColor(R.color.ic_purple));
                OtherSideFaceColor.setBackgroundColor(getResources().getColor(R.color.ic_purple));
                AtmosphereCardColor.setBackgroundColor(getResources().getColor(R.color.ic_purple));
                SuggestionIcon1.setImageResource(R.drawable.ic_health_sport_red);
                SuggestionIcon2.setImageResource(R.drawable.ic_health_window_red);
                SuggestionIcon3.setImageResource(R.drawable.ic_health_mask_red);
                SuggestionIcon4.setImageResource(R.drawable.ic_health_airpurifier_red);
            }

            switch (weatherIconCode) {
                case "01d":
                    WeatherIcon.setImageResource(R.drawable.ic_01d);
                    WeatherText.setText(getResources().getString(R.string.Clear_Sky_Day));
                    break;
                case "01n":
                    WeatherIcon.setImageResource(R.drawable.ic_01n);
                    WeatherText.setText(getResources().getString(R.string.Clear_Sky_night));
                    break;
                case "02d":
                    WeatherIcon.setImageResource(R.drawable.ic_02d);
                    WeatherText.setText(getResources().getString(R.string.Few_Clouds_day));
                    break;
                case "02n":
                    WeatherIcon.setImageResource(R.drawable.ic_02n);
                    WeatherText.setText(getResources().getString(R.string.Few_Couds_night));
                    break;
                case "03d":
                    WeatherIcon.setImageResource(R.drawable.ic_03d);
                    WeatherText.setText(getResources().getString(R.string.Scattered_Clouds_day_time));
                    break;
                case "03n":
                    WeatherIcon.setImageResource(R.drawable.ic_03d);
                    WeatherText.setText(getResources().getString(R.string.Scattered_Clouds_night_time));
                    break;
                case "04d":
                    WeatherIcon.setImageResource(R.drawable.ic_04d);
                    WeatherText.setText(getResources().getString(R.string.Broken_Clouds_day_time));
                    break;
                case "04n":
                    WeatherIcon.setImageResource(R.drawable.ic_04d);
                    WeatherText.setText(getResources().getString(R.string.Broken_Clouds_night_time));
                    break;
                case "09d":
                    WeatherIcon.setImageResource(R.drawable.ic_09d);
                    WeatherText.setText(getResources().getString(R.string.Shower_Rain_day_time));
                    break;
                case "09n":
                    WeatherIcon.setImageResource(R.drawable.ic_09d);
                    WeatherText.setText(getResources().getString(R.string.Shower_Rain_night_time));
                    break;
                case "10d":
                    WeatherIcon.setImageResource(R.drawable.ic_10d);
                    WeatherText.setText(getResources().getString(R.string.Rain_day_time));
                    break;
                case "10n":
                    WeatherIcon.setImageResource(R.drawable.ic_10n);
                    WeatherText.setText(getResources().getString(R.string.Rain_night_time));
                    break;
                case "11d":
                    WeatherIcon.setImageResource(R.drawable.ic_11d);
                    WeatherText.setText(getResources().getString(R.string.Thunderstorm_day_time));
                    break;
                case "11n":
                    WeatherIcon.setImageResource(R.drawable.ic_11d);
                    WeatherText.setText(getResources().getString(R.string.Thunderstorm_night_time));
                    break;
                case "13d":
                    WeatherIcon.setImageResource(R.drawable.ic_13d);
                    WeatherText.setText(getResources().getString(R.string.Snow_day_time));
                    break;
                case "13n":
                    WeatherIcon.setImageResource(R.drawable.ic_13d);
                    WeatherText.setText(getResources().getString(R.string.Snow_night_time));
                    break;
                case "50n":
                    WeatherIcon.setImageResource(R.drawable.ic_50d);
                    WeatherText.setText(getResources().getString(R.string.Mist_night_time));
                    break;
                case "50d":
                    WeatherIcon.setImageResource(R.drawable.ic_50d);
                    WeatherText.setText(getResources().getString(R.string.Mist_day_time));
                    break;
            }

            City.setText(myCity);
            State.setText(myState);
            Country.setText(myCountry);
            Aqi.setText(String.valueOf(aqi));
            Category.setText(category);
            Temperature.setText(temperature + "°C");
            Pressure.setText(pressure + " hPa");
            Humidity.setText(humidity + "%");
            WindSpeed.setText(windSpeed + " m/s");
            WindDirection.setText(windDirection + "° due N");

            goToMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent sendDataToMap = new Intent(NearestActivity.this, SpecificCityMapActivity.class);
                    sendDataToMap.putExtra("LONGITUDE", cityLongitude);
                    sendDataToMap.putExtra("LATITUDE", cityLatitude);
                    sendDataToMap.putExtra("COUNTRY", myCountry);
                    sendDataToMap.putExtra("STATE", myState);
                    sendDataToMap.putExtra("CITY", myCity);
                    sendDataToMap.putExtra("AQI", aqi);
                    startActivity(sendDataToMap);
                }
            });

        }

    }
}
