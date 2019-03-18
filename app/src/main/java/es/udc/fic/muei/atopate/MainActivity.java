package es.udc.fic.muei.atopate;

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

        Button clickButton = (Button) findViewById(R.id.btnAjustes);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Redirección Ajustes");
                Toast.makeText(MainActivity.this, "Redirección Ajustes",
                        Toast.LENGTH_SHORT).show();
                Intent ajustesIntent = new Intent(MainActivity.this, Ajustes.class);
                startActivity(ajustesIntent);
            }
        });
    }
}
