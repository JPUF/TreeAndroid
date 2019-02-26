package com.example.psyjb12.tree;

import android.os.HandlerThread;
import android.util.Log;

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

public class Species {
    public String scientific_name;
    public String GBIF_id;
    public ArrayList<String> vernacular_names;
    public String general_notes;
    public String characteristics;

    public Species(String scientific_name) {
        this.scientific_name = scientific_name;
        this.GBIF_id = setGBIF_id(scientific_name);
    }

    private class IDThread implements Runnable {
        private volatile String GBIF_id;
        String scientific_name;
        IDThread(String scientific_name) {
            this.scientific_name = scientific_name;
        }

        @Override
        public void run() {
            try {
                String urlString = "http://api.gbif.org/v1/species/search?q='"+ URLEncoder.encode(this.scientific_name, "UTF-8")+"'&limit=1";
                URL url = new URL(urlString);
                URLConnection request = url.openConnection();
                request.connect();

                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                JsonArray nameResults = rootobj.get("results").getAsJsonArray();
                JsonElement topResult  = nameResults.getAsJsonArray().get(0);
                JsonElement nameElement = topResult.getAsJsonObject().get("nubKey");
                this.GBIF_id = nameElement.getAsString();
            }catch (IOException e) {
                e.printStackTrace();
            }

        }

        public String getGBIF_id() {
            return GBIF_id;
        }
    }

    private String setGBIF_id(String scientific_name) {
        IDThread id_thread = new IDThread(scientific_name);
        Thread t = new Thread(id_thread);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return id_thread.getGBIF_id();
    }
}
