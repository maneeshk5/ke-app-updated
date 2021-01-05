package com.authentik.ke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.authentik.model.Instrument;
import com.authentik.utils.DatabaseHelper;
import com.authentik.utils.DialogBox;

import java.text.SimpleDateFormat;
import java.util.Date;

public class barcode_scan extends AppCompatActivity {

    TextView currUser;
    TextView dateAndTime;
    TextView app_path;
    SharedPreferences sharedPreferences;
    Instrument instrument_selected;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);

        currUser = findViewById(R.id.username_tv);
        dateAndTime = findViewById(R.id.date_time_tv);
        app_path = findViewById(R.id.app_path_tv);

        db = new DatabaseHelper(getApplicationContext());

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("Username", "no name");
        currUser.setText("User: " + value);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String datetime = ft.format(dNow);
        dateAndTime.setText(datetime);

//        String instrument_name = getIntent().getStringExtra("instrument_name");
//        app_path.setText(instrument_name + " > " + "Barcode");

        instrument_selected = (Instrument) getIntent().getSerializableExtra("instrument_object");
        app_path.setText(instrument_selected.getName() + " > " + "Barcode");

        Button btn = findViewById(R.id.barcode_scan_btn);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//                Intent intent = new Intent(barcode_scan.this, Tag_information.class);
//                intent.putExtra("instrument_object", instrument);
//                startActivity(intent);
//            }
//        });
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
                    final Instrument instrument_scanned = db.getInstrumentFromBarcode(data);

                    if (instrument_scanned.getBarcodeId().equals(instrument_selected.getBarcodeId())) {
                        //start Tag Activity
                        finish();
                        Intent intent2 = new Intent(barcode_scan.this, Tag_information.class);
                        intent2.putExtra("instrument_object", instrument_selected);
                        startActivity(intent2);
                    }
                    else {
//                        Toast.makeText(barcode_scan.this,"Scanned Barcode does not match with selected Instrument",Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(barcode_scan.this);
                        builder.setTitle("Warning!");
                        builder.setMessage("Scanned Barcode does not match with selected instrument barcode. Are you sure you want to continue?");
                        builder.setPositiveButton("Yes ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                Intent intent2 = new Intent(barcode_scan.this, Tag_information.class);
                                intent2.putExtra("instrument_object", instrument_scanned);
                                startActivity(intent2);
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


    public void goBack (View view){
        finish();
    }
}
