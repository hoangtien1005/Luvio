package com.android.Luvio.activities.User;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio.R;
import com.android.Luvio.databinding.ActivitySetInfoBinding;
import com.android.Luvio.utilities.Constants;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


public class SetInfoActivity extends AppCompatActivity {
    String encodeFirstImage = "";
    String encodeSecondImage = "";
    String encodeThirdImage = "";
    boolean firstImageAdded = false;
    boolean secondImageAdded = false;
    int currentImage = 1;
    private ActivitySetInfoBinding binding;
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
                            if(currentImage == 1) {
                                encodeFirstImage=encodeImage(imageProfile);
                                firstImageAdded = true;
                                updateImagesAdded();
                                binding.imgMySettingNew1.setImageBitmap(getRoundedCornerBitmap(previewImage, 20));
                            }
                            else if(currentImage == 2) {
                                encodeSecondImage=encodeImage(imageProfile);
                                secondImageAdded = true;
                                updateImagesAdded();
                                binding.imgMySettingNew2.setImageBitmap(getRoundedCornerBitmap(previewImage, 20));
                            }
                            else if(currentImage == 3) {
                                encodeThirdImage=encodeImage(imageProfile);
                                binding.imgMySettingNew3.setImageBitmap(getRoundedCornerBitmap(previewImage, 20));
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        TODO: get phone, interested gender and 3 images encode string from database or from previous activity, add "city" to Constants
        Bundle extras = getIntent().getExtras();
        binding.edtFirstName.setText(extras.getString(Constants.KEY_FIRST_NAME));
        binding.edtLastName.setText(extras.getString(Constants.KEY_LAST_NAME));
        binding.edtBirthday.setText(extras.getString(Constants.KEY_BIRTHDAY));
        binding.edtGender.setText(extras.getString(Constants.KEY_GENDER));
        binding.edtCity.setText(extras.getString("city"));
        binding.edtInterestedGender.setText("Nữ");
        binding.edtPhone.setText("1235983295");

        setListener();
    }

    private String encodeImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    private void setListener(){

//        Dropdown menu for setting gender
        ArrayAdapter<String> genderAdapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.gender));
        binding.edtGender.setAdapter(genderAdapter);
        ArrayAdapter<String> interestedGenderAdapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.interested_gender));
        binding.edtInterestedGender.setAdapter(interestedGenderAdapter);

        binding.imgMySettingNew1.setOnClickListener(view -> {
            currentImage = 1;
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

        binding.imgMySettingNew2.setOnClickListener(view -> {
            if(firstImageAdded) {
                currentImage = 2;
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

        binding.imgMySettingNew3.setOnClickListener(view -> {
            if(secondImageAdded) {
                currentImage = 3;
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

//        set birthday listener
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày sinh")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.DatePickerStyle)
                .build();
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(MaterialDatePicker.todayInUtcMilliseconds());
        SimpleDateFormat format1=new SimpleDateFormat("dd/MM/yyyy");
        binding.edtBirthday.setOnClickListener(view -> {
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


//        update info and return to the last activity
        binding.btnBack.setOnClickListener(view -> {
            if(isValidData()) {

//          TODO: update phone number, interested gender, about text on database and personal information
            Intent returnIntent = new Intent();
            Bundle bundleData = new Bundle();

            bundleData.putString(Constants.KEY_ABOUT_ME,binding.edtAboutMe.getText().toString().trim());
            bundleData.putString(Constants.KEY_FIRST_NAME,binding.edtFirstName.getText().toString().trim());
            bundleData.putString(Constants.KEY_LAST_NAME, binding.edtLastName.getText().toString().trim());
            bundleData.putString(Constants.KEY_BIRTHDAY,binding.edtBirthday.getText().toString().trim());
            bundleData.putString(Constants.KEY_GENDER, binding.edtGender.getText().toString().trim());
            bundleData.putString(Constants.KEY_CITY, binding.edtCity.getText().toString().trim());
            bundleData.putString("firstImage", encodeFirstImage);
            bundleData.putString("secondImage", encodeSecondImage);
            bundleData.putString("thirdImage", encodeThirdImage);
            returnIntent.putExtras(bundleData);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            }

        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    private boolean isValidData(){

        if(binding.edtFirstName.getText().toString().trim().isEmpty()){
            showToast("Chưa nhập họ");
            return false;

        }
        else if(binding.edtLastName.getText().toString().trim().isEmpty()){
            showToast("Chưa nhập tên");
            return false;
        }

        else if (binding.edtPhone.getText().toString().trim().isEmpty()) {
            showToast("Chưa nhập số điện thoại");
            return false;
        }

        return true;
    }

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

    public  Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    private void updateImagesAdded() {
        if(firstImageAdded) {
            binding.imgMySettingNew2.setImageResource(R.drawable.ic_button_add_img);
        }
        if(secondImageAdded) {
            binding.imgMySettingNew3.setImageResource(R.drawable.ic_button_add_img);
        }
    }

}