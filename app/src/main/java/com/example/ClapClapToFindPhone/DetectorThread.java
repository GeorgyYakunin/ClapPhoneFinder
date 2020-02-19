package com.example.ClapClapToFindPhone;

import android.media.AudioRecord;
import com.musicg.api.WhistleApi;
import com.musicg.wave.WaveHeader;
import java.util.LinkedList;

public class DetectorThread extends Thread {

    private volatile Thread _thread;
    private int numWhistles;
    private OnSignalsDetectedListener onSignalsDetectedListener;
    private RecorderThread recorder;
    private WaveHeader waveHeader;
    private WhistleApi whistleApi;
    private int whistleCheckLength = 3;
    private int whistlePassScore = 3;
    private LinkedList<Boolean> whistleResultList = new LinkedList();

    public DetectorThread(RecorderThread recorderThread) {
        this.recorder = recorderThread;
        AudioRecord audioRecord = recorderThread.getAudioRecord();
        int i = 0;
        int i2 = audioRecord.getAudioFormat() == 2 ? 16 : audioRecord.getAudioFormat() == 3 ? 8 : 0;
        if (audioRecord.getChannelConfiguration() == 16) {
            i = 1;
        }
        this.waveHeader = new WaveHeader();
        this.waveHeader.setChannels(i);
        this.waveHeader.setBitsPerSample(i2);
        this.waveHeader.setSampleRate(audioRecord.getSampleRate());
        this.whistleApi = new WhistleApi(this.waveHeader);
    }

    private void initBuffer() {
        this.numWhistles = 0;
        this.whistleResultList.clear();
        for (int i = 0; i < this.whistleCheckLength; i++) {
            this.whistleResultList.add(Boolean.valueOf(false));
        }
    }

    public void start() {
        this._thread = new Thread(this);
        this._thread.start();
    }

    public void stopDetection() {
        this._thread = null;
    }

    public void run() {
        try {
            initBuffer();
            Thread currentThread = Thread.currentThread();
            while (this._thread == currentThread) {
                byte[] frameBytes = this.recorder.getFrameBytes();
                if (frameBytes != null) {
                    boolean isWhistle = this.whistleApi.isWhistle(frameBytes);
                    if (((Boolean) this.whistleResultList.getFirst()).booleanValue()) {
                        this.numWhistles--;
                    }
                    this.whistleResultList.removeFirst();
                    this.whistleResultList.add(Boolean.valueOf(isWhistle));
                    if (isWhistle) {
                        this.numWhistles++;
                    }
                    if (this.numWhistles >= this.whistlePassScore) {
                        initBuffer();
                        onWhistleDetected();
                    }
                } else {
                    if (((Boolean) this.whistleResultList.getFirst()).booleanValue()) {
                        this.numWhistles--;
                    }
                    this.whistleResultList.removeFirst();
                    this.whistleResultList.add(Boolean.valueOf(false));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onWhistleDetected() {
        OnSignalsDetectedListener onSignalsDetectedListener = this.onSignalsDetectedListener;
        if (onSignalsDetectedListener != null) {
            onSignalsDetectedListener.onWhistleDetected();
        }
    }

    public void setOnSignalsDetectedListener(OnSignalsDetectedListener onSignalsDetectedListener) {
        this.onSignalsDetectedListener = onSignalsDetectedListener;
    }
}
