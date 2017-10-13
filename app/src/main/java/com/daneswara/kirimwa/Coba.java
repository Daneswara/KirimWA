package com.daneswara.kirimwa;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class Coba extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coba);
        //insert contact
        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
        operationList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // first and last names
        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "Second Name")
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, "First Name")
                .build());

        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "09876543210")
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());
        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)

                .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, "abc@xyz.com")
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .build());

        try{
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
            System.out.println(results+"");
        }catch(Exception e){
            e.printStackTrace();
        }



//        Cursor dataCursor = getContentResolver().query(
//                ContactsContract.Data.CONTENT_URI,
//                new String[]{
//                        ContactsContract.Data.CONTACT_ID,
//                        ContactsContract.Data.DATA1,
//                },
//                ContactsContract.Data.MIMETYPE + "=?",
//                new String[]{ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE}, null
//        );
//        dataCursor.moveToFirst();
//        while (dataCursor.moveToNext()) //
//        {
//            String s0 = dataCursor.getString(0);                //contact_id
//            String s1 = dataCursor.getString(1);                //contact_name
//            Log.d("tag", "contact_id: " + s0 +"groupID: "+ s1);
//        }
//        Cursor groupCursor = getContentResolver().query(
//                ContactsContract.Groups.CONTENT_URI,
//                new String[]{
//                        ContactsContract.Groups._ID,
//                        ContactsContract.Groups.TITLE
//                }, null, null, null
//        );
//        groupCursor.moveToFirst();
//        while (groupCursor.moveToNext()) //
//        {
//            String s0 = groupCursor.getString(0);                //contact_id
//            String s1 = groupCursor.getString(1);                //contact_name
//            Log.d("tag", "groupID: " + s0 +"title: "+ s1);
//        }
//
//        Cursor user = getContentResolver().query(
//                ContactsContract.Contacts.CONTENT_URI,
//                new String[]{
//                        ContactsContract.Contacts.DISPLAY_NAME,
//                        ContactsContract.Contacts.HAS_PHONE_NUMBER,
//                        ContactsContract.Contacts._ID
//                }, null, null, null
//        );
//        user.moveToFirst();
//        while (user.moveToNext()) //
//        {
//            String s0 = user.getString(0);                //contact_id
//            String s1 = user.getString(1);                //contact_name
//            String contactId = user.getString(2);                //contact_name
//            Log.d("tag", "Name: " + s0 +"haPhone: "+ s1);
//            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{
//                            ContactsContract.CommonDataKinds.Phone._ID,
//                            ContactsContract.Groups.TITLE
//                    },
//                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
//
//
//            phones.moveToFirst();
//            while (phones.moveToNext()) //
//            {
//                String nomer = phones.getString(0);                //contact_id
//                String type = phones.getString(1);                //contact_name
//                Log.d("tag", "groupID: " + s0 +"title: "+ s1);
//            }
//
//
//            int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
//            switch (type) {
//                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//                    // do something with the Home number here...
//                    break;
//                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                    // do something with the Mobile number here...
//                    break;
//                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                    // do something with the Work number here...
//                    break;
//            }
//        }
    }
}
