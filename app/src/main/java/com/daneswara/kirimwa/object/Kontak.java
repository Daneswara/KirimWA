package com.daneswara.kirimwa.object;

import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Daneswara on 07/10/2017.
 */

@IgnoreExtraProperties
public class Kontak {

    public String id;
    public String nama;
    public String nomer;
    public String grup;
    public String tanggal_dibuat;

    public Kontak() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Kontak(String nomer, String nama, String id, String grup) {
        this.id = id;
        this.nomer = nomer;
        this.nama = nama;
        this.grup = grup;
        this.tanggal_dibuat = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
    }

}