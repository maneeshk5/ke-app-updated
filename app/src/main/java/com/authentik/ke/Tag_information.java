package com.authentik.ke;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.Reading;
import com.authentik.model.System;
import com.authentik.utils.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tag_information extends AppCompatActivity {

    TextView currUser;
    TextView dateAndTime;
    TextView app_path;
    TextView tag_instrument;
    TextView tag_kksCode;
    TextView tag_serialNo;
    TextView tag_unit;
    TextView tag_range;
    TextView tag_plant;
    TextView tag_system;

    SharedPreferences sharedPreferences;
    EditText reading_value_et;
    Button submit_tag_btn;
    DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_information);

        db = new DatabaseHelper(getApplicationContext());

        currUser = findViewById(R.id.username_tv);
        dateAndTime = findViewById(R.id.date_time_tv);
        app_path = findViewById(R.id.app_path_tv);

        tag_instrument = findViewById(R.id.tag_inst);
        tag_kksCode = findViewById(R.id.tag_kkscode);
        tag_serialNo = findViewById(R.id.tag_serial);
        tag_unit = findViewById(R.id.tag_unit);
        tag_range = findViewById(R.id.tag_range);
        tag_plant = findViewById(R.id.tag_plant);
        tag_system = findViewById(R.id.tag_system);

        reading_value_et = findViewById(R.id.reading_value_et);
        submit_tag_btn = findViewById(R.id.submit_tag_btn);

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("Username", "no name");
        currUser.setText("User: " + value);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String datetime = ft.format(dNow);
        dateAndTime.setText(datetime);

        final Instrument instrument = (Instrument) getIntent().getSerializableExtra("instrument_object");
        app_path.setText( instrument.getName() + " > " + "Tag");

        final Plant plant = (Plant) getIntent().getSerializableExtra("plant_object");

        final System system = (System) getIntent().getSerializableExtra("system_object");


        //set tag details
        tag_instrument.append(instrument.getName());
        tag_kksCode.append(instrument.getKksCode());
        tag_unit.append(instrument.getUnit());
        tag_range.append("Min (" + instrument.getLowerLimit() + ") " + "Max (" + instrument.getUpperLimit() + ")");
        tag_serialNo.append(instrument.getBarcodeId());
        tag_plant.append(plant.getPlant_name());
        tag_system.append(system.getName());

        //set Instrument barcode ID in local db
        db.setInstrumentBarcodeId(instrument);

        submit_tag_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reading_value_et.getText().length() == 0) {
                    Toast.makeText(Tag_information.this,"Please Enter a value",Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Tag_information.this);
                    builder.setTitle("Confirmation!");
                    builder.setMessage("Your input reading is: " + reading_value_et.getText().toString());

                    builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder pic_dilogue_builder = new AlertDialog.Builder(Tag_information.this);
                            pic_dilogue_builder.setView(null).setMessage(null);
                            pic_dilogue_builder.setTitle("Confirmation");
                            pic_dilogue_builder.setMessage("Do you want to take a picture?");

                            pic_dilogue_builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Tag_information.this,"Data Saved Successfully",Toast.LENGTH_LONG).show();

                                    //Insert reading data locally
                                    Date dNow = new Date();
                                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                                    String reading_id = ft.format(dNow);

                                    SimpleDateFormat ft2 = new SimpleDateFormat("dd-MM-yyyy");
                                    String reading_date_time = ft2.format(dNow);

                                    SimpleDateFormat ft3 = new SimpleDateFormat("HH:mm:ss");
                                    String reading_time = ft3.format(dNow);

                                    Reading reading = new Reading();
                                    reading.setId(reading_id);
                                    reading.setDate_time(reading_date_time);
                                    reading.setTime(reading_time);
                                    reading.setInstrument_id(instrument.getId());
                                    reading.setShift_id(sharedPreferences.getString("shift_id","-"));
                                    reading.setReading_value(reading_value_et.getText().toString());

                                    if(db.checkReading(reading.getShift_id(),reading.getInstrument_id())) {
                                        Toast.makeText(Tag_information.this,"Value already recorded for this instrument and shift", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        db.addReading(reading);
                                    }

                                    Intent intent = new Intent(Tag_information.this,Plant_List.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                }
                            });

                            pic_dilogue_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Insert reading data locally
                                    Date dNow = new Date();
                                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                                    String reading_id = ft.format(dNow);

                                    SimpleDateFormat ft2 = new SimpleDateFormat("dd-MM-yyyy");
                                    String reading_date_time = ft2.format(dNow);

                                    SimpleDateFormat ft3 = new SimpleDateFormat("HH:mm:ss");
                                    String reading_time = ft3.format(dNow);
                                    Reading reading = new Reading();
                                    reading.setId(reading_id);
                                    reading.setDate_time(reading_date_time);
                                    reading.setTime(reading_time);
                                    reading.setInstrument_id(instrument.getId());
                                    reading.setShift_id(sharedPreferences.getString("shift_id","-"));
                                    reading.setReading_value(reading_value_et.getText().toString());

                                    db.addReading(reading);
//                                    Toast.makeText(Tag_information.this,"Open camera page",Toast.LENGTH_LONG).show();
//                                    dispatchTakePictureIntent();
                                    finish();
                                    Intent intent = new Intent(Tag_information.this,reading_picture.class);
                                    intent.putExtra("reading_id",reading.getId());
                                    startActivity(intent);

                                }
                            });
                            pic_dilogue_builder.create().show();
                        }
                    });
                    builder.create().show();
                }
            }
        });
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
                    finish();
                    Instrument instrument = db.getInstrumentFromBarcode(data);
                    System system = db.getSystemFromInstrument(instrument);
                    Plant plant = db.getPlantFromSystem(system);
                    Log.i("Plant of Instrument",plant.getPlant_name());
                    Log.i("System of Instrument",system.getName());

                    Intent intent2 = new Intent(Tag_information.this, Tag_information.class);
                    intent2.putExtra("instrument_object", instrument);
                    intent2.putExtra("system_object", system);
                    intent2.putExtra("plant_object", plant);

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
        startActivity(new Intent(Tag_information.this,Settings_Page.class));
    }

    public void log_Out(View view) {
        android.support.v7.app.AlertDialog.Builder logout_dialogue_builder = new android.support.v7.app.AlertDialog.Builder(Tag_information.this);
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