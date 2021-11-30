package com.android.Luvio1.activities.Main;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.Luvio1.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class UserViewHolder extends RecyclerView.ViewHolder {
    public TextView txt_name,txt_age,txt_bio,txt_star;
    public ImageButton info_btn,message_btn,like_btn;
    public RoundedImageView avatar;
    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_name=itemView.findViewById(R.id.txt_name);
        txt_age=itemView.findViewById(R.id.txt_age);
        txt_bio=itemView.findViewById(R.id.txt_bio);
        txt_star=itemView.findViewById(R.id.txt_star);
        avatar=itemView.findViewById(R.id.user_avatar);
        info_btn= itemView.findViewById(R.id.info_btn);
        message_btn=itemView.findViewById(R.id.message_btn);
        like_btn= itemView.findViewById(R.id.like_btn);
    }
}

