package com.example.plakaapp2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.plakaapp2.Models.SelectedModel;
import com.example.plakaapp2.Models.ifsaModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

public class ifsaActivity extends AppCompatActivity implements LocationListener {
    Bitmap selectedImage;
    ImageView imageView;
    EditText  plakaText;
    EditText konumText;
    EditText tarihText;
    ProgressDialog pd;
    String Sokal,Mahalle,Il;
    Button savebutton,konumAl;
    Uri ImageUri;
    String Longitude="",Latitude="";
    LocationManager konumYoneticisi ;
    SQLiteDatabase database;
    private DatabaseReference mDatabase;
    StorageReference storageReference;
    FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ifsa);
        konumYoneticisi = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        imageView = findViewById(R.id.imageView);
        plakaText= findViewById(R.id.editTextPlaka);
        konumText = findViewById(R.id.editTextKonum);
        tarihText= findViewById(R.id.editTextDate);
        savebutton= findViewById(R.id.savebutton);
        konumAl= findViewById(R.id.konumAllButton);
        pd= new ProgressDialog(this);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference("ifsalar");
        if(ContextCompat.checkSelfPermission(ifsaActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ifsaActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        konumAl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });
        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if (info.matches("new")) {//menu'den geldiyse
            plakaText.setText("");
            konumText.setText("");
            SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(System.currentTimeMillis());
            tarihText.setText(formatter.format(date));
            savebutton.setVisibility(View.VISIBLE);


            Bitmap selectImage = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.foto_ekle);
            imageView.setImageBitmap(selectImage);
            //edittextleri boşalt,butonu görünür yap,imageview'a select image resmini koy.
            //yeni birşey eklemeye hazır hale getir kısaca.

        } else  {//listview'den geldiyse
            int plakaId = intent.getIntExtra("plakaId",1);
            //intent'ten artId ile gelen idArray.get(position)'da ki değeri getir.
            savebutton.setVisibility(View.INVISIBLE);
            konumAl.setVisibility(View.INVISIBLE);
            imageView.setClickable(false);
            plakaText.setText(SelectedModel.Model.Plaka);
            konumText.setText(SelectedModel.Model.Sokak+" "+SelectedModel.Model.Mahalle+ ""+SelectedModel.Model.Il);
            tarihText.setText(SelectedModel.Model.Tarih);
            storageReference=storage.getReference()
                    .child("images").child(SelectedModel.Model.ResimId);
            storageReference.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imageView.setImageBitmap(bitmap);
                }
            });
           /* try {

                Cursor cursor = database.rawQuery("SELECT * FROM ifsalar WHERE id = ?",new String[] {String.valueOf(plakaId)});
                //id değeri,artId key'i ile gelen idArray'deki idIndex.
                //yani idIndex'de ki id ne ise onu al ve;
                int plakaIx = cursor.getColumnIndex("plaka");
                int konumIx = cursor.getColumnIndex("konum");
                int tarihIx = cursor.getColumnIndex("tarih");
                int imageIx = cursor.getColumnIndex("image");
                //diğer kolonlardaki indexleri de al.
                while (cursor.moveToNext()) {

                    plakaText.setText(cursor.getString(plakaIx));
                    konumText.setText(cursor.getString(konumIx));
                    tarihText.setText(cursor.getString(tarihIx));
                    //cursor ilerledikçe mevcut indexin tüm kolonlardaki değerini edittextlerde göster.

                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imageView.setImageBitmap(bitmap);
                    //byteArray şeklinde DB'de duran image'i bitmape çevirip ,imageview'de göster.


                }

                cursor.close();

            } catch (Exception e) {

            }*/


        }
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
                    (LocationListener) ifsaActivity.this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Longitude=""+location.getLongitude();
        Latitude=""+location.getLatitude();


        try {
            Geocoder geo = new Geocoder(ifsaActivity.this.getApplicationContext(), Locale.getDefault());
            double lati=Double.parseDouble(Latitude);
            double longu= Double.parseDouble(Longitude);
            List<Address> addresses = geo.getFromLocation(lati, longu, 1);
            if (addresses.isEmpty()) {
                Log.e("Adres","Waiting for Location");
            }
            else {
                if (addresses.size() > 0) {
                    Sokal=addresses.get(0).getThoroughfare();
                    Mahalle=addresses.get(0).getSubLocality();
                    Il=addresses.get(0).getAdminArea();
                    String adres = (addresses.get(0).getThoroughfare() + ", " + addresses.get(0).getSubLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                    Log.e("Adres",adres);
                    konumText.setText(adres);
                }
            }
        }
        catch (Exception e){

        }
        pd.dismiss();
    }

    @Override
    public void onProviderDisabled(String provider) {
        pd.dismiss();
        Toast.makeText(ifsaActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    public void selectImage(View view){


        Intent kamera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE); // Resim çekme isteği ve activity başlatılıp id'si tanımlandı
        startActivityForResult(kamera,33);

            /*if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }else{
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intentToGallery,2);
            }*/

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==33){
            try {
                selectedImage=(Bitmap)data.getExtras().get("data");//Çekilen resim id olarak bitmap şeklinde alındı ve imageview'e atandı
                ImageUri = data.getData();
                imageView.setImageBitmap(selectedImage);

            }
            catch (Exception e){
                Toast.makeText(this, "Resim Yüklenemedi", Toast.LENGTH_SHORT).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void save(View view){
        final  String rndkey= UUID.randomUUID().toString();



        if (selectedImage!=null && !plakaText.getText().toString().equals("") && !tarihText.getText().toString().equals("")
                && !Longitude.equals("") && !Latitude.equals("") ) {
            ByteArrayOutputStream stream =  new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG,100,stream);
            byte []b=stream.toByteArray();
            ifsaModel model= new ifsaModel();

            pd.setTitle("Yükleniyor ...");
            pd.show();
            model.Latitude=Latitude;
            model.Longitude=Longitude;
            model.ResimId=rndkey;
            model.Sokak=Sokal;
            model.Mahalle=Mahalle;
            model.Il=Il;
            model.Plaka=plakaText.getText().toString();
            model.Tarih=tarihText.getText().toString();




            StorageReference storageR= storageReference.child("images/"+ rndkey);
            storageR.putBytes(b).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();

                    String userId = mDatabase.push().getKey();
                    mDatabase.child(userId).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ifsaActivity.this, " Eklendi :) ", Toast.LENGTH_SHORT).show();
                            Intent intent= new Intent(ifsaActivity.this,MainActivity.class);
                            startActivity(intent);

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();

                    Toast.makeText(ifsaActivity.this, "Hata Oluştu"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double count=(100.00)*snapshot.getBytesTransferred()/snapshot.getTotalByteCount();
                    pd.setMessage("Yükleniyor " + (int)count + " %");
                }
            });
        }
        else {
            Toast.makeText(this, "Boş Alan bırakmayın", Toast.LENGTH_SHORT).show();
        }


    }



    public Bitmap makeSmallerImage(Bitmap image, int maximumSize) {//bir bitmap al,ve istenen max boyut.

        int width = image.getWidth();//bitmapin genişliği
        int height = image.getHeight();//ve yüksekliği

        float bitmapRatio = (float) width / (float) height;//genişliği yüksekliğe oranını bul.

        if (bitmapRatio > 1) {//oran 1 den büyükse width>height demektir.yani resim yatay.
            width = maximumSize;//genişliği istenen max boyuta getir.
            height = (int) (width / bitmapRatio);//max boyutu,orana böl,genişlikte aynı oranda küçülür.
        } else {//1'den büyük değilse resim dikey
            height = maximumSize;//yüksekliği istenen max boyuta getir.
            width = (int) (height * bitmapRatio);//genişliği aynı oranda arttır.
        }

        return Bitmap.createScaledBitmap(image,width,height,true);
        //bir bitmap alıp width ve heightini yeniden hesapladık,yeni değerleri
        //create scaled bitmap ile çağırılan bitmap dosyasının boyutları olarak ayarladık.
    }
}