package com.authentik.ke;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.Reading;
import com.authentik.model.System;
import com.authentik.utils.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Barcode_Instrument_List extends AppCompatActivity {

    TextView currUser;
    TextView dateAndTime;
    TextView appPath;
    SharedPreferences sharedPreferences;
    TextView barcode_tv;
    ListView instrument_barcode_list;
    DatabaseHelper db;
    int totalReadingsTaken;
    List<Instrument> instrumentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode__instrument__list);

        currUser = findViewById(R.id.username_tv);
        dateAndTime = findViewById(R.id.date_time_tv);
        barcode_tv = findViewById(R.id.barcode_id_tv);
        instrument_barcode_list = findViewById(R.id.barcode_instrument_list);
        appPath = findViewById(R.id.app_path_tv);

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("Username", "no name");
        final String shift_id = sharedPreferences.getString("shift_id", "-");

        db = new DatabaseHelper(getApplicationContext());

        currUser.setText(value);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String datetime = ft.format(dNow);
        dateAndTime.setText(datetime);


        instrumentList = (List<Instrument>) getIntent().getSerializableExtra("Instrument_list");
        String barcode_id = getIntent().getStringExtra("barcode_id");

        Log.i("Barcode id",barcode_id);

        appPath.setText("Barcode: " + barcode_id + " > " + "Instruments");

        List<String> inst_names = new ArrayList<>();

        for(int i=0; i<instrumentList.size(); i++) {
            Log.i("Instrument" + i, instrumentList.get(i).getName());
            inst_names.add(instrumentList.get(i).getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,inst_names) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position,convertView,parent);
                int instrumentReadingTaken = db.getInstrumentStatus(instrumentList.get(position).getId(), shift_id);
                if (instrumentReadingTaken == 0) {
                    view.setBackgroundColor(Color.parseColor("#B22222"));
                }
                else {
                    view.setBackgroundColor(Color.parseColor("#68922e"));
                    totalReadingsTaken++;
                }
                return view;
            }
        };
        instrument_barcode_list.setAdapter(adapter);

        instrument_barcode_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new Instrument();
                Instrument instrument = instrumentList.get(position);
                System system = db.getSystemFromInstrument(instrument);
                Plant plant = db.getPlantFromSystem(system);

                Intent intent2 = new Intent(Barcode_Instrument_List.this, Tag_information.class);
                intent2.putExtra("instrument_object", instrument);
                intent2.putExtra("system_object", system);
                intent2.putExtra("plant_object", plant);
                startActivity(intent2);
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Barcode_Instrument_List.this);
        builder.setTitle("Confirmation!");
        builder.setMessage("Exit without saving?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                startActivity(new Intent(Barcode_Instrument_List.this,Plant_List.class));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void goBack(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Barcode_Instrument_List.this);
        builder.setTitle("Confirmation!");
        builder.setMessage("Exit without saving?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                startActivity(new Intent(Barcode_Instrument_List.this,Plant_List.class));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void saveReadings(View view) {

//        class ReadingSync extends AsyncTask<Void, Void, String> {
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
////                super.onPostExecute(s);
////
////                try {
////
////                    JSONArray arr = new JSONArray("{" + s + "}");
//////                    JSONObject obj = new JSONObject(s);
////                    Log.i("JSON response",s);
////
////                    if (arr.length() == 0) {
////                        Toast.makeText(getApplicationContext(), "Unable to retrieve data", Toast.LENGTH_SHORT).show();
////                    } else {
////                        for (int i = 0; i < arr.length(); i++) {
////                        JSONObject obj = arr.getJSONObject(i);
////                            int code = obj.getInt("success");
////                            String msg = obj.getString("message");
////                        }
////
//////                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
////
////                    }
////
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//
//            }
//
//            @Override
//            protected String doInBackground(Void... voids) {
//
//                String status = " ";
//
//                Log.i("Instrument List size ",Integer.toString(instrumentList.size()));
//                Log.i("total Readings Taken", Integer.toString(totalReadingsTaken));
//
//                final HashMap<String,String> params = new HashMap<>();
////        JSONObject params = new JSONObject();
//                List<Reading> readingList = db.getAllReadings();
//
//                if(readingList.size() > 0) {
//
//                    for (int i = 0; i < readingList.size(); i++) {
//                        params.put("id", readingList.get(i).getId());
//                        params.put("instrument_id", Integer.toString(readingList.get(i).getInstrument_id()));
//                        params.put("shift_id", readingList.get(i).getShift_id());
//                        params.put("reading_value", Double.toString(readingList.get(i).getReading_value()));
//                        params.put("date", readingList.get(i).getDate_time());
//                        params.put("system_id", Integer.toString(readingList.get(i).getInstrument_id()));
//                        params.put("plant_id", Integer.toString(readingList.get(i).getInstrument_id()));
//                        params.put("time", readingList.get(i).getTime());
//                        if (readingList.get(i).getImage_path() != null) {
//                            params.put("image", readingList.get(i).getImage_path().toString());
//                        }
//                        RequestHandler requestHandler = new RequestHandler();
//                        //returing the response
//                        status = requestHandler.sendPostRequest("http://192.168.100.230/ke_app_api/addReading.php", params);
//                    }
//                }
//                else {
//                    status = "No records found";
//                }
//                return status;
//            }
//        }
//
//        ReadingSync ul = new ReadingSync();
//        ul.execute();


        if (totalReadingsTaken == instrumentList.size()) {
            Toast.makeText(getApplicationContext(),"Data Saved Successfully",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Barcode_Instrument_List.this,Plant_List.class));

        }
        else {

            AlertDialog.Builder builder = new AlertDialog.Builder(Barcode_Instrument_List.this);
            builder.setTitle("Confirmation!");
            builder.setMessage("All instrument readings not taken, Do you want to continue?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    startActivity(new Intent(Barcode_Instrument_List.this,Plant_List.class));
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }

    public void settingsPage(View view) {
        startActivity(new Intent(Barcode_Instrument_List.this,Settings_Page.class));
    }

    public void log_Out(View view) {
        AlertDialog.Builder logout_dialogue_builder = new AlertDialog.Builder(Barcode_Instrument_List.this);
        logout_dialogue_builder.setTitle("Are you sure you want to Log Out and Exit?");
        logout_dialogue_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putString("Username", "-");
                editor.putString("shift_id", "-");
                editor.apply();
                Intent intent = new Intent(getApplicationContext(),Login.class);
                finishAffinity();
                startActivity(intent);
            }
        });

        logout_dialogue_builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.i("Status", "logout confirmed");
            }
        });
        logout_dialogue_builder.create().show();
    }
}