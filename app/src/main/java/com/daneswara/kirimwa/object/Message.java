package com.daneswara.kirimwa.object;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Daneswara on 07/10/2017.
 */

@IgnoreExtraProperties
public class Message {

    public String id;
    public String number;
    public String message;
    public String time;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Message(String id, String number, String message) {
        this.id = id;
        this.number = number;
        this.message = message;
        this.time = Calendar.getInstance().getTimeInMillis()+"";
    }

}