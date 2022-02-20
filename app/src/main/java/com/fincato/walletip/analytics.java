package com.fincato.walletip;

import static com.fincato.walletip.MainActivity.SHORTMONTHNAME;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class analytics extends AppCompatActivity {
    //TEXTVIEW
    TextView tv_Month_top;
    TextView tv_money_month;
    TextView tv_avg_percentage;
    TextView tv_money_identifier;

    //BUTTON
    Button btn_tot_month;
    Button btn_avg_day;
    Button btn_All;
    Button btn_12;

    //DATABASE
    public DB_Records myDB;

    //BARCHART
    public BarChart bar_Chart;

    //Month Selector
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    public String[] crop_monthName;
    String today_month_number;
    int index_month_selected;

    //DATE
    public Calendar calendar = Calendar.getInstance();
    public Date date = calendar.getTime();
    public long millisecondsDate = date.getTime();

    //COLOR
    public int[] color={R.color.gray,R.color.gray,R.color.gray,R.color.gray,R.color.gray,
            R.color.gray,R.color.gray,R.color.gray,R.color.gray,R.color.gray,R.color.gray,R.color.gray};

    //checked
    boolean ck_tot_month;
    boolean ck_avg_day;
    boolean ck_all;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        tv_money_month = findViewById(R.id.tv_money_month2);
        tv_Month_top = findViewById(R.id.tv_Month_top);
        tv_avg_percentage=findViewById(R.id.tv_avg_percentage);
        tv_money_identifier=findViewById(R.id.tv_money_identifier);

        btn_avg_day = (Button) findViewById(R.id.btn_avg_day);
        btn_12 = (Button) findViewById(R.id.btn_12);
        btn_All = (Button) findViewById(R.id.btn_All);
        btn_tot_month = (Button) findViewById(R.id.btn_tot_month);

        //DATABASE
        myDB = new DB_Records(this);

        bar_Chart = findViewById(R.id.graph);

        today_month_number = ((String) new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(millisecondsDate)).substring(3, 5);
        crop_monthName=new String[Integer.parseInt(today_month_number)];
        System.arraycopy(MainActivity.MONTHNAME,0,crop_monthName,0,crop_monthName.length);

        //BTN 12 month
        btn_12.setTextColor(getColor(R.color.paint));
        btn_All.setTextColor(getColor(R.color.white));
        ck_all=false;

        changeMonthTOT(Integer.parseInt(today_month_number)-1);
        //start today month
        index_month_selected = Integer.parseInt(today_month_number)-1;

        setCk_tot_month(true);

        ck_tot_month=true;
        ck_avg_day=false;

    }

    //  BTN LISTVIEW
    public void BtnView(View view) {
        Intent intent = new Intent(analytics.this, ViewListContents.class);
        startActivity(intent);
    }

    public void selectMonth(View view) {
        final Calendar today = Calendar.getInstance();

        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(analytics.this,
                R.style.AlertDialog);

        builder.setTitle("Select month:");

        builder.setItems(crop_monthName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                index_month_selected=which;
                if(isCk_tot_month())
                    changeMonthTOT(which);
                else if (isCk_avg_day())
                    changeMonthAVG(which);

            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

    public void changeMonthTOT(int index){
        //TEXTVIEW MONTH NAME
        tv_Month_top.setText(MainActivity.MONTHNAME[index]);

        //GRAPH TOT MONTH
        String[] parts;
        parts = myDB.getAllMonthToT().split(";");


        if(parts[0].compareTo("")==0)
            return ;

        //_______________________
        List<String> month_list = myDB.getMonthList();

        if(!ck_all){//only 12
            int dim = month_list.size()-12;
            month_list=month_list.subList(dim,month_list.size());
        }
        Log.d("analytics","analytics######## month List:"+month_list.toString());
        ArrayList<String> short_name=new ArrayList<String>();
        for(int i =0;i<month_list.size();i++){
            Log.d("analytics","######## i:"+i+" get(i):"+month_list.get(i).substring(5));
            short_name.add( SHORTMONTHNAME[ Integer.parseInt(month_list.get(i).substring(5)) -1]);
            Log.d("analytics","-------- name:"+ SHORTMONTHNAME[ Integer.parseInt(month_list.get(i).substring(5)) -1]);
        }
        //_______________________
        //prova
        float tot_month_selected=Float.parseFloat(parts[parts.length-1]);
        ArrayList<BarEntry> ar_month = new ArrayList<>();
        ArrayList<String> SHORT =new ArrayList<String>();

        int count=0;
        int number_of_graph=0;

        if(ck_all){//all
            // parts = myDB.getAllMonthToT().split(";");
            for (int i = 0; i < parts.length; i++) {
                if(Float.parseFloat(parts[i])>0) {
                    ar_month.add(new BarEntry(count, Float.parseFloat(parts[i])));
                    SHORT.add(short_name.get(i));
                    count++;
                    number_of_graph = count;
                }
            }
        }else{//only 12
            //parts = myDB.getLast12MonthToT().split(";");
            int k=short_name.size()-1;
            for (int i = parts.length-13; i < parts.length; i++)  {
                if(Float.parseFloat(parts[i])>0) {
                    ar_month.add(new BarEntry(count, Float.parseFloat(parts[i])));
                    SHORT.add(short_name.get(k));
                    k--;
                    count++;
                    number_of_graph = count;
                }
            }
        }


        BarDataSet bar_data_set = new BarDataSet(ar_month, "€ Tot");

        int[] newcolor = new int[color.length];
        newcolor=color.clone();

        //AVG
        float sum=0;
        count=0;
        for (int i = 0; i < parts.length; i++) {
            if(Float.parseFloat(parts[i])>0){
                sum+=Float.parseFloat(parts[i]);
                count++;
            }
        }
        float AVG = sum/count;

        //SET COLOR OF MONTH
        if(tot_month_selected>0){
            if(tot_month_selected>=AVG){
                newcolor[number_of_graph-1]=R.color.paint;
            }else{
                newcolor[number_of_graph-1]=R.color.red;
            }
        }

        bar_data_set.setColors(newcolor, analytics.this);
        bar_data_set.setValueTextColor(Color.WHITE);
        bar_data_set.setValueTextSize(16f);
        bar_data_set.setBarBorderColor(getColor(R.color.paint));


        XAxis x = bar_Chart.getXAxis();
        x.setValueFormatter(new IndexAxisValueFormatter(SHORT));
        x.setGranularity(1);
        x.setTextColor(getColor(R.color.paint));

        bar_Chart.getAxisLeft().setTextColor(getColor(R.color.paint));
        bar_Chart.getAxisRight().setTextColor(getColor(R.color.paint));


        BarData bar_Data = new BarData(bar_data_set);
        bar_Chart.getDescription().setText("month");
        bar_Chart.setFitBars(true);
        bar_Chart.setBorderColor(Color.WHITE);
        bar_Chart.setData(bar_Data);
        bar_Chart.animateY(1500);

        //TEXTVIEW TOT MONTH SELECTED
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        tv_money_month.startAnimation(animation);
        tv_money_month.setText(tot_month_selected + " €");

        //TEXTVIEW MONEY IDENTIFIER
        tv_money_identifier.setText("Tot month:");

        //TEXTVIEW AVG PERCENT
        float difference=tot_month_selected-AVG;
        double percent=Math.round(((100*difference)/AVG)*10.0)/10.0;
        if(percent>=0){
            tv_avg_percentage.setText("+"+percent+"%");
            tv_avg_percentage.setTextColor(getColor(R.color.lime));
        }
        else{
            tv_avg_percentage.setText(percent+"%");
            tv_avg_percentage.setTextColor(getColor(R.color.red));
        }


    }



    public void changeMonthAVG(int index){
        //TEXTVIEW MONTH NAME

        tv_Month_top.setText(MainActivity.MONTHNAME[index]);

        //GRAPH TOT MONTH
        String[] parts;
        if(ck_all){//all
            parts = myDB.getAllMonthAVG().split(";");
        }else{//only 12
            parts = myDB.getLast12MonthAVG().split(";");
        }
        if(parts[0].compareTo("")==0)
            return ;

        float avg_month_selected=Float.parseFloat(parts[index]);
        ArrayList<BarEntry> ar_month = new ArrayList<>();

        int count=0;
        int number_of_graph=0;
        for (int i = 0; i < parts.length; i++) {
            if(Float.parseFloat(parts[i])>0){
                ar_month.add(new BarEntry(i, Float.parseFloat(parts[i])));
                count++;
                if(i==index)
                    number_of_graph=count;

            }

        }

        Log.d("analytics","analytics######## number_of_graph:"+number_of_graph);

        BarDataSet bar_data_set = new BarDataSet(ar_month, "AVG €/D");

        int[] newcolor = new int[color.length];
        newcolor=color.clone();

        //AVG
        float sum=0;
        count=0;
        for (int i = 0; i < parts.length; i++) {
            if(Float.parseFloat(parts[i])>0){
                sum+=Float.parseFloat(parts[i]);
                count++;
            }
        }
        float AVG = sum/count;

        //SET COLOR OF MONTH
        if(avg_month_selected>0){
            if(avg_month_selected>=AVG){
                newcolor[number_of_graph-1]=R.color.paint;
            }else{
                newcolor[number_of_graph-1]=R.color.red;
            }
        }

        bar_data_set.setColors(newcolor, analytics.this);
        bar_data_set.setValueTextColor(Color.WHITE);
        bar_data_set.setValueTextSize(16f);

        BarData bar_Data = new BarData(bar_data_set);
        bar_Chart.setFitBars(true);
        bar_Chart.setBorderColor(Color.WHITE);
        bar_Chart.setData(bar_Data);
        bar_Chart.animateY(2000);

        //TEXTVIEW AVG MONTH SELECTED
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        tv_money_month.startAnimation(animation);
        tv_money_month.setText(Math.round( Float.parseFloat(parts[index])*10.0 )/10.0 + " €");

        //TEXTVIEW MONEY IDENTIFIER
        tv_money_identifier.setText("AVG €/day:");

        //TEXTVIEW AVG PERCENT
        float difference=avg_month_selected-AVG;
        double percent=Math.round(((100*difference)/AVG)*10.0)/10.0;

        if(percent>=0){
            tv_avg_percentage.setText("+"+percent+"%");
            tv_avg_percentage.setTextColor(getColor(R.color.lime));
        }
        else{
            tv_avg_percentage.setText(percent+"%");
            tv_avg_percentage.setTextColor(getColor(R.color.red));
        }


    }

    public boolean isCk_tot_month() {
        return ck_tot_month;
    }

    public void setCk_tot_month(boolean bool) {
        this.ck_tot_month = bool;
        if(bool){
            btn_tot_month.setTextColor(getColor(R.color.paint));
        }else{
            btn_tot_month.setTextColor(getColor(R.color.white));
        }
    }

    public boolean isCk_avg_day() {
        return ck_avg_day;
    }

    public void setCk_avg_day(boolean bool) {
        this.ck_avg_day = bool;
        if(bool){
            btn_avg_day.setTextColor(getColor(R.color.paint));
            //btn_avg_day.setBackground(getDrawable(R.drawable.toproudstyle));
        }else{
            btn_avg_day.setTextColor(getColor(R.color.white));
            //btn_avg_day.setBackground(getDrawable(R.drawable.roudstylenotchecked));
        }
    }

    public void graph_tot_month(View view){
        if(!isCk_tot_month()){
            changeMonthTOT(index_month_selected);
            setCk_tot_month(true);
            setCk_avg_day(false);
        }
    }

    public void graph_avg_month(View view){
        if(!isCk_avg_day()){
            changeMonthAVG(index_month_selected);
            setCk_avg_day(true);
            setCk_tot_month(false);
        }
    }

    public void set12Graph(View view){
        btn_12.setTextColor(getColor(R.color.paint));
        btn_All.setTextColor(getColor(R.color.white));
        ck_all=false;
        if(isCk_tot_month()){
            changeMonthTOT(index_month_selected);
        }else{
            changeMonthAVG(index_month_selected);
        }
    }

    public void setAllGraph(View view){
        btn_All.setTextColor(getColor(R.color.paint));
        btn_12.setTextColor(getColor(R.color.white));
        ck_all=true;
        if(isCk_tot_month()){
            changeMonthTOT(index_month_selected);
        }else{
            changeMonthAVG(index_month_selected);
        }
    }

}
