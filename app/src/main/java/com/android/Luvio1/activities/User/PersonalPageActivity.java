package com.android.Luvio1.activities.User;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.Luvio1.R;
import com.android.Luvio1.databinding.ActivityProfilePageBinding;

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
