package com.android.Luvio1.activities.Main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.Luvio1.R;
import com.android.Luvio1.activities.User.ProfilePageActivity;
import com.android.Luvio1.firebase.RealTimeDBManager;
import com.android.Luvio1.models.User;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity{
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    ImageButton filterBtn,profileBtn;
    RealTimeDBManager realTimeDBManager;
    boolean isLoading=false;
    String key=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        bindingView();


        userAdapter = new UserAdapter(this);
        recyclerView.setAdapter(userAdapter);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        realTimeDBManager=new RealTimeDBManager();
        loadData();
        setListener();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager=(LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItem = linearLayoutManager.getItemCount();
                int lastVisible= linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if(totalItem<lastVisible+3){
                    if(!isLoading){
                        isLoading=true;
                        loadData();
                    }
                }
            }
        });
    }
    private void bindingView(){
        swipeRefreshLayout= findViewById(R.id.swipe);
        recyclerView= findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        filterBtn=(ImageButton) findViewById(R.id.filter_btn);
        profileBtn=(ImageButton) findViewById(R.id.my_profile_btn);
    }



    protected void setListener()
    {
        filterBtn.setOnClickListener(view ->
        {
//            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            SearchDialog(R.layout.search_filter);
        });
        profileBtn.setOnClickListener(view -> {
            Intent intent=new Intent(getApplicationContext(), ProfilePageActivity.class);
            startActivity(intent);
        });
    }
    private void SearchDialog (int layoutStyle){
        BottomSheetDialogFragment bottomSheetDialogFragment = new SearchFilterActivity(layoutStyle);
        bottomSheetDialogFragment.setShowsDialog(true);
        bottomSheetDialogFragment.show(getSupportFragmentManager(),bottomSheetDialogFragment.getTag());
    }

    private void loadData() {

        swipeRefreshLayout.setRefreshing(true);

        realTimeDBManager.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<User> users = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    users.add(user);
                    key = data.getKey();

                }
                userAdapter.setItems(users);
                userAdapter.notifyDataSetChanged();
                isLoading=false;
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}