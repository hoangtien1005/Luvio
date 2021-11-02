package com.android.Luvio.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio.databinding.ActivitySignInBinding;

import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setListener();
    }
    private void setListener(){
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 startActivity(new Intent(getApplicationContext(),PhoneNumberActivity.class));
            }
        });
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
    private boolean isValidSignIn(){
        if(binding.edtPhoneNumber.toString().isEmpty()){
            showToast("Nhập số điện thoại");
            return false;

        }
        else if(!Patterns.PHONE.matcher(binding.edtPhoneNumber.toString()).matches()){
            showToast("Số điện thoại không hợp lệ");
            return false;
        }
        else if (binding.edtPassword.toString().isEmpty()){
            showToast("Nhập mật khẩu");
            return false;
        }
        return true;
    }
    private void loading(boolean isLoading){
        if(isLoading){
            binding.signInButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.signInButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}