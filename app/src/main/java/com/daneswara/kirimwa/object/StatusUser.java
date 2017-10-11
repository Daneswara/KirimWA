package com.daneswara.kirimwa.object;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Daneswara on 07/10/2017.
 */

@IgnoreExtraProperties
public class StatusUser {

    public long fail;
    public long sent;
    public long read;
    public long engage;
    public long buyer;

    public StatusUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public StatusUser(String id_device) {
        this.fail = 0;
        this.sent = 0;
        this.read = 0;
        this.engage = 0;
        this.buyer = 0;
    }

}