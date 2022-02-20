package com.fincato.walletip;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;

public class ViewListContents extends AppCompatActivity {

    DB_Records myDB;
    Button btn_delete;
    ConstraintLayout mainLayout;
    ListView listViewrecord;
    ListView listViewMonth;
    ArrayList<String> theList;
    //record=r_date+" "+n_hours+" "+n_minutes+" "+r_tot_hours+" "+r_ntip+" "+r_carflag+" "+r_total_day;
    //EXAMPLE -->30/10/2021 1 0 6 0.0 0 6.0
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewlistcontents_layout);

        mainLayout= findViewById(R.id.main_layout);

        listViewrecord = (ListView) findViewById(R.id.listView);
        listViewMonth = (ListView) findViewById(R.id.listViewMonth);
        myDB = new DB_Records(this);

        //populate an ArrayList<String> from the database and then view it
        theList = new ArrayList<>();
        Cursor data = myDB.getListContents();
        String car="";
        if(data.getCount() == 0){
            Toast.makeText(this, "There are no contents in this list!",Toast.LENGTH_SHORT).show();
        }else{
            while(data.moveToNext()){
                if(data.getString(5).compareTo("1")==0)
                    car="SI";
                else
                    car="NO";

                if(data.getString(0).compareTo("car")!=0)
                    theList.add(data.getString(0)+"    "+data.getString(1)+":"+
                                 data.getString(2)+"0    "+data.getString(3)+"    "+
                                 data.getString(4)+"    "+car+"    "+
                                 data.getString(6));

                //ListAdapter listAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,theList);
                //listView.setAdapter(listAdapter);
            }

        }
        ListAdapter listAdapter = new ArrayAdapter<>(this,R.layout.list_item,theList);
        listViewrecord.setAdapter(listAdapter);


        //           MONTH LIST
        ArrayList<String> theList2 = new ArrayList<>();

        Cursor month = myDB.getMonthContents();
        if(month.getCount() == 0){
            Toast.makeText(this, "There are no contents in this list!",Toast.LENGTH_SHORT).show();
        }else{
            while(month.moveToNext()){
                if(Double.parseDouble(month.getString(1))>0)
                theList2.add(month.getString(0)+" "+month.getString(1));
            }
            listAdapter = new ArrayAdapter<>(this,R.layout.list_item,theList2);
            listViewMonth.setAdapter(listAdapter);
        }


        //DELETE ITEM

        listViewrecord.setOnItemClickListener (new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                Log.d("itemclick","POSITION :"+position);
                String date= theList.get(position).substring(0,10);
                Log.d("itemclick","DATE :"+date+"-");
                setDialogDelete(date,position);
            }
        });


    }

    public void setDialogDelete(String record,int index){
        //DIALOG
        AlertDialog.Builder b_dialog= new AlertDialog.Builder(ViewListContents.this);
        View dialog_view = getLayoutInflater().inflate(R.layout.dialogdeleteitem,null);
        Button btn_delete = (Button) dialog_view.findViewById(R.id.btn_dialog_delete);
        TextView tv_title= (TextView) dialog_view.findViewById(R.id.tv_title);
        TextView tv_record= (TextView) dialog_view.findViewById(R.id.tv_record);

        //SET LABEL
        tv_record.setText(record);


        b_dialog.setView(dialog_view);
        AlertDialog dialog = b_dialog.create();
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DELETE

                 myDB.delete_record_date(record);

                 myDB.refresh_all();

                 //remove on the list and refresh listView
                updatelist(index);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updatelist(int index){
        theList.remove(index);
        ListAdapter listAdapter = new ArrayAdapter<>(this,R.layout.list_item,theList);
        listViewrecord.setAdapter(listAdapter);
    }



}
