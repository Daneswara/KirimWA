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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.daneswara.kirimwa.adapter.AdapterKontak;
import com.daneswara.kirimwa.object.Kontak;
import com.daneswara.kirimwa.object.KontakDB;
import com.daneswara.kirimwa.tools.ExpandableHeightGridView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mega4tech.whatsappapilibrary.WhatsappApi;
import com.mega4tech.whatsappapilibrary.exception.WhatsappNotInstalledException;
import com.mega4tech.whatsappapilibrary.liseteners.GetContactsListener;
import com.mega4tech.whatsappapilibrary.model.WContact;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {
    private LinearLayout fl, f2, f3;
    String nama[];
    int foto[];
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String nomeruser;
    List<Kontak> datakontak;
    ExpandableHeightGridView gridView;
    private static final String TAG = "FCM Service";

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
                case R.id.navigation_home:
                    fl.setVisibility(View.VISIBLE);
                    f2.setVisibility(View.GONE);
                    f3.setVisibility(View.GONE);
                    mOption.clear();
                    getMenuInflater().inflate(R.menu.menu, mOption);
                    setTitle("Kontak");
                    editor.putInt("menu", 1);
                    editor.commit();
                    return true;
                case R.id.navigation_dashboard:
                    fl.setVisibility(View.GONE);
                    f2.setVisibility(View.VISIBLE);
                    f3.setVisibility(View.GONE);
                    mOption.clear();
                    getMenuInflater().inflate(R.menu.menu_bot, mOption);
                    setTitle("Campaign");
                    editor.putInt("menu", 2);
                    editor.commit();
                    return true;
                case R.id.navigation_notifications:
                    fl.setVisibility(View.GONE);
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sharedpreferences = getSharedPreferences("menu", Context.MODE_PRIVATE);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent keluar = new Intent(MainActivity.this, PhoneAuthActivity.class);
            startActivity(keluar);
            finish();
        } else {
            if (!WhatsappApi.getInstance().isWhatsappInstalled()) {
                Toast.makeText(this, "Whatsapp not installed", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!WhatsappApi.getInstance().isRootAvailable()) {
                Toast.makeText(this, "Root is not available", Toast.LENGTH_SHORT).show();
                return;
            }
            setContentView(R.layout.activity_main);
            setTitle("Kontak");

            navigation = findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            navigation.setSaveEnabled(true);
            fl = findViewById(R.id.kontak);
            f2 = findViewById(R.id.bot);
            f3 = findViewById(R.id.pengaturan);

            nomeruser = mAuth.getCurrentUser().getPhoneNumber().replace("+", "");
            datakontak = new LinkedList<>();


            gridView = (ExpandableHeightGridView) findViewById(R.id.gridview);
            mDatabase.child("kontak").child(nomeruser).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    datakontak.add(dataSnapshot.getValue(Kontak.class));
                    gridView.setAdapter(new AdapterKontak(MainActivity.this, datakontak));
                    gridView.setExpanded(true);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            final SwitchCompat sub = (SwitchCompat) findViewById(R.id.simpleSwitch);
            mDatabase.child("bots").child(nomeruser).child("news").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    System.out.println("value" + dataSnapshot.getValue());
                    System.out.println("key" + dataSnapshot.getKey());
                    sub.setChecked((boolean) dataSnapshot.getValue());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (sub.isChecked()) {
                        subscribe("news", true);
                        FirebaseMessaging.getInstance().subscribeToTopic("news");
                        Log.d(TAG, "Subscribed to news topic");
                        Toast.makeText(MainActivity.this, "Subscribed to news topic", Toast.LENGTH_SHORT).show();
                    } else {
                        subscribe("news", false);
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
                        Toast.makeText(MainActivity.this, "Unsubscribed to news topic", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Unsubscribed to news topic");
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
            editor.putInt("menu", 1);
            editor.commit();
            fl.setVisibility(View.VISIBLE);
            f2.setVisibility(View.GONE);
            f3.setVisibility(View.GONE);
            navigation.setSelectedItemId(R.id.navigation_home);
        } else if (menunya == 1) {
            fl.setVisibility(View.VISIBLE);
            f2.setVisibility(View.GONE);
            f3.setVisibility(View.GONE);
            navigation.setSelectedItemId(R.id.navigation_home);
        } else if (menunya == 2) {
            fl.setVisibility(View.GONE);
            f2.setVisibility(View.VISIBLE);
            f3.setVisibility(View.GONE);
            navigation.setSelectedItemId(R.id.navigation_dashboard);
        } else if (menunya == 3) {
            fl.setVisibility(View.GONE);
            f2.setVisibility(View.GONE);
            f3.setVisibility(View.VISIBLE);
            navigation.setSelectedItemId(R.id.navigation_notifications);
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
        if (id == R.id.action_item_refresh_menu) {
            try {
                Toast.makeText(MainActivity.this, "Syncing with WhatsApp Contact", Toast.LENGTH_SHORT).show();
                nomeruser = mAuth.getCurrentUser().getPhoneNumber().replace("+", "");
                WhatsappApi.getInstance().getContacts(this, new GetContactsListener() {
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

                    }
                });
            } catch (WhatsappNotInstalledException e) {
                e.printStackTrace();
            }
            // Do something
            return true;
        } else if (id == R.id.action_item_add_kontak) {
            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
            startActivity(intent);
            // Do something
            return true;
        } else if (id == R.id.action_item_tambah_bot) {
            Intent bot = new Intent(MainActivity.this, DetailBot.class);
            startActivity(bot);
            // Do something
            return true;
        } else if (id == R.id.action_item_logout) {
            mAuth.signOut();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            Intent keluar = new Intent(MainActivity.this, PhoneAuthActivity.class);
            startActivity(keluar);
            finish();
            // Do something
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void writeNewKontak(String nomer, String nama) {
        Kontak user = new Kontak(nomer, nama);
        mDatabase.child("kontak").child(nomeruser).child(nomer).setValue(user);
    }

    private void subscribe(String topic, Boolean aktif) {
        mDatabase.child("bots").child(nomeruser).child(topic).setValue(aktif);
    }
}
