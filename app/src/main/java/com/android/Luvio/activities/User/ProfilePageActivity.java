package com.android.Luvio.activities.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio.R;
import com.android.Luvio.activities.Setting.SettingActivity;
import com.android.Luvio.databinding.ActivityProfilePageBinding;
import com.android.Luvio.utilities.Constants;
import com.android.Luvio.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class ProfilePageActivity extends AppCompatActivity {
    private ActivityProfilePageBinding binding;
    // this is just a temporary solution
    PreferenceManager preferenceManager;
    FirebaseFirestore db;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding=ActivityProfilePageBinding.inflate(getLayoutInflater());
        preferenceManager=new PreferenceManager(getApplicationContext());



        setContentView(binding.getRoot());
        setFirstTimeData();
        setListener();
    }
    private String findAge(String birthday){
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(MaterialDatePicker.todayInUtcMilliseconds());
        SimpleDateFormat format=new SimpleDateFormat("yyyy");
        String todayYear=format.format(calendar.getTime());
        String birthYear=birthday.split("/",3)[2];
        int age=Integer.parseInt(todayYear)-Integer.parseInt(birthYear);
        return String.valueOf(age);
    }
    void setFirstTimeData(){
        byte[] bytes= Base64.decode(preferenceManager.getString(Constants.KEY_AVATAR),Base64.DEFAULT);
        Bitmap avatar= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        cropAvatar(avatar);
        binding.avatarImage.setImageBitmap(avatar);
        binding.txtFirstName.setText(preferenceManager.getString(Constants.KEY_FIRST_NAME) );
        binding.txtLastName.setText(preferenceManager.getString(Constants.KEY_LAST_NAME)+", " );
        binding.txtAge.setText(findAge(preferenceManager.getString(Constants.KEY_BIRTHDAY)));
        if (preferenceManager.getString(Constants.KEY_ABOUT_ME)!=null){
            binding.txtAboutMe.setText(preferenceManager.getString(Constants.KEY_ABOUT_ME));
        }
        else{
            binding.txtAboutMe.setText("Không có lời giới thiệu");
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

    }
    int LAUNCH_SET_INFO_ACTIVITY = 1;
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
                if(!data.get("firstImage").equals("")){
                    byte[] bytes= Base64.decode(data.getString("firstImage"),Base64.DEFAULT);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.fragmentMyGalary1.setImageBitmap(cropImage(bitmap));
                }
                if(!data.get("secondImage").equals("")){
                    byte[] bytes= Base64.decode(data.getString("secondImage"),Base64.DEFAULT);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.fragmentMyGalary2.setImageBitmap(cropImage(bitmap));
                }
                if(!data.get("thirdImage").equals("")){
                    byte[] bytes= Base64.decode(data.getString("thirdImage"),Base64.DEFAULT);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.fragmentMyGalary3.setImageBitmap(cropImage(bitmap));
                }
//                TODO: add "city" to Constants

            }
        }
    });


    private void setListener() {
        binding.settingBtn.setOnClickListener(view->{
            Intent intent=new Intent(getApplicationContext(),SettingActivity.class);
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
