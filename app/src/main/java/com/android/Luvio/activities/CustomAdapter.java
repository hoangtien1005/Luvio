package com.android.Luvio.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.Luvio.R;


public class CustomAdapter extends ArrayAdapter<String> {
    Context context;
    String[] names, ages, bios;
    String[] stars;
    Integer[] avatars;
    int resource;

    public CustomAdapter(@NonNull Context context, int resource, @NonNull String[] names,
                         String[] ages, String[] stars, String[] bios, Integer[] avatars) {
        super(context, resource);
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

        TextView nameAge = (TextView) view.findViewById(R.id.name_age);
        TextView star = (TextView) view.findViewById(R.id.star);
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
                case View.INVISIBLE:
                    likeCheck.setVisibility(View.VISIBLE);
            }

        });

        nameAge.setText(names[position] + ", " + ages[position]);
        star.setText(stars[position]);
        avatar.setBackgroundResource(avatars[position]);
        return view;
    }


}
