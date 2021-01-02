package com.authentik.ke;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.authentik.model.Instrument;
import com.authentik.model.Reading;
import com.authentik.utils.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Tag_information extends AppCompatActivity {

    TextView currUser;
    TextView dateAndTime;
    TextView app_path;
    TextView tag_instrument;
    TextView tag_kksCode;
    TextView tag_serialNo;
    TextView tag_unit;
    TextView tag_range;
    SharedPreferences sharedPreferences;
    EditText reading_value_et;
    Button submit_tag_btn;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_information);

        db = new DatabaseHelper(getApplicationContext());

        currUser = findViewById(R.id.username_tv);
        dateAndTime = findViewById(R.id.date_time_tv);
        app_path = findViewById(R.id.app_path_tv);

        tag_instrument = findViewById(R.id.tag_inst);
        tag_kksCode = findViewById(R.id.tag_kkscode);
        tag_serialNo = findViewById(R.id.tag_serial);
        tag_unit = findViewById(R.id.tag_unit);
        tag_range = findViewById(R.id.tag_range);

        reading_value_et = findViewById(R.id.reading_value_et);
        submit_tag_btn = findViewById(R.id.submit_tag_btn);

        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        String value = sharedPreferences.getString("Username", "no name");
        currUser.setText("User: " + value);

        Date dNow = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String datetime = ft.format(dNow);
        dateAndTime.setText(datetime);

        final Instrument instrument = (Instrument) getIntent().getSerializableExtra("instrument_object");
        app_path.setText( instrument.getName() + " > " + "Tag");

        //set tag details
        tag_instrument.append(instrument.getName());
        tag_kksCode.append(instrument.getKksCode());
        tag_unit.append(instrument.getUnit());
        tag_range.append("Min (" + instrument.getLowerLimit() + ") " + "Max (" + instrument.getUpperLimit() + ")");

        submit_tag_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reading_value_et.getText().length() == 0) {
                    Toast.makeText(Tag_information.this,"Please Enter a value",Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Tag_information.this);
                    builder.setTitle("Confirmation!");
                    builder.setMessage("Your input reading is: " + reading_value_et.getText().toString());

                    builder.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder pic_dilogue_builder = new AlertDialog.Builder(Tag_information.this);
                            pic_dilogue_builder.setView(null).setMessage(null);
                            pic_dilogue_builder.setTitle("Confirmation");
                            pic_dilogue_builder.setMessage("Do you want to take a picture?");

                            pic_dilogue_builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(Tag_information.this,"Data Saved Successfully",Toast.LENGTH_LONG).show();

                                    //Insert reading data locally
                                    Date dNow = new Date();
                                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                                    String reading_id = ft.format(dNow);

                                    SimpleDateFormat ft2 = new SimpleDateFormat("dd-MM-yyyy");
                                    String reading_date_time = ft2.format(dNow);

                                    SimpleDateFormat ft3 = new SimpleDateFormat("HH:mm:ss");
                                    String reading_time = ft3.format(dNow);

                                    Reading reading = new Reading();
                                    reading.setId(reading_id);
                                    reading.setDate_time(reading_date_time);
                                    reading.setTime(reading_time);
                                    reading.setInstrument_id(instrument.getId());
                                    reading.setShift_id(sharedPreferences.getString("shift_id","-"));
                                    reading.setReading_value(reading_value_et.getText().toString());

                                    db.addReading(reading);

                                    Intent intent = new Intent(Tag_information.this,Plant_List.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                }
                            });

                            pic_dilogue_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //Insert reading data locally
                                    Date dNow = new Date();
                                    SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                                    String reading_id = ft.format(dNow);

                                    SimpleDateFormat ft2 = new SimpleDateFormat("dd-MM-yyyy");
                                    String reading_date_time = ft2.format(dNow);

                                    SimpleDateFormat ft3 = new SimpleDateFormat("HH:mm:ss");
                                    String reading_time = ft3.format(dNow);
                                    Reading reading = new Reading();
                                    reading.setId(reading_id);
                                    reading.setDate_time(reading_date_time);
                                    reading.setTime(reading_time);
                                    reading.setInstrument_id(instrument.getId());
                                    reading.setShift_id(sharedPreferences.getString("shift_id","-"));
                                    reading.setReading_value(reading_value_et.getText().toString());

                                    db.addReading(reading);

                                    Toast.makeText(Tag_information.this,"Open camera page",Toast.LENGTH_LONG).show();
                                }
                            });
                            pic_dilogue_builder.create().show();
                        }
                    });
                    builder.create().show();
                }
            }
        });
    }

    public void goBack(View view) {
        finish();
    }
}