package com.example.psyjb12.tree;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    TextView text;
    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        text = (TextView) findViewById(R.id.text);
        b = (Button) findViewById(R.id.vern);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVernacular();
            }
        });
    }

    public void toSpecies(View view) {
        Intent intent = new Intent(this, SpeciesActivity.class);
        startActivity(intent);
    }

    private void getVernacular() {
        new Thread() {
            public void run() {
                try {
                    String urlString = "http://api.gbif.org/v1/species/2686212/vernacularNames";
                    URL url = new URL(urlString);
                    URLConnection request = url.openConnection();
                    request.connect();

                    JsonParser jp = new JsonParser();
                    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                    JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                    JsonArray nameResults = rootobj.get("results").getAsJsonArray();
                    JsonElement topResult  = nameResults.getAsJsonArray().get(0);
                    JsonElement nameElement = topResult.getAsJsonObject().get("vernacularName");
                    final String name = nameElement.getAsString();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            text.setText(name);
                        }
                    });

                }catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.start();

    }

}
