package com.monree.timeawareness;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    int hourLen;
    int minuteLen;
    int sencondLen;
    TextView hourTv;
    TextView minuteTv;
    TextView secondTv;
    Button startBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hourTv = findViewById(R.id.HourTextView);
        minuteTv = findViewById(R.id.MinuteTextView);
        secondTv = findViewById(R.id.SecondTextView);
        startBtn = findViewById(R.id.startButton);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hourLen = 0;
                minuteLen = 0;
                sencondLen = 3;
                Message message = handler.obtainMessage(1);
                handler.sendMessageDelayed(message,1000);
            }
        });
    }



    final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    hourTv.setText(hourLen+":");
                    if(minuteLen < 10)
                        minuteTv.setText("0"+minuteLen+":");
                    else
                        minuteTv.setText(minuteLen+":");
                    if(sencondLen < 10)
                        secondTv.setText("0"+sencondLen+"");
                    else
                        secondTv.setText(sencondLen+"");
                    if(sencondLen > 0){
                        sencondLen--;
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message,1000);
                    }
                    else if(sencondLen==0 && minuteLen > 0){
                        sencondLen = 59;
                        minuteLen--;
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message,1000);
                    }
                    else if(minuteLen==0 && hourLen > 0 && sencondLen > 0){
                        sencondLen--;
                        minuteLen = 59;
                        hourLen--;
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message,1000);
                    }
                    else if(minuteLen == 0 && hourLen > 0 && sencondLen == 0){
                        sencondLen = 59;
                        minuteLen = 59;
                        hourLen--;
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message,1000);
                    }
//                    else
//                        timerTv.setVisibility(View.GONE);
            }
            super.handleMessage(msg);
        }
    };
}
