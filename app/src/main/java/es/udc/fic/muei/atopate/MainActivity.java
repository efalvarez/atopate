package es.udc.fic.muei.atopate;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button clickButton = (Button) findViewById(R.id.button);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Going to Estadisticas");
                Toast.makeText(MainActivity.this, "Going to Estadisticas",
                        Toast.LENGTH_SHORT).show();
                Intent estadisticasIntent = new Intent(MainActivity.this, Estadisticas.class);
                startActivity(estadisticasIntent);
            }
        });

    }
}
