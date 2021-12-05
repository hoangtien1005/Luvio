package com.android.Luvio1.activities.Main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRadioButton;

import com.android.Luvio1.R;
import com.android.Luvio1.interfaces.PageCallback;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.RangeSlider;

import java.util.List;


public class SearchFilterActivity extends BottomSheetDialogFragment {
    private int layoutStyle;
    private PageCallback callback;
    TextView chosenStarScope, chosenAgeScope;
    RangeSlider ageScope, starScope;
    RadioGroup radioGroup;
    Button maleBtn, femaleBtn, othersBtn, finishBtn;


    public SearchFilterActivity(int layoutStyle, PageCallback callback) {
        this.layoutStyle = layoutStyle;
        this.callback = callback;
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
        radioGroup = view.findViewById(R.id.gender_group);
        maleBtn = view.findViewById(R.id.male_btn);
        femaleBtn = view.findViewById(R.id.female_btn);
        othersBtn = view.findViewById(R.id.others_btn);
        finishBtn = view.findViewById(R.id.finish_btn);


        dialog.setContentView(view);
        setListener();
    }

    private void setListener() {

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

        finishBtn.setOnClickListener(v -> {
            String data;
            List<Float> ageRange = ageScope.getValues();
            List<Float> starRange = starScope.getValues();
            String gender;
            switch (radioGroup.getCheckedRadioButtonId()) {
                case 1:
                    gender = "Nam";
                    break;
                case 2:
                    gender = "Nữ";
                    break;
                default:
                    gender = "Khác";
                    break;
            }
            data = starRange.get(0) + "|" + starRange.get(1) + "|" + ageRange.get(0).intValue() + "|" + ageRange.get(1).intValue() + "|" + gender;
            callback.callbackMethod(data);
            this.dismiss();
        });


        ageScope.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> values = slider.getValues();
                chosenAgeScope.setText(values.get(0).intValue() + " - " + values.get(1).intValue());
            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> values = slider.getValues();
                chosenAgeScope.setText(values.get(0).intValue() + " - " + values.get(1).intValue());
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

