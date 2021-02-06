package com.example.plakaapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button bildir;
    Button bildirilenler;
    Button encoklar;
    Button Adresler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bildir = findViewById(R.id.bildir);
        bildirilenler = findViewById(R.id.bildirilenler);
        encoklar=findViewById(R.id.top3);
     /*  Adresler=findViewById(R.id.top2);
        Adresler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,adresActivity.class);
                startActivity(intent);
            }
        });*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        //menuInflater.inflate(R.menu.add_ifsa,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        return super.onOptionsItemSelected(item);
    }

    public void ifsabildir(View view)
    {
        Intent intent = new Intent(MainActivity.this,ifsaActivity.class);
        intent.putExtra("info","new");
        startActivity(intent);
    }

    public void gotoBildirilenler(View view){
        Intent intent = new Intent(MainActivity.this,listeActivity.class);
        startActivity(intent);
    }

    public void gotoplakalar(View view){
        Intent intent = new Intent (MainActivity.this, plakalarActivity.class);
        startActivity(intent);
    }

    public void gotobolgeler(View view){
        Intent intent = new Intent(MainActivity.this,adresActivity.class);
        startActivity(intent);
    }
}