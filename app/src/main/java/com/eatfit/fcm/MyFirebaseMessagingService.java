/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eatfit.fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.eatfit.R;
import com.eatfit.activity.FollowersFollowing;
import com.eatfit.constants.Constants;
import com.eatfit.fragments.FeedFragment;
import com.eatfit.helper.SharedPreference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    SharedPreference spMain;
    Intent intent;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            spMain = spMain.getInstance(this);
            try {
                String pushMessages = remoteMessage.getData().get("msg").toString();
                JSONObject pushObject = new JSONObject(pushMessages);

                String pushMessage = pushObject.optString("message");
                String pushType = pushObject.optString("messageCode");
//                String number = pushObject.optString("phone");
                String pushTitle = getString(R.string.app_name);
                if (spMain.getBoolean(Constants.ISLogin, false)) {

                    switch (Integer.parseInt(pushType)) {
                        case 1:
                            intent = new Intent(this, FollowersFollowing.class);
                            intent.putExtra("eventId", pushObject.optString("eventId"));
                            sendNotification(pushMessage, pushTitle);
                            break;
                        case 2:
                            intent = new Intent(this, FeedFragment.class);
                            intent.putExtra("eventId", pushObject.optString("eventId"));
                            sendNotification(pushMessage, pushTitle);
                            break;
                        case 3:
                            intent = new Intent(this, FeedFragment.class);
                            sendNotification(pushMessage, pushTitle);
                            break;
                        case 4:
                            intent = new Intent(this, FeedFragment.class);
                            intent.putExtra("eventId", pushObject.optString("eventId"));
                            sendNotification(pushMessage, pushTitle);
                            break;
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            spMain = spMain.getInstance(this);
            try {
                String pushMessages = remoteMessage.getData().get("msg").toString();
                JSONObject pushObject = new JSONObject(pushMessages);

                String pushMessage = pushObject.optString("pushMessage");
                String pushType = pushObject.optString("pushType");
                String number = pushObject.optString("phone");
                String pushTitle = getString(R.string.app_name);
                if (spMain.getBoolean(Constants.ISLogin, false)) {

                    switch (Integer.parseInt(pushType)) {
                        case 1:
                            intent = new Intent(this, FollowersFollowing.class);
                            intent.putExtra("eventId", pushObject.optString("eventId"));
                            sendNotification(pushMessage, pushTitle);
                            break;
                        case 2:
                            intent = new Intent(this, FeedFragment.class);
                            intent.putExtra("eventId", pushObject.optString("eventId"));
                            sendNotification(pushMessage, pushTitle);
                            break;
                        case 3:
                            intent = new Intent(this, FeedFragment.class);
                            sendNotification(pushMessage, pushTitle);
                            break;
                        case 4:
                            intent = new Intent(this, FeedFragment.class);
                            intent.putExtra("eventId", pushObject.optString("eventId"));
                            sendNotification(pushMessage, pushTitle);
                            break;
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, String pushTitle) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Set and initialize the view elements.
     */
}
