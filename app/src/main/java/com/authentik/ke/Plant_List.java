package com.authentik.ke;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.System;
import com.authentik.utils.DatabaseHelper;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Plant_List extends AppCompatActivity {

    DatabaseHelper db;
    TextView currUser;
    TextView dateAndTime;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant__list);

        currUser = findViewById(R.id.username_tv);
        dateAndTime = findViewById(R.id.date_time_tv);

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("Username", "no name");
        final String shift_id = sharedPreferences.getString("shift_id", "-");

        currUser.setText("User: " + value);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String datetime = ft.format(dNow);
        dateAndTime.setText(datetime);


        final ProgressDialog dialog = ProgressDialog.show(this, "Loading", "Please wait....", true);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                dialog.dismiss();
            }
        }, 10000);

        db = new DatabaseHelper(getApplicationContext());

                final List<Plant> plants = db.getAllPlants();

                int itemCount = plants.size();

                TableLayout tl = findViewById(R.id.plant_table);
                TableLayout t2 = findViewById(R.id.plant_header_table);

                TextView row_header_1 = new TextView(Plant_List.this);
                TextView row_header_2 = new TextView(Plant_List.this);
                TextView row_header_3 = new TextView(Plant_List.this);

                row_header_1.setText("S.#");
                row_header_1.setTextColor(Color.BLACK);
                row_header_1.setPadding(10, 5, 20, 5);

                row_header_2.setText("Plant Name");
                row_header_2.setTextColor(Color.BLACK);
                row_header_2.setPadding(10, 0, 0, 5);
                row_header_2.setWidth(200);

                row_header_3.setText("Status");
                row_header_3.setTextColor(Color.BLACK);
                row_header_3.setPadding(60, 0, 10, 5);

                TableRow header = new TableRow(Plant_List.this);
                header.setBackgroundColor(Color.GRAY);
                header.addView(row_header_1);
                header.addView(row_header_2);
                header.addView(row_header_3);

                tl.addView(header);

                for (int i = 0; i < itemCount; i++) {
                    TextView serial_num = new TextView(Plant_List.this);
                    TextView plant_name = new TextView(Plant_List.this);
                    TextView status = new TextView(Plant_List.this);

                    serial_num.setText(Integer.toString(i + 1));
                    serial_num.setTextColor(Color.BLACK);
                    serial_num.setPadding(10, 5, 20, 5);

                    plant_name.setText(plants.get(i).getPlant_name());
                    plant_name.setTextColor(Color.BLACK);
                    plant_name.setPadding(10, 0, 0, 5);
                    plant_name.setWidth(200);

//                    class myTask extends AsyncTask<Void, Void, Void> {
//
//                        @Override
//                        protected Void doInBackground(Void... voids) {
//                            return null;
//                        }
//
//                    }
                    List<System> systemList = db.getPlantSystem(plants.get(i).getPlant_id());
                    int noOfSystemsInPlant = systemList.size();

                    int plantStatus = 0;

                    for (int j = 0; j < noOfSystemsInPlant; j++) {
                        int systemStatus = db.getSystemStatus(systemList.get(j).getId(), shift_id);
                        plantStatus = plantStatus + systemStatus;
                    }
//                    plantStatus = calculatePlantStatus(noOfSystemsInPlant,db,systemList,shift_id);
                    if (i==3) {
                        plantStatus = 20;
                    }

                    status.setText(plantStatus + "/" + noOfSystemsInPlant);
                    status.setTextColor(Color.BLACK);
                    status.setPadding(60, 0, 10, 5);

                    TableRow tr = new TableRow(Plant_List.this);

                    if (plantStatus == 0) {
                        tr.setBackgroundResource(R.drawable.row_borders);
                    } else if (plantStatus == noOfSystemsInPlant) {
                        tr.setBackgroundResource(R.drawable.row_border_green);
                    } else {
                        tr.setBackgroundResource(R.drawable.row_border_yellow);
                    }

                    tr.setClickable(true);

                    final int finalI = i;
                    tr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                    tr.setId(plants.get(i).getPlant_id());
                            Log.i("Plant id:", Integer.toString(plants.get(finalI).getPlant_id()));
                            Intent intent = new Intent(getApplicationContext(), System_List.class);
                            intent.putExtra("plant_id", plants.get(finalI).getPlant_id());
                            intent.putExtra("plant_name", plants.get(finalI).getPlant_name());
//                    intent.putExtra("plant", plants.get(finalI));
                            startActivity(intent);
                        }
                    });

                    tr.addView(serial_num);
                    tr.addView(plant_name);
                    tr.addView(status);

                    tr.setId(plants.get(i).getPlant_id());
                    tl.addView(tr);
                }
    }


    public void goBack(View view) {
        finish();
    }

    public int calculatePlantStatus(final int noOfSystemsInPlant, final DatabaseHelper db, final List<System> systemList, final String shift_id) {
        final int[] plantStatus = {0};
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < noOfSystemsInPlant; j++) {
                    int systemStatus = db.getSystemStatus(systemList.get(j).getId(), shift_id);
                    plantStatus[0] = plantStatus[0] + systemStatus;
                }
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return plantStatus[0];
    }
}