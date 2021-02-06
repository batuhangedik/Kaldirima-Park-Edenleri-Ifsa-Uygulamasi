package com.example.plakaapp2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.plakaapp2.Models.ifsaModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class plakalarActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> plakaArray;
    ArrayList<String> konumArray;
    ArrayList<ifsaModel> models;
    ArrayList<Integer> idArray;
    private DatabaseReference mDatabase;
    ArrayAdapter arrayAdapter;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plakalar);

        listView = findViewById(R.id.listView);
        plakaArray=new ArrayList<String>();
        idArray = new ArrayList<Integer>();
        konumArray = new ArrayList<String>();
        models= new ArrayList<>();






        mDatabase = FirebaseDatabase.getInstance().getReference("ifsalar");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                plakaArray.clear();
                models.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    ifsaModel k = postSnapshot.getValue(ifsaModel.class);
                    k.Id=postSnapshot.getKey().toString();
                    plakaArray.add(k.Plaka);
                    models.add(k);
                }
                if(plakaArray.size()<=0){
                    Toast.makeText(plakalarActivity.this, "Liste Boş ... ", Toast.LENGTH_LONG).show();
                }
                else {
                    Map<String, Long> result =
                            plakaArray.stream()
                                    .sorted(String::compareTo)
                                    .collect(
                                            Collectors.groupingBy(
                                                    Function.identity(), Collectors.counting()
                                            )
                                    );
                    LinkedHashMap<String, Long> sortedMap = new LinkedHashMap<>();
                    result.entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                            .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

                    System.out.println("Reverse Sorted Map   : " + sortedMap);

                    List<String> strlist= new ArrayList<>(sortedMap.keySet());
                    strlist=strlist.size()>3?strlist.subList(0,3):strlist.subList(0,strlist.size());
                    arrayAdapter = new ArrayAdapter(plakalarActivity.this,android.R.layout.simple_list_item_1,strlist);
                    listView.setAdapter(arrayAdapter);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //  getData();
    }

    public void getData(){


      /*  try {
            SQLiteDatabase database = this.openOrCreateDatabase("ifsalar", MODE_PRIVATE, null);
            //Cursor cursor = database.rawQuery("SELECT 'plaka', COUNT('plaka') AS 'value_occurrence' FROM ifsalar GROUP BY 'plaka' ORDER BY 'value_occurrence' DESC LIMIT 3;", null);
            Cursor cursor = database.rawQuery("SELECT *,\n" +
                    "             COUNT('plaka') AS 'value_occurrence' \n" +
                    "    FROM     ifsalar\n" +
                    "    GROUP BY plaka\n" +
                    "    ORDER BY value_occurrence DESC\n" +
                    "    LIMIT    3;", null);
            int plakaIx = cursor.getColumnIndex("plaka");
            int idX = cursor.getColumnIndex("id");

            while (cursor.moveToNext()) {
                plakaArray.add(cursor.getString(plakaIx));
                idArray.add(cursor.getInt(idX));
            }
            arrayAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){e.printStackTrace();}*/
    }


    static class ValueComparator<K, V extends Comparable<V>> implements Comparator<K> {

        Map<K, V> map;

        public ValueComparator(Map<K, V> base) {
            this.map = base;
        }

        @Override
        public int compare(K o1, K o2) {
            return map.get(o2).compareTo(map.get(o1));
        }
    }
}