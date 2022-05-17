package com.example.explibrarychatsdk;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.Call;
import com.cometchat.pro.helpers.CometChatHelper;
import com.cometchat.pro.models.BaseMessage;
import com.cometchat.pro.models.Group;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.example.explibrarychatsdk.constants.AppConfig;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseService";
    private JSONObject json;
    private Intent intent;
    private int count = 0;
    private Call call;
    public static String token;
    private static final int REQUEST_CODE = 12;

    private boolean isCall;

    public static void subscribeUserNotification(String UID) {
        FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_USER + "_" +
                UID).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, UID + " Subscribed Success");
            }
        });
    }

    public static void unsubscribeUserNotification(String UID) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(AppConfig.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_USER + "_" +
                UID).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, UID + " Unsubscribed Success");
            }
        });
    }

    public static void subscribeGroupNotification(String GUID) {
        FirebaseMessaging.getInstance().subscribeToTopic(AppConfig.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_GROUP + "_" +
                GUID).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e(TAG, GUID + " Subscribed Success");
            }
        });
    }

    public static void unsubscribeGroupNotification(String GUID) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(AppConfig.APP_ID + "_" + CometChatConstants.RECEIVER_TYPE_GROUP + "_" +
                GUID);
    }

    @Override
    public void onNewToken(String s) {
        token = s;
        Log.d(TAG, "onNewToken: " + s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "onMessageReceieved");
        try {
            count++;
            json = new JSONObject(remoteMessage.getData());
            Log.d(TAG, "JSONObject: " + json.toString());
            //JSONObject messageData = new JSONObject(json.getString("message"));
            BaseMessage baseMessage = CometChatHelper
                    .processMessage(
                            new JSONObject(
                                    remoteMessage
                                            .getData()
                                            .get("message")
                            )
                    );
            showNotifcation(baseMessage);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getBitmapFromURL(String strURL) {
        if (strURL != null) {
            try {
                URL url = new URL(strURL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    private void showNotifcation(BaseMessage baseMessage) {

//        Intent intent = new Intent(this, WavelabsChatActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pendingIntent = PendingIntent.getActivity(
//                this,
//                0 /* Request code */,
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT
//        );


        try {
            //int m = (int) ((new Date().getTime()));
            String GROUP_ID = "group_id";

            Intent messageIntent = new Intent(getApplicationContext(), CometChatMessageListActivity.class);
            messageIntent.putExtra(UIKitConstants.IntentStrings.TYPE, baseMessage.getReceiverType());
            messageIntent.putExtra("USER_ID", baseMessage.getConversationId());

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
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent messagePendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    0123,
                    messageIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, AppConfig.CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.cc)
                    .setContentTitle(json.getString("title"))
                    .setContentText(json.getString("alert"))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setColor(getResources().getColor(R.color.colorPrimaryuikit))
                    .setLargeIcon(getBitmapFromURL(baseMessage.getSender().getAvatar()))
                    .setGroup(GROUP_ID)
                    .setContentIntent(messagePendingIntent)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .setOngoing(false)
                    .setAutoCancel(true);
            if (baseMessage.getType().equals(CometChatConstants.MESSAGE_TYPE_IMAGE)) {
                builder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(getBitmapFromURL(((MediaMessage) baseMessage).getAttachment().getFileUrl())));
            }
            NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(this, AppConfig.CHANNEL_2_ID)
                    .setContentTitle("CometChat")
                    .setContentText(count + " messages")
                    .setSmallIcon(R.drawable.cc)
                    .setGroup(GROUP_ID)
                    .setGroupSummary(true)
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(baseMessage.getId(), builder.build());
            notificationManager.notify(
                    0,
                    summaryBuilder.build()
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Intent getCallIntent(String title) {
        Intent callIntent = new Intent(getApplicationContext(), CallNotificationAction.class);
        callIntent.putExtra(UIKitConstants.IntentStrings.SESSION_ID, call.getSessionId());
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setAction(title);
        return callIntent;
    }
}