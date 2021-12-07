package com.android.Luvio1.activities.Setting;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio1.R;
import com.android.Luvio1.databinding.ActivityBlockListBinding;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BlockListActivity extends AppCompatActivity {

    private ActivityBlockListBinding binding;
    PreferenceManager preferenceManager;
    FirebaseFirestore db;

    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> images = new ArrayList<>();

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBlockListBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        db = FirebaseFirestore.getInstance();
        setContentView(binding.getRoot());

        context = this;

        binding.btnBack.setOnClickListener(view -> {
            onBackPressed();
        });

        if(preferenceManager.getString(Constants.KEY_BLOCK) != null) {
            db.collection(Constants.KEY_COLLECTION_USER)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful() && task.getResult() != null) {

                                String[] blockIds = preferenceManager.getString(Constants.KEY_BLOCK).split(",");

                                for(DocumentSnapshot doc : task.getResult().getDocuments()) {

                                    for(int i = 0 ; i < blockIds.length; i++) {
//                                        found block user
                                        if(blockIds[i].equals(doc.getId())) {
                                            names.add(doc.getString(Constants.KEY_FIRST_NAME) + " " + doc.getString(Constants.KEY_LAST_NAME));
                                            images.add(doc.getString(Constants.KEY_AVATAR));
                                        }
                                    }
                                    CustomAdapter adapter = new CustomAdapter(context, R.layout.custom_block_list_item, names, images);
                                    binding.listView.setAdapter(adapter);

                                }
                            }
                        }
                    });


        }

    }
}