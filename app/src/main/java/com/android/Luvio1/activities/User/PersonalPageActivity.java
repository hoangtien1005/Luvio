package com.android.Luvio1.activities.User;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.Luvio1.R;
import com.android.Luvio1.databinding.ActivityPersonalPageBinding;
import com.android.Luvio1.models.User;
import com.android.Luvio1.utilities.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import me.relex.circleindicator.CircleIndicator3;

public class PersonalPageActivity extends AppCompatActivity {

    private ViewPager2 mViewPager2;
    private CircleIndicator3 mCircleIndicator3;
    private ActivityPersonalPageBinding binding;
    FirebaseFirestore db;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        binding=ActivityPersonalPageBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        mViewPager2 = findViewById(R.id.view_pager2);
        mCircleIndicator3 = findViewById(R.id.circle_indicator3);


        db=FirebaseFirestore.getInstance();
        user=(User) getIntent().getSerializableExtra("INFO");
        setListener();
        setData();
    }

    void setData(){
        db.collection(Constants.KEY_COLLECTION_USER)
                .document(user.getFsId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        binding.txtPersonalName.setText(user.getFirstName() + " " + user.getLastName()+", ");
                        binding.txtAge.setText(findAge(user.getBirthday()));
                        if (user.getAboutMe()==""){
                            binding.txtAboutMe.setVisibility(View.GONE);
                        }
                        else{
                            binding.txtAboutMe.setText(user.getAboutMe());
                        }
                        binding.txtMyBirthday.setText(user.getBirthday());
                        ArrayList<String> interests = (ArrayList<String>) documentSnapshot.get(Constants.KEY_INTERESTS);
                        binding.chip1.setText(interests.get(0));
                        binding.chip2.setText(interests.get(1));
                        binding.chip3.setText(interests.get(2));
                        binding.txtMyGender.setText((String) documentSnapshot.get(Constants.KEY_GENDER));
                        if (documentSnapshot.get(Constants.KEY_CITY)!=null)
                            binding.txtMyCity.setText((String)documentSnapshot.get(Constants.KEY_CITY));
                        else
                            binding.txtMyCity.setText("Không có");

                        List<Photo> list = new ArrayList<>();
                        list.add(new Photo(decodeImage((String) documentSnapshot.get(Constants.KEY_AVATAR))));
                        if(documentSnapshot.get(Constants.KEY_IMAGES)!=null){
                            ArrayList<String> images = (ArrayList) documentSnapshot.get(Constants.KEY_IMAGES);
                            for (String str:images){
                                list.add(new Photo(decodeImage(str)));
                            }
                        }
                        PhotoAdapter ptAdapter = new PhotoAdapter(PersonalPageActivity.this,list);
                        mViewPager2.setAdapter(ptAdapter);
                        mCircleIndicator3.setViewPager(mViewPager2);

                    }
                });

    }
    private Bitmap decodeImage(String encodeImage){
        byte[] imageBytes= Base64.decode(encodeImage,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
        return bitmap;
    }
    private void setListener(){
        binding.backBtn.setOnClickListener(v -> {
            onBackPressed();
        });
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

}
