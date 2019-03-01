package com.example.psyjb12.tree;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

public class HomeActivity extends AppCompatActivity {
    public static final String TREE_SEARCH = "com.example.psyjb12.SEARCH";

    LinearLayout search_results_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        search_results_ll = (LinearLayout) findViewById(R.id.searchResultsLayout);
        Species yew = new Species("Acer palmatum");
        Log.i("id_test", "yew ID = " + yew.GBIF_id);
        Log.i("id_test", "Yew vernacular names = " + yew.vernacular_names.toString());
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

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            search_results_ll.removeAllViews();//remove any previous search results.
                        }
                    });

                    String urlString = "http://api.gbif.org/v1/species/search?q=" + URLEncoder.encode(searchTerm,"UTF-8")+"&rank=SPECIES&highertaxon_key=6&limit=5";
                    URL url = new URL(urlString);
                    URLConnection request = url.openConnection();
                    request.connect();

                    JsonParser jp = new JsonParser();
                    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                    JsonObject rootObject = root.getAsJsonObject(); //May be an array, may be an object.
                    JsonArray results= rootObject.get("results").getAsJsonArray();
                    String scientificName;

                    //TODO handle genus results (e.g. search for 'Birch'/'Betula'. Should be able to go straight to Betula genus page.)

                    ArrayList<String> uniqueNames = new ArrayList<>();//a list of valid and unique scientific names.
                    for (JsonElement result : results) {//To iterate through all search results. (needed in case the desired species isn't result #1)
                        try{
                            scientificName = result.getAsJsonObject().get("canonicalName").toString();
                            scientificName = scientificName.substring(1,scientificName.length()-1);
                            if(!uniqueNames.contains(scientificName)) {//if current name is unique.
                                if(result.getAsJsonObject().get("kingdom").toString().contains("Plantae")) { //if name belongs to a plant.
                                    uniqueNames.add(scientificName);
                                    Log.i("scientificName", "canon: " + scientificName);
                                    Log.i("scientificName", "kingdom: " + result.getAsJsonObject().get("kingdom").toString());
                                }
                            }
                        } catch (NullPointerException e) {
                            scientificName = result.getAsJsonObject().get("scientificName").toString();
                            scientificName = scientificName.substring(1,scientificName.length()-1);
                            if(!uniqueNames.contains(scientificName)) {//if current name is unique.
                                uniqueNames.add(scientificName);
                                Log.i("scientificName", " sciN: " + scientificName);
                            }
                        }
                    }

                    for (String name: uniqueNames) {
                        final Species tree = new Species(name);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                search_results_ll.addView(createTreeCard(tree.scientific_name, tree.vernacular_names));
                            }
                        });
                    }
                }catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.start();

    }

    public CardView createTreeCard(String name, ArrayList<String> vernacularNames) {
        String allNames;
        if(!vernacularNames.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String n : vernacularNames) {
                sb.append(n);
                sb.append(", ");
            }
            allNames = sb.toString();
            if (allNames.length() >= 3)
                allNames = allNames.substring(0, allNames.length() - 2);
        } else {
            allNames = "";
        }
        Context context = getApplicationContext();
        CardView cv = new CardView(context);
        ViewGroup.LayoutParams cvParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cv.setLayoutParams(cvParams);
        cv.setPadding(0,2,0,0);

        LinearLayout outer_layout = new LinearLayout(context);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        outer_layout.setLayoutParams(llParams);
        outer_layout.setOrientation(LinearLayout.HORIZONTAL);

        ImageView iv = new ImageView(context);
        iv.setImageDrawable(getBaseContext().getDrawable(R.drawable.yew_fruit));
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        iv.setLayoutParams(ivParams);

        LinearLayout inner_layout = new LinearLayout(context);
        inner_layout.setLayoutParams(llParams);
        inner_layout.setOrientation(LinearLayout.VERTICAL);

        TextView title_tv = new TextView(context);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        title_tv.setLayoutParams(tvParams);
        title_tv.setPadding(4, 0, 0, 0);
        title_tv.setTypeface(title_tv.getTypeface(), Typeface.BOLD);
        title_tv.setText(name);

        TextView vernacular_tv = new TextView(context);
        vernacular_tv.setLayoutParams(tvParams);
        vernacular_tv.setPadding(8, 0, 0 ,0);
        vernacular_tv.setTextSize(12);
        vernacular_tv.setText(allNames);

        outer_layout.addView(iv);
        inner_layout.addView(title_tv);
        inner_layout.addView(vernacular_tv);
        outer_layout.addView(inner_layout);
        cv.addView(outer_layout);

        final String finalName = name;
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSpeciesActivity(finalName);
            }
        });

        return cv;
    }

}
