package com.daneswara.kirimwa.object;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Daneswara on 07/10/2017.
 */

@IgnoreExtraProperties
public class User {

    public String name;
    public long tipe;
    public String register;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, long tipe) {
        this.name = name;
        this.tipe = tipe;
        this.register = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
    }

}