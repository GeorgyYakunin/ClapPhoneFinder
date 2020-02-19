package com.example.ClapClapToFindPhone;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MainActivity extends Activity {

    public static boolean MainIsRun;
    public static Activity activityMain;
    private ClassesApp classesApp;
    private int intOnTick;
    private RelativeLayout mProgresseBar;
    private ImageView mRateit;
    private ImageView mStop;
    private ImageView start;


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        activityMain = this;

        this.intOnTick = 0;
        ImageView imageView = (ImageView) findViewById(R.id.moreapps);
        MainIsRun = true;
        this.start = (ImageView) findViewById(R.id.setting);
        this.classesApp = new ClassesApp(this);
        isMyServiceRunning(VocalService.class);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.adViewContainerIndex);
        imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://developer?id="+getString(R.string.more)));
                intent.addFlags(1208483840);
                try {
                    MainActivity.this.startActivity(intent);
                } catch (ActivityNotFoundException unused) {
                    MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/developer?id="+getString(R.string.more))));
                }
            }
        });
        this.mRateit = (ImageView) findViewById(R.id.rateit);
        this.mRateit.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("market://details?id=");
                stringBuilder.append(MainActivity.this.getBaseContext().getPackageName());
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString()));
                intent.addFlags(1208483840);
                try {
                    MainActivity.this.startActivity(intent);
                } catch (ActivityNotFoundException unused) {
                    MainActivity mainActivity = MainActivity.this;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("http://play.google.com/store/apps/details?id=");
                    stringBuilder2.append(MainActivity.this.getBaseContext().getPackageName());
                    mainActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder2.toString())));
                }
            }
        });
        imageView = (ImageView) findViewById(R.id.shareapp);
        this.mProgresseBar = (RelativeLayout) findViewById(R.id.progressBar);
        this.mProgresseBar.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
            }
        });
        if (this.mProgresseBar.getVisibility() == View.INVISIBLE) {
            this.mProgresseBar.setVisibility(View.VISIBLE);
        }
        imageView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("https://play.google.com/store/apps/details?id=");
                stringBuilder.append(MainActivity.this.getBaseContext().getPackageName());
                String stringBuilder2 = stringBuilder.toString();
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                intent.putExtra("android.intent.extra.SUBJECT", MainActivity.this.getResources().getText(R.string.app_name));
                intent.putExtra("android.intent.extra.TEXT", stringBuilder2);
                MainActivity.this.startActivity(Intent.createChooser(intent, ""));
            }
        });
        this.start.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ActivitySetting.class);
                    intent.setFlags(268435456);
                    MainActivity.this.startActivity(intent);
            }
        });
        setProgresseBarInvisible();
        new CountDownTimer(3500, 500) {
            public void onTick(long j) {
                MainActivity.this.intOnTick = MainActivity.this.intOnTick + 1;
                if (MainActivity.this.intOnTick >= 6) {
                    MainActivity.this.setProgresseBarInvisible();
                }
            }

            public void onFinish() {
                MainActivity.this.setProgresseBarInvisible();
                MainActivity.this.intOnTick = 0;
            }
        }.start();
        if (!isNetworkAvailable(this)) {
            setProgresseBarInvisible();
        }

    }


    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 3;
    }

    private boolean isMyServiceRunning(Class<?> cls) {
        for (RunningServiceInfo runningServiceInfo : ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(runningServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setProgresseBarInvisible() {
        if (this.mProgresseBar.getVisibility() == View.VISIBLE) {
            this.mProgresseBar.setVisibility(View.INVISIBLE);
        }
    }


    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void onPause() {
        super.onPause();
    }


    public void onResume() {
        super.onResume();
    }


    public void onDestroy() {
        MainIsRun = false;
        super.onDestroy();
    }

    public void onBackPressed() {
        this.classesApp.save("AdsIsShowed", "0");
        try {
            ActivitySetting.activitySetting.finish();
        } catch (Exception unused) {
        }
        finish();
    }
}
