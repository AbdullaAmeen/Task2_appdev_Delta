package com.example.pong_task2;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import java.util.Random;

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

    public int speedx, speedy;
    RectF cord;

    public Object(int height, int length, int x, int y, int speedx, int speedy) {
        this.cord = new RectF( x, y,x+length, y+height);
        this.speedx= speedx;
        this.speedy= speedy;
    }

}

class Powerups extends Object{

    private Bitmap bmpqn, bmp_contract, bmp_expand, bmp_speedincrease, bmp_speeddecrease;
    boolean isAlive = false;
    int powerups =-1;
    final int POWERUP_NONE = -1, POWERUP_EXTEND = 0, POWERUP_CONTRACT = 1, POWERUP_SPEEDUP = 2, POWERUP_SLOWDOWN = 3;

    public Powerups( int x, int y, Resources res) {
        super(0, 0, x, y, 0, 5);
        bmpqn = BitmapFactory.decodeResource(res, R.drawable.powerup);
        bmp_expand = BitmapFactory.decodeResource(res, R.drawable.powerup_expand);
        bmp_contract = BitmapFactory.decodeResource(res, R.drawable.powerup_contract);
        bmp_speedincrease = BitmapFactory.decodeResource(res, R.drawable.powerup_ball_speedincrease);
        bmp_speeddecrease = BitmapFactory.decodeResource(res, R.drawable.powerup_ball_speeddecrease);
        this.cord = new RectF( x, y,x+ bmpqn.getWidth(), y+ bmpqn.getHeight());
    }

    public void randomizeLocation(int Height, int Width){

        isAlive = true;
        cord.offsetTo(new Random().nextInt((int)(Width - cord.width())),  Height/2  + new Random().nextInt((int)(Height/4 - cord.height())));

    }

    public void updatePowerup(int Height){
        if(cord.top > Height) {
            isAlive = false;

        }
        cord.offsetTo(cord.left,cord.top + speedy);

    }

    public void activatePowerups(RectF bat, Ball ball){

           powerups = new Random().nextInt(4);
        //  powerups =0;
        switch (powerups){
            case POWERUP_EXTEND:
                bat.left -= 50;
                bat.right += 50;
                break;

            case POWERUP_CONTRACT:
                bat.left += 25;
                bat.right -= 25;
                break;

            case POWERUP_SPEEDUP:
                 ball.increaseBallSpeed(3);
                break;
            case POWERUP_SLOWDOWN:
                ball.increaseBallSpeed(-3);
                break;
        }



    }

    public void DeactivatePowerups(RectF bat, Ball ball){
        switch (powerups){
            case POWERUP_EXTEND:
                bat.left += 50;
                bat.right -= 50;
                break;

            case POWERUP_CONTRACT:
                bat.left -= 25;
                bat.right += 25;
                break;

            case POWERUP_SPEEDUP:
                ball.increaseBallSpeed(-3);
                break;
            case POWERUP_SLOWDOWN:
                ball.increaseBallSpeed(+3);
                break;
        }



    }
    public Bitmap getBmp(int id) {

        switch (id){
            case POWERUP_EXTEND:
                return bmp_expand;

            case POWERUP_CONTRACT:
                return bmp_contract;

            case POWERUP_SPEEDUP:
                return  bmp_speedincrease;

            case POWERUP_SLOWDOWN:
                return bmp_speeddecrease;

        }
        return bmpqn;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }




}
class Ball extends Object{
    private int speed;

    public Ball(int speedx, int speed,int Width,int Height, int direction) {
        super(30, 30, (Width-30)/2 +105 , (Height-30)/2 + 105, speedx,direction*(int) Math.sqrt(speed*speed - speedx*speedx));
        this.speed = speed;
    }

    public void updateBall() {


        cord.offsetTo(cord.left+speedx,cord.top + speedy);
        //return 0;

    }
  /*  public void ballUnstuck(int dy,int dx){
        cord.offset(dx, dy);

    }*/
    public void onCollision(int sign){
        /*if(cord.top  > batBottom.top){
            speedx = -1*speedx/Math.abs(speedx)*(speed - new Random().nextInt(speed/2)- 1);
        }
        else*/
        //speedx = -1*speedx/Math.abs(speedx);
        int speed = this.speed + 8;

        speedx = sign*Integer.signum(speedx)*((speed - new Random().nextInt(speed/2)- 1));//} //ERROR speed sometimes zero
        Log.v("hit", "yes");
        speedy = -1*Integer.signum(speedy)*(int) Math.sqrt(speed*speed - speedx*speedx);
      //  ballUnstuck(5* speedy/Math.abs(speedy),-5*speedx/Math.abs(speedx));
    }

    public void increaseBallSpeed(int ds){
        speed += ds;
    }

    public int getSpeed() {
        return speed;
    }

}

class Bat extends Object{
    final int STOP = 0, LEFT = -1, RIGHT = 1;
    private int state;
    int aiHeight;
    float middlepos; float endpos; boolean turn;

    public Bat(int height, int length, int x, int y, int speedx,int  aiHeight) {
        super(height, length, x, y, speedx, 0);
        state = STOP;
        endpos = cord.centerX();
        middlepos = cord.centerX();
        this.aiHeight = aiHeight;
    }
    public void updateBat(int swidth){
        if ( cord.left < 0 && state == LEFT) state = STOP;
        if (cord.right > swidth && state == RIGHT) state = STOP;
        cord.offsetTo(cord.left+ state*speedx, cord.top);
    }

    public void setState(int state) {
        this.state = state;
    }

    public void batAI(int swidth, int ballspeedx, int ballspeedy, RectF ball){
       // if(middlepos == endpos) {

            int height = (int) ( ball.top - cord.bottom);
            float time = Math.abs(height / ballspeedy), distance = Math.abs(time * ballspeedx);

            Log.v("height1", " "+ height +" "+ distance + " " + swidth);
            if(ballspeedx < 0) distance = Math.abs(distance-ball.centerX());
            else distance += ball.centerX();

            if(((Math.floor(distance/swidth))%2 == 0))
                endpos = distance%swidth;
            else
                endpos = swidth - (distance%swidth);
            Log.v("height", " "+ height +" "+ distance + " " + swidth);
            }


         //   endpos = Math.abs((ballspeedx * time) % swidth);
      //  }



    public void updateBatAI(int swidth){

        Log.v("endpos", ""+endpos + " "+cord.centerX() + " " + swidth);
        if(Math.abs(cord.centerX()-endpos) <=5)
            state = STOP;
        else if(cord.centerX() > endpos)
            state = LEFT;
        else
            state = RIGHT;

        updateBat(swidth);
    }

}

public class GameView extends SurfaceView {

    final boolean OUTCOME_WIN = true, OUTCOME_LOSE = false;
    final int GAMEMODE_COMPUTER=1;

    int gameDifficulty,gameMode;

    private Bitmap bmp;


// Vibrate for 400 milliseconds

    SurfaceHolder holder;

    private GameThread gameThread;
    Paint paint = new Paint();

    int Width, Height;
//    final int TopBorderHeight = 80;

    private SoundPool audio;
    int SOUND_BAT_HIT, SOUND_PING, SOUND_WALL_HIT, SOUND_LOSE_CRY, SOUND_GAME_END;

    Vibrator v;


    Ball ball;
    Bat batTop, batBottom;
    Button bt_restart, bt_menu;
    Powerups powerups;
    float ds =0, dp= 0;
    Context context;

    public GameView(Context context, int Width, int Height, int gameDifficulty, int gameMode, Vibrator v) {
        super(context);
        this.v = v;
      //initialize powerup
        powerups = new Powerups( 0,0,getResources());

        this.gameDifficulty = gameDifficulty;
        this.gameMode = gameMode;

        gameThread = new GameThread(this);

        this.context = context;

        this.Width = Width;
        this.Height = Height;

        audio = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        SOUND_BAT_HIT = audio.load(context, R.raw.bat_hit, 1);
        SOUND_WALL_HIT = audio.load(context, R.raw.wall_hit, 1);
        SOUND_PING = audio.load(context, R.raw.ping, 1);
        SOUND_LOSE_CRY = audio.load(context, R.raw.lose_cry, 1);
        SOUND_GAME_END = audio.load(context, R.raw.gameend,1);
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
                        batBottom.setState(batBottom.RIGHT);
                    } else {
                        batBottom.setState(batBottom.LEFT);
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
                    batBottom.setState(batBottom.STOP);
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

            //score wall
            if(gameMode == GAMEMODE_COMPUTER)
                writeText_Center("YOU " + gameThread.getScorePlayer() + " COMP " + gameThread.getScoreComputer(), Width / 2, Height / 16, canvas, Color.WHITE, 64);
            else
                writeText_Center("Score" + gameThread.getScorePlayer(), Width / 2, Height / 16, canvas, Color.WHITE, 75);


            //top bar
            paint.setColor(Color.WHITE);
            //    canvas.drawRect(0, 0, Width, TopBorderHeight, paint);

            //draw Top currently a wall
            canvas.drawRect(batTop.cord.left, batTop.cord.top, batTop.cord.right, batTop.cord.bottom, paint);
            //draw Bat
            canvas.drawRect(batBottom.cord.left, batBottom.cord.top, batBottom.cord.right, batBottom.cord.bottom, paint);
           // paint.setColor(Color.WHITE);
            //draw ball
            canvas.drawRect(ball.cord.left, ball.cord.top, ball.cord.right, ball.cord.bottom, paint);
//test
           // canvas.drawBitmap(powerups.getBmp(powerups.powerups), 10, 10, null);

            Log.v("speed",""+ball.getSpeed()+" "+gameDifficulty+" "+ds);

            //collide with wall
            if (ball.cord.left < 0) {
                if(audio.play( SOUND_WALL_HIT,1.0f, 1.0f, 1, 0, 1.0f)==0)
                    Log.v("sounderror", "error");
                ball.speedx= Math.abs(ball.speedx);
            }
            if (ball.cord.right > Width) {
                audio.play( SOUND_WALL_HIT,1.0f, 1.0f, 1, 0, 1.0f);
                ball.speedx = -1*Math.abs(ball.speedx) ;
            }

            //collide with batTop
            if (RectF.intersects(ball.cord, batTop.cord)) {
                audio.play( SOUND_BAT_HIT,1.0f, 1.0f, 0, 0, 1.0f);
                ball.onCollision(1);
                ball.cord.offsetTo(ball.cord.left, batTop.cord.bottom+1);
                //cord.left += speedx;
                //cord.top += speedy;
            }
            //collide with batBottom
            if (RectF.intersects(ball.cord, batBottom.cord)) {
                audio.play( SOUND_BAT_HIT,1.0f, 1.0f, 0, 0, 1.0f);

                if (ball.cord.top < batBottom.cord.top)
                    ball.onCollision(1);
                else ball.onCollision(-1);

                if(gameMode == GAMEMODE_COMPUTER)
                    batTop.turn = true;
               // batTop.batAI(Width, ball.speedx, ball.speedy, ball.cord );
                else
                gameThread.incrementScorePlayer();


             //   Log.v("yesoffset","yes");
                ball.cord.offsetTo(ball.cord.left, batBottom.cord.top-31);

                ds += gameDifficulty*0.5;
                if(ds == 1){
                    ball.increaseBallSpeed((int)ds);
                    ds =0;
                }
                //spawn powerup
                dp += 1;
                if(dp == 5){

                    powerups.randomizeLocation(Height, Width);
                   // powerups.setAlive(true);

                    dp=0;
                }
                if(powerups.powerups != -1 && dp == 2) {
                        powerups.DeactivatePowerups(batBottom.cord, ball);
                        powerups.powerups = -1;


                }
            }


            //powerupcorner icon
            if(powerups.powerups != powerups.POWERUP_NONE)
                canvas.drawBitmap(powerups.getBmp(powerups.powerups), 10, 30, null);




     /*      if(ball.speedy <0) batTop.batAI(Width, ball.speedx, ball.speedy, ball.cord );
           if(batBottom.cord.contains(ball.cord)){

                Log.v("yesoffset","yes");
         }
           /* if(batTop.cord.contains(ball.cord)){
                ball.cord.offsetTo(ball.cord.left, batTop.cord.bottom+15);
            }*/

            //activate ai
            if ( (ball.cord.top - batTop.cord.bottom) <  batTop.aiHeight && batTop.turn) {
                Log.v("BatAI", ""+(ball.cord.top - batTop.cord.bottom) + " "+batTop.aiHeight);
                batTop.batAI(Width, ball.speedx, ball.speedy, ball.cord);
                batTop.turn = false;
            }

            //player side out
            if ( ball.cord.bottom  > Height + 5*(ball.cord.height())) {
                v.vibrate(500);v.vibrate(500);
               // audio.play( SOUND_LOSE_CRY,1.0f, 1.0f, 0, 0, 1.0f);

                if (gameMode == GAMEMODE_COMPUTER) {
                    gameThread.incrementScoreComputer();

                    if(gameThread.getScoreComputer() == 5){
                        audio.play( SOUND_GAME_END,1.0f, 1.0f, 1, 0, 1.0f);
                        gameOverMenu(canvas);
                        gameOverOutcome(canvas, OUTCOME_LOSE);
                    }

                    newBall(10, 1);
                    batBottom.cord.offsetTo((Width - 250) / 2,(Height - 25) );
                    batTop.cord.offsetTo((Width - 250) / 2,(Height - 5) / 8 );
                }
                else {
                    audio.play( SOUND_GAME_END,1.0f, 1.0f, 1, 0, 1.0f);
                    gameOverMenu(canvas);
                    gameOverScore(canvas);
                }
            }
            //computerside out
            if ( ball.cord.top < -5*(ball.cord.height())) {
                audio.play( SOUND_PING,1.0f, 1.0f, 0, 0, 1.0f);
                gameThread.incrementScorePlayer();
                if(gameThread.getScorePlayer() == 5){
                    audio.play( SOUND_GAME_END,1.0f, 1.0f, 1, 0, 1.0f);
                    gameOverMenu(canvas);
                    gameOverOutcome(canvas, OUTCOME_WIN);
                }
                newBall(10, -1);
                batBottom.cord.offsetTo((Width - 250) / 2,(Height - 25) );
                batTop.cord.offsetTo((Width - 250) / 2,(Height - 5) / 8 );
                batTop.batAI(Width, ball.speedx, ball.speedy, ball.cord );
            }


            //update velocity
            ball.updateBall();
            batBottom.updateBat(Width);
            batTop.updateBatAI(Width);

            if(powerups.isAlive){
                Log.v("powerup",powerups.cord.height()+" "+powerups.cord.width());
                canvas.drawBitmap(powerups.getBmp(powerups.POWERUP_NONE),powerups.cord.left ,powerups.cord.top , null);

             //   canvas.drawRect(powerups.cord, paint);

                powerups.updatePowerup(Height);

                if(RectF.intersects(powerups.cord,batBottom.cord)){
                    powerups.setAlive(false);
                    powerups.activatePowerups(batBottom.cord, ball);

                }
            }


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
        ds=0;dp=0;
       // ball = new Ball(1, 9 + 2*gameDifficulty, (Width), (Height), -1);
        newBall(10,1);
        batBottom = new Bat(5, 250, (Width - 250) / 2, (Height - 25), 10,0);
        Log.v("Gamemode", ""+gameMode);
        if(gameMode == GAMEMODE_COMPUTER)
            batTop = new Bat(5, 250, (Width - 250) / 2, (Height - 5) / 8, 3 + 3*gameDifficulty, (gameDifficulty+1)*Height/3);
        else //wall
            batTop = new Bat(50, Width, 0, (Height - 50) / 8, 5,0);
        bt_restart = new Button(100, 300, (int) (Width - 300) / 2, (int) Height * 5 / 8);
        bt_menu = new Button(75, 150, (Width - 150) / 2, Height * 56 / 80);


        if(Thread.currentThread().getState() == Thread.State.RUNNABLE)
            gameThread.start();
    }

    public void newBall(int speed, int direction){
        int speedx=(new Random().nextInt(10 )-5);
        speedx = speedx == 0?speedx+1:speedx;
        ball = new Ball(speedx, speed + 2*gameDifficulty, (Width), (Height), direction);
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

    public void gameOverMenu(Canvas canvas) {
        gameThread.setState(gameThread.STATE_GAMEOVER);
        Paint paint = new Paint();
        //End Screen
        canvas.save();
        paint.setColor(Color.WHITE);
        canvas.drawRect(bt_restart.cords.left, bt_restart.cords.top, bt_restart.cords.right, bt_restart.cords.bottom, paint);
        writeText_Center("RESTART", bt_restart.getCentreX(), bt_restart.getCentreY(), canvas, Color.BLACK, 30);

        canvas.drawRect(bt_menu.cords.left, bt_menu.cords.top, bt_menu.cords.right, bt_menu.cords.bottom, paint);
        writeText_Center("MENU", bt_menu.getCentreX(), bt_menu.getCentreY(), canvas, Color.BLACK, 30);
        /*if(gameMode == GAMEMODE_COMPUTER)
            gameOverOutcome(canvas);
        else
            gameOverScore(canvas);*/
    }

    public void gameOverScore(Canvas canvas){
        int score = gameThread.getScorePlayer();
        if(getDataInt("HighScore") < score){
            writeText_Center("HighScore " + score, Width/2, Height/2 , canvas, Color.WHITE, 50);
            saveDataInt("HighScore",score);
        }
        else
            // writeText_Center("HighScore: " + getDataInt(), Width/2, Height*3/8 , canvas, Color.WHITE, 50);
            writeText_Center("Score: " + score, Width/2, Height/2 , canvas, Color.WHITE, 50);
        // canvas.restore();
    }

    public void gameOverOutcome(Canvas canvas,boolean outcome) {
        if(outcome){
            writeText_Center("You WIN", Width/2, Height/2 , canvas, Color.WHITE, 50);
            saveDataInt("Win",getDataInt("Win")+1);
        }
        else {
            // writeText_Center("HighScore: " + getDataInt(), Width/2, Height*3/8 , canvas, Color.WHITE, 50);
            writeText_Center("You LOSE", Width / 2, Height / 2, canvas, Color.WHITE, 50);
            saveDataInt("Loss", getDataInt("Loss") + 1);
        }

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

    public void saveDataInt(String key, int data) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("HighScore", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(gameDifficulty+key, data);
        editor.commit();
    }

    public int getDataInt(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("HighScore", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(gameDifficulty+key, 0);
    }

}