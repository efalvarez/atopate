package es.udc.fic.muei.atopate;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import es.udc.fic.muei.atopate.adapter.ItemHistorialAdapter;
import es.udc.fic.muei.atopate.entities.itemHistorialEntity;

public class HistorialActivity extends AppCompatActivity {

    ArrayList<itemHistorialEntity> historials = new ArrayList<itemHistorialEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        ListView listV = (ListView) findViewById(R.id.historial_list);

        Drawable icono = Drawable.createFromPath("@drawable/ic_launcher_background.xml");

        //TODO enlazar a la base de datos y alimentar desde ahí en vez de toda esta cosa -->
        historials.add(new itemHistorialEntity("Hace 3 horas", "A coruña", "Madrid", "530km", icono));
        historials.add(new itemHistorialEntity("Lunes, 25/02/2019", "Av. dos Mallos", "Pza Pontevedra", "2km", icono));
        for (int i = 0; i<10; i++) {
            historials.add(new itemHistorialEntity());
        }
        //  <--------------------------------------------------------

        ItemHistorialAdapter adapter = new ItemHistorialAdapter(this, historials);

        listV.setAdapter(adapter);

        listV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                Toast.makeText(getApplicationContext(), "Apretado " + Integer.toString(position),Toast.LENGTH_SHORT).show();
            }
        });
    }

}
