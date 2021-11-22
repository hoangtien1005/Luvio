package com.android.Luvio.activities.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.Luvio.R;
import com.android.Luvio.databinding.ActivityPhoneNumberBinding;
import com.android.Luvio.utilities.Constants;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneNumberActivity extends AppCompatActivity  {
    private ActivityPhoneNumberBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }
    private void setListener(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
        ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.country_code));
        binding.countryCode.setAdapter(myAdapter);
        binding.nextButtonLayout.setOnClickListener(view -> {
            if(isValidPhoneNumber()){
                loading(true);
                PhoneAuthOptions options=
                        PhoneAuthOptions.newBuilder()
                            .setPhoneNumber(String.format(binding.countryCode.getText().toString(),binding.edtPhoneNumber.getText().toString()))
                            .setTimeout(60L,TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(mCallBack)
                            .build();
                PhoneAuthProvider.verifyPhoneNumber(options);

            }

        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack=new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            loading(false);
            Bundle bundle=new Bundle();
            Intent intent=new Intent(getApplicationContext(),VerifyPhoneNumberActivity.class);
            bundle.putString(Constants.KEY_PHONE_NUMBER,binding.edtPhoneNumber.getText().toString().trim());
            bundle.putString(Constants.KEY_COUNTRY_CODE, binding.countryCode.getText().toString().trim());
            bundle.putString("verificationId",s);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            loading(false);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            loading(false);
            showToast(e.getMessage());
        }
    };
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
    private boolean isValidPhoneNumber(){
        if(binding.edtPhoneNumber.toString().isEmpty()){
            showToast("Nhập số điện thoại");
            return false;

        }
        else if(!Patterns.PHONE.matcher(binding.edtPhoneNumber.toString()).matches()){
            showToast("Số điện thoại không hợp lệ");
            return false;
        }
        return true;
    }
    private void loading(boolean isLoading){
        if(isLoading){
            binding.nextButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.nextButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

}