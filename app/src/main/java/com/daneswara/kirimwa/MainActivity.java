package com.daneswara.kirimwa;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daneswara.kirimwa.adapter.AdapterKontak;
import com.daneswara.kirimwa.object.Grup;
import com.daneswara.kirimwa.object.Kontak;
import com.daneswara.kirimwa.object.Message;
import com.daneswara.kirimwa.object.StatusUser;
import com.daneswara.kirimwa.tools.ExpandableHeightGridView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mega4tech.whatsappapilibrary.WhatsappApi;
import com.mega4tech.whatsappapilibrary.exception.WhatsappNotInstalledException;
import com.mega4tech.whatsappapilibrary.liseteners.GetContactsListener;
import com.mega4tech.whatsappapilibrary.liseteners.SendMessageListener;
import com.mega4tech.whatsappapilibrary.model.WContact;
import com.mega4tech.whatsappapilibrary.model.WMessage;

import java.io.IOException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private LinearLayout f2, f3;
    private Intent mServiceIntent;
    String nama[];
    int foto[];
    private FirebaseAuth mAuth;
    //    private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    private String uid_user;
    List<Kontak> datakontak;
    ExpandableHeightGridView gridView;
    private String riwayat;
    private static final String TAG = "FCM Service";
    //    private ArrayAdapter<String> list;
    //String id_device;
    private TextView list_message;

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    private String token = FirebaseInstanceId.getInstance().getToken();

    BottomNavigationView navigation;
    SharedPreferences sharedpreferences;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    f2.setVisibility(View.VISIBLE);
                    f3.setVisibility(View.GONE);
                    mOption.clear();
                    getMenuInflater().inflate(R.menu.menu_bot, mOption);
                    setTitle("Message");
                    editor.putInt("menu", 2);
                    editor.commit();
                    return true;
                case R.id.navigation_notifications:
                    f2.setVisibility(View.GONE);
                    f3.setVisibility(View.VISIBLE);
                    mOption.clear();
                    getMenuInflater().inflate(R.menu.menu_pengaturan, mOption);
                    setTitle("Pengaturan");
                    editor.putInt("menu", 3);
                    editor.commit();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
//        mDatabase = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        sharedpreferences = getSharedPreferences("menu", Context.MODE_PRIVATE);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (!WhatsappApi.getInstance().isWhatsappInstalled()) {
            Toast.makeText(this, "Whatsapp not installed", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!WhatsappApi.getInstance().isRootAvailable()) {
            Toast.makeText(this, "Root is not available", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentUser == null) {
            Intent keluar = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(keluar);
            finish();
        } else {
//            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            id_device = telephonyManager.getDeviceId();
//            if (id_device == null) {
//                Toast.makeText(MainActivity.this, "ID Device is empty", Toast.LENGTH_SHORT).show();
//                Intent keluar = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(keluar);
//                finish();
//            }
            setContentView(R.layout.activity_main);
            setTitle("Kontak");
//            fl = findViewById(R.id.kontak);
            f2 = findViewById(R.id.bot);
            f3 = findViewById(R.id.pengaturan);

            navigation = findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            navigation.setSaveEnabled(true);

            list_message = findViewById(R.id.message);


            uid_user = mAuth.getCurrentUser().getUid();
            final SwitchCompat sub = findViewById(R.id.simpleSwitch);
            db.collection("device").document(token).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        sub.setChecked((boolean) documentSnapshot.getData().get("campaign"));
                        list_message.setText(documentSnapshot.getData().get("log")+"");
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
            mServiceIntent = new Intent(MainActivity.this, MessageService.class);
            db.collection("device").document(token).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if((boolean)documentSnapshot.getData().get("campaign") && !isMyServiceRunning(MessageService.class)){
                        startService(mServiceIntent);
                    } else if(!(boolean) documentSnapshot.getData().get("campaign") && isMyServiceRunning(MessageService.class)){
                        stopService(mServiceIntent);
                    }
                }
            });
            final TextView sync_contact = findViewById(R.id.sync_contact);
            db.collection("device").document(token).collection("kontak").document("sync").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (e != null) {
//                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        CharSequence estimasi = DateUtils.getRelativeTimeSpanString((long) documentSnapshot.get("time"), Calendar.getInstance().getTimeInMillis(), 0);
                        sync_contact.setText("Last Synced " + estimasi);
                    } else {
                        Log.d(TAG, "Current data: null");
                    }

                }
            });
            sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sub.isChecked()) {
                        if(!isMyServiceRunning(MessageService.class)) {
                            startService(mServiceIntent);
                        }
                        campaign(true);
                        Log.d(TAG, "Campaign Activated");
                        Toast.makeText(MainActivity.this, "Campaign Activated", Toast.LENGTH_SHORT).show();
                    } else {
                        campaign(false);
                        if(isMyServiceRunning(MessageService.class)){
                            stopService(mServiceIntent);
                        }
                        Toast.makeText(MainActivity.this, "Campaign Unactivated", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Campaign Unactivated");
                    }
                }
            });
            RelativeLayout setting_contact = findViewById(R.id.setting_contact);
            setting_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Toast.makeText(MainActivity.this, "Syncing with WhatsApp Contact", Toast.LENGTH_SHORT).show();
                        WhatsappApi.getInstance().getContacts(MainActivity.this, new GetContactsListener() {
                            @Override
                            public void receiveWhatsappContacts(List<WContact> contacts) {
                                int i = 0;
                                for (WContact contact : contacts) {
                                    String id = contact.getId().split("@")[0]; //+ ", " + contact.getId().split("@")[0];
                                    String nama = contact.getName();//+ ", " + contact.getId().split("@")[0];
                                    String raw_id = getContactId(id);//+ ", " + contact.getId().split("@")[0];
                                    if (id != null && nama != null) {
                                        writeNewKontak(id, nama, raw_id);
                                    }
                                    i++;
                                }
                                Map<String, Object> time = new HashMap<>();

                                time.put("time", Calendar.getInstance().getTimeInMillis());
                                db.collection("device").document(token).collection("kontak").document("sync").set(time).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                    }
                                });

                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Cursor groupCursor = getContentResolver().query(
                            ContactsContract.Groups.CONTENT_URI,
                            new String[]{
                                    ContactsContract.Groups._ID,
                                    ContactsContract.Groups.TITLE
                            }, null, null, null
                    );
                    groupCursor.moveToFirst();
                    while (groupCursor.moveToNext()) //
                    {
                        String s0 = groupCursor.getString(0);                //contact_id
                        String s1 = groupCursor.getString(1);                //contact_name
                        Grup grup = new Grup(s0, s1);
                        db.collection("device").document(token).collection("grup").document(s0).set(grup).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                e.printStackTrace();
                            }
                        });
                        Log.d("tag", "groupID: " + s0 + "title: " + s1);
                    }
                }
            });
        }
    }

    Menu mOption;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mOption = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        int menunya = sharedpreferences.getInt("menu", 0);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (menunya == 0) {
            editor.putInt("menu", 2);
            editor.commit();
            f2.setVisibility(View.VISIBLE);
            f3.setVisibility(View.GONE);
            navigation.setSelectedItemId(R.id.navigation_dashboard);
        } else if (menunya == 2) {
            f2.setVisibility(View.VISIBLE);
            f3.setVisibility(View.GONE);
            navigation.setSelectedItemId(R.id.navigation_dashboard);
        } else if (menunya == 3) {
            f2.setVisibility(View.GONE);
            f3.setVisibility(View.VISIBLE);
            navigation.setSelectedItemId(R.id.navigation_notifications);
        } else {
            editor.putInt("menu", 2);
            editor.commit();
            f2.setVisibility(View.VISIBLE);
            f3.setVisibility(View.GONE);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_item_tambah_bot) {
//            Intent bot = new Intent(MainActivity.this, DetailBot.class);
//            startActivity(bot);

            Message pesan = new Message("6285730595101", "Tes " + Calendar.getInstance().getTimeInMillis(), "wait");
            db.collection("device").document(token).collection("message").add(pesan);
            // Do something
            return true;
        } else if (id == R.id.action_item_logout) {
            if(isMyServiceRunning(MessageService.class)){
                stopService(mServiceIntent);
            }
            mAuth.signOut();
            FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.commit();
            Intent keluar = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(keluar);
            finish();
            // Do something
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void writeNewKontak(String nomer, String nama, String id) {
        Kontak user = new Kontak(nomer, nama, id, getGroupIdFor(id));
//        System.out.println("idnya adalah "+id);
//        System.out.println("group id nya adalah"+getGroupIdFor(id));
//        StatusUser userstatus = new StatusUser(id_device);
        db.collection("device").document(token).collection("kontak").document(nomer).set(user, SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
//        mDatabase.child("kontak").child(id_device).child(nomer).setValue(user);
//        mDatabase.child("kontak").child(id_device).child(nomer).child("status").setValue(userstatus);
//        mDatabase.child("kontak").child(id_device).child(nomer).child("score").setValue(0);
    }

    private void campaign(Boolean aktif) {
        Map<String, Object> message = new HashMap<>();
        message.put("campaign", aktif);
        db.collection("device").document(token).update(message);
    }

    public String getGroupIdFor(String contactId) {
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String where = String.format(
                "%s = ? AND %s = ?",
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID);

        String[] whereParams = new String[]{
                ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE,
                contactId,
        };

        String[] selectColumns = new String[]{
                ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
        };


        Cursor groupIdCursor = getContentResolver().query(
                uri,
                selectColumns,
                where,
                whereParams,
                null);
        try {
            String idgrup = "";
            if (groupIdCursor.moveToFirst()) {
                idgrup += groupIdCursor.getString(groupIdCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID)) + ",";
                while (groupIdCursor.moveToNext()) {
                    idgrup += groupIdCursor.getString(groupIdCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID)) + ",";
                }
            }

            return idgrup;
        } finally {
            groupIdCursor.close();
        }
    }

    private int cek = 0;

    public String getContactId(String nomer) {
        String contactid26 = null;
//
        ContentResolver contentResolver = getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode("+" + nomer));

        Cursor cursor =
                contentResolver.query(
                        uri,
                        new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID},
                        null,
                        null,
                        null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                contactid26 = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));

            }
            cursor.close();
        }
        if (contactid26 == null) {
            Log.d("tag", "No contact found associated with this number");
//            Toast.makeText(Coba.this, "No contact found associated with this number", Toast.LENGTH_SHORT).show();
            if (cek == 0) {
                cek++;
                return getGroupIdFor(nomer.replaceFirst("62", "0"));
            } else {
                return "";
            }
        } else {
//            Log.d("tag", "No contact found associated with this number");
            return contactid26;
//            Intent intent_contacts = new Intent(Intent.ACTION_VIEW, Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactid26)));
//            //Intent intent_contacts = new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/" + contactid26));
//            startActivity(intent_contacts);
        }

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
