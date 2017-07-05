package com.example.hp.musicplayer3;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {


    private static final int MY_PERMISSION_REQYEST=1;
    ArrayList<String> arrayList=new ArrayList<String>();
    ArrayList<File> allSongs=new ArrayList<File>();
    ArrayList<Bitmap> allAlbums=new ArrayList<Bitmap>();
    ListView listView;
    ArrayAdapter<String> adapter;
    static MediaPlayer mp;
    ImageView albumarts;
    TextView albumid;





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
            new loadingSongs().execute();
        }
    }

    public class loadingSongs extends AsyncTask<File,Void,Void> {

        private ProgressDialog dialog;

        @Override
        protected Void doInBackground(File... voids) {
            getmusic();
            return null;
        }

        @Override
        protected void onPreExecute() {
            dialog=new ProgressDialog(MainActivity.this);
            dialog.setTitle("Loading");
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            dostuff();
        }

    }




    public void dostuff()
    {
        listView=(ListView)findViewById(R.id.listview);
        albumarts=(ImageView)findViewById(R.id.albumarts);
        albumid=(TextView)findViewById(R.id.albumid);
        adapter=new ArrayAdapter<>(getApplicationContext(),R.layout.list,R.id.text,arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri u=Uri.parse(allSongs.get(i).toString());
                mp= MediaPlayer.create(getApplicationContext(),u);
                mp.start();
                albumarts.setImageBitmap(allAlbums.get(i));
            }
        });
    }


    public void getmusic()
    {
        ContentResolver cr=getContentResolver();
        Uri songUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri albumUri=MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;

        final Uri ART_CONTENT_URI=Uri.parse("content://media/external/audio/albumart");

        Uri artUri;

        Cursor songCursor = cr.query(songUri,null,null,null,null);
        Cursor albumCursor = cr.query(albumUri,null,null,null,null);

        if(songCursor!=null && albumCursor!=null && songCursor.moveToFirst() && albumCursor.moveToFirst())
        {
            int songTitle=songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist=songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songPosition=songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);


            do{
                long songAlbumid=songCursor.getLong(songCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));
                artUri= ContentUris.withAppendedId(ART_CONTENT_URI,songAlbumid);
                Bitmap album_art_bitmap=null;
                try{

                    album_art_bitmap=(MediaStore.Images.Media.getBitmap(getContentResolver(),artUri));

                }
                catch (Exception e){
                    e.printStackTrace();
                }

                String currentTitle = songCursor.getString(songTitle);
                String currentArtist= songCursor.getString(songArtist);
                arrayList.add(currentTitle + "\n" + currentArtist);
                File singlesong=new File(songCursor.getString(songPosition));

                allSongs.add(singlesong);
                allAlbums.add(album_art_bitmap);


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
                       new loadingSongs().execute();
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
