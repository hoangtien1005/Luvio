package com.android.Luvio.activities.Main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.android.Luvio.R;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class Personal_Page extends AppCompatActivity {

    private ViewPager2 mViewPager2;
    private CircleIndicator3 mCircleIndicator3;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_page);

        mViewPager2 = findViewById(R.id.view_pager2);
        mCircleIndicator3 = findViewById(R.id.circle_indicator3);

        photoAdapter ptAdapter = new photoAdapter(this,getListPhoto());
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
