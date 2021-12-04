package com.android.Luvio1.activities.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.Luvio1.R;
import com.android.Luvio1.activities.User.PersonalPageActivity;
import com.android.Luvio1.models.User;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;
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
        preferenceManager=new PreferenceManager(context);
        db=FirebaseFirestore.getInstance();
        User user=list.get(position);
        vh.txt_name.setText(user.getLastName());
        vh.txt_bio.setText(user.getGender());
        vh.txt_star.setText(user.getStar());
        vh.txt_age.setText(findAge(user.getBirthday()));
        vh.avatar.setImageBitmap(decodeImage(user.getAvatar()));
        vh.info_btn.setOnClickListener(v -> {
            Intent intent=new Intent(context, PersonalPageActivity.class);
            intent.putExtra("INFO",user);
            context.startActivity(intent);
        });
        if(isAlreadyLike(user.getFsId())){
            vh.like_check.setVisibility(View.VISIBLE);
        }

        vh.like_btn.setOnClickListener(v->{
            switch(vh.like_check.getVisibility()){
                case View.INVISIBLE:
                    likeUser(vh,user);
                    break;
                case View.VISIBLE:
                    unlikeUser(vh,user);
            }
        });

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
    private void showToast(String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();

    }

    private void unlikeUser(UserViewHolder vh,User user) {
        String[] user_like=preferenceManager.getString(Constants.KEY_COLLECTION_LIKE).split(",");
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<user_like.length;i++){
            if(!user_like[i].equals(user.getFsId())){
                sb.append(user_like[i]).append(",");
            }
        }
        preferenceManager.putString(Constants.KEY_COLLECTION_LIKE,sb.toString());
        db.collection(Constants.KEY_COLLECTION_LIKE)
                .whereEqualTo(Constants.KEY_ID_1,preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_ID_2,user.getFsId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if(task.isSuccessful()){
                                                   String id=task.getResult().getDocuments().get(0).getId();
                                                   deleteDocument(id);
                                                   vh.like_check.setVisibility(View.INVISIBLE);
                                               }
                                           }
                                       }
                );


    }
    private void deleteDocument(String id){
        db.collection(Constants.KEY_COLLECTION_LIKE)
                .document(id)
                .delete();
    }
    private void likeUser(UserViewHolder vh,User user){
        HashMap<String,Object> like_user=new HashMap<>();

        if(preferenceManager.getString(Constants.KEY_COLLECTION_LIKE)==null){
            preferenceManager.putString(Constants.KEY_COLLECTION_LIKE,user.getFsId()+",");

        }
        else{
            StringBuilder sb = new StringBuilder(preferenceManager.getString(Constants.KEY_COLLECTION_LIKE));
            sb.append(user.getFsId()).append(",");
            preferenceManager.putString(Constants.KEY_COLLECTION_LIKE,sb.toString());

        }
        like_user.put(Constants.KEY_ID_1,preferenceManager.getString(Constants.KEY_USER_ID));
        like_user.put(Constants.KEY_ID_2,user.getFsId());
        db.collection(Constants.KEY_COLLECTION_LIKE)
                .add(like_user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        showToast("Cập nhật thành công");
                        vh.like_check.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showToast("Cập nhật thất bại");
                    }
                });


    }
    private boolean isAlreadyLike( String id){
        if(preferenceManager.getString(Constants.KEY_COLLECTION_LIKE)==null){
            return false;
        }
        String[] like_user = preferenceManager.getString(Constants.KEY_COLLECTION_LIKE).split(",");
        for (int i=0;i<like_user.length;i++){
            if (id.equals(like_user[i])){
                return true;
            }
        }
        return false;
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
