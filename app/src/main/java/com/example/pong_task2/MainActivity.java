package com.example.pong_task2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
//import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    final int GAMEMODE_COMPUTER=1;
    Button bt_goToNormal;
    TextView tv_highScore;
    int GameMode, GameDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt_goToNormal = findViewById(R.id.bt_goToNormal);
        tv_highScore = findViewById(R.id.tv_highScore);

        Spinner sp_Difficulty = findViewById(R.id.sp_Difficulty);
        ArrayAdapter<CharSequence> adapter_difficulty = ArrayAdapter.createFromResource(this,
        R.array.DIFFICULTY, android.R.layout.simple_spinner_item);
        adapter_difficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_Difficulty.setAdapter(adapter_difficulty);
        sp_Difficulty.setOnItemSelectedListener(this);

        Spinner sp_Gamemode = findViewById(R.id.sp_Gamemode);
        ArrayAdapter<CharSequence> adapter_Gamemode = ArrayAdapter.createFromResource(this,
        R.array.GAMEMODE, android.R.layout.simple_spinner_item);
        adapter_Gamemode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_Gamemode.setAdapter(adapter_Gamemode);
        sp_Gamemode.setOnItemSelectedListener(this);

        bt_goToNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it_goToNormal = new Intent(MainActivity.this, NormalMode.class);
                it_goToNormal.putExtra("GameMode", GameMode);
                it_goToNormal.putExtra("GameDifficulty", GameDifficulty);
                startActivity(it_goToNormal);


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setScore();
    }

    public int getDataInt(String key) {
        SharedPreferences sharedPreferences = getSharedPreferences("HighScore", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(GameDifficulty+key, 0);
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(parent.getItemAtPosition(0).toString().equals("EASY"))
             GameDifficulty = (int) id;
        else {
        //    Toast.makeText(parent.getContext(),""+id, Toast.LENGTH_SHORT).show();
            GameMode = (int) id;
        }
        setScore();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setScore(){
        if(GameMode == GAMEMODE_COMPUTER)
            tv_highScore.setText("WIN: " + getDataInt("Win") + " LOSS: "+ getDataInt("Loss"));
        else
            tv_highScore.setText("HIGHSCORE: " + getDataInt("HighScore"));
    }
}