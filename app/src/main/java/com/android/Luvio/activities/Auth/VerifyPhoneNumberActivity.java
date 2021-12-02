package com.android.Luvio.activities.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.android.Luvio.R;
import com.android.Luvio.databinding.ActivityVerifyPhoneNumberBinding;
import com.android.Luvio.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneNumberActivity extends AppCompatActivity {
    private ActivityVerifyPhoneNumberBinding binding;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityVerifyPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        verificationId=getIntent().getExtras().getString("verificationId");

        setListener();
        setupOTPInput();
    }

    private void setListener(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.verifyButton.setOnClickListener(view -> {
            loading(true);
            if (!isValid()){
                loading(false);
                return;
            }
            else{

                Intent intent=getIntent();
                Bundle bundleData=intent.getExtras();
                String code=binding.inputCode1.getText().toString()+
                        binding.inputCode2.getText().toString()+
                        binding.inputCode3.getText().toString()+
                        binding.inputCode4.getText().toString()+
                        binding.inputCode5.getText().toString()+
                        binding.inputCode6.getText().toString();
                if (verificationId!=null){
                    loading(false);
                    PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(
                            verificationId,
                            code
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){
                                        Intent intent1=new Intent(getApplicationContext(),PersonalInformationActivity1.class);
                                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        bundleData.remove("verificationId");
                                        intent1.putExtras(bundleData);
                                        startActivity(intent1);
                                    }
                                    else{
                                        loading(false);
                                        showToast("Mã xác thực không hợp lệ");
                                    }
                                }
                            });

                }
            }
        });
        binding.resendVerficationCode.setOnClickListener(view -> {
            Bundle bundleData=getIntent().getExtras();
            PhoneAuthOptions options=
                    PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                            .setPhoneNumber(bundleData.getString(Constants.KEY_COUNTRY_CODE)+bundleData.getString(Constants.KEY_PHONE_NUMBER))
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(this)
                            .setCallbacks(mCallBack)
                            .setForceResendingToken(mForceResendingToken)
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack=new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            verificationId=s;
            mForceResendingToken=forceResendingToken;
            showToast("Đã gửi lại mã xác thực");
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

            showToast(e.getMessage());
        }
    };

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
    private boolean isValid(){
        if (binding.inputCode1.getText().toString().isEmpty()
                ||binding.inputCode2.getText().toString().isEmpty()
                ||binding.inputCode3.getText().toString().isEmpty()
                ||binding.inputCode4.getText().toString().isEmpty()
                ||binding.inputCode5.getText().toString().isEmpty()
                ||binding.inputCode6.getText().toString().isEmpty()){
            showToast("Vui lòng nhập mã xác thực");
            return false;
        }
        return true;
    }
    private void setupOTPInput(){
        binding.inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    binding.inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    binding.inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    binding.inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    binding.inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!charSequence.toString().trim().isEmpty()){
                    binding.inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void loading(boolean isLoading){
        if(isLoading){
            binding.verifyButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.verifyButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}