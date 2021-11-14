package com.android.Luvio.activities.SignUpSignIn;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio.databinding.ActivityVerifyPhoneNumberBinding;

public class VerifyPhoneNumberActivity extends AppCompatActivity {
    private ActivityVerifyPhoneNumberBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityVerifyPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }
    private void setListener(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}