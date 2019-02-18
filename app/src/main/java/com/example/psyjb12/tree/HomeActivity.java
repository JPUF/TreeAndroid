package com.example.psyjb12.tree;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.net.URLEncoder;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    public static final String TREE_SEARCH = "com.example.psyjb12.SEARCH";

    LinearLayout search_results_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        search_results_ll = (LinearLayout) findViewById(R.id.searchResultsLayout);
    }

    public void searchTree(View view) {
        Intent intent = new Intent(this, SpeciesActivity.class);
        EditText editText = (EditText) findViewById(R.id.treeSearch);
        String searchTerm = editText.getText().toString();
        //TODO this needs to call /species/search?q=searchTerm, then display the results. This should then work for both vernacular and scientific names.

        displaySearchResults(searchTerm);
    }

    public void goToSpeciesActivity(String scientificName) {
        Intent intent = new Intent(this, SpeciesActivity.class);
        intent.putExtra(TREE_SEARCH, scientificName);
        startActivity(intent);
    }

    public boolean isScientificName(String name) {
        return false;
    }

    public void goToYew(View view) {
        Intent intent = new Intent(this, SpeciesActivity.class);
        intent.putExtra(TREE_SEARCH, "Taxus baccata");
        startActivity(intent);
    }

    public void goToBirch(View view) {
        Intent intent = new Intent(this, SpeciesActivity.class);
        intent.putExtra(TREE_SEARCH, "Betula pendula");
        startActivity(intent);
    }

    public void displaySearchResults(final String searchTerm) {
        final String search = searchTerm;

        new Thread() {
            public void run() {
                try {
                    String urlString = "http://api.gbif.org/v1/species/search?q=" + URLEncoder.encode(searchTerm,"UTF-8");
                    URL url = new URL(urlString);
                    URLConnection request = url.openConnection();
                    request.connect();

                    JsonParser jp = new JsonParser();
                    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                    JsonObject rootObject = root.getAsJsonObject(); //May be an array, may be an object.
                    JsonArray results= rootObject.get("results").getAsJsonArray();
                    String scientificName;

                    //TODO handle genus results (e.g. search for 'Birch'/'Betula'. Should be able to go straight to Betula genus page.)

                    ArrayList<String> uniqueNames = new ArrayList<>();
                    for (JsonElement result : results) {//To iterate through all search results. (needed in case the desired species isn't result #1)
                        try{
                            scientificName = result.getAsJsonObject().get("canonicalName").toString();
                            scientificName = scientificName.substring(1,scientificName.length()-1);
                            if(!uniqueNames.contains(scientificName)) {//if current name is unique.
                                uniqueNames.add(scientificName);
                                Log.i("scientificName", "canon: "+scientificName);
                            }

                        } catch (NullPointerException e) {
                            scientificName = result.getAsJsonObject().get("scientificName").toString();
                            scientificName = scientificName.substring(1,scientificName.length()-1);
                            if(!uniqueNames.contains(scientificName)) {//if current name is unique.
                                uniqueNames.add(scientificName);
                                Log.i("scientificName", " sciN: "+scientificName);
                            }
                        }
                    }

                    for (String name : uniqueNames) {
                        final String finalName = name;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                search_results_ll.addView(createTreeCard(finalName));
                            }
                        });
                    }

                }catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.start();

    }

    public void getScientificFromVernacular(String name) {
        final String vernacularText = name;
        new Thread() {
            public void run() {
                try {
                    String urlString = "http://api.gbif.org/v1/species/search?q=" + URLEncoder.encode(vernacularText,"UTF-8");
                    URL url = new URL(urlString);
                    URLConnection request = url.openConnection();
                    request.connect();

                    JsonParser jp = new JsonParser();
                    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                    JsonObject rootObject = root.getAsJsonObject(); //May be an array, may be an object.
                    JsonArray results= rootObject.get("results").getAsJsonArray();
                    String scientificName;
                    /*
                    JsonElement result = results.get(0);
                    try{
                        scientificName = result.getAsJsonObject().get("canonicalName").toString();
                    } catch (NullPointerException e) {
                        scientificName = result.getAsJsonObject().get("scientificName").toString();
                    }
                    scientificName = scientificName.substring(1,scientificName.length()-1);
                    Log.i("vernacular", scientificName);
                    */
                    //TODO needs a more robust system to get the appropriate result. Preferably displaying search results for the user to choose.

                    for (JsonElement result : results) {//To iterate through all search results. (needed in case the desired species isn't result #1)
                        try{
                            scientificName = result.getAsJsonObject().get("canonicalName").toString();
                            scientificName = scientificName.substring(1,scientificName.length()-1);
                            Log.i("scientificName", scientificName);
                            goToSpeciesActivity(scientificName);
                        } catch (NullPointerException e) {
                            scientificName = result.getAsJsonObject().get("scientificName").toString();
                            scientificName = scientificName.substring(1,scientificName.length()-1);
                            goToSpeciesActivity(scientificName);
                        }
                    }
                }catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.start();
    }

    public CardView createTreeCard(String name) {
        Context context = getApplicationContext();

        CardView cv = new CardView(context);
        ViewGroup.LayoutParams cvParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cv.setLayoutParams(cvParams);
        cv.setPadding(0,2,0,0);

        LinearLayout ll = new LinearLayout(context);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.setLayoutParams(llParams);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        ImageView iv = new ImageView(context);
        iv.setImageDrawable(getBaseContext().getDrawable(R.drawable.yew_fruit));
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        iv.setLayoutParams(ivParams);

        TextView tv = new TextView(context);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(tvParams);
        tv.setText(name);

        ll.addView(iv);
        ll.addView(tv);
        cv.addView(ll);

        return cv;
    }
}
