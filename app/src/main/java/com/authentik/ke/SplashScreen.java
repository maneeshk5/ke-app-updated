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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.Shift;
import com.authentik.model.System;
import com.authentik.model.User;
import com.authentik.utils.DatabaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SplashScreen extends Activity {

    String usersURL;
    String plantsURL;
    String systemsURL;
    String instrumentsURL;

//    String instrumentsURL = "http://192.168.100.230:80/ke_app_api/readInstruments.php";
//    String usersURL = "http://192.168.100.230:80/ke_app_api/readUsers.php";
//    String plantsURL = "http://192.168.100.230:80/ke_app_api/readPlants.php";
//    String systemsURL = "http://192.168.100.230:80/ke_app_api/readSystems.php";

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //intialize api urls
        usersURL =  getString(R.string.server_name) + "readUsers.php";
        plantsURL = getString(R.string.server_name) + "readPlants.php";
        systemsURL = getString(R.string.server_name) + "readSystems.php";
        instrumentsURL = getString(R.string.server_name) + "readInstruments.php";


        //create local database
        db = new DatabaseHelper(getApplicationContext());

        // sync server db to local db if internet is available
        if (isInternetAvailable()) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
//                    DbSync();
//                    DatabaseSync();
                    class DBSync extends AsyncTask<Void,Void,Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {
//                            DatabaseSync();
                            DbSync();
                            return null;
                        }
                    }
                    DBSync dbSync = new DBSync();
                    dbSync.execute();
                }
            });
            thread.start();
//            thread.getState().toString();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            MyTask task = new MyTask(SplashScreen.this);
//            task.execute();
            Toast.makeText(getApplicationContext(), "Internet Available", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "Sever Sync not available due to no internet", Toast.LENGTH_SHORT).show();
        }

        SharedPreferences sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

            if (sharedpreferences.contains("isLoggedIn")) {
                boolean value = sharedpreferences.getBoolean("isLoggedIn", false);
                if (value) {
                    startActivity(new Intent(SplashScreen.this, Shift_Selection.class));
                } else {
                    startActivity(new Intent(SplashScreen.this, Login.class));
                }
            } else {
                startActivity(new Intent(SplashScreen.this, Login.class));
            }
            finish();

    }

    public boolean isInternetAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void DatabaseSync() {


        class UserSync extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {

                    JSONArray arr = new JSONArray(s);

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

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //returing the response
                return requestHandler.sendReadRequest(usersURL);
            }
        }

        UserSync ul = new UserSync();
        ul.execute();
//        Toast.makeText(getApplicationContext(), "Database Synced", Toast.LENGTH_SHORT).show();

        class PlantSync extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {

                    JSONArray arr = new JSONArray(s);

                    if (arr.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Unable to retrieve data", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < arr.length(); i++) {
                            User user = new User();
                            Plant plant = new Plant();
                            JSONObject obj = arr.getJSONObject(i);
                            plant.setPlant_id(obj.getInt("plant_id"));
                            plant.setPlant_name(obj.getString("plant_name"));
                            plant.setIsActive(obj.getInt("isActive"));
                            plant.setReadingTimeId(obj.getInt("readingTimeId"));

                            if (!db.checkPlant(plant.getPlant_id()) && user.getIsActive() == 1) {
                                db.addPlant(plant);
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //returning the response
                return requestHandler.sendReadRequest(plantsURL);
            }
        }

        PlantSync pl = new PlantSync();
        pl.execute();
//        Toast.makeText(getApplicationContext(), "Database Synced", Toast.LENGTH_SHORT).show();

        class InstrumentSync extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {

                    JSONArray arr = new JSONArray(s);

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

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //returing the response
                return requestHandler.sendReadRequest(instrumentsURL);
            }
        }

        InstrumentSync instrumentSync = new InstrumentSync();
        instrumentSync.execute();

        class SystemSync extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {

                    JSONArray arr = new JSONArray(s);

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

            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //returing the response
                return requestHandler.sendReadRequest(systemsURL);
            }
        }

        SystemSync sl = new SystemSync();
        sl.execute();
    }

    private void DbSync() {

        //user Sync
        RequestHandler requestHandler = new RequestHandler();
        String getUsers =  requestHandler.sendReadRequest(usersURL);
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


    }

}

