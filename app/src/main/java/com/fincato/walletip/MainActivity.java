package com.fincato.walletip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //ITEMS
    private TextView tv_date;
    private TextView tv_Month_top;
    private TextView tv_moneymonth;
    private RadioGroup rbg_Car;
    private RadioButton rb_Yes;
    private RadioButton rb_No;
    private Spinner sp_Nhours;
    private int current_Sp_index;
    private int Array_Hours_size=18;
    private EditText etn_Ntip;
    private ConstraintLayout layout_car;

    //INSTANCE
    private static MainActivity instance;

    //TEMP
    public Button btnView;

    //EDITABLE VARIABLES
    public int HOURS_REWARD=6;
    public int CAR_REWARD=3;

    //DATABASE
    public DB_Records myDB;
    String record;

    //RECORD
    String record_add;

    public static String[] MONTHNAME={"January","February","March","April","May",
            "June","July","August","September","October","November","December"};
    public static String[] SHORTMONTHNAME={"Jan","Feb","Mar","Apr","May","Jun","Jul",
            "Aug","Sep","Oct","Nov","Dec"};
    String[] MONTHNUMBER={"01","02","03","04","05","06","07","08","09","10","11","12"};
    String text_data_select;



    public Calendar calendar;
    int year;
    int month;
    int day;
    int year_selected;
    int month_selected;
    int day_selected;

    public Date date;

    ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        date = calendar.getTime();
        year_selected = year;
        month_selected = month;
        day_selected = day;

        //INSTANCE
        instance = this;

        //  TEXT VIEW DATA
        tv_date = findViewById(R.id.tv_date2);

        //  SPINNER HOURS
        sp_Nhours= findViewById(R.id.sp_Nhours);
        adapter = ArrayAdapter.createFromResource(this, R.array.Hours, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_Nhours.setAdapter(adapter);
        //Index
        current_Sp_index=0;

        //  TEXTVIEW MONEY MONTH
        tv_moneymonth= findViewById(R.id.tv_money_month2);

        //  TEXTVIEW MONTH TOP
        tv_Month_top= findViewById(R.id.tv_Month_top);

        //  EDIT TEXT NUMBER (DECIMAL)
        etn_Ntip= findViewById(R.id.etn_Ntip);

        //  RADIO BUTTON YES
        rb_Yes = findViewById(R.id.rb_Yes);

        //  RADIO BUTTON NO
        rb_No = findViewById(R.id.rb_No);

        layout_car=findViewById(R.id.ly_Car);

        //DATABASE
        myDB= new DB_Records(this);

        //myDB.add

        //month number
        text_data_select = (String) new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date.getTime());
        Log.d("MyActivity","MA130########  ONCreate MAinAtivity");
        Log.d("MyActivity","MA130######## dat:"+text_data_select);

        //SET TEXT

        setDataText();
        setMoneyMonth();
        tv_Month_top.setText(MONTHNAME[month]);

        if(myDB.getCarSettings()){
            removeCarReward();
        }
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //  BTN SETTINGS
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            Intent intent = new Intent(MainActivity.this, ViewSettings.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectMonth(View view) {
        final Calendar today = Calendar.getInstance();
        datePickerDialog(year_selected,month_selected,day_selected);
    }

    public void datePickerDialog( int year_start, int month_start, int day_start){
        DatePickerDialog datePickerDialog=new DatePickerDialog(MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Calendar c = calendar.getInstance();
                        c.set(year, month, day);
                        year_selected=year;
                        month_selected=month;
                        day_selected=day;
                        if( !(DateUtils.isToday(c.getTimeInMillis()))&& c.compareTo(calendar)>0){
                            Toast.makeText(MainActivity.this, "Selected date is after today's date" ,Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(year<calendar.get(Calendar.YEAR)-5){
                            Toast.makeText(MainActivity.this, "Selected date is too old" ,Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String ck_day=day+"";
                        if(day<10)
                            ck_day="0"+day;

                        //CONTROLLARE SE CAMBIA MESE
                        if(text_data_select.substring(3,5).compareTo(MONTHNUMBER[month])!=0){//update money month
                            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                                    R.anim.fadein);
                            Log.d("MyActivity","tvMonthtop######## MONTHNAME["+month);
                            tv_Month_top.startAnimation(animation);
                            tv_Month_top.setText(MONTHNAME[month]);

                            text_data_select=ck_day+"/"+MONTHNUMBER[month]+"/"+year;
                            setMoneyMonth();
                        }


                        text_data_select=ck_day+"/"+MONTHNUMBER[month]+"/"+year;
                        Log.d("MyActivity","########  text_data_select"+ text_data_select);
                        setDataText();
                        set_input();
                    }
                },year_start,month_start,day_start);
        datePickerDialog.show();


    }
    //  BTN LISTVIEW
    public void BtnView(View view) {
        Intent intent = new Intent(MainActivity.this, ViewListContents.class);
        startActivity(intent);
    }

    //  BTN ANALYTICS
    public void GoToAnalytics(View view) {
        Intent intent = new Intent(MainActivity.this, analytics.class);
        startActivity(intent);
    }

    //  BTN PREVIOUS HOURS
    public void previusHours(View view){
       current_Sp_index =  sp_Nhours.getSelectedItemPosition();
        if(current_Sp_index>0)
            sp_Nhours.setSelection(current_Sp_index-=1);
    }

    //  BTN NEXT Hours
    public void nextHours(View view){
       current_Sp_index =  sp_Nhours.getSelectedItemPosition();
        if(current_Sp_index!= Array_Hours_size )
            sp_Nhours.setSelection(current_Sp_index+=1);
    }

    // ADD Record
    public void replaceOrAddRecord(View view){
        String s_nhours;
        double r_ntip=0;
        int r_tot_hours,r_carflag=0,n_hours,n_minutes;
        double r_total_day=0;

        s_nhours =(String) sp_Nhours.getSelectedItem();
        if(etn_Ntip.getText().toString().compareTo("")!=0){

            Log.d("MyActivity","########  -"+etn_Ntip.getTextSize()+"-" );
            r_ntip = Double.parseDouble(etn_Ntip.getText().toString());
        }

        if(rb_Yes.isChecked())
            r_carflag=1;

        n_hours = Character.getNumericValue(s_nhours.charAt(0));
        n_minutes = Character.getNumericValue(s_nhours.charAt(2));
        r_tot_hours = (int)  n_hours*HOURS_REWARD + n_minutes;
        r_total_day = r_tot_hours + r_ntip + r_carflag*CAR_REWARD;

        rb_No.setChecked(true);
        rb_Yes.setChecked(false);

        //NEW RECORD
        record=text_data_select+" "+n_hours+" "+n_minutes+" "+r_tot_hours+" "+r_ntip+" "+r_carflag+" "+r_total_day;
        Log.d("MyActivity","#################### record:"+record );


        record_add = record;
        //SET DIALOG
        setDialog();

    }

    public void setDialog(){
        //DIALOG
        AlertDialog.Builder b_dialog= new AlertDialog.Builder(MainActivity.this);
        View dialog_view = getLayoutInflater().inflate(R.layout.dialogadd,null);
        Button btn_save = (Button) dialog_view.findViewById(R.id.btn_dialog_delete);
        Button btn_no = (Button) dialog_view.findViewById(R.id.btn_No);
        TextView tv_title= (TextView) dialog_view.findViewById(R.id.tv_title);

        //SET LABEL
        if(myDB.alreadySaved(record_add)){
            tv_title.setText("Already existing,REPLACE record ?");
            btn_save.setText("REPLACE");
        }else{
            tv_title.setText("ADD record ?");
            btn_save.setText("ADD");
        }

        b_dialog.setView(dialog_view);
        AlertDialog dialog = b_dialog.create();
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ADD
                boolean added = myDB.addData(record_add);

                setMoneyMonth();

                //reset input
                etn_Ntip.setText("");

                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.sample_anim);
                sp_Nhours.startAnimation(animation);
                sp_Nhours.setSelection(0);
                dialog.dismiss();
            }
        });
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_no.setBackgroundColor(getColor(R.color.background));
        dialog.show();
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void setHoursReward(int hr){
        HOURS_REWARD=hr;
    }

    public void setCarReward(int car){
        CAR_REWARD=car;
    }

    public int getHoursReward(){
        return HOURS_REWARD;
    }

    public int getCarReward(){
        return CAR_REWARD;
    }

    public void setDataText(){
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fadein);
        tv_date.startAnimation(animation);
        tv_date.setText(text_data_select.substring(0,2));
    }

    public void setMoneyMonth(){
        String monthyear = text_data_select.substring(3);
        Log.d("MyActivity","MA284######## myDB.getMoneyMonth("+monthyear+")" );
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        tv_moneymonth.startAnimation(animation);
        tv_moneymonth.setText(myDB.getMoneyMonth(monthyear)+"€");

    }

    public void removeCarReward(){
        layout_car.setVisibility(View.GONE);
        rb_No.setChecked(true);
        rb_Yes.setChecked(false);
    }

    public void addCarReward(){
        layout_car.setVisibility(View.VISIBLE);
        rb_No.setChecked(true);
        rb_Yes.setChecked(false);
    }

    public void set_input(){
        String[] input = myDB.getValueOfDate(text_data_select);

        if(input==null) {//no record in the db
            sp_Nhours.setSelection(0);
            etn_Ntip.setText("");
            rb_No.setChecked(true);
            rb_Yes.setChecked(false);
            return;
        }

        for(int i=0;i<adapter.getCount();i++){
            if(input[0].compareTo(adapter.getItem(i).toString())==0)
                sp_Nhours.setSelection(i);
        }
        etn_Ntip.setText(input[1]);
    }

}