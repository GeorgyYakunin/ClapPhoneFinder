package com.example.ClapClapToFindPhone;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

class ClassesApp {

    private Context mContext;

    public ClassesApp(Context context) {

        this.mContext = context;

    }

    public String read(String str, String str2) {
        return PreferenceManager.getDefaultSharedPreferences(this.mContext).getString(str, str2);
    }

    public void save(String str, String str2) {
        Editor edit = PreferenceManager.getDefaultSharedPreferences(this.mContext).edit();
        edit.putString(str, str2);
        edit.commit();
    }
}
