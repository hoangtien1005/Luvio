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

import com.android.Luvio1.R;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class CustomDialog extends DialogFragment {

    String type = "isBlocked";
    int layout = R.layout.dialog_is_blocked;

    FirebaseFirestore db;

    String currentUserId;
    String otherUserId;

    PreferenceManager preferenceManager;


    public CustomDialog() {

    }

    public CustomDialog(Context context, String type, String currentUserId, String otherUserId) {
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setTitle(type);


        if(type.equals("rating")) {
            Button btnDone = (Button)view.findViewById(R.id.btnDone);
            btnDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
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
}