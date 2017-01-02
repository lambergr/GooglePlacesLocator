package com.example.greg.lambertfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class GoogleMap extends ActionBarActivity { //Declare needed variables

    ArrayList<Places> places = new ArrayList<Places>();

    TextView test;

    Double myLat;

    Double myLng;

    Geocoder geocoder = null;

    LatLng myLatLng;

    LatLng placePos;

    Marker myMarker;

    View v;

    private com.google.android.gms.maps.GoogleMap theMap;

    boolean favorite;

    Bitmap myBitMap;

    ImageView imagePic;

    String imageString;

    ArrayList<Bitmap> bits = new ArrayList<Bitmap>();

    int xyz = 0;

    @Override
    
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        final Intent myLocalIntent = getIntent();  //Unbundle information pushed over from main activity

        Bundle myBundle = myLocalIntent.getExtras();

        places = myBundle.getParcelableArrayList("Places");

        myLat = myBundle.getDouble("myLat");

        myLng = myBundle.getDouble("myLng");

        Places place = places.get(0);

        new setUpMarkers().execute();

    }

    private class setUpMarkers extends AsyncTask<String, String, String> {// load the Google map

        ProgressDialog progressDialog = ProgressDialog.show(GoogleMap.this, "Loading", "May Take a Minute");

        @Override

        protected void onPreExecute(){
            
            super.onPreExecute();
            
        }

        protected String doInBackground(String... arguments){ // render the image to put in place of the defualt marker
            
            for(int z = 0; z < places.size(); z++) {

                Places place2 = places.get(z);

                imageString = place2.getIconUrl();


                try {
                    
                    URL url = new URL(imageString);
                    
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    
                    connection.setDoInput(true);
                    
                    connection.connect();
                    
                    InputStream input = connection.getInputStream();
                    
                    myBitMap = BitmapFactory.decodeStream(input);

                    bits.add(myBitMap);

                } catch (IOException e) {
                    // Log exception
                }

            }

            return("bye");
        }

        protected void onPostExecute(String result){ 
            
            markers(v);
            
            progressDialog.dismiss();
            
        }


    }

    public void markers(View view) { //add markers to the map, orient camera

        if (theMap == null) {
            
            theMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.theMap2)).getMap();
            
        }

        myLatLng = new LatLng(myLat, myLng);

        myMarker = theMap.addMarker(new MarkerOptions()
                .position(myLatLng)
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        theMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 14), 500, null);

        for (int i = 0; i < places.size(); i++) {
            
            Places place = places.get(i);

            Double placeLat = place.getLat();
            
            Double placeLng = place.getLng();

            imageString = place.getIconUrl();

            if (placeLat != null || placeLng != null || placePos != null) {

                myLatLng = new LatLng(placeLat, placeLng);

                theMap.addMarker(new MarkerOptions()
                        .position(myLatLng)
                        .title(place.getName()))
                        .setIcon(BitmapDescriptorFactory.fromBitmap(bits.get(i)));

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // Inflate the menu; this adds items to the action bar if it is present.
        
        getMenuInflater().inflate(R.menu.menu_google_map, menu);
        
        return true;
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            
            return true;
            
        }

        return super.onOptionsItemSelected(item);
    }
}
