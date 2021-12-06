package com.android.Luvio1.activities.Main;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.Luvio1.activities.User.PersonalPageActivity;
import com.android.Luvio1.databinding.ActivityChannelBinding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.PopupMenu;
import com.android.Luvio1.R;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.android.Luvio1.utilities.Constants;
import com.android.Luvio1.utilities.PreferenceManager;
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Normal;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.Mode.Thread;
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel.State.NavigateUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.Member;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.input.MessageInputView;
import io.getstream.chat.android.ui.message.input.viewmodel.MessageInputViewModelBinding;
import io.getstream.chat.android.ui.message.list.header.MessageListHeaderView;
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel;
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModelBinding;
import io.getstream.chat.android.ui.message.list.viewmodel.MessageListViewModelBinding;
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory;

public class ChannelActivity extends AppCompatActivity {

    private static List<Member> members;
    String currentUserId;
    String otherUserId;
    ChatClient client;

    FirebaseFirestore db;

    PreferenceManager preferenceManager;

    List<String> membersId = new ArrayList<String>();

    private final static String CID_KEY = "key:cid";

    public static Intent newIntent(Context context, Channel channel, String currentUserId, String otherUserId) {
        final Intent intent = new Intent(context, ChannelActivity.class);
        intent.putExtra(CID_KEY, channel.getCid());
        intent.putExtra("currentUserId", currentUserId);
        intent.putExtra("otherUserId", otherUserId);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Context context = getApplicationContext();

        ActivityChannelBinding binding = ActivityChannelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String cid = getIntent().getStringExtra(CID_KEY);
        if (cid == null) {
            throw new IllegalStateException("Specifying a channel id is required when starting ChannelActivity");
        }

        currentUserId = getIntent().getStringExtra("currentUserId");
        otherUserId = getIntent().getStringExtra("otherUserId");



        db = FirebaseFirestore.getInstance();
        client = ChatClient.instance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        if(preferenceManager.getString(Constants.KEY_BLOCK)!=null){
            String[] blockIds = preferenceManager.getString(Constants.KEY_BLOCK).split(",");
            for(int i = 0; i < blockIds.length; i++) {
                if(otherUserId.equals(blockIds[i])) {
                    FragmentManager fm = getSupportFragmentManager();
                    CustomDialog dialog =  new CustomDialog(context, "block", currentUserId, otherUserId);
                    dialog.setCancelable(false);
                    dialog.show(fm, "");
                }
            }
        }


        MessageListViewModelFactory factory = new MessageListViewModelFactory(cid);
        ViewModelProvider provider = new ViewModelProvider(this, factory);
        MessageListHeaderViewModel messageListHeaderViewModel = provider.get(MessageListHeaderViewModel.class);
        MessageListViewModel messageListViewModel = provider.get(MessageListViewModel.class);
        MessageInputViewModel messageInputViewModel = provider.get(MessageInputViewModel.class);


        MessageListHeaderViewModelBinding.bind(messageListHeaderViewModel, binding.messageListHeaderView, this);
        MessageListViewModelBinding.bind(messageListViewModel, binding.messageListView, this);
        MessageInputViewModelBinding.bind(messageInputViewModel, binding.messageInputView, this);

        messageListViewModel.getMode().observe(this, mode -> {
            if (mode instanceof Thread) {
                Message parentMessage = ((Thread) mode).getParentMessage();
                messageListHeaderViewModel.setActiveThread(parentMessage);
                messageInputViewModel.setActiveThread(parentMessage);
            }
//            else if (mode instanceof MessageInputView.InputMode.Normal) {
//                messageListHeaderViewModel.resetThread();
//                messageInputViewModel.resetThread();
//            }
        });

        binding.messageListView.setMessageEditHandler(messageInputViewModel::postMessageToEdit);

        messageListViewModel.getState().observe(this, state -> {
            if (state instanceof NavigateUp) {
                finish();
            }
        });

        MessageListHeaderView.OnClickListener backHandler = () -> {
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed.INSTANCE);
        };


        binding.messageListHeaderView.setAvatarClickListener(() -> {
            PopupMenu menu = new PopupMenu(context, binding.messageListHeaderView);
            menu.getMenuInflater().inflate(R.menu.chat_top_menu, menu.getMenu());
            menu.setGravity(Gravity.END);
            menu.show();

            menu.setOnMenuItemClickListener(menuItem -> {
                if(menuItem.getItemId() == R.id.profile) {
                    Intent intent = new Intent(this, PersonalPageActivity.class);
                    intent.putExtra("INFO", otherUserId);
                    startActivity(intent);

                }
                else if(menuItem.getItemId() == R.id.block) {

                    HashMap<String, Object> blockUser = new HashMap<>();
                    blockUser.put(Constants.KEY_ID_1, currentUserId);
                    blockUser.put(Constants.KEY_ID_2, otherUserId);


                    db.collection(Constants.KEY_COLLECTION_BLOCK)
                            .add(blockUser)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()) {

                                        if(preferenceManager.getString(Constants.KEY_BLOCK) == null) {
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append(otherUserId).append(",");
                                            preferenceManager.putString(Constants.KEY_BLOCK, stringBuilder.toString());
                                        } else {
                                            StringBuilder stringBuilder = new StringBuilder(preferenceManager.getString(Constants.KEY_BLOCK));
                                            stringBuilder.append(otherUserId).append(",");
                                            preferenceManager.putString(Constants.KEY_BLOCK, stringBuilder.toString());
                                        }

                                        FragmentManager fm = getSupportFragmentManager();
                                        CustomDialog dialog =  new CustomDialog(context, "block", currentUserId, otherUserId);
                                        dialog.setCancelable(false);
                                        dialog.show(fm, "");
                                    }
                                }
                            });
                }
                return true;
            });
        });

        binding.messageListHeaderView.setBackButtonClickListener(backHandler);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                backHandler.onClick();
            }
        });
    }
}