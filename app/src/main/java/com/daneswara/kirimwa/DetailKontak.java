package com.daneswara.kirimwa;

import android.app.ActionBar;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class DetailKontak extends AppCompatActivity {
    EditText input_nama, input_nomer;
    String status;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kontak);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        input_nama = (EditText) findViewById(R.id.input_nama);
        input_nomer = (EditText) findViewById(R.id.input_nomer);
        RadioGroup input_status = (RadioGroup) findViewById(R.id.input_status);
        if (getIntent().getStringExtra("nomer") != null) {
            String nama = getIntent().getStringExtra("nama");
            String nomer = getIntent().getStringExtra("nomer");
            status = getIntent().getStringExtra("status");

            setTitle(nama);
            input_nama.setText(nama);
            input_nomer.setText(nomer);

            if (status.equalsIgnoreCase("Calon Pembeli")) {
                input_status.check(R.id.calonpembeli);
            } else if (status.equalsIgnoreCase("Pembeli")) {
                input_status.check(R.id.pembeli);
            } else if (status.equalsIgnoreCase("Pelanggan")) {
                input_status.check(R.id.pelanggan);
            }

            input_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    if(checkedId == R.id.calonpembeli){
                        status = "Calon Pembeli";
                    } else if(checkedId == R.id.pembeli){
                        status = "Pembeli";
                    } else if(checkedId == R.id.pelanggan){
                        status = "Pelanggan";
                    }
                }
            });
        } else {
            setTitle("Tambak Kontak Baru");
            input_status.check(R.id.calonpembeli);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail_kontak, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_item_save) {
            // Do something
            // Creates a new Intent to insert a contact
            editStatus();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static boolean insertContact(ContentResolver contactAdder, String firstName, String mobileNumber) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName).build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber).withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        try {
            contactAdder.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void editStatus() {
        String nomeruser = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().replace("+","");
        mDatabase.child("kontak").child(nomeruser).child(input_nomer.getText().toString()).child("status").setValue(status);
        Toast.makeText(DetailKontak.this, "Status "+input_nama.getText()+" berhasil diubah menjadi "+status, Toast.LENGTH_SHORT).show();
    }


}
