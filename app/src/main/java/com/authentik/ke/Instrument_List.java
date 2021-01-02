package com.authentik.ke;

import android.content.Context;
import android.content.Intent;
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
}
