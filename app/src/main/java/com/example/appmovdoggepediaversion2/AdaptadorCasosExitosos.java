package com.example.appmovdoggepediaversion2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AdaptadorCasosExitosos extends RecyclerView.Adapter<AdaptadorCasosExitosos.CasoExitosoViewHolder> {

    private Context mCtx;
    private List<AdquirirCasosExitosos> CasosList;

    public AdaptadorCasosExitosos(Context mCtx, List<AdquirirCasosExitosos> Casos) {
        this.mCtx = mCtx;
        this.CasosList = Casos;
    }

    @NonNull
    @Override
    public CasoExitosoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.custom_lista_casos_exitosos,null);
        return new CasoExitosoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CasoExitosoViewHolder holder, int position) {
        AdquirirCasosExitosos Caso = CasosList.get(position);

        holder.Adoptante.setText(Caso.getNombre() + " " + Caso.getApePat() + " " + Caso.getApeMat());
        holder.Adoptado.setText(Caso.getNombreAnimal());
        holder.Fecha.setText(Caso.getFecha());
        Glide.with(mCtx)
                .load(Caso.getFotografiaUno() + "?timestamp=" + System.currentTimeMillis())
                .into(holder.Seg1);
        Glide.with(mCtx)
                .load(Caso.getFotografiaDos() + "?timestamp=" + System.currentTimeMillis())
                .into(holder.Seg2);
        Glide.with(mCtx)
                .load(Caso.getFotografiaTres() + "?timestamp=" + System.currentTimeMillis())
                .into(holder.Seg3);

    }

    @Override
    public int getItemCount() {
        return CasosList.size();
    }

    class CasoExitosoViewHolder extends RecyclerView.ViewHolder {

        ImageView Seg1, Seg2, Seg3;
        TextView Adoptante, Adoptado, Fecha;
        public CasoExitosoViewHolder(@NonNull View itemView) {
            super(itemView);

            Adoptante = itemView.findViewById(R.id.TextVAdoptanteListadoCasosExitosos);
            Adoptado = itemView.findViewById(R.id.TextVAdoptadoListadoCasosExitosos);
            Fecha = itemView.findViewById(R.id.TextVFechaListadoCasosExitosos);
            Seg1 = itemView.findViewById(R.id.ImageVCasosExitosoSeg1);
            Seg2 = itemView.findViewById(R.id.ImageVCasosExitosoSeg2);
            Seg3 = itemView.findViewById(R.id.ImageVCasosExitosoSeg3);
        }
    }
}
