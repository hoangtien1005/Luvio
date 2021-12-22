package com.android.Luvio1.activities.Main;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.annotations.NotNull;

import io.getstream.chat.android.client.models.PushMessage;
import io.getstream.chat.android.client.notifications.handler.ChatNotificationHandler;
import io.getstream.chat.android.client.notifications.handler.NotificationConfig;

public class CustomChatNotificationHandler extends ChatNotificationHandler {

    public CustomChatNotificationHandler(@NotNull Context context, @NotNull NotificationConfig config) {
        super(context, config);



    }



    @Override
    public boolean onPushMessage(@NonNull PushMessage message) {
        // Handle push message and return true if message should not be handled by SDK
        return true;
    }
}

