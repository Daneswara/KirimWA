package com.daneswara.kirimwa;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daneswara.kirimwa.adapter.AdapterKontak;
import com.daneswara.kirimwa.object.Kontak;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mega4tech.whatsappapilibrary.WhatsappApi;
import com.mega4tech.whatsappapilibrary.liseteners.GetContactsListener;
import com.mega4tech.whatsappapilibrary.model.WContact;

import java.security.Timestamp;
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
    String nama[];
    int foto[];
    private FirebaseAuth mAuth;
//    private DatabaseReference mDatabase;
    private FirebaseFirestore db;
    private String uid_user;
    List<Kontak> datakontak;
    ExpandableHeightGridView gridView;
    private static final String TAG = "FCM Service";
    String id_device;

    static {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

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
                    setTitle("Campaign");
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
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            id_device = telephonyManager.getDeviceId();
            if(id_device == null){
                Toast.makeText(MainActivity.this, "ID Device is empty", Toast.LENGTH_SHORT).show();
                Intent keluar = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(keluar);
                finish();
            }
            setContentView(R.layout.activity_main);
            setTitle("Kontak");
//            fl = findViewById(R.id.kontak);
            f2 = findViewById(R.id.bot);
            f3 = findViewById(R.id.pengaturan);

            navigation = findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            navigation.setSaveEnabled(true);

            uid_user = mAuth.getCurrentUser().getUid();

            final SwitchCompat sub = findViewById(R.id.simpleSwitch);
            db.collection("message").document(id_device).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        sub.setChecked((boolean)documentSnapshot.getData().get("campaign"));
                    } else {
                        Log.d(TAG, "Current data: null");
                    }
                }
            });
            db.collection("message").document(id_device).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    sub.setChecked((boolean)documentSnapshot.getData().get("campaign"));
                }
            });
            final TextView sync_contact = findViewById(R.id.sync_contact);
            db.collection("kontak").document(mAuth.getCurrentUser().getUid()).collection(id_device).document("sync").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                    if (e != null) {
//                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        CharSequence estimasi = DateUtils.getRelativeTimeSpanString((long) documentSnapshot.get("time"), Calendar.getInstance().getTimeInMillis(), 0);
                        sync_contact.setText("Last Synced "+estimasi);
                    } else {
                        Log.d(TAG, "Current data: null");
                    }

                }
            });
            sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sub.isChecked()) {
                        campaign(true);
                        Log.d(TAG, "Campaign Activated");
                        Toast.makeText(MainActivity.this, "Campaign Activated", Toast.LENGTH_SHORT).show();
                    } else {
                        campaign(false);
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
                                    if (id != null && nama != null) {
                                        writeNewKontak(id, nama);
                                    }
                                    i++;
                                }
                                Map<String,Object> time = new HashMap<>();

                                time.put("time", Calendar.getInstance().getTimeInMillis());
                                db.collection("kontak").document(mAuth.getCurrentUser().getUid()).collection(id_device).document("sync").set(time).addOnFailureListener(new OnFailureListener() {
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
            Intent bot = new Intent(MainActivity.this, DetailBot.class);
            startActivity(bot);
            // Do something
            return true;
        } else if (id == R.id.action_item_logout) {
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

    private void writeNewKontak(String nomer, String nama) {
        Kontak user = new Kontak(nomer, nama);
//        StatusUser userstatus = new StatusUser(id_device);
        db.collection("kontak").document(mAuth.getCurrentUser().getUid()).collection(id_device).document(nomer).set(user, SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
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
        Map<String,Object> message = new HashMap<>();
        message.put("campaign", aktif);
        db.collection("message").document(id_device).set(message);
    }
}
