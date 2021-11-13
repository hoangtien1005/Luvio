package com.android.Luvio.activities.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.Luvio.R;
import com.android.Luvio.databinding.ActivityVerifyPhoneNumberBinding;

public class VerifyPhoneNumberActivity extends AppCompatActivity {
    private ActivityVerifyPhoneNumberBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityVerifyPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }
    private void setListener(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }
}