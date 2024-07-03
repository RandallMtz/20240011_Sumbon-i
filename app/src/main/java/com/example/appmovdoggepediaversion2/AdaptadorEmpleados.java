package com.example.appmovdoggepediaversion2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdaptadorEmpleados extends RecyclerView.Adapter<AdaptadorEmpleados.EmpleadoViewHolder> {

    private Context mCtx;
    private List<AdquirirUsuarios> EmpleadosList;

    public AdaptadorEmpleados(Context mCtx, List<AdquirirUsuarios> empleados) {
        this.mCtx = mCtx;
        this.EmpleadosList = empleados;
    }

    @NonNull
    @Override
    public EmpleadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.custom_lista_empleados,null);
        return new EmpleadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpleadoViewHolder holder, int position) {
        AdquirirUsuarios Empleado = EmpleadosList.get(position);

        holder.ApeMater.setText(Empleado.getApeMat()); holder.ApePater.setText(Empleado.getApePat());
        holder.Correo.setText(Empleado.getCorreo());
        holder.Nombre.setText(Empleado.getNombre());
        holder.Telefono.setText(Empleado.getTelefono());
        holder.Usuario.setText(Empleado.getUsuario());

    }

    @Override
    public int getItemCount() {
        return EmpleadosList.size();
    }

    class EmpleadoViewHolder extends RecyclerView.ViewHolder {

        TextView ApeMater, ApePater, Correo, Nombre, Telefono, Usuario;
        public EmpleadoViewHolder(@NonNull View itemView) {
            super(itemView);

            ApeMater = itemView.findViewById(R.id.TextVApeMaterListadoUsuarios); ApePater = itemView.findViewById(R.id.TextVApePaterListadoUsuarios);
            Correo = itemView.findViewById(R.id.TextVCorreoListadoUsuarios);
            Nombre = itemView.findViewById(R.id.TextVNombreListadoUsuarios);
            Telefono = itemView.findViewById(R.id.TextVTelefonoListadoUsuarios);
            Usuario = itemView.findViewById(R.id.TextVUsuarioListadoUsuarios);
        }
    }
}
