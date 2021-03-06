package com.authentik.ke;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.System;
import com.authentik.utils.DatabaseHelper;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
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

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "user has been inactive for 1 hour, logging out", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putString("Username", "-");
                editor.putString("shift_id", "-");
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                finishAffinity();
//                startActivity(intent);
            }
        };
        startHandler();


        currUser = findViewById(R.id.username_tv);
        dateAndTime = findViewById(R.id.date_time_tv);

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("Username", "no name");
        String shift_id = sharedPreferences.getString("shift_id", "-");

        currUser.setText(value);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String datetime = ft.format(dNow);
        dateAndTime.setText(datetime);


        db = new DatabaseHelper(getApplicationContext());

        final List<Plant> plants = db.getAllPlants();

        int itemCount = plants.size();

        if (itemCount == 0) {
            Toast.makeText(getApplicationContext(),"No plants to show, sync app with server",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(),HomePage.class));
        }

        TableLayout tl = findViewById(R.id.plant_table);
//                TableLayout t2 = findViewById(R.id.plant_header_table);

        TextView row_header_1 = new TextView(Plant_List.this);
        TextView row_header_2 = new TextView(Plant_List.this);
        TextView row_header_3 = new TextView(Plant_List.this);

        row_header_1.setText("S.#");
        row_header_1.setTextColor(Color.BLACK);
        row_header_1.setPadding(10, 5, 20, 5);
        row_header_1.setTextSize(16);

        row_header_2.setText("Plant Name");
        row_header_2.setTextColor(Color.BLACK);
        row_header_2.setPadding(10, 0, 0, 5);
        row_header_2.setWidth(200);
        row_header_2.setTextSize(16);

        row_header_3.setText("Status");
        row_header_3.setTextColor(Color.BLACK);
        row_header_3.setPadding(60, 0, 10, 5);
        row_header_3.setTextSize(16);

        TableRow header = new TableRow(Plant_List.this);
        header.setBackgroundColor(Color.GRAY);
        header.addView(row_header_1);
        header.addView(row_header_2);
        header.addView(row_header_3);
//        header.setMinimumHeight(60);

        tl.addView(header);

        for (int i = 0; i < itemCount; i++) {
            TextView serial_num = new TextView(Plant_List.this);
            TextView plant_name = new TextView(Plant_List.this);
            TextView status = new TextView(Plant_List.this);

            plant_name.setTextSize(16);
            serial_num.setTextSize(16);
            status.setTextSize(16);


            serial_num.setText(Integer.toString(i + 1));
            serial_num.setPadding(10, 5, 20, 5);

            plant_name.setText(plants.get(i).getPlant_name());
            plant_name.setPadding(10, 0, 0, 5);
            plant_name.setWidth(200);

            List<System> systemList = db.getPlantSystem(plants.get(i).getPlant_id());
//                    List<Instrument> instrumentList = db.getPlantInstruments(plants.get(i).getPlant_id());
            int noOfSystemsInPlant = systemList.size();
            int plantStatus = 0;

//                    int plantReadingsTaken = db.getPlantStatus(plants.get(i).getPlant_id(),shift_id);
//                    plantStatus += plantReadingsTaken;

            for (int j = 0; j < noOfSystemsInPlant; j++) {
                int systemStatus = db.getSystemStatus(systemList.get(j).getId(), shift_id);
//                int systemStatus = 9;
                int totalInst = db.getSystemInstruments(systemList.get(j).getId()).size();

//                Log.i("System Status", Integer.toString(systemStatus));
//                Log.i("Instrument size", Integer.toString(totalInst));

                if (systemStatus == totalInst) {
                    plantStatus += 1;
                }
//                Log.i("System Name", systemList.get(j).getName());
//                Log.i("System Status", Integer.toString(systemStatus));
//                Log.i("System Status", Integer.toString());
//                Log.i("Plant Name", plants.get(i).getPlant_name());
//                Log.i("Plant Status", Integer.toString(plantStatus));
            }
//                    plantStatus = calculatePlantStatus(noOfSystemsInPlant,db,systemList,shift_id);

            status.setText(plantStatus + "/" + noOfSystemsInPlant);
            status.setPadding(60, 0, 10, 5);
//                    status.setTextAlignment(COLOR);

            TableRow tr = new TableRow(Plant_List.this);

            if (plantStatus == 0) {
                tr.setBackgroundResource(R.drawable.row_borders);
                serial_num.setTextColor(Color.WHITE);
                plant_name.setTextColor(Color.WHITE);
                status.setTextColor(Color.WHITE);
            } else if (plantStatus == noOfSystemsInPlant) {
                tr.setBackgroundResource(R.drawable.row_border_green);
                serial_num.setTextColor(Color.WHITE);
                plant_name.setTextColor(Color.WHITE);
                status.setTextColor(Color.WHITE);
            } else {
                tr.setBackgroundResource(R.drawable.row_border_yellow);
                serial_num.setTextColor(Color.BLACK);
                plant_name.setTextColor(Color.BLACK);
                status.setTextColor(Color.BLACK);
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

            tr.setMinimumHeight(60);

            tr.setId(plants.get(i).getPlant_id());
            tl.addView(tr);
        }
    }


    Handler handler;
    Runnable r;

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        stopHandler();
        startHandler();
    }



    public void stopHandler() {
        handler.removeCallbacks(r);
    }

    public void startHandler() {
        handler.postDelayed(r, 3600 * 1000);
    }


    public void goBack(View view) {
        finish();
        startActivity(new Intent(Plant_List.this, HomePage.class));
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(Plant_List.this, HomePage.class));
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

    private final String TAG = "IntentApiSample";
    private final String ACTION_BARCODE_DATA = "com.honeywell.sample.action.BARCODE_DATA";
    private static final String ACTION_CLAIM_SCANNER = "com.honeywell.aidc.action.ACTION_CLAIM_SCANNER";

    private static final String ACTION_RELEASE_SCANNER = "com.honeywell.aidc.action.ACTION_RELEASE_SCANNER";

    private static final String EXTRA_SCANNER = "com.honeywell.aidc.extra.EXTRA_SCANNER";

    private static final String EXTRA_PROFILE = "com.honeywell.aidc.extra.EXTRA_PROFILE";

    private static final String EXTRA_PROPERTIES = "com.honeywell.aidc.extra.EXTRA_PROPERTIES";
    private TextView textView;
    private BroadcastReceiver barcodeDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_BARCODE_DATA.equals(intent.getAction())) {
/*
These extras are available:
"version" (int) = Data Intent Api version
"aimId" (String) = The AIM Identifier
            "charset" (String) = The charset used to convert "dataBytes" to "data" string
"codeId" (String) = The Honeywell Symbology Identifier
"data" (String) = The barcode data as a string
"dataBytes" (byte[]) = The barcode data as a byte array
"timestamp" (String) = The barcode timestamp
*/
                int version = intent.getIntExtra("version", 1);
                if (version >= 1) {
                    String aimId = intent.getStringExtra("aimId");
                    String charset = intent.getStringExtra("charset");
                    String codeId = intent.getStringExtra("codeId");
                    String data = intent.getStringExtra("data");
                    byte[] dataBytes = intent.getByteArrayExtra("dataBytes");
                    String dataBytesStr = bytesToHexString(dataBytes);
                    String timestamp = intent.getStringExtra("timestamp");
                    String text = String.format(
                            "Data:%s\n" +
                                    "Charset:%s\n" +
                                    "Bytes:%s\n" +
                                    "AimId:%s\n" +
                                    "CodeId:%s\n" +
                                    "Timestamp:%s\n",
                            data, charset, dataBytesStr, aimId, codeId, timestamp);

                    String text2 = String.format(
                            "Data:%s\n",
                            data);

                    Log.i("Scan Result ", text2);

                    List<Instrument> instrumentList = db.getListOfInstrumentsFromBarcode(data);

                    if (instrumentList.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Invalid Barcode", Toast.LENGTH_SHORT).show();
                    }
//                    else if (instrumentList.size() == 1) {
//                        Instrument instrument = instrumentList.get(0);
//                        System system = db.getSystemFromInstrument(instrument);
//                        Plant plant = db.getPlantFromSystem(system);
//                        Intent intent2 = new Intent(Plant_List.this, Tag_information.class);
//                        intent2.putExtra("instrument_object", instrument);
//                        intent2.putExtra("system_object", system);
//                        intent2.putExtra("plant_object", plant);
//                        finish();
//                        startActivity(intent2);
//                    }
                    else {
                        Intent intent2 = new Intent(Plant_List.this, Barcode_Instrument_List.class);
                        intent2.putExtra("Instrument_list", (Serializable) instrumentList);
                        intent2.putExtra("barcode_id", data);
//                    finishAffinity();
                        finish();
                        startActivity(intent2);
                    }
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(barcodeDataReceiver, new IntentFilter(ACTION_BARCODE_DATA));
        claimScanner();
        startHandler();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(barcodeDataReceiver);
        releaseScanner();
        stopHandler();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopHandler();
    }

    private void claimScanner() {
        Bundle properties = new Bundle();
        properties.putBoolean("DPR_DATA_INTENT", true);
        properties.putString("DPR_DATA_INTENT_ACTION", ACTION_BARCODE_DATA);
        sendBroadcast(new Intent(ACTION_CLAIM_SCANNER)
                .putExtra(EXTRA_SCANNER, "dcs.scanner.imager")
                .putExtra(EXTRA_PROFILE, "MyProfile1")
                .putExtra(EXTRA_PROPERTIES, properties)
        );
    }

    private void releaseScanner() {
        sendBroadcast(new Intent(ACTION_RELEASE_SCANNER));
    }


    private String bytesToHexString(byte[] arr) {
        String s = "[]";
        if (arr != null) {
            s = "[";
            for (int i = 0; i < arr.length; i++) {
                s += "0x" + Integer.toHexString(arr[i]) + ", ";
            }
            s = s.substring(0, s.length() - 2) + "]";
        }
        return s;
    }

    public void settingsPage(View view) {
        startActivity(new Intent(Plant_List.this, Settings_Page.class));
    }

    public void log_Out(View view) {
        AlertDialog.Builder logout_dialogue_builder = new AlertDialog.Builder(Plant_List.this);
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
                Intent intent = new Intent(getApplicationContext(), Login.class);
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