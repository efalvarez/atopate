package es.udc.fic.muei.atopate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Ajustes extends AppCompatActivity {

    private static final String TAG = Ajustes.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        Button clickBtnExportar = (Button) findViewById(R.id.btnExportar);
        clickBtnExportar.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Exportando copia de seguridad");
                Toast.makeText(Ajustes.this, "Exportando copia de seguridad...",
                Toast.LENGTH_SHORT).show();
            }
        });

        Button clickBtnImportar = (Button) findViewById(R.id.btnImportar);
        clickBtnImportar.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Importando copia de seguridad");
                Toast.makeText(Ajustes.this, "Importando copia de seguridad...",
                        Toast.LENGTH_SHORT).show();
            }
        });

        Button clickBtnEliminar = (Button) findViewById(R.id.btnEliminar);
        clickBtnEliminar.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Eliminando registros");
                Toast.makeText(Ajustes.this, "Eliminando registros...",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}
