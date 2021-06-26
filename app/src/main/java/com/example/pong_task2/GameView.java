package com.example.pong_task2;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.HeaderViewListAdapter;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import java.util.Random;
import java.util.logging.Handler;

class Button{
    Rect cords;

    public Button(int height, int length, int x, int y ) {
        cords = new Rect(x,y,x + length,y + height);
    }
    public int getCentreX(){
        return (cords.left + cords.right)/2;
    }
    public int getCentreY(){
        return (cords.top + cords.bottom)/2;
    }

}
class Object{

    private int  height, length;
    public int speedx, speedy;
    RectF cord;

    public Object(int height, int length, int x, int y, int speedx, int speedy) {
        /*this.height = height;
        this.length = length;*/
        this.cord = new RectF(x,y,x+length, y+height);
        this.speedx= speedx;
        this.speedy= speedy;
    }

    public int getHeight() {
        return height;
    }

    public int getLength() {
        return length;
    }

}



class Ball extends Object{
    private int speed;

    public Ball(int speedx, int speed,int Width,int Height) {
        super(30, 30, (Width-30)/2 +105 , (Height-30)/2 + 105, speedx,(int) Math.sqrt(speed*speed - speedx*speedx));
        this.speed = speed;
    }

    public boolean updateBall(int swidth, int sheight, RectF batTop, RectF batBottom) {
        if ( cord.left < 0 || cord.right > swidth) speedx *= -1;
        if ( cord.bottom  > sheight + 2*(cord.bottom - cord.top)) return true;


        cord.offsetTo(cord.left+speedx,cord.top + speedy);
        return false;

    }
    public void ballUnstuck(int dy,int dx){
        cord.offset(dx, dy);

    }
    public void onCollision(int sign){
        /*if(cord.top  > batBottom.top){
            speedx = -1*speedx/Math.abs(speedx)*(speed - new Random().nextInt(speed/2)- 1);
        }
        else*/
        //speedx = -1*speedx/Math.abs(speedx);

        speedx = sign*speedx/Math.abs(speedx)*(speed - new Random().nextInt(speed/2)- 1);//} //ERROR speed sometimes zero
        Log.v("hit", "yes");
        speedy = -1*speedy/Math.abs(speedy)*(int) Math.sqrt(speed*speed - speedx*speedx);
        ballUnstuck(5* speedy/Math.abs(speedy),-5*speedx/Math.abs(speedx));
    }

}

class Bat extends Object{
    final int stop = 0, left= -1, right= 1;
    private int state;
    public Bat(int height, int length, int x, int y, int speedx) {
        super(height, length, x, y, speedx, 0);
        state = stop;
    }
    public void updateBat(int swidth){
        if ( cord.left < 0 && state == left) state = stop;
        if (cord.right > swidth && state ==right) state = stop;
        cord.offsetTo(cord.left+ state*speedx, cord.top);
    }

    public void setState(int state) {
        this.state = state;
    }
}

public class GameView extends SurfaceView {

    SurfaceHolder holder;
    private GameThread gameThread;
    Paint paint = new Paint();

    int Width, Height;
//    final int TopBorderHeight = 80;

    Ball ball;
    Bat batTop, batBottom;
    Button bt_restart, bt_menu;

    Context context;

    public GameView(Context context, int Width, int Height) {
        super(context);
        gameThread = new GameThread(this);

        this.context = context;

        this.Width = Width;
        this.Height = Height;

        //GameStart();

        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                gameThread.setState(gameThread.STATE_START);
                gameThread.startGame();
                //gameThread.start();

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                boolean retry = true;
                gameThread.setState(gameThread.STATE_GAMEOVER);
                while (retry) {
                    try {
                        gameThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }

            }
        });


    }

    public void pause(){
        boolean retry = true;
        gameThread.setState(gameThread.STATE_PAUSE);
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
    public void Start(){
        if(gameThread.getGameState() == gameThread.STATE_PAUSE)
        gameThread.setState(gameThread.STATE_START);
        gameStart();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        if (gameThread.getRunning() == gameThread.STATE_GAMEOVER) {
            if (bt_restart.cords.contains((int) x, (int) y)) {
                restart();
            }
            if (bt_menu.cords.contains((int) x, (int) y)) {
                exit();
            }
        }
        else if(gameThread.getRunning() == gameThread.STATE_INGAME) {
            switch (motionEvent.getAction()) {

                case MotionEvent.ACTION_DOWN:


                case MotionEvent.ACTION_MOVE:
                    if (motionEvent.getX() > Width/2) {
                        batBottom.setState(batBottom.right);
                    } else {
                        batBottom.setState(batBottom.left);
                    }
                    break;
                    // case MotionEvent.ACTION_POINTER_DOWN:



               /* if(motionEvent.getX() > Width / 2){
                    batBottom.setState(batBottom.right);
                }
                else{
                    batBottom.setState(batBottom.left);
                }
                break;*/

                case MotionEvent.ACTION_UP:
                    batBottom.setState(batBottom.stop);
                    break;

            /*case MotionEvent.ACTION_POINTER_UP:

                break;*/

            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (canvas != null) {
            //background
            canvas.drawColor(Color.BLACK);


            writeText_Center("Score" + gameThread.getScore(), Width / 2, Height / 16, canvas, Color.WHITE, 75);


            //top bar
            paint.setColor(Color.WHITE);
            //    canvas.drawRect(0, 0, Width, TopBorderHeight, paint);

            //draw Top currently a wall
            canvas.drawRect(batTop.cord.left, batTop.cord.top, batTop.cord.right, batTop.cord.bottom, paint);
            //draw Bat
            canvas.drawRect(batBottom.cord.left, batBottom.cord.top, batBottom.cord.right, batBottom.cord.bottom, paint);
            //draw ball
            canvas.drawRect(ball.cord.left, ball.cord.top, ball.cord.right, ball.cord.bottom, paint);

            if (RectF.intersects(ball.cord, batTop.cord)) {
                ball.onCollision(1);
                //cord.left += speedx;
                //cord.top += speedy;
            }
            if (RectF.intersects(ball.cord, batBottom.cord)) {
                if (ball.cord.top < batBottom.cord.top)
                    ball.onCollision(1);
                else ball.onCollision(-1);

                gameThread.incrementScore();

            }
            if(batBottom.cord.contains(ball.cord)){
                ball.cord.offsetTo(ball.cord.left, batBottom.cord.top-5);
            }
            if(batTop.cord.contains(ball.cord)){
                ball.cord.offsetTo(ball.cord.left, batTop.cord.bottom+5);
            }
            //update ball velocity
            if (ball.updateBall(Width, Height, batTop.cord, batBottom.cord)) {
                gameOver(canvas);
            }
            batBottom.updateBat(Width);



        }


    }

    public void startDraw(Canvas canvas, int timeleft) {
        if (canvas != null) {
            //background
            canvas.drawColor(Color.BLACK);
            //writeText_Center(""+1000, Width/2,Height/2, canvas,Color.WHITE,50 );
            writeText_Center("" + timeleft, Width / 2, Height / 2, canvas, Color.WHITE, 50);
        }
    }

    public void gameStart() {
        //initialize
        int speedx=(new Random().nextInt(20 )-5);
        speedx = speedx == 0?speedx+1:speedx;

        ball = new Ball(speedx, 20, (Width), (Height));
        batBottom = new Bat(5, 250, (Width - 250) / 2, (Height - 25), 9);
        batTop = new Bat(50, Width, 0, (Height - 50) / 8, 5);

        bt_restart = new Button(100, 300, (int) (Width - 300) / 2, (int) Height * 5 / 8);
        bt_menu = new Button(75, 150, (Width - 150) / 2, Height * 56 / 80);
        if(Thread.currentThread().getState() == Thread.State.RUNNABLE)
            gameThread.start();
    }

    public void writeText_Center(String text, int x, int y, Canvas canvas, int color, int fontSize) {
        Paint paintText = new Paint();
        paintText.setColor(color);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setAntiAlias(true);
        Typeface Arcade = ResourcesCompat.getFont(getContext(), R.font.arcaden);
        paintText.setTypeface(Arcade);
        paintText.setTextSize(fontSize);
        int xPos = x;
        paintText.setTextAlign(Paint.Align.CENTER);
        //int xPos = (int)((x - (int)(paintText.measureText(text)))/2);
        int yPos = (int) (y - (paintText.descent() + paintText.ascent()) / 2);
        canvas.save();  //
        canvas.drawText(text, x, yPos, paintText);
        canvas.restore();
    }

    public void gameOver(Canvas canvas) {
        gameThread.setState(gameThread.STATE_GAMEOVER);
        Paint paint = new Paint();
        //End Screen
        canvas.save();
        paint.setColor(Color.WHITE);
        canvas.drawRect(bt_restart.cords.left, bt_restart.cords.top, bt_restart.cords.right, bt_restart.cords.bottom, paint);
        writeText_Center("RESTART", bt_restart.getCentreX(), bt_restart.getCentreY(), canvas, Color.BLACK, 30);

        canvas.drawRect(bt_menu.cords.left, bt_menu.cords.top, bt_menu.cords.right, bt_menu.cords.bottom, paint);
        writeText_Center("MENU", bt_menu.getCentreX(), bt_menu.getCentreY(), canvas, Color.BLACK, 30);
        int score = gameThread.getScore();
        if(getDataInt() < score){
            writeText_Center("HighScore " + score, Width/2, Height/2 , canvas, Color.WHITE, 50);
            saveDataInt(score);
        }
        else
           // writeText_Center("HighScore: " + getDataInt(), Width/2, Height*3/8 , canvas, Color.WHITE, 50);
            writeText_Center("Score: " + score, Width/2, Height/2 , canvas, Color.WHITE, 50);
            // canvas.restore();
    }

    public void restart() {
        gameThread = new GameThread(this);
       // gameThread.setState(gameThread.STATE_START);
     //   gameThread.setRunning(true);
        gameThread.startGame();

    }

    public void exit() {
        ((Activity) getContext()).finish();
    }


    public void saveDataInt(int data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("HighScore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("HighScore", data);
        editor.commit();
    }

    public int getDataInt() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("HighScore", Context.MODE_PRIVATE);
        return sharedPreferences.getInt("HighScore", 0);
    }

}