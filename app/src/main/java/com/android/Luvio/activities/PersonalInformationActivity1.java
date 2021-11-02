package com.android.Luvio.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.DatePicker;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio.databinding.ActivityPersonalInformation1Binding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;


public class PersonalInformationActivity1 extends AppCompatActivity {
    private ActivityPersonalInformation1Binding binding;
    DatePickerDialog.OnDateSetListener setDateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPersonalInformation1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Calendar calendar=Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);
        setListener(year,month,day);

    }
    private String encodeImage(Bitmap bitmap){
        int previewWidth=150;
        int previewHeight=bitmap.getHeight()*previewWidth/ bitmap.getWidth();
        Bitmap previewImage=Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        previewImage.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()==RESULT_OK){
                    if(result.getData()!=null){
                        Uri imageUri=result.getData().getData();
                        try{
                            InputStream inputStream=getContentResolver().openInputStream(imageUri);
                            Bitmap imageProfile= BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(imageProfile);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );


    private void setListener(int year,int month,int day){
        binding.btnCamera.setOnClickListener(view -> {
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        pickBirthday(year,month,day);
        binding.skipButton.setOnClickListener(view -> {
            Intent intent=new Intent(getApplicationContext(),PersonalInformationActivity2.class);
            startActivity(intent);
        });
    }
    private void pickBirthday(int year,int month,int day){
        binding.layoutBirthday.setEndIconOnClickListener(view -> {
            DatePickerDialog datePickerDialog=new DatePickerDialog(
                    PersonalInformationActivity1.this,setDateListener,year,month,day
                    );

            datePickerDialog.show();
        });
        setDateListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayofmonth) {
                month=month+1;
                String birthday=dayofmonth+"/"+month+"/"+year;
                binding.edtBirthday.setText(birthday);
            }
        };
    }

}