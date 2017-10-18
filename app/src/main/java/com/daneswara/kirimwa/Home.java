package com.daneswara.kirimwa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mega4tech.whatsappapilibrary.WhatsappApi;
import com.mega4tech.whatsappapilibrary.exception.WhatsappNotInstalledException;
import com.mega4tech.whatsappapilibrary.liseteners.SendMessageListener;
import com.mega4tech.whatsappapilibrary.model.WContact;
import com.mega4tech.whatsappapilibrary.model.WMessage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Home extends AppCompatActivity {
    private static final String TAG = "FCM Service";
    EditText tujuan, pesan;
    Button kirim, sub, unsub;
    List<WContact> mReceivers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (!WhatsappApi.getInstance().isWhatsappInstalled()) {
            Toast.makeText(this, "Whatsapp not installed", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!WhatsappApi.getInstance().isRootAvailable()) {
            Toast.makeText(this, "Root is not available", Toast.LENGTH_SHORT).show();
            return;
        }


        setContentView(R.layout.activity_home);

        mReceivers = new LinkedList<>();

        tujuan = (EditText) findViewById(R.id.nohp);
        pesan = (EditText) findViewById(R.id.pesan);
        kirim = (Button) findViewById(R.id.kirim);
        sub = (Button) findViewById(R.id.subscribe);
        unsub = (Button) findViewById(R.id.unsubscribe);

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMessaging.getInstance().subscribeToTopic("news");
                Log.d(TAG, "Subscribed to news topic");
            }
        });

        unsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("news");
                Log.d(TAG, "Unsubscribed to news topic");
            }
        });

        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(pesan.getText()) || TextUtils.isEmpty(tujuan.getText())) {
                    Toast.makeText(Home.this, "Mohon masukkan tujuan dan pesan anda untuk mengirim pesan wa", Toast.LENGTH_SHORT).show();
                    return;
                }
                String telp = tujuan.getText().toString()+"@s.whatsapp.net";

                WContact contact = new WContact("Tamu", telp);
                mReceivers.add(contact);
                System.out.println("id:"+contact.getId());
                System.out.println("name:"+contact.getName());

                String text = pesan.getText().toString();
                WMessage message = new WMessage(text, null, Home.this);

                try {
                    WhatsappApi.getInstance().sendMessage(mReceivers, message, Home.this, new SendMessageListener() {
                        @Override
                        public void finishSendWMessage(List<WContact> contact, WMessage message) {
                            Toast.makeText(Home.this, "your message has been sent successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (WhatsappNotInstalledException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
