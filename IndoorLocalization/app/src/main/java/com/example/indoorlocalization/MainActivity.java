package com.example.indoorlocalization;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private final static int roomWidth = 782;
    private final static int roomHeight = 1259;
    private final static int originX = -65;
    private final static int originY = 245;

    private int initialBotMargin;
    private int initialRightMargin;
    private int curX;
    private int curY;

    ImageView room;
    ImageView locationPointer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        room = findViewById(R.id.room);
        locationPointer = findViewById(R.id.location_pointer);

        setInitialPos();


        // 반복하기 위해 timer 생성
        Timer timer = new Timer();

        TimerTask TT = new TimerTask() {
            @Override
            public void run() {
                curX -= 20;
                curY += 5;
                Message msg = handler.obtainMessage();
                handler.sendMessage(msg);
            }
        };

        timer.schedule(TT, 1000, 1000);
//        timer.cancel();
    }

    private void setInitialPos() {
        ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) locationPointer.getLayoutParams();

        Resources res = getResources();
        newLayoutParams.bottomMargin = (int)(res.getDimension(R.dimen.room_height) * ((float)originY / roomHeight));
        newLayoutParams.rightMargin = -(int)(res.getDimension(R.dimen.room_width) * ((float)originX / roomWidth));
        initialBotMargin = newLayoutParams.bottomMargin;
        initialRightMargin = newLayoutParams.rightMargin;
        locationPointer.setLayoutParams(newLayoutParams);
        curX = 0;
        curY = 0;
    }

    private void setPos(int x, int y) {
        ConstraintLayout.LayoutParams newLayoutParams = (ConstraintLayout.LayoutParams) locationPointer.getLayoutParams();
        Resources res = getResources();
        newLayoutParams.bottomMargin = initialBotMargin + (int)(res.getDimension(R.dimen.room_height) * ((float)y / roomHeight));
        newLayoutParams.rightMargin = initialRightMargin - (int)(res.getDimension(R.dimen.room_width) * ((float)x / roomWidth));
        locationPointer.setLayoutParams(newLayoutParams);
    }

    // 에러를 방지하기 위해 Handler 선언
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            setPos(curX, curY);
        }
    };
}

