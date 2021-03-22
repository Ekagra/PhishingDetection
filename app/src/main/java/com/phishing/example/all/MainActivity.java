package com.phishing.example.all;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getSupportActionBar().hide();
        viewPager = (ViewPager) findViewById(R.id.container);

        //setup the pages
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Link()); //index 0
        adapter.addFragment(new Message()); //index 0

        viewPager.setAdapter(adapter);

        TabLayout tablayout = (TabLayout) findViewById(R.id.tab);
        tablayout.setupWithViewPager(viewPager);

        tablayout.getTabAt(0).setText("Links");
        tablayout.getTabAt(1).setText("Spam");

    }
}
