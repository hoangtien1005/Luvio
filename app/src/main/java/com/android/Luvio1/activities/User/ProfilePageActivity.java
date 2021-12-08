package com.android.Luvio1.activities.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio1.activities.Setting.SettingActivity;
import com.android.Luvio1.activities.Setting.ThemeChangeActivity;
import com.android.Luvio1.databinding.ActivityProfilePageBinding;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class ProfilePageActivity extends ThemeChangeActivity {
    private ActivityProfilePageBinding binding;
    // this is just a temporary solution
    PreferenceManager preferenceManager;
    FirebaseFirestore db;
    String encodeFirstImage = "";
    String encodeSecondImage = "";
    String encodeThirdImage = "";
    ArrayList<String> images;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding=ActivityProfilePageBinding.inflate(getLayoutInflater());
        preferenceManager=new PreferenceManager(getApplicationContext());
        db=FirebaseFirestore.getInstance();
        images=new ArrayList<String>();
        setContentView(binding.getRoot());
        setFirstTimeData();
        setListener();
    }
    public String findAge(String birthday){
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(MaterialDatePicker.todayInUtcMilliseconds());
        SimpleDateFormat format=new SimpleDateFormat("yyyy");
        String todayYear=format.format(calendar.getTime());
        String birthYear=birthday.split("/",3)[2];
        int age=Integer.parseInt(todayYear)-Integer.parseInt(birthYear);
        return String.valueOf(age);
    }
    void setFirstTimeData(){
        Bitmap avatar= decodeImage(preferenceManager.getString(Constants.KEY_AVATAR));
        cropAvatar(avatar);
        binding.avatarImage.setImageBitmap(avatar);
        binding.txtFirstName.setText(preferenceManager.getString(Constants.KEY_FIRST_NAME) );
        binding.txtLastName.setText(preferenceManager.getString(Constants.KEY_LAST_NAME) );
        binding.txtAge.setText(findAge(preferenceManager.getString(Constants.KEY_BIRTHDAY)));
        if (preferenceManager.getString(Constants.KEY_ABOUT_ME)!=""){
            binding.txtAboutMe.setText(preferenceManager.getString(Constants.KEY_ABOUT_ME));
        }
        else{
//            binding.txtAboutMe.setVisibility(View.GONE);
            binding.txtAboutMe.setText("");
        }
        binding.txtInterestGender.setText(preferenceManager.getString(Constants.KEY_INTERESTED_GENDER));
        binding.txtMyBirthday.setText(preferenceManager.getString(Constants.KEY_BIRTHDAY));
        binding.txtMyGender.setText(preferenceManager.getString(Constants.KEY_GENDER));
        String[] interests = preferenceManager.getString(Constants.KEY_INTERESTS).split(",");
        binding.chip1.setText(interests[0]);
        binding.chip2.setText(interests[1]);
        binding.chip3.setText(interests[2]);
        if (preferenceManager.getString(Constants.KEY_CITY)!=null) {
            binding.txtMyCity.setText(preferenceManager.getString(Constants.KEY_CITY));
        }
        else{
            binding.txtMyCity.setText("Không có");
        }
        addImages();


    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
    private Bitmap decodeImage(String encodeImage){
        byte[] imageBytes= Base64.decode(encodeImage,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
        return bitmap;
    }
    private void addImages(){
        encodeFirstImage=preferenceManager.getString(Constants.KEY_FIRST_IMAGE);
        encodeSecondImage=preferenceManager.getString(Constants.KEY_SECOND_IMAGE);
        encodeThirdImage=preferenceManager.getString(Constants.KEY_THIRD_IMAGE);
        if (encodeFirstImage!=""&&encodeFirstImage!=null){
            Bitmap bitmap=decodeImage(encodeFirstImage);
            binding.fragmentMyGalary1.setImageBitmap(cropImage(bitmap));
        }
        if (encodeSecondImage!=""&&encodeSecondImage!=null){
            Bitmap bitmap=decodeImage(encodeSecondImage);
            binding.fragmentMyGalary2.setImageBitmap(cropImage(bitmap));
        }if (encodeThirdImage!=""&&encodeThirdImage!=null){
            Bitmap bitmap=decodeImage(encodeThirdImage);
            binding.fragmentMyGalary3.setImageBitmap(cropImage(bitmap));
        }


    }

    private final ActivityResultLauncher<Intent> editInfo = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            if(result.getData() != null) {
                Bundle data = result.getData().getExtras();
                binding.txtAboutMe.setText(data.getString(Constants.KEY_ABOUT_ME));
                binding.txtFirstName.setText(data.getString(Constants.KEY_FIRST_NAME));
                binding.txtLastName.setText(data.getString(Constants.KEY_LAST_NAME));
                binding.txtMyGender.setText(data.getString(Constants.KEY_GENDER));
                binding.txtInterestGender.setText(data.getString(Constants.KEY_INTERESTED_GENDER));
                binding.txtMyBirthday.setText(data.getString(Constants.KEY_BIRTHDAY));
                binding.txtMyCity.setText(data.getString(Constants.KEY_CITY));
                binding.txtAge.setText(findAge(data.getString(Constants.KEY_BIRTHDAY)));
//                decode images string to bitmap
                if(!data.get(Constants.KEY_FIRST_IMAGE).equals("")&&data.get(Constants.KEY_FIRST_IMAGE)!=null){
                    encodeFirstImage=data.getString(Constants.KEY_FIRST_IMAGE);
                    Bitmap bitmap= decodeImage(encodeFirstImage);
                    binding.fragmentMyGalary1.setImageBitmap(cropImage(bitmap));
                }
                if(!data.get(Constants.KEY_SECOND_IMAGE).equals("")&&data.get(Constants.KEY_SECOND_IMAGE)!=null){
                    encodeSecondImage=data.getString(Constants.KEY_SECOND_IMAGE);
                    Bitmap bitmap= decodeImage(encodeSecondImage);
                    binding.fragmentMyGalary2.setImageBitmap(cropImage(bitmap));
                }
                if(!data.get(Constants.KEY_THIRD_IMAGE).equals("")&&data.get(Constants.KEY_THIRD_IMAGE)!=null){
                    encodeThirdImage=data.getString(Constants.KEY_THIRD_IMAGE);
                    Bitmap bitmap= decodeImage(encodeThirdImage);
                    binding.fragmentMyGalary3.setImageBitmap(cropImage(bitmap));
                }
//                TODO: add "city" to Constants

            }
        }
    });


    private void setListener() {
        binding.settingBtn.setOnClickListener(view->{
            Intent intent=new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
        });
        binding.backButton.setOnClickListener(view->{
            onBackPressed();
        });
//        call SetInfoActivity when click on the edit icon
        binding.btnChangeInformation.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SetInfoActivity.class);
            Bundle bundleData = new Bundle();
//            TODO: put string of encode bitmap images to bundle
            bundleData.putString(Constants.KEY_ABOUT_ME,binding.txtAboutMe.getText().toString());
            bundleData.putString(Constants.KEY_FIRST_NAME,binding.txtFirstName.getText().toString());
            bundleData.putString(Constants.KEY_LAST_NAME, binding.txtLastName.getText().toString());
            bundleData.putString(Constants.KEY_BIRTHDAY,binding.txtMyBirthday.getText().toString().trim());
            bundleData.putString(Constants.KEY_GENDER, binding.txtMyGender.getText().toString().trim());
            bundleData.putString(Constants.KEY_INTERESTED_GENDER, binding.txtInterestGender.getText().toString().trim());
//            TODO: add "city" to Constants
            bundleData.putString(Constants.KEY_CITY, binding.txtMyCity.getText().toString().trim());

            if(encodeFirstImage!="")
                bundleData.putString(Constants.KEY_FIRST_IMAGE,encodeFirstImage);
            if(encodeSecondImage!="")
                bundleData.putString(Constants.KEY_SECOND_IMAGE,encodeSecondImage);
            if (encodeThirdImage!="")
                bundleData.putString(Constants.KEY_THIRD_IMAGE,encodeThirdImage);
            intent.putExtras(bundleData);
            editInfo.launch(intent);
        });
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
    public void cropAvatar(Bitmap avatar) {

        int height = avatar.getHeight();
        int width = avatar.getWidth();
        int ratio=(int)(width/height);
        if(ratio==2){
            binding.avatarImage.setScaleType(ImageView.ScaleType.valueOf("FIT_XY"));
        }
        else{
            binding.avatarImage.setScaleType(ImageView.ScaleType.valueOf("CENTER_CROP"));
        }
    }
}
