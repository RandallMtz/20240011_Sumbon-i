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

public class AdaptadorCampañas extends RecyclerView.Adapter<AdaptadorCampañas.CampañaViewHolder> {

    private Context mCtx;
    private List<AdquirirCampanas> CampañaList;

    public AdaptadorCampañas(Context mCtx, List<AdquirirCampanas> campañas) {
        this.mCtx = mCtx;
        this.CampañaList = campañas;
    }

    @NonNull
    @Override
    public CampañaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.custom_lista_campanas,null);
        return new CampañaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampañaViewHolder holder, int position) {
        AdquirirCampanas Campaña = CampañaList.get(position);

        holder.Calle.setText(Campaña.getCalle());
        holder.Descripcion.setText(Campaña.getDescripcion());
        holder.Fecha.setText(Campaña.getFechaRealizacion());
        holder.Finalidad.setText(Campaña.getFinalidad());
        Glide.with(mCtx)
                .load(Campaña.getFotoPromocional() + "?timestamp=" + System.currentTimeMillis())
                .into(holder.Imagen);
        holder.Municipio.setText(Campaña.getMunicipio());
        holder.Telefono1.setText(Campaña.getTelefono1() + " / " + Campaña.getTelefono2());

    }

    @Override
    public int getItemCount() {
        return CampañaList.size();
    }

    class CampañaViewHolder extends RecyclerView.ViewHolder {

        ImageView Imagen;
        TextView Calle, Descripcion, Fecha, Finalidad, Municipio, Telefono1;
        public CampañaViewHolder(@NonNull View itemView) {
            super(itemView);

            Calle = itemView.findViewById(R.id.TextVCalleListadoCampañas);
            Descripcion = itemView.findViewById(R.id.TextVDescripcionListadoCampañas);
            Fecha = itemView.findViewById(R.id.TextVFechaListadoCampañas);
            Finalidad = itemView.findViewById(R.id.TextVFinalidadListadoCampañas);
            Imagen = itemView.findViewById(R.id.ImageVImagenListadoCampañas);
            Municipio = itemView.findViewById(R.id.TextVMunicipioListadoCampañas);
            Telefono1 = itemView.findViewById(R.id.TextVTelefonosListadoCampañas);
        }
    }
}
