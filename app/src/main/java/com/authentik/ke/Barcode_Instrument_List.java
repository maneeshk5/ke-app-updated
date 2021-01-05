package com.authentik.ke;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.authentik.model.Instrument;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Barcode_Instrument_List extends AppCompatActivity {

    TextView currUser;
    TextView dateAndTime;
    SharedPreferences sharedPreferences;
    TextView barcode_tv;
    ListView instrument_barcode_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode__instrument__list);

        currUser = findViewById(R.id.username_tv);
        dateAndTime = findViewById(R.id.date_time_tv);
        barcode_tv = findViewById(R.id.barcode_id_tv);
        instrument_barcode_list = findViewById(R.id.barcode_instrument_list);

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("Username", "no name");
        String shift_id = sharedPreferences.getString("shift_id", "-");

        currUser.setText("User: " + value);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String datetime = ft.format(dNow);
        dateAndTime.setText(datetime);


        List<Instrument> instrumentList = (List<Instrument>) getIntent().getSerializableExtra("Instrument_list");
        String barcode_id = getIntent().getStringExtra("barcode_id");

        Log.i("Barcode id",barcode_id);

        barcode_tv.setText("Barcode: " + barcode_id);
        List<String> inst_names = new ArrayList<>();

        for(int i=0; i<instrumentList.size(); i++) {
//            Log.i("Instrument" + i, instrumentList.get(i).getName());
            inst_names.add(instrumentList.get(i).getName());
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, inst_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        instrument_barcode_list.setAdapter(adapter);

    }

    public void goBack(View view) {
        finish();
    }
}