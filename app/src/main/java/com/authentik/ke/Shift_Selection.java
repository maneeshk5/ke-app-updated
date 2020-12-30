package com.authentik.ke;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.authentik.model.Shift;
import com.authentik.utils.DatabaseHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class Shift_Selection extends AppCompatActivity {
    String[] shifts = {"Select Shift","Night","Morning", "Evening"};
    String[] reading_types = {"Select Reading Types","First","Second","Third"};
    TextView btnLogOut;
    TextView currDate;
    TextView currTime;
    Spinner spin;
    Spinner spin2;
    Spinner options_spinner;
    DatabaseHelper db;
    String thisDate;
    SharedPreferences sharedpreferences;
    String timeComp;

    public boolean isInternetAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift__selection);

        db = new DatabaseHelper(getApplicationContext());
//        try {
//            Thread.sleep(8000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        dialog.dismiss();
        if (isInternetAvailable()) {
            final ProgressDialog dialog = ProgressDialog.show(this, "Loading", "Please wait....", true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            }, 10000);
        }

        currDate = findViewById(R.id.curr_date_text);
        currTime = findViewById(R.id.curr_time_text);

        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        Date todayDate = new Date();
        thisDate = currentDate.format(todayDate);
        currDate.append(thisDate);


        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat time_format = new SimpleDateFormat("HH:mm");
        timeComp = time_format.format(time);
        currTime.append(timeComp);


//        btnLogOut = findViewById(R.id.logout_link);
//        logout User
//        btnLogOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedpreferences.edit();
//                editor.putBoolean("isLoggedIn",false);
//                editor.putString("Username","-");
//                editor.apply();
//                finishAffinity();
//            }
//        });

        final TextView userName = findViewById(R.id.curr_user_text);
        sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        String value = sharedpreferences.getString("Username","-");
        userName.setText("User: " + value);


        spin = findViewById(R.id.spinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, shifts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        spin2 = findViewById(R.id.spinner2);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, reading_types);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin2.setAdapter(adapter2);

        options_spinner = findViewById(R.id.options_spinner);
        options_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = options_spinner.getSelectedItem().toString();
                if (selection.equals("Logout")) {
                    sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean("isLoggedIn",false);
                    editor.putString("Username","-");
                    editor.putString("shift_id","-");
                    editor.apply();
                    finishAffinity();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btn = findViewById(R.id.shift_submit_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String shift_type = spin.getSelectedItem().toString();
                String reading_type = spin2.getSelectedItem().toString();

                if (shift_type.equals("Select Shift") || reading_type.equals("Select Reading Types")) {
                    Toast.makeText(getApplicationContext(), "Please select a relevant option", Toast.LENGTH_SHORT).show();
                }
                else {
                    Shift shift = new Shift();

                    Date dNow = new Date();
                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                    String datetime = ft.format(dNow);

                    shift.setId(datetime);
                    shift.setName(shift_type);
                    shift.setReading_type(reading_type);

                    sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

                    String value = sharedpreferences.getString("Username","-");

                    shift.setUser_Name(value);
                    shift.setStart_time(timeComp);
                    shift.setDate(thisDate);
                    shift.setEnd_time("-");

                    if (!db.checkShift(shift.getName(),shift.getDate(),shift.getReading_type())) {
                        db.addShift(shift);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("shift_id",shift.getId());
                        editor.apply();
                    }
                    Intent intent = new Intent(Shift_Selection.this, Plant_List.class);
                    startActivity(intent);
                }
            }
        });


    }

}