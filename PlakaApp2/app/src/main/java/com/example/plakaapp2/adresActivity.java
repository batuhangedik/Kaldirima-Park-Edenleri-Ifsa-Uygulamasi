package com.example.plakaapp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.plakaapp2.Models.ifsaModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class adresActivity extends AppCompatActivity implements LocationListener {
    ArrayList<String> AdresArray;
    ArrayList<ifsaModel> models;
    private DatabaseReference mDatabase;
    ArrayAdapter arrayAdapter;
    ListView listView;
    List<String> selectedAdres;
    ProgressDialog pd;
    String Sokak="",Mahalle="",Il="";
    String Longitude="",Latitude="";
    LocationManager konumYoneticisi ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adres);
        models= new ArrayList<>();
        listView = findViewById(R.id.adreslst);
        pd= new ProgressDialog(this);
        selectedAdres=new ArrayList<>();
        AdresArray=new ArrayList<String>();
        mDatabase = FirebaseDatabase.getInstance().getReference("ifsalar");
        if(ContextCompat.checkSelfPermission(adresActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(adresActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        getLocation();




    }
    @Override
    public void onLocationChanged(Location location) {
        Longitude=""+location.getLongitude();
        Latitude=""+location.getLatitude();

        try {
            Geocoder geo = new Geocoder(adresActivity.this.getApplicationContext(), Locale.getDefault());
            double lati=Double.parseDouble(Latitude);
            double longu= Double.parseDouble(Longitude);
            List<Address> addresses = geo.getFromLocation(lati, longu, 1);
            if (addresses.isEmpty()) {
                Log.e("Adres","Waiting for Location");
            }
            else {
                if (addresses.size() > 0 && (Mahalle.equals("") && Sokak.equals("") && Il.equals("") )) {
                    Sokak=addresses.get(0).getThoroughfare();
                    Mahalle=addresses.get(0).getSubLocality();
                    Il=addresses.get(0).getAdminArea();
                    String adres = (addresses.get(0).getThoroughfare() + ", " + addresses.get(0).getSubLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                    Log.e("Adres",adres);
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            AdresArray.clear();
                            models.clear();

                            for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                                ifsaModel k = postSnapshot.getValue(ifsaModel.class);
                                k.Id=postSnapshot.getKey().toString();
                                AdresArray.add(k.Sokak +" - "+k.Mahalle +" - "+k.Il);
                                models.add(k);
                            }
                            if(AdresArray.size()<=0){
                                Toast.makeText(adresActivity.this, "Liste Boş ... ", Toast.LENGTH_LONG).show();
                            }
                            else if(AdresArray.size()<=3)
                            {
                                arrayAdapter = new ArrayAdapter(adresActivity.this,android.R.layout.simple_list_item_1,AdresArray);
                                listView.setAdapter(arrayAdapter);
                            }
                            else if(AdresArray.size()>3) {
                               selectedAdres.clear();
                                for (String s:AdresArray){
                                    if(s.contains(Sokak) && s.contains(Mahalle) && s.contains(Il) && selectedAdres.size()<4){
                                        if(selectedAdres.indexOf(s)<0){selectedAdres.add(s);}
                                    }
                                }
                                for (String s:AdresArray){
                                    if(s.contains(Mahalle) && s.contains(Il) && selectedAdres.size()<4){
                                        if(selectedAdres.indexOf(s)<0){selectedAdres.add(s);}
                                    }
                                }

                                for (String s:AdresArray){
                                    if(s.contains(Sokak)  && s.contains(Il) && selectedAdres.size()<4 ){
                                        if(selectedAdres.indexOf(s)<0){selectedAdres.add(s);}
                                    }
                                }
                                for (String s:AdresArray){
                                    if(s.contains(Sokak) && s.contains(Mahalle)&& selectedAdres.size()<4 ){
                                        if(selectedAdres.indexOf(s)<0){selectedAdres.add(s);}
                                    }
                                }

                                for (String s:AdresArray){
                                    if(s.contains(Mahalle) ){
                                        if(selectedAdres.indexOf(s)<0){selectedAdres.add(s);}
                                    }
                                }
                                for (String s:AdresArray){
                                    if(s.contains(Il) ){
                                        if(selectedAdres.indexOf(s)<0){selectedAdres.add(s);}
                                    }
                                }
                                Toast.makeText(adresActivity.this, ""+Il+Mahalle+Sokak, Toast.LENGTH_SHORT).show();
                                arrayAdapter = new ArrayAdapter(adresActivity.this,android.R.layout.simple_list_item_1,selectedAdres);
                                listView.setAdapter(arrayAdapter);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }
            } pd.dismiss();
        }
        catch (Exception e){

        }
        pd.dismiss();
    }

    @Override
    public void onProviderDisabled(String provider) {
        pd.dismiss();
        Toast.makeText(adresActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();

    }


    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            pd.setTitle("Konum Servisi");
            pd.show();
            pd.setMessage("Alınıyor...");
            konumYoneticisi = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            konumYoneticisi.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5000,
                    5,
                    (LocationListener) adresActivity.this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

}