package com.android.Luvio1.activities.Main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.Luvio1.R;
import com.android.Luvio1.models.User;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    ArrayList<User> list = new ArrayList<>();

    public UserAdapter(Context context) {
        this.context = context;
    }
    public void setItems(ArrayList<User> users){
        list.addAll(users);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserViewHolder vh=(UserViewHolder) holder;
        User user=list.get(position);
        vh.txt_name.setText(user.getFirstName()+" "+user.getLastName());
        if(user.getAboutMe()==""){
            vh.txt_bio.setText(user.getBirthday());
        }
        vh.txt_bio.setText(user.getAboutMe());
        vh.txt_star.setText(user.getStar());
        vh.txt_age.setText(findAge(user.getBirthday()));
        vh.avatar.setImageBitmap(decodeImage(user.getAvatar()));
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public Bitmap decodeImage(String encodeImage){
        byte[] imageBytes= Base64.decode(encodeImage,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
        return bitmap;
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
