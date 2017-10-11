package com.daneswara.kirimwa.object;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Daneswara on 07/10/2017.
 */

@IgnoreExtraProperties
public class Kontak {

    public String nama;
    public String nomer;
    public String tanggal_dibuat;

    public Kontak() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Kontak(String nomer, String nama) {
        this.nomer = nomer;
        this.nama = nama;
        this.tanggal_dibuat = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
    }

}