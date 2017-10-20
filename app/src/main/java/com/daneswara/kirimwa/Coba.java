package com.daneswara.kirimwa;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class Coba extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coba);
        //insert contact
//        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
//        operationList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
//                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
//                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
//                .build());
//
//        // first and last names
//        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, "Second Name")
//                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, "First Name")
//                .build());
//
//        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "09876543210")
//                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
//                .build());
//        operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//
//                .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
//                .withValue(ContactsContract.CommonDataKinds.Email.DATA, "abc@xyz.com")
//                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
//                .build());
//
//        try{
//            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
//            System.out.println(results+"");
//        }catch(Exception e){
//            e.printStackTrace();
//        }



        Cursor dataCursor = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI,
                new String[]{
                        ContactsContract.Data.CONTACT_ID,
                        ContactsContract.Data.DATA1,
                },
                ContactsContract.Data.MIMETYPE + "=?",
                new String[]{ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE}, null
        );
        dataCursor.moveToFirst();
        while (dataCursor.moveToNext()) //
        {
            String s0 = dataCursor.getString(0);                //contact_id
            String s1 = dataCursor.getString(1);                //contact_name
            Log.d("tag", "contact_id: " + s0 +"groupID: "+ s1);
        }
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
//            Log.d("tag", "Name: " + s0 +" Phone: "+ s1);
//            s0 = s0.replace("'","\'");
//            ContentResolver cr = getContentResolver();
//            Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
//                    "DISPLAY_NAME = '" + s0 + "'", null, null);
//            if (cursor.moveToFirst()) {
//                String contactId =
//                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
//                //
//                //  Get all phone numbers.
//                //
//                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
//                while (phones.moveToNext()) {
//                    String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    Log.d("tag", "Number: " + number);
//                    int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
//                    switch (type) {
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
//                            // do something with the Home number here...
//                            break;
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
//                            // do something with the Mobile number here...
//                            break;
//                        case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
//                            // do something with the Work number here...
//                            break;
//                    }
//                }
//                phones.close();
//            }
//            cursor.close();
//        }

        // get name and number
//        ContentResolver cr = getContentResolver();
//        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
//                null, null, null);
//        if (cur.getCount() > 0) {
//            while (cur.moveToNext()) {
//                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
//                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                Log.d("Names", name);
//                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
//                {
//                    // Query phone here. Covered next
//                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
//                    while (phones.moveToNext()) {
//                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        Log.d("Number", phoneNumber);
//                    }
//                    phones.close();
//                }
//
//            }
//        }

//        String contactid26 = null;
//
//        ContentResolver contentResolver = getContentResolver();
//
//        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode("085648067872"));
//
//        Cursor cursor =
//                contentResolver.query(
//                        uri,
//                        new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID},
//                        null,
//                        null,
//                        null);
//
//        if(cursor!=null) {
//            while(cursor.moveToNext()){
//                String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
//                contactid26 = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
//
//            }
//            cursor.close();
//        }
//        if (contactid26 == null)
//        {
//            Log.d("tag", "No contact found associated with this number");
////            Toast.makeText(Coba.this, "No contact found associated with this number", Toast.LENGTH_SHORT).show();
//        }
//        else
//        {
////            Log.d("tag", "No contact found associated with this number");
//            Intent intent_contacts = new Intent(Intent.ACTION_VIEW, Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactid26)));
//            //Intent intent_contacts = new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/" + contactid26));
//            startActivity(intent_contacts);
//        }
        getGroupIdFor("61425");
        getGroupIdFor("216");
    }

    public void getGroupIdFor(String contactId){
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String where = String.format(
                "%s = ? AND %s = ?",
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID);

        String[] whereParams = new String[] {
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
        try{
            if(groupIdCursor.moveToFirst()){
                System.out.println(groupIdCursor.getString(groupIdCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID)) + ",");
                while (groupIdCursor.moveToNext()) {
                    System.out.println(groupIdCursor.getString(groupIdCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID)) + ",");
                }
            }
        }finally{
            groupIdCursor.close();
        }
    }
}
