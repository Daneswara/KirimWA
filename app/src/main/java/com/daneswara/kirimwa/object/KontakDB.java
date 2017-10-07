package com.daneswara.kirimwa.object;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Daneswara on 07/10/2017.
 */

@IgnoreExtraProperties
public class KontakDB {

    public String nama;
    public String nomer;
    public String status;
    public String tanggal_dibuat;

    public KontakDB() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public KontakDB(String nama, String nomer, String status, String tanggal_dibuat) {
        this.nomer = nomer;
        this.nama = nama;
        this.status = status;
        this.tanggal_dibuat = tanggal_dibuat;
    }

}