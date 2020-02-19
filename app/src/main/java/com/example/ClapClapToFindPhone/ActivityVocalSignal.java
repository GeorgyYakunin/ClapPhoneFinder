package com.example.ClapClapToFindPhone;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

public class ActivityVocalSignal extends Activity implements OnCompletionListener, Callback {
    public static MediaPlayer mySong;
    private Camera camera;
    private ClassesApp classesApp;
    private boolean deviceHasCameraFlash;
    private String flashbox;
    private boolean hasFlash;
    private boolean isFlashOn;
    private Context mCtx;
    SurfaceHolder mHolder;
    private RelativeLayout mProgresseBar;
    private CountDownTimer mTimer = null;
    private ImageView mhome;
    MediaPlayer mp;
    private ImageView mstop;
    Parameters params;
    SurfaceView preview;
    private boolean run;
    private String soundbox;
    private TextView txtsettingtext;
    private Vibrator v;
    private String vibratebox;

    public void onPointerCaptureChanged(boolean z) {
    }

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_vocal_signal);

        this.mProgresseBar = (RelativeLayout) findViewById(R.id.progressBar);
        this.classesApp = new ClassesApp(this);
        setProgresseBarInvisible();
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.adViewContainer);
        this.mCtx = this;
        this.deviceHasCameraFlash = getPackageManager().hasSystemFeature("android.hardware.camera.flash");
        initialize();
        ClassesApp classesApp = new ClassesApp(this.mCtx);
        classesApp.save("StopService", "1");
        this.flashbox = classesApp.read("flashbox", "1");
        this.vibratebox = classesApp.read("vibratebox", "1");
        this.soundbox = classesApp.read("soundbox", "1");
        classesApp.save("detectClap", "1");
        stopService(new Intent(getBaseContext(), VocalService.class));
        setvolume(Integer.parseInt(classesApp.read("seekBar", "50")));
        getWindow().addFlags(128);
        if (this.flashbox.equals("1") && this.deviceHasCameraFlash) {
            this.isFlashOn = false;
            getCamera();
            startTimer(1000, true);
        }
        if (this.vibratebox.equals("1")) {
            runvibrate(true);
        }
        if (this.soundbox.equals("1")) {
            runsong();
        }
        setProgresseBarInvisible();
    }

    private void setvolume(int i) {
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        audioManager.setStreamVolume(3, (int) (((float) audioManager.getStreamMaxVolume(3)) * (((float) i) / 100.0f)), 0);
    }

    private void runvibrate(boolean z) {
        this.run = z;
        Context context = this.mCtx;
        this.v = (Vibrator) getSystemService("vibrator");
        new Thread(new Runnable() {
            public void run() {
                while (ActivityVocalSignal.this.run) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        ActivityVocalSignal.this.v.vibrate(1000);
                    } catch (Exception unused) {
                    }
                }
            }
        }).start();
    }

    private void runsong() {
        mySong = MediaPlayer.create(this, R.raw.alarm);
        mySong.setOnCompletionListener(this);
        mySong.start();
    }

    private void initialize() {
        this.txtsettingtext = (TextView) findViewById(R.id.settingtext);
        this.mstop = (ImageView) findViewById(R.id.stop);
        this.mhome = (ImageView) findViewById(R.id.home);
        this.mstop.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (ActivityVocalSignal.this.mTimer != null) {
                    ActivityVocalSignal.this.mTimer.cancel();
                }
                ActivityVocalSignal.this.turnOffFlash();
                if (ActivityVocalSignal.mySong != null) {
                    ActivityVocalSignal.mySong.release();
                }
                ActivityVocalSignal.this.run = false;
                ActivityVocalSignal.this.runvibrate(false);
                ActivityVocalSignal.this.mstop.setVisibility(4);
                ActivityVocalSignal.this.mhome.setVisibility(0);
                ActivityVocalSignal.this.txtsettingtext.setText("ALARM STOPPED");
                ActivityVocalSignal.this.txtsettingtext.setTextColor(ActivityVocalSignal.this.getResources().getColor(R.color.green));
            }
        });
        this.mhome.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(ActivityVocalSignal.this, MainActivity.class);
                intent.setFlags(268435456);
                ActivityVocalSignal.this.startActivity(intent);
                ActivityVocalSignal.this.finish();
            }
        });
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    /* Access modifiers changed, original: 0000 */
    public void startTimer(long j, boolean z) {
        this.run = z;
        if (this.run) {
            this.mTimer = new CountDownTimer(j, 1000) {
                public void onTick(long j) {
                }

                public void onFinish() {
                    if (ActivityVocalSignal.this.isFlashOn) {
                        ActivityVocalSignal.this.turnOffFlash();
                    } else {
                        ActivityVocalSignal.this.turnOnFlash();
                    }
                    ActivityVocalSignal activityVocalSignal = ActivityVocalSignal.this;
                    activityVocalSignal.startTimer(1000, activityVocalSignal.run);
                }
            };
            this.mTimer.start();
        }
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.mHolder = surfaceHolder;
        try {
            this.camera.setPreviewDisplay(this.mHolder);
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        try {
            this.camera.stopPreview();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        this.mHolder = null;
    }


    public void onDestroy() {
        try {
            ActivitySetting.activitySetting.finish();
        } catch (Exception unused) {
        }
        super.onDestroy();
    }


    public void onPause() {
        super.onPause();
        turnOffFlash();
    }


    public void onRestart() {
        super.onRestart();
    }


    public void onResume() {
        super.onResume();
        if (this.hasFlash) {
            turnOnFlash();
        }
    }


    public void onStart() {
        super.onStart();
        getCamera();
    }


    public void onStop() {
        super.onStop();
        Camera camera = this.camera;
        if (camera != null) {
            camera.release();
            this.camera = null;
        }
    }

    public void checkFlash() {
        this.hasFlash = getApplicationContext().getPackageManager().hasSystemFeature("android.hardware.camera.flash");
        if (this.hasFlash) {
            Log.d("FFF", "c'è il flash");
            return;
        }
        Builder builder = new Builder(this);
        builder.setTitle("Errore!");
        builder.setMessage("Il tuo telefono non ha il flash!");
        builder.setPositiveButton("OK, compro un Nexus", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityVocalSignal.this.finish();
            }
        });
    }

    private void getCamera() {
        this.preview = (SurfaceView) findViewById(R.id.PREVIEW);
        this.mHolder = this.preview.getHolder();
        this.mHolder.addCallback(this);
        this.mHolder.setType(3);
        if (this.camera == null) {
            try {
                this.camera = Camera.open();
                this.params = this.camera.getParameters();
                try {
                    this.camera.setPreviewDisplay(this.mHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (RuntimeException unused) {
            }
        }
    }

    private void turnOnFlash() {
        if (!this.isFlashOn) {
            Camera camera = this.camera;
            if (camera != null && this.params != null) {
                this.isFlashOn = true;
                try {
                    this.params = camera.getParameters();
                    this.params.setFlashMode("torch");
                    this.camera.setParameters(this.params);
                    this.camera.startPreview();
                } catch (Exception unused) {
                }
            }
        }
    }

    private void turnOffFlash() {
        if (this.isFlashOn) {
            Camera camera = this.camera;
            if (camera != null && this.params != null) {
                this.isFlashOn = false;
                try {
                    this.params = camera.getParameters();
                    this.params.setFlashMode("off");
                    this.camera.setParameters(this.params);
                    this.camera.stopPreview();
                } catch (Exception unused) {
                }
            }
        }
    }

    public void onBackPressed() {
        CountDownTimer countDownTimer = this.mTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        turnOffFlash();
        MediaPlayer mediaPlayer = mySong;
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        this.run = false;
        runvibrate(false);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(268435456);
        startActivity(intent);
        finish();
    }

    private void setProgresseBarInvisible() {
        if (this.mProgresseBar.getVisibility() == 0) {
            this.mProgresseBar.setVisibility(4);
        }
    }
}
