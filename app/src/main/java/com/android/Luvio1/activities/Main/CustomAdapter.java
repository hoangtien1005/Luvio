package com.android.Luvio1.activities.Main;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.Luvio1.R;


public class  CustomAdapter extends ArrayAdapter<String> {
    Context context;
    String[] names, ages, bios;
    String[] stars;
    Integer[] avatars;
    int resource;

    public CustomAdapter(@NonNull Context context, int resource, @NonNull String[] names,
                         String[] ages, String[] stars, String[] bios, Integer[] avatars) {
        super(context, resource, names);
        this.context = context;
        this.names = names;
        this.ages = ages;
        this.stars = stars;
        this.bios = bios;
        this.avatars = avatars;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(resource, null);

//        TextView nameAge = (TextView) view.findViewById(R.id.name_age);
//        TextView star = (TextView) view.findViewById(R.id.star);
//        TextView bio = (TextView) view.findViewById(R.id.bio);
        ImageView avatar = (ImageView) view.findViewById(R.id.user_avatar);

        ImageButton likeBtn = (ImageButton) view.findViewById(R.id.like_btn);
        ImageButton infoBtn = (ImageButton) view.findViewById(R.id.info_btn);
        ImageButton messageBtn = (ImageButton) view.findViewById(R.id.message_btn);

        ImageView likeCheck = (ImageView) view.findViewById(R.id.liked_check);


        likeBtn.setOnClickListener(view1 ->
        {
            switch (likeCheck.getVisibility()) {
                case View.VISIBLE:
                    likeCheck.setVisibility(View.INVISIBLE);
                    break;
                case View.INVISIBLE:
                    likeCheck.setVisibility(View.VISIBLE);
                    break;
            }

        });

//        nameAge.setText(names[position] + ", " + ages[position]);
//        star.setText(stars[position]);
//        bio.setText(bios[position]);
        avatar.setImageResource(avatars[position]);
        return view;
    }


}

