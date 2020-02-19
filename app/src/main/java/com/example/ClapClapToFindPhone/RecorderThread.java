package com.example.ClapClapToFindPhone;

import android.media.AudioRecord;

public class RecorderThread extends Thread {
    private int audioEncoding = 2;
    private AudioRecord audioRecord = new AudioRecord(1, this.sampleRate, this.channelConfiguration, this.audioEncoding, AudioRecord.getMinBufferSize(this.sampleRate, this.channelConfiguration, this.audioEncoding));
    byte[] buffer = new byte[this.frameByteSize];
    private int channelConfiguration = 16;
    private int frameByteSize = 2048;
    private boolean isRecording;
    private int rateSupported;
    private boolean rate_send;
    private int sampleRate = getValidSampleRates();

    public int getValidSampleRates() {
        for (int i : new int[]{44100, 22050, 16000, 11025, 8000}) {
            if (AudioRecord.getMinBufferSize(i, 1, 2) > 0 && !this.rate_send) {
                this.rateSupported = i;
                this.rate_send = true;
            }
        }
        return this.rateSupported;
    }

    public AudioRecord getAudioRecord() {
        return this.audioRecord;
    }

    public boolean isRecording() {
        return isAlive() && this.isRecording;
    }

    public void startRecording() {
        try {
            this.audioRecord.startRecording();
            this.isRecording = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        try {
            this.audioRecord.stop();
            this.audioRecord.release();
            this.isRecording = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getFrameBytes() {
        int i;
        int i2 = 0;
        this.audioRecord.read(this.buffer, 0, this.frameByteSize);
        int i3 = 0;
        while (true) {
            i = this.frameByteSize;
            if (i2 >= i) {
                break;
            }
            byte[] bArr = this.buffer;
            i3 += Math.abs((short) ((bArr[i2 + 1] << 8) | bArr[i2]));
            i2 += 2;
        }
        if (((float) ((i3 / i) / 2)) < 30.0f) {
            return null;
        }
        return this.buffer;
    }

    public void run() {
        startRecording();
    }
}
