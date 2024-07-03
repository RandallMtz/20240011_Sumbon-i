package com.example.appmovdoggepediaversion2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FragmentGestionAdministradores extends Fragment {

    private AlertDialog.Builder builder;
    private Button btCerrarSesion;
    private CardView ListadoUsuariosCard, OperacionesAnimalesCard, OperacionesUsuariosCard;
    private RequestQueue requestQueue;

    private String UsuarioActual = "";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_administradores, container, false);

        builder = new AlertDialog.Builder(getContext());

        btCerrarSesion = view.findViewById(R.id.CerrarSesionAdministrativo);

        ListadoUsuariosCard = view.findViewById(R.id.ListadoUsuariosCard);
        OperacionesAnimalesCard = view.findViewById(R.id.OperacionesAnimalesCard);
        OperacionesUsuariosCard = view.findViewById(R.id.OperacionesUsuariosCard);

        MySingleton singleton = MySingleton.getInstance(getContext());
        requestQueue = singleton.getRequestQueue();

        MetodoRecuperarUsuarioActual();

        if (ActivityAutentificacion.NivelAdmin.equals("Administrativo") || ActivityAutentificacion.NivelAdmin.equals("Desarrollador")){
            OperacionesUsuariosCard.setEnabled(true);
        } else {
            OperacionesUsuariosCard.setEnabled(false);
            OperacionesUsuariosCard.setCardBackgroundColor(Color.rgb(220, 220, 220));
        }

        ListadoUsuariosCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityListadoEmpleados.class);
                startActivity(intent);
            }
        });

        OperacionesAnimalesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityOperacionesAnimales.class);
                startActivity(intent);
            }
        });

        OperacionesUsuariosCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityAutentificacion.NivelAdmin.equals("Desarrollador")){
                    Intent intent = new Intent(getContext(), ActivityOperacionesUsuarios.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), ActivityOperacionesUsuarios.class);
                    startActivity(intent);
                }
            }
        });

        btCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setMessage("¿Deseas cerrar sesión?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MetodoCerrarSesion(ActivityAutentificacion.URLGeneral + "/CerrarSesionPOST.php");
                                MetodoGuardarUsuarioYContraseña();
                                Intent intent = new Intent(getContext(), ActivityAutentificacion.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        });
        return view;
    }

    private void MetodoCerrarSesion(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                } catch (Exception e) {
                    Toast.makeText(getContext(), "" + response.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("Usuario", UsuarioActual);
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void MetodoGuardarUsuarioYContraseña(){
        SharedPreferences preferences = requireContext().getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Usuario", "");
        editor.putString("Contrasena", "");
        editor.putString("IdNivel", "");
        editor.putBoolean("sesion", true);
        editor.commit();
    }

    private void MetodoRecuperarUsuarioActual(){
        SharedPreferences preferences = requireContext().getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        UsuarioActual = (preferences.getString("Usuario",""));
    }
}