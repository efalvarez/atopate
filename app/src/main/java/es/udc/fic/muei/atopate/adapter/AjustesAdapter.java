package es.udc.fic.muei.atopate.adapter;

import android.app.Activity;
import android.widget.Toast;

import es.udc.fic.muei.atopate.R;


public class AjustesAdapter {
    public void setTema(Activity activity, String tema) {
        String temaOscuro = activity.getString(R.string.tema_oscuro);
        String temaClaro = activity.getString(R.string.tema_claro);

        if (tema.equals(temaOscuro)) {
            activity.setTheme(R.style.TemaOscuro);
        } else if (tema.equals(temaClaro)) {
            activity.setTheme(R.style.TemaClaro);
        } else {
            activity.setTheme(R.style.AppTheme);
            Toast.makeText(activity, "Hay un error al tratar de utilizar de modificar el tema", Toast.LENGTH_LONG).show();
        }
    }
}
