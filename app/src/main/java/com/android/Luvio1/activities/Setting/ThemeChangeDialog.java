package com.android.Luvio1.activities.Setting;

import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;

import com.android.Luvio1.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ThemeChangeDialog extends BottomSheetDialogFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        int theme = PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .getInt("Theme", R.style.Theme_Luvio);
        getActivity().setTheme(theme);
        super.onCreate(savedInstanceState);
    }
}
