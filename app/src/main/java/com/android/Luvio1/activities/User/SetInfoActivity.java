package com.android.Luvio1.activities.User;

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
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio1.R;
import com.android.Luvio1.databinding.ActivitySetInfoBinding;
import com.android.Luvio1.firebase.RealTimeDBManager;
import com.android.Luvio1.models.User;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;


public class SetInfoActivity extends AppCompatActivity {
    String encodeFirstImage = "";
    String encodeSecondImage = "";
    String encodeThirdImage = "";
    RealTimeDBManager realTimeDBManager;
    int currentImage = 1;
    private InputFilter filter;
    FirebaseFirestore db;
    PreferenceManager preferenceManager;
    ArrayList<String> images;
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
                                updateImagesAdded();
                                binding.imgMySettingNew1.setImageBitmap(getRoundedCornerBitmap(previewImage, 20));
                            }
                            else if(currentImage == 2) {
                                encodeSecondImage=encodeImage(imageProfile);
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

    Bundle extras ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        TODO: get phone, interested gender and 3 images encode string from database or from previous activity, add "city" to Constants
        extras=getIntent().getExtras();
        db=FirebaseFirestore.getInstance();
        images=new ArrayList<String>();
        preferenceManager=new PreferenceManager(getApplicationContext());
        realTimeDBManager=new RealTimeDBManager();
        setData();
        setListener();
    }

    private void setData(){
        if(extras.getString(Constants.KEY_FIRST_IMAGE)!=null){
            encodeFirstImage=extras.getString(Constants.KEY_FIRST_IMAGE);
            binding.imgMySettingNew1.setImageBitmap(cropImage(decodeImage(encodeFirstImage)));
        }
        if(extras.getString(Constants.KEY_SECOND_IMAGE)!=null){
            encodeSecondImage=extras.getString(Constants.KEY_SECOND_IMAGE);
            binding.imgMySettingNew2.setImageBitmap(cropImage(decodeImage(encodeSecondImage)));
        }
        if(extras.getString(Constants.KEY_THIRD_IMAGE)!=null){
            encodeThirdImage=extras.getString(Constants.KEY_THIRD_IMAGE);
            binding.imgMySettingNew3.setImageBitmap(cropImage(decodeImage(encodeThirdImage)));
        }
        updateImagesAdded();

        binding.edtFirstName.setText(extras.getString(Constants.KEY_FIRST_NAME));
        binding.edtLastName.setText(extras.getString(Constants.KEY_LAST_NAME));
        binding.edtBirthday.setText(extras.getString(Constants.KEY_BIRTHDAY));
        binding.edtGender.setText(extras.getString(Constants.KEY_GENDER));
        binding.edtCity.setText(extras.getString(Constants.KEY_CITY));
        binding.edtAboutMe.setText(extras.getString(Constants.KEY_ABOUT_ME));
        binding.edtInterestedGender.setText(extras.getString(Constants.KEY_INTERESTED_GENDER));
        binding.txtPhone.setText(preferenceManager.getString(Constants.KEY_PHONE_NUMBER));
    }
    private Bitmap decodeImage(String encodeImage){
        byte[] imageBytes= Base64.decode(encodeImage,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
        return bitmap;
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
        binding.edtAboutMe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int wordsLength = countWords(s.toString());// words.length;
                // count == 0 means a new word is going to start
                if (count == 0 && wordsLength >= 50) {
                    setCharLimit(binding.edtAboutMe, binding.edtAboutMe.getText().length());
                } else {
                    removeFilter(binding.edtAboutMe);
                }
                binding.textCount.setText(String.valueOf(wordsLength) + "/50" );
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.imgMySettingNew1.setOnClickListener(view -> {
            if(encodeFirstImage==""){
                currentImage = 1;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

        binding.imgMySettingNew2.setOnClickListener(view -> {
            if(encodeSecondImage==""&&encodeFirstImage!="") {
                currentImage = 2;
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intent);
            }
        });

        binding.imgMySettingNew3.setOnClickListener(view -> {
            if(encodeThirdImage==""&&encodeFirstImage!=""&&encodeSecondImage!="") {
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
            updateFireStore();
            updateDatabaseAndPreference();
            bundleData.putString(Constants.KEY_ABOUT_ME,binding.edtAboutMe.getText().toString().trim());
            bundleData.putString(Constants.KEY_FIRST_NAME,binding.edtFirstName.getText().toString().trim());
            bundleData.putString(Constants.KEY_LAST_NAME, binding.edtLastName.getText().toString().trim());
            bundleData.putString(Constants.KEY_BIRTHDAY,binding.edtBirthday.getText().toString().trim());
            bundleData.putString(Constants.KEY_GENDER, binding.edtGender.getText().toString().trim());
            bundleData.putString(Constants.KEY_INTERESTED_GENDER, binding.edtInterestedGender.getText().toString().trim());
            bundleData.putString(Constants.KEY_CITY, binding.edtCity.getText().toString().trim());
            bundleData.putString(Constants.KEY_FIRST_IMAGE, encodeFirstImage);
            bundleData.putString(Constants.KEY_SECOND_IMAGE, encodeSecondImage);
            bundleData.putString(Constants.KEY_THIRD_IMAGE, encodeThirdImage);
            returnIntent.putExtras(bundleData);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
            }

        });
    }


    private void updateDatabaseAndPreference() {
        String aboutMe=binding.edtAboutMe.getText().toString().trim();
        String firstName=binding.edtFirstName.getText().toString().trim();
        String lastName=binding.edtLastName.getText().toString().trim();
        String birthday=binding.edtBirthday.getText().toString().trim();
        String gender=binding.edtGender.getText().toString().trim();
        String interestedGender=binding.edtInterestedGender.getText().toString().trim();
        String city=binding.edtCity.getText().toString().trim();
        preferenceManager.putString(Constants.KEY_ABOUT_ME,aboutMe);
        preferenceManager.putString(Constants.KEY_FIRST_NAME,firstName);
        preferenceManager.putString(Constants.KEY_LAST_NAME, lastName);
        preferenceManager.putString(Constants.KEY_BIRTHDAY,birthday);
        preferenceManager.putString(Constants.KEY_GENDER, gender);
        preferenceManager.putString(Constants.KEY_INTERESTED_GENDER, interestedGender);
        preferenceManager.putString(Constants.KEY_CITY, city);
        preferenceManager.putString(Constants.KEY_FIRST_IMAGE, encodeFirstImage);
        preferenceManager.putString(Constants.KEY_SECOND_IMAGE, encodeSecondImage);
        preferenceManager.putString(Constants.KEY_THIRD_IMAGE, encodeThirdImage);


        User user=new User(preferenceManager.getString(Constants.KEY_AVATAR),
                firstName,lastName,birthday,
                preferenceManager.getString(Constants.KEY_USER_ID),
                preferenceManager.getString(Constants.KEY_STAR),aboutMe);
        realTimeDBManager.add(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                showToast("Cập nhật Real time DB thành công");
            }
        }).addOnFailureListener(e->{
            showToast(e.getMessage());
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
    private void updateImagesAdded() {
        if(encodeFirstImage!=""&&encodeSecondImage=="") {
            binding.imgMySettingNew2.setImageResource(R.drawable.ic_button_add_img);
        }
        if(encodeSecondImage!=""&&encodeFirstImage!=""&&encodeThirdImage=="") {
            binding.imgMySettingNew3.setImageResource(R.drawable.ic_button_add_img);
        }
    }

    private void updateFireStore(){
        if(encodeFirstImage!=""){
            images.add(0,encodeFirstImage);
        }
        if(encodeSecondImage!=""){
            images.add(1,encodeSecondImage);
        }
        if (encodeThirdImage!=""){
            images.add(2,encodeThirdImage);
        }
        db.collection(Constants.KEY_COLLECTION_USER)
                .document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update(Constants.KEY_GENDER,binding.edtGender.getText().toString(),
                        Constants.KEY_INTERESTED_GENDER,binding.edtInterestedGender.getText().toString(),
                        Constants.KEY_FIRST_NAME,binding.edtFirstName.getText().toString(),
                        Constants.KEY_LAST_NAME,binding.edtLastName.getText().toString(),
                        Constants.KEY_ABOUT_ME,binding.edtAboutMe.getText().toString(),
                        Constants.KEY_BIRTHDAY,binding.edtBirthday.getText().toString(),
                        Constants.KEY_CITY,binding.edtCity.getText().toString(),
                        Constants.KEY_IMAGES,images
                        )
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        showToast("Cập nhật thành công");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Không thể cập nhật");
                    }
                });
    }
}