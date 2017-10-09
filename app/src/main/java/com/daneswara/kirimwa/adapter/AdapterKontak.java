package com.daneswara.kirimwa.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.daneswara.kirimwa.DetailKontak;
import com.daneswara.kirimwa.R;
import com.daneswara.kirimwa.object.Kontak;
import com.daneswara.kirimwa.object.KontakDB;

import java.util.List;

/**
 * Created by Daneswara on 06/10/2017.
 */

public class AdapterKontak extends BaseAdapter {
    private Context mContext;
    private String[] nama;
    private int[] foto;
    // Keep all Images in array
    public String[] mThumbIds;
    ColorGenerator generator;
    List<Kontak> daftarkontak;
    // Constructor
    public AdapterKontak(Context c, List<Kontak> daftarkontak) {
        this.daftarkontak = daftarkontak;
        generator = ColorGenerator.MATERIAL;
        mContext = c;
        this.nama = new String[daftarkontak.size()];
        mThumbIds = new String[daftarkontak.size()];
        for (int i = 0; i < daftarkontak.size(); i++){
            this.nama[i] = daftarkontak.get(i).nama;
            mThumbIds[i] = this.nama[i].toUpperCase().charAt(0)+"";
        }
    }

    public int getCount() {
        return nama.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        TextView teks;
        LinearLayout lin;
        TextDrawable drawable;
        if (convertView == null) {
            imageView = new ImageView(mContext);

            teks = new TextView(mContext);
            teks.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            teks.setGravity(Gravity.CENTER_VERTICAL);
            teks.setPadding(16, 0, 0, 0);

            lin = new LinearLayout(mContext);
            lin.addView(imageView);
            lin.addView(teks);

        } else {
            lin = (LinearLayout) convertView;
            imageView = (ImageView) lin.getChildAt(0);
            teks = (TextView) lin.getChildAt(1);
        }

        drawable = TextDrawable.builder()
                .beginConfig()
                .width(80)  // width in px
                .height(80) // height in px
                .endConfig()
                .buildRound(String.valueOf(nama[position].charAt(0)), generator.getColor(nama[position].charAt(0)));
        imageView.setImageDrawable(drawable);
        teks.setText(nama[position]);
        lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detail = new Intent(mContext, DetailKontak.class);
                detail.putExtra("nama", daftarkontak.get(position).nama);
                detail.putExtra("nomer", daftarkontak.get(position).nomer);
                detail.putExtra("status", daftarkontak.get(position).status);
                mContext.startActivity(detail);
            }
        });
        lin.setOrientation(LinearLayout.HORIZONTAL);
        return lin;
    }



}
