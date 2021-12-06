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
import androidx.lifecycle.ViewModelProvider;

import com.android.Luvio1.activities.User.ProfilePageActivity;
import com.android.Luvio1.databinding.ActivityChannelListBinding;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
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


public class ChatFragment extends Fragment {
    String currentUserId;
    String otherUserId;

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

        }catch (IllegalStateException e){
            throw new IllegalStateException("Error");
        }
        
        db = FirebaseFirestore.getInstance();
        
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityChannelListBinding binding = ActivityChannelListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        preferenceManager= new PreferenceManager(context);
        ChatClient client = ChatClient.instance();


        String channelType = "messaging";

        binding.myProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProfilePageActivity.class);
            startActivity(intent);
        });

        if (preferenceManager.getString(Constants.KEY_CHAT_IDS)==null){
            return view;
        }
        String[] chat_ids=preferenceManager.getString(Constants.KEY_CHAT_IDS).split(",");
        for (int i=0;i<chat_ids.length;i++ ){
            List<String> member = Arrays.asList(preferenceManager.getString(Constants.KEY_USER_ID),chat_ids[i]);
            client.createChannel(channelType, member).enqueue();
        }

//        filter channels where members include this user
        FilterObject filter = Filters.and(
                Filters.eq("type", "messaging"),
                Filters.in("members", singletonList(preferenceManager.getString(Constants.KEY_USER_ID)))
        );

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
    }
}
