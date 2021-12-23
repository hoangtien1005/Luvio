package com.android.Luvio1.activities.Main;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.annotations.NotNull;

import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.PushMessage;
import io.getstream.chat.android.client.notifications.handler.ChatNotificationHandler;
import io.getstream.chat.android.client.notifications.handler.NotificationConfig;

public class CustomChatNotificationHandler extends ChatNotificationHandler {


    public CustomChatNotificationHandler(@NonNull Context context, @NonNull NotificationConfig config) {
        super(context,config);

    }

    @NonNull
    @Override
    public NotificationCompat.Builder buildNotification(int notificationId, @NonNull Channel channel, @NonNull io.getstream.chat.android.client.models.Message message) {
        return super.buildNotification(notificationId, channel, message);
    }

    @NonNull
    @Override
    public NotificationCompat.Builder buildNotificationGroupSummary(@NonNull Channel channel, @NonNull io.getstream.chat.android.client.models.Message message) {
        return super.buildNotificationGroupSummary(channel, message);
    }

    @Override
    public boolean onPushMessage(@NonNull PushMessage message) {
        // Handle push message and return true if message should not be handled by SDK
        return true;
    }
}

