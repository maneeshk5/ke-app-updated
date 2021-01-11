package com.authentik.utils;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.authentik.ke.R;
import com.authentik.ke.RequestHandler;
import com.authentik.model.Reading;
import com.authentik.model.Shift;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SyncDbService extends Service {

    DatabaseHelper db;
    public SyncDbService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        String serverURL = getResources().getString(R.string.server_name);
        SharedPreferences sharedpreferences = getSharedPreferences("ServerData", Context.MODE_PRIVATE);
        final String server_url = sharedpreferences.getString("server_url", serverURL);

        final String readingsURL =  server_url + "addReading.php";
        final String shiftsURL = server_url + "addShift.php";
        final String shiftSystemStatusURL = server_url + "addSystemStatus.php";
        
         db = new DatabaseHelper(getApplicationContext());


        class ReadingSync extends AsyncTask<Void, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    clearDb();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Log.i("Response JSON",s);

                try {
                    clearDb();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... voids) {

                String status = " ";
                
                final HashMap<String,String> reading_params = new HashMap<>();
                List<Reading> readingList = db.getAllReadings();

                if(readingList.size() > 0) {

                    for (int i = 0; i < readingList.size(); i++) {
                        reading_params.put("id", readingList.get(i).getId());
                        reading_params.put("instrument_id", Integer.toString(readingList.get(i).getInstrument_id()));
                        reading_params.put("shift_id", readingList.get(i).getShift_id());
                        reading_params.put("reading_value", Double.toString(readingList.get(i).getReading_value()));
                        reading_params.put("date", readingList.get(i).getDate_time());
                        reading_params.put("system_id", Integer.toString(readingList.get(i).getInstrument_id()));
                        reading_params.put("plant_id", Integer.toString(readingList.get(i).getInstrument_id()));
                        reading_params.put("time", readingList.get(i).getTime());
                        if (readingList.get(i).getImage_path() != null) {
                            reading_params.put("image", readingList.get(i).getImage_path().toString());
                        }
                        RequestHandler requestHandler = new RequestHandler();
                        //returing the response
                        status = requestHandler.sendPostRequest(readingsURL, reading_params);
                    }
                }
                
                //sync shift_system_status db table
                final HashMap<String,String> shift_params = new HashMap<>();
                List<Shift> shiftList = db.getAllShifts();

                if(shiftList.size() > 0) {

                    for (int i = 0; i < shiftList.size(); i++) {
                        shift_params.put("id", shiftList.get(i).getId());
                        shift_params.put("name", shiftList.get(i).getName());
                        shift_params.put("readingType", shiftList.get(i).getReading_type());
                        shift_params.put("userName", shiftList.get(i).getUser_Name());
                        shift_params.put("startTime", shiftList.get(i).getStart_time());
                        shift_params.put("date", shiftList.get(i).getDate());

                        if (shiftList.get(i).getEnd_time() != null) {
                            shift_params.put("end_time",shiftList.get(i).getEnd_time());
                        }
                        RequestHandler requestHandler = new RequestHandler();
                        //returning the response
                        status = requestHandler.sendPostRequest(shiftsURL, shift_params);
                    }
                }

                //sync shift_system_status db table
                final HashMap<String,String> shift_system_status_params = new HashMap<>();
//                List<Shift> shiftList = db.getAllShifts();
                List<ContentValues> values = db.getAllSystemStatus();
                if(values.size() > 0) {

                    for (int i = 0; i < values.size(); i++) {
                        shift_system_status_params.put("id", values.get(i).get("id").toString());
                        shift_system_status_params.put("shift_id", values.get(i).get("shift_id").toString());
                        shift_system_status_params.put("system_id", values.get(i).get("system_id").toString());
                        shift_system_status_params.put("system_status_value", values.get(i).get("system_status_value").toString());
                        shift_system_status_params.put("date_time", values.get(i).get("date_time").toString());

                        RequestHandler requestHandler = new RequestHandler();
                        //returing the response
                        status = requestHandler.sendPostRequest(shiftSystemStatusURL, shift_system_status_params);
                    }
                }
                
                return status;
            }
        }

        ReadingSync ul = new ReadingSync();
        ul.execute();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    private void clearDb() throws ParseException {
//        Log.i("clearDb method","Hello");
        List<Reading> readingList =  db.getAllReadings();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
        Date todayDate = new Date();
        String fortodayDate = currentDate.format(todayDate);
        Date todayDate1 =sdformat.parse(fortodayDate);

        if (readingList.size() > 0) {
            for (int i=0; i<readingList.size(); i++) {
                Date recordedDate = sdformat.parse(readingList.get(i).getDate_time());
                if (recordedDate.compareTo(todayDate1) < 0) {
                    Log.i("Reading status",readingList.get(i).getId() +  " should be deleted");
                }
                else {
                    Log.i("Reading status",readingList.get(i).getId() + " should not be deleted");

                }

            }
        }
    }
}