package com.daneswara.kirimwa;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.daneswara.kirimwa.object.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mega4tech.whatsappapilibrary.WhatsappApi;
import com.mega4tech.whatsappapilibrary.exception.WhatsappNotInstalledException;
import com.mega4tech.whatsappapilibrary.liseteners.SendMessageListener;
import com.mega4tech.whatsappapilibrary.model.WContact;
import com.mega4tech.whatsappapilibrary.model.WMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static eu.chainfire.libsuperuser.Debug.TAG;

/**
 * Created by Daneswara on 29/10/2017.
 */

public class MessageService extends IntentService {
    private String riwayat;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String token;
    private Thread messageThread = null;
    private boolean status;
    public MessageService(){
        super("MessageService");
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        token = FirebaseInstanceId.getInstance().getToken();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    List<WContact> mReceivers;

    public void KirimPesan(final Message pesan, final String id_message) {
        String nomer = pesan.number.replace("+", "");
        String telp = nomer + "@s.whatsapp.net";

        WContact contact = new WContact("Tamu", telp, "1");
        mReceivers = new LinkedList<>();

        mReceivers.add(contact);
        System.out.println("id:" + contact.getId());
        System.out.println("name:" + contact.getName());

        String text = pesan.message;
        WMessage message = new WMessage(text, null, this);

        try {

            WhatsappApi.getInstance().sendMessage(mReceivers, message, this, new SendMessageListener() {
                @Override
                public void finishSendWMessage(List<WContact> contact, WMessage message) {
                    riwayat += "Message to " + pesan.number + " successfully sent\n";
                    Map<String, Object> status = new HashMap<>();
                    status.put("status", "sent");
                    db.collection("device").document(token).collection("message").document(id_message).update(status).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
                    Map<String, Object> log = new HashMap<>();
                    log.put("log", riwayat);
                    db.collection("device").document(token).update(log).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
//                    Toast.makeText(MainActivity.this, "your message has been sent successfully", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (WhatsappNotInstalledException e) {
            e.printStackTrace();
            Toast.makeText(MessageService.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            riwayat += "Message to " + pesan.number + " failed to sent\n";
            Map<String, Object> status = new HashMap<>();
            status.put("status", "failed");
            db.collection("device").document(token).collection("message").document(id_message).update(status).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MessageService.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            riwayat += "Message to " + pesan.number + " failed to sent\n";
            Map<String, Object> status = new HashMap<>();
            status.put("status", "failed");
            db.collection("device").document(token).collection("message").document(id_message).update(status).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        status = false;
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if ( intent == null){
            Log.i(TAG, "recieved null intent");
            return(START_NOT_STICKY);
        }
//        messageThread = new MessageThread();
//        messageThread.start();
        status = true;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("Kirim WA")
                .setContentText("Activated")
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .build();
// batas treat
        if(mAuth.getCurrentUser() != null) {
            db.collection("device").document(token).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        riwayat = documentSnapshot.getData().get("log")+"\n";
//                        if(!(boolean) documentSnapshot.getData().get("campaign")){
//                            stopForeground(true);
//                            stopSelf();
//                        }
                    }
                    if (documentSnapshot.getData().get("log")==null){
                        riwayat = "Start\n";
                        Map<String, Object> log = new HashMap<>();
                        log.put("log", riwayat);
                        db.collection("device").document(token).update(log).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }
            });

            db.collection("device").document(token).collection("message").whereEqualTo("status", "wait").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "listen:error", e);
                        return;
                    }

                    for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                if(status) {
                                    Message pesan = dc.getDocument().toObject(Message.class);
                                    riwayat += "New message " + pesan.message + "\n";
                                    riwayat += "Sending message to " + pesan.number + "\n";
                                    Map<String, Object> status = new HashMap<>();
                                    status.put("status", "sending");
                                    db.collection("device").document(token).collection("message").document(dc.getDocument().getId()).update(status).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    Map<String, Object> log = new HashMap<>();
                                    log.put("log", riwayat);
                                    db.collection("device").document(token).update(log).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    Log.d(TAG, "New message: " + dc.getDocument().getData());
                                    KirimPesan(pesan, dc.getDocument().getId());
                                }
                                break;
                            case MODIFIED:
                                Log.d(TAG, "Modified message: " + dc.getDocument().getData());
                                break;
                            case REMOVED:
                                Log.d(TAG, "Removed message: " + dc.getDocument().getData());
                                break;
                        }
                    }

                }
            });
        }
        // batas
        startForeground(12, notification);
        return START_NOT_STICKY;
    }
    private class MessageThread extends Thread {
        @Override
        public void run() {

        }
    }
}

