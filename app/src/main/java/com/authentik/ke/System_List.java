package com.authentik.ke;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

public class System_List extends AppCompatActivity {

    DatabaseHelper db;
    TextView currUser;
    TextView dateAndTime;
    SharedPreferences sharedPreferences;
    TextView app_path;
    String [] system_status_options = {"Stand by", "PFW/Shutdown"};
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

        int itemCount = systems.size();

        TableLayout tl = findViewById(R.id.system_table);
        TableLayout t2 = findViewById(R.id.system_header_table);

        TextView row_header_1  = new TextView(this);
        TextView row_header_2  = new TextView(this);
        TextView row_header_3  = new TextView(this);

        row_header_1.setText("S.#");
        row_header_1.setTextColor(Color.BLACK);
        row_header_1.setPadding(10,5,20,5);

        row_header_2.setText("System Name");
        row_header_2.setTextColor(Color.BLACK);
        row_header_2.setPadding(10,0,0,5);
        row_header_2.setWidth(200);

        row_header_3.setText("Status");
        row_header_3.setTextColor(Color.BLACK);
        row_header_3.setPadding(45,0,10,5);

        TableRow header = new TableRow(this);
        header.setBackgroundColor(Color.GRAY);
        header.addView(row_header_1);
        header.addView(row_header_2);
        header.addView(row_header_3);

        t2.addView(header);


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
            tr.setBackgroundResource(R.drawable.row_borders);
            tr.setClickable(true);

            updateSystemStatus = false;

            final int finalI = i;
            tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder system_active_dialogue_builder = new AlertDialog.Builder(System_List.this);
                    system_active_dialogue_builder.setMessage("Is the System Running?");
                    system_active_dialogue_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Log.i("System id:", Integer.toString(systems.get(finalI).getId()));
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
                            updateSystemStatus = true;
//                            Toast.makeText(getApplicationContext(),"System not running",Toast.LENGTH_SHORT).show();
                            final ArrayAdapter<String> adapter =
                                    new ArrayAdapter<>(System_List.this, android.R.layout.simple_spinner_item,system_status_options);
                            final Spinner sp = new Spinner(System_List.this);
                            sp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
                            sp.setAdapter(adapter);

                            final AlertDialog.Builder system_status_dialogue_builder = new AlertDialog.Builder(System_List.this);
                            system_status_dialogue_builder.setView(null).setMessage(null);
                            system_status_dialogue_builder.setMessage("Please Update System Status");
                            system_status_dialogue_builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    systems.get(finalI).setStatus(sp.getSelectedItem().toString());
                                    db.updateSystemStatus(systems.get(finalI));
                                    Log.i("System id:", Integer.toString(systems.get(finalI).getId()));
                                    Intent intent = new Intent(getApplicationContext(), Instrument_List.class);
                                    intent.putExtra("system_id", systems.get(finalI).getId());
                                    intent.putExtra("system_name",systems.get(finalI).getName());
                                    startActivity(intent);
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
}
