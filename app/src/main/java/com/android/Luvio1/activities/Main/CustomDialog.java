package com.android.Luvio1.activities.Main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.Luvio1.R;
import com.android.Luvio1.firebase.DBUserManager;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class CustomDialog extends DialogFragment {
    String type = "isBlocked";
    int layout = R.layout.dialog_is_blocked;

    FirebaseFirestore db;
    Context context;
    String currentUserId;
    String otherUserId;

    DBUserManager DBUserManager;
    PreferenceManager preferenceManager;

    public CustomDialog() {

    }

    public CustomDialog(Context context, String type, String currentUserId, String otherUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.otherUserId = otherUserId;

        if(type.equals("rating")) {
            this.type = type;
            layout = R.layout.dialog_rating;
        }
        else if (type.equals("block")) {
            this.type = type;
            layout = R.layout.dialog_block;
        }
        else if (type.equals("isBlocked")) {
            this.type = type;
            layout = R.layout.dialog_is_blocked;
        }

        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(context);
        DBUserManager = new DBUserManager();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setTitle(type);


        if(type.equals("rating")) {
            Button btnDone = (Button)view.findViewById(R.id.btnDone);
            RatingBar rating = (RatingBar)view.findViewById(R.id.rating);

            btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String star = Float.toString(rating.getRating());
                    HashMap<String, Object> rating = new HashMap<>();
                    rating.put(Constants.KEY_ID_1, currentUserId);
                    rating.put(Constants.KEY_ID_2, otherUserId);
                    rating.put(Constants.KEY_STAR, star);

                    db.collection(Constants.KEY_COLLECTION_RATING)
                            .add(rating)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful() && task.getResult() != null) {

                                        findAndUpdateUserStar(otherUserId, star);
                                        StringBuilder sb = new StringBuilder();
                                        if(preferenceManager.getString(Constants.KEY_COLLECTION_RATING) != null) {
                                            sb.append(preferenceManager.getString(Constants.KEY_COLLECTION_RATING));
                                        }
                                        sb.append(otherUserId).append(",");
                                        preferenceManager.putString(Constants.KEY_COLLECTION_RATING, sb.toString());
                                    }
                                    dismiss();
                                }
                            });
                }
            });
        }
        else if(type.equals("block")) {
            Button btnRemove = (Button) view.findViewById(R.id.btnRemove);
            Button btnContinue = (Button) view.findViewById(R.id.btnContinue);

            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection(Constants.KEY_COLLECTION_BLOCK)
                            .whereEqualTo(Constants.KEY_ID_1, currentUserId)
                            .whereEqualTo(Constants.KEY_ID_2, otherUserId)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful() && task.getResult() != null) {

                                        db.collection(Constants.KEY_COLLECTION_BLOCK)
                                                .document(task.getResult().getDocuments().get(0).getId())
                                                .delete();

                                        StringBuilder stringBuilder = new StringBuilder();
                                        String[] blockIds = preferenceManager.getString(Constants.KEY_BLOCK).split(",");
                                        for(int i = 0; i < blockIds.length; i++) {
                                            if(!otherUserId.equals(blockIds[i])) {
                                                stringBuilder.append(blockIds[i]).append(",");
                                            }
                                        }
                                        preferenceManager.putString(Constants.KEY_BLOCK, stringBuilder.toString());
                                    }
                                }
                            });
                    dismiss();
                }
            });

            btnContinue.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                }
            });

        }
        return view;
    }

    private void findAndUpdateUserStar(String otherUserId, String newStar) {
        db.collection(Constants.KEY_COLLECTION_USER)
                .document(otherUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            String currentStar = task.getResult().getString(Constants.KEY_STAR);
                            String numberOfRating = task.getResult().getString(Constants.KEY_NUMBER_OF_RATING);

                            float newStarFloat = Float.parseFloat(newStar);
                            float currentStarFloat = Float.parseFloat(currentStar);
                            int numberOfRatingInt = Integer.parseInt(numberOfRating);

                            float totalStarFloat = currentStarFloat * numberOfRatingInt + newStarFloat;
                            numberOfRatingInt++;
                            String finalStar = Float.toString(totalStarFloat / numberOfRatingInt);
                            String finalNumberOfRating = Integer.toString(numberOfRatingInt);
                            updateFirestore(otherUserId, finalStar, finalNumberOfRating);
                            updateRealtimeDB(otherUserId, finalStar);
                        } else {
                            Toast.makeText(context, "Chấm điểm thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateFirestore(String otherUserId, String star, String numberOfRating) {
        db.collection(Constants.KEY_COLLECTION_USER)
                .document(otherUserId)
                .update(Constants.KEY_STAR, star,
                        Constants.KEY_NUMBER_OF_RATING, numberOfRating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Chấm điểm thành công", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateRealtimeDB(String otherUserId, String star) {
        HashMap<String,Object>hashMap=new HashMap();
        hashMap.put(Constants.KEY_STAR, star);

        DBUserManager.update(otherUserId, hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Cập nhật Real time DB thành công", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(e->{
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }
}