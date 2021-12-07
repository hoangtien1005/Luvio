package com.android.Luvio1.activities.Setting;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.Luvio1.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;


public class  CustomAdapter extends ArrayAdapter<String> {
    Context context;
    ArrayList<String> names = new ArrayList<>();

    ArrayList<Bitmap> avatarImages = new ArrayList<>();
    int resource;


    public CustomAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> names, ArrayList<String> avatars) {
        super(context, resource, names);
        this.context = context;
        this.names = names;

        this.resource = resource;

        for(int i = 0 ; i < avatars.size(); i++) {
            avatarImages.add(decodeImage(avatars.get(i)));
        }

    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(resource, null);

        RoundedImageView imgAvatar = (RoundedImageView) view.findViewById(R.id.imgAvatar);

        TextView txtName = (TextView) view.findViewById(R.id.txtName);

        imgAvatar.setImageBitmap(avatarImages.get(position));
        txtName.setText(names.get(position));

        return view;
    }

    private Bitmap decodeImage(String encodeImage){
        byte[] imageBytes= Base64.decode(encodeImage,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
        return bitmap;
    }

}

