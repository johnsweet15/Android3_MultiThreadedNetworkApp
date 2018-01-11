package com.example.john.multithreadednetworkapp;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MyAsyncTask extends AsyncTask<String, Void, String> {

    private MainActivity myActivity;
    private static final int TIMEOUT = 1000;
    private static final int READ_THIS_AMOUNT = 8096;
    private static final String ERROR = "error";
    private String currURL = "";
    private String s;

    public MyAsyncTask(MainActivity activity) {
        this.myActivity = activity;
    }

    @Override
    protected String doInBackground(String... params) {
        currURL = params[0];

        try {
            URL url = new URL(currURL);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);
            connection.setRequestProperty("Accept-Charset", "UTF-8");

            BufferedReader in = null;
            try {
                connection.connect();

                int statusCode = connection.getResponseCode();
                if (statusCode / 100 != 2) {
                    return ERROR + statusCode;
                }

                in = new BufferedReader(new InputStreamReader(connection.getInputStream()), READ_THIS_AMOUNT);

                String myData;
                StringBuffer buffer = new StringBuffer();

                while ((myData = in.readLine()) != null) {
                    buffer.append(myData);
                }

                return buffer.toString();

            }catch (IOException e){
                e.printStackTrace();
            }finally {
                if(in != null) {
                    in.close();
                }
                connection.disconnect();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if(!(result.equals(ERROR + "404"))) {
            myActivity.doJSON(result);
        }
        else {
            Toast.makeText(myActivity, "ERROR when connecting to: " + currURL + "\nServer returned 404", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}