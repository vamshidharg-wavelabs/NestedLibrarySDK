package com.example.explibrarychatsdk;

import static android.provider.Contacts.GroupMembership.GROUP_ID;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.CustomMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.models.MessageReceipt;
import com.cometchat.pro.models.TextMessage;
import com.cometchat.pro.models.TypingIndicator;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.example.explibrarychatsdk.constants.AppConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class WavelabsChatActivity extends AppCompatActivity {

    public static final String TAG = "LoginClass";
    private static String token;
    private static int count = 0;
    private static Context context = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG, "I am getting called");

        String uid = getIntent().getStringExtra("USER_ID");
        if(uid != null) {
            launchChatScreen(this, uid);
        } else {
            Intent intent = new Intent();
            intent.setClass(this, CometChatUI.class);
            startActivity(intent);
        }
    }

    public static void launchChatScreen(Activity MainActivity, String UID) {
        context = MainActivity.getBaseContext();
        try {
            CometChat.login(UID, AppConfig.AUTH_KEY, new CometChat.CallbackListener<User>() {

                @Override
                public void onSuccess(User user) {
                    Log.d(TAG, "Login Successful: " + user.toString());

                    token = MyFirebaseMessagingService.token; // firebaseInstance
                    if (token == null)
                        fetchFirebaseToken(MainActivity, UID);
                    else
                        registerPushNotifications(MainActivity, token, UID);
                }

                @Override
                public void onError(CometChatException e) {
                    Log.d(TAG, "Login failed with exception: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fetchFirebaseToken(Activity MainActivity, String UID) {
        //@Provided by Google docs
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                token = task.getResult();
                Log.d(TAG, token);
                registerPushNotifications(MainActivity, token, UID);
            }
        });
    }

    private static void registerPushNotifications(Activity MainActivity, String token, String uid) {
        CometChat.registerTokenForPushNotification(token, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e("onSuccessPN: ", s);

                Intent intent = new Intent();
                intent.setClass(MainActivity, CometChatUI.class);
                MainActivity.startActivity(intent);
            }

            @Override
            public void onError(CometChatException e) {
                Log.e("onErrorPN: ", e.getMessage());
            }
        });
        // registerMessageListener();
    }

    private static void registerMessageListener() {
        CometChat.addMessageListener(
                token,
                new CometChat.MessageListener() {
                    @Override
                    public void onTextMessageReceived(TextMessage textMessage) {
                        Log.e(TAG, " onTextMessageReceivedI will be called" + textMessage.toString());
                        count++;
                        //new MyFirebaseMessagingService().showNotifcation(textMessage);
                        showNotification(textMessage);
                        super.onTextMessageReceived(textMessage);
                    }

                    @Override
                    public void onMediaMessageReceived(MediaMessage mediaMessage) {
                        Log.e(TAG, "onMediaMessageReceived I will be called");
                        super.onMediaMessageReceived(mediaMessage);
                    }

                    @Override
                    public void onCustomMessageReceived(CustomMessage customMessage) {
                        Log.e(TAG, " onCustomMessageReceivedI will be called");
                        super.onCustomMessageReceived(customMessage);
                    }

                    @Override
                    public void onTypingStarted(TypingIndicator typingIndicator) {
                        Log.e(TAG, "onTypingStarted I will be called");
                        super.onTypingStarted(typingIndicator);
                    }

                    @Override
                    public void onTypingEnded(TypingIndicator typingIndicator) {
                        Log.e(TAG, "onTypingEnded I will be called");
                        super.onTypingEnded(typingIndicator);
                    }

                    @Override
                    public void onMessagesDelivered(MessageReceipt messageReceipt) {
                        Log.e(TAG, "onMessagesDelivered I will be called");
                        super.onMessagesDelivered(messageReceipt);
                    }

                    @Override
                    public void onMessagesRead(MessageReceipt messageReceipt) {
                        Log.e(TAG, " onMessagesReadI will be called");
                        super.onMessagesRead(messageReceipt);
                    }

                    @Override
                    public void onMessageEdited(BaseMessage baseMessage) {
                        Log.e(TAG, "onMessageEdited I will be called");
                        super.onMessageEdited(baseMessage);
                    }

                    @Override
                    public void onMessageDeleted(BaseMessage baseMessage) {
                        Log.e(TAG, "onMessageDeletedI will be called");
                        super.onMessageDeleted(baseMessage);
                    }
                }
        );
    }

    static void showNotification(TextMessage baseMessage) {

        if (context == null) return;

        Intent messageIntent = new Intent(
                context,
                CometChatMessageListActivity.class
        );
        messageIntent
                .putExtra(
                        UIKitConstants.IntentStrings.TYPE,
                        baseMessage.getReceiverType()
                );

        if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_USER)) {
            messageIntent.putExtra(UIKitConstants.IntentStrings.NAME, baseMessage.getSender().getName());
            messageIntent.putExtra(UIKitConstants.IntentStrings.UID, baseMessage.getSender().getUid());
            messageIntent.putExtra(UIKitConstants.IntentStrings.AVATAR, baseMessage.getSender().getAvatar());
            messageIntent.putExtra(UIKitConstants.IntentStrings.STATUS, baseMessage.getSender().getStatus());
        } else if (baseMessage.getReceiverType().equals(CometChatConstants.RECEIVER_TYPE_GROUP)) {
            messageIntent.putExtra(UIKitConstants.IntentStrings.GUID, ((Group) baseMessage.getReceiver()).getGuid());
            messageIntent.putExtra(UIKitConstants.IntentStrings.NAME, ((Group) baseMessage.getReceiver()).getName());
            messageIntent.putExtra(UIKitConstants.IntentStrings.GROUP_DESC, ((Group) baseMessage.getReceiver()).getDescription());
            messageIntent.putExtra(UIKitConstants.IntentStrings.GROUP_TYPE, ((Group) baseMessage.getReceiver()).getGroupType());
            messageIntent.putExtra(UIKitConstants.IntentStrings.GROUP_OWNER, ((Group) baseMessage.getReceiver()).getOwner());
            messageIntent.putExtra(UIKitConstants.IntentStrings.MEMBER_COUNT, ((Group) baseMessage.getReceiver()).getMembersCount());
        }

        PendingIntent messagePendingIntent = PendingIntent.getActivity(
                context,
                0123,
                messageIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    context,
                    AppConfig.CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.cc)
                    .setContentTitle(baseMessage.getSender().getName())
                    //.setContentText(json.getString("alert"))
                    .setContentText(baseMessage.getText())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    //.setColor(context.getResources().getColor(R.color.colorPrimaryuikit))
                    //.setLargeIcon(getBitmapFromURL(baseMessage.getSender().getAvatar()))
                    .setGroup(GROUP_ID)
                    .setContentIntent(messagePendingIntent)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setOngoing(false)
                    .setAutoCancel(true);

            NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(context, AppConfig.CHANNEL_2_ID)
                    .setContentTitle("CometChat")
                    .setContentText(count + " messages")
                    .setSmallIcon(R.drawable.cc)
                    .setGroup(GROUP_ID)
                    .setGroupSummary(true)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(baseMessage.getId(), builder.build());
            notificationManager.notify(0, summaryBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
