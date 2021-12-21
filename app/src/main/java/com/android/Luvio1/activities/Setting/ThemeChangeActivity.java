package com.android.Luvio1.activities.Setting;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio1.R;

public class ThemeChangeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int theme = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext())
                .getInt("Theme", R.style.Theme_Luvio);
        this.setTheme(theme);

        super.onCreate(savedInstanceState);
    }
}
