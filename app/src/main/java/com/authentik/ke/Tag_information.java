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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.Reading;
import com.authentik.model.System;
import com.authentik.utils.DatabaseHelper;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static java.util.Objects.isNull;

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
    Instrument instrument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_information);

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
        String shift_id = sharedPreferences.getString("shift_id", "-");
        final String value = sharedPreferences.getString("Username", "no name");
        currUser.setText(value);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy");
        String datetime = ft.format(dNow);
        dateAndTime.setText(datetime);

        instrument = (Instrument) getIntent().getSerializableExtra("instrument_object");
        final Plant plant = (Plant) getIntent().getSerializableExtra("plant_object");
        final System system = (System) getIntent().getSerializableExtra("system_object");

        app_path.setText(plant.getPlant_name() + " > " + system.getName() + " > " + instrument.getName() + " > " + "Tag");


        //set tag details
        tag_instrument.append(instrument.getName());
        tag_kksCode.append(instrument.getKksCode());
        tag_unit.append(instrument.getUnit());
        tag_range.append("Min (" + instrument.getLowerLimit() + ") " + "Max (" + instrument.getUpperLimit() + ")");
        tag_serialNo.append(instrument.getBarcodeId());
        tag_plant.append(plant.getPlant_name());
        tag_system.append(system.getName());

        try {

            Reading reading = db.getReading(shift_id, instrument.getId());

            if (reading != null) {
                reading_value_et.setText(Double.toString(reading.getReading_value()));
                reading_value_et.setEnabled(false);
                submit_tag_btn.setEnabled(false);
                Toast.makeText(Tag_information.this, "Reading recorded", Toast.LENGTH_SHORT).show();
            }
        } catch (NullPointerException e) {
//            e.printStackTrace();
            Log.i("Reading object status:", "Null continue");
        }


        submit_tag_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                closeKeyboard();
                if (reading_value_et.getText().length() == 0) {
                    Toast.makeText(Tag_information.this, "Please Enter a value", Toast.LENGTH_SHORT).show();
                } else {
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
                                    reading.setShift_id(sharedPreferences.getString("shift_id", "-"));
                                    reading.setReading_value(Double.parseDouble(reading_value_et.getText().toString()));
                                    reading.setSystem_id(system.getId());
                                    reading.setPlant_id(plant.getPlant_id());
                                    reading.setUser_name(value);

                                    db.addReading(reading);
                                    List<Instrument> instrumentList = db.getListOfInstrumentsFromBarcode(instrument.getBarcodeId());

                                    if (instrumentList.size() == 1) {
                                        Toast.makeText(getApplicationContext(), "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Tag_information.this, HomePage.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        finish();
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Tag_information.this, Barcode_Instrument_List.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.putExtra("Instrument_list", (Serializable) instrumentList);
                                        intent.putExtra("barcode_id", instrument.getBarcodeId());
                                        finish();
                                        startActivity(intent);
                                    }
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
                                    reading.setShift_id(sharedPreferences.getString("shift_id", "-"));
                                    reading.setReading_value(Double.parseDouble(reading_value_et.getText().toString()));
                                    reading.setSystem_id(system.getId());
                                    reading.setPlant_id(plant.getPlant_id());
                                    reading.setUser_name(value);

                                    finish();
                                    Intent intent = new Intent(Tag_information.this, reading_picture.class);
                                    intent.putExtra("tag_instrument", instrument);
                                    intent.putExtra("reading_object", reading);
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

    Handler handler;
    Runnable r;

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

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
        List<Instrument> instrumentList = db.getListOfInstrumentsFromBarcode(instrument.getBarcodeId());

        if (instrumentList.size() == 1) {
            Intent intent = new Intent(Tag_information.this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        List<Instrument> instrumentList = db.getListOfInstrumentsFromBarcode(instrument.getBarcodeId());

        if (instrumentList.size() == 1) {
            Intent intent = new Intent(Tag_information.this, HomePage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(intent);
        } else {
            finish();
        }
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


                    List<Instrument> instrumentList = db.getListOfInstrumentsFromBarcode(data);
                    if (instrumentList.size() == 0) {
                        Toast.makeText(getApplicationContext(), "Invalid Barcode", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Intent intent2 = new Intent(Tag_information.this, Barcode_Instrument_List.class);
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
        startActivity(new Intent(Tag_information.this, Settings_Page.class));
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
                Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
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