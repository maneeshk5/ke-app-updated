package com.authentik.ke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

public class System_List extends AppCompatActivity {

    DatabaseHelper db;
    TextView currUser;
    TextView dateAndTime;
    SharedPreferences sharedPreferences;
    TextView app_path;
    String [] system_status_options = {"Select Status","Stand by", "PFW/Shutdown"};
    String plant_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system__list);

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "user is inactive from last 1 hour, logging out",Toast.LENGTH_SHORT).show();
                SharedPreferences sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putString("Username", "-");
                editor.putString("shift_id", "-");
                editor.apply();
                Intent intent = new Intent(getApplicationContext(),Login.class);
                finishAffinity();
//                startActivity(intent);
            }
        };
        startHandler();

        currUser = findViewById(R.id.username_tv);
        dateAndTime = findViewById(R.id.date_time_tv);
        app_path = findViewById(R.id.app_path_tv);

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("Username", "no name");
        final String shift_id = sharedPreferences.getString("shift_id","-");
        currUser.setText(value);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String datetime = ft.format(dNow);
        dateAndTime.setText(datetime);

        db = new DatabaseHelper(getApplicationContext());

        int plant_id = getIntent().getIntExtra("plant_id",0);
        plant_name = getIntent().getStringExtra("plant_name");
        app_path.setText(plant_name + " > " + "System List");

        final List<System> systems = db.getPlantSystem(plant_id);


        int itemCount = systems.size();

        TableLayout tl = findViewById(R.id.system_table);
//        TableLayout t2 = findViewById(R.id.system_header_table);

        TextView row_header_1  = new TextView(this);
        TextView row_header_2  = new TextView(this);
        TextView row_header_3  = new TextView(this);

        row_header_1.setText("S.#");
        row_header_1.setTextColor(Color.BLACK);
        row_header_1.setPadding(10,5,20,5);

        row_header_2.setText("System Name");
        row_header_2.setTextColor(Color.BLACK);
        row_header_2.setPadding(10, 0, 0, 5);
        row_header_2.setWidth(200);

        row_header_3.setText("Status");
        row_header_3.setTextColor(Color.BLACK);
        row_header_3.setPadding(60, 0, 10, 5);

        TableRow header = new TableRow(this);
        header.setBackgroundColor(Color.GRAY);
        header.addView(row_header_1);
        header.addView(row_header_2);
        header.addView(row_header_3);

        tl.addView(header);


        for (int i = 0; i < itemCount; i++) {
            TextView serial_num = new TextView(this);
            TextView system_name = new TextView(this);
            TextView status = new TextView(this);

            serial_num.setText(Integer.toString(i + 1));
//            serial_num.setTextColor(Color.BLACK);
            serial_num.setPadding(10, 5, 20, 5);

            system_name.setText(systems.get(i).getName());
//            system_name.setTextColor(Color.BLACK);
            system_name.setPadding(10, 0, 0, 5);
            system_name.setWidth(200);

            List<Instrument> instrumentList = db.getSystemInstruments(systems.get(i).getId());
            int noOfinstrumentsInSystem = instrumentList.size();
//            int systemStatus = 0;
//            for (int j=0; j<noOfinstrumentsInSystem; j++) {
//                int instStatus = db.getInstrumentStatus(instrumentList.get(j).getId(),shift_id);
//                systemStatus += instStatus;
//            }
            int systemStatus = 0;
            int systemReadingsTaken = db.getSystemStatus(systems.get(i).getId(),shift_id);
            systemStatus += systemReadingsTaken;
            status.setText(systemStatus + "/" + noOfinstrumentsInSystem);
//            Log.i("No. of instruments: ", String.valueOf(noOfinstrumentsInSystem));
//            status.setTextColor(Color.BLACK);
            status.setPadding(60, 0, 10, 5);

            TableRow tr = new TableRow(this);

            if (systemStatus == 0) {
                tr.setBackgroundResource(R.drawable.row_borders);
                serial_num.setTextColor(Color.WHITE);
                system_name.setTextColor(Color.WHITE);
                status.setTextColor(Color.WHITE);
            }
            else if (systemStatus == noOfinstrumentsInSystem){
                tr.setBackgroundResource(R.drawable.row_border_green);
                serial_num.setTextColor(Color.WHITE);
                system_name.setTextColor(Color.WHITE);
                status.setTextColor(Color.WHITE);
            }
            else {
                tr.setBackgroundResource(R.drawable.row_border_yellow);
                serial_num.setTextColor(Color.BLACK);
                system_name.setTextColor(Color.BLACK);
                status.setTextColor(Color.BLACK);
            }

            tr.setClickable(true);

            final int finalI = i;
            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder system_active_dialogue_builder = new AlertDialog.Builder(System_List.this);
                    system_active_dialogue_builder.setTitle("Alert");
                    system_active_dialogue_builder.setMessage("Is the System Running?");
                    system_active_dialogue_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Date dNow = new Date();
                            SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                            SimpleDateFormat ft2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            String status_id = ft.format(dNow);
                            String date_time = ft2.format(dNow);
                            String status_value = "Running";

                            db.addSystemStatus(status_id,shift_id,systems.get(finalI).getId(),status_value,date_time);

                            Log.i(" System id:", Integer.toString(systems.get(finalI).getId()));
                            Intent intent = new Intent(getApplicationContext(), Instrument_List.class);
                            intent.putExtra("app_path",app_path.getText().toString());
                            intent.putExtra("system_id", systems.get(finalI).getId());
                            intent.putExtra("system_name",systems.get(finalI).getName());
                            intent.putExtra("plant_name",plant_name);
                            startActivity(intent);
                        }
                    });
                    system_active_dialogue_builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(System_List.this, android.R.layout.simple_spinner_dropdown_item,system_status_options);
                            final Spinner sp = new Spinner(System_List.this);
                            sp.setLayoutParams(new LinearLayout.LayoutParams(20, 20,1f));
                            sp.setPadding(30,10,10,10);
                            sp.setAdapter(adapter);

                            final AlertDialog.Builder system_status_dialogue_builder = new AlertDialog.Builder(System_List.this);
                            system_status_dialogue_builder.setView(null).setMessage(null);
                            system_status_dialogue_builder.setTitle("Update System Status");
                            system_status_dialogue_builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (sp.getSelectedItem().equals("Select Status")) {
                                        Toast.makeText(System_List.this,"Select a valid status",Toast.LENGTH_SHORT).show();

                                    } else {
//                                        String status_value = systems.get(finalI).setStatus(sp.getSelectedItem().toString());
//                                        db.updateSystemStatus(systems.get(finalI));
                                        String status_value = sp.getSelectedItem().toString();
                                        Date dNow = new Date();
                                        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                                        SimpleDateFormat ft2 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                                        String status_id = ft.format(dNow);
                                        String date_time = ft2.format(dNow);

                                        db.addSystemStatus(status_id,shift_id,systems.get(finalI).getId(),status_value,date_time);

                                        if (status_value.equals("PFW/Shutdown")) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(System_List.this);
                                            builder.setView(null).setMessage(null);
                                            builder.setMessage("Select Another system?");
                                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                   dialog.dismiss();
                                                }
                                            });
                                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    AlertDialog.Builder plant_alert = new AlertDialog.Builder(System_List.this);
                                                    plant_alert.setView(null).setMessage(null);
                                                    plant_alert.setMessage("Select Another Plant?");
                                                    plant_alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
//                                                            Intent intent = new Intent(System_List.this,Plant_List.class);
//                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                    plant_alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                    plant_alert.create().show();
                                                }
                                            });
                                            builder.create().show();
                                        }
                                        else {
//                                        Log.i("System id:", Integer.toString(systems.get(finalI).getId()));
                                            Intent intent = new Intent(System_List.this, Instrument_List.class);
                                            intent.putExtra("app_path", app_path.getText().toString());
                                            intent.putExtra("system_id", systems.get(finalI).getId());
                                            intent.putExtra("system_name", systems.get(finalI).getName());
                                            intent.putExtra("plant_name", plant_name);
                                            startActivity(intent);
                                        }
                                    }
                                }
                            });
                            system_status_dialogue_builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            system_status_dialogue_builder.setView(sp);
                            system_status_dialogue_builder.create().show();
                        }
                    });

                    AlertDialog system_active_dialogue = system_active_dialogue_builder.create();
                    system_active_dialogue.show();

                }
            });

            tr.addView(serial_num);
            tr.addView(system_name);
            tr.addView(status);

            tr.setId(systems.get(i).getId());
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
        handler.postDelayed(r, 3600*1000);
    }

    public void goBack(View view) {
        finish();
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
                        Toast.makeText(getApplicationContext(),"Invalid Barcode",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent2 = new Intent(System_List.this, Barcode_Instrument_List.class);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(barcodeDataReceiver);
        releaseScanner();
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
        startActivity(new Intent(System_List.this,Settings_Page.class));
    }

    public void log_Out(View view) {
        AlertDialog.Builder logout_dialogue_builder = new AlertDialog.Builder(System_List.this);
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
                Intent intent = new Intent(getApplicationContext(),SplashScreen.class);
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
