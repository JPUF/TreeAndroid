package com.example.psyjb12.tree;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SpeciesActivity extends AppCompatActivity {

    TextView scientific_name_tv;
    TextView notes_tv;
    TextView characteristics_tv;
    ImageView dist0_iv;
    ImageView dist1_iv;
    ImageView tree_img1_iv;
    ImageView tree_img2_iv;
    ImageView tree_img3_iv;

    String scientific_name = new String();
    String GBIF_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species);

        Intent intent = getIntent();
        this.scientific_name = intent.getStringExtra(HomeActivity.TREE_SEARCH);
        //String search_name = intent.getStringExtra(HomeActivity.TREE_SEARCH);

        scientific_name_tv = (TextView) findViewById(R.id.scientific_name);
        notes_tv = (TextView) findViewById(R.id.notes);
        characteristics_tv = (TextView) findViewById(R.id.characteristics);
        dist0_iv = (ImageView) findViewById(R.id.dist0);
        dist1_iv = (ImageView) findViewById(R.id.dist1);
        tree_img1_iv = (ImageView) findViewById(R.id.treeImage1);
        tree_img2_iv = (ImageView) findViewById(R.id.treeImage2);
        tree_img3_iv = (ImageView) findViewById(R.id.treeImage3);

        setGBIF_id(this.scientific_name);//TODO probably won't work when we're not using the hardcoded scientific name.
        setNotes(this.scientific_name);
        setTreeImages(this.scientific_name);
    }

    private void setID(String id) {
        this.GBIF_id = id;
        setScientificName(id);
        setDistribution(id);
    }

    private void setDistribution(String id) {
        downloadSetImage(id, "0");
        downloadSetImage(id, "1");
    }

    private void downloadSetImage(String id, final String dist){
        final String urlString = "https://api.gbif.org/v2/map/occurrence/density/0/"+dist+"/0@1x.png?style=orange.marker&srs=EPSG:4326&taxonKey="+id;
        new Thread() {
            public void run() {
                try {
                    InputStream is = (InputStream) new URL(urlString).getContent();
                    final Drawable d = Drawable.createFromStream(is, "src");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(dist.equals("0"))
                                dist0_iv.setImageDrawable(d);
                            else
                                dist1_iv.setImageDrawable(d);
                        }
                    });
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setGBIF_id(final String scientific_name) {
        final String scientificName = scientific_name;
        new Thread() {
            public void run() {
                try {
                    String urlString = "http://api.gbif.org/v1/species/search?q='"+ URLEncoder.encode(scientificName, "UTF-8")+"'&limit=1";
                    URL url = new URL(urlString);
                    URLConnection request = url.openConnection();
                    request.connect();

                    JsonParser jp = new JsonParser();
                    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                    JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                    JsonArray nameResults = rootobj.get("results").getAsJsonArray();
                    JsonElement topResult  = nameResults.getAsJsonArray().get(0);
                    JsonElement nameElement = topResult.getAsJsonObject().get("nubKey");
                    final String result = nameElement.getAsString();
                    setID(result);
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void setScientificName(String id) {
        final String GBIF_id = id;
        new Thread() {
            public void run() {
                try {
                    String urlString = "http://api.gbif.org/v1/species/"+GBIF_id+"/name";
                    URL url = new URL(urlString);
                    URLConnection request = url.openConnection();
                    request.connect();

                    JsonParser jp = new JsonParser();
                    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
                    JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object.
                    String name;
                    try {
                        name = rootobj.get("canonicalName").getAsString();
                    }
                    catch (NullPointerException e) {
                        name = rootobj.get("scientificName").getAsString();
                    }
                    final String result = name;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            scientific_name_tv.setText(result);
                        }
                    });
                }catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.start();
    }

    private void setNotes(String name) {
        final String scientificName = name;
        new Thread() {
            String result;
            public void run() {
                try {
                    Document doc = null;
                    try {
                        Log.i("scientificName", "setNotes: "+scientificName);
                        doc = Jsoup.connect("https://selectree.calpoly.edu/tree-detail/"+scientificName.replace(' ', '-')).get();

                        Element content = doc.getElementById("content");
                        Elements paragraphs = content.getElementsByClass("col-md-6");
                        String pText = null;
                        for (Element p : paragraphs) {
                            result = p.text();
                            break;//can get additional info by removing this break;
                        }
                        String [] words = result.split("[\\s]");
                        String generalNotes = "";
                        String treeCharacteristics = "";
                        boolean afterCharacteristics = false;
                        for(String word:words) {
                            if(afterCharacteristics) {
                                treeCharacteristics += (word + " ");
                                if(word.contains("."))
                                    treeCharacteristics += "\n";
                            }
                            else {
                                if(!word.equals("Tree") && !word.equals("Characteristics") && !word.equals("General") && !word.equals("Notes"))
                                    generalNotes += (word + " ");
                            }
                            if (word.equals("Characteristics"))
                                afterCharacteristics = true;
                        }
                        final String finalNotes = generalNotes;
                        final String finalCharacteristics = treeCharacteristics;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notes_tv.setText(finalNotes);
                                characteristics_tv.setText(finalCharacteristics);
                            }
                        });
                    } catch (HttpStatusException e) {
                        result = "No notes";
                    }

                }catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.start();
    }

    private void setTreeImages(String name) {
        final String scientificName = name;
        new Thread() {
            String result;
            public void run() {
                try {
                    Document doc = null;
                    try {
                        doc = Jsoup.connect("https://selectree.calpoly.edu/tree-detail/"+scientificName.replace(' ', '-')).get();

                        Element content = doc.getElementById("content");
                        Elements galleryClass = content.getElementsByClass("fancybox-buttons");
                        String imgURLs[] = new String[3];//For now we're just getting 3 images.
                        int count = 0;
                        for (Element i : galleryClass) {
                            imgURLs[count] = "https://selectree.calpoly.edu"+i.attr("href");//gets all image URLs unless for loop is broken.
                            Log.i("GalleryElements", imgURLs[count]);
                            count++;
                            if(count >= 3)
                                break;
                        }

                        Drawable images[] = new Drawable[3];
                        try {
                            for(int i = 0; i <= 3; i++) {
                                InputStream iStream = (InputStream) new URL(imgURLs[i]).getContent();
                                images[i] = Drawable.createFromStream(iStream, "src");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String urlString = "https://upload.wikimedia.org/wikipedia/commons/thumb/0/02/OSIRIS_Mars_true_color.jpg/1024px-OSIRIS_Mars_true_color.jpg";
                        InputStream inputStream = (InputStream) new URL(urlString).getContent();
                        final Drawable drawable = Drawable.createFromStream(inputStream, "src");

                        final Drawable finalImages[] = images;


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tree_img1_iv.setImageDrawable(finalImages[0]);
                                tree_img2_iv.setImageDrawable(finalImages[1]);
                                tree_img3_iv.setImageDrawable(drawable);
                            }
                        });
                    } catch (HttpStatusException e) {
                        result = "No Images";
                    }

                }catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.start();
    }

    public void toGenus(View view) {
        Intent intent = new Intent(this, GenusActivity.class);
        startActivity(intent);
    }
}
