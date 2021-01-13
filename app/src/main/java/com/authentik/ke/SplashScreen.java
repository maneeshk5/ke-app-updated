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
//        if (isInternetAvailable()) {
//
            dialog = ProgressDialog.show(this, "Loading", "Please wait....", true);

            //Start sync service
            serviceIntent = new Intent(this, SyncDbService.class);
            startService(serviceIntent);
//
//
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    class DBSync extends AsyncTask<Void, Void, Void> {
//
//                        @Override
//                        protected Void doInBackground(Void... voids) {
////                            DatabaseSync();
//                            serverURL[0] = DbSync();
//                            return null;
//                        }
//
//                        @Override
//                        protected void onPostExecute(Void aVoid) {
//                            dialog.dismiss();
//                            if (!serverURL[0]) {
//                                Toast.makeText(getApplicationContext(), "Server Connection Error", Toast.LENGTH_LONG).show();
//                                sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
//
//                                if (sharedpreferences.contains("isLoggedIn")) {
//                                    boolean value = sharedpreferences.getBoolean("isLoggedIn", false);
//                                    if (value) {
//                                        startActivity(new Intent(SplashScreen.this, Shift_Selection.class));
//                                    } else {
//                                        startActivity(new Intent(SplashScreen.this, Login.class));
//                                    }
//                                } else {
//                                    startActivity(new Intent(SplashScreen.this, Login.class));
//                                }
//
//                            } else {
//                                Toast.makeText(getApplicationContext(), "Data Synced with Server", Toast.LENGTH_LONG).show();
//
//                                sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
//
//                                if (sharedpreferences.contains("isLoggedIn")) {
//                                    boolean value = sharedpreferences.getBoolean("isLoggedIn", false);
//                                    if (value) {
//                                        startActivity(new Intent(SplashScreen.this, Shift_Selection.class));
//                                    } else {
//                                        startActivity(new Intent(SplashScreen.this, Login.class));
//                                    }
//                                } else {
//                                    startActivity(new Intent(SplashScreen.this, Login.class));
//                                }
//
//                            }
//                            finish();
//                        }
//                    }
//                    DBSync dbSync = new DBSync();
//                    dbSync.execute();
//                }
//            });
//            thread.start();
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            Toast.makeText(getApplicationContext(), "Sever Sync not available due to no internet", Toast.LENGTH_SHORT).show();
//
//            sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
//
//            if (sharedpreferences.contains("isLoggedIn")) {
//                boolean value = sharedpreferences.getBoolean("isLoggedIn", false);
//                if (value) {
//                    startActivity(new Intent(SplashScreen.this, Shift_Selection.class));
//                } else {
//                    startActivity(new Intent(SplashScreen.this, Login.class));
//                }
//            } else {
//                startActivity(new Intent(SplashScreen.this, Login.class));
//            }
//            finish();
//        }
//        else {
//            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
//        }

//        sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
//        if (sharedpreferences.contains("isLoggedIn")) {
//            boolean value = sharedpreferences.getBoolean("isLoggedIn", false);
//            if (value) {
//                startActivity(new Intent(getApplicationContext(), Shift_Selection.class));
//            } else {
//                startActivity(new Intent(getApplicationContext(), Login.class));
//            }
//        } else {
//            startActivity(new Intent(getApplicationContext(), Login.class));
//        }
//        finish();

    }

    public boolean isInternetAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onDestroy() {
        dialog.dismiss();
//        if (isInternetAvailable()) {
            stopService(serviceIntent);
//        }
        super.onDestroy();
    }

    private boolean DbSync() {

        //user Sync
        RequestHandler requestHandler = new RequestHandler();
        String getUsers =  requestHandler.sendReadRequest(usersURL);

        if (getUsers.equals("Invalid Server URL")) {
            return false;
        }
        try {

            JSONArray arr = new JSONArray(getUsers);

            if (arr.length() == 0) {
                Toast.makeText(getApplicationContext(), "Unable to retrieve data", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < arr.length(); i++) {
                    User user = new User();
                    JSONObject obj = arr.getJSONObject(i);
                    user.setId(obj.getInt("user_id"));
                    user.setName(obj.getString("userName"));
                    user.setPassword(obj.getString("password"));
                    user.setIsActive(obj.getInt("isActive"));
                    user.setIsDeleted(obj.getInt("isDeleted"));

                    if (!db.checkUser(user.getName()) && user.getIsActive() == 1)  {
                        db.addUser(user);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //plant Sync
        String getPlants =  requestHandler.sendReadRequest(plantsURL);
        try {

            JSONArray arr = new JSONArray(getPlants);

            if (arr.length() == 0) {
                Toast.makeText(getApplicationContext(), "Unable to retrieve data", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < arr.length(); i++) {
                    Plant plant = new Plant();
                    JSONObject obj = arr.getJSONObject(i);
                    plant.setPlant_id(obj.getInt("plant_id"));
                    plant.setPlant_name(obj.getString("plant_name"));
                    plant.setIsActive(obj.getInt("isActive"));
                    plant.setReadingTimeId(obj.getInt("readingTimeId"));

                    if (!db.checkPlant(plant.getPlant_id()) && plant.getIsActive() == 1) {
                        db.addPlant(plant);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //systemSync
        String getSystems =  requestHandler.sendReadRequest(systemsURL);
        try {

            JSONArray arr = new JSONArray(getSystems);

            if (arr.length() == 0) {
                Toast.makeText(getApplicationContext(), "Unable to retrieve data", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < arr.length(); i++) {
                    System system = new System();
                    JSONObject obj = arr.getJSONObject(i);
                    system.setId(obj.getInt("system_id"));
                    system.setName(obj.getString("system_name"));
                    system.setIsActive(obj.getInt("isActive"));
                    system.setLogSheet(obj.getString("logSheet"));
                    system.setPlantId(obj.getInt("systemPlantId"));

                    if (!db.checkSystem(system.getId()) && system.getIsActive() == 1) {
//                                Log.v("System","Hello");
                        db.addSystem(system);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Instrument Sync
        String getInstruments =  requestHandler.sendReadRequest(instrumentsURL);

        try {

            JSONArray arr = new JSONArray(getInstruments);

            if (arr.length() == 0) {
                Toast.makeText(getApplicationContext(), "Unable to retrieve data", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < arr.length(); i++) {
                    Instrument instrument = new Instrument();
                    JSONObject obj = arr.getJSONObject(i);
                    instrument.setId(obj.getInt("instrument_id"));
                    instrument.setName(obj.getString("instrument_name"));
                    instrument.setIsActive(obj.getInt("isActive"));
                    instrument.setKksCode(obj.getString("kksCode"));
                    instrument.setBarcodeId(obj.getString("barcodeId"));
                    instrument.setLowerLimit(obj.getDouble("lowerLimit"));
                    instrument.setUpperLimit(obj.getDouble("upperLimit"));
                    instrument.setUnit(obj.getString("unit"));
                    instrument.setIsActive(obj.getInt("isActive"));
                    instrument.setSystemId(obj.getInt("systemId"));


                    if (!db.checkInstrument(instrument.getId()) && instrument.getIsActive() == 1) {
                        db.addInstrument(instrument);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    return true;
    }

}

