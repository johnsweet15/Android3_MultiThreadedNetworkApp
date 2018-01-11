package com.example.john.multithreadednetworkapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private static final int TOOLBAR_ALPHA = 120;
    private static final String URL = "http://www.pcs.cnu.edu/~kperkins/pets/pets.json";
    private static final String IMAGE_URL = "http://www.tetonsoftware.com";
    private static final String NO_NETWORK = "No Network Connection";
    private static final String NETWORK_UNAVAILABLE = "The Network is unavailable. Please try your request again later.";
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SharedPreferences myPreference;
    private Spinner spinner;
    private String currentUrl;
    private String s;
    private ArrayList<Pet> pets;
    private final ImageTask imageTask = new ImageTask(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.getBackground().setAlpha(TOOLBAR_ALPHA);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        s = sp.getString("JSON_url","-1");

        if(s.equals(URL)) {
            spinner.setVisibility(View.GONE);
        }
        else {
            spinner.setVisibility(View.VISIBLE);
        }

        myPreference = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences myPref, String key) {
                currentUrl = myPref.getString(key, null);
                s = myPreference.getString("JSON_url","-1");
                if(s.equals(URL)) {
                    spinner.setVisibility(View.GONE);
                }
                else {
                    spinner.setVisibility(View.VISIBLE);
                }
                if(isOnline()) {
                    MyAsyncTask myTask = new MyAsyncTask(MainActivity.this);
                    myTask.execute(currentUrl);
                }
            }
        };
        myPreference.registerOnSharedPreferenceChangeListener(listener);

        //alert user if there is no network connectivity
        if(!isOnline()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(NO_NETWORK);
            builder.setMessage(NETWORK_UNAVAILABLE);
            builder.setPositiveButton("OK", null);
            builder.create().show();
        }
        else if(!(s.equals(URL))) {
            MyAsyncTask myTask = new MyAsyncTask(MainActivity.this);
            myTask.execute(s);
            imageTask.execute(IMAGE_URL + "/pets/p3" + ".png");
        }
    }

    public void doJSON(String result) {
        s = result;
        pets = new ArrayList<Pet>();
        final ArrayList<String> petNames = new ArrayList<String>();
        final ArrayList<String> petImages = new ArrayList<String>();

        try {
            JSONObject json = new JSONObject(result);
            JSONArray jsonArray = new JSONArray(json.optString("pets"));

            Pet pet = new Pet();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                pet.setName(jsonObject.optString("name"));
                pet.setFile(jsonObject.optString("file"));
                pets.add(pet);
                petNames.add(jsonObject.optString("name"));
            }

            spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, petNames));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

        String imageURL = IMAGE_URL + "/pets/p" + (position + 3) + ".png";

        ImageTask imageTask = new ImageTask(this);
        imageTask.execute(imageURL);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        //check if mobile or wifi is connected
        return(cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_settings) {
            Intent myIntent = new Intent(this, SettingsActivity.class);
            startActivity(myIntent);
        }
        else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
