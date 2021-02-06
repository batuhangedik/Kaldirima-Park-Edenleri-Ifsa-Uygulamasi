package com.example.plakaapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class bolgeler extends AppCompatActivity {
    ListView listView;
    ArrayList<String> plakaArray;
    ArrayList<String> konumArray;
    ArrayList<Integer> idArray;
    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolgeler);


        listView = findViewById(R.id.listView);
        plakaArray=new ArrayList<String>();
        idArray = new ArrayList<Integer>();
        konumArray = new ArrayList<String>();


        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,konumArray);
        listView.setAdapter(arrayAdapter);
    }

    public void getData(){
        try {
            SQLiteDatabase database = this.openOrCreateDatabase("ifsalar", MODE_PRIVATE, null);
            //Cursor cursor = database.rawQuery("SELECT 'plaka', COUNT('plaka') AS 'value_occurrence' FROM ifsalar GROUP BY 'plaka' ORDER BY 'value_occurrence' DESC LIMIT 3;", null);
            Cursor cursor = database.rawQuery("SELECT 'konum',\n" +
                    "             COUNT('konum') AS 'value_occurrence' \n" +
                    "    FROM     ifsalar\n" +
                    "    GROUP BY konum\n" +
                    "    ORDER BY value_occurrence DESC\n" +
                    "    LIMIT    3;", null);
            int konumIx = cursor.getColumnIndex("konum");
            int idX = cursor.getColumnIndex("id");

            while (cursor.moveToNext()) {
                konumArray.add(cursor.getString(konumIx));
                idArray.add(cursor.getInt(idX));
            }
            arrayAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){e.printStackTrace();}
    }

}