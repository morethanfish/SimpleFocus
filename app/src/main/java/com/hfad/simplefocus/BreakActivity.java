package com.hfad.simplefocus;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class BreakActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "myLogs";
    public static final int NOTIFICATION_ID = 1;

    int myCurrentPeriod = 300;
    private Timer myTimer;

    private Button btnStartBreak;
    private Button btnStopBreak;
    private Button btnTestBreak;

    private TextView tvName;
    private TextView timerBreak;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break);

        tvName = (TextView) findViewById(R.id.tvName);
        timerBreak = (TextView) findViewById(R.id.timerBreak);

        btnStartBreak = (Button) findViewById(R.id.btnStartBreak);
        btnStopBreak = (Button) findViewById(R.id.btnStopBreak);
        btnTestBreak = (Button) findViewById(R.id.btnTestBreak);

        btnStartBreak.setOnClickListener(this);
        btnStopBreak.setOnClickListener(this);
        btnTestBreak.setOnClickListener(this);

        Intent intent = getIntent();
        tvName.setText(intent.getStringExtra("task"));

        Log.v(TAG, "BreakActivity onCreate");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Уведомление о том, что будет запущена активность
        Log.v(TAG, "BreakActivity onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Уведомление о том, что активность запускается
        Log.v(TAG, "BreakActivity onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Уведомление о том, что активность будет взаимодействовать с пользователем
        Log.v(TAG, "BreakActivity onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Уведомление о том, что активность прекращает взаимодействовать с пользователем
        Log.v(TAG, "BreakActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Уведомление о том, что активность больше не видима
        Log.v(TAG, "BreakActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Уведомление о том, что активность будет удалена
        if (myTimer != null) {
            myTimer.cancel();
        }
        Log.v(TAG, "BreakActivity onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //Сохранение состояния экземпляра
        super.onSaveInstanceState(outState);
        Log.v(TAG, "BreakActivity onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Восстановление состояни
        Log.v(TAG, "BreakActivity onRestoreInstanceState");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnStartBreak:
                if (myTimer == null) {
                    myTimer = new Timer();
                    myTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runTimer();
                        }
                    }, 0, 1000);
                }
                Log.v(TAG, "BreakActivity onStartClicked");
                break;
            case R.id.btnStopBreak:
                if (myCurrentPeriod != 300) {
                    endBreak();
                    Log.v(TAG, "BreakActivity onStopClicked");
                }
                break;
            case R.id.btnTestBreak:
                myCurrentPeriod = 15;
                Log.v(TAG, "BreakActivity onTestClicked");
                break;
            default:
                Toast toast = Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
    }

    private void runTimer() {
        this.runOnUiThread(Timer_Tick);
        Log.v(TAG, "BreakActivity runTimer");
    }

    private Runnable Timer_Tick = new Runnable() {
        @Override
        public void run() {
            myCurrentPeriod--;
            int minutes = (myCurrentPeriod%3600)/60;
            int secs = myCurrentPeriod%60;
            String time = String.format("%02d:%02d", minutes, secs);
            timerBreak.setText(time);
            if (myCurrentPeriod == 0) {
                endBreak();
                Log.v(TAG, "BreakActivity start MainActivity");
            }
        }
    };

    private void endBreak() {
        myTimer.cancel();
        myCurrentPeriod = 300;
        timerBreak.setText("5:00");
        sendNotification();
    }

    public void sendNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.tomato);
        builder.setAutoCancel(true);
        builder.setTicker("Перерыв окончен");
        builder.setContentTitle("К работе!");
        builder.setContentText("Пора заняться делом");
        builder.setDefaults(Notification.DEFAULT_VIBRATE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
