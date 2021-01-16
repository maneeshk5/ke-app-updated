package com.authentik.ke;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.authentik.model.Plant;
import com.authentik.model.Reading;
import com.authentik.model.Shift;
import com.authentik.utils.DatabaseHelper;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class Shift_Selection extends AppCompatActivity {

    ImageView btnLogOut;
    TextView currDate;
    TextView currTime;
    Spinner spin;
    Spinner spin2;
    ImageView btnSettings;
    DatabaseHelper db;
    String thisDate;
    SharedPreferences sharedpreferences;
    String timeComp;
    //    BroadcastReceiver broadcastReceiver;
    Handler handler;
    Runnable r;

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

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "user has been inactive for 1 hour, logging out", Toast.LENGTH_SHORT).show();
                sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putString("Username", "-");
                editor.putString("shift_id", "-");
                editor.apply();
//                Intent intent = new Intent(getApplicationContext(),Login.class);
                finishAffinity();
//                startActivity(intent);
            }
        };
        startHandler();

        db = new DatabaseHelper(getApplicationContext());

//        broadcastReceiver = new ConnectivityReceiver();
//        registerBroadcastReceiver();

        currDate = findViewById(R.id.curr_date_text);
//        currTime = findViewById(R.id.curr_time_text);

        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        Date todayDate = new Date();
        thisDate = currentDate.format(todayDate);
        currDate.append(thisDate);


        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat time_format = new SimpleDateFormat("HH:mm");
        timeComp = time_format.format(time);
//        currTime.append(timeComp);


        btnLogOut = findViewById(R.id.btn_logout);
        btnSettings = findViewById(R.id.btn_settings);

        final TextView userName = findViewById(R.id.username_tv);
        sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        String value = sharedpreferences.getString("Username", "-");
        userName.setText(value);

        spin = findViewById(R.id.spinner);

        spin2 = findViewById(R.id.spinner2);

        Button btn = findViewById(R.id.shift_submit_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String shift_type = spin.getSelectedItem().toString();
                String reading_type = spin2.getSelectedItem().toString();

                if (shift_type.equals("Select Shift") || reading_type.equals("Select Reading Type")) {
                    Toast.makeText(getApplicationContext(), "Please select a relevant option", Toast.LENGTH_SHORT).show();
                } else {
                    Shift shift = new Shift();

                    Date dNow = new Date();
                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                    String datetime = ft.format(dNow);

                    shift.setId(datetime);
                    shift.setName(shift_type);
                    shift.setReading_type(reading_type);

                    sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

                    String value = sharedpreferences.getString("Username", "-");

                    shift.setUser_Name(value);
                    shift.setStart_time(timeComp);
                    shift.setDate(thisDate);
                    shift.setEnd_time("-");

                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    String shift_id = db.checkShift(shift.getName(), shift.getDate(), shift.getReading_type());

                    if (shift_id.equals("new shift")) {
                        db.addShift(shift);
                        editor.putString("shift_id", shift.getId());
                    } else {
                        editor.putString("shift_id", shift_id);
                    }
                    editor.apply();

                    Intent intent = new Intent(Shift_Selection.this, Plant_List.class);
                    startActivity(intent);
                }
            }
        });

    }

    private long backPressedTime;

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "Press Back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
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

    @Override
    protected void onPause() {
        stopHandler();
        super.onPause();
    }

    @Override
    protected void onResume() {
        startHandler();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopHandler();
        super.onDestroy();
    }



    public void settingsPage(View view) {
        startActivity(new Intent(Shift_Selection.this, Settings_Page.class));
    }

    public void log_Out(View view) {
        AlertDialog.Builder logout_dialogue_builder = new AlertDialog.Builder(Shift_Selection.this);
        logout_dialogue_builder.setTitle("Are you sure you want to Log Out and Exit?");
        logout_dialogue_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
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