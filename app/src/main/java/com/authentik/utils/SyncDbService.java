package com.authentik.utils;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.authentik.ke.Login;
import com.authentik.ke.R;
import com.authentik.ke.RequestHandler;
import com.authentik.ke.Shift_Selection;
import com.authentik.ke.SplashScreen;
import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.Reading;
import com.authentik.model.Shift;
import com.authentik.model.System;
import com.authentik.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SyncDbService extends Service {

    String readingsURL;
    String shiftsURL;
    String shiftSystemStatusURL;
    String usersURL;
    String systemsURL;
    String instrumentsURL;
    String plantsURL;
    ProgressDialog dialog;
    SharedPreferences sharedpreferences;

    DatabaseHelper db;

    public SyncDbService() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String serverURL = getResources().getString(R.string.server_name);
        SharedPreferences sharedpreferences2 = getSharedPreferences("ServerData", Context.MODE_PRIVATE);
        String server_url = sharedpreferences2.getString("server_url", serverURL);

        db = new DatabaseHelper(getApplicationContext());
//        dialog = ProgressDialog.show(this, "Loading", "Please wait....", true);

        readingsURL = server_url + "addReading.php";
        shiftsURL = server_url + "addShift.php";
        shiftSystemStatusURL = server_url + "addSystemStatus.php";
        usersURL = server_url + "readUsers.php";
        plantsURL = server_url + "readPlants.php";
        systemsURL = server_url + "readSystems.php";
        instrumentsURL = server_url + "readInstruments.php";

        Log.i("Service Status:", "Sync Service Started");

        final boolean[] serverConn = new boolean[1];

        if (isInternetAvailable()) {
//            dialog = ProgressDialog.show(this, "Loading", "Please wait....", true);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    class DBSync extends AsyncTask<Void, Void, Void> {

                        @Override
                        protected Void doInBackground(Void... voids) {
//                            DatabaseSync();
                            serverConn[0] = DbSync();
                            sendToServer();

                            try {
                                clearDb();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
//                        dialog.dismiss();
                            if (!serverConn[0]) {
                                Toast.makeText(getApplicationContext(), "Server Connection Error", Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Data Synced with Server", Toast.LENGTH_LONG).show();
                            }
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
        }
        else {
            Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
            sharedpreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
            if (sharedpreferences.contains("isLoggedIn")) {
                boolean value = sharedpreferences.getBoolean("isLoggedIn", false);
                Intent intent2;
                if (value) {
                    intent2 = new Intent(new Intent(getApplicationContext(), Shift_Selection.class));
                } else {
                    intent2 = new Intent(new Intent(getApplicationContext(), Login.class));
                }
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
            } else {
                Intent intent2 = new Intent(new Intent(getApplicationContext(), Login.class));
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
            }
        }

        return START_STICKY;
    }


    public boolean isInternetAvailable() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onCreate() {
        Log.i("Service Status:", "Sync Service Created");

        String serverURL = getResources().getString(R.string.server_name);
        SharedPreferences sharedpreferences2 = getSharedPreferences("ServerData", Context.MODE_PRIVATE);
        String server_url = sharedpreferences2.getString("server_url", serverURL);

        db = new DatabaseHelper(getApplicationContext());
//        dialog = ProgressDialog.show(this, "Loading", "Please wait....", true);

        readingsURL = server_url + "addReading.php";
        shiftsURL = server_url + "addShift.php";
        shiftSystemStatusURL = server_url + "addSystemStatus.php";
        usersURL = server_url + "readUsers.php";
        plantsURL = server_url + "readPlants.php";
        systemsURL = server_url + "readSystems.php";
        instrumentsURL = server_url + "readInstruments.php";


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    private void clearDb() throws ParseException {
//        Log.i("clearDb method", "Hello");
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
        Date todayDate = new Date();
        String fortodayDate = currentDate.format(todayDate);
        Date todayDate1 = sdformat.parse(fortodayDate);

        //clear reading table data
        List<Reading> readingList = db.getAllReadings();
        if (readingList.size() > 0) {
            for (int i = 0; i < readingList.size(); i++) {
                Date recordedReadingDate = sdformat.parse(readingList.get(i).getDate_time());
                    if (recordedReadingDate.compareTo(todayDate1) < 0 && readingList.get(i).getSync_status() == 1) {
                    Log.i("Reading status", readingList.get(i).getId() + " should be deleted");
                    db.deleteReading(readingList.get(i));
                } else {
                    Log.i("Reading status", readingList.get(i).getId() + " should not be deleted");
                }

            }
        }

        //clear shift table data
        List<Shift> shiftList = db.getAllShifts();
        if (shiftList.size() > 0) {
            for (int i = 0; i < shiftList.size(); i++) {
                Date recordedShiftDate = sdformat.parse(shiftList.get(i).getDate());
                if (recordedShiftDate.compareTo(todayDate1) < 0 && shiftList.get(i).getSync_status() == 1) {
                    Log.i("shift status", shiftList.get(i).getId() + " should be deleted");
                    db.deleteShift(shiftList.get(i));
                } else {
                    Log.i("shift status", shiftList.get(i).getId() + " should not be deleted");
                }

            }
        }
        //clear status table data
        List<ContentValues> values = db.getAllSystemStatus();
        SimpleDateFormat sdformat2 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        if (values.size() > 0) {
            for (int i = 0; i < values.size(); i++) {
                Date recordedStatusDate = sdformat2.parse(values.get(i).getAsString("date_time"));
                if (recordedStatusDate.compareTo(todayDate1) < 0 && Integer.parseInt(values.get(i).getAsString("sync_status")) == 1) {
                    Log.i("system status", values.get(i).getAsString("id") + " should be deleted");
                    db.deleteStatus(values.get(i).getAsString("id"));
                } else {
                    Log.i("system status", values.get(i).getAsString("id") + " should not be deleted");
                }

            }
        }
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
        if (getPlants.equals("Invalid Server URL")) {
            return false;
        }
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
        if (getSystems.equals("Invalid Server URL")) {
            return false;
        }
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
        if (getInstruments.equals("Invalid Server URL")) {
            return false;
        }
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

    private boolean sendToServer() {
        Log.i("Hello", "Server method");

        String status = " ";

        final HashMap<String, String> reading_params = new HashMap<>();
        List<Reading> readingList = db.getAllReadings();

        if (readingList.size() > 0) {

            for (int i = 0; i < readingList.size(); i++) {
                if (readingList.get(i).getSync_status() == 0) {
                    reading_params.put("id", readingList.get(i).getId());
                    reading_params.put("instrument_id", Integer.toString(readingList.get(i).getInstrument_id()));
                    reading_params.put("shift_id", readingList.get(i).getShift_id());
                    reading_params.put("reading_value", Double.toString(readingList.get(i).getReading_value()));
                    reading_params.put("date", readingList.get(i).getDate_time());
                    reading_params.put("system_id", Integer.toString(readingList.get(i).getSystem_id()));
                    reading_params.put("plant_id", Integer.toString(readingList.get(i).getPlant_id()));
                    reading_params.put("time", readingList.get(i).getTime());
                    reading_params.put("user_name", readingList.get(i).getUser_name());
                    if (readingList.get(i).getImage_path() != null) {
                        reading_params.put("image",  Base64.encodeToString(readingList.get(i).getImage_path(), Base64.DEFAULT));
//                        reading_params.put("image",readingList.get(i).getImage_path().toString());
                    }
                    RequestHandler requestHandler = new RequestHandler();
                    //returing the response
                    status = requestHandler.sendPostRequest(readingsURL, reading_params);

                    if (status.equals("Invalid Server URL")) {
                        return false;
                    }
                    JSONObject obj;
                    try {
                        obj = new JSONObject(status);

                        if (obj.getInt("success") == 1) {
                            db.changeReadingSyncStatus(readingList.get(i).getId(), 1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //sync shift_system_status db table
        final HashMap<String, String> shift_params = new HashMap<>();
        List<Shift> shiftList = db.getAllShifts();

        if (shiftList.size() > 0) {

            for (int i = 0; i < shiftList.size(); i++) {
                if (shiftList.get(i).getSync_status() == 0) {
                    shift_params.put("id", shiftList.get(i).getId());
                    shift_params.put("name", shiftList.get(i).getName());
                    shift_params.put("readingType", shiftList.get(i).getReading_type());
                    shift_params.put("userName", shiftList.get(i).getUser_Name());
                    shift_params.put("startTime", shiftList.get(i).getStart_time());
                    shift_params.put("date", shiftList.get(i).getDate());

                    if (shiftList.get(i).getEnd_time() != null) {
                        shift_params.put("end_time", shiftList.get(i).getEnd_time());
                    }
                    RequestHandler requestHandler = new RequestHandler();
                    //returning the response
                    status = requestHandler.sendPostRequest(shiftsURL, shift_params);
                    if (status.equals("Invalid Server URL")) {
                        return false;
                    }

                    JSONObject obj;
                    try {
                        obj = new JSONObject(status);

                        if (obj.getInt("success") == 1) {
//                                db.changeReadingSyncStatus(readingList.get(i).getId(),1);
                            db.changeShiftSyncStatus(shiftList.get(i).getId(), 1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //sync shift_system_status db table
        final HashMap<String, String> shift_system_status_params = new HashMap<>();
//                List<Shift> shiftList = db.getAllShifts();
        List<ContentValues> values = db.getAllSystemStatus();
        if (values.size() > 0) {

            for (int i = 0; i < values.size(); i++) {
                if (values.get(i).getAsInteger("sync_status") == 0) {
                    shift_system_status_params.put("id", values.get(i).get("id").toString());
                    shift_system_status_params.put("shift_id", values.get(i).get("shift_id").toString());
                    shift_system_status_params.put("system_id", values.get(i).get("system_id").toString());
                    shift_system_status_params.put("system_status_value", values.get(i).get("system_status_value").toString());
                    shift_system_status_params.put("date_time", values.get(i).get("date_time").toString());
                    shift_system_status_params.put("user_name", values.get(i).get("user_name").toString());


                    RequestHandler requestHandler = new RequestHandler();
                    //returing the response
                    status = requestHandler.sendPostRequest(shiftSystemStatusURL, shift_system_status_params);
                    if (status.equals("Invalid Server URL")) {
                        return false;
                    }
                    JSONObject obj;
                    try {
                        obj = new JSONObject(status);

                        if (obj.getInt("success") == 1) {
//                                db.changeReadingSyncStatus(readingList.get(i).getId(),1);
                            db.changeSystemStatusSync(values.get(i).getAsString("id"), 1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

}