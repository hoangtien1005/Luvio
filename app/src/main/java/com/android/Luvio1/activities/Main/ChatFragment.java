package com.android.Luvio1.activities.Main;

import static java.util.Collections.singletonList;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.Luvio1.databinding.ActivityChannelListBinding;
import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;

import java.util.Arrays;
import java.util.List;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.FilterObject;
import io.getstream.chat.android.client.logger.ChatLogLevel;
import io.getstream.chat.android.client.models.Filters;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.livedata.ChatDomain;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModelBinding;
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory;



public class ChatFragment extends Fragment {
    String[] ids = {"tien", "someone", "another-one", "a-fourth-one"};
    String[] names = {"Tiến", "Tài", "Tâm", "Phụng"};
    MainActivity main;
    PreferenceManager preferenceManager;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ActivityChannelListBinding binding = ActivityChannelListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        preferenceManager= new PreferenceManager(context);
        ChatClient client = ChatClient.instance();
//        TODO: get user's id, name, image from database
//        User user = new User();

//        user.setId(preferenceManager.getString(Constants.KEY_USER_ID));
//        user.getExtraData().put("name", preferenceManager.getString(Constants.KEY_FIRST_NAME)+" "+preferenceManager.getString(Constants.KEY_LAST_NAME));
//        user.getExtraData().put("image", preferenceManager.getString(Constants.KEY_AVATAR));
        Log.i("IDSSSS",preferenceManager.getString(Constants.KEY_CHAT_IDS));
//        String token=client.devToken(preferenceManager.getString(Constants.KEY_USER_ID));
//        client.connectUser(user, token).enqueue();

        String channelType = "messaging";

//        TODO: select from like table where column has the user's id and create the channel
//        example: for (id1, id2 in LikeTable)
//        {
//            List<String> members = Arrays.asList(id1, id2);
//            client.createChannel(channelType, members).enqueue();
//        }
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
                channel -> startActivity(ChannelActivity.newIntent(context, channel))
        );
        return view;
    }
}
