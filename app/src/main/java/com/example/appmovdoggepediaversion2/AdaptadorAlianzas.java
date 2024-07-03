package com.example.appmovdoggepediaversion2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdaptadorAlianzas extends RecyclerView.Adapter<AdaptadorAlianzas.AlianzaViewHolder> {

    private Context mCtx;
    private List<AdquirirAlianzas> AlianzaList;

    public AdaptadorAlianzas(Context mCtx, List<AdquirirAlianzas> alianzas) {
        this.mCtx = mCtx;
        this.AlianzaList = alianzas;
    }

    @NonNull
    @Override
    public AlianzaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.custom_lista_alianzas,null);
        return new AlianzaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlianzaViewHolder holder, int position) {
        AdquirirAlianzas Alianza = AlianzaList.get(position);

        holder.Calle.setText(Alianza.getCalle());
        holder.Colonia.setText(Alianza.getColonia());
        holder.Empresa.setText(Alianza.getEmpresa());
        holder.Municipio.setText(Alianza.getMunicipio());
        holder.Telefono1.setText(Alianza.getTelefono1()); holder.Telefono2.setText(Alianza.getTelefono2());

    }

    @Override
    public int getItemCount() {
        return AlianzaList.size();
    }

    class AlianzaViewHolder extends RecyclerView.ViewHolder {

        TextView Calle, Colonia, Empresa, Municipio, Telefono1, Telefono2;
        public AlianzaViewHolder(@NonNull View itemView) {
            super(itemView);

            Calle = itemView.findViewById(R.id.TextVDireccionListadoAlianzas);
            Colonia = itemView.findViewById(R.id.TextVColoniaListadoAlianzas);
            Empresa = itemView.findViewById(R.id.TextVEmpresaListadoAlianzas);
            Municipio = itemView.findViewById(R.id.TextVMunicipioListadoAlianzas);
            Telefono1 = itemView.findViewById(R.id.TextVTelefono1ListadoAlianzas); Telefono2 = itemView.findViewById(R.id.TextVTelefono2ListadoAlianzas);
        }
    }
}
