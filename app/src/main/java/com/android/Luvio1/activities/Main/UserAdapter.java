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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.Luvio1.R;
import com.android.Luvio1.activities.Setting.ThemeChangeActivity;
import com.android.Luvio1.activities.User.PersonalPageActivity;
import com.android.Luvio1.models.UserModel;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private Context context;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore db;
    ArrayList<UserModel> list = new ArrayList<>();
    ArrayList<UserModel> allUserList = new ArrayList<>();

    public UserAdapter(Context context) {
        this.context = context;
    }

    public void setItems(ArrayList<UserModel> userModels) {
        list.addAll(userModels);
        allUserList.addAll(userModels);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserModel e = null;
        this.onBindViewHolder(holder, position, e);
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, UserModel e) {
        UserViewHolder vh = (UserViewHolder) holder;
        preferenceManager = new PreferenceManager(context);

        UserModel userModel = e == null ? list.get(holder.getAbsoluteAdapterPosition()) : e;
        db = FirebaseFirestore.getInstance();
        vh.txt_name.setText(userModel.getLastName());
        vh.txt_bio.setText(userModel.getGender());

        float starFloat = Float.parseFloat(userModel.getStar());
        starFloat = (float) (Math.round(starFloat * 2) / 2.0);
        String star = Float.toString(starFloat);
        vh.txt_star.setText(star);

        vh.txt_age.setText(findAge(userModel.getBirthday()));
        vh.avatar.setImageBitmap(decodeImage(userModel.getAvatar()));
        vh.info_btn.setOnClickListener(v -> {
            Intent intent = new Intent(context, PersonalPageActivity.class);
            intent.putExtra("INFO", userModel.getFsId());
            context.startActivity(intent);
        });
        if (isAlreadyLike(userModel.getFsId())) {
            vh.like_check.setVisibility(View.VISIBLE);
        } else {
            vh.like_check.setVisibility(View.INVISIBLE);
        }

        vh.like_btn.setOnClickListener(v -> {
            switch (vh.like_check.getVisibility()) {
                case View.INVISIBLE:
                    likeUser(vh, userModel);
                    break;
                case View.VISIBLE:
                    unlikeUser(vh, userModel);
                    break;
            }
        });
        vh.message_btn.setOnClickListener(v -> {
            if (preferenceManager.getString(Constants.KEY_CHAT_IDS) == null) {
                preferenceManager.putString(Constants.KEY_CHAT_IDS, userModel.getFsId() + ",");
            } else {

                HashMap<String, Object>chats=new HashMap<>();
                chats.put(Constants.KEY_ID_1,preferenceManager.getString(Constants.KEY_USER_ID));
                chats.put(Constants.KEY_ID_2,userModel.getFsId());
                db.collection(Constants.KEY_COLLECTION_CHAT)
                        .add(chats)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @java.lang.Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if(task.isSuccessful()){
                                    String[] ids = preferenceManager.getString(Constants.KEY_CHAT_IDS).split(",");
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < ids.length; i++) {
                                        if (!ids[i].equals(userModel.getFsId())) {
                                            sb.append(ids[i]).append(",");
                                        }
                                    }
                                    sb.append(userModel.getFsId()).append(",");
                                    preferenceManager.putString(Constants.KEY_CHAT_IDS, sb.toString());
                                }
                                else{
                                    showToast("Không thể cập nhật chat ids");
                                }
                            }
                        });



            }
            BottomNavigationView navBar = (BottomNavigationView) ((MainActivity) context).findViewById(R.id.bottom_navigation);
            navBar.setSelectedItemId(R.id.message);
            ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChatFragment()).commit();
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Bitmap decodeImage(String encodeImage) {
        byte[] imageBytes = Base64.decode(encodeImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return bitmap;
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }

    private void unlikeUser(UserViewHolder vh, UserModel userModel) {
        String[] user_like = preferenceManager.getString(Constants.KEY_COLLECTION_LIKE).split(",");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < user_like.length; i++) {
            if (!user_like[i].equals(userModel.getFsId())) {
                sb.append(user_like[i]).append(",");
            }
        }
        preferenceManager.putString(Constants.KEY_COLLECTION_LIKE, sb.toString());
        db.collection(Constants.KEY_COLLECTION_LIKE)
                .whereEqualTo(Constants.KEY_ID_1, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_ID_2, userModel.getFsId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   String id = task.getResult().getDocuments().get(0).getId();
                                                   deleteLikeDocument(id, preferenceManager.getString(Constants.KEY_USER_ID), userModel.getFsId());
                                                   vh.like_check.setVisibility(View.INVISIBLE);
                                               }
                                           }
                                       }
                );

    }

    private void deleteLikeDocument(String id, String currentUserId, String otherUserId) {
        db.collection(Constants.KEY_COLLECTION_LIKE)
                .document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        findAndDeleteMatch(currentUserId, otherUserId);
                    }
                });
    }

    private void deleteMatchDocument(String id) {
        db.collection(Constants.KEY_COLLECTION_MATCH)
                .document(id)
                .delete();
    }


    private void likeUser(UserViewHolder vh, UserModel userModel) {
        HashMap<String, Object> like_user = new HashMap<>();

        if (preferenceManager.getString(Constants.KEY_COLLECTION_LIKE) == null) {
            preferenceManager.putString(Constants.KEY_COLLECTION_LIKE, userModel.getFsId() + ",");

        } else {
            StringBuilder sb = new StringBuilder(preferenceManager.getString(Constants.KEY_COLLECTION_LIKE));
            sb.append(userModel.getFsId()).append(",");
            preferenceManager.putString(Constants.KEY_COLLECTION_LIKE, sb.toString());
            like_user.put(Constants.KEY_ID_1, preferenceManager.getString(Constants.KEY_USER_ID));
            like_user.put(Constants.KEY_ID_2, userModel.getFsId());
//            update collection "likes"
            db.collection(Constants.KEY_COLLECTION_LIKE)
                    .add(like_user)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                showToast("Cập nhật thành công");
                                vh.like_check.setVisibility(View.VISIBLE);
                                checkAndUpdateMatch(userModel);
                            } else {
                                showToast("Cập nhật thất bại");
                            }
                        }
                    });

        }


    }

    private boolean isAlreadyLike(String id) {
        if (preferenceManager.getString(Constants.KEY_COLLECTION_LIKE) == null) {
            return false;
        }
        String[] like_user = preferenceManager.getString(Constants.KEY_COLLECTION_LIKE).split(",");
        for (int i = 0; i < like_user.length; i++) {
            if (id.equals(like_user[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean isAlreadyMatch(String id) {
        if (preferenceManager.getString(Constants.KEY_COLLECTION_MATCH) == null) {
            return false;
        }
        String[] match_user = preferenceManager.getString(Constants.KEY_COLLECTION_MATCH).split(",");
        for (int i = 0; i < match_user.length; i++) {
            if (id.equals(match_user[i])) {
                return true;
            }
        }
        return false;
    }


    private void checkAndUpdateMatch(UserModel userModel) {
        db.collection(Constants.KEY_COLLECTION_LIKE)
                .whereEqualTo(Constants.KEY_ID_2, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_ID_1, userModel.getFsId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                                                    if(!isAlreadyMatch(userModel.getFsId())) {
                                                       addMatch(userModel);
                                                    }
                                               }
                                           }
                                       }
                );
    }

    private void addMatch(UserModel userModel) {
        HashMap<String, Object> match_user = new HashMap<>();
        match_user.put(Constants.KEY_ID_1, preferenceManager.getString(Constants.KEY_USER_ID));
        match_user.put(Constants.KEY_ID_2, userModel.getFsId());

        db.collection((Constants.KEY_COLLECTION_MATCH))
                .add(match_user)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()) {
                            if (preferenceManager.getString(Constants.KEY_COLLECTION_MATCH) == null) {
                                preferenceManager.putString(Constants.KEY_COLLECTION_MATCH, userModel.getFsId() + ",");

                            } else {
                                StringBuilder sb = new StringBuilder(preferenceManager.getString(Constants.KEY_COLLECTION_MATCH));
                                sb.append(userModel.getFsId()).append(",");
                                preferenceManager.putString(Constants.KEY_COLLECTION_MATCH, sb.toString());
                            }
                            showToast("Bạn đã match với người này");
                        } else {
                            showToast("update match thất bại");
                        }
                    }
                });
    }



    private void findAndDeleteMatch(String id1, String id2) {
        db.collection(Constants.KEY_COLLECTION_MATCH)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            for(DocumentSnapshot doc: task.getResult().getDocuments()) {
                                String _id1 = doc.getString(Constants.KEY_ID_1);
                                String _id2 = doc.getString(Constants.KEY_ID_2);
                                if((id1.equals(_id1) && id2.equals(_id2)) || (id1.equals(_id2) && id2.equals(_id1))) {
                                    deleteMatchDocument(doc.getId());
                                }
                            }

                        }
                    }
                });
    }


    public String findAge(String birthday) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(MaterialDatePicker.todayInUtcMilliseconds());
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        String todayYear = format.format(calendar.getTime());
        String birthYear = birthday.split("/", 3)[2];
        int age = Integer.parseInt(todayYear) - Integer.parseInt(birthYear);
        return String.valueOf(age);
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<UserModel> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(allUserList);
                } else {
                    if (constraint.toString().contains("@")) {
                        String[] strings = constraint.toString().split("@");
                        String searchInput = strings[0];
                        String[] data = strings[1].split("\\|");
                        float minStar = Float.parseFloat(data[0]);
                        float maxStar = Float.parseFloat(data[1]);
                        int minAge = Integer.parseInt(data[2]);
                        int maxAge = Integer.parseInt(data[3]);
                        String Gender = data[4];
                        for (UserModel user : allUserList) {
                            float userStar = Float.parseFloat(user.getStar());
                            int userAge = Integer.parseInt(user.getAge());
                            String userGender = user.getGender();
                            if (minStar <= userStar &&
                                    userStar <= maxStar &&
                                    minAge <= userAge &&
                                    userAge <= maxAge &&
                                    userGender.equals(Gender) &&
                                    user.getLastName().toLowerCase()
                                            .contains(searchInput.toLowerCase())) {
                                filteredList.add(user);
                            }
                        }


                    } else if (constraint.toString().contains("|")) {
                        String[] data = constraint.toString().split("\\|");
                        float minStar = Float.parseFloat(data[0]);
                        float maxStar = Float.parseFloat(data[1]);
                        int minAge = Integer.parseInt(data[2]);
                        int maxAge = Integer.parseInt(data[3]);
                        String Gender = data[4];

                        for (UserModel user : allUserList) {
                            float userStar = Float.parseFloat(user.getStar());
                            int userAge = Integer.parseInt(user.getAge());
                            String userGender = user.getGender();
                            if (minStar <= userStar && userStar <= maxStar && minAge <= userAge && userAge <= maxAge && userGender.equals(Gender)) {
                                filteredList.add(user);
                            }
                        }
                    } else {
                        for (UserModel user : allUserList) {
                            if (user.getLastName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                                filteredList.add(user);
                            }
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list.clear();
                list.addAll((Collection<? extends UserModel>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
