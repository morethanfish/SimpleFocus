package com.hfad.simplefocus;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "myLogs";
    public static final int NOTIFICATION_ID = 1;

    int myCurrentPeriod = 1500;
    private Timer myTimer;

    private EditText etName;
    private TextView tv;

    private Button btnStartPause;
    private Button btnStop;
    private Button btnTest;

    private String startStr;
    private String pauseStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        tv = (TextView) findViewById(R.id.timer);
        btnStartPause = (Button) findViewById(R.id.btnStartPause);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnTest = (Button) findViewById(R.id.btnTest);

        startStr = getResources().getString(R.string.btnStart);
        pauseStr = getResources().getString(R.string.btnPause);

        btnStartPause.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnTest.setOnClickListener(this);

        Log.v(TAG, "MainActivity onCreate");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Уведомление о том, что будет запущена активность
        Log.v(TAG, "MainActivity onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Уведомление о том, что активность запускается
        Log.v(TAG, "MainActivity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Уведомление о том, что активность будет взаимодействовать с пользователем
        Log.v(TAG, "MainActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Уведомление о том, что активность прекращает взаимодействовать с пользователем
        Log.v(TAG, "MainActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Уведомление о том, что активность больше не видима
        Log.v(TAG, "MainActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Уведомление о том, что активность будет удалена
        if (myTimer != null) {
            myTimer.cancel();
        }
        Log.v(TAG, "MainActivity onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Сохранение состояния экземпляра
        super.onSaveInstanceState(outState);
        Log.v(TAG, "MainActivity onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Восстановление состояни
        Log.v(TAG, "MainActivity onRestoreInstanceState");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        etName.clearFocus();
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartPause:
                Button btn = (Button)v;
                String btnText = btn.getText().toString();
                if (btnText.equals(startStr)) {
                    btnStartPause.setText(pauseStr);
                    myTimer = new Timer();
                    myTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runTimer();
                        }
                    }, 0, 1000);
                    Log.v(TAG, "MainActivity onClickedStart");
                } else if (btnText.equals(pauseStr)) {
                    btnStartPause.setText(startStr);
                    myTimer.cancel();
                    Log.v(TAG, "MainActivity onPauseClicked");
                }
                break;
            case R.id.btnStop:
                if (myCurrentPeriod != 1500) {
                    myTimer.cancel();
                    myCurrentPeriod = 1500;
                    tv.setText("25:00");
                    Log.v(TAG, "MainActivity onStopClicked");
                }
                btnStartPause.setText(startStr);
                break;
            case R.id.btnTest:
                myCurrentPeriod = 7;
                Log.v(TAG, "MainActivity onTestClicked");
                break;
            default:
                Toast toast = Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
    }

    private void runTimer() {
        this.runOnUiThread(Timer_Tick);
        Log.v(TAG, "runTimer");
    }

    private Runnable Timer_Tick = new Runnable() {
        @Override
        public void run() {
            myCurrentPeriod--;
            int minutes = (myCurrentPeriod%3600)/60;
            int secs = myCurrentPeriod%60;
            String time = String.format("%02d:%02d", minutes, secs);
            tv.setText(time);
            if (myCurrentPeriod == 0) {
                myTimer.cancel();
                myCurrentPeriod = 1500;
                tv.setText("25:00");
                btnStartPause.setText(startStr);
                sendNotification();
//                Intent intent = new Intent(MainActivity.this, BreakActivity.class);
//                startActivity(intent);
                Log.v(TAG, "start BreakActivity");
            }
        }
    };

    public void sendNotification() {
        Intent intent = new Intent(this, BreakActivity.class);
        intent.putExtra("task", etName.getText().toString());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.tomato)
                .setAutoCancel(true)
                .setTicker("Настало время отдыха")
                .setContentTitle("Перерыв")
                .setContentText("Ты отлично потрудился!")
                .setDefaults(Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
