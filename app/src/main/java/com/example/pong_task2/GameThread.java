package com.example.pong_task2;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.NonNull;

public class GameThread extends Thread {
    final int STATE_START = -1, STATE_INGAME=1, STATE_GAMEOVER=0, STATE_PAUSE=-2;


    private GameView view;
    private int state = STATE_START;
    private int score = 0;
    Canvas c = null;
    public int getScore() {
        return score;
    }

    public void incrementScore() {
        this.score+=2;
    }

    public GameThread(GameView view){
        this.view = view;
    }

    public void setState(int run){
        state = run;
    }


    public int getGameState() {
        return state;
    }

    public int getRunning() {
        return state;
    }

    public void startGame(){
      //  c = view.getHolder().lockCanvas();

        CountDownTimer timer = new CountDownTimer(3000, 1000) {
            int timeRemaining = 3;
            @Override
            public void onTick(long millisUntilFinished) {

                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()) {
                    timeRemaining--;
                    view.startDraw(c, timeRemaining );

                }
                view.getHolder().unlockCanvasAndPost(c);
            }

            @Override
            public void onFinish() {
                setState(STATE_INGAME);
                Log.v("crash",""+currentThread().isInterrupted());
                view.gameStart();
            }


        };
        timer.start();

    }

    @SuppressLint("WrongCall")
    public void run(){

        while(state == STATE_INGAME){

            try{
                c = view.getHolder().lockCanvas();
                synchronized (view.getHolder()){
                    view.onDraw(c);

                }
            }finally {
                if(c != null) {
                    view.getHolder().unlockCanvasAndPost(c);
                }
            }
        }
    }
}
