package com.daneswara.kirimwa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class DetailBot extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    EditText input_tanggal, input_jam;
    Boolean cek = true, cek_time = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_bot);
        Spinner spinner = findViewById(R.id.spinner);
        input_tanggal = findViewById(R.id.input_tanggal);
        input_jam = findViewById(R.id.input_jam);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spin_waktu, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String tipe[] = {"Calon Pembeli", "Pembeli", "Pelanggan"};
        Spinner spinner_tipe = findViewById(R.id.spinner_tipe);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipe);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner_tipe.setAdapter(spinnerArrayAdapter);

        input_tanggal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (cek) {
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            DetailBot.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                    dpd.setMinDate(now);
                    dpd.setOkText("Pilih");
                    dpd.setCancelText("Batal");
                    dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                    cek = false;
                } else {
                    cek = true;
                }
            }
        });
        input_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        DetailBot.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                dpd.setMinDate(now);
                dpd.setOkText("Pilih");
                dpd.setCancelText("Batal");
                dpd.setVersion(DatePickerDialog.Version.VERSION_2);
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        input_jam.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (cek_time) {
                    Calendar now = Calendar.getInstance();
                    TimePickerDialog tpd = TimePickerDialog.newInstance(
                            DetailBot.this,
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            true
                    );
                    tpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                    if(input_tanggal.getText().toString().equalsIgnoreCase(String.format("%02d", now.getTime().getDay()) + "-" + String.format("%02d", (now.getTime().getMonth() + 1)) + "-" + now.getTime().getHours())){
                        tpd.setMinTime(now.getTime().getHours(), now.getTime().getMinutes(), now.getTime().getSeconds());
                    }
                    tpd.enableSeconds(true);
                    tpd.setOkText("Pilih");
                    tpd.setCancelText("Batal");
                    tpd.setVersion(TimePickerDialog.Version.VERSION_2);
                    tpd.show(getFragmentManager(), "Timepickerdialog");
                    cek_time = false;
                } else {
                    cek_time = true;
                }
            }
        });

        input_jam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        DetailBot.this,
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                );
                tpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
                tpd.setMinTime(now.getTime().getHours(), now.getTime().getMinutes(), now.getTime().getSeconds());
                tpd.enableSeconds(true);
                tpd.setOkText("Pilih");
                tpd.setCancelText("Batal");
                tpd.setVersion(TimePickerDialog.Version.VERSION_2);
                tpd.show(getFragmentManager(), "Timepickerdialog");
            }
        });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = String.format("%02d", dayOfMonth) + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + year;
        input_tanggal.setText(date);
        //tanggalpilihan = date;
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = String.format("%02d", hourOfDay) + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second);
        input_jam.setText(time);
        //timeTextView.setText(time);
    }
}
