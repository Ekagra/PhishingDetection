package com.phishing.example.all;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class Launcher extends AppCompatActivity {
    private Button button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.launcher);
        button = (Button) findViewById(R.id.button);

        //TypeWriiter Effect
        final TypeWriter tw = (TypeWriter) findViewById(R.id.launcher);

        tw.setCharacterDelay(200);
        tw.animateText("Welcome to Phishing Detection");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Launcher.this, About.class);
                startActivity(intent);
                finish();
            }
        });



    }
}
