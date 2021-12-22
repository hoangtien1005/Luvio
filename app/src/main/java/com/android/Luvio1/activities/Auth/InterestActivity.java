package com.android.Luvio1.activities.Auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio1.R;
import com.android.Luvio1.activities.Main.MainActivity;
import com.android.Luvio1.databinding.ActivityInterestBinding;
import com.android.Luvio1.firebase.DBUserManager;
import com.android.Luvio1.models.UserModel;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.logger.ChatLogLevel;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.client.notifications.handler.ChatNotificationHandler;
import io.getstream.chat.android.client.notifications.handler.NotificationConfig;
import io.getstream.chat.android.client.notifications.handler.PushDeviceGenerator;
import io.getstream.chat.android.livedata.ChatDomain;
import io.getstream.chat.android.pushprovider.firebase.FirebasePushDeviceGenerator;

public class InterestActivity extends AppCompatActivity {
    private ActivityInterestBinding binding;
    private String[] interests =new String[] {"Bóng đá", "Mua sắm", "Yoga", "Bơi lội", "Bóng rổ", "Karaoke", "Quần vợt", "Nấu ăn", "Đọc sách", "Âm nhạc", "Xem phim", "Nghệ thuật", "Động vật", "Chính trị", "Du lịch", "Game"};
    private ArrayList<String> userInterests;
    FirebaseFirestore db;
    private PreferenceManager preferenceManager;
    ChatClient client;
    private DBUserManager DBUserManager = new DBUserManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityInterestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userInterests=new ArrayList<String>();
        db=FirebaseFirestore.getInstance();
        preferenceManager=new PreferenceManager(getApplicationContext());

        List<PushDeviceGenerator> pushDeviceGenerators = new ArrayList<PushDeviceGenerator>() {{
            add(new FirebasePushDeviceGenerator());
        }};

        NotificationConfig notificationConfig = new NotificationConfig(
                R.string.logo,
                R.string.logo,
                R.drawable.ic_launcher_foreground,
                R.string.logo,
                R.string.logo,
                R.string.logo,
                R.drawable.ic_launcher_foreground,
                R.string.logo,
                R.string.logo,
                R.string.logo,
                R.string.logo,
                true,
                true,
                pushDeviceGenerators
        );
        


        ChatNotificationHandler notificationHandler = new ChatNotificationHandler(getApplicationContext(), notificationConfig);

        client= new ChatClient.Builder("an38qgjtsfsj", getApplicationContext())
                .notifications(notificationHandler)
                .logLevel(ChatLogLevel.ALL)
                .build();
        new ChatDomain.Builder(client, getApplicationContext()).build();
        setListener();

    }

    private void signUp(){
        loading(true);


        Intent intent=getIntent();
        Bundle bundleData=intent.getExtras();
        String star="5";
        String numberOfRating="1";
        String aboutMe="";
        String countryCode=bundleData.getString(Constants.KEY_COUNTRY_CODE);
        String phoneNumber=bundleData.getString(Constants.KEY_PHONE_NUMBER);
        String avatar=bundleData.getString(Constants.KEY_AVATAR);
        String firstName=bundleData.getString(Constants.KEY_FIRST_NAME);
        String lastName=bundleData.getString(Constants.KEY_LAST_NAME);
        String gender=bundleData.getString(Constants.KEY_GENDER);
        String interestGender=bundleData.getString(Constants.KEY_INTERESTED_GENDER);
        String password=bundleData.getString(Constants.KEY_PASSWORD);
        String birthday=bundleData.getString(Constants.KEY_BIRTHDAY);
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
        preferenceManager.putString(Constants.KEY_STAR,star);
        preferenceManager.putString(Constants.KEY_COUNTRY_CODE,countryCode);
        preferenceManager.putString(Constants.KEY_ABOUT_ME,aboutMe);
        preferenceManager.putString(Constants.KEY_PHONE_NUMBER,phoneNumber);
        preferenceManager.putString(Constants.KEY_AVATAR,avatar);
        preferenceManager.putString(Constants.KEY_FIRST_NAME,firstName);
        preferenceManager.putString(Constants.KEY_LAST_NAME,lastName);
        preferenceManager.putString(Constants.KEY_GENDER,gender);
        preferenceManager.putString(Constants.KEY_INTERESTED_GENDER,interestGender);
        preferenceManager.putString(Constants.KEY_PASSWORD,password);
        preferenceManager.putString(Constants.KEY_BIRTHDAY, birthday);
        preferenceManager.putString(Constants.KEY_NUMBER_OF_RATING, numberOfRating);

        String[] interests = new String[userInterests.size()];

        for (int i = 0; i < userInterests.size(); i++) {
            interests[i] = userInterests.get(i);
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < interests.length; i++) {
            sb.append(interests[i]).append(",");
        }
        preferenceManager.putString(Constants.KEY_INTERESTS, sb.toString());

        String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        HashMap<String,Object>user=new HashMap<>();
        user.put(Constants.KEY_COUNTRY_CODE,countryCode);
        user.put(Constants.KEY_PHONE_NUMBER,phoneNumber);
        user.put(Constants.KEY_IS_DELETE,false);
        user.put(Constants.KEY_STAR,star);
        user.put(Constants.KEY_ABOUT_ME,aboutMe);
        user.put(Constants.KEY_AVATAR,avatar);
        user.put(Constants.KEY_FIRST_NAME,firstName);
        user.put(Constants.KEY_LAST_NAME,lastName);
        user.put(Constants.KEY_GENDER,gender);
        user.put(Constants.KEY_INTERESTED_GENDER,interestGender);
        user.put(Constants.KEY_PASSWORD,bcryptHashString);
        user.put(Constants.KEY_BIRTHDAY,birthday);
        user.put(Constants.KEY_INTERESTS, userInterests);
        user.put(Constants.KEY_NUMBER_OF_RATING, numberOfRating);

        db.collection(Constants.KEY_COLLECTION_USER)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putString(Constants.KEY_USER_ID,documentReference.getId());
                    User user1 = new User();
                    user1.setId(documentReference.getId());
                    user1.getExtraData().put("name", firstName+" "+lastName);
                    user1.getExtraData().put("image", bitmapToUri(avatar));
                    String token = client.devToken(documentReference.getId());
                    client.connectUser(user1, token).enqueue();
                    preferenceManager.putString(Constants.KEY_CHAT_TOKEN,token);


                    UserModel userModel1 =new UserModel(avatar,gender,firstName,lastName,birthday,documentReference.getId(),star,aboutMe);
                    DBUserManager.add(userModel1)
                            .addOnSuccessListener(suc ->
                            {
                                showToast("Cập nhật Real Time DB thành công");

                            })
                            .addOnFailureListener(e ->
                            {
                                showToast(e.getMessage());
                            });

                    Intent intentMain=new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intentMain);
                })
                .addOnFailureListener(Exception->{
                    loading(false);
                    showToast(Exception.getMessage());
                });

    }
    public File createImageFile(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mFileTemp = null;
        String root=context.getDir("my_sub_dir", Context.MODE_PRIVATE).getAbsolutePath();
        File myDir = new File(root + "/Img");
        if(!myDir.exists()){
            myDir.mkdirs();
        }
        try {
            mFileTemp= File.createTempFile(imageFileName,".jpg",myDir.getAbsoluteFile());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return mFileTemp;
    }
    private String bitmapToUri(String encodeImage){
        byte[] imageBytes= Base64.decode(encodeImage,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
        File file = createImageFile(getApplicationContext());
        if (file != null) {
            FileOutputStream fout;
            try {
                fout = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 70, fout);
                fout.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Uri uri = Uri.fromFile(file);
            String hi = uri.toString();
            return hi;
        }
        return "";
    }
    private void setListener(){
        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });
        chipListener();


        binding.confirmButton.setOnClickListener(view -> {
            if (isValidData()){
                getChipValue();
                signUp();
            }
        });
    }

    //    private void updateDeleteAccount(String countryCode,String phoneNum){
//        db.collection(Constants.KEY_COLLECTION_USER)
//                .whereEqualTo(Constants.KEY_PHONE_NUMBER,phoneNum)
//                .whereEqualTo(Constants.KEY_COUNTRY_CODE,countryCode)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()&&task.getResult()!=null){
//                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
//                            documentSnapshot.getId();
//                        }
//                    }
//                })
//    }
    private  void getChipValue(){
        List<Integer> ids=binding.chipGroup.getCheckedChipIds();
        for (Integer id:ids){
            Chip chip=(Chip) binding.chipGroup.findViewById(id);
            userInterests.add(chip.getText().toString());
        }

    }

    private void chipListener(){

        for (int i=0;i<binding.chipGroup.getChildCount();i++){
            Chip chip=(Chip) binding.chipGroup.getChildAt(i);

            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    List<Integer> ids=binding.chipGroup.getCheckedChipIds();
                    if (b){

                        setCounter(ids);
                        if (ids.size()>3){
                            chip.setChecked(false);
                        }
                    }
                    else{
                        setCounter(ids);
                    }
                }

            });

        }
    }
    private void loading(boolean isLoading){
        if (isLoading){
            binding.confirmButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.confirmButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    private boolean isValidData(){
        List<Integer> ids=binding.chipGroup.getCheckedChipIds();
        if (ids.size()<3){
            showToast("Chọn đủ 3 sở thích");
            return false;
        }
        return true;
    }
    private boolean isNumberExist(){
//        temporary
        return true;
    }
    private void setCounter(List<Integer> ids) {

        if(ids.size()==0){
            binding.interestCounter.setText("");

        }
        else if (ids.size()==1){
            binding.interestCounter.setText("(1/3)");
        }
        else if (ids.size()==2){
            binding.interestCounter.setText("(2/3)");
        }
        else if (ids.size()==3){
            binding.interestCounter.setText("(3/3)");
        }

    }
}