package com.android.Luvio.activities.Main;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.android.Luvio.databinding.ActivityMyPageBinding;
import com.android.Luvio.utilities.Constants;

public class MyPageActivity extends AppCompatActivity {

    ActivityMyPageBinding binding;
    int LAUNCH_SET_INFO_ACTIVITY = 1;

    // this is just a temporary solution
    String strLastName = "Lê";
    String strFirstName = "Đức Tâm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TODO: get personal information from database or from the previous activity and display
        // TODO: delete text attribute in activity_my_page.xml

        setListener();
    }

    private final ActivityResultLauncher<Intent> editInfo = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if(result.getResultCode() == RESULT_OK) {
            if(result.getData() != null) {


                Bundle data = result.getData().getExtras();
                binding.txtMyName.setText(data.getString(Constants.KEY_LAST_NAME) + " " + data.getString(Constants.KEY_FIRST_NAME));
                binding.txtMyGender.setText(data.getString(Constants.KEY_GENDER));
                binding.txtMyBirthday.setText(data.getString(Constants.KEY_BIRTHDAY));
//                decode images string to bitmap
                if(!data.get("firstImage").equals("")){
                    byte[] bytes= Base64.decode(data.getString("firstImage"),Base64.DEFAULT);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.fragmentMyGallery1.setImageBitmap(cropImage(bitmap));
                }
//                TODO: add "city" to Constants
                binding.txtMyCity.setText(data.getString("city"));
            }
        }
    });


    private void setListener() {

//        call SetInfoActivity when click on the edit icon
        binding.btnMyChangeInformation.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SetInfoActivity.class);
            Bundle bundleData = new Bundle();
//            TODO: put string of encode bitmap images to bundle
            bundleData.putString(Constants.KEY_FIRST_NAME,strFirstName);
            bundleData.putString(Constants.KEY_LAST_NAME, strLastName);
            bundleData.putString(Constants.KEY_BIRTHDAY,binding.txtMyBirthday.getText().toString().trim());
            bundleData.putString(Constants.KEY_GENDER, binding.txtMyGender.getText().toString().trim());
//            TODO: add "city" to Constants
            bundleData.putString("city", binding.txtMyCity.getText().toString().trim());
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

}