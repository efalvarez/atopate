package es.udc.fic.muei.atopate.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import es.udc.fic.muei.atopate.entities.itemHistorialEntity;


public class ItemHistorialAdapter extends RecyclerView.Adapter<ItemHistorialHolder>  {

    private int itemResource;
    private List<itemHistorialEntity> items;
    private Context context;
    private Activity activity;

    public ItemHistorialAdapter(Context context, int itemResource, List<itemHistorialEntity> items, Activity activity) {
        this.itemResource = itemResource;
        this.items = items;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ItemHistorialHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);

        return new ItemHistorialHolder(this.context, view, activity, items);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHistorialHolder holder, int i) {
        itemHistorialEntity item = this.items.get(i);

        holder.bindItem(item);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
}