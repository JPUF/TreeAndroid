package com.example.psyjb12.tree;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void toSpecies(View view) {
        Intent intent = new Intent(this, SpeciesActivity.class);
        startActivity(intent);
    }
}
