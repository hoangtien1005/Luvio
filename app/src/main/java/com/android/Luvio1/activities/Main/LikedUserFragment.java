package com.android.Luvio1.activities.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.Luvio1.R;
import com.android.Luvio1.activities.User.ProfilePageActivity;
import com.android.Luvio1.interfaces.CompleteQueryListener;
import com.android.Luvio1.models.UserModel;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LikedUserFragment extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    ArrayList<UserModel> like_userModels;
    ImageButton filterBtn,profileBtn;
    PreferenceManager preferenceManager;
    String[] like_users_id ;
    FirebaseFirestore db;
    Context context=null;
    boolean isLoading=false;

    MainActivity main;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            main = (MainActivity) getActivity();
            context=getActivity();

        }catch (IllegalStateException e){
            throw new IllegalStateException("Error");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_home_page,container,false);
        swipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipe);
        recyclerView= (RecyclerView) view.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        filterBtn=(ImageButton) view.findViewById(R.id.filter_btn);
        profileBtn=(ImageButton) view.findViewById(R.id.my_profile_btn);
        like_userModels =new ArrayList<>();
        db= FirebaseFirestore.getInstance();
        userAdapter = new UserAdapter(context);
        recyclerView.setAdapter(userAdapter);
        LinearLayoutManager manager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        preferenceManager =new PreferenceManager(context);
        loadData();
        setListener();
        return view;

    }


    protected void setListener()
    {
        filterBtn.setOnClickListener(view ->
        {
//            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            SearchDialog(R.layout.search_filter);
        });
        profileBtn.setOnClickListener(view -> {
            Intent intent=new Intent(context, ProfilePageActivity.class);
            startActivity(intent);
        });
    }

    private void SearchDialog (int layoutStyle){
        BottomSheetDialogFragment bottomSheetDialogFragment = new SearchFilterActivity(layoutStyle);
        bottomSheetDialogFragment.setShowsDialog(true);
        bottomSheetDialogFragment.show(main.getSupportFragmentManager(),bottomSheetDialogFragment.getTag());
    }

    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);

        if (preferenceManager.getString(Constants.KEY_COLLECTION_LIKE)==null){
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        like_users_id=preferenceManager.getString(Constants.KEY_COLLECTION_LIKE).split(",");
        ArrayList<UserModel> like_userModels = new ArrayList<>();
        readData(db.collection(Constants.KEY_COLLECTION_USER)
                .get(), new CompleteQueryListener() {
            @Override
            public void onSuccess(Task<QuerySnapshot> task) {
                for (DocumentSnapshot documentSnapshot: task.getResult().getDocuments()){
                    for (int i=0;i<like_users_id.length;i++){
                        if (documentSnapshot.getId().equals(like_users_id[i])){
                            UserModel userModel = new UserModel((String) documentSnapshot.get(Constants.KEY_AVATAR),
                                    (String)documentSnapshot.get(Constants.KEY_GENDER),
                                    (String)documentSnapshot.get(Constants.KEY_FIRST_NAME),
                                    (String)documentSnapshot.get(Constants.KEY_LAST_NAME),
                                    (String)documentSnapshot.get(Constants.KEY_BIRTHDAY),
                                    (String)documentSnapshot.getId(),
                                    (String)documentSnapshot.get(Constants.KEY_STAR),
                                    (String)documentSnapshot.get(Constants.KEY_ABOUT_ME));
                            like_userModels.add(userModel);
                        }
                    }




                }
                userAdapter.setItems(like_userModels);
                userAdapter.notifyDataSetChanged();
                isLoading=false;
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onFailure() {

            }
        });




    }
    private void readData(Task<QuerySnapshot> querySnapshotTask , final CompleteQueryListener listener){
        querySnapshotTask.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()&&task.getResult()!=null){
                    listener.onSuccess(task);
                }
                else{
                    listener.onFailure();
                }
            }
        });

    }
}
