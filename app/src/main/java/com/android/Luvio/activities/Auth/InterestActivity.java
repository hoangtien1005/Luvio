package com.android.Luvio.activities.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio.activities.Main.MainActivity;
import com.android.Luvio.databinding.ActivityInterestBinding;
import com.android.Luvio.utilities.Constants;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class InterestActivity extends AppCompatActivity {
    private ActivityInterestBinding binding;
    private String[] interests =new String[] {"Bóng đá", "Mua sắm", "Yoga", "Bơi lội", "Bóng rổ", "Karaoke", "Quần vợt", "Nấu ăn", "Đọc sách", "Âm nhạc", "Xem phim", "Nghệ thuật", "Động vật", "Chính trị", "Du lịch", "Game"};
    private ArrayList<String> userInterests=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityInterestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();

    }

    private void signUp(){
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Intent intent=getIntent();
        Bundle bundleData=intent.getExtras();
        HashMap<String,Object>user=new HashMap<>();
        user.put(Constants.KEY_COUNTRY_CODE,bundleData.getString(Constants.KEY_COUNTRY_CODE));
        user.put(Constants.KEY_PHONE_NUMBER,bundleData.getString(Constants.KEY_PHONE_NUMBER));
        user.put(Constants.KEY_IS_DELETE,false);
        user.put(Constants.KEY_IMAGE,bundleData.getString(Constants.KEY_IMAGE));
        user.put(Constants.KEY_FIRST_NAME,bundleData.getString(Constants.KEY_FIRST_NAME));
        user.put(Constants.KEY_LAST_NAME,bundleData.getString(Constants.KEY_LAST_NAME));
        user.put(Constants.KEY_GENDER,bundleData.getString(Constants.KEY_GENDER));
        user.put(Constants.KEY_INTERESTED_GENDER,bundleData.getString(Constants.KEY_INTERESTED_GENDER));
        user.put(Constants.KEY_PASSWORD,bundleData.getString(Constants.KEY_PASSWORD));
        user.put(Constants.KEY_BIRTHDAY,bundleData.getString(Constants.KEY_BIRTHDAY));
        user.put(Constants.KEY_INTERESTS, userInterests);
        db.collection(Constants.KEY_COLLECTION_USER)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);

                    Intent intentMain=new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentMain);
                })
                .addOnFailureListener(Exception->{
                    loading(false);
                    showToast(Exception.getMessage());
                });
    }

    private void setListener(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
        chipListener();


        binding.confirmButton.setOnClickListener(view -> {
            if (isValidData()){
                getChipValue();
                signUp();
            }
        });
    }
    private  void getChipValue(){
        List<Integer> ids=binding.chipGroup.getCheckedChipIds();
        for (Integer id:ids){
            Chip chip=(Chip) binding.chipGroup.findViewById(id);
            userInterests.add(chip.getText().toString());
        }

    }

    private void chipListener(){

        for (int i=0;i<binding.chipGroup.getChildCount();i++){
            Chip chip=(Chip) binding.chipGroup.getChildAt(i);

            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    List<Integer> ids=binding.chipGroup.getCheckedChipIds();
                    if (b){

                        setCounter(ids);
                        if (ids.size()>3){
                            chip.setChecked(false);
                        }
                    }
                    else{
                        setCounter(ids);
                    }
                }

            });

        }
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
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    private boolean isValidData(){
        List<Integer> ids=binding.chipGroup.getCheckedChipIds();
        if (ids.size()<3){
            showToast("Chọn đủ 3 sở thích");
            return false;
        }
        return true;
    }

    private void setCounter(List<Integer> ids) {

        if(ids.size()==0){
            binding.interestCounter.setText("");

        }
        else if (ids.size()==1){
            binding.interestCounter.setText("(1/3)");
        }
        else if (ids.size()==2){
            binding.interestCounter.setText("(2/3)");
        }
        else if (ids.size()==3){
            binding.interestCounter.setText("(3/3)");
        }

    }
}