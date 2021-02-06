package com.example.plakaapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.plakaapp2.Models.SelectedModel;
import com.example.plakaapp2.Models.ifsaModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.SplittableRandom;

public class listeActivity extends AppCompatActivity {
        ListView listView;
        ArrayList<String> plakaArray;
        ArrayList<ifsaModel> models;
        ArrayList<Integer> idArray;
    private DatabaseReference mDatabase;
    ArrayAdapter arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);
        models= new ArrayList<>();
        listView = findViewById(R.id.listView);
        plakaArray=new ArrayList<String>();
        idArray = new ArrayList<Integer>();
        mDatabase = FirebaseDatabase.getInstance().getReference("ifsalar");
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,plakaArray);
        listView.setAdapter(arrayAdapter);

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
                if(plakaArray.size()<1){
                    Toast.makeText(listeActivity.this, "Liste Boş ... ", Toast.LENGTH_LONG).show();
                }
                arrayAdapter = new ArrayAdapter(listeActivity.this,android.R.layout.simple_list_item_1,plakaArray);
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(listeActivity.this,ifsaActivity.class);
                intent.putExtra("plakaId",models.get(position).Id);
                intent.putExtra("info","old");
                SelectedModel.Model=models.get(position);
                startActivity(intent);
            }
        });


      //  getData();


    }

    public void getData(){
        try {
            SQLiteDatabase database = this.openOrCreateDatabase("ifsalar", MODE_PRIVATE, null);
            Cursor cursor = database.rawQuery("SELECT * FROM ifsalar", null);
            int plakaIx = cursor.getColumnIndex("plaka");
            int idX = cursor.getColumnIndex("id");

            while (cursor.moveToNext()) {
                plakaArray.add(cursor.getString(plakaIx));
                idArray.add(cursor.getInt(idX));
            }
            arrayAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){e.printStackTrace();}
    }
}