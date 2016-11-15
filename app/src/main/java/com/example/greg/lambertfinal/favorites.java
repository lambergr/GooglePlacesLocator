package com.example.greg.lambertfinal;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class favorites extends ActionBarActivity {

    SQLiteDatabase db;
    ArrayAdapter<String> ad;
    List<String> nameList;

    ListView nameView;

    int bigPosition;

    TextView test;

    Double myLat;
    Double myLng;

    String placeLat;
    String placeLng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        nameView = (ListView) findViewById(R.id.nameList);
        test = (TextView) findViewById(R.id.testText);

        File storagePath = getApplication().getFilesDir();

        String myDbPath = storagePath + "/" + "favPlaces";

        nameList = new ArrayList<String>();


        final Intent myLocalIntent = getIntent();

        Bundle myBundle = myLocalIntent.getExtras();

        myLat = myBundle.getDouble("myLat");

        myLng = myBundle.getDouble("myLng");


        try {

            db = SQLiteDatabase.openDatabase(myDbPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);


            int i = 1;
            Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name != 'android_metadata' AND name != 'sqlite_sequence'", null);

            c.moveToFirst();
            while (!c.isAfterLast()) {
                nameList.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
                i++;
            }
            c.close();
            //db.close();


        } catch (SQLiteException e) {


        }

        ad = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, nameList);


        nameView.setAdapter(ad);
        nameView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        nameView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                bigPosition = position;


            }
        });


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
        menu.add(groupId, 2, ++order, "Delete From Favorites");


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return applyMenuOption((item));
    }


    public boolean applyMenuOption(MenuItem item) {
        int itemid = item.getItemId();





        if (itemid == 1) {

            if (nameView.getAdapter().isEmpty()) {

                Toast.makeText(getApplicationContext(), "There are no Favorites", Toast.LENGTH_SHORT).show();


            } else {


                ArrayList<String> tester = new ArrayList<String>();

                String name = (String) nameView.getAdapter().getItem(bigPosition);


                try {

                    int i = 1;
                    Cursor c = db.rawQuery("SELECT * FROM " + name, null);
                    c.moveToFirst();

                    tester.add(c.getString(c.getColumnIndex("LAT")));
                    c.moveToFirst();
                    tester.add(c.getString(c.getColumnIndex("LNG")));
                    c.moveToFirst();
                    tester.add(c.getString(c.getColumnIndex("NAME")));
                    c.moveToFirst();
                    tester.add(c.getString(c.getColumnIndex("PIC")));





                    c.close();
                    //db.close();


                } catch (SQLiteException e) {


                }


                //test.setText(tester.get(1));

                Double placeLat = Double.parseDouble(tester.get(0));

                Double placeLng = Double.parseDouble(tester.get(1));

                String theName = tester.get(2);

                String thePic = tester.get(3);

                Intent abstractList = new Intent(favorites.this, GoogleMapFav.class);

                Bundle myDataBundle = new Bundle();


                myDataBundle.putDouble("placeLat", placeLat);
                myDataBundle.putDouble("placeLng", placeLng);

                myDataBundle.putDouble("myLat", myLat);
                myDataBundle.putDouble("myLng", myLng);

                myDataBundle.putString("name", theName);

                myDataBundle.putString("pic", thePic);



                abstractList.putExtras(myDataBundle);


                startActivityForResult(abstractList, 103);


            }
        }

        if(itemid == 2){



            if (nameList.isEmpty()) {
                Toast.makeText(getApplicationContext(), "You are attempting to delete nothing", Toast.LENGTH_SHORT).show();
            } else if (bigPosition == -1 || bigPosition == nameList.size()) {
                Toast.makeText(getApplicationContext(), "Select an item", Toast.LENGTH_SHORT).show();
            } else {

                String name = (String) nameView.getAdapter().getItem(bigPosition);


                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                nameList.remove(nameList.get(bigPosition));
                nameView.setAdapter(ad);
               // enterText.setText("");
                try {

                    db.execSQL("drop table " + name);


                } catch (SQLiteException e) {

                    Toast.makeText(getApplicationContext(), "Delete Aborted", Toast.LENGTH_SHORT).show();

                }
                Toast.makeText(getApplicationContext(), "Poof! Item " + (bigPosition + 1) + " is gone!", Toast.LENGTH_SHORT).show();
                while (bigPosition < nameList.size()) {
                    nameList.set(bigPosition, nameList.get(bigPosition));
                    bigPosition++;


                }
                bigPosition = nameList.size();


            }





        }

        return true;


    }

}
