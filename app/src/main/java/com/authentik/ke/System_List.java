package com.authentik.ke;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.System;
import com.authentik.utils.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class System_List extends AppCompatActivity {

    DatabaseHelper db;
    TextView currUser;
    TextView dateAndTime;
    SharedPreferences sharedPreferences;
    TextView app_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system__list);

        currUser = findViewById(R.id.username_tv);
        dateAndTime = findViewById(R.id.date_time_tv);
        app_path = findViewById(R.id.app_path_tv);

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("Username", "no name");
        currUser.setText(value);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String datetime = ft.format(dNow);
        dateAndTime.setText(datetime);

        db = new DatabaseHelper(getApplicationContext());

        int plant_id = getIntent().getIntExtra("plant_id",0);
        String plant_name = getIntent().getStringExtra("plant_name");
        app_path.setText(plant_name + " > " + "System List");

        final List<System> systems = db.getPlantSystem(plant_id);

        int itemCount = systems.size();

        TableLayout tl = findViewById(R.id.system_table);

        TextView row_header_1 = new TextView(this);
        TextView row_header_2 = new TextView(this);
        TextView row_header_3 = new TextView(this);

        row_header_1.setText("S.#");
        row_header_1.setTextColor(Color.BLACK);
        row_header_1.setPadding(10, 5, 20, 5);

        row_header_2.setText("System Name");
        row_header_2.setTextColor(Color.BLACK);
        row_header_2.setPadding(10, 0, 0, 5);

        row_header_3.setText("Status");
        row_header_3.setTextColor(Color.BLACK);
        row_header_3.setPadding(50, 0, 10, 5);

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

            status.setText("0/0");
            status.setTextColor(Color.BLACK);
            status.setPadding(60, 0, 10, 5);

            TableRow tr = new TableRow(this);
            tr.setClickable(true);

            final int finalI = i;
            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("System id:", Integer.toString(systems.get(finalI).getId()));
                    Intent intent = new Intent(getApplicationContext(), Instrument_List.class);
                    intent.putExtra("system_id", systems.get(finalI).getId());
                    intent.putExtra("system_name",systems.get(finalI).getName());
                    startActivity(intent);
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
}
