package com.android.Luvio1.activities.Auth;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio1.R;
import com.android.Luvio1.databinding.ActivityPersonalInformation1Binding;
import com.android.Luvio1.utilities.Constants;
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
    String encodeImage;
    DatePickerDialog.OnDateSetListener setDateListener;
    private InputFilter filter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPersonalInformation1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();

    }
    private String encodeImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
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
                            Bitmap previewImage=cropImage(imageProfile);
                            encodeImage=encodeImage(imageProfile);
                            binding.imageProfile.setImageBitmap(previewImage);
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
        binding.edtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int wordsLength = countWords(s.toString());// words.length;
                // count == 0 means a new word is going to start
                if (count == 0 && wordsLength >= 2) {
                    setCharLimit(binding.edtFirstName, binding.edtFirstName.getText().length());
                    showToast("Không được nhập quá 2 từ");
                } else {
                    removeFilter(binding.edtFirstName);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.edtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int wordsLength = countWords(s.toString());// words.length;
                // count == 0 means a new word is going to start
                if (count == 0 && wordsLength >= 1) {
                    setCharLimit(binding.edtLastName, binding.edtLastName.getText().length());
                    showToast("Không được nhập quá 1 từ");
                } else {
                    removeFilter(binding.edtLastName);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.DatePickerStyle)
                .build();
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(MaterialDatePicker.todayInUtcMilliseconds());
        SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
        String today=format1.format(calendar.getTime());
        binding.edtBirthday.setText(today);
        binding.layoutBirthday.setEndIconOnClickListener(view -> {
            datePicker.show(getSupportFragmentManager(),"Material_Date_Picker");
            datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    calendar.setTimeInMillis(selection);
                    String birthday=format1.format(calendar.getTime());
                    binding.edtBirthday.setText(birthday);
                }
            });
        });

        binding.nextButton.setOnClickListener(view->{
            if(isValidData()){
                Intent intentPrev=getIntent();
                Bundle bundleData=intentPrev.getExtras();
                Intent intent=new Intent(getApplicationContext(),PersonalInformationActivity2.class);
                bundleData.putString(Constants.KEY_AVATAR,encodeImage);
                bundleData.putString(Constants.KEY_FIRST_NAME,binding.edtFirstName.getText().toString().trim());
                bundleData.putString(Constants.KEY_LAST_NAME, binding.edtLastName.getText().toString().trim());
                bundleData.putString(Constants.KEY_BIRTHDAY,binding.edtBirthday.getText().toString().trim());
                intent.putExtras(bundleData);
                startActivity(intent);
            }

        });
    }

    private int countWords(String s) {
        String trim = s.trim();
        if (trim.isEmpty())
            return 0;
        return trim.split("\\s+").length; // separate string around spaces
    }



    private void setCharLimit(EditText et, int max) {
        filter = new InputFilter.LengthFilter(max);
        et.setFilters(new InputFilter[] { filter });
    }

    private void removeFilter(EditText et) {
        if (filter != null) {
            et.setFilters(new InputFilter[0]);
            filter = null;
        }
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    private boolean isValidData(){
        if(encodeImage==null){
            showToast("Chưa chọn hình ảnh");
            return false;
        }
        else if(binding.edtFirstName.getText().toString().trim().isEmpty()){
            showToast("Chưa nhập họ");
            return false;

        }
        else if(binding.edtLastName.getText().toString().trim().isEmpty()){
            showToast("Chưa nhập tên");
            return false;
        }

        else if (binding.edtBirthday.getText().toString().trim().isEmpty()){
            showToast("Chưa chọn ngày sinh");
            return false;
        }
        return true;
    }

//    private void clearData() {
//        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//        calendar.setTimeInMillis(MaterialDatePicker.todayInUtcMilliseconds());
//        SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
//        String today=format1.format(calendar.getTime());
//        binding.edtBirthday.setText(today);
//        binding.edtFirstName.getText().clear();
//        binding.edtLastName.getText().clear();
//    }
    public Bitmap cropImage(Bitmap imageProfile){
        int height=imageProfile.getHeight();
        int width=imageProfile.getWidth();
        int previewWidth,previewHeight;
        Bitmap previewImage;
        if (height<width){
            previewHeight=height;
            previewWidth=previewHeight;
            previewImage=Bitmap.createBitmap(imageProfile,(width/2)-(previewWidth/2),0,previewWidth,previewHeight);
        }
        else {
            previewWidth=width;
            previewHeight=previewWidth;
            previewImage=Bitmap.createBitmap(imageProfile,0,(height/2)-(previewHeight/2),previewWidth,previewHeight);
        }
        return previewImage;
    }
}