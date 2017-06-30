package com.example.hp.musicplayer3;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQYEST=1;
    ArrayList<String> arrayList;
    ListView listView;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQYEST);
            }
            else {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},MY_PERMISSION_REQYEST);
            }
        }
        else {
            dostuff();
        }
    }
    public void dostuff()
    {
        listView=(ListView)findViewById(R.id.listview);
        arrayList=new ArrayList<>();
        getmusic();
        adapter=new ArrayAdapter<>(getApplicationContext(),R.layout.list,R.id.text,arrayList);
        listView.setAdapter(adapter);
    }
    public void getmusic()
    {
        ContentResolver cr=getContentResolver();
        Uri songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = cr.query(songUri,null,null,null,null);
        if(songCursor!=null && songCursor.moveToFirst())
        {
            int songTitle=songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist=songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do{
                String currentTitle = songCursor.getString(songTitle);
                String currentArtist= songCursor.getString(songArtist);
                arrayList.add(currentTitle + "\n" + currentArtist);
            }while (songCursor.moveToNext());
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
       switch (requestCode)
       {
           case MY_PERMISSION_REQYEST:{
               if (grantResults.length> 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                   if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)==
                           PackageManager.PERMISSION_GRANTED){
                       Toast.makeText(this,"permission granted",Toast.LENGTH_SHORT).show();
                       dostuff();
                   }
               }
               else
               {
                   Toast.makeText(this,"not granted",Toast.LENGTH_SHORT).show();
                   finish();
               }
           }
       }
    }
}
