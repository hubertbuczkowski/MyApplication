package com.example.h_buc.activitytracker.Helpers;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by h_buc on 02/02/2018.
 */

public class urlData extends AsyncTask<String, String, String> {

    HttpURLConnection urlConnection;

    @Override
    protected String doInBackground(String... args) {

        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL("https://api.nutritionix.com/v1_1/search/egg?results=0%3A20&fields=item_name%2Cbrand_name%2Citem_id&appId=8ff256cf&appKey=+b21e4bba7884e8ae4e928b811afc0d5f");
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }


        return result.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            JSONArray json = new JSONObject(result).getJSONArray("hits");
            for(int i=0; i<json.length(); i++)
            {
                JSONObject jsonTemp = json.getJSONObject(i).getJSONObject("fields");

                System.out.println(jsonTemp.getString("item_name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
