package com.example.appmovdoggepediaversion2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class AdaptadorAnimales extends RecyclerView.Adapter<AdaptadorAnimales.AnimalViewHolder> {

    private Context mCtx;
    private List<AdquirirAnimales> AnimalesList;

    public AdaptadorAnimales(Context mCtx, List<AdquirirAnimales> animales) {
        this.mCtx = mCtx;
        this.AnimalesList = animales;
    }

    @NonNull
    @Override
    public AdaptadorAnimales.AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.custom_lista_animales,null);
        return new AnimalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorAnimales.AnimalViewHolder holder, int position) {
        AdquirirAnimales Animal = AnimalesList.get(position);

        holder.Edad.setText(Animal.getEdad()+"");
        Glide.with(mCtx)
                .load(Animal.getFotografia() + "?timestamp=" + System.currentTimeMillis())
                .into(holder.Imagen);
        holder.Institucion.setText(Animal.getInstitucion());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ActivityInformacionDelAnimal.class);
                intent.putExtra("CodigoAnimal", AnimalesList.get(position).getCodigoAnimal());
                view.getContext().startActivity(intent);

            }
        });

        holder.Nombre.setText(Animal.getNombre());
        if(Animal.getRazaCanina().equals("Desconocido")){
            holder.RazaCan.setText("");
            holder.RazaMin.setText(Animal.getRazaMinina());
        } else if(Animal.getRazaMinina().equals("Desconocido")){
            holder.RazaMin.setText("");
            holder.RazaCan.setText(Animal.getRazaCanina());
        }
        holder.Sexo.setText(Animal.getSexo());
        holder.Tamaño.setText(Animal.getTamaño());
    }

    @Override
    public int getItemCount() {
        return AnimalesList.size();
    }

    class AnimalViewHolder extends RecyclerView.ViewHolder {

        ImageView Imagen;
        LinearLayout linearLayout;
        TextView Edad, Institucion, Nombre, RazaCan, RazaMin, Sexo, Tamaño;
        public AnimalViewHolder(@NonNull View itemView) {
            super(itemView);

            Edad = itemView.findViewById(R.id.TextVValorEdadListadoAnimales);
            Imagen = itemView.findViewById(R.id.ImageVImagenListadoAnimales);
            Institucion = itemView.findViewById(R.id.TextVValorInstitucionListadoAnimales);
            linearLayout = itemView.findViewById(R.id.LinearLayout);
            Nombre = itemView.findViewById(R.id.TextVNombreListadoAnimales);
            RazaCan = itemView.findViewById(R.id.TextVValorRazaCaninaListadoAnimales); RazaMin = itemView.findViewById(R.id.TextVValorRazaMininaListadoAnimales);
            Sexo = itemView.findViewById(R.id.TextVValorSexoListadoAnimales);
            Tamaño = itemView.findViewById(R.id.TextVValorTamañoListadoAnimales);
        }
    }
}
