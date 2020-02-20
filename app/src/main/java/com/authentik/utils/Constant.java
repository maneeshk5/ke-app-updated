package com.authentik.utils;

import android.os.Environment;

public class Constant {
//    public static final String BASE_URL = "http://192.168.0.153:8700/";
    public static final String BASE_URL = "https://ke.authentiksolution.com/api/";
    public static final String DATABASE_NAME = "ke_DB";
    public static final String USER_TABLE = "user";
    public static final String ASSETS_QUES_TABLE = "assets_ques";
    public static final String COLUMN_USERID = "id";
    public static final String COLUMN_USERNAME = "name";
    public static final String COLUMN_TOKEN = "token";
    public static final String COLUMN_QUES_ID = "id";
    public static final String COLUMN_ASSET_CODE = "code";
    public static final String COLUMN_BARCODE_ID = "barcodeId";
    public static final String COLUMN_QUES_TEXT = "question";
    public static final String COLUMN_QUES_UNIT = "unit";
    public static final String COLUMN_QUES_UPPER_LIMIT= "uLimit";
    public static final String COLUMN_QUES_LOWER_LIMIT= "lLimit";
    public static final String COLUMN_QUES_PLANT = "plant";
    public static final String COLUMN_QUES_SYSTEM_ID = "systemId";
    public static final String COLUMN_QUES_SYSTM_NAME = "systemName";
    public static final String IMAGE_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/KE";
    public static final String ANSWER_TABLE = "answer";
    public static final String COLUMN_ANSWERID = "id";
    public static final String COLUMN_READING = "reading";
    public static final String COLUMN_IMGPATH = "image_path";
    public static final String COLUMN_ANS_QUESID = "ques_id";
    public static final String COLUMN_ANS_USERID = "user_id";
    public static final String COLUMN_RECORDED_TIME = "recorded_time";
    public static final String COLUMN_SENT_STATUS = "status";
}
