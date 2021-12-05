package com.android.Luvio1.activities.Setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.Luvio1.activities.Auth.SignInActivity;
import com.android.Luvio1.databinding.ActivityChangePasswordBinding;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;
    String documentID;
    PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        setListener();
    }
    private void setListener(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.confirmButton.setOnClickListener(view -> {
            if(isValid()){
                loading(true);

                FirebaseFirestore db= FirebaseFirestore.getInstance();
                db.collection(Constants.KEY_COLLECTION_USER)
                        .document(preferenceManager.getString(Constants.KEY_USER_ID))
                        .update(Constants.KEY_PASSWORD,binding.edtNewPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                showToast("Mật khẩu đã thay đổi thành công");
                                Intent intent1 = new Intent(getApplicationContext(), SignInActivity.class);
                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent1);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loading(false);
                                showToast("Không thể thay đổi mật khẩu");
                            }
                        });
            }
        });
    }


    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
    private boolean isValid(){
        if(binding.edtNewPassword.getText().toString().trim().isEmpty()){
            showToast("Nhập mật khẩu mới");
            return false;
        }
        else if(binding.edtRetypeNewPassword.getText().toString().trim().isEmpty()){
            showToast("Chưa nhập lại mật khẩu");
            return false;
        }
        else if(!binding.edtNewPassword.getText().toString().trim().equals(binding.edtRetypeNewPassword.getText().toString().trim())){
            showToast("Mật khẩu nhập lại không trùng khớp");
            return false;

        }
        return true;
    }

    private void loading(boolean isLoading){
        if (isLoading){
            binding.confirmButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.confirmButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

}