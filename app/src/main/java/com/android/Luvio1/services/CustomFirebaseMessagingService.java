package com.android.Luvio1.services;

import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.models.Device;
import io.getstream.chat.android.client.models.PushMessage;
import io.getstream.chat.android.client.models.PushProvider;

public class CustomFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NotNull String token) {
        // Update device's token on Stream backend
        try {
            ChatClient.setDevice(
                    new Device(
                            token,
                            PushProvider.FIREBASE
                    )
            );
        } catch (IllegalStateException exception) {
            // ChatClient was not initialized
        }
    }

    @Override
    public void onMessageReceived(@NotNull RemoteMessage message) {
        try {
            // Handle RemoteMessage and convert it to a PushMessage to sent back to Stream
            PushMessage pushMessage = new PushMessage(
                    message.getData().get("message_id"),
                    message.getData().get("channel_id"),
                    message.getData().get("channel_type")
            );
            ChatClient.handlePushMessage(pushMessage);
        } catch (IllegalStateException exception) {
            // ChatClient was not initialized
        }
    }
}
