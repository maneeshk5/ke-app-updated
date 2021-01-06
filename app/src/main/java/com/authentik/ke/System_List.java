package com.authentik.ke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
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
    boolean updateSystemStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system__list);

        currUser = findViewById(R.id.username_tv);
        dateAndTime = findViewById(R.id.date_time_tv);
        app_path = findViewById(R.id.app_path_tv);

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("Username", "no name");
        String shift_id = sharedPreferences.getString("shift_id","-");
        currUser.setText("User: " + value);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String datetime = ft.format(dNow);
        dateAndTime.setText(datetime);

        db = new DatabaseHelper(getApplicationContext());

        int plant_id = getIntent().getIntExtra("plant_id",0);
        String plant_name = getIntent().getStringExtra("plant_name");
        app_path.setText(plant_name + " > " + "System List");

        final List<System> systems = db.getPlantSystem(plant_id);

//        for (Iterator<System> iterator = systems.iterator(); iterator.hasNext(); ) {
//            System system = iterator.next();
//            if (system.getIsActive() == 0) {
//                iterator.remove();
//            }
//        }

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
            serial_num.setTextColor(Color.BLACK);
            serial_num.setPadding(10, 5, 20, 5);

            system_name.setText(systems.get(i).getName());
            system_name.setTextColor(Color.BLACK);
            system_name.setPadding(10, 0, 0, 5);
            system_name.setWidth(200);

            List<Instrument> instrumentList = db.getSystemInstruments(systems.get(i).getId());
            int noOfinstrumentsInSystem = instrumentList.size();
            int systemStatus = 0;
            for (int j=0; j<noOfinstrumentsInSystem; j++) {
                int instStatus = db.getInstrumentStatus(instrumentList.get(j).getId(),shift_id);
                systemStatus += instStatus;
            }
            status.setText(systemStatus + "/" + noOfinstrumentsInSystem);
//            Log.i("No. of instruments: ", String.valueOf(noOfinstrumentsInSystem));
            status.setTextColor(Color.BLACK);
            status.setPadding(60, 0, 10, 5);

            TableRow tr = new TableRow(this);

            if (systemStatus == 0) {
                tr.setBackgroundResource(R.drawable.row_borders);
            }
            else if (systemStatus == noOfinstrumentsInSystem){
                tr.setBackgroundResource(R.drawable.row_border_green);
            }
            else {
                tr.setBackgroundResource(R.drawable.row_border_yellow);
            }

            tr.setClickable(true);
            updateSystemStatus = false;

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

                            Log.i(" System id:", Integer.toString(systems.get(finalI).getId()));
                            Intent intent = new Intent(getApplicationContext(), Instrument_List.class);
                            intent.putExtra("system_id", systems.get(finalI).getId());
                            intent.putExtra("system_name",systems.get(finalI).getName());
                            startActivity(intent);
                        }
                    });
                    system_active_dialogue_builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                            Toast.makeText(getApplicationContext(),"System not running",Toast.LENGTH_SHORT).show();
                            final ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(System_List.this, android.R.layout.simple_spinner_dropdown_item,system_status_options);
                            final Spinner sp = new Spinner(System_List.this);
                            sp.setLayoutParams(new LinearLayout.LayoutParams(20, 20,1f));
//                            sp.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT,));
//                            sp.setGravity(Gravity.CENTER);
//                            sp.setPrompt("Select status");
//                            sp.setBackgroundResource(R.drawable.border);
                            sp.setPadding(30,10,10,10);
//                            sp.setPaddingRelative(30,10,10,10);
//                            sp.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                            sp.setAdapter(adapter);

                            final AlertDialog.Builder system_status_dialogue_builder = new AlertDialog.Builder(System_List.this);
                            system_status_dialogue_builder.setView(null).setMessage(null);
                            system_status_dialogue_builder.setTitle("Update System Status");
//                            system_status_dialogue_builder.setMessage("Update System Status");
                            system_status_dialogue_builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (sp.getSelectedItem().equals("Select Status")) {
                                        Toast.makeText(System_List.this,"Select a valid status",Toast.LENGTH_SHORT).show();

                                    } else {
                                        systems.get(finalI).setStatus(sp.getSelectedItem().toString());
                                        db.updateSystemStatus(systems.get(finalI));
                                        Log.i("System id:", Integer.toString(systems.get(finalI).getId()));
                                        Intent intent = new Intent(getApplicationContext(), Instrument_List.class);
                                        intent.putExtra("system_id", systems.get(finalI).getId());
                                        intent.putExtra("system_name", systems.get(finalI).getName());
                                        startActivity(intent);
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
//                    setText(text2);
//                    goQuestionsActivity(data);

//                    start Tag Activity
//                    finish();
//                    Instrument instrument = db.getInstrumentFromBarcode(data);
//                    System system = db.getSystemFromInstrument(instrument);
//                    Plant plant = db.getPlantFromSystem(system);
//                    Log.i("Plant of Instrument",plant.getPlant_name());
//                    Log.i("System of Instrument",system.getName());

                    List<Instrument> instrumentList = db.getListOfInstrumentsFromBarcode(data);
                    Intent intent2 = new Intent(System_List.this,Barcode_Instrument_List.class);
                    intent2.putExtra("Instrument_list", (Serializable) instrumentList);
                    intent2.putExtra("barcode_id",data);
                    startActivity(intent2);
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
                finishAffinity();
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
