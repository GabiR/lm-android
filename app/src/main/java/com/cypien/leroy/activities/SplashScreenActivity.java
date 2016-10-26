package com.cypien.leroy.activities;/*
 * Created by Alex on 05.09.2016.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cypien.leroy.R;
import com.cypien.leroy.models.Store;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.DatabaseConnector;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SplashScreenActivity extends AppCompatActivity {
    private SharedPreferences.Editor spEditor;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseMessaging.getInstance().subscribeToTopic("leroyAndroid");
        setContentView(R.layout.splash_screen);


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Connections.isNetworkConnected(this)) {

            new GetStore().execute(null, null, null);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1000);
        }
    }

    public class GetStore extends AsyncTask<Void, Void, Void> {
        private void uploadStores() throws JSONException {
            DatabaseConnector.getHelper(SplashScreenActivity.this).deleteAllStores();
            JSONObject jsonObject = makePublicRequest();
            JSONArray jsonArray;
            try {
                jsonArray = jsonObject.getJSONArray("result");
            } catch (Exception e) {
                jsonArray = null;
            }
            int size = jsonArray != null ? jsonArray.length() : 0;
            for (int i = 0; i < size; i++) {
                JSONObject jsn = jsonArray.getJSONObject(i);
                Store store = new Store();
                store.setId(jsn.getString("contact_id"));
                store.setAddress(jsn.getString("contact_adresa"));
                store.setFacebookAddress(jsn.getString("contact_facebook"));
                store.setLatitude(jsn.getDouble("contact_lat"));
                store.setLongitude(jsn.getDouble("contact_lng"));
                store.setPhone(jsn.getString("contact_tel"));
                store.setOpen("Luni - Sambata: " + jsn.getString("contact_orar_rest_deschidere") + ":00 - " +
                        jsn.getString("contact_orar_rest_inchidere") + ":00\nDuminica: " + jsn.getString("contact_orar_duminica_deschidere") + ":00 - " +
                        jsn.getString("contact_orar_duminica_inchidere") + ":00");
                store.setName(jsn.getString("contact_magazin_title"));

                DatabaseConnector.getHelper(SplashScreenActivity.this).insertStore(store);
            }
        }

        public JSONObject makePublicRequest() {
            ArrayList<Integer> parameters = new ArrayList<>();
            parameters.add(0);
            parameters.add(100);
            JSONObject request = new JSONObject();
            try {
                request.put("method", "contact_get_all");
                request.put("params", new JSONArray(parameters));

                return new JSONObject(getRequest("http://www.leroymerlin.ro/api/publicEndpoint", "q=" + request.toString()));
            } catch (JSONException e) {
                Log.e("eroare", e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                uploadStores();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected String getRequest(String... info) {
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL url = null;
            StringBuilder sb = new StringBuilder();
            InputStreamReader isr = null;
            BufferedReader reader = null;

            try {
                url = new URL(info[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setInstanceFollowRedirects(false);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                connection.setRequestMethod("POST");
                request = new OutputStreamWriter(connection.getOutputStream());

                request.write(info[1]);
                request.flush();
                request.close();

                String line = "";
                isr = new InputStreamReader(connection.getInputStream());
                reader = new BufferedReader(isr);
                line = reader.readLine();
                while (line != null) {
                    sb.append(line);
                    try {
                        line = reader.readLine();
                    } catch (Exception e) {
                        line = null;
                    }
                }
            } catch (Exception e) {
                Log.e("eroare", e.toString());
                e.printStackTrace();
            }
            if (sb != null && !sb.equals(""))
                return sb.toString();
            else return "";
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
