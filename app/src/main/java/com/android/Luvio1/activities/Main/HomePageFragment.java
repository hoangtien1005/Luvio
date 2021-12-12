package com.android.Luvio1.activities.Main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.android.Luvio1.activities.Setting.ThemeChangeFragment;
import com.android.Luvio1.activities.User.ProfilePageActivity;
import com.android.Luvio1.firebase.DBUserManager;
import com.android.Luvio1.interfaces.PageCallBack;
import com.android.Luvio1.models.UserModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePageFragment extends ThemeChangeFragment implements PageCallBack {
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    ImageButton filterBtn,profileBtn;
    TextInputEditText searchBar;
    DBUserManager DBUserManager;
    Context context=null;
    boolean isLoading=false;
    String key=null;
    MainActivity main;
    PageCallBack callBack;
//    int lastVisible;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        callBack = this;
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

        swipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        recyclerView= (RecyclerView) view.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);
        filterBtn=(ImageButton) view.findViewById(R.id.filter_btn);
        profileBtn=(ImageButton) view.findViewById(R.id.my_profile_btn);
        searchBar=(TextInputEditText)view.findViewById(R.id.search_bar);

        userAdapter = new UserAdapter(context);
        recyclerView.setAdapter(userAdapter);
        RecyclerView.LayoutManager manager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);
        DBUserManager =new DBUserManager();
        loadData();
        setListener();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {


            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                LinearLayoutManager linearLayoutManager=(LinearLayoutManager) recyclerView.getLayoutManager();
                int totalItem = linearLayoutManager.getItemCount();
                int lastVisible= linearLayoutManager.findLastVisibleItemPosition();

                if(totalItem<lastVisible+3){
                    if(!isLoading){
                        isLoading=true;
                        loadData();
                    }
                }
            }
        });
        return view;

    }

    protected void setListener()
    {
        filterBtn.setOnClickListener(view -> {
            SearchDialog(R.layout.search_filter, callBack);

        });
        profileBtn.setOnClickListener(view -> {
            Intent intent=new Intent(context, ProfilePageActivity.class);
            startActivity(intent);
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }
    private void SearchDialog (int layoutStyle, PageCallBack callBack){
        BottomSheetDialogFragment bottomSheetDialogFragment = new SearchFilterActivity(layoutStyle, callBack);
        bottomSheetDialogFragment.setShowsDialog(true);
        bottomSheetDialogFragment.show(main.getSupportFragmentManager(),bottomSheetDialogFragment.getTag());
    }

    private void loadData() {



        DBUserManager.get(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<UserModel> userModels = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    UserModel userModel = data.getValue(UserModel.class);
                    userModels.add(userModel);
                    key = data.getKey();

                }
                userAdapter.setItems(userModels);
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

    @Override
    public void callbackMethod(String data) {
        userAdapter.getFilter().filter(data);
    }
}
