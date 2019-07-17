package com.authentik.ke;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;

import com.authentik.Adapter.AssetsFormAdapter;
import com.authentik.model.AssetsQues;
import com.authentik.utils.DatabaseHandler;

import java.util.List;

public class Test extends Activity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    DatabaseHandler dbHandler;
    List<AssetsQues> ques;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_v);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        dbHandler = DatabaseHandler.getDbHandlerInstance(getApplicationContext());
        Intent intent = getIntent();
        ques = dbHandler.getAssetQues(intent.getStringExtra("barcodeStr"));

        mAdapter = new AssetsFormAdapter(ques, this);
        recyclerView.setAdapter(mAdapter);
    }
}
