package com.cypien.leroy.utils;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Alex on 7/16/2015.
 */
public class WebServiceConnector extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... info) {
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
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
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
            e.printStackTrace();
        }
        if(sb!= null && !sb.equals(""))
            return sb.toString();
        else return "";
    }
}

