package com.authentik.ke;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.Reading;
import com.authentik.model.Shift;
import com.authentik.model.System;
import com.authentik.model.User;
import com.authentik.utils.Constant;
import com.authentik.utils.DatabaseHelper;
import com.authentik.utils.SyncDbService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SplashScreen extends Activity {

    String usersURL;
    String plantsURL;
    String systemsURL;
    String instrumentsURL;
    ProgressDialog dialog;
    SharedPreferences sharedpreferences;
    Intent serviceIntent;

//    String instrumentsURL = "http://192.168.100.230:80/ke_app_api/readInstruments.php";
//    String usersURL = "http://192.168.100.230:80/ke_app_api/readUsers.php";
//    String plantsURL = "http://192.168.100.230:80/ke_app_api/readPlants.php";
//    String systemsURL = "http://192.168.100.230:80/ke_app_api/readSystems.php";

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Boolean isFirstRun = getSharedPreferences("Preference", Context.MODE_PRIVATE).getBoolean("isFirstRun",true);

        if (!isFirstRun) {
//            getSharedPreferences("Preference",MODE_PRIVATE).edit().putBoolean("isFirstRun",false).apply();
            finish();
            startActivity(new Intent(SplashScreen.this,FirstAppUse.class));
        }

        db = new DatabaseHelper(getApplicationContext());

        String serverDefaultURL = getResources().getString(R.string.server_name);
        sharedpreferences = getSharedPreferences("ServerData", Context.MODE_PRIVATE);
        final String server_url = sharedpreferences.getString("server_url", serverDefaultURL);

        //intialize api urls
//        usersURL =  getString(R.string.server_name) + "readUsers.php";
//        plantsURL = getString(R.string.server_name) + "readPlants.php";
//        systemsURL = getString(R.string.server_name) + "readSystems.php";
//        instrumentsURL = getString(R.string.server_name) + "readInstruments.php";

        usersURL = server_url + "readUsers.php";
        plantsURL = server_url + "readPlants.php";
        systemsURL = server_url + "readSystems.php";
        instrumentsURL = server_url + "readInstruments.php";

        // sync server db to local db if internet is available
        if (isInternetAvailable()) {
//
            dialog = ProgressDialog.show(this, "Loading", "Please wait....", true);

            //Start sync service
            serviceIntent = new Intent(this, SyncDbService.class);
            startService(serviceIntent);
        }
        else {
            sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            if (sharedpreferences.contains("isLoggedIn")) {
                boolean value = sharedpreferences.getBoolean("isLoggedIn", false);
                Intent intent;
                if (value) {
                    intent = new Intent(new Intent(getApplicationContext(), Shift_Selection.class));
                } else {
                    intent = new Intent(new Intent(getApplicationContext(), Login.class));
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(new Intent(getApplicationContext(), Login.class));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }

    }

    public boolean isInternetAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onDestroy() {
        try {
            dialog.dismiss();
            stopService(serviceIntent);
        }
        catch (NullPointerException e) {
            Log.i("splash screen","no dialog box");

        }
        super.onDestroy();
    }

}

