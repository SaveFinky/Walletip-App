package com.fincato.walletip;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DB_Records extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "record_list.db";
    public static final String TABLE_NAME = "record_table";
    public static final String TABLE_MONTH = "month_table";
    public static final String COL1 = "DATE";
    public static final String COL2 = "N_HOURS";
    public static final String COL3 = "N_MINUTES";
    public static final String COL4 = "TOT_HOURS";
    public static final String COL5 = "N_TIP";
    public static final String COL6 = "CAR_FLAG";
    public static final String COL7 = "TOT_DAY";
    private Context context_db;
    public Array[] month_array= new Array[12];
    String[] month={"01","02","03","04","05","06","07","08","09","10","11","12"};

    public Calendar calendar = Calendar.getInstance();
    public Date date = calendar.getTime();
    public long msDate = date.getTime();
    public String s_month;

    //record=r_date+" "+n_hours+" "+n_minutes+" "+r_tot_hours+" "+r_ntip+" "+r_carflag+" "+r_total_day;
    //EXAMPLE -->30/10/2021 1 0 6 0.0 0 6.0

    public DB_Records(Context context) {
        super(context, DATABASE_NAME, null, 1);
        context_db=context;
        Log.d("MainActivity", "##################   costruttore DBrecord");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //MONTH TABLE
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_MONTH + " (ID TEXT PRIMARY KEY , " +
                " TOT_MONTH TEXT)";
        db.execSQL(createTable);

        //RECORD TABLE
        createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (DATE TEXT PRIMARY KEY , " +
                " N_HOURS TEXT,"+
                " N_MINUTES TEXT,"+
                " TOT_HOURS TEXT,"+
                " N_TIP TEXT,"+
                " CAR_FLAG TEXT,"+
                " TOT_DAY TEXT)";
        db.execSQL(createTable);

        //populateTableMonth(db);

        Log.d("MainActivity", "##################   ONCREATE DBrecord");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       /*
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONTH);
        onCreate(db);
        Log.d("MainActivity", "##################   ONUPGRADE DBrecord");
        */

    }

    public boolean addData(String record) {
        String[] parts = record.split(" ");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, parts[0]);
        contentValues.put(COL2, parts[1]);
        contentValues.put(COL3, parts[2]);
        contentValues.put(COL4, parts[3]);
        contentValues.put(COL5, parts[4]);
        contentValues.put(COL6, parts[5]);
        contentValues.put(COL7, parts[6]);
        long result;

        Log.d("MainActivity", "Parts[0]="+parts[0]+"<");

        String querySelect= "SELECT * FROM "+TABLE_NAME+" WHERE "+COL1+" = \""+parts[0]+"\";";

        Cursor cursor = db.rawQuery(querySelect, null);

        if(cursor.getCount()>0){//ALREADY EXISTING
            //Log.d("MainActivity", "########### NUMBER of cursor:"+cursor.getCount());
            db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + "=\"" + parts[0] + "\";");
            Toast.makeText(context_db,"REPLACED",Toast.LENGTH_SHORT).show();

        }
        //NOT EXISTING or DELETED
        result = db.insert(TABLE_NAME, null, contentValues);
        Toast.makeText(context_db,"ADDED",Toast.LENGTH_SHORT).show();

        updateMonth(parts[0]);

        Log.d("MainActivity",cursor.getCount()+"");
        Log.d("MainActivity","ADDED, part0 ="+parts[0]+"-");

        return true;
    }

    public boolean alreadySaved(String record){
        String[] parts = record.split(" ");
        SQLiteDatabase db = this.getWritableDatabase();

        String querySelect= "SELECT * FROM "+TABLE_NAME+" WHERE "+COL1+" = \""+parts[0]+"\";";
        Cursor cursor = db.rawQuery(querySelect, null);
        if(cursor.getCount()>0) {//ALREADY EXISTING
            return true;
        }
        return false;
    }

    public Cursor getListContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;
    }

    public Cursor getMonthContents(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_MONTH, null);
        return data;
    }

    public boolean updateMonth(String record){
        String[] parts = record.split(" ");
        double m_month=0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE "+COL1+" LIKE '___"+parts[0].substring(3)+"'", null);

        try { // always want to make sure the cursor gets closed.
            while (data.moveToNext()) {
                m_month = m_month + Double.parseDouble(data.getString(6));
            }
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            return false;
        } finally {
            data.close();
        }
        //UPDATE TABLE
        //delete
        db.execSQL("DELETE FROM " + TABLE_MONTH + " WHERE ID =\"" +parts[0].substring(3)+ "\";");

        //insert new
        ContentValues contentValues = new ContentValues();
        contentValues.put("ID", parts[0].substring(3));
        contentValues.put("TOT_MONTH", m_month);
        Log.d("MainActivity", "ID="+parts[0].substring(3)+", TOT_MONTH="+m_month);
        db.insert(TABLE_MONTH, null, contentValues);
        Log.d("MainActivity", "TABLE_UPDATED");
        return true;
    }

    public String getLast12MonthToT(){
        s_month =(String) new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(msDate);
        String allmonth="";

        for(int i = 0;i<12;i++){
           if(i <= calendar.get(Calendar.MONTH)){
               allmonth+=getMoneyMonth(month[i]+"/"+calendar.get(Calendar.YEAR))+";";
           }else{
               allmonth+=getMoneyMonth(month[i]+"/"+(calendar.get(Calendar.YEAR)-1))+";";
           }
        }
        return allmonth;
    }

    public String getLast12MonthAVG(){
        s_month =(String) new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(msDate);
        String allmonth="";

        for(int i = 0;i<12;i++){
            if(i <= calendar.get(Calendar.MONTH)){
                allmonth+=getMonthAVG(month[i]+"/"+calendar.get(Calendar.YEAR))+";";
            }else{
                allmonth+=getMonthAVG(month[i]+"/"+(calendar.get(Calendar.YEAR)-1))+";";
            }
        }
        Log.d("###############   AllmonthAVG =",allmonth);
        return allmonth;
    }

    //LIST IS REVERSED 03/2021 -> 2021/03
    public List<String> getMonthList(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_MONTH + ";", null);

        if(data.getCount()==0){
            return null;
        }

        List<String> sList = new ArrayList<String>();
        while (data.moveToNext()) {
            sList.add(data.getString(0));
        }

        //reverse
        for(int i=0;i<sList.size();i++) {
            String[] str = sList.get(i).split("/");
            sList.set(i, str[1]+"/"+str[0]);
        }
        //sort
        insertionSort(sList, sList.size());
        //Collections.sort(sList);

        for (int i = 0; i< sList.size();i++)
            Log.d("LISTA MESI",sList.get(i));
        return sList;

    }

    public String getAllMonthToT(){
        List<String> month_list = getMonthList();
        String allmonth="",my;

        if(month_list==null)
            return "";

        for(int i = 0;i<month_list.size();i++){
            my=month_list.get(i).substring(5)+"/"+month_list.get(i).substring(0,4);
            Log.d("###############getmonth my =",my);
            allmonth+=getMoneyMonth(my)+";";
        }
        return allmonth;
    }

    public String getAllMonthAVG(){
        List<String> month_list = getMonthList();
        String allmonth="",my;

        if(month_list==null)
            return "";

        for(int i = 0;i<month_list.size();i++){
            my=month_list.get(i).substring(5)+"/"+month_list.get(i).substring(0,4);
            Log.d("############### my =",my);
            allmonth+=getMonthAVG(my)+";";
        }
        return allmonth;
    }

    public String getMoneyMonth(String number_month){
        double m_month=0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_MONTH +" WHERE ID =\"" + number_month + "\";", null);
        try { // always want to make sure the cursor gets closed.

            if(data.getCount()==0)//not existing
                if(createMonth(number_month,db)){
                    return "0.0";
                }else{
                    return "error";
                }
            while (data.moveToNext()) {
                m_month =Double.parseDouble(data.getString(1));
            }
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            return "ERROR EXCEPTION";
        } finally {
            data.close();
        }
        return m_month+"";
    }

    public String getMonthAVG(String number_month){
        double tot_month=0;
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE DATE LIKE '___"+number_month+"'", null);

        int number_of_day= data.getCount();
        try { // always want to make sure the cursor gets closed.

            if(data.getCount()==0){
                if(createMonth(number_month,db)){
                    return "0.0";
                }else{
                    return "error";
                }
            }
            while (data.moveToNext()) {//scorre le tuple
                tot_month += Double.parseDouble(data.getString(6));
            }
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
            return "ERROR EXCEPTION";
        } finally {
            data.close();
        }
        double avg = tot_month/number_of_day;
        return avg + "";
    }

    //03/2021
    public Boolean createMonth(String monthyear,SQLiteDatabase db){
        int month= Integer.parseInt(monthyear.substring(0,2));
        int year=Integer.parseInt(monthyear.substring(3));

        Log.d("settings", "########### month:"+month+",year:"+year+"---tod.month:"+(calendar.get(Calendar.MONTH)+1)+",tod.year:"+(calendar.get(Calendar.YEAR)));
        if(!( month>(calendar.get(Calendar.MONTH)+1) && year == (calendar.get(Calendar.YEAR)) )
            && !( year > (calendar.get(Calendar.YEAR)) )
            && !( year < (calendar.get(Calendar.YEAR))-5 )){
            //ok create
            ContentValues contentValues = new ContentValues();
            contentValues.put("ID", monthyear);
            contentValues.put("TOT_MONTH","0");
            db.insert(TABLE_MONTH, null, contentValues);

            return true;
        }else{
            return false;
        }
    }
    /*
    *@return True is present
    *@return False isn't present
     */
    public boolean getCarSettings(){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor data = db.rawQuery("SELECT * FROM "+TABLE_NAME +" WHERE DATE = \"car\";", null);

        Log.d("settings", "########### get car settings count number :"+data.getCount());
        try { //there is
            if(data.getCount()!=0)
                return true;
        } catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
        } finally {
            data.close();
        }
        return false;
    }

    /*
    *true insert
    *false remove   */
    public void setCarSettings(boolean set){
        SQLiteDatabase db = this.getWritableDatabase();

        if(set){//insert
            if(!getCarSettings()){
                ContentValues contentValues = new ContentValues();
                contentValues.put(COL1, "car");
                contentValues.put(COL2, "reward");
                contentValues.put(COL3, "are");
                contentValues.put(COL4, "not");
                contentValues.put(COL5, "visible");
                contentValues.put(COL6, "");
                contentValues.put(COL7, "");
                db.insert(TABLE_NAME, null, contentValues);
                Log.d("DB", "########### added CAR");
            }
        }
        else{//remove
            if(getCarSettings()){
                db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + "=\"car\";");
                Log.d("DB", "########### delete CAR");
            }

        }
    }

    public void clearDB(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME +";");
        db.execSQL("DELETE FROM " + TABLE_MONTH +";");
    }

    //record=    r_date+" "+n_hours+" "+n_minutes+" "+r_tot_hours+" "+r_ntip+" "+r_carflag+" "+r_total_day;
    //EXAMPLE -->30/10/2021 1 0 6 0.0 0 6.0
    public String[] getValueOfDate(String Date) {
        Log.d("DB", "################## Date: "+Date);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE "+COL1+" = \""+Date+"\"", null);
        if(data.getCount()==0){
            Log.d("DB", "################## getValueOfDate count: 0");
            return null;
        }


        String hour="",tip="";
        while (data.moveToNext()) {
            hour = data.getString(1)+"h"+data.getString(2)+"0m";
            tip = data.getString(4);
        }
        Log.d("DB", "################## hour,tip: "+hour+","+tip);
        return new String[]{hour,tip};
    }

    public void delete_record_date(String date){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + COL1 + "=\"" + date + "\";");
        Toast.makeText(context_db,"DELETED",Toast.LENGTH_SHORT).show();
    }

    public void refresh_all(){
        getAllMonthToT();
    }


    public void insertionSort(List<String> list, int vSize){

        for (int i = 1; i < vSize; i++){
            String temp = list.get(i); // new element
            int j;

            for (j = i; j > 0 && compareMonth(temp,list.get(j-1)); j--)
                list.set(j, list.get(j-1));
            list.set(j, temp); // inserisci temp in posizione
        }
    }

    // DATE = 08/2020
    //date1 < date2 = TRUE
    public boolean compareMonth(String date1, String date2){
        if(date1.substring(3).compareTo(date2.substring(3))<0)//date1 < date2
            return true;
        if(date1.substring(3).compareTo(date2.substring(3))>0) //date1 > date2
            return false;
        if(date1.compareTo(date2)<0)//date1 < date2
            return true;
        else
            return false;
    }
}

