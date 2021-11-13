package com.android.Luvio.activities.SignUp;

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
        setListenner();

    }
    private void setListenner(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}