package com.example.h_buc.activitytracker;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.h_buc.activitytracker.Helpers.CheckConnection;
import com.example.h_buc.activitytracker.Helpers.FirebaseManagement;
import com.example.h_buc.activitytracker.Helpers.internalDatabaseManager;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class searchFood extends AppCompatActivity {

    ListView list;
    List<String> searchResList = new ArrayList<>();
    ArrayList<Map<String, String>> searchMap = new ArrayList<>();
    Map<String, String> dialogMap = new HashMap<>();
    String titleString;

    PieChart mPieChart;
    TextView title;
    TextView txt;

    SurfaceView cameraView;
    CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_food);
        title = findViewById(R.id.SearchTitle);
        titleString = getIntent().getExtras().getString("Meal Type");
        title.setText(titleString);

        String[] words = new String[]{
                "Chicken", "Chives", "Porridge", "Beef", "Egg"
        };

        //add simple data for auto complete
        final AutoCompleteTextView autoSearch = this.findViewById(R.id.searchAuto);
        ArrayAdapter<String> aaStr = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, words);
        autoSearch.setAdapter(aaStr);

        ImageView searchBtn = findViewById(R.id.searchView);
        ImageView barcodeBtn = findViewById(R.id.barcodeView);
        list = findViewById(R.id.SearchRes);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                details(searchMap.get(position).get("ID"));

            }
        });

        //populate initial data in linear layout for better efficiency
        Map<String,String> temp = new HashMap<>();
        switch (titleString)
        {
            case "Breakfast":
                searchResList.add("Porridge");
                temp.put("ID", "5499dbf7e6dcce634c4693d5");
                searchMap.add(0, temp);
                searchResList.add("Egg");
                temp.put("ID", "513fceb375b8dbbc21000152");
                searchMap.add(1, temp);
                searchResList.add("Bread");
                temp.put("ID", "513fceb675b8dbbc210026a5");
                searchMap.add(2, temp);
                searchResList.add("Butter");
                temp.put("ID", "513fceb375b8dbbc21000003");
                searchMap.add(3, temp);
                searchResList.add("Milk");
                temp.put("ID", "513fceb375b8dbbc210000f1");
                searchMap.add(4, temp);
                break;
            case "Lunch":
                searchResList.add("Bread");
                temp.put("ID", "513fceb675b8dbbc210026a5");
                searchMap.add(0, temp);
                searchResList.add("Butter");
                temp.put("ID", "513fceb375b8dbbc21000003");
                searchMap.add(1, temp);
                searchResList.add("Porridge");
                temp.put("ID", "5499dbf7e6dcce634c4693d5");
                searchMap.add(2, temp);
                searchResList.add("Egg");
                temp.put("ID", "513fceb375b8dbbc21000152");
                searchMap.add(3, temp);
                searchResList.add("Sausage");
                temp.put("ID", "463d623713c29a3885180c5e");
                searchMap.add(4, temp);
                searchResList.add("Beans");
                temp.put("ID", "513fceb675b8dbbc21002189");
                searchMap.add(5, temp);
                searchResList.add("Rice");
                temp.put("ID", "513fceb775b8dbbc21002e43");
                searchMap.add(6, temp);
                searchResList.add("Chicken");
                temp.put("ID", "51c37c1297c3e6d272824fd5");
                searchMap.add(7, temp);
                break;
            case "Dinner":
                searchResList.add("Rice");
                temp.put("ID", "513fceb775b8dbbc21002e43");
                searchMap.add(0, temp);
                searchResList.add("Chicken");
                temp.put("ID", "51c37c1297c3e6d272824fd5");
                searchMap.add(0, temp);
                searchResList.add("Beef");
                temp.put("ID", "513fceb775b8dbbc210031f9");
                searchMap.add(0, temp);
                searchResList.add("Potato");
                temp.put("ID", "513fceb575b8dbbc210017d2");
                searchMap.add(0, temp);
                searchResList.add("Lettuce");
                temp.put("ID", "513fceb575b8dbbc210015a0");
                searchMap.add(0, temp);
                searchResList.add("Pasta");
                temp.put("ID", "56d9a58973232b6569114ef5");
                searchMap.add(0, temp);
                searchResList.add("Tomato");
                temp.put("ID", "513fceb575b8dbbc21001743");
                searchMap.add(0, temp);
                break;
            case "Supper":
                searchResList.add("Pasta");
                temp.put("ID", "56d9a58973232b6569114ef5");
                searchMap.add(0, temp);
                searchResList.add("Rice");
                temp.put("ID", "513fceb775b8dbbc21002e43");
                searchMap.add(0, temp);
                searchResList.add("Sausage");
                temp.put("ID", "463d623713c29a3885180c5e");
                searchMap.add(0, temp);
                searchResList.add("Pork");
                temp.put("ID", "57cd18543f8b952b70e36da6");
                searchMap.add(0, temp);
                searchResList.add("Pasta");
                temp.put("ID", "56d9a58973232b6569114ef5");
                searchMap.add(0, temp);
                searchResList.add("513fceb775b8dbbc210031f9");
                temp.put("ID", "Beef");
                searchMap.add(0, temp);
                searchResList.add("51c37c1297c3e6d272824fd5");
                temp.put("ID", "Chicken");
                searchMap.add(0, temp);
                break;
            case "Snack":
                searchResList.add("Apple");
                temp.put("ID", "513fceb475b8dbbc21000f93");
                searchMap.add(0, temp);
                searchResList.add("Carrot");
                temp.put("ID", "513fceb575b8dbbc210014e2");
                searchMap.add(0, temp);
                searchResList.add("Protein bar");
                temp.put("ID", "5631369e517dab4c12db31de");
                searchMap.add(0, temp);
                searchResList.add("Orange");
                temp.put("ID", "513fceb575b8dbbc210010bf");
                searchMap.add(0, temp);
                searchResList.add("Grapes");
                temp.put("ID", "513fceb575b8dbbc21001054");
                searchMap.add(0, temp);
                searchResList.add("Pineapple");
                temp.put("ID", "513fceb575b8dbbc21001132");
                searchMap.add(0, temp);
                break;
        }


        list.setAdapter(new ArrayAdapter(searchFood.this, android.R.layout.simple_list_item_1, searchResList));

        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                search(autoSearch.getText().toString());
            }
        });

        barcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new connectionChecker().execute();
            }
        });

    }

    //open camera dialog with barcode scanner detector
    private void startCamera() {
        final Dialog dialog = new Dialog(searchFood.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.camera_dialog);

        cameraView = dialog.findViewById(R.id.camera_view);

        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.EAN_13)
                        .build();

        cameraSource = new CameraSource
                .Builder(dialog.getContext(), barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setAutoFocusEnabled(true)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {
                    if (ActivityCompat.checkSelfPermission(dialog.getContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0) {
                    System.out.print(barcodes.valueAt(0).displayValue);
                    new itemData().execute("ups", barcodes.valueAt(0).displayValue);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    //in case no internet connection, it allows user to enter data manually
    //opens dialog for manual input
    private void manualDialog(String mealName) {
        final Dialog dialog = new Dialog(searchFood.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.manual_food_editor);

        final EditText protein = dialog.findViewById(R.id.editProtein);
        final EditText carbs = dialog.findViewById(R.id.editCarbs);
        final EditText fat = dialog.findViewById(R.id.editFat);
        final EditText calories = dialog.findViewById(R.id.editCalories);
        final EditText weight = dialog.findViewById(R.id.editWeight);
        final EditText name = dialog.findViewById(R.id.editName);
        final Button cancel = dialog.findViewById(R.id.manualCancel);
        final Button add = dialog.findViewById(R.id.manualAdd);

        if(mealName != null)
        {
            name.setText(mealName);
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int err = 0;
                if(protein.getText().toString().isEmpty())
                {
                    err=1;
                    protein.setError("This field cannot be empty");
                }
                if(carbs.getText().toString().isEmpty())
                {
                    err=1;
                    carbs.setError("This field cannot be empty");
                }
                if(fat.getText().toString().isEmpty())
                {
                    err=1;
                    fat.setError("This field cannot be empty");
                }
                if(calories.getText().toString().isEmpty())
                {
                    err=1;
                    calories.setError("This field cannot be empty");
                }

                if(weight.getText().toString().isEmpty())
                {
                    err=1;
                    weight.setError("This field cannot be empty");
                }
                if(name.getText().toString().isEmpty())
                {
                    err=1;
                    name.setError("This field cannot be empty");
                }
                if(err == 0)
                {
                    int weightInt = Integer.parseInt(weight.getText().toString());
                    dialogMap.put("PROTEIN_INPUT", String.valueOf((Float.parseFloat(protein.getText().toString())/100)*weightInt));
                    dialogMap.put("CARBS_INPUT", String.valueOf((Float.parseFloat(carbs.getText().toString())/100)*weightInt));
                    dialogMap.put("FAT_INPUT", String.valueOf((Float.parseFloat(fat.getText().toString())/100)*weightInt));
                    dialogMap.put("CALS_INPUT", String.valueOf((Float.parseFloat(calories.getText().toString())/100)*weightInt));
                    dialogMap.put("WEIGHT_INPUT", String.valueOf((Float.parseFloat(protein.getText().toString())/100)*weightInt));
                    dialogMap.put("NAME", name.getText().toString());
                    dialogMap.put("ID", "0");
                    addFood();
                    dialog.dismiss();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        txt = dialog.findViewById(R.id.textViewCalories);


        dialog.show();
    }

    //open dialog with data from food database
    private void createDialog() {
        final Dialog dialog = new Dialog(searchFood.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_food_dialog);

        final EditText editGrams = dialog.findViewById(R.id.gramsConsumed);
        final Button add = dialog.findViewById(R.id.update);
        txt = dialog.findViewById(R.id.textViewCalories);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!dialogMap.get("WEIGHT_INPUT").isEmpty())
                {
                    addFood();
                    dialog.hide();
                }
                else
                {
                    editGrams.setError("Enter weight, you can't eat air");
                }
            }
        });



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

        mPieChart = dialog.findViewById(R.id.chart1);

        TextView tx = dialog.findViewById(R.id.foodText);
        tx.setText(dialogMap.get("NAME"));

        mPieChart.addPieSlice(new PieModel("Fat", Float.parseFloat(dialogMap.get("FAT")), Color.parseColor("#FF1944")));
        mPieChart.addPieSlice(new PieModel("Protein", Float.parseFloat(dialogMap.get("PROTEIN")), Color.parseColor("#00B5FF")));
        mPieChart.addPieSlice(new PieModel("Carbohydrates", Float.parseFloat(dialogMap.get("CARBS")), Color.parseColor("#CCC314")));
        txt.setText(dialogMap.get("CALORIES")+" cal");
        if (dialogMap.get("WEIGHT") != "null") {
            editGrams.setText(dialogMap.get("WEIGHT"));
        }
        else
        {
            editGrams.setText("100");
        }

        mPieChart.startAnimation();
        dialog.show();
    }

    //adds food details into databases
    private void addFood(){
        final String date = new SimpleDateFormat("ddMMyyyy").format(new Date());
        final internalDatabaseManager db = new internalDatabaseManager(getApplicationContext());
        new Thread(new Runnable() {
            public void run() {
                if(CheckConnection.InternetConnection())
                {
                    FirebaseManagement.addFood(dialogMap.get("NAME"), dialogMap.get("ID"), dialogMap.get("WEIGHT_INPUT"), dialogMap.get("PROTEIN_INPUT"),
                            dialogMap.get("CARBS_INPUT"), dialogMap.get("FAT_INPUT"), dialogMap.get("CALS_INPUT"), titleString);

                    db.addMeal(date, dialogMap.get("ID"), dialogMap.get("NAME"), dialogMap.get("WEIGHT_INPUT"), dialogMap.get("PROTEIN_INPUT"),
                            dialogMap.get("CARBS_INPUT"), dialogMap.get("FAT_INPUT"), dialogMap.get("CALS_INPUT"), titleString, true);

                }
                else
                {
                    db.addMeal(date, dialogMap.get("ID"), dialogMap.get("NAME"), dialogMap.get("WEIGHT_INPUT"), dialogMap.get("PROTEIN_INPUT"),
                            dialogMap.get("CARBS_INPUT"), dialogMap.get("FAT_INPUT"), dialogMap.get("CALS_INPUT"), titleString, false);
                }
            }
        }).start();


    }

    //updates pie chart when user changes weight to show most up to date micro nutrients details
    private void addDataSet() {

        float divider = 1;
        float dividerInput = 1;
        if (dialogMap.get("WEIGHT") != "null") {
            divider = Float.parseFloat(dialogMap.get("WEIGHT")) / 100;
        }
        if (!dialogMap.get("WEIGHT_INPUT").isEmpty()) {
            dividerInput = (Float.parseFloat(dialogMap.get("WEIGHT_INPUT")) / 100);
        }

        mPieChart.clearChart();

        if (dialogMap.get("FAT") != "null") {
            float fat = (Float.parseFloat(dialogMap.get("FAT")) * dividerInput) / divider;
            dialogMap.put("FAT_INPUT", String.valueOf(fat));
            mPieChart.addPieSlice(new PieModel("Fat", fat, Color.parseColor("#FF1944")));
        }
        if (dialogMap.get("CARBS") != "null") {
            float carbs = (Float.parseFloat(dialogMap.get("CARBS")) * dividerInput) / divider;
            dialogMap.put("CARBS_INPUT", String.valueOf(carbs));
            mPieChart.addPieSlice(new PieModel("Carbohydrates", carbs, Color.parseColor("#00B5FF")));
        }
        if (dialogMap.get("PROTEIN") != "null") {
            float protein = (Float.parseFloat(dialogMap.get("PROTEIN")) * dividerInput) / divider;
            dialogMap.put("PROTEIN_INPUT", String.valueOf(protein));
            mPieChart.addPieSlice(new PieModel("Protein", protein, Color.parseColor("#CCC314")));
        }
        if (dialogMap.get("CALORIES") != "null") {
            float cals = (Float.parseFloat(dialogMap.get("CALORIES")) * dividerInput) / divider;
            dialogMap.put("CALS_INPUT", String.valueOf(cals));
            txt.setText(dialogMap.get("CALS_INPUT")+" cal");
        }

    }

    //send url request to provide details of selected ingredient
    void details(String str){
        new itemData().execute("item", str);
    }

    //send url request to search for specified by user ingredient
    void search(final String str){
        new urlData().execute(str);
    }

    //send url search request and process response
    public class urlData extends AsyncTask<String, String, String[]> {

        HttpURLConnection urlConnection;

        @Override
        protected String[] doInBackground(String... args) {
            if(CheckConnection.InternetConnection()) {
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

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    urlConnection.disconnect();
                }
                return new String[] {result.toString()};
            }
            else
            {
                return new String[] {"Error", args[0].toString()};
            }
        }

        @Override
        protected void onPostExecute(String[] result) {
            if(!result[0].equals("Error")) {
                searchResList.clear();
                searchMap.clear();
                try {
                    JSONArray json = new JSONObject(result[0]).getJSONArray("hits");
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject jsonTemp = json.getJSONObject(i).getJSONObject("fields");
                        if (!searchResList.contains(jsonTemp.getString("item_name"))) {
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
            else
            {
                manualDialog(result[1]);
            }
        }
    }

    //send url request for item detailed data
    public class itemData extends AsyncTask<String, String, String> {

        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... args) {

            StringBuilder result = new StringBuilder();
            String addr = null;
            if(args[0].toString().equals("item"))
            {
                addr = "https://api.nutritionix.com/v1_1/item?id="+ args[1].toString() +"&appId=8ff256cf&appKey=b21e4bba7884e8ae4e928b811afc0d5f";
            }
            else
            {
                addr = "https://api.nutritionix.com/v1_1/item?upc="+ args[1].toString() +"&appId=8ff256cf&appKey=b21e4bba7884e8ae4e928b811afc0d5f";
            }

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
                Toast.makeText(getApplicationContext(), "Item doesn't exist in database", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //checks connection with internet
    //when there is no connection, it opens manual dialog
    public class connectionChecker extends AsyncTask<Void, Void, Boolean>{

        @Override
        public Boolean doInBackground(Void... params) {
            return CheckConnection.InternetConnection();
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if(result)
            {
                startCamera();
            }
            else
            {
                manualDialog("");
            }
        }
    }


}

