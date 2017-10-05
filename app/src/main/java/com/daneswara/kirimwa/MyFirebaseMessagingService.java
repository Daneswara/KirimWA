package com.daneswara.kirimwa;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mega4tech.whatsappapilibrary.WhatsappApi;
import com.mega4tech.whatsappapilibrary.exception.WhatsappNotInstalledException;
import com.mega4tech.whatsappapilibrary.liseteners.SendMessageListener;
import com.mega4tech.whatsappapilibrary.model.WContact;
import com.mega4tech.whatsappapilibrary.model.WMessage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Daneswara on 05/10/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    List<WContact> mReceivers;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("message").toString());
        Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("tujuan").toString());

//        String notif = remoteMessage.getNotification().getBody();
//        String[] tampung = notif.split("_");
        String tujuan = remoteMessage.getData().get("tujuan").toString();
        String pesan = remoteMessage.getData().get("message").toString();

        String telp = tujuan+"@s.whatsapp.net";

        WContact contact = new WContact("Tamu", telp);
        mReceivers = new LinkedList<>();

        mReceivers.add(contact);
        System.out.println("id:"+contact.getId());
        System.out.println("name:"+contact.getName());

        String text = pesan;
        WMessage message = new WMessage(text, null, MyFirebaseMessagingService.this);

        try {
            WhatsappApi.getInstance().sendMessage(mReceivers, message, MyFirebaseMessagingService.this, new SendMessageListener() {
                @Override
                public void finishSendWMessage(List<WContact> contact, WMessage message) {
                    Toast.makeText(MyFirebaseMessagingService.this, "your message has been sent successfully", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (WhatsappNotInstalledException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
