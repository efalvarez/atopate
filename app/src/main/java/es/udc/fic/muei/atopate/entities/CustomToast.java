package es.udc.fic.muei.atopate.entities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class CustomToast {
    private Context context;
    private CharSequence text;
    private int duration;

    public CustomToast(Context context, CharSequence text, int duration) {
        this.context = context;
        this.text = text;
        this.duration = duration;
    }

    public void show() {
        Toast toast = Toast.makeText(this.context, this.text, this.duration);
        View viewToast = toast.getView();
        viewToast.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        TextView textToast = viewToast.findViewById(android.R.id.message);
        textToast.setBackgroundColor(Color.TRANSPARENT);
        textToast.setTextColor(Color.WHITE);
        toast.show();
    }
}