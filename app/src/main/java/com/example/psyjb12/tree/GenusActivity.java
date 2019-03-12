package com.example.psyjb12.tree;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class GenusActivity extends AppCompatActivity {

    LinearLayout genus_ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genus);
        genus_ll = (LinearLayout) findViewById(R.id.genusLayout);

        Bundle b = getIntent().getExtras();
        ArrayList<String> child_IDs = b.getStringArrayList("child_IDs");
        for (String id : child_IDs ) {
            genus_ll.addView(createGenusCard(id));
        }
    }

    public void displayGenus(final String id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                genus_ll.addView(createGenusCard(id));
            }
        });
    }

    //TODO change to a proper card. Not textview
    public TextView createGenusCard(String id) {
        Context context = getApplicationContext();
        TextView tv = new TextView(context);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(tvParams);
        tv.setText(id);
        return tv;
    }
}


//TODO refactor SpeciesActivity to actually use Species(). So then we can pass the relevant information to GenusActivity. maybe