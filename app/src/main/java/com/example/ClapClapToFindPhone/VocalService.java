package com.example.ClapClapToFindPhone;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;


public class VocalService extends Service implements OnSignalsDetectedListener {

    public static final int DETECT_NONE = 0;
    public static final int DETECT_WHISTLE = 1;
    private static final int NOTIFICATION_Id = 1;

    public static int selectedDetection;
    private ClassesApp classesApp;
    private DetectorThread detectorThread;
    private RecorderThread recorderThread;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        startDetection();
        return 1;
    }

    public void startDetection() {
        try {
            new DetectClapClap(getApplicationContext()).listen();
            this.classesApp = new ClassesApp(this);
            this.classesApp.save("detectClap", "0");
        } catch (Exception unused) {
            Toast.makeText(this, "Recorder not supported by this device", 1).show();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        RecorderThread recorderThread = this.recorderThread;
        if (recorderThread != null) {
            recorderThread.stopRecording();
            this.recorderThread = null;
        }
        DetectorThread detectorThread = this.detectorThread;
        if (detectorThread != null) {
            detectorThread.stopDetection();
            this.detectorThread = null;
        }
        selectedDetection = 0;
        Toast.makeText(this, "Detection stopped", 1).show();
    }

    public void onWhistleDetected() {
        Intent intent = new Intent(this, ActivityVocalSignal.class);
        intent.setFlags(268435456);
        startActivity(intent);
        Toast.makeText(this, "Clap detected", 1).show();
        stopSelf();
    }
}
