package com.daneswara.kirimwa;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.provider.ContactsContract;
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
import java.util.ArrayList;
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

        if (remoteMessage.getData().get("message") != null && remoteMessage.getData().get("tujuan") != null) {
            //Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
//            System.out.println("Masuk ke kirim wa");
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("message"));
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData().get("tujuan"));

            String tujuan = remoteMessage.getData().get("tujuan");
            String pesan = remoteMessage.getData().get("message");

            String telp = tujuan + "@s.whatsapp.net";

            WContact contact = new WContact("Tamu", telp);
            mReceivers = new LinkedList<>();

            mReceivers.add(contact);
            System.out.println("id:" + contact.getId());
            System.out.println("name:" + contact.getName());

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
        } else if (remoteMessage.getData().get("number") != null && remoteMessage.getData().get("nama") != null) {
//            System.out.println("Masuk ke tambah kontak");
            String number = remoteMessage.getData().get("number");
            String nama = remoteMessage.getData().get("nama");
            String addToGroup = remoteMessage.getData().get("addToGroup");
            if (remoteMessage.getData().get("removeFromGroup") == null) {
                ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
                operationList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());

                // first and last names
                operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, nama)
                        .build());

                operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .build());

                operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE,
                                ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, addToGroup)
                        .build());

                try {
                    ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
                    System.out.println(results + "");
                } catch (Exception e) {
                    System.out.println("ERROR: ");
                    e.printStackTrace();
                }
            } else if (remoteMessage.getData().get("idKontak") != null && remoteMessage.getData().get("removeFromGroup") != null) {
                String removeFromGroup = remoteMessage.getData().get("removeFromGroup");
                String idKontak = remoteMessage.getData().get("idKontak");


            } else if (remoteMessage.getData().get("addGroup") != null) {
                String GroupName = remoteMessage.getData().get("addGroup");
                ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
                operationList.add(ContentProviderOperation.newInsert(ContactsContract.Groups.CONTENT_URI)
                        .withValue(ContactsContract.Groups.TITLE, GroupName)
                        .build());

                try {
                    ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
                    System.out.println(results + "");
                } catch (Exception e) {
                    System.out.println("ERROR: ");
                    e.printStackTrace();
                }
            } else if (remoteMessage.getData().get("removeFromGroup") != null && remoteMessage.getData().get("contact_id") != null) {
                String groupId = remoteMessage.getData().get("addGroup");
                String contact_id = remoteMessage.getData().get("contact_id");
                try
                {
                    String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + " = ?";
                    String[] args = {contact_id, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE, groupId};
                    getContentResolver().delete(ContactsContract.Data.CONTENT_URI, where, args);
                }
                catch (Exception e)
                {}
            }
        }
    }
}
