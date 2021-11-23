package com.android.Luvio.activities.User;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.Luvio.R;
import com.android.Luvio.databinding.ActivityProfilePageBinding;
import com.android.Luvio.utilities.Constants;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class PersonalPageActivity extends AppCompatActivity {

    private ViewPager2 mViewPager2;
    private CircleIndicator3 mCircleIndicator3;
    private ActivityProfilePageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);

        mViewPager2 = findViewById(R.id.view_pager2);
        mCircleIndicator3 = findViewById(R.id.circle_indicator3);

        PhotoAdapter ptAdapter = new PhotoAdapter(this,getListPhoto());
        mViewPager2.setAdapter(ptAdapter);
        mCircleIndicator3.setViewPager(mViewPager2);

    }
    private List<Photo>getListPhoto(){
        List<Photo> list = new ArrayList<>();
        list.add(new Photo(R.drawable.ic_next));
        list.add(new Photo(R.drawable.ic_next));
        list.add(new Photo(R.drawable.ic_next));
        list.add(new Photo(R.drawable.ic_next));
        return list;
    }

}
