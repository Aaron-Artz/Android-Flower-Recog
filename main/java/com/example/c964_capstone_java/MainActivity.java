package com.example.c964_capstone_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button identifyBtn = (Button) findViewById(R.id.identifyBtn);
        Button exploreBtn = (Button) findViewById(R.id.exploreBtn);


        // Identify button action, sends to IdentifyAction
        identifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent identifyIntent = new Intent(getApplicationContext(), IdentifyActivity.class);
                startActivity(identifyIntent);
            }
        });

        // Explore button action, sends to ExploreAction
        exploreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent exploreIntent = new Intent(getApplicationContext(), ExploreActivity.class);
                startActivity(exploreIntent);
            }
        });






    }
}