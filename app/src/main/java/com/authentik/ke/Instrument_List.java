package com.authentik.ke;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.System;
import com.authentik.utils.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Instrument_List extends AppCompatActivity {

    DatabaseHelper db;
    TextView currUser;
    TextView dateAndTime;
    SharedPreferences sharedPreferences;
    TextView app_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument__list);

        currUser = findViewById(R.id.username_tv);
        dateAndTime = findViewById(R.id.date_time_tv);
        app_path = findViewById(R.id.app_path_tv);

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("Username", "no name");
        currUser.setText("User: " + value);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String datetime = ft.format(dNow);
        dateAndTime.setText(datetime);

        String system_name = getIntent().getStringExtra("system_name");
        app_path.setText(system_name + " > " + "Instrument List");

        db = new DatabaseHelper(getApplicationContext());

        int system_id = getIntent().getIntExtra("system_id",0);
        Log.i("system_id:",Integer.toString(system_id));

        final List<Instrument> instruments = db.getSystemInstruments(system_id);

        int itemCount = instruments.size();

        TableLayout tl = findViewById(R.id.instrument_table);
        TableLayout t2 = findViewById(R.id.instrument_header_table);

        TextView row_header_1  = new TextView(this);
        TextView row_header_2  = new TextView(this);
        TextView row_header_3  = new TextView(this);

        row_header_1.setText("S.#");
        row_header_1.setTextColor(Color.BLACK);
//        row_header_1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        row_header_1.setPadding(10,5,20,5);

        row_header_2.setText("Instrument Name");
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
            TextView inst_name = new TextView(this);
            TextView status = new TextView(this);

            serial_num.setText(Integer.toString(i + 1));
            serial_num.setTextColor(Color.BLACK);
            serial_num.setPadding(10, 5, 20, 5);

            inst_name.setText(instruments.get(i).getName());
            inst_name.setTextColor(Color.BLACK);
            inst_name.setPadding(10, 0, 0, 5);
            inst_name.setWidth(200);

            String shift_id = sharedPreferences.getString("shift_id","-");
            int instrumentReadingsTaken = db.getInstrumentStatus(instruments.get(i).getId(),shift_id);
            if (instrumentReadingsTaken == 0) {
                instruments.get(i).setStatus("Not Done");
                status.setText("Not Done");
            }
            else {
                instruments.get(i).setStatus("Done");
                status.setText("Done");
            }
            status.setTextColor(Color.BLACK);
            status.setPadding(60, 0, 10, 5);

            TableRow tr = new TableRow(this);

            if (status.getText() == "Not Done") {
                tr.setBackgroundResource(R.drawable.row_borders);
            }
            else {
                tr.setBackgroundResource(R.drawable.row_border_green);
            }

            tr.setClickable(true);

            final int finalI = i;
            final String finalStatus = status.getText().toString();
            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!finalStatus.equals("Done")) {
//                    Log.i("Instrument id:", Integer.toString(.get(finalI).getId());
                        Intent intent = new Intent(getApplicationContext(), barcode_scan.class);
                        intent.putExtra("instrument_id", instruments.get(finalI).getId());
                        intent.putExtra("instrument_name", instruments.get(finalI).getName());
//                    Log.i("Instrument kks Code:",instruments.get(finalI).getKksCode());
                        Log.i("Instrument unit:", instruments.get(finalI).getUnit());
                        intent.putExtra("instrument_object", instruments.get(finalI));
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Instrument Reading recorded in this shift",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            tr.addView(serial_num);
            tr.addView(inst_name);
            tr.addView(status);

            tr.setId(instruments.get(i).getId());
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
                    Intent intent2 = new Intent(Instrument_List.this, Tag_information.class);
                    Instrument instrument = db.getInstrumentFromBarcode(data);
                    intent2.putExtra("instrument_object", instrument);
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

}
