package com.example.greg.lambertfinal;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Parcel;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.google.android.gms.location.LocationListener;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;



public class FinalMainActivity extends ActionBarActivity implements LocationListener {

    private LocationManager locationManager;
    private String provider;
    TextView latTxt;
    TextView lngTxt;
    ListView listView;
    Double lat2;
    Double lng2;
    JSONObject jsonobject;
    JSONArray jsonarray;
    String placesSearchStr;
    SQLiteDatabase db;

    String[] types = {"any", "airport", "aquarium", "atm", "night_club", "police", "food", "hospital", "lawyer", "library", "subway_station", "school", "park", "pharmacy"};


    ArrayList<Places> places;
    ArrayList<String> names;

    ListView placeView;

    ArrayAdapter<String> workTitle;

    Spinner placeSpin;

    Parcel in;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_main);
        latTxt = (TextView) findViewById(R.id.latTxt);
        //lngTxt = (TextView) findViewById(R.id.lngTxt);
        File storagePath = getApplication().getFilesDir();

        placeSpin = (Spinner) findViewById(R.id.placeSpin);

        placeView = (ListView) findViewById(R.id.placeView);

        String myDbPath = storagePath + "/" + "favPlaces";

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        placeSpin.setAdapter(adapter);

        try {

            db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);

        } catch (SQLiteException e) {


        }


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            System.out.println("Location not available");
        }


        placeSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(placeSpin.getSelectedItem() == "any"){

                    placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + Double.toString(lat2) + "," + Double.toString(lng2) + "&radius=500&key=AIzaSyDtafYuCYHM0TqKhhdBnphl3xQe3a8488I";

                    new DownloadJSON().execute();





                }

                else{


                    placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + Double.toString(lat2) + "," + Double.toString(lng2) + "&types=" +placeSpin.getSelectedItem()+  "&radius=500&key=AIzaSyDtafYuCYHM0TqKhhdBnphl3xQe3a8488I";
                    new DownloadJSON().execute();






                }


                // your code here
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //       Toast.makeText(getApplicationContext(), Double.toString(lng2), Toast.LENGTH_SHORT).show();

//        placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
//                "json?location="+lat2+","+lng2+
//                "&radius=1000&sensor=true" +
//                "&types=food|bar|store|museum|art_gallery"+
//                "&key=AIzaSyBfAF6-eEW3jiHwILIDK4pbCYBFKMH03K0";

        // placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&key=AIzaSyDtafYuCYHM0TqKhhdBnphl3xQe3a8488I";
        //placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + Double.toString(lat2) + "," + Double.toString(lng2) + "&radius=500&key=AIzaSyDtafYuCYHM0TqKhhdBnphl3xQe3a8488I";
//&radius=500&types=food&name=cruise - rigth after coordinates and before &key
        //new DownloadJSON().execute();


    }



    private class DownloadJSON extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {

            places = new ArrayList<Places>();

            names = new ArrayList<String>();


            jsonobject = JSONfunctions.getJSONfromURL(placesSearchStr);


            try {

                jsonarray = jsonobject.getJSONArray("results");
                for (int i = 0; i < jsonarray.length(); i++) {

                    JSONObject geoJson = jsonarray.getJSONObject(i);

                    JSONObject geometry = geoJson.optJSONObject("geometry");

                    JSONObject location = geometry.optJSONObject("location");


                    Places place = new Places();

                    place.setLat(location.optString("lat"));
                    place.setLng(location.optString("lng"));

                    if (geoJson.has("icon")) {

                        place.setIconUrl(geoJson.optString("icon"));



                    }
                    else{

                        place.setIconUrl("http://maps.gstatic.com/mapfiles/place_api/icons/geocode-71.png");

                    }




                    String name = geoJson.optString("name");


                    place.setName(name);

                    names.add(name);

                    if (geoJson.has("opening_hours")) {

                        JSONObject openHours = geoJson.optJSONObject("opening_hours");

                        place.setOperating(openHours.optString("open_now"));

                    }


                    places.add(place);


                }


            } catch (Exception e) {


            }


            return null;

        }


        @Override
        protected void onPostExecute(Void args) {

            //int z = places.size();

            //Places place2 = places.get(0);

            //latTxt.setText(Integer.toString(z));

            //lngTxt.setText(place2.getOperating());

            workTitle = new ArrayAdapter<String>(FinalMainActivity.this, android.R.layout.simple_list_item_checked, names);

            placeView.setAdapter(workTitle);

            if(workTitle.isEmpty()){

                Toast.makeText(getApplicationContext(), "Nothing Near You", Toast.LENGTH_LONG).show();



            }


        }

    }



    /* Remove the locationlistener updates when Activity is paused */

    public void onLocationChanged(Location location) {
       Double lat = location.getLatitude();
       Double lng = location.getLongitude();

        //lat2 = lat;
        //lng2 = lng;

        lat2 = -33.8670522;
        lng2 = 151.1957362;


        //Toast.makeText(getApplicationContext(), lat + "----" + lng, Toast.LENGTH_LONG).show();
        Log.i("", "Lattitude:" + lat);
        Log.i("", "Longitude:" + lng);
    }


    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }


    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }


    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {


        super.onCreateContextMenu(menu, v, menuInfo);
        populateMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        populateMenu(menu);
        return super.onCreateOptionsMenu(menu);


    }

    public void populateMenu(Menu menu) {
        int groupId = 0;
        int order = 0;

        menu.add(groupId, 1, ++order, "Go to Selected in Maps");
        menu.add(groupId, 2, ++order, "Add to Favorites");
        menu.add(groupId, 3, ++order, "Go to Favorites");


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return applyMenuOption((item));
    }


    public boolean applyMenuOption(MenuItem item) {
        int itemid = item.getItemId();


        if (itemid == 1) {


            ArrayList<Places> bundleList = new ArrayList<Places>();


            int cntChoice = placeView.getCount();

            //String checked = "";

            //String unchecked = "";

            SparseBooleanArray sparseBooleanArray = placeView.getCheckedItemPositions();

            for (int i = 0; i < cntChoice; i++) {

                if (sparseBooleanArray.get(i) == true) {
                    //checked += placeView.getItemAtPosition(i).toString() + "\n";
                    bundleList.add(places.get(i));
                } else if (sparseBooleanArray.get(i) == false) {
                    //unchecked+= placeView.getItemAtPosition(i).toString() + "\n";
                }

            }

            if (bundleList.size() == 0) {

                Toast.makeText(getApplicationContext(), "Please Select Place(s)", Toast.LENGTH_LONG).show();


            } else {


                Intent abstractList = new Intent(FinalMainActivity.this, GoogleMap.class);

                Bundle myDataBundle = new Bundle();

                myDataBundle.putParcelableArrayList("Places", bundleList);

                myDataBundle.putDouble("myLat", lat2);
                myDataBundle.putDouble("myLng", lng2);

                abstractList.putExtras(myDataBundle);

                //Places place2 = bundleList.get(0);

                //latTxt.setText(Double.toString(place2.getLng()));

                startActivityForResult(abstractList, 101);

            }


            return true;
        }


        if (itemid == 2) {

            ArrayList<Places> bundleList = new ArrayList<Places>();


            int cntChoice = placeView.getCount();

            SparseBooleanArray sparseBooleanArray = placeView.getCheckedItemPositions();

            for (int i = 0; i < cntChoice; i++) {

                if (sparseBooleanArray.get(i) == true) {
                    //checked += placeView.getItemAtPosition(i).toString() + "\n";
                    bundleList.add(places.get(i));
                } else if (sparseBooleanArray.get(i) == false) {
                    //
                }
            }


            if (bundleList.size() == 0) {

                Toast.makeText(getApplicationContext(), "Please Select Place(s)", Toast.LENGTH_LONG).show();


            } else {

                for (int x = 0; x < bundleList.size(); x++) {

                    Places sqlPlace = bundleList.get(x);

                    String name = sqlPlace.getName();

                    String sqlLat = Double.toString(sqlPlace.getLat());
                    String sqlLng = Double.toString(sqlPlace.getLng());
                    String pic = sqlPlace.getIconUrl();

                  //latTxt.setText(sqlLat);



                    String name2 = name;

                    name = name.replaceAll("\\s+", "");


                    try {
//                        db.execSQL("create table if not exists " + name + " ("
//                                + " recID integer PRIMARY KEY autoincrement, "
//                                + "name text, "
//                                + "lat text, "
//                                + "lng text);");

                        String sql = "CREATE TABLE IF NOT EXISTS " + name +
                                "(ID INTEGER PRIMARY KEY autoincrement," +
                                " NAME           TEXT    NOT NULL, " +
                                " LAT            TEXT     NOT NULL, " +
                                " LNG       TEXT     NOT NULL, " +
                                " PIC        TEXT     NOT NULL)";



                        db.execSQL(sql);


                    } catch (SQLiteException e) {


                        Toast.makeText(getApplicationContext(), "Error, Place in Unfreindly SQLLite Format", Toast.LENGTH_LONG).show();


                    }


                    try {
                        int id = 1;

                        db.execSQL("INSERT INTO " + name + " (NAME, LAT, LNG, PIC) VALUES ('"+name2+"','"+sqlLat+"', '"+sqlLng+"', '"+pic+"')");


                    } catch (SQLiteException e) {

                        Toast.makeText(getApplicationContext(), "Error, Unfreindly SQLLite Format", Toast.LENGTH_LONG).show();


                    }




                }


            }
        }

        if (itemid == 3) {

            Intent abstractList = new Intent(FinalMainActivity.this, favorites.class);

            Bundle myDataBundle = new Bundle();


            myDataBundle.putDouble("myLat", lat2);
            myDataBundle.putDouble("myLng", lng2);

            abstractList.putExtras(myDataBundle);



            startActivityForResult(abstractList, 102);


        }

        return true;
    }
}