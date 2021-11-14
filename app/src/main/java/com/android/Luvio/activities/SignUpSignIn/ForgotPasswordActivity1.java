package com.android.Luvio.activities.SignUpSignIn;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio.databinding.ActivityForgotPassword1Binding;

public class ForgotPasswordActivity1 extends AppCompatActivity {
    private ActivityForgotPassword1Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityForgotPassword1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListenner();

    }
    private void setListenner(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}