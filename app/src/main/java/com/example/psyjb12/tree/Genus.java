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


//Got to the point of adding new species to the children thread. Need to parse results from genus api call.


public class Genus {
    public ArrayList<String> child_IDs;

    public Genus(String genusID) {
        this.child_IDs = setChildren(genusID);
    }



    private class ChildrenThread implements Runnable {
        private volatile ArrayList<String> child_IDs = new ArrayList<>();
        String gbif_ID;
        ChildrenThread(String genusID) {
            this.gbif_ID = genusID;
        }

        @Override
        public void run() {
            try {
                String urlString = "http://api.gbif.org/v1/species/"+URLEncoder.encode(this.gbif_ID, "UTF-8")+"/children?limit=200";
                URL url = new URL(urlString);
                URLConnection request = url.openConnection();
                request.connect();

                JsonParser jp = new JsonParser();
                JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                JsonObject rootObj = root.getAsJsonObject(); //May be an array, may be an object.
                JsonArray results = rootObj.get("results").getAsJsonArray();
                String childID;
                for (JsonElement child : results) {
                    childID = child.getAsJsonObject().get("speciesKey").getAsString();
                    child_IDs.add(childID);
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        private ArrayList<String> getChildren() {
            return child_IDs;
        }
    }

    private ArrayList<String> setChildren(String genusID) {
        ChildrenThread children_thread = new ChildrenThread(genusID);
        Thread t = new Thread(children_thread);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return children_thread.getChildren();
    }
}
