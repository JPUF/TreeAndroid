package com.example.psyjb12.tree;

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
        this.GBIF_id = setGBIFid(scientific_name);
        this.vernacular_names = setVernacularNames(this.GBIF_id);
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

        private String getGBIF_id() {
            return GBIF_id;
        }
    }

    private String setGBIFid(String scientific_name) {
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

    private class VernacularThread implements Runnable {
        private volatile ArrayList<String> vernacularNames;
        String id;
        VernacularThread(String gbif_id) { this.id = gbif_id; }

        @Override
        public void run() {
            try {
                String urlString = "http://api.gbif.org/v1/species/" + URLEncoder.encode(id, "UTF-8") + "/vernacularNames";
                URL url = new URL(urlString);
                URLConnection request = url.openConnection();
                request.connect();
                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject rootobj = root.getAsJsonObject();
                JsonArray results = rootobj.get("results").getAsJsonArray();

                ArrayList<String> vNames = new ArrayList<>();
                String vernacularName;
                for (JsonElement vResult : results) {
                    try {
                        if (vResult.getAsJsonObject().get("language").toString().contains("eng")) {//if vernacular name is in English.
                            vernacularName = vResult.getAsJsonObject().get("vernacularName").toString();
                            vernacularName = vernacularName.substring(1, vernacularName.length() - 1);
                            if(!vNames.contains(vernacularName)) {//if unique
                                vNames.add(vernacularName);
                            }
                        } //else no english name.
                    } catch (NullPointerException e) {
                        //Log.i("vernacular", "      langauge is null");
                        e.printStackTrace();
                    }
                }
                setVernacularNames(vNames);
            } catch (Exception e ){
                e.printStackTrace();
            }
        }

        private void setVernacularNames(ArrayList<String> names) { this.vernacularNames = names; }
        private ArrayList<String> getVernacularNames() { return vernacularNames; }
    }

    private ArrayList<String> setVernacularNames(String gbif_id) {
        VernacularThread v_thread = new VernacularThread(gbif_id);
        Thread t = new Thread(v_thread);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return v_thread.getVernacularNames();
    }
}
