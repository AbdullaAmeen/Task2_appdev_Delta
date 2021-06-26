package com.example.pong_task2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;

public class NormalMode extends AppCompatActivity {

    GameView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState );
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

    }

    @Override
    protected void onPause() {
        super.onPause();
        view.pause();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Display display= getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        view = new  GameView(this, size.x, size.y);

        setContentView(view);
        //view.addView();



    }
}