package com.android.Luvio.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio.R;
import com.android.Luvio.databinding.ActivityForgotPassword3Binding;

public class ForgotPasswordActivity3 extends AppCompatActivity {
    private ActivityForgotPassword3Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityForgotPassword3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }
    private void setListener(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

}