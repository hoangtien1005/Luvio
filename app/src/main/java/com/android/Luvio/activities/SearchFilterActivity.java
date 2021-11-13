package com.android.Luvio.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio.databinding.SearchFilterBinding;
import com.google.android.material.slider.RangeSlider;

import java.util.List;

public class SearchFilterActivity extends AppCompatActivity {
    private SearchFilterBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SearchFilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }

    private void setListener()
    {
        binding.finishBtn.setOnClickListener(view ->
        {
            onBackPressed();
        });

        binding.maleBtn.setOnClickListener(view ->
        {
            binding.maleBtn.setTextColor(Color.WHITE);
            binding.femaleBtn.setTextColor(Color.BLACK);
            binding.othersBtn.setTextColor(Color.BLACK);

        });

        binding.femaleBtn.setOnClickListener(view ->
        {
            binding.maleBtn.setTextColor(Color.BLACK);
            binding.femaleBtn.setTextColor(Color.WHITE);
            binding.othersBtn.setTextColor(Color.BLACK);

        });

        binding.othersBtn.setOnClickListener(view ->
        {
            binding.maleBtn.setTextColor(Color.BLACK);
            binding.femaleBtn.setTextColor(Color.BLACK);
            binding.othersBtn.setTextColor(Color.WHITE);

        });

        binding.ageScope.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> values = slider.getValues();
                binding.chosenAgeScope.setText(values.get(0) + " - " + values.get(1));
            }

            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> values = slider.getValues();
                binding.chosenAgeScope.setText(values.get(0) + " - " + values.get(1));
            }
        });

        binding.starScope.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> values = slider.getValues();
                binding.chosenStarScope.setText(values.get(0) + " - " + values.get(1));
            }
            @Override
            public void onStopTrackingTouch(@NonNull RangeSlider slider) {
                List<Float> values = slider.getValues();
                binding.chosenStarScope.setText(values.get(0) + " - " + values.get(1));
            }
        });

    }

}


