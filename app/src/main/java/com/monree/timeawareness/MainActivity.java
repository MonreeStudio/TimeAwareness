package com.monree.timeawareness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    int hourLen;
    int minuteLen;
    int secondLen;
    TextView hourTv;
    TextView minuteTv;
    TextView secondTv;
    LinearLayout mainLayout;
    Timer timer;
    SharedPreferences sharedPreferences;
    Context context;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_main);
        hourTv = findViewById(R.id.HourTextView);
        minuteTv = findViewById(R.id.MinuteTextView);
        secondTv = findViewById(R.id.SecondTextView);
        mainLayout = findViewById(R.id.mainLayout);
        context = getApplicationContext();
        sharedPreferences= getSharedPreferences("data",MainActivity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putBoolean("run",false);
        editor.commit();
        hourLen = 0;
        minuteLen =0;
        secondLen =0;
        hourTv.setOnClickListener(this);
        minuteTv.setOnClickListener(this);
        secondTv.setOnClickListener(this);

        mainLayout.setOnTouchListener(new OnDoubleClickListener(new OnDoubleClickListener.DoubleClickCallback() {
            @Override
            public void onDoubleClick() {
                boolean isRun = sharedPreferences.getBoolean("run",false);
                if(isRun==true){
                    Toast.makeText(MainActivity.this,"暂停计时",Toast.LENGTH_SHORT).show();
                    editor.putBoolean("run",false);
                    editor.commit();
                }
                else {
                    Toast.makeText(MainActivity.this,"开始计时",Toast.LENGTH_SHORT).show();
                    editor.putBoolean("run",true);
                    editor.commit();
                    Message message = handler.obtainMessage(1);
                    handler.sendMessageDelayed(message,1000);
                }
            }
        }));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.HourTextView:
            case R.id.MinuteTextView:
            case R.id.SecondTextView:
                editor.putBoolean("run",false);
                editor.commit();
                showAddDialog();
                break;
            default:
                    break;
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(this, "横屏模式", Toast.LENGTH_SHORT).show();
            float pxDimension = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 120,context.getResources().getDisplayMetrics());
            hourTv.setTextSize(pxDimension);
            minuteTv.setTextSize(pxDimension);
            secondTv.setTextSize(pxDimension);
            Toast.makeText(this, "横屏模式", Toast.LENGTH_SHORT).show();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            //Toast.makeText(this, "竖屏模式", Toast.LENGTH_SHORT).show();
            float pxDimension = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 70,context.getResources().getDisplayMetrics());
            hourTv.setTextSize(pxDimension);
            minuteTv.setTextSize(pxDimension);
            secondTv.setTextSize(pxDimension  );
            Toast.makeText(this, "竖屏模式", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler(){
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg){

            switch (msg.what){
                case 1:
                    boolean isRun = sharedPreferences.getBoolean("run",false);
                    if (isRun==true) {

                        hourTv.setText(hourLen + ":");
                        if (minuteLen < 10)
                            minuteTv.setText("0" + minuteLen + ":");
                        else
                            minuteTv.setText(minuteLen + ":");
                        if (secondLen < 10)
                            secondTv.setText("0" + secondLen + "");
                        else
                            secondTv.setText(secondLen + "");
                        if (secondLen > 0) {
                            secondLen--;
                            if (secondLen < 10)
                                secondTv.setText("0" + secondLen + "");
                            else
                                secondTv.setText(secondLen + "");
                            Message message = handler.obtainMessage(1);
                            handler.sendMessageDelayed(message, 1000);

                        } else if (secondLen == 0 && minuteLen > 0) {
                            secondLen = 59;
                            minuteLen--;
                            Message message = handler.obtainMessage(1);
                            handler.sendMessageDelayed(message, 1000);
                        } else if (minuteLen == 0 && hourLen > 0 && secondLen > 0) {
                            secondLen--;
                            minuteLen = 59;
                            hourLen--;
                            Message message = handler.obtainMessage(1);
                            handler.sendMessageDelayed(message, 1000);
                        } else if (minuteLen == 0 && hourLen > 0 && secondLen == 0) {
                            secondLen = 59;
                            minuteLen = 59;
                            hourLen--;
                            Message message = handler.obtainMessage(1);
                            handler.sendMessageDelayed(message, 1000);
                        }

                    }
                    if(hourTv.getText().toString().equals("0:")&&minuteTv.getText().toString().equals("00:")&&secondTv.getText().toString().equals("00"))
                        Toast.makeText(MainActivity.this,"时间到！",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    protected void showAddDialog() {

        LayoutInflater factory = LayoutInflater.from(context);
        final View textEntryView = factory.inflate(R.layout.dialog_layout, null);
//        final EditText editHourTv = textEntryView.findViewById(R.id.edit_hourTextView);
//        final EditText editMinuteTv = textEntryView.findViewById(R.id.MinuteTextView);
//        final EditText eidtSecondTv = textEntryView.findViewById(R.id.SecondTextView);
        final NumberPicker hourNp = textEntryView.findViewById(R.id.hourNumberPicker);
        final NumberPicker minuteNp = textEntryView.findViewById(R.id.minuteNumberPicker);
        final NumberPicker secondNp = textEntryView.findViewById(R.id.secondNumberPicker);
        hourNp.setMaxValue(24);
        hourNp.setMinValue(0);
        hourNp.setValue(hourLen);
        minuteNp.setMaxValue(59);
        minuteNp.setMinValue(0);
        minuteNp.setValue(minuteLen);
        secondNp.setMaxValue(59);
        secondNp.setMinValue(0);
        secondNp.setValue(secondLen);
//        if(secondLen==0)
//            secondNp.setValue(secondLen);
//        else
//            secondNp.setValue(secondLen+1);
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(MainActivity.this);
        adBuilder.setTitle("设置时长:");
        //adBuilder.setIcon(android.R.drawable.ic_menu_edit);
        adBuilder.setView(textEntryView);
        adBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                hourLen = hourNp.getValue();
                minuteLen = minuteNp.getValue();
                secondLen = secondNp.getValue();
                hourTv.setText( hourLen+":");
                if (minuteLen < 10)
                    minuteTv.setText("0" + minuteLen + ":");
                else
                    minuteTv.setText(minuteLen + ":");
                if (secondLen < 10)
                    secondTv.setText("0" + secondLen);
                else
                    secondTv.setText(secondLen + "");
            }
        });
        adBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {

            }
        });
        adBuilder.show();// 显示对话框

    }

}

class OnDoubleClickListener implements View.OnTouchListener{

    private int count = 0;//点击次数
    private long firstClick = 0;//第一次点击时间
    private long secondClick = 0;//第二次点击时间
    /**
     * 两次点击时间间隔，单位毫秒
     */
    private final int totalTime = 1000;
    /**
     * 自定义回调接口
     */
    private DoubleClickCallback mCallback;

    public interface DoubleClickCallback {
        void onDoubleClick();
    }
    public OnDoubleClickListener(DoubleClickCallback callback) {
        super();
        this.mCallback = callback;
    }

    /**
     * 触摸事件处理
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {//按下
            count++;
            if (1 == count) {
                firstClick = System.currentTimeMillis();//记录第一次点击时间
            } else if (2 == count) {
                secondClick = System.currentTimeMillis();//记录第二次点击时间
                if (secondClick - firstClick < totalTime) {//判断二次点击时间间隔是否在设定的间隔时间之内
                    if (mCallback != null) {
                        mCallback.onDoubleClick();
                    }
                    count = 0;
                    firstClick = 0;
                } else {
                    firstClick = secondClick;
                    count = 1;
                }
                secondClick = 0;
            }
        }
        return true;
    }
}

