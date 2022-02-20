package com.fincato.walletip;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ViewSettings extends AppCompatActivity {

    private TextView tv_s_car_reward;
    private TextView tv_eh2;
    private EditText etn_hourly_rate;
    private EditText etn_car;
    private Context context;
    private RadioButton rb_car_Yes;
    private RadioButton rb_car_No;

    public DB_Records myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewsettings);
        context= this;

        tv_s_car_reward = findViewById(R.id.tv_s_car_reward);
        tv_eh2 = findViewById(R.id.tv_eh2);
        etn_hourly_rate = findViewById(R.id.etn_hourly_rate);
        etn_car= findViewById(R.id.etn_car);
        etn_car.setText(MainActivity.getInstance().getCarReward()+"",null);
        etn_hourly_rate.setText(MainActivity.getInstance().getHoursReward()+"",null);

        //  RADIO BUTTON YES
        rb_car_Yes = findViewById(R.id.rb_car_yes);

        //  RADIO BUTTON NO
        rb_car_No = findViewById(R.id.rb_car_no);

        myDB= new DB_Records(this);

        etn_hourly_rate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                etn_hourly_rate.setTextColor(ContextCompat.getColor(context, R.color.white));
            }
        });

        etn_car.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                etn_car.setTextColor(ContextCompat.getColor(context, R.color.white));
            }
        });

        if(!myDB.getCarSettings()){
            Log.d("settings", "########### visible");
            rb_car_No.setChecked(false);
            rb_car_Yes.setChecked(true);
            etn_car.setVisibility(View.VISIBLE);
            tv_s_car_reward.setVisibility(View.VISIBLE);
            tv_eh2.setVisibility(View.VISIBLE);
        }else{
            Log.d("settings", "########### not visible");
            rb_car_No.setChecked(true);
            rb_car_Yes.setChecked(false);
            etn_car.setVisibility(View.GONE);
            tv_s_car_reward.setVisibility(View.GONE);
            tv_eh2.setVisibility(View.GONE);
        }

    }

    public void selectNoCar(View view){
        rb_car_No.setChecked(true);
        etn_car.setVisibility(View.GONE);
        tv_s_car_reward.setVisibility(View.GONE);
        tv_eh2.setVisibility(View.GONE);


    }

    public void selectYesCar(View view){
        rb_car_Yes.setChecked(true);
        etn_car.setVisibility(View.VISIBLE);
        tv_s_car_reward.setVisibility(View.VISIBLE);
        tv_eh2.setVisibility(View.VISIBLE);

    }

    //  BTN SAVE
    public void saveSettings(View view){
        int e_car=0;
        int e_hours=0;
        try { // always want to make sure the cursor gets closed.
            e_car=Integer.parseInt(etn_car.getText().toString());
            e_hours=Integer.parseInt(etn_hourly_rate.getText().toString());
            etn_car.setTextColor(ContextCompat.getColor(context, R.color.white));
            etn_hourly_rate.setTextColor(ContextCompat.getColor(context, R.color.white));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"ERROR",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


        if(rb_car_Yes.isChecked()){
            if(e_car>0){
                if(e_hours>0){
                    MainActivity.getInstance().setHoursReward(e_hours);
                    MainActivity.getInstance().setCarReward(e_car);
                    MainActivity.getInstance().addCarReward();
                    myDB.setCarSettings(false);
                    Toast.makeText(getApplicationContext(),"SAVED",Toast.LENGTH_LONG).show();
                    //RETURN
                }else{
                    etn_hourly_rate.setTextColor(ContextCompat.getColor(this, R.color.red));
                    Toast.makeText(getApplicationContext(),"NOT SAVED",Toast.LENGTH_LONG).show();
                }
            }else{
                etn_car.setTextColor(ContextCompat.getColor(this, R.color.red));
                Toast.makeText(getApplicationContext(),"NOT SAVED",Toast.LENGTH_LONG).show();
            }
        }else{//no checked
            if(e_hours>0){//ok
                MainActivity.getInstance().setHoursReward(e_hours);
                MainActivity.getInstance().removeCarReward();
                myDB.setCarSettings(true);
                Toast.makeText(getApplicationContext(),"SAVED",Toast.LENGTH_LONG).show();
                //RETURN
            }else{//no
                etn_hourly_rate.setTextColor(ContextCompat.getColor(this, R.color.red));
                Toast.makeText(getApplicationContext(),"NOT SAVED",Toast.LENGTH_LONG).show();
            }
        }
    }

    public void DialogClearDB(View view){
        //DIALOG
        AlertDialog.Builder b_dialog= new AlertDialog.Builder(ViewSettings.this);
        View dialog_view = getLayoutInflater().inflate(R.layout.dialogadd,null);
        Button btn_yes = (Button) dialog_view.findViewById(R.id.btn_dialog_delete);
        Button btn_no = (Button) dialog_view.findViewById(R.id.btn_No);
        TextView tv_title= (TextView) dialog_view.findViewById(R.id.tv_title);

        tv_title.setText("Do you want delete the database? ?");
        btn_yes.setText("YES");


        b_dialog.setView(dialog_view);
        AlertDialog dialog = b_dialog.create();
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete
                myDB.clearDB();
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
}
