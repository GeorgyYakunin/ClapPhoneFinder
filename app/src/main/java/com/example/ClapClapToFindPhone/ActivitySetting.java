package com.example.ClapClapToFindPhone;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;


public class ActivitySetting extends Activity implements OnClickListener {
    public static Activity activitySetting;
    private ImageView btnStart;
    private CheckBox checkBoxflash;
    private CheckBox checkBoxsound;
    private CheckBox checkBoxvibrate;
    private ClassesApp classesApp;
    private String flashbox;
    private int intOnTick;

    public Boolean mPermCAm;
    private RelativeLayout mProgresseBar;
    private ImageView mStop;
    private SeekBar seekBar;
    private String soundbox;
    private ImageView stoplinear;
    private LinearLayout txtStart;
    private String vibratebox;
    private ImageView vocalButton;


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_setting);


        this.mProgresseBar = (RelativeLayout) findViewById(R.id.progressBar);
        this.btnStart = (ImageView) findViewById(R.id.startservice);
        this.txtStart = (LinearLayout) findViewById(R.id.linear1txtStart);
        this.stoplinear = (ImageView) findViewById(R.id.startservice);
        activitySetting = this;
        if (isMyServiceRunning(VocalService.class)) {
            this.txtStart.setVisibility(View.VISIBLE);
            this.stoplinear.setVisibility(View.VISIBLE);
        }
        if (this.mProgresseBar.getVisibility() == View.INVISIBLE) {
            this.mProgresseBar.setVisibility(View.VISIBLE);
        }

        this.mPermCAm = Boolean.valueOf(false);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.adViewContainerSetting);
        this.checkBoxflash = (CheckBox) findViewById(R.id.flashbox);
        this.checkBoxvibrate = (CheckBox) findViewById(R.id.vibratebox);
        this.checkBoxsound = (CheckBox) findViewById(R.id.soundbox);
        this.checkBoxflash.setChecked(false);
        this.checkBoxvibrate.setChecked(false);
        this.checkBoxsound.setChecked(false);
        this.classesApp = new ClassesApp(this);
        this.seekBar = (SeekBar) findViewById(R.id.seekbar);
        this.classesApp.read("seekBar", "50");
        this.flashbox = this.classesApp.read("flashbox", "1");
        this.vibratebox = this.classesApp.read("vibratebox", "1");
        this.soundbox = this.classesApp.read("soundbox", "1");
        if (this.flashbox.equals("1")) {
            this.checkBoxflash.setChecked(true);
        }
        if (this.vibratebox.equals("1")) {
            this.checkBoxvibrate.setChecked(true);
        }
        if (this.soundbox.equals("1")) {
            this.checkBoxsound.setChecked(true);
        }
        seekBarValue();
        initialization();
        this.seekBar.setProgress(Integer.parseInt(this.classesApp.read("seekBar", "50")));
        initvolume();
        setProgresseBarInvisible();
        new CountDownTimer(5000, 500) {
            public void onTick(long j) {
                ActivitySetting.this.intOnTick = ActivitySetting.this.intOnTick + 1;
                if (ActivitySetting.this.intOnTick >= 9) {
                    ActivitySetting.this.setProgresseBarInvisible();
                }
            }

            public void onFinish() {
                ActivitySetting.this.setProgresseBarInvisible();
                ActivitySetting.this.intOnTick = 0;
            }
        }.start();
        checkCamFlash();
    }



    private boolean isMyServiceRunning(Class<?> cls) {
        for (RunningServiceInfo runningServiceInfo : ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void seekBarValue() {
        this.seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("");
                stringBuilder.append(i);
                ActivitySetting.this.classesApp.save("seekBar", stringBuilder.toString());
                ActivitySetting.this.setvolume(i);
            }
        });
    }

    private void checkbox() {
        if (!this.checkBoxflash.isChecked()) {
            this.classesApp.save("flashbox", "0");
        } else if (this.mPermCAm.booleanValue()) {
            this.classesApp.save("flashbox", "1");
        } else {
            this.classesApp.save("flashbox", "0");
        }
        if (this.checkBoxvibrate.isChecked()) {
            this.classesApp.save("vibratebox", "1");
        } else {
            this.classesApp.save("vibratebox", "0");
        }
        if (this.checkBoxsound.isChecked()) {
            this.classesApp.save("soundbox", "1");
        } else {
            this.classesApp.save("soundbox", "0");
        }
    }

    private void initvolume() {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.getStreamVolume(3);
        audioManager.setStreamVolume(3, (int) (((float) audioManager.getStreamMaxVolume(3)) * (Float.parseFloat(this.classesApp.read(NotificationCompat.CATEGORY_PROGRESS, "50")) / 100.0f)), 0);
    }

    private void setvolume(int i) {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(3, (int) (((float) audioManager.getStreamMaxVolume(3)) * (((float) i) / 100.0f)), 0);
    }

    private void initialization() {
        this.vocalButton = (ImageView) findViewById(R.id.startservice);
        this.vocalButton.setOnClickListener(this);
        this.mStop = (ImageView) findViewById(R.id.stop);
        this.mStop.setOnClickListener(this);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.startservice) {
            check();
        } else if (id == R.id.stop) {
            stopService(new Intent(this, VocalService.class));
            this.txtStart.setVisibility(View.INVISIBLE);
            this.stoplinear.setVisibility(View.INVISIBLE);
            this.btnStart.setVisibility(View.VISIBLE);
            if (ActivityVocalSignal.mySong != null) {
                ActivityVocalSignal.mySong.release();
            }
            Toast.makeText(this, "Detection stopped", Toast.LENGTH_LONG).show();
        }
    }

    public void checkCamFlash() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.CAMERA"}, 223);
            return;
        }
        this.mPermCAm = Boolean.valueOf(true);
    }

    public void check() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.RECORD_AUDIO"}, 123);
            return;
        }
        initializePlayerAndStartRecording();
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 123) {
            if (iArr.length > 0 && iArr[0] == 0) {
                initializePlayerAndStartRecording();
            }
        } else if (i == 223) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                this.mPermCAm = Boolean.valueOf(false);
            } else {
                this.mPermCAm = Boolean.valueOf(true);
            }
        }
    }

    private void initializePlayerAndStartRecording() {
        checkbox();
        this.classesApp.save("StopService", "0");
        startService(new Intent(getBaseContext(), VocalService.class));
        Toast.makeText(this, "Detection started", Toast.LENGTH_LONG).show();
        if (this.txtStart.getVisibility() == View.INVISIBLE) {
            this.txtStart.setVisibility(View.VISIBLE);
        }
        if (this.btnStart.getVisibility() == View.VISIBLE) {
            this.btnStart.setVisibility(View.INVISIBLE);
            this.stoplinear.setVisibility(View.VISIBLE);
        }
        if (MainActivity.MainIsRun) {
            try {
                MainActivity.activityMain.finish();
            } catch (Exception unused) {
            }
        }
    }

    public void startAlert() {
        ((AlarmManager) getSystemService(NotificationCompat.CATEGORY_ALARM)).set(0, System.currentTimeMillis() + ((long) 1000), PendingIntent.getBroadcast(getApplicationContext(), 234, new Intent(this, MyBroadcastReceiver.class), 0));
    }

    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(268435456);
        startActivity(intent);
        finish();
    }

    private void setProgresseBarInvisible() {
        if (this.mProgresseBar.getVisibility() == View.VISIBLE) {
            this.mProgresseBar.setVisibility(View.INVISIBLE);
        }
    }


    public void onDestroy() {
        super.onDestroy();
    }
}
