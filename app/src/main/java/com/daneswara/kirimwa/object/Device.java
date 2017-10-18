package com.daneswara.kirimwa.object;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Daneswara on 07/10/2017.
 */

@IgnoreExtraProperties
public class Device {

    public String token;
    public String id_device;
    public String tambah;

    public Device() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Device(String token, String id_device) {
        this.token = token;
        this.id_device = id_device;
        this.tambah = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
    }

}