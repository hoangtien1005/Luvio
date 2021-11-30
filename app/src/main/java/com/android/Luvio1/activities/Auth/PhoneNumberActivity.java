package com.android.Luvio1.activities.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.Luvio1.R;
import com.android.Luvio1.databinding.ActivityPhoneNumberBinding;
import com.android.Luvio1.interfaces.CompleteQueryListener;
import com.android.Luvio1.utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class PhoneNumberActivity extends AppCompatActivity  {
    private ActivityPhoneNumberBinding binding;
    private PhoneAuthProvider.ForceResendingToken mForceResendingToken;

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
        binding.nextButton.setOnClickListener(view -> {
            loading(true);
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            readData(db.collection(Constants.KEY_COLLECTION_USER)
                            .whereEqualTo(Constants.KEY_PHONE_NUMBER, binding.edtPhoneNumber.getText().toString()).get(),
                    new CompleteQueryListener() {
                        @Override
                        public void onSuccess(Task<QuerySnapshot> task) {
                            loading(false);
                            showToast("Số điện thoại đã tồn tại");
                        }

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFailure() {
                            if(isValidPhoneNumber()){
                                PhoneAuthOptions options=
                                        PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                                                .setPhoneNumber(binding.countryCode.getText().toString()+binding.edtPhoneNumber.getText().toString())
                                                .setTimeout(60L,TimeUnit.SECONDS)
                                                .setActivity(PhoneNumberActivity.this)
                                                .setCallbacks(mCallBack)
                                                .setForceResendingToken(mForceResendingToken)
                                                .build();
                                PhoneAuthProvider.verifyPhoneNumber(options);
                            }
                            else{
                                loading(false);
                            }

                        }
                    });







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
            mForceResendingToken=forceResendingToken;
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

        if(binding.edtPhoneNumber.getText().toString().isEmpty()){
            showToast("Nhập số điện thoại");
            return false;

        }
        else if(!Patterns.PHONE.matcher(binding.edtPhoneNumber.getText().toString()).matches()){
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
//    private boolean isExist(String phoneNum){
//        FirebaseFirestore db=FirebaseFirestore.getInstance();
//        db.collection(Constants.KEY_COLLECTION_USER)
//                .whereEqualTo(Constants.KEY_PHONE_NUMBER,phoneNum).get()
//                .addOnCompleteListener(task -> {
//                    if (!task.isSuccessful() || task.getResult() == null ) {
//                        isAlreadyExist=false;
//
//                    } else {
//                        isAlreadyExist=true;
//                    }
//                    Log.i("Outer1", new Boolean(isAlreadyExist).toString());
//                });
//        Log.i("Outer2", new Boolean(isAlreadyExist).toString());
//        return isAlreadyExist;
//    }
    public void readData(Task<QuerySnapshot> querySnapshotTask,final CompleteQueryListener listener){
        listener.onStart();
        querySnapshotTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()&& task.getResult() !=null && task.getResult().getDocuments().size()>0){
                    listener.onSuccess(task);
                }
                else{
                    listener.onFailure();
                }

            }

        });
    }

}