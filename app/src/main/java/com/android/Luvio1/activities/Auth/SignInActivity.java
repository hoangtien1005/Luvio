package com.android.Luvio1.activities.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio1.activities.Main.HomePageActivity;
import com.android.Luvio1.databinding.ActivitySignInBinding;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent =new Intent(getApplicationContext(), HomePageActivity.class);
            startActivity(intent);
            finish();
        }

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setListener();
    }

    private void signIn(){
        loading(true);
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USER)
                .whereEqualTo(Constants.KEY_IS_DELETE,false)
                .whereEqualTo(Constants.KEY_PHONE_NUMBER,binding.edtPhoneNumber.getText().toString().trim())
                .whereEqualTo(Constants.KEY_PASSWORD,binding.edtPassword.getText().toString().trim())
                .get()
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()&& task.getResult() !=null && task.getResult().getDocuments().size()>0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_STAR, documentSnapshot.getString(Constants.KEY_STAR));
                        preferenceManager.putString(Constants.KEY_ABOUT_ME, documentSnapshot.getString(Constants.KEY_ABOUT_ME));
                        preferenceManager.putString(Constants.KEY_PHONE_NUMBER,documentSnapshot.getString(Constants.KEY_PHONE_NUMBER));
                        preferenceManager.putString(Constants.KEY_COUNTRY_CODE,documentSnapshot.getString(Constants.KEY_COUNTRY_CODE));
                        preferenceManager.putString(Constants.KEY_LAST_NAME,documentSnapshot.getString(Constants.KEY_LAST_NAME));
                        preferenceManager.putString(Constants.KEY_FIRST_NAME,documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                        preferenceManager.putString(Constants.KEY_AVATAR,documentSnapshot.getString(Constants.KEY_AVATAR));
                        preferenceManager.putString(Constants.KEY_BIRTHDAY,documentSnapshot.getString(Constants.KEY_BIRTHDAY));
                        preferenceManager.putString(Constants.KEY_GENDER,documentSnapshot.getString(Constants.KEY_GENDER));
                        preferenceManager.putString(Constants.KEY_INTERESTED_GENDER, documentSnapshot.getString(Constants.KEY_INTERESTED_GENDER));
                        ArrayList<String> al= (ArrayList<String>) documentSnapshot.get(Constants.KEY_INTERESTS);
                        String[] interests = new String[al.size()];

                        for (int i = 0; i < al.size(); i++) {
                            interests[i] = al.get(i);
                        }
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < interests.length; i++) {
                            sb.append(interests[i]).append(",");
                        }
                        preferenceManager.putString(Constants.KEY_INTERESTS, sb.toString());
                        Intent intent=new Intent(getApplicationContext(), HomePageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }
                    else{
                        loading(false);
                        showToast("Không thể đăng nhập");
                    }
                });
    }
    private void setListener(){
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 startActivity(new Intent(getApplicationContext(),PhoneNumberActivity.class));
            }
        });
        binding.btnForgotPassword.setOnClickListener(view -> {
            Intent intent=new Intent(SignInActivity.this,ForgotPasswordActivity1.class);
            startActivity(intent);
        });
        binding.signInButton.setOnClickListener(view -> {
            if(isValidSignIn()){
                signIn();
            }
        });
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
    private boolean isValidSignIn(){
        if(binding.edtPhoneNumber.getText().toString().isEmpty()){
            showToast("Nhập số điện thoại");
            return false;

        }
        else if(!Patterns.PHONE.matcher(binding.edtPhoneNumber.getText().toString()).matches()){
            showToast("Số điện thoại không hợp lệ");
            return false;
        }
        else if (binding.edtPassword.getText().toString().isEmpty()){
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