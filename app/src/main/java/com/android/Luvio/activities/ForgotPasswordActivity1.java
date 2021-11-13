package com.android.Luvio.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.Luvio.R;
import com.android.Luvio.databinding.ActivityForgotPassword1Binding;

public class ForgotPasswordActivity1 extends AppCompatActivity {
    private ActivityForgotPassword1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityForgotPassword1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();

    }
    private void setListener(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}