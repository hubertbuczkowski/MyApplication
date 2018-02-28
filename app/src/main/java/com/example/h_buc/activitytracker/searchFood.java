package com.example.h_buc.activitytracker;

import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class searchFood extends AppCompatActivity {

    ListView list;
    List<String> searchResList = new ArrayList<>();
    ArrayList<Map<String, String>> searchMap = new ArrayList<>();
    Map<String, String> dialogMap = new HashMap<>();

    private float[] yData = {25.3f, 10.6f, 66.76f, 44.32f, 46.01f, 16.89f, 23.9f};
    private String[] xData = {"Mitch", "Jessica" , "Mohammad" , "Kelsey", "Sam", "Robert", "Ashley"};
    PieChart pieChart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);

        String[] words=new String[] {
                "word1", "word2", "word3", "word4", "word5"
        };

        final AutoCompleteTextView autoSearch =  this.findViewById(R.id.searchAuto);
        ArrayAdapter<String> aaStr = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,words);
        autoSearch.setAdapter(aaStr);

        ImageView searchBtn = findViewById(R.id.searchView);
        list = findViewById(R.id.SearchRes);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //String tmp = searchResList.get(position);
                //Toast.makeText(getApplicationContext(), searchMap.get(position).get(tmp), Toast.LENGTH_SHORT).show();
                details(searchMap.get(position).get("ID"));

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                search( autoSearch.getText().toString() );
            }
        });

    }

    private void createDialog(){
        final Dialog dialog = new Dialog(searchFood.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_food_dialog);

        final EditText editGrams = dialog.findViewById(R.id.gramsConsumed);

        editGrams.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                dialogMap.put("WEIGHT_INPUT", editable.toString());
                addDataSet();
            }
        });

        pieChart = dialog.findViewById(R.id.chart1);

        pieChart.setRotationEnabled(true);
        //pieChart.setUsePercentValues(true);
        //pieChart.setHoleColor(Color.BLUE);
        //pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText(dialogMap.get("NAME"));
        pieChart.setCenterTextSize(10);
        //pieChart.setDrawEntryLabels(true);
        //pieChart.setEntryLabelTextSize(20);
        //More options just check out the documentation!

        addDataSet();
        dialog.show();
    }

    private void addDataSet() {
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        float divider = 1;
        float dividerInput = 1;
        if(dialogMap.get("WEIGHT") != "null"){
            divider = Float.parseFloat(dialogMap.get("WEIGHT")) / 100;
        }
        if(!dialogMap.get("WEIGHT_INPUT").isEmpty()){
            dividerInput = (Float.parseFloat(dialogMap.get("WEIGHT_INPUT")) / 100);
        }


        if(dialogMap.get("FAT") != "null"){
            yEntrys.add(new PieEntry((Float.parseFloat(dialogMap.get("FAT")) * dividerInput)  / divider, 1));
        }
        if(dialogMap.get("CARBS") != "null") {
            yEntrys.add(new PieEntry((Float.parseFloat(dialogMap.get("CARBS")) * dividerInput) / divider, 2));
        }
        if(dialogMap.get("PROTEIN") != "null") {
            yEntrys.add(new PieEntry((Float.parseFloat(dialogMap.get("PROTEIN")) * dividerInput) / divider, 3));
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "Micro nutrients");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);

        pieDataSet.setColors(colors);

        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }



    void details(String str){
        new itemData().execute(str);
    }

    void search(String str){
        new urlData().execute(str);

    }

    public class urlData extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();
            String addr = "https://api.nutritionix.com/v1_1/search/" + args[0].toString() + "?results=0%3A20&fields=item_name%2Cbrand_name%2Citem_id&appId=8ff256cf&appKey=+b21e4bba7884e8ae4e928b811afc0d5f";

            try {
                URL url = new URL(addr);
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
            searchResList.clear();
            searchMap.clear();
            try {
                JSONArray json = new JSONObject(result).getJSONArray("hits");
                for(int i=0; i<json.length(); i++)
                {
                    JSONObject jsonTemp = json.getJSONObject(i).getJSONObject("fields");
                    if(!searchResList.contains(jsonTemp.getString("item_name")))
                    {
                        Map<String, String> tempMap = new HashMap<>();
                        searchResList.add(jsonTemp.getString("item_name"));
                        tempMap.put("ID", jsonTemp.getString("item_id"));
                        tempMap.put("NAME", jsonTemp.getString("item_name"));
                        searchMap.add(tempMap);
                    }
                }
                list.setAdapter(new ArrayAdapter(searchFood.this, android.R.layout.simple_list_item_1, searchResList));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class itemData extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();
            String addr = "https://api.nutritionix.com/v1_1/item?id="+ args[0].toString() +"&appId=8ff256cf&appKey=b21e4bba7884e8ae4e928b811afc0d5f";

            try {
                URL url = new URL(addr);
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
            dialogMap.clear();
            try {
                JSONObject json = new JSONObject(result);
                dialogMap.put("ID", json.getString("item_id"));
                dialogMap.put("NAME", json.getString("item_name"));
                dialogMap.put("CALORIES", json.getString("nf_calories"));
                dialogMap.put("FAT", json.getString("nf_total_fat"));
                dialogMap.put("SATURATED", json.getString("nf_saturated_fat"));
                dialogMap.put("CARBS", json.getString("nf_total_carbohydrate"));
                dialogMap.put("SUGAR", json.getString("nf_sugars"));
                dialogMap.put("PROTEIN", json.getString("nf_protein"));
                dialogMap.put("WEIGHT", json.getString("nf_serving_weight_grams"));
                dialogMap.put("WEIGHT_INPUT", "");
                //allergens !
                createDialog();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

