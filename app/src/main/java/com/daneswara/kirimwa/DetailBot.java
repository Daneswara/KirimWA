package com.daneswara.kirimwa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class DetailBot extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_bot);
        Spinner spinner = findViewById(R.id.spinner);
        EditText input_tanggal = findViewById(R.id.input_tanggal);
        EditText input_jam = findViewById(R.id.input_jam);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spin_waktu, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String tipe[] = {"Calon Pembeli", "Pembeli", "Pelanggan"};
        Spinner spinner_tipe = findViewById(R.id.spinner_tipe);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipe);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner_tipe.setAdapter(spinnerArrayAdapter);
//        Calendar now = Calendar.getInstance();
//        com.wdullaer.materialdatetimepicker.date.DatePickerDialog dpd = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
//                PesanKamar.this,
//                now.get(Calendar.YEAR),
//                now.get(Calendar.MONTH),
//                now.get(Calendar.DAY_OF_MONTH)
//        );
//        dpd.setAccentColor(getResources().getColor(R.color.colorPrimary));
//        dpd.setMinDate(now);
//        dpd.setOkText("Pilih");
//        dpd.setCancelText("Batal");
//        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = String.format("%02d", dayOfMonth) + "-" + String.format("%02d", (monthOfYear + 1)) + "-" + year;
        //tanggal.setText("Tanggal: " + date);
        //tanggalpilihan = date;
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = String.format("%02d", hourOfDay)+":"+String.format("%02d", minute)+":"+String.format("%02d", second);
        //timeTextView.setText(time);
    }
}
