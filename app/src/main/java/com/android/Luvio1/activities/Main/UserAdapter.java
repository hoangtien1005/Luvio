package com.android.Luvio1.activities.Main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.Luvio1.R;
import com.android.Luvio1.activities.User.PersonalPageActivity;
import com.android.Luvio1.models.UserModel;
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
    ArrayList<UserModel> list = new ArrayList<>();


    public UserAdapter(Context context) {
        this.context = context;
    }
    public void setItems(ArrayList<UserModel> userModels){
        list.addAll(userModels);
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
        UserModel userModel =list.get(position);
        vh.txt_name.setText(userModel.getLastName());
        vh.txt_bio.setText(userModel.getGender());
        vh.txt_star.setText(userModel.getStar());
        vh.txt_age.setText(findAge(userModel.getBirthday()));
        vh.avatar.setImageBitmap(decodeImage(userModel.getAvatar()));
        vh.info_btn.setOnClickListener(v -> {
            Intent intent=new Intent(context, PersonalPageActivity.class);
            intent.putExtra("INFO", userModel.getFsId());
            context.startActivity(intent);
        });
        Log.i("LIKE_IDS",preferenceManager.getString(Constants.KEY_COLLECTION_LIKE));
        if(isAlreadyLike(userModel.getFsId())){

            Log.i("users",preferenceManager.getString(Constants.KEY_COLLECTION_LIKE));

            Log.i("userID", userModel.getFsId());
            Log.i("userName", userModel.getLastName());
            vh.like_check.setVisibility(View.VISIBLE);
        }

        vh.like_btn.setOnClickListener(v->{
            switch(vh.like_check.getVisibility()){
                case View.INVISIBLE:
                    likeUser(vh, userModel);
                    break;
                case View.VISIBLE:
                    unlikeUser(vh, userModel);
                    break;
            }
        });
        vh.message_btn.setOnClickListener(v->{
            if (preferenceManager.getString(Constants.KEY_CHAT_IDS)==null){
                preferenceManager.putString(Constants.KEY_CHAT_IDS,userModel.getFsId()+",");
            }
            else{
                String[] ids=preferenceManager.getString(Constants.KEY_CHAT_IDS).split(",");
                StringBuilder sb=new StringBuilder();
                for (int i = 0; i < ids.length; i++) {
                    if (!ids[i].equals(userModel.getFsId())) {
                        sb.append(ids[i]).append(",");
                    }
                }
                sb.append(userModel.getFsId()).append(",");
                preferenceManager.putString(Constants.KEY_CHAT_IDS,sb.toString());
            }
            ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ChatFragment()).commit();
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

    private void unlikeUser(UserViewHolder vh, UserModel userModel) {
        String[] user_like=preferenceManager.getString(Constants.KEY_COLLECTION_LIKE).split(",");
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<user_like.length;i++){
            if(!user_like[i].equals(userModel.getFsId())){
                sb.append(user_like[i]).append(",");
            }
        }
        preferenceManager.putString(Constants.KEY_COLLECTION_LIKE,sb.toString());
        db.collection(Constants.KEY_COLLECTION_LIKE)
                .whereEqualTo(Constants.KEY_ID_1,preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_ID_2, userModel.getFsId())
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
    private void likeUser(UserViewHolder vh, UserModel userModel){
        HashMap<String,Object> like_user=new HashMap<>();

        if(preferenceManager.getString(Constants.KEY_COLLECTION_LIKE)==null){
            preferenceManager.putString(Constants.KEY_COLLECTION_LIKE, userModel.getFsId()+",");

        }
        else{
            if(isAlreadyLike(userModel.getFsId())){
                return;
            }
            else{
                StringBuilder sb = new StringBuilder(preferenceManager.getString(Constants.KEY_COLLECTION_LIKE));
                sb.append(userModel.getFsId()).append(",");
                preferenceManager.putString(Constants.KEY_COLLECTION_LIKE,sb.toString());
                like_user.put(Constants.KEY_ID_1,preferenceManager.getString(Constants.KEY_USER_ID));
                like_user.put(Constants.KEY_ID_2, userModel.getFsId());
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


        }



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
