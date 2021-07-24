package com.example.pong_task2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Display;
import android.view.Window;

public class NormalMode extends AppCompatActivity {

    GameView view;
    int GameMode, GameDifficulty;



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

        Intent intent = getIntent();
        GameMode = intent.getIntExtra("GameMode",0);
        GameDifficulty = intent.getIntExtra("GameDifficulty", 0);
        Vibrator v = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        view = new  GameView(this, size.x, size.y, GameDifficulty,GameMode, v);

        setContentView(view);
        //view.addView();



    }
}