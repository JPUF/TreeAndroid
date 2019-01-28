package com.example.psyjb12.tree;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         tv = (TextView) findViewById(R.id.scientific_name);
         tv.setText("Larix decidua");

         findViewById(R.id.dist0).bringToFront();
         findViewById(R.id.dist1).bringToFront();
    }
}
