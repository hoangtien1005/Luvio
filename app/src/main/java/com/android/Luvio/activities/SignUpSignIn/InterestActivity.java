package com.android.Luvio.activities.SignUpSignIn;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio.databinding.ActivityInterestBinding;
import com.google.android.material.chip.Chip;

import java.util.List;

public class InterestActivity extends AppCompatActivity {
    private ActivityInterestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityInterestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }
    private void setListener(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
        chipCount();
        binding.confirmButton.setOnClickListener(view -> {
            if(isValid()){
                Intent intent=new Intent(getApplicationContext(),PhoneNumberActivity.class);
                startActivity(intent);
            }
        });
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
    private boolean isValid() {

        if (binding.chipGroup.getCheckedChipIds().size()<3){
            showToast("Chọn đúng 3 sở thích của bản thân");
            return false;
        }
        return true;
    }

    private void chipCount(){
        for (int i=0;i<binding.chipGroup.getChildCount();i++){
            Chip chip=(Chip)binding.chipGroup.getChildAt(i);
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    List<Integer> ids = binding.chipGroup.getCheckedChipIds();
                    chipCountUpdate(ids);
                    if(compoundButton.isChecked()){
                        if (ids.size() > 3) {
                            chip.setChecked(false);
                        }
                    }
                }
            });


        }

    }
    private void chipCountUpdate(List<Integer> ids){
        if(ids.size()==0){
            binding.interestCounter.setText("");
        }
        if (ids.size()==1) {
            binding.interestCounter.setText("(1/3)");
        }
        if(ids.size()==2) {
            binding.interestCounter.setText("(2/3)");
        }
        if(ids.size()==3) {
            binding.interestCounter.setText("(3/3)");
        }

    }
}