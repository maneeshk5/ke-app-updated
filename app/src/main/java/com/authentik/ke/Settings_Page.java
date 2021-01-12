package com.authentik.ke;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.System;
import com.authentik.model.User;
import com.authentik.utils.Constant;
import com.authentik.utils.DatabaseHelper;
import com.authentik.utils.SyncDbService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Settings_Page extends AppCompatActivity {

    ListView settings_lv;
    DatabaseHelper db;
    String usersURL;
    String plantsURL;
    String systemsURL;
    String instrumentsURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings__page);

        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "user is inactive from last 1 hour, logging out",Toast.LENGTH_SHORT).show();
                SharedPreferences sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putString("Username", "-");
                editor.putString("shift_id", "-");
                editor.apply();
                Intent intent = new Intent(getApplicationContext(),Login.class);
                finishAffinity();
//                startActivity(intent);
            }
        };
        startHandler();

        settings_lv = findViewById(R.id.settings_lv);

        db = new DatabaseHelper(getApplicationContext());

        SharedPreferences sharedpreferences = getSharedPreferences("ServerData", Context.MODE_PRIVATE);
        String serverURL = getResources().getString(R.string.server_name);
        final String server_url = sharedpreferences.getString("server_url", serverURL);

        usersURL = server_url + "readUsers.php";
        plantsURL = server_url + "readPlants.php";
        systemsURL = server_url + "readSystems.php";
        instrumentsURL = server_url + "readInstruments.php";

        settings_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {

                    if (isInternetAvailable()) {
//                    Toast.makeText(Settings_Page.this,"Enter New Server Url",Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder builder = new AlertDialog.Builder(Settings_Page.this);
                        builder.setView(null).setMessage(null);
                        builder.setTitle("Enter new ip address: ");
                        final EditText input_server_url = new EditText(Settings_Page.this);
//                    input_server_url.setText("http://");
                        input_server_url.setSelection(input_server_url.getText().length());
//                    input_server_url.setLayoutParams(new LinearLayout.LayoutParams(10, 10,1f));
//                    input_server_url.setPadding(30,10,30,10);


                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (input_server_url.getText().length() == 0) {
                                    Toast.makeText(Settings_Page.this, "Please Enter a valid url", Toast.LENGTH_SHORT).show();
                                } else {
                                    SharedPreferences sharedpreferences = getSharedPreferences("ServerData", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedpreferences.edit();
                                    editor.putString("server_url", "http://" + input_server_url.getText().toString() + "/ke_app_api");
                                    editor.apply();
                                    Toast.makeText(Settings_Page.this, "App needs a restart after server change", Toast.LENGTH_SHORT).show();
                                    Log.i("New Server Url", input_server_url.getText().toString());
                                    finishAffinity();
                                }
                            }
                        });
                        builder.setView(input_server_url);
                        builder.create().show();
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                    }
                } else if (position == 1) {

                    if (isInternetAvailable()) {
                        //Send local db to server before clearing
                        startService(new Intent(Settings_Page.this, SyncDbService.class));
                        final ProgressDialog dialog = ProgressDialog.show(Settings_Page.this, "Loading", "Please wait....", true);

                        db.rebuildDB(db.getWritableDatabase());

//                        getApplicationContext().deleteDatabase("pvs_ke");
//                        db = new DatabaseHelper(getApplicationContext());

                        final boolean[] serverURL = new boolean[1];

                        // sync server db to local db if internet is available
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                class DBSync extends AsyncTask<Void, Void, Void> {

                                    @Override
                                    protected Void doInBackground(Void... voids) {
                                        serverURL[0] = DbSync();
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        dialog.dismiss();
                                        if (!serverURL[0]) {
                                            Toast.makeText(getApplicationContext(), "Server Connection failed", Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Data Synced with Server, Restarting app", Toast.LENGTH_LONG).show();
                                            finishAffinity();
                                        }
                                    }
                                }
                                DBSync dbSync = new DBSync();
                                dbSync.execute();
                            }
                        });
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
    }

    Handler handler;
    Runnable r;

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
        handler.postDelayed(r, 3600*1000);
    }

    public boolean isInternetAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean DbSync() {

        //user Sync
        RequestHandler requestHandler = new RequestHandler();
        String getUsers = requestHandler.sendReadRequest(usersURL);

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

                    if (!db.checkUser(user.getName()) && user.getIsActive() == 1) {
                        db.addUser(user);
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //plant Sync
        String getPlants = requestHandler.sendReadRequest(plantsURL);
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
        String getSystems = requestHandler.sendReadRequest(systemsURL);
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
        String getInstruments = requestHandler.sendReadRequest(instrumentsURL);

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