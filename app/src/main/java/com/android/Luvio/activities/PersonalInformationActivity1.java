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

import com.android.Luvio.R;
import com.android.Luvio.databinding.ActivityPersonalInformation1Binding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


public class PersonalInformationActivity1 extends AppCompatActivity {
    private ActivityPersonalInformation1Binding binding;

    DatePickerDialog.OnDateSetListener setDateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPersonalInformation1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();

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


    private void setListener(){
        binding.btnCamera.setOnClickListener(view -> {
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.ThemeOverlay_App_DatePicker)
                .build();

        binding.layoutBirthday.setEndIconOnClickListener(view -> {
            datePicker.show(getSupportFragmentManager(),"Material_Date_Picker");
            datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    calendar.setTimeInMillis(selection);
                    SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
                    String birthday=format1.format(calendar.getTime());
                    binding.edtBirthday.setText(birthday);
                }
            });
        });
        binding.skipButton.setOnClickListener(view -> {
            Intent intent=new Intent(getApplicationContext(),PersonalInformationActivity2.class);
            startActivity(intent);
        });
    }
    private void pickBirthday(){


    }

}