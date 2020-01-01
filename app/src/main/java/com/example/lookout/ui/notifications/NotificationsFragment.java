package com.example.lookout.ui.notifications;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

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

public class NotificationsFragment extends Fragment {

//    private String copyOutput;
//    private String myIP;
    private String myCity;
    private String myState;
    private String myCountry;
    private String[] countryList = new String[96];
    private String[] stateList;
    private String[] cityList;
    private AutoCompleteTextView countrySearch;
    private AutoCompleteTextView stateSearch;
    private AutoCompleteTextView citySearch;
    private TextView test;

    //    private static final String MY_IP_URL = "https://api.ipify.org?format=json";
    private final String COUNTRY_LIST_URL = "https://api.airvisual.com/v2/countries?key=9a11661d-a1a4-4629-8030-3669adaade7d";
    private final String STATE_LIST_URL = "https://api.airvisual.com/v2/states?country=" + myCountry + "&key=9a11661d-a1a4-4629-8030-3669adaade7d";
    private final String CITY_LIST_URL = "https://api.airvisual.com/v2/cities?state=" + myState + "&country=" + myCountry + "&key=9a11661d-a1a4-4629-8030-3669adaade7d";
    private final String SPECIFIC_CITY_URL = "https://api.airvisual.com/v2/city?city=" + myCity + "&state=" + myState + "&country=" + myCountry + "&key=9a11661d-a1a4-4629-8030-3669adaade7d";


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        Button nearestData;
        nearestData = root.findViewById(R.id.nearest_data_button);
        nearestData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNearest = new Intent(getActivity(), NearestActivity.class);
                startActivity(intentNearest);
            }
        });

        CardView aqiCard;
        aqiCard = root.findViewById(R.id.aqi_card);
        aqiCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentNearest = new Intent(getActivity(), CARDInfo.class);
                startActivity(intentNearest);
            }
        });

        countrySearch = root.findViewById(R.id.country_search);
        stateSearch = root.findViewById(R.id.state_search);
        citySearch = root.findViewById(R.id.city_search);
        test = root.findViewById(R.id.testyoyo);

        COUNTRYHttpRequest requestCountry = new COUNTRYHttpRequest();
        requestCountry.execute();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, countryList);
        countrySearch.setAdapter(adapter);
        countrySearch.setHint("Search your Country here");
        countrySearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myCountry = countrySearch.getText().toString();

                STATEHttpRequest requestState = new STATEHttpRequest();
                requestState.execute();
            }
        });



        stateSearch.setHint("Search your State here");
//        stateSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                myCountry = countrySearch.getText().toString();
//            }
//        });

        citySearch.setHint("Search your City here");


        return root;
    }


    public class COUNTRYHttpRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;
            try {
                url = new URL(COUNTRY_LIST_URL);
            } catch (MalformedURLException exception) {
                Log.e("errorTag", "Error with creating URL", exception);
                return null;
            }

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e("errorTag", "Error in country request");
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
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
            urlConnection.disconnect();

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

            String finalOutput = output.toString();
            JSONObject parentObject;
            JSONArray dataArray;

            try {
                parentObject = new JSONObject(finalOutput);
                dataArray = parentObject.getJSONArray("data");
                for (int i = 0; i < 96; i++) {
                    countryList[i] = dataArray.getJSONObject(i).getString("country");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return finalOutput;
        }
    }

    public class STATEHttpRequest extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... urls) {

            URL url;
            try {
                url = new URL(STATE_LIST_URL);
            } catch (MalformedURLException exception) {
                Log.e("errorTag", "Error with creating URL", exception);
                return null;
            }

            String jsonResponse = "";
            try {

                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e("errorTag", "Error in state request");
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            test.setText(s);
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
            urlConnection.disconnect();

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

            String finalOutput = output.toString();
//            JSONObject parentObject;
//            JSONArray dataArray;
//
//            try {
//                parentObject = new JSONObject(finalOutput);
//                dataArray = parentObject.getJSONArray("data");
//                for (int i = 0; i < 96; i++) {
//                    countryList[i] = dataArray.getJSONObject(i).getString("country");
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            return finalOutput;
        }
    }
}

