package com.example.psyjb12.tree;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;

public class SpeciesActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species);
         tv = (TextView) findViewById(R.id.scientific_name);
         tv.setText("Larix decidua");

         findViewById(R.id.dist0).bringToFront();
         findViewById(R.id.dist1).bringToFront();
    }

    public void toGenus(View view) {
        Intent intent = new Intent(this, GenusActivity.class);
        startActivity(intent);
    }
}
