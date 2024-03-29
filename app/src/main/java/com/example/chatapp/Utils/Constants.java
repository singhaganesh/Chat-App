package com.example.chatapp.Utils;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_NAME = "name";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "chatAppPreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHATS = "chats";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSIONS = "conversions";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String REMOTE_MESSAGE_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MESSAGE_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MESSAGE_DATA = "data";
    public static final String REMOTE_MESSAGE_REGISTRATION_IDS = "registration_ids";
    public static final String KEY_NOTIFICATION_ID = "notification_id";

    public static HashMap<String, String> remoteMessageHeaders = null;
    public static HashMap<String, String> getRemoteMessageHeaders(){
        if (remoteMessageHeaders == null){
            remoteMessageHeaders = new HashMap<>();
            remoteMessageHeaders.put(
                    REMOTE_MESSAGE_AUTHORIZATION,
                    "key=AAAADkYuBWo:APA91bGSiwHmYUIZWCMxDP7usNajp6jxk2Asl6Q-V99OGcQexD9b_XUW2w72lAk8XMby9pUDIOxd0v11TsrwlaNujd-mSHQAtowjpp0o9W05fHH2csxoiX5lwvp1ytxCFrI18uKZB6cH"
            );
            remoteMessageHeaders.put(
                    REMOTE_MESSAGE_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMessageHeaders;
    }

}
