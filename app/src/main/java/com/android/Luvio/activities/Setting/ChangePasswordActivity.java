package com.android.Luvio.activities.Setting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.Luvio.databinding.ActivityChangePasswordBinding;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }


}