package com.example.psyjb12.tree;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class GenusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genus);

        Bundle b = getIntent().getExtras();
        ArrayList<String> child_IDs = b.getStringArrayList("child_IDs");
        for (String i : child_IDs ) {
            Log.i("genus", i);
        }
    }
}


//TODO refactor SpeciesActivity to actually use Species(). So then we can pass the relevant information to GenusActivity.