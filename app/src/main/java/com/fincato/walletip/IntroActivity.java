package com.fincato.walletip;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class IntroActivity extends AppCompatActivity {
    public TextView tv ;
    CharSequence charSequence;
    int index;
    long delay = 200;
    Handler hendler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        getSupportActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        tv=findViewById(R.id.tv_intro);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent( IntroActivity.this,
                        MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        },3000);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            tv.setText(charSequence.subSequence(0,index++));

            if(index <= charSequence.length()){
                hendler.postDelayed(runnable,delay);
            }

        }
    };


}