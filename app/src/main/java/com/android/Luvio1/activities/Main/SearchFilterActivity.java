package com.android.Luvio1.activities.Main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.Luvio1.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.RangeSlider;

import java.util.List;


public class SearchFilterActivity extends BottomSheetDialogFragment{
    private int layoutStyle;
    TextView chosenStarScope, chosenAgeScope;
    RangeSlider ageScope, starScope;
    Button maleBtn, femaleBtn, othersBtn, finishBtn;

    public SearchFilterActivity(int layoutStyle){
        this.layoutStyle = layoutStyle;
    }


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {

        super.setupDialog(dialog, style);
        View view = View.inflate(getContext(), this.layoutStyle, null);
        chosenAgeScope = view.findViewById(R.id.chosen_age_scope);
        chosenStarScope = view.findViewById(R.id.chosen_star_scope);
        ageScope = view.findViewById(R.id.age_scope);
        starScope = view.findViewById(R.id.star_scope);
        maleBtn = view.findViewById(R.id.male_btn);
        femaleBtn = view.findViewById(R.id.female_btn);
        othersBtn = view.findViewById(R.id.others_btn);
        finishBtn = view.findViewById(R.id.finish_btn);


        dialog.setContentView(view);
        setListener();
    }

    private void setListener()
    {

        maleBtn.setOnClickListener(view ->
        {
            maleBtn.setTextColor(Color.WHITE);
            femaleBtn.setTextColor(Color.BLACK);
            othersBtn.setTextColor(Color.BLACK);

        });

        femaleBtn.setOnClickListener(view ->
        {
            maleBtn.setTextColor(Color.BLACK);
            femaleBtn.setTextColor(Color.WHITE);
            othersBtn.setTextColor(Color.BLACK);

        });

        othersBtn.setOnClickListener(view ->
        {
            maleBtn.setTextColor(Color.BLACK);
            femaleBtn.setTextColor(Color.BLACK);
            othersBtn.setTextColor(Color.WHITE);

        });

        ageScope.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> values = slider.getValues();
                chosenAgeScope.setText(values.get(0) + " - " + values.get(1));
            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> values = slider.getValues();
                chosenAgeScope.setText(values.get(0) + " - " + values.get(1));
            }
        });

        starScope.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> values = slider.getValues();
                chosenStarScope.setText(values.get(0) + " - " + values.get(1));
            }
            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> values = slider.getValues();
                chosenStarScope.setText(values.get(0) + " - " + values.get(1));
            }
        });

    }
}

