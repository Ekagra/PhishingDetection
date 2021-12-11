package com.phishing.example.all;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    /*private ViewPager viewPager;*/
    private Button b1,b2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option);
        b1 = (Button) findViewById(R.id.links);

        SafetyNet.getClient(this).initSafeBrowsing();


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Link.class);
                startActivity(intent);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        SafetyNet.getClient(this).initSafeBrowsing();
    }
}
