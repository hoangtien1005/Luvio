package com.android.Luvio1.activities.Main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.android.Luvio1.databinding.ActivityChannelListBinding;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.FilterObject;
import io.getstream.chat.android.client.logger.ChatLogLevel;
import io.getstream.chat.android.client.models.Filters;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.livedata.ChatDomain;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel;
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModelBinding;
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory;
import static java.util.Collections.singletonList;

public class ChannelListActivity extends AppCompatActivity {
    

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActivityChannelListBinding binding = ActivityChannelListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Context context = view.getContext();


        ChatClient client = new ChatClient.Builder("an38qgjtsfsj", getApplicationContext())
                .logLevel(ChatLogLevel.ALL)
                .build();
        new ChatDomain.Builder(client, getApplicationContext()).build();

//        TODO: get user's id, name, image from database
        User user = new User();
        user.setId("tien");
        user.getExtraData().put("name", "Tiến");
        user.getExtraData().put("image", "https://bit.ly/2TIt8NR");

        String token = client.devToken(user.getId());

        client.connectUser(user, token).enqueue();




        FilterObject filter = Filters.and(
                Filters.eq("type", "messaging"),
                Filters.in("members", singletonList(user.getId()))
        );

        ChannelListViewModelFactory factory = new ChannelListViewModelFactory(
                filter,
                ChannelListViewModel.DEFAULT_SORT
        );

        ChannelListViewModel channelsViewModel =
                new ViewModelProvider(this, factory).get(ChannelListViewModel.class);


        ChannelListViewModelBinding.bind(channelsViewModel, binding.channelListView, this);
        binding.channelListView.setChannelItemClickListener(
                channel -> startActivity(ChannelActivity.newIntent(this, channel))
        );
    }

}