package com.authentik.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.authentik.model.Answer;
import com.authentik.model.AssetsQues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static DatabaseHandler DbHandler = null;

    private DatabaseHandler(Context context) {
        super(context, Constant.DATABASE_NAME , null, 1);
    }

    public static DatabaseHandler getDbHandlerInstance(Context context) {
        if(DbHandler == null) {
            return new DatabaseHandler(context);
        }
        else {
            return DbHandler;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + Constant.USER_TABLE + "("
                + Constant.COLUMN_USERID + " INTEGER PRIMARY KEY," + Constant.COLUMN_USERNAME + " TEXT,"
                + Constant.COLUMN_TOKEN + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_QUES_TABLE = "CREATE TABLE " + Constant.ASSETS_QUES_TABLE + "("
                + Constant.COLUMN_QUES_ID + " INTEGER PRIMARY KEY," + Constant.COLUMN_ASSET_CODE + " TEXT,"
                + Constant.COLUMN_BARCODE_ID+ " TEXT," + Constant.COLUMN_QUES_TEXT + " TEXT," + Constant.COLUMN_QUES_UNIT + " TEXT,"
                + Constant.COLUMN_QUES_UPPER_LIMIT+ " TEXT," + Constant.COLUMN_QUES_LOWER_LIMIT+ " TEXT,"+ Constant.COLUMN_QUES_PLANT + " TEXT,"
                + Constant.COLUMN_QUES_SYSTEM_ID+ " INTEGER,"  + Constant.COLUMN_QUES_SYSTM_NAME+ " TEXT"  + ")";
        db.execSQL(CREATE_QUES_TABLE);

        String CREATE_ANS_TABLE = "CREATE TABLE " + Constant.ANSWER_TABLE + "("
                + Constant.COLUMN_ANSWERID + " INTEGER PRIMARY KEY," + Constant.COLUMN_READING + " TEXT,"
                + Constant.COLUMN_IMGPATH + " TEXT," + Constant.COLUMN_ANS_QUESID + " INTEGER," + Constant.COLUMN_ANS_USERID + " INTEGER,"
                + Constant.COLUMN_SENT_STATUS + " INTEGER DEFAULT 0," + Constant.COLUMN_RECORDED_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
        db.execSQL(CREATE_ANS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Constant.USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Constant.ASSETS_QUES_TABLE);

        // Create tables again
        onCreate(db);
    }

    public void addUser(int userId, String name, String token) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.COLUMN_USERID, userId);
        values.put(Constant.COLUMN_USERNAME, name);
        values.put(Constant.COLUMN_TOKEN, token);

        // Inserting Row
        db.insert(Constant.USER_TABLE, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public boolean isUser() {

        String selectQuery = "SELECT  * FROM " + Constant.USER_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.getCount() > 0) {
            db.close();
            return true;
        }

        db.close();
        return false;
    }

    public void deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from "+ Constant.USER_TABLE);

        db.close();
    }

    public HashMap<String,String> getUser() {
        HashMap<String, String> data = new HashMap<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + Constant.USER_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                data.put("id" , String.valueOf(cursor.getInt(0)));
                data.put("name", cursor.getString(1));
                data.put("token", cursor.getString(2));

            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return data;
    }

    public List<AssetsQues> getAssetQues(String assetCode){
        List<AssetsQues> quesList = new ArrayList<AssetsQues>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Constant.ASSETS_QUES_TABLE + " WHERE " + Constant.COLUMN_BARCODE_ID + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[] {assetCode});

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AssetsQues ques = new AssetsQues();
                ques.setId(Integer.parseInt(cursor.getString(0)));
                ques.setCode(cursor.getString(1));
                ques.setBarcodeId(cursor.getString(2));
                ques.setQuestion_text(cursor.getString(3));
                ques.setUnit(cursor.getString(4));
                ques.setUpperLimit(cursor.getString(5));
                ques.setLowerLimit(cursor.getString(6));
                ques.setPlant(cursor.getString(7));
                ques.setSystemId(Integer.parseInt(cursor.getString(8)));
                ques.setSystemName(cursor.getString(9));
                quesList.add(ques);
            } while (cursor.moveToNext());
        }
        db.close();
        return quesList;
    }

    public List<AssetsQues> getAllAssetQues(){
        List<AssetsQues> quesList = new ArrayList<AssetsQues>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Constant.ASSETS_QUES_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AssetsQues ques = new AssetsQues();
                ques.setId(Integer.parseInt(cursor.getString(0)));
                ques.setCode(cursor.getString(1));
                ques.setQuestion_text(cursor.getString(2));
                ques.setUnit(cursor.getString(3));

                quesList.add(ques);
            } while (cursor.moveToNext());
        }
        db.close();
        return quesList;
    }

    public List<Answer> getAllAnswerNotSent(boolean isUpdate){
        List<Answer> ansList = new ArrayList<Answer>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Constant.ANSWER_TABLE + " WHERE " + Constant.COLUMN_SENT_STATUS + " = 0";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                if (isUpdate) {
                    updateAnswerStatus(1,0,Integer.parseInt(cursor.getString(0)));
                }

                Answer ans = new Answer();
                ans.setId(Integer.parseInt(cursor.getString(0)));
                ans.setReading(cursor.getString(1));
                ans.setImage_fileName(cursor.getString(2));
                ans.setQues_id(Integer.parseInt(cursor.getString(3)));
                ans.setUser_id(Integer.parseInt(cursor.getString(4)));
                ans.setStatus(Integer.parseInt(cursor.getString(5)));
                ans.setRecorded_time(cursor.getString(6));
                ansList.add(ans);
            } while (cursor.moveToNext());
        }
        db.close();
        return ansList;
    }

    public List<Answer> getAllAnswer(){
        List<Answer> ansList = new ArrayList<Answer>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + Constant.ANSWER_TABLE ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Answer ans = new Answer();
                ans.setId(Integer.parseInt(cursor.getString(0)));
                ans.setReading(cursor.getString(1));
                ans.setImage_fileName(cursor.getString(2));
                ans.setQues_id(Integer.parseInt(cursor.getString(3)));
                ans.setUser_id(Integer.parseInt(cursor.getString(4)));
                ans.setStatus(Integer.parseInt(cursor.getString(5)));
                ans.setRecorded_time(cursor.getString(6));
                ansList.add(ans);
            } while (cursor.moveToNext());
        }
        db.close();
        return ansList;
    }


    public void updateAnswerStatus(int status, int flag, int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(Constant.COLUMN_SENT_STATUS, status);

        db.update(Constant.ANSWER_TABLE, cv, Constant.COLUMN_SENT_STATUS + " = " +flag + " AND " + Constant.COLUMN_ANSWERID + " = " + id, null);
        db.close();
    }

    public void updateAnswerStatus(int status, int flag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(Constant.COLUMN_SENT_STATUS, status);

        db.update(Constant.ANSWER_TABLE, cv, Constant.COLUMN_SENT_STATUS + " = " +flag, null);
        db.close();
    }

    public void addAssetsQues(List<AssetsQues> ques) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (AssetsQues asset_q : ques) {
            ContentValues values = new ContentValues();
            values.put(Constant.COLUMN_QUES_ID, asset_q.getId());
            values.put(Constant.COLUMN_ASSET_CODE, asset_q.getCode());
            values.put(Constant.COLUMN_BARCODE_ID, asset_q.getBarcodeId());
            values.put(Constant.COLUMN_QUES_TEXT, asset_q.getQuestion_text());
            values.put(Constant.COLUMN_QUES_UNIT, asset_q.getUnit());
            values.put(Constant.COLUMN_QUES_UPPER_LIMIT, asset_q.getUpperLimit());
            values.put(Constant.COLUMN_QUES_LOWER_LIMIT, asset_q.getLowerLimit());
            values.put(Constant.COLUMN_QUES_PLANT, asset_q.getPlant());
            values.put(Constant.COLUMN_QUES_SYSTEM_ID, asset_q.getSystemId());
            values.put(Constant.COLUMN_QUES_SYSTM_NAME, asset_q.getSystemName());

            db.replace(Constant.ASSETS_QUES_TABLE, null, values);
        }
        db.close(); // Closing database connection
    }

    public void addAnswer(List<Answer> ans) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (Answer asset_q : ans) {
            ContentValues values = new ContentValues();
            values.put(Constant.COLUMN_READING, asset_q.getReading());
            values.put(Constant.COLUMN_IMGPATH, asset_q.getImage_fileName());
            values.put(Constant.COLUMN_ANS_QUESID, asset_q.getQues_id());
            values.put(Constant.COLUMN_ANS_USERID, asset_q.getUser_id());

            db.insert(Constant.ANSWER_TABLE, null, values);
        }
        db.close(); // Closing database connection
    }

    public void deleteSentAnswerRecord(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from "+ Constant.ANSWER_TABLE + " Where "+ Constant.COLUMN_ANSWERID + " = " + id);

        db.close();
    }
}
