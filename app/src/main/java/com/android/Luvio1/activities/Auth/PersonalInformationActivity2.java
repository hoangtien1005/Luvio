package com.android.Luvio1.activities.Auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio1.R;
import com.android.Luvio1.databinding.ActivityPersonalInformation2Binding;
import com.android.Luvio1.utilities.Constants;

public class PersonalInformationActivity2 extends AppCompatActivity {
    private ActivityPersonalInformation2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=  ActivityPersonalInformation2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }

    private void setListener(){
        binding.btnBack.setOnClickListener(view -> { onBackPressed(); });
        ArrayAdapter<String> genderAdapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.gender));
        binding.edtGender.setAdapter(genderAdapter);
        ArrayAdapter<String> interestedGenderAdapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.interested_gender));
        binding.edtInterestedGender.setAdapter(interestedGenderAdapter);
        binding.nextButton.setOnClickListener(view -> {
            if(isValidData()){
                Intent intent1=getIntent();
                Bundle bundleData=intent1.getExtras();
                bundleData.putString(Constants.KEY_PASSWORD,binding.edtPassword.getText().toString().trim());
                bundleData.putString(Constants.KEY_GENDER, binding.edtGender.getText().toString().trim());
                bundleData.putString(Constants.KEY_INTERESTED_GENDER, binding.edtInterestedGender.getText().toString().trim());
                Intent intent2=new Intent(getApplicationContext(),InterestActivity.class);
                intent2.putExtras(bundleData);
                startActivity(intent2);
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
    private boolean isValidData(){
        if(binding.edtPassword.getText().toString().trim().isEmpty()){
            showToast("Chưa nhập mật khẩu");
            return false;

        }
        else if(binding.edtConfirmPassword.getText().toString().trim().isEmpty()){
            showToast("Chưa nhập mật khẩu nhập lại");
            return false;
        }
        else if(!binding.edtPassword.getText().toString().trim().equals(binding.edtConfirmPassword.getText().toString().trim())){
            showToast("Mật khẩu nhập lại không trùng khớp");
            return false;
        }
        else if (binding.edtGender.getText().toString().trim().isEmpty()){
            showToast("Chưa chọn giới tính");
            return false;
        }
        else if (binding.edtInterestedGender.getText().toString().isEmpty()){
            showToast("Chưa chọn giới tính muốn làm quen");
            return false;
        }
        return true;
    }


}