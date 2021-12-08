package com.android.Luvio1.activities.Setting;

import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.Luvio1.R;

public class ThemeChangeFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        int theme = PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .getInt("Theme", R.style.Theme_Luvio);
        getContext().setTheme(theme);
        super.onCreate(savedInstanceState);
    }
}
