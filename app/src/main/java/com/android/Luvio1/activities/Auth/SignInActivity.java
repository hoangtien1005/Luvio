package com.android.Luvio1.activities.Auth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio1.activities.Main.MainActivity;
import com.android.Luvio1.databinding.ActivitySignInBinding;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.logger.ChatLogLevel;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.client.notifications.handler.NotificationConfig;
import io.getstream.chat.android.client.notifications.handler.PushDeviceGenerator;
import io.getstream.chat.android.livedata.ChatDomain;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.getstream.chat.android.client.models.User;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    PreferenceManager preferenceManager;
    FirebaseFirestore db;
    ChatClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        db=FirebaseFirestore.getInstance();
        client= new ChatClient.Builder("an38qgjtsfsj", getApplicationContext())
                .logLevel(ChatLogLevel.ALL)
                .build();
        new ChatDomain.Builder(client, getApplicationContext()).build();
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            Intent intent =new Intent(getApplicationContext(), MainActivity.class);

            User user1 = new User();
            user1.setId(preferenceManager.getString(Constants.KEY_USER_ID));
            user1.getExtraData().put("name", preferenceManager.getString(Constants.KEY_FIRST_NAME) + " " + preferenceManager.getString(Constants.KEY_LAST_NAME));
            user1.getExtraData().put("image", bitmapToUri(preferenceManager.getString(Constants.KEY_AVATAR)));
            String token = client.devToken(preferenceManager.getString(Constants.KEY_USER_ID));
            client.connectUser(user1, token).enqueue();
            startActivity(intent);
            finish();
        }

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setListener();
    }

    private void signIn(){
        loading(true);
        db.collection(Constants.KEY_COLLECTION_USER)
                .whereEqualTo(Constants.KEY_IS_DELETE,false)
                .whereEqualTo(Constants.KEY_PHONE_NUMBER,binding.edtPhoneNumber.getText().toString().trim())
                .get()
                .addOnCompleteListener(task->{
                    if(task.isSuccessful()&& task.getResult() !=null && task.getResult().getDocuments().size()>0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        BCrypt.Result result=BCrypt.verifyer().verify(binding.edtPassword.getText().toString().toCharArray(), documentSnapshot.getString(Constants.KEY_PASSWORD));
                        if(result.verified){
                            preferenceManager.clear();
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                            preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                            preferenceManager.putString(Constants.KEY_STAR, documentSnapshot.getString(Constants.KEY_STAR));
                            preferenceManager.putString(Constants.KEY_ABOUT_ME, documentSnapshot.getString(Constants.KEY_ABOUT_ME));
                            preferenceManager.putString(Constants.KEY_PHONE_NUMBER,documentSnapshot.getString(Constants.KEY_PHONE_NUMBER));
                            preferenceManager.putString(Constants.KEY_COUNTRY_CODE,documentSnapshot.getString(Constants.KEY_COUNTRY_CODE));
                            preferenceManager.putString(Constants.KEY_LAST_NAME,documentSnapshot.getString(Constants.KEY_LAST_NAME));
                            preferenceManager.putString(Constants.KEY_FIRST_NAME,documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                            preferenceManager.putString(Constants.KEY_AVATAR,documentSnapshot.getString(Constants.KEY_AVATAR));
                            preferenceManager.putString(Constants.KEY_BIRTHDAY,documentSnapshot.getString(Constants.KEY_BIRTHDAY));
                            preferenceManager.putString(Constants.KEY_GENDER,documentSnapshot.getString(Constants.KEY_GENDER));
                            preferenceManager.putString(Constants.KEY_INTERESTED_GENDER, documentSnapshot.getString(Constants.KEY_INTERESTED_GENDER));
                            preferenceManager.putString(Constants.KEY_NUMBER_OF_RATING, documentSnapshot.getString(Constants.KEY_NUMBER_OF_RATING));

                            User user1 = new User();
                            user1.setId(documentSnapshot.getId());
                            user1.getExtraData().put("name", documentSnapshot.getString(Constants.KEY_FIRST_NAME)+" "+documentSnapshot.getString(Constants.KEY_LAST_NAME));
                            user1.getExtraData().put("image", bitmapToUri(documentSnapshot.getString(Constants.KEY_AVATAR)));
                            String token = client.devToken(documentSnapshot.getId());
                            client.connectUser(user1, token).enqueue();
                            ArrayList<String> al= (ArrayList<String>) documentSnapshot.get(Constants.KEY_INTERESTS);
                            String[] interests = new String[al.size()];

                            for (int i = 0; i < al.size(); i++) {
                                interests[i] = al.get(i);
                            }
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < interests.length; i++) {
                                sb.append(interests[i]).append(",");
                            }
                            preferenceManager.putString(Constants.KEY_INTERESTS, sb.toString());
                            updateLikeUser(documentSnapshot.getId());
                        }
                        else{
                            showToast("Sai mật khẩu");
                            loading(false);
                        }


                    }
                    else{
                        loading(false);
                        showToast("Không thể đăng nhập");
                    }
                });
    }
    private void setListener(){
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),PhoneNumberActivity.class));
            }
        });
        binding.btnForgotPassword.setOnClickListener(view -> {
            Intent intent=new Intent(SignInActivity.this,ForgotPasswordActivity1.class);
            startActivity(intent);
        });
        binding.signInButton.setOnClickListener(view -> {
            if(isValidSignIn()){
                signIn();
            }
        });
    }
    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

    }
    private boolean isValidSignIn(){
        if(binding.edtPhoneNumber.getText().toString().isEmpty()){
            showToast("Nhập số điện thoại");
            return false;

        }
        else if(!Patterns.PHONE.matcher(binding.edtPhoneNumber.getText().toString()).matches()){
            showToast("Số điện thoại không hợp lệ");
            return false;
        }
        else if (binding.edtPassword.getText().toString().isEmpty()){
            showToast("Nhập mật khẩu");
            return false;
        }
        return true;
    }
    private void updateChatUser(String id){
        db.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_ID_1,id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder sb = new StringBuilder();
                            for (DocumentSnapshot each : task.getResult().getDocuments()) {
                                sb.append(each.get(Constants.KEY_ID_2)).append(",");
                            }
                            preferenceManager.putString(Constants.KEY_CHAT_IDS, sb.toString());
                            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    }
                });
    }
    private void updateLikeUser(String id){
        db.collection(Constants.KEY_COLLECTION_LIKE)
                .whereEqualTo(Constants.KEY_ID_1,id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder sb = new StringBuilder();
                            for (DocumentSnapshot each : task.getResult().getDocuments()) {
                                sb.append(each.get(Constants.KEY_ID_2)).append(",");
                            }
                            preferenceManager.putString(Constants.KEY_COLLECTION_LIKE, sb.toString());
                            updateChatUser(id);
                        }
                    }
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
    private void loading(boolean isLoading){
        if(isLoading){
            binding.signInButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else{
            binding.signInButton.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}