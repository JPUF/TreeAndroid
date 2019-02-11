package com.example.psyjb12.tree;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    public static final String TREE_SEARCH = "com.example.psyjb12.SEARCH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void searchTree(View view) {
        Intent intent = new Intent(this, SpeciesActivity.class);
        EditText editText = (EditText) findViewById(R.id.treeSearch);
        String searchTerm = editText.getText().toString();
        intent.putExtra(TREE_SEARCH, searchTerm);
        startActivity(intent);
    }

    public void toSpecies(View view) {
        Intent intent = new Intent(this, SpeciesActivity.class);
        startActivity(intent);
    }

}
