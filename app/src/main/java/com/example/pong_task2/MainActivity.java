package com.example.pong_task2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    Button bt_goToNormal;
    TextView tv_highScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_goToNormal = findViewById(R.id.bt_goToNormal);
        tv_highScore = findViewById(R.id.tv_highScore);
        bt_goToNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it_goToNormal = new Intent(MainActivity.this, NormalMode.class);
                startActivity(it_goToNormal);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_highScore.setText("HIGHSCORE: " + getDataInt());
    }

    public int getDataInt() {
        SharedPreferences sharedPreferences = getSharedPreferences("HighScore", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("HighScore", 0);
    }


}