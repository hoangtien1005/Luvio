package com.android.Luvio1.activities.Main;

import static java.util.Collections.singletonList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.android.Luvio1.R;
import com.android.Luvio1.activities.Setting.ThemeChangeFragment;
import com.android.Luvio1.activities.User.ProfilePageActivity;
import com.android.Luvio1.databinding.ActivityChannelListBinding;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.FilterObject;
import io.getstream.chat.android.client.logger.ChatLogLevel;
import io.getstream.chat.android.client.models.Filters;
import io.getstream.chat.android.client.models.Member;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.livedata.ChatDomain;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModelBinding;
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory;

public class ChatFragment extends ThemeChangeFragment {
    String currentUserId;
    String otherUserId;
    int currentPos= 0;
    MainActivity main;
    PreferenceManager preferenceManager;
    FirebaseFirestore db;
    Context context=null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            main = (MainActivity) getActivity();
            context=getActivity();
            preferenceManager= new PreferenceManager(context);

            if(getArguments() != null) {
                Bundle arguments = getArguments();
                currentPos = arguments.getInt("pos");
            }

        }catch (IllegalStateException e){
            throw new IllegalStateException("Error");
        }
        
        db = FirebaseFirestore.getInstance();
        updateSharedPreferences();
        
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityChannelListBinding binding = ActivityChannelListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        ChatClient client = ChatClient.instance();



        binding.tabLayout.getTabAt(currentPos).select();

//        tab listener
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if(currentPos != tab.getPosition()) {
                    currentPos = tab.getPosition();
                    Bundle bundle = new Bundle();
                    bundle.putInt("pos", currentPos);
                    FragmentManager fm = getFragmentManager();

                    ChatFragment fragment = new ChatFragment();

                    fragment.setArguments(bundle);
                    main.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.myProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfilePageActivity.class);
            startActivity(intent);
        });
        return showChannelList(view, currentPos, binding, client);
    }


    View showChannelList(View view, int currentPos, ActivityChannelListBinding binding, ChatClient client) {
        String channelType = "messaging";

        String[] matchIds = {""};
        String[] chatIds = {""};

        if(preferenceManager.getString(Constants.KEY_COLLECTION_MATCH) != null) {
            matchIds = preferenceManager.getString(Constants.KEY_COLLECTION_MATCH).split(",");
        }

        if(preferenceManager.getString(Constants.KEY_CHAT_IDS) != null) {
            chatIds = preferenceManager.getString(Constants.KEY_CHAT_IDS).split(",");
        }

        ArrayList<String> ignoreIdsList = new ArrayList<>();

        for(int i = 0 ; i < chatIds.length; i++) {
            if(!Arrays.asList(matchIds).contains(chatIds[i])) {
                ignoreIdsList.add(chatIds[i]);
            }
        }

        if(ignoreIdsList.size() == 0) {
            ignoreIdsList.add("");
        }

        String[] ignoreIds = new String[ignoreIdsList.size()];
        ignoreIds = ignoreIdsList.toArray(ignoreIds);


//        create channels
        for (int i=0 ; i<chatIds.length ; i++ ){
            List<String> members = Arrays.asList(preferenceManager.getString(Constants.KEY_USER_ID),chatIds[i]);
            client.createChannel(channelType, members).enqueue();
        }

        FilterObject filter;
//        filter channels where members include this user

        if(currentPos == 0) {
            filter = Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.in("members", singletonList(preferenceManager.getString(Constants.KEY_USER_ID))),
                    Filters.nin("members", ignoreIds)
            );
        } else {
            filter = Filters.and(
                    Filters.eq("type", "messaging"),
                    Filters.in("members", singletonList(preferenceManager.getString(Constants.KEY_USER_ID))),
                    Filters.nin("members", matchIds)
            );
        }


        ChannelListViewModelFactory factory = new ChannelListViewModelFactory(
                filter,
                ChannelListViewModel.DEFAULT_SORT
        );

        ChannelListViewModel channelsViewModel =
                new ViewModelProvider(this, factory).get(ChannelListViewModel.class);


        ChannelListViewModelBinding.bind(channelsViewModel, binding.channelListView, this);
        binding.channelListView.setChannelItemClickListener(
                channel -> {
//                    Get currentUserId and otherUserId
                    List<Member> members =  channel.getMembers();
                    List<String> membersId = new ArrayList<String>();

                    for(int i = 0 ; i < 2; i ++)
                    {
                        membersId.add(members.get(i).getUser().getId());
                    }
                    currentUserId = client.getCurrentUser().getId();

                    for(int i = 0 ; i < 2; i ++)
                    {
                        if(!membersId.get(i).equals(currentUserId)) {
                            otherUserId = membersId.get(i);
                        }
                    }

                    db.collection(Constants.KEY_COLLECTION_BLOCK)
                            .whereEqualTo(Constants.KEY_ID_1, otherUserId)
                            .whereEqualTo(Constants.KEY_ID_2, currentUserId)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                                        Toast.makeText(context, "Người dùng này đã chặn bạn", Toast.LENGTH_LONG).show();
                                    } else {
                                        startActivity(ChannelActivity.newIntent(context, channel, currentUserId, otherUserId));
                                    }
                                }
                            });
                }
        );
        return view;
    };

    private void updateSharedPreferences() {
        if(preferenceManager.getString(Constants.KEY_COLLECTION_LIKE) != null) {
            String user_like = preferenceManager.getString(Constants.KEY_COLLECTION_LIKE);
//            nếu chưa có chat và có like
            if (preferenceManager.getString(Constants.KEY_CHAT_IDS) == null) {
                preferenceManager.putString(Constants.KEY_CHAT_IDS, user_like);
            } else {
                String[] likeIds = user_like.split(",");
                String[] chatIds = preferenceManager.getString(Constants.KEY_CHAT_IDS).split(",");
                StringBuilder sb = new StringBuilder();

                LinkedHashSet<String> set = new LinkedHashSet<>();

                set.addAll(Arrays.asList(likeIds));
                set.addAll(Arrays.asList(chatIds));

                String[] newChatIds = new String[set.size()];
                newChatIds = (String[]) set.toArray(newChatIds);

                for (int i = 0; i < newChatIds.length; i++) {
                    sb.append(newChatIds[i]).append(",");
                }
                preferenceManager.putString(Constants.KEY_CHAT_IDS, sb.toString());
            }
            updateMatch();
        }
    }

    private void updateMatch() {
        db.collection(Constants.KEY_COLLECTION_MATCH)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {

                            StringBuilder sb = new StringBuilder();

                            for(DocumentSnapshot doc: task.getResult().getDocuments()) {
                                String id1 = doc.getString(Constants.KEY_ID_1);
                                String id2 = doc.getString(Constants.KEY_ID_2);
                                String newMatchId = "";
                                if(preferenceManager.getString(Constants.KEY_USER_ID).equals(id1)) {
                                    newMatchId = id2;
                                } else if (preferenceManager.getString(Constants.KEY_USER_ID).equals(id2)){
                                    newMatchId = id1;
                                }
                                if(!newMatchId.equals("")) {
                                    sb.append(newMatchId).append(",");
                                }
                            }
                            preferenceManager.putString(Constants.KEY_COLLECTION_MATCH, sb.toString());
                        }
                    }
                });
    }
}

