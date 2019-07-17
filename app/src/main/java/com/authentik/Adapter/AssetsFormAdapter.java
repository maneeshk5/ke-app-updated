package com.authentik.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.authentik.ke.AssetsForm;
import com.authentik.ke.R;
import com.authentik.model.AssetsQues;


import java.util.List;

public class AssetsFormAdapter extends RecyclerView.Adapter<AssetsFormAdapter.MyViewHolder>{

    List<AssetsQues> questionList;
    Context context;
    AssetsForm af;


    public AssetsFormAdapter(List<AssetsQues> ques, Context context) {
        this.questionList = ques;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView question, unit, plant, paging;
        public EditText reading_et;
        ImageView img_view;
        Button capture_btn;

        public MyViewHolder(View view) {
            super(view);
            af = (AssetsForm)context;
            question = (TextView) view.findViewById(R.id.ques);
            unit = (TextView) view.findViewById(R.id.unit);
            plant = (TextView) view.findViewById(R.id.plant);
            paging = (TextView) view.findViewById(R.id.paging);
            reading_et = (EditText) view.findViewById(R.id.reading_et);
//            img_view = (ImageView) view.findViewById(R.id.img_view);
            capture_btn = (Button) view.findViewById(R.id.capture_btn);

            reading_et.setShowSoftInputOnFocus(false);
            capture_btn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            af.captureBtnListener();
        }
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int i) {
        AssetsQues ques = questionList.get(i);
        holder.question.setText(ques.getQuestion_text());
        holder.unit.setText(ques.getUnit());
        holder.plant.setText(ques.getPlant());
        int curCount = i+1;
        holder.paging.setText("Page "+ curCount +"/"+getItemCount());
        holder.reading_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                af.readings.put(i,s.toString());
            }
        });
    }


    @Override
    public int getItemCount() {
        return questionList.size();
    }



    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
