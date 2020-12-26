package com.authentik.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.authentik.model.Plant;
import com.authentik.model.User;
import com.authentik.model.System;
import java.util.ArrayList;
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
            + COLUMN_SYSTEM_IS_ACTIVE + " INTEGER," + COLUMN_SYSTEM_LOGSHEET + " TEXT," + COLUMN_SYSTEM_PLANT_ID + " INTEGER"+")";

    // drop table sql query
    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
    private String DROP_PLANT_TABLE = "DROP TABLE IF EXISTS " + TABLE_PLANTS;
    private String DROP_SYSTEM_TABLE = "DROP TABLE IF EXISTS " + TABLE_SYSTEMS;

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
//        db.close();
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Drop User Table if exist
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_PLANT_TABLE);
        db.execSQL(DROP_SYSTEM_TABLE);

        // Create tables again
        onCreate(db);

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



    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return list
     */

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
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id,user_name,user_email,user_password FROM user ORDER BY user_name;
         */
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

    public List<Plant> getAllPlants() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_PLANT_ID,
                COLUMN_PLANT_NAME,
                COLUMN_PLANT_READING_TIME_ID,
                COLUMN_PLANT_IS_ACTIVE};
        // sorting orders
        String sortOrder =
                COLUMN_PLANT_NAME + " ASC";
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

    public List<System> getAllSystems() {
        // array of columns to fetch
        String[] columns = {
                COLUMN_SYSTEM_ID,
                COLUMN_SYSTEM_NAME,
                COLUMN_SYSTEM_LOGSHEET,
                COLUMN_SYSTEM_IS_ACTIVE,
                COLUMN_SYSTEM_PLANT_ID};
        // sorting orders
        String sortOrder =
                COLUMN_SYSTEM_NAME + " ASC";

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
                null,        //columns for the WHERE clause
                null,        //The values for the WHERE clause
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

    public void updateSystem(System system) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SYSTEM_IS_ACTIVE, system.getIsActive());

        // updating row
        db.update(TABLE_SYSTEMS, values, COLUMN_SYSTEM_ID + " = ?",
                new String[]{String.valueOf(system.getId())});
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

    public boolean checkUser(String email) {
        // array of columns to fetch
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = COLUMN_USER_NAME + " = ?";
        // selection argument
        String[] selectionArgs = {email};
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

    //
}
