package com.android.Luvio.activities.Setting;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio.activities.Auth.ForgotPasswordActivity3;
import com.android.Luvio.databinding.ActivitySettingBinding;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();

    }
    private void setListener(){

        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.btnChangePassword.setOnClickListener(v->{
            Intent intent=new Intent(getApplicationContext(), ForgotPasswordActivity3.class);

        });
    }

}
