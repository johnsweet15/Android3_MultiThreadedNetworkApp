package com.example.john.multithreadednetworkapp;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class ImageTask extends AsyncTask<String, Void, Drawable> {

    private MainActivity myActivity;

    public ImageTask(MainActivity activity) {
        this.myActivity = activity;
    }

    @Override
    protected Drawable doInBackground(String... params) {
        String url = params[0];

        try {
            InputStream in = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(in, "name");
            return d;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Drawable d) {
        RelativeLayout layout = (RelativeLayout)myActivity.findViewById(R.id.relativeLayout);
        layout.setBackground(d);
    }
}
