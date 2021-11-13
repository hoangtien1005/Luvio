package com.android.Luvio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.Luvio.R;
import com.android.Luvio.databinding.ActivityInterestBinding;
import com.android.Luvio.databinding.ActivityVerifyPhoneNumberBinding;

public class InterestActivity extends AppCompatActivity {
    private ActivityInterestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityInterestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }
    private void setListener(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}