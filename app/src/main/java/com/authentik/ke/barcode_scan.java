package com.authentik.ke;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.authentik.model.Instrument;

import java.text.SimpleDateFormat;
import java.util.Date;

public class barcode_scan extends AppCompatActivity {

    TextView currUser;
    TextView dateAndTime;
    TextView app_path;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scan);

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

        String instrument_name = getIntent().getStringExtra("instrument_name");
        app_path.setText( instrument_name + " > " + "Barcode");

        final Instrument instrument = (Instrument) getIntent().getSerializableExtra("instrument_object");


        Button btn = findViewById(R.id.barcode_scan_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(barcode_scan.this, Tag_information.class);
                intent.putExtra("instrument_object",instrument);
                startActivity(intent);
            }
        });
    }

    public void goBack(View view) {
        finish();
    }
}