package com.android.Luvio.activities.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio.R;
import com.android.Luvio.databinding.ActivityForgotPassword3Binding;
import com.android.Luvio.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class ForgotPasswordActivity3 extends AppCompatActivity {
    private ActivityForgotPassword3Binding binding;
    String documentID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityForgotPassword3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getDocumentId();
        setListener();
    }
    private void setListener(){
        binding.btnBack.setOnClickListener(view -> {
            Intent intent=new Intent(getApplicationContext(),ForgotPasswordActivity2.class);
            startActivity(intent);
        });

        binding.confirmButton.setOnClickListener(view -> {
            if(isValid()){
                loading(true);

                FirebaseFirestore db= FirebaseFirestore.getInstance();
                db.collection(Constants.KEY_COLLECTION_USER)
                        .document(documentID)
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

    private synchronized void getDocumentId(){
        Intent intent=getIntent();
        Bundle bundleData=intent.getExtras();

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_USER)
                .whereEqualTo(Constants.KEY_COUNTRY_CODE,bundleData.getString(Constants.KEY_COUNTRY_CODE))
                .whereEqualTo(Constants.KEY_PHONE_NUMBER,bundleData.getString(Constants.KEY_PHONE_NUMBER))
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()&& task.getResult() !=null && task.getResult().getDocuments().size()>0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        documentID=documentSnapshot.getId();
                    }
                    else{
                        loading(false);
                        showToast("Không thể thay đổi mật khẩu");
                        return;
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