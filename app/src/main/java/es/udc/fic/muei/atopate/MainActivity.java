package es.udc.fic.muei.atopate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import es.udc.fic.muei.atopate.trayecto.TrayectoActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClickMockButton(View view) {
        Intent actividadTrayecto = new Intent(this, TrayectoActivity.class);

        startActivity(actividadTrayecto);
    }


}
