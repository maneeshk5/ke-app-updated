package com.authentik.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.authentik.model.Instrument;
import com.authentik.model.Plant;
import com.authentik.model.Reading;
import com.authentik.model.Shift;
import com.authentik.model.User;
import com.authentik.model.System;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "pvs_ke";

    // table names
    private static final String TABLE_USER = "user";
    private static final String TABLE_PLANTS = "plants";
    private static final String TABLE_SYSTEMS = "systems";
    private static final String TABLE_INSTRUMENTS = "instruments";
    private static final String TABLE_SHIFT = "shift_details";
    private static final String TABLE_READING  = "readings";
    private static final String TABLE_SYSTEM_STATUS  = "shift_system_status";


    // User Table Columns names
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "userName";
    private static final String COLUMN_USER_PASSWORD = "user_password";
    private static final String COLUMN_USER_IS_ACTIVE = "isActive";
    private static final String COLUMN_USER_IS_DELETED = "isDeleted";

    // Plant Table Column Names
    private static final String COLUMN_PLANT_ID = "id";
    private static final String COLUMN_PLANT_NAME = "plantName";
    private static final String COLUMN_PLANT_IS_ACTIVE = "isActive";
    private static final String COLUMN_PLANT_READING_TIME_ID = "readingTimeId";

    //System Table Column Names
    private static final String COLUMN_SYSTEM_ID = "id";
    private static final String COLUMN_SYSTEM_NAME = "systemName";
    private static final String COLUMN_SYSTEM_IS_ACTIVE = "isActive";
    private static final String COLUMN_SYSTEM_LOGSHEET = "logSheet";
    private static final String COLUMN_SYSTEM_PLANT_ID = "plant_id";
    private static final String COLUMN_SYSTEM_STATUS = "system_status";

    //Instrument Table Column Names
    private static final String COLUMN_INSTRUMENT_ID = "id";
    private static final String COLUMN_INSTRUMENT_NAME = "instrumentName";
    private static final String COLUMN_INSTRUMENT_KKSCODE = "kksCode";
    private static final String COLUMN_INSTRUMENT_BARCODE_ID = "barcodeId";
    private static final String COLUMN_INSTRUMENT_UNIT = "unit";
    private static final String COLUMN_INSTRUMENT_LOWER_LIMIT = "lowerLimit";
    private static final String COLUMN_INSTRUMENT_UPPER_LIMIT = "upperLimit";
    private static final String COLUMN_INSTRUMENT_SYSTEM_ID = "systemId";
    private static final String COLUMN_INSTRUMENT_IS_ACTIVE = "isActive";

    //Shift Table Column Names
    private static final String COLUMN_SHIFT_ID = "id";
    private static final String COLUMN_SHIFT_NAME = "name";
    private static final String COLUMN_SHIFT_READING_TYPE = "readingType";
    private static final String COLUMN_SHIFT_START_TIME = "startTime";
    private static final String COLUMN_SHIFT_END_TIME= "endTime";
    private static final String COLUMN_SHIFT_USER_NAME = "userName";
    private static final String COLUMN_SHIFT_DATE = "date";
    private static final String COLUMN_SHIFT_SYNC_STATUS= "sync_status";


    //Reading Table Column Names
    private static final String COLUMN_READING_ID = "id";
    private static final String COLUMN_READING_INSTRUMENT_ID = "instrument_id";
    private static final String COLUMN_READING_IMAGE_PATH = "image";
    private static final String COLUMN_READING_TIME = "time";
    private static final String COLUMN_READING_DATETIME = "date_time";
    private static final String COLUMN_READING_VALUE = "reading_value";
    private static final String COLUMN_READING_SHIFT_ID = "shift_id";
    private static final String COLUMN_READING_SYSTEM_ID = "system_id";
    private static final String COLUMN_READING_PLANT_ID = "plant_id";
    private static final String COLUMN_READING_SYSTEM_STATUS = "system_status";
    private static final String COLUMN_READING_SYNC_STATUS= "sync_status";

    //System Status Column Names
    private static final String COLUMN_STATUS_ID = "id";
    private static final String COLUMN_STATUS_SYSTEM_ID = "system_id";
    private static final String COLUMN_STATUS_SHIFT_ID = "shift_id";
    private static final String COLUMN_STATUS_VALUE = "system_status_value";
    private static final String COLUMN_STATUS_DATETIME = "date_time";
    private static final String COLUMN_STATUS_SYNC_STATUS= "sync_status";


    // create table sql query
    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_PASSWORD + " TEXT," + COLUMN_USER_IS_ACTIVE + " INTEGER,"
            + COLUMN_USER_IS_DELETED + " INTEGER" + ")";

    private String CREATE_PLANT_TABLE = "CREATE TABLE " + TABLE_PLANTS + "("
            + COLUMN_PLANT_ID + " INTEGER PRIMARY KEY," + COLUMN_PLANT_NAME + " TEXT,"
            + COLUMN_PLANT_IS_ACTIVE + " INTEGER," + COLUMN_PLANT_READING_TIME_ID + " INTEGER" + ")";

    private String CREATE_SYSTEM_TABLE = "CREATE TABLE " + TABLE_SYSTEMS + "("
            + COLUMN_SYSTEM_ID + " INTEGER PRIMARY KEY," + COLUMN_SYSTEM_NAME + " TEXT,"
            + COLUMN_SYSTEM_IS_ACTIVE + " INTEGER," + COLUMN_SYSTEM_LOGSHEET + " TEXT," + COLUMN_SYSTEM_PLANT_ID + " INTEGER, "
            + COLUMN_SYSTEM_STATUS + " TEXT "  + ")";

    private String CREATE_INSTRUMENT_TABLE = "CREATE TABLE " + TABLE_INSTRUMENTS + "("
            + COLUMN_INSTRUMENT_ID + " INTEGER PRIMARY KEY," + COLUMN_INSTRUMENT_NAME + " TEXT,"
            + COLUMN_INSTRUMENT_IS_ACTIVE + " INTEGER," + COLUMN_INSTRUMENT_KKSCODE + " TEXT," + COLUMN_INSTRUMENT_SYSTEM_ID + " INTEGER, "
            +  COLUMN_INSTRUMENT_UNIT + " INTEGER, " + COLUMN_INSTRUMENT_LOWER_LIMIT + " REAL, " + COLUMN_INSTRUMENT_UPPER_LIMIT
            + " REAL, " + COLUMN_INSTRUMENT_BARCODE_ID + " TEXT " + ")";

    private String CREATE_SHIFT_TABLE = "CREATE TABLE " + TABLE_SHIFT + "("
            + COLUMN_SHIFT_ID + " TEXT PRIMARY KEY ," + COLUMN_SHIFT_NAME + " TEXT,"
            + COLUMN_SHIFT_READING_TYPE + " TEXT," + COLUMN_SHIFT_USER_NAME + " TEXT,"
            + COLUMN_SHIFT_START_TIME + " TEXT, " + COLUMN_SHIFT_END_TIME + " TEXT, " +
            COLUMN_SHIFT_DATE + " TEXT, " +  COLUMN_SHIFT_SYNC_STATUS + " INTEGER " + ")";

    private String CREATE_READING_TABLE = "CREATE TABLE " + TABLE_READING + "("
            + COLUMN_READING_ID + " TEXT PRIMARY KEY ," + COLUMN_READING_INSTRUMENT_ID + " INTEGER,"
            + COLUMN_READING_IMAGE_PATH + " BLOB," + COLUMN_READING_TIME + " TEXT,"
            + COLUMN_READING_SHIFT_ID + " TEXT, " + COLUMN_READING_VALUE + " DOUBLE , "
            + COLUMN_READING_DATETIME + " TEXT, " + COLUMN_READING_SYSTEM_ID + " INTEGER, " + COLUMN_READING_PLANT_ID
            + " INTEGER, " + COLUMN_READING_SYSTEM_STATUS +  " TEXT, " +  COLUMN_READING_SYNC_STATUS + " INTEGER " + ")";

    private String CREATE_SHIFT_SYSTEM_STATUS_TABLE = "CREATE TABLE " + TABLE_SYSTEM_STATUS + "("
            + COLUMN_STATUS_ID + " TEXT PRIMARY KEY, " + COLUMN_STATUS_SHIFT_ID + " TEXT, " + COLUMN_STATUS_SYSTEM_ID
            + " INTEGER, " + COLUMN_STATUS_VALUE + " TEXT, " + COLUMN_STATUS_DATETIME +  " TEXT, " +  COLUMN_READING_SYNC_STATUS + " INTEGER " + ")";


    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
    private String DROP_PLANT_TABLE = "DROP TABLE IF EXISTS " + TABLE_PLANTS;
    private String DROP_SYSTEM_TABLE = "DROP TABLE IF EXISTS " + TABLE_SYSTEMS;
    private String DROP_INSTRUMENT_TABLE = "DROP TABLE IF EXISTS " + TABLE_INSTRUMENTS;
    private String DROP_SHIFT_TABLE = "DROP TABLE IF EXISTS " + TABLE_SHIFT;
    private String DROP_READING_TABLE = "DROP TABLE IF EXISTS " + TABLE_READING;
    private String DROP_SYSTEM_STATUS_TABLE = "DROP TABLE IF EXISTS " + TABLE_SYSTEM_STATUS;


    //empty table query
    private String TRUNCATE_SHIFT_TABLE = "DELETE FROM " + TABLE_SHIFT;
    private String TRUNCATE_READING_TABLE = "DELETE FROM " + TABLE_READING;


    /**
     * Constructor
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_PLANT_TABLE);
        db.execSQL(CREATE_SYSTEM_TABLE);
        db.execSQL(CREATE_INSTRUMENT_TABLE);
        db.execSQL(CREATE_SHIFT_TABLE);
        db.execSQL(CREATE_READING_TABLE);
        db.execSQL(CREATE_SHIFT_SYSTEM_STATUS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_PLANT_TABLE);
        db.execSQL(DROP_SYSTEM_TABLE);
        db.execSQL(DROP_INSTRUMENT_TABLE);
        db.execSQL(DROP_SHIFT_TABLE);
        db.execSQL(DROP_READING_TABLE);
        db.execSQL(DROP_SYSTEM_STATUS_TABLE);

        // Create tables again
        onCreate(db);
    }

    public void rebuildDB(SQLiteDatabase db) {
        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_PLANT_TABLE);
        db.execSQL(DROP_SYSTEM_TABLE);
        db.execSQL(DROP_INSTRUMENT_TABLE);
//        db.execSQL(DROP_SHIFT_TABLE);
//        db.execSQL(DROP_READING_TABLE);
//        db.execSQL(DROP_SYSTEM_STATUS_TABLE);

        // Create tables again
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_PLANT_TABLE);
        db.execSQL(CREATE_SYSTEM_TABLE);
        db.execSQL(CREATE_INSTRUMENT_TABLE);
    }

    /**
     * This method is to create user record
     *
     * @param user
     */
    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID,user.getId());
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        values.put(COLUMN_USER_IS_ACTIVE,user.getIsActive());
        values.put(COLUMN_USER_IS_DELETED,user.getIsDeleted());


        // Inserting Row
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public void addPlant(Plant plant) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PLANT_NAME, plant.getPlant_name());
        values.put(COLUMN_PLANT_ID, plant.getPlant_id());
        values.put(COLUMN_PLANT_IS_ACTIVE, plant.getIsActive());
        values.put(COLUMN_PLANT_READING_TIME_ID, plant.getReadingTimeId());

        // Inserting Row
        db.insert(TABLE_PLANTS, null, values);
        db.close();
    }

    public void addSystem(System system) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SYSTEM_ID, system.getId());
        values.put(COLUMN_SYSTEM_NAME, system.getName());
        values.put(COLUMN_SYSTEM_IS_ACTIVE, system.getIsActive());
        values.put(COLUMN_SYSTEM_LOGSHEET, system.getLogSheet());
        values.put(COLUMN_SYSTEM_PLANT_ID,system.getPlantId());

        // Inserting Row
        db.insert(TABLE_SYSTEMS, null, values);
        db.close();
    }

    public void addInstrument(Instrument instrument) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_INSTRUMENT_ID, instrument.getId());
        values.put(COLUMN_INSTRUMENT_NAME, instrument.getName());
        values.put(COLUMN_INSTRUMENT_KKSCODE, instrument.getKksCode());
        values.put(COLUMN_INSTRUMENT_BARCODE_ID, instrument.getBarcodeId());
        values.put(COLUMN_INSTRUMENT_UNIT, instrument.getUnit());
        values.put(COLUMN_INSTRUMENT_LOWER_LIMIT, instrument.getLowerLimit());
        values.put(COLUMN_INSTRUMENT_UPPER_LIMIT, instrument.getUpperLimit());
        values.put(COLUMN_INSTRUMENT_SYSTEM_ID, instrument.getSystemId());
        values.put(COLUMN_INSTRUMENT_IS_ACTIVE, instrument.getIsActive());

        // Inserting Row
        db.insert(TABLE_INSTRUMENTS, null, values);
        db.close();
    }

    public void addShift(Shift shift) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SHIFT_ID, shift.getId());
        values.put(COLUMN_SHIFT_NAME, shift.getName());
        values.put(COLUMN_SHIFT_READING_TYPE, shift.getReading_type());
        values.put(COLUMN_SHIFT_START_TIME, shift.getStart_time());
        values.put(COLUMN_SHIFT_END_TIME, shift.getEnd_time());
        values.put(COLUMN_SHIFT_USER_NAME, shift.getUser_Name());
        values.put(COLUMN_SHIFT_DATE, shift.getDate());
        values.put(COLUMN_SHIFT_SYNC_STATUS,0);


        // Inserting Row
        db.insert(TABLE_SHIFT, null, values);
        db.close();
    }


    public void addReading(Reading reading) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_READING_ID, reading.getId());
        values.put(COLUMN_READING_INSTRUMENT_ID, reading.getInstrument_id());
        values.put(COLUMN_READING_IMAGE_PATH, reading.getImage_path());
        values.put(COLUMN_READING_TIME, reading.getTime());
        values.put(COLUMN_READING_SHIFT_ID, reading.getShift_id());
        values.put(COLUMN_READING_VALUE, reading.getReading_value());
        values.put(COLUMN_READING_DATETIME, reading.getDate_time());
        values.put(COLUMN_READING_SYSTEM_ID, reading.getSystem_id());
        values.put(COLUMN_READING_PLANT_ID, reading.getPlant_id());
        values.put(COLUMN_READING_SYNC_STATUS,0);

        // Inserting Row
        db.insert(TABLE_READING, null, values);
        db.close();
    }

    public void resetLocalDb(SQLiteDatabase db) {
        db.execSQL(TRUNCATE_READING_TABLE);
        db.execSQL(TRUNCATE_SHIFT_TABLE);
    }
    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return list
     */

    public List<Shift> getAllShifts() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_SHIFT_ID,
                COLUMN_SHIFT_NAME,
                COLUMN_SHIFT_READING_TYPE,
                COLUMN_SHIFT_USER_NAME,
                COLUMN_SHIFT_START_TIME,
                COLUMN_SHIFT_END_TIME,
                COLUMN_SHIFT_DATE,
                COLUMN_SHIFT_SYNC_STATUS
        };
        // sorting orders
        String sortOrder =
                COLUMN_SHIFT_ID + " ASC";
        List<Shift> shiftList = new ArrayList<Shift>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table

        Cursor cursor = db.query(TABLE_SHIFT, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Shift shift = new Shift();
                shift.setId(cursor.getString(cursor.getColumnIndex(COLUMN_SHIFT_ID)));
                shift.setName(cursor.getString(cursor.getColumnIndex(COLUMN_SHIFT_NAME)));
                shift.setReading_type(cursor.getString(cursor.getColumnIndex(COLUMN_SHIFT_READING_TYPE)));
                shift.setUser_Name(cursor.getString(cursor.getColumnIndex(COLUMN_SHIFT_USER_NAME)));
                shift.setStart_time(cursor.getString(cursor.getColumnIndex(COLUMN_SHIFT_START_TIME)));
                shift.setEnd_time(cursor.getString(cursor.getColumnIndex(COLUMN_SHIFT_END_TIME)));
                shift.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_SHIFT_DATE)));
                shift.setSync_status(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_SHIFT_SYNC_STATUS))));

                // Adding user record to list
                shiftList.add(shift);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return shiftList;
    }

    public List<ContentValues> getAllSystemStatus() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_STATUS_ID,
                COLUMN_STATUS_VALUE,
                COLUMN_STATUS_SYSTEM_ID,
                COLUMN_STATUS_SHIFT_ID,
                COLUMN_STATUS_DATETIME,
                COLUMN_STATUS_SYNC_STATUS
        };
        // sorting orders
        String sortOrder =
                COLUMN_STATUS_ID + " ASC";

        List<ContentValues> valuesList = new ArrayList<>();


        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table

        Cursor cursor = db.query(TABLE_SYSTEM_STATUS, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
//                Shift shift = new Shift();
                ContentValues values = new ContentValues();
                values.put("id",cursor.getString(cursor.getColumnIndex(COLUMN_STATUS_ID)));
                values.put("shift_id",cursor.getString(cursor.getColumnIndex(COLUMN_STATUS_SHIFT_ID)));
                values.put("system_id",Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS_SYSTEM_ID))));
                values.put("system_status_value",cursor.getString(cursor.getColumnIndex(COLUMN_STATUS_VALUE)));
                values.put("date_time",cursor.getString(cursor.getColumnIndex(COLUMN_STATUS_DATETIME)));
                values.put("sync_status",Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_STATUS_SYNC_STATUS))));

                // Adding user record to list
                valuesList.add(values);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return valuesList;
    }


    public List<User> getAllUser() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_NAME,
                COLUMN_USER_PASSWORD
        };
        // sorting orders
        String sortOrder =
                COLUMN_USER_NAME + " ASC";
        List<User> userList = new ArrayList<User>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table

        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
                // Adding user record to list
                userList.add(user);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return userList;
    }

    public List<Reading> getAllReadings() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_READING_ID,
                COLUMN_READING_DATETIME,
                COLUMN_READING_TIME,
                COLUMN_READING_VALUE,
                COLUMN_READING_SHIFT_ID,
                COLUMN_READING_IMAGE_PATH,
                COLUMN_READING_INSTRUMENT_ID,
                COLUMN_READING_SYNC_STATUS,
                COLUMN_READING_SYSTEM_ID,
                COLUMN_READING_PLANT_ID};
        // sorting orders
        String sortOrder =
                COLUMN_READING_ID + " ASC";

        List<Reading> readingList = new ArrayList<Reading>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table

        Cursor cursor = db.query(TABLE_READING, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
//                User user = new User();
                Reading reading = new Reading();
                reading.setId(cursor.getString(cursor.getColumnIndex(COLUMN_READING_ID)));
                reading.setReading_value(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_READING_VALUE))));
                reading.setShift_id(cursor.getString(cursor.getColumnIndex(COLUMN_READING_SHIFT_ID)));
                reading.setInstrument_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_READING_INSTRUMENT_ID))));
                reading.setDate_time(cursor.getString(cursor.getColumnIndex(COLUMN_READING_DATETIME)));
                reading.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_READING_TIME)));
                reading.setImage_path(cursor.getBlob(cursor.getColumnIndex(COLUMN_READING_IMAGE_PATH)));
                reading.setSync_status(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_READING_SYNC_STATUS))));
                reading.setPlant_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_READING_PLANT_ID))));
                reading.setSystem_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_READING_SYSTEM_ID))));

                // Adding user record to list
                readingList.add(reading);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return readingList;
    }

    public List<Plant> getAllPlants() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_PLANT_ID,
                COLUMN_PLANT_NAME,
                COLUMN_PLANT_READING_TIME_ID,
                COLUMN_PLANT_IS_ACTIVE};
        // sorting orders
        String sortOrder =
                COLUMN_PLANT_ID + " ASC";
        List<Plant> plantList = new ArrayList<Plant>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_PLANTS, //Table to query
                columns,    //columns to return
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Plant plant = new Plant();
                plant.setPlant_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_ID))));
                plant.setPlant_name(cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_NAME)));
                plant.setIsActive(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_IS_ACTIVE))));
                plant.setReadingTimeId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_READING_TIME_ID))));

                // Adding user record to list
                plantList.add(plant);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return plantList;
    }

    public List<System> getPlantSystem(int plantId) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_SYSTEM_ID,
                COLUMN_SYSTEM_NAME,
                COLUMN_SYSTEM_LOGSHEET,
                COLUMN_SYSTEM_IS_ACTIVE,
                COLUMN_SYSTEM_PLANT_ID};
        // sorting orders
        String sortOrder =
                COLUMN_SYSTEM_ID + " ASC";

        String selection = COLUMN_SYSTEM_PLANT_ID + " = ?";

        String[] selectionArgs = {Integer.toString(plantId)};

        List<System> systemList = new ArrayList<System>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_SYSTEMS, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                System system = new System();
                system.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_SYSTEM_ID))));
                system.setName(cursor.getString(cursor.getColumnIndex(COLUMN_SYSTEM_NAME)));
                system.setIsActive(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_SYSTEM_IS_ACTIVE))));
                system.setLogSheet(cursor.getString(cursor.getColumnIndex(COLUMN_SYSTEM_LOGSHEET)));
                system.setPlantId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_SYSTEM_PLANT_ID))));

                // Adding user record to list
                systemList.add(system);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return systemList;
    }

    public Reading getReading(String shift_id, int instrument_id) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_READING_ID,
                COLUMN_READING_DATETIME,
                COLUMN_READING_TIME,
                COLUMN_READING_VALUE,
                COLUMN_READING_SHIFT_ID,
                COLUMN_READING_INSTRUMENT_ID};
        // sorting orders
        String sortOrder =
                COLUMN_READING_ID + " ASC";

        String selection = COLUMN_READING_SHIFT_ID + " = ?" + " AND " + COLUMN_READING_INSTRUMENT_ID + " = ?";

        String[] selectionArgs = {shift_id,Integer.toString(instrument_id)};

//        List<System> systemList = new ArrayList<System>();
        Reading reading = new Reading();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_READING, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    reading.setId(cursor.getString(cursor.getColumnIndex(COLUMN_READING_ID)));
                    reading.setReading_value(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_READING_VALUE))));
                    reading.setShift_id(cursor.getString(cursor.getColumnIndex(COLUMN_READING_SHIFT_ID)));
                    reading.setInstrument_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_READING_INSTRUMENT_ID))));
                    reading.setDate_time(cursor.getString(cursor.getColumnIndex(COLUMN_READING_DATETIME)));
                    reading.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_READING_TIME)));

                    // Adding user record to list
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();

        // return user list
        return reading;
    }

    public User getPassword(String user_name) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_NAME,
                COLUMN_USER_PASSWORD,
                COLUMN_USER_IS_ACTIVE};
        // sorting orders
        String sortOrder =
                COLUMN_USER_ID + " ASC";

        String selection = COLUMN_USER_NAME + " = ?";

        String[] selectionArgs = {user_name};

//        List<System> systemList = new ArrayList<System>();
//        Reading reading = new Reading();
        User user = new User();
        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    user.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID))));
                    user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
                    user.setName(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
                    user.setIsActive(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_USER_IS_ACTIVE))));

                    // Adding user record to list
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();

        // return user list
        return user;
    }


    public List<Instrument> getSystemInstruments(int system_id) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_INSTRUMENT_ID,
                COLUMN_INSTRUMENT_NAME,
                COLUMN_INSTRUMENT_KKSCODE,
                COLUMN_INSTRUMENT_BARCODE_ID,
                COLUMN_INSTRUMENT_UNIT,
                COLUMN_INSTRUMENT_LOWER_LIMIT,
                COLUMN_INSTRUMENT_UPPER_LIMIT,
                COLUMN_INSTRUMENT_SYSTEM_ID,
                COLUMN_INSTRUMENT_IS_ACTIVE,
        };
        // sorting orders
        String sortOrder =
                COLUMN_INSTRUMENT_ID + " ASC";

        String selection = COLUMN_INSTRUMENT_SYSTEM_ID + " = ?";

        String[] selectionArgs = {Integer.toString(system_id)};

        List<Instrument> instrumentList = new ArrayList<Instrument>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_INSTRUMENTS, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Instrument instrument = new Instrument();
                instrument.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_ID))));
                instrument.setName(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_NAME)));
                instrument.setKksCode(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_KKSCODE)));
                instrument.setBarcodeId(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_BARCODE_ID)));
                instrument.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_UNIT)));
                instrument.setLowerLimit(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_LOWER_LIMIT))));
                instrument.setUpperLimit(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_UPPER_LIMIT))));
                instrument.setSystemId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_SYSTEM_ID))));
                instrument.setIsActive(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_IS_ACTIVE))));

                // Adding user record to list
                instrumentList.add(instrument);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return instrumentList;
    }


    public List<Instrument> getPlantInstruments(int plant_id) {
        List<System> systemList = getPlantSystem(plant_id);

        List<Instrument> PlantInstrumentList = new ArrayList<>();

        for (int i=0; i<systemList.size(); i++) {
            List<Instrument> SystemInstrumentList = getSystemInstruments(systemList.get(i).getId());
            PlantInstrumentList.addAll(SystemInstrumentList);
        }
        return PlantInstrumentList;
    }

    public int getInstrumentStatus(int instrument_id, String shift_id) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_READING_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_READING_INSTRUMENT_ID + " = ?" + " AND " + COLUMN_READING_SHIFT_ID + " = ?";
        // selection argument
        String[] selectionArgs = {Integer.toString(instrument_id),shift_id};
        // query user table with condition

        Cursor cursor = db.query(TABLE_READING, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
//        if (cursorCount > 0) {
//            return true;
//        }
//        return false;
//    }
        return cursorCount;
    }

    public int getSystemStatus(int system_id, String shift_id) {
//        int systemDone;
//        List<Instrument> instrumentList = getSystemInstruments(system_id);
//        int instStatus = 0;
//        for (int i=0; i<instrumentList.size(); i++) {
//            instStatus = instStatus + getInstrumentStatus(instrumentList.get(i).getId(),shift_id);
//        }
//        if (instStatus == instrumentList.size()) {
//            systemDone = 1;
//        }
//        else {
//            systemDone = 0;
//        }
//        return systemDone;
        // array of columns to fetch
        String[] columns = {
                COLUMN_READING_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_READING_SYSTEM_ID + " = ?" + " AND " + COLUMN_READING_SHIFT_ID + " = ?";
        // selection argument
        String[] selectionArgs = {Integer.toString(system_id),shift_id};
        // query user table with condition

        Cursor cursor = db.query(TABLE_READING, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
//        if (cursorCount > 0) {
//            return true;
//        }
//        return false;
//    }
        return cursorCount;
    }

    public int getPlantStatus(int plant_id, String shift_id) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_READING_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_READING_PLANT_ID + " = ?" + " AND " + COLUMN_READING_SHIFT_ID + " = ?";
        // selection argument
        String[] selectionArgs = {Integer.toString(plant_id),shift_id};
        // query user table with condition

        Cursor cursor = db.query(TABLE_READING, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
//        if (cursorCount > 0) {
//            return true;
//        }
//        return false;
//    }
        return cursorCount;
    }


    /**
         * This method to update user record
         *
         * @param user
         */
    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getName());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        // updating row
        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public void updateSystemStatus(System system) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SYSTEM_STATUS, system.getStatus());

        // updating row
        db.update(TABLE_SYSTEMS, values, COLUMN_SYSTEM_ID + " = ?",
                new String[]{String.valueOf(system.getId())});
        db.close();
    }

    public void addSystemStatus(String id, String shift_id, int system_id, String status_value, String date_time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS_ID, id);
        values.put(COLUMN_STATUS_SHIFT_ID, shift_id);
        values.put(COLUMN_STATUS_SYSTEM_ID, system_id);
        values.put(COLUMN_STATUS_VALUE, status_value);
        values.put(COLUMN_STATUS_DATETIME, date_time);
        values.put(COLUMN_STATUS_SYNC_STATUS,0);


        db.insert(TABLE_SYSTEM_STATUS, null, values);
        db.close();
    }



    public void insertReadingImage(String reading_id,byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_READING_IMAGE_PATH, image);

        // updating row
        db.update(TABLE_READING, values, COLUMN_READING_ID + " = ?",
                new String[]{String.valueOf(reading_id)});
        db.close();
    }

    public void changeReadingSyncStatus(String reading_id, int syncStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_READING_SYNC_STATUS, syncStatus);

        // updating row
        db.update(TABLE_READING, values, COLUMN_READING_ID + " = ?",
                new String[]{String.valueOf(reading_id)});
        db.close();
    }

    public void changeShiftSyncStatus(String shift_id, int syncStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SHIFT_SYNC_STATUS, syncStatus);

        // updating row
        db.update(TABLE_SHIFT, values, COLUMN_SHIFT_ID + " = ?",
                new String[]{String.valueOf(shift_id)});
        db.close();
    }

    public void changeSystemStatusSync(String status_id, int syncStatus) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS_SYNC_STATUS, syncStatus);

        // updating row
        db.update(TABLE_SYSTEM_STATUS, values, COLUMN_STATUS_ID + " = ?",
                new String[]{String.valueOf(status_id)});
        db.close();
    }

    public void setInstrumentBarcodeId(Instrument instrument) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_INSTRUMENT_BARCODE_ID, instrument.getBarcodeId());

        // updating row
        db.update(TABLE_INSTRUMENTS, values, COLUMN_INSTRUMENT_ID + " = ?",
                new String[]{String.valueOf(instrument.getId())});
        db.close();
    }



    /**
     * This method is to delete user record
     *
     * @param user
     */
    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_USER, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public void deleteReading(Reading reading) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_READING, COLUMN_READING_ID + " = ?",
                new String[]{String.valueOf(reading.getId())});
        db.close();
    }

    public void deleteShift(Shift shift) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_SHIFT, COLUMN_SHIFT_ID + " = ?",
                new String[]{String.valueOf(shift.getId())});
        db.close();
    }

    public void deleteStatus(String status_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // delete user record by id
        db.delete(TABLE_SYSTEM_STATUS, COLUMN_STATUS_ID + " = ?",
                new String[]{String.valueOf(status_id)});
        db.close();
    }

    public boolean checkUser(String userName) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_NAME + " = ?";
        // selection argument
        String[] selectionArgs = {userName};
        // query user table with condition
        /**
         EL* Here query function is used to fetch records from user table this function works like we use sql query.
         *          * SQL query equivalent to this query function is
         *          * SECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public boolean checkReading(String shift_id,int instrument_id) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_READING_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_SHIFT_ID + " = ?" + " AND " + COLUMN_INSTRUMENT_ID + " = ?";
        // selection argument
        String[] selectionArgs = {shift_id,Integer.toString(instrument_id)};
        // query user table with condition
        /**
         EL* Here query function is used to fetch records from user table this function works like we use sql query.
         *          * SQL query equivalent to this query function is
         *          * SECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(TABLE_READING, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }


    public boolean checkInstrument(int instrumentId) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_INSTRUMENT_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_INSTRUMENT_ID + " = ?";
        // selection argument
        String[] selectionArgs = {Integer.toString(instrumentId)};
        // query user table with condition

        Cursor cursor = db.query(TABLE_INSTRUMENTS, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public boolean checkSystem(int systemId) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_SYSTEM_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_SYSTEM_ID + " = ?";
        // selection argument
        String[] selectionArgs = {Integer.toString(systemId)};
        // query user table with condition

        Cursor cursor = db.query(TABLE_SYSTEMS, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public boolean checkPlant(int plantId) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_PLANT_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_PLANT_ID + " = ?";
        // selection argument
        String[] selectionArgs = {Integer.toString(plantId)};
        // query user table with condition

        Cursor cursor = db.query(TABLE_PLANTS, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public String checkShift(String shift_name, String shift_date, String shift_reading_type) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_SHIFT_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_SHIFT_NAME + " = ?" + " AND " + COLUMN_SHIFT_DATE + " = ?" + " AND "
                + COLUMN_SHIFT_READING_TYPE + " = ?";
        // selection argument
        String[] selectionArgs = {shift_name, shift_date, shift_reading_type};
        // query user table with condition

        Cursor cursor = db.query(TABLE_SHIFT, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        String shift_id;
        if (cursorCount > 0 && cursor.moveToFirst()) {
           shift_id =  cursor.getString(cursor.getColumnIndex(COLUMN_SHIFT_ID));
        } else {
            shift_id =  "new shift";
        }
        cursor.close();
        db.close();
        return shift_id;
    }



    public boolean checkUser(String email, String password) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_NAME + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        // selection arguments
        String[] selectionArgs = {email, password};
        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        Cursor cursor = db.query(TABLE_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public Instrument getInstrumentFromBarcode(String barcode_id) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_INSTRUMENT_ID,
                COLUMN_INSTRUMENT_NAME,
                COLUMN_INSTRUMENT_KKSCODE,
                COLUMN_INSTRUMENT_BARCODE_ID,
                COLUMN_INSTRUMENT_UNIT,
                COLUMN_INSTRUMENT_LOWER_LIMIT,
                COLUMN_INSTRUMENT_UPPER_LIMIT,
                COLUMN_INSTRUMENT_SYSTEM_ID,
                COLUMN_INSTRUMENT_IS_ACTIVE,
        };
        // sorting orders
        String sortOrder =
                COLUMN_INSTRUMENT_ID + " ASC";

        String selection = COLUMN_INSTRUMENT_BARCODE_ID + " = ?";

        String[] selectionArgs = {barcode_id};

//        List<Instrument> instrumentList = new ArrayList<Instrument>();
        Instrument instrument = new Instrument();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_INSTRUMENTS, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
//                Instrument instrument = new Instrument();
                instrument.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_ID))));
                instrument.setName(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_NAME)));
                instrument.setKksCode(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_KKSCODE)));
                instrument.setBarcodeId(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_BARCODE_ID)));
                instrument.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_UNIT)));
                instrument.setLowerLimit(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_LOWER_LIMIT))));
                instrument.setUpperLimit(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_UPPER_LIMIT))));
                instrument.setSystemId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_SYSTEM_ID))));
                instrument.setIsActive(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_IS_ACTIVE))));

                // Adding user record to list
//                instrumentList.add(instrument);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return instrument;
    }

    public System getSystemFromInstrument(Instrument instrument) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_SYSTEM_ID,
                COLUMN_SYSTEM_NAME,
                COLUMN_SYSTEM_LOGSHEET,
                COLUMN_SYSTEM_IS_ACTIVE,
                COLUMN_SYSTEM_PLANT_ID};
        // sorting orders
        String sortOrder =
                COLUMN_SYSTEM_ID + " ASC";

        String selection = COLUMN_SYSTEM_ID + " = ?";

        String[] selectionArgs = {Integer.toString(instrument.getSystemId())};

        System system = new System();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_SYSTEMS, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                system.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_SYSTEM_ID))));
                system.setName(cursor.getString(cursor.getColumnIndex(COLUMN_SYSTEM_NAME)));
                system.setIsActive(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_SYSTEM_IS_ACTIVE))));
                system.setLogSheet(cursor.getString(cursor.getColumnIndex(COLUMN_SYSTEM_LOGSHEET)));
                system.setPlantId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_SYSTEM_PLANT_ID))));

                // Adding user record to list
//                systemList.add(system);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return system;
    }

    public Plant getPlantFromSystem(System system) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_PLANT_ID,
                COLUMN_PLANT_NAME,
                COLUMN_PLANT_READING_TIME_ID,
                COLUMN_PLANT_IS_ACTIVE};
        // sorting orders
        String sortOrder =
                COLUMN_PLANT_ID + " ASC";

        String selection = COLUMN_PLANT_ID + " = ?";

        String[] selectionArgs = {Integer.toString(system.getPlantId())};

        Plant plant = new Plant();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_PLANTS, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                plant.setPlant_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_ID))));
                plant.setPlant_name(cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_NAME)));
                plant.setIsActive(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_IS_ACTIVE))));
                plant.setReadingTimeId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_PLANT_READING_TIME_ID))));

                // Adding user record to list
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return plant;
    }

    public List<Instrument> getListOfInstrumentsFromBarcode(String barcode_id) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_INSTRUMENT_ID,
                COLUMN_INSTRUMENT_NAME,
                COLUMN_INSTRUMENT_KKSCODE,
                COLUMN_INSTRUMENT_BARCODE_ID,
                COLUMN_INSTRUMENT_UNIT,
                COLUMN_INSTRUMENT_LOWER_LIMIT,
                COLUMN_INSTRUMENT_UPPER_LIMIT,
                COLUMN_INSTRUMENT_SYSTEM_ID,
                COLUMN_INSTRUMENT_IS_ACTIVE,
        };
        // sorting orders
        String sortOrder =
                COLUMN_INSTRUMENT_ID + " ASC";

        String selection = COLUMN_INSTRUMENT_BARCODE_ID + " = ?";

        String[] selectionArgs = {barcode_id};

        List<Instrument> instrumentList = new ArrayList<Instrument>();

        SQLiteDatabase db = this.getReadableDatabase();

        // query the user table
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
        Cursor cursor = db.query(TABLE_INSTRUMENTS, //Table to query
                columns,    //columns to return
                selection,        //columns for the WHERE clause
                selectionArgs,        //The values for the WHERE clause
                null,       //group the rows
                null,       //filter by row groups
                sortOrder); //The sort order


        // Traversing through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Instrument instrument = new Instrument();
                instrument.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_ID))));
                instrument.setName(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_NAME)));
                instrument.setKksCode(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_KKSCODE)));
                instrument.setBarcodeId(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_BARCODE_ID)));
                instrument.setUnit(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_UNIT)));
                instrument.setLowerLimit(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_LOWER_LIMIT))));
                instrument.setUpperLimit(Double.parseDouble(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_UPPER_LIMIT))));
                instrument.setSystemId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_SYSTEM_ID))));
                instrument.setIsActive(Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUMENT_IS_ACTIVE))));

                // Adding user record to list
                instrumentList.add(instrument);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return user list
        return instrumentList;
    }



}
