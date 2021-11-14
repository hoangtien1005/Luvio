package com.android.Luvio.activities.SignUpSignIn;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio.databinding.ActivityForgotPassword2Binding;

public class ForgotPasswordActivity2 extends AppCompatActivity {
    private ActivityForgotPassword2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityForgotPassword2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListenner();

    }
    private void setListenner(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

}