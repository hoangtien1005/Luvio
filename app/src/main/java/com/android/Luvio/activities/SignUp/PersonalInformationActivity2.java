package com.android.Luvio.activities.SignUp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.android.Luvio.R;
import com.android.Luvio.databinding.ActivityPersonalInformation2Binding;

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
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
        ArrayAdapter<String> genderAdapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.gender));
        binding.edtGender.setAdapter(genderAdapter);
        ArrayAdapter<String> interestedGenderAdapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.interested_gender));
        binding.edtInterestedGender.setAdapter(interestedGenderAdapter);
    }
}