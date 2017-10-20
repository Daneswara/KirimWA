package com.daneswara.kirimwa.object;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Daneswara on 07/10/2017.
 */

@IgnoreExtraProperties
public class Grup {

    public String id;
    public String title;
    public String tanggal_dibuat;

    public Grup() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Grup(String id, String title) {
        this.id = id;
        this.title = title;
        this.tanggal_dibuat = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
    }

}