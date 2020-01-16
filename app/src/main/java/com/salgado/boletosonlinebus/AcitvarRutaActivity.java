package com.salgado.boletosonlinebus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class AcitvarRutaActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar myToolbar;

    private TextView txtFecha;
    private Button btnRuta1, btnRuta2, btnRuta3, btnRuta4;
    private String fechaActual, costoBoleto;
    private EditText numAutobus;
    private Boolean numUnidad = false;


    private ProgressDialog loadingBar;

    String ruta, numBus, horario1, horario2, horario3;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acitvar_ruta);

        myToolbar = findViewById(R.id.my_Toolbar);
        setSupportActionBar(myToolbar);
        //getSupportActionBar().setTitle("Rutas");

        loadingBar = new ProgressDialog(this);

        txtFecha = findViewById(R.id.fecha);
        btnRuta1 = findViewById(R.id.ruta1);
        btnRuta2 = findViewById(R.id.ruta2);
        btnRuta3 = findViewById(R.id.ruta3);
        btnRuta4 = findViewById(R.id.ruta4);
        numAutobus = findViewById(R.id.numAutobus);

        MostrarFecha();

        btnRuta1.setOnClickListener(this);
        btnRuta2.setOnClickListener(this);
        btnRuta3.setOnClickListener(this);
        btnRuta4.setOnClickListener(this);

    }

    private void MostrarFecha() {
        long date = System.currentTimeMillis();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
        fechaActual = formatoFecha.format(date);
        txtFecha.setText(fechaActual);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.ruta1:
                ruta="Riobamba-Quito";
                costoBoleto = "5";
                horario1=fechaActual+"-08:00";
                horario2=fechaActual+"-12:00";
                horario3=fechaActual+"-18:00";
                CapturarDatos();
                if(numUnidad){
                    numUnidad = false;
                    btnRuta1.setEnabled(false);
                    btnRuta1.setText("Activada");
                    btnRuta1.setBackgroundColor(Color.GREEN);
                }

                break;
            case R.id.ruta2:
                ruta="Riobamba-Guayaquil";
                costoBoleto = "7";
                horario1=fechaActual+"-08:00";
                horario2=fechaActual+"-12:00";
                horario3=fechaActual+"-18:00";
                CapturarDatos();
                if(numUnidad){
                    numUnidad = false;
                    btnRuta2.setEnabled(false);
                    btnRuta2.setText("Activada");
                    btnRuta2.setBackgroundColor(Color.GREEN);
                }
                break;
            case R.id.ruta3:
                ruta="Quito-Riobamba";
                costoBoleto = "5";
                horario1=fechaActual+"-08:00";
                horario2=fechaActual+"-12:00";
                horario3=fechaActual+"-18:00";
                CapturarDatos();
                if(numUnidad){
                    numUnidad = false;
                    btnRuta3.setEnabled(false);
                    btnRuta3.setText("Activada");
                    btnRuta3.setBackgroundColor(Color.GREEN);
                }

                break;
            case R.id.ruta4:
                ruta="Guayaquil-Riobamba";
                costoBoleto = "7";
                horario1=fechaActual+"-08:00";
                horario2=fechaActual+"-12:00";
                horario3=fechaActual+"-18:00";
                CapturarDatos();
                if(numUnidad){
                    numUnidad = false;
                    btnRuta4.setEnabled(false);
                    btnRuta4.setText("Activada");
                    btnRuta4.setBackgroundColor(Color.GREEN);
                }

                break;
            default:
                break;

        }

    }

    private void CapturarDatos() {
        numBus = numAutobus.getText().toString();
        if(TextUtils.isEmpty(numBus)){
            Toast.makeText(this, "Por favor Ingrese el Numero de la Unidad", Toast.LENGTH_LONG).show();

        }else{
            ActivarRuta(ruta, numBus);
            numUnidad = true;
        }

    }

    private void ActivarRuta(final String ruta, final String numBus) {

        loadingBar.setTitle("Activando..");
        loadingBar.setMessage("Espere mientras activamos la ruta");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.child("Rutas").child(ruta).child(horario1).exists())){

                    //Creamos la nueva Ruta
                    HashMap<String, Object> rutaDataMap = new HashMap<>();
                    rutaDataMap.put("numeroBus",numBus);
                    rutaDataMap.put("lugaresLibres","40");
                    rutaDataMap.put("lugaresOcupados","0");
                    rutaDataMap.put("valorBoleto",costoBoleto);


                    rootRef.child("Rutas").child(ruta).child(horario1).setValue(rutaDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(AcitvarRutaActivity.this, " La ruta de las 08:00 " +
                                                        "ha sido creada ",
                                                Toast.LENGTH_SHORT).show();
                                    }else {
                                        loadingBar.dismiss();
                                        Toast.makeText(AcitvarRutaActivity.this, " Error de coneccion, por favor intente mas tarde ",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }else {
                    Toast.makeText(AcitvarRutaActivity.this, " Ruta de las 8:00 ya fue creada con anterioridad ",
                            Toast.LENGTH_LONG).show();
                }

                    if (!(dataSnapshot.child("Rutas").child(ruta).child(horario2).exists())){

                    //Creamos la nueva Ruta
                    HashMap<String, Object> rutaDataMap = new HashMap<>();
                    rutaDataMap.put("numeroBus",numBus);
                    rutaDataMap.put("lugaresLibres","40");
                    rutaDataMap.put("lugaresOcupados","0");
                    rutaDataMap.put("valorBoleto",costoBoleto);


                        rootRef.child("Rutas").child(ruta).child(horario2).setValue(rutaDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(AcitvarRutaActivity.this, " La ruta de las 12:00 " +
                                                        "ha sido creada ",
                                                Toast.LENGTH_SHORT).show();
                                    }else {
                                        loadingBar.dismiss();
                                        Toast.makeText(AcitvarRutaActivity.this, " Error de coneccion, por favor intente mas tarde ",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                        Toast.makeText(AcitvarRutaActivity.this, " Ruta de las 12:00 ya fue creada con anterioridad ",
                                Toast.LENGTH_LONG).show();
                    }

                    if (!(dataSnapshot.child("Rutas").child(ruta).child(horario3).exists())){

                    //Creamos la nueva Ruta
                    HashMap<String, Object> rutaDataMap = new HashMap<>();
                    rutaDataMap.put("numeroBus",numBus);
                    rutaDataMap.put("lugaresLibres","40");
                    rutaDataMap.put("lugaresOcupados","0");
                    rutaDataMap.put("valorBoleto",costoBoleto);


                        rootRef.child("Rutas").child(ruta).child(horario3).setValue(rutaDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(AcitvarRutaActivity.this, " La ruta de las 18:00 " +
                                                        "ha sido creada ",
                                                Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();

                                    }else {
                                        loadingBar.dismiss();
                                        Toast.makeText(AcitvarRutaActivity.this, " Error de coneccion, por favor intente mas tarde ",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }else{
                        loadingBar.dismiss();
                        Toast.makeText(AcitvarRutaActivity.this, " Ruta de las 18:00 ya fue creada con anterioridad ",
                                Toast.LENGTH_LONG).show();
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
