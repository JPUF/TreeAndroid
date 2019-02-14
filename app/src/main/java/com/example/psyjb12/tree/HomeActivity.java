package com.example.psyjb12.tree;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class HomeActivity extends AppCompatActivity {
    public static final String TREE_SEARCH = "com.example.psyjb12.SEARCH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void searchTree(View view) {
        Intent intent = new Intent(this, SpeciesActivity.class);
        EditText editText = (EditText) findViewById(R.id.treeSearch);
        String searchTerm = editText.getText().toString();
        intent.putExtra(TREE_SEARCH, searchTerm);
        startActivity(intent);
    }

    public void goToSpeciesActivity(String scientificName) {
        Intent intent = new Intent(this, SpeciesActivity.class);
        intent.putExtra(TREE_SEARCH, scientificName);
        startActivity(intent);
    }

    public void getScientificFromVernacular(View view) {
        EditText editText = (EditText) findViewById(R.id.vernacularText);
        final String vernacularText = editText.getText().toString();
        Log.i("vernacular", vernacularText);

        new Thread() {
            public void run() {
                try {
                    String urlString = "http://api.gbif.org/v1/species/search?q="+vernacularText;//TODO need to format to %20 etc.
                    URL url = new URL(urlString);
                    URLConnection request = url.openConnection();
                    request.connect();

                    JsonParser jp = new JsonParser();
                    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                    JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                    JsonArray results= rootobj.get("results").getAsJsonArray();
                    String scientificName;
                    JsonElement result = results.get(0);
                    try{
                        scientificName = result.getAsJsonObject().get("canonicalName").toString();
                    } catch (NullPointerException e) {
                        scientificName = result.getAsJsonObject().get("scientificName").toString();
                    }
                    scientificName = scientificName.substring(1,scientificName.length()-1);
                    Log.i("vernacular", scientificName);

                    goToSpeciesActivity(scientificName);

                    /*
                    for (JsonElement result : results) {//To iterate through all search results. (needed in case the desired species isn't result #1)
                        try{
                            scientificName = result.getAsJsonObject().get("canonicalName").toString();
                        } catch (NullPointerException e) {
                            scientificName = result.getAsJsonObject().get("scientificName").toString();
                        }
                        Log.i("vernacular", scientificName);
                    }
                    */

                }catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.start();
    }
}
