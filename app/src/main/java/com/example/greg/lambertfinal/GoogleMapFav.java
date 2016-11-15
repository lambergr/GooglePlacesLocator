package com.example.greg.lambertfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class GoogleMapFav extends ActionBarActivity {

    Double myLat;

    Double myLng;

    Double placeLat;

    Double placeLng;

    String placeName;

    String imageString;


    LatLng myLatLng;

    LatLng placePos;

    View v;

    Bitmap myBitMap;

    ArrayList<Bitmap> bits = new ArrayList<Bitmap>();



    private com.google.android.gms.maps.GoogleMap theMap;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map_fav);

        final Intent myLocalIntent = getIntent();

        Bundle myBundle = myLocalIntent.getExtras();

        myLat = myBundle.getDouble("myLat");

        myLng = myBundle.getDouble("myLng");

        placeLat = myBundle.getDouble("placeLat");

        placeLng = myBundle.getDouble("placeLng");

        placeName = myBundle.getString("name");

        imageString = myBundle.getString("pic");



        new setUpMarkers().execute();



    }


    private class setUpMarkers extends AsyncTask<String, String, String> {

        ProgressDialog progressDialog = ProgressDialog.show(GoogleMapFav.this, "Loading", "May Take a Minute");


        @Override

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... arguments) {


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


            return ("bye");
        }

        protected void onPostExecute(String result) {
            markers(v);
            progressDialog.dismiss();
        }
    }





    public void markers(View view) {

        if (theMap == null) {
            theMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.theMap)).getMap();
        }

        myLatLng = new LatLng(myLat, myLng);

        placePos = new LatLng(placeLat, placeLng);

        theMap.addMarker(new MarkerOptions()
                .position(myLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title("Your Location"));

        theMap.addMarker(new MarkerOptions()
                .position(placePos)
                .title(placeName))
                .setIcon(BitmapDescriptorFactory.fromBitmap(bits.get(0)));



        theMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placePos, 14), 5000, null);


            }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_google_map_fav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
