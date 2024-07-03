package com.example.appmovdoggepediaversion2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class FragmentListadosCliente extends Fragment {

    private AlertDialog.Builder builder;
    private AsyncHttpClient cliente;
    private Button btCerrarSesion;
    private CardView ListadoAlianzasCard, ListadoAnimalesCard, SubirSeguimientoCard;
    private RequestQueue requestQueue;
    private SearchableSpinner spAdopciones;

    private String IdUsuarioActual = "", UsuarioActual = "";

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_listados_cliente, container, false);

        builder = new AlertDialog.Builder(getContext());

        btCerrarSesion = view.findViewById(R.id.CerrarSesionAdministrativo);

        cliente = new AsyncHttpClient();

        ListadoAlianzasCard = view.findViewById(R.id.ListadoAlianzasCardGenerico);
        ListadoAnimalesCard = view.findViewById(R.id.GaleriaAnimalesCardGenerico);
        SubirSeguimientoCard = view.findViewById(R.id.SubiSeguimientoCardGenerico);

        spAdopciones = view.findViewById(R.id.SpinnerAdopcionesCliente);

        spAdopciones.setTitle("Selecciona una adopción.");
        spAdopciones.setPositiveButton("Cerrar.");

        MySingleton singleton = MySingleton.getInstance(getContext());
        requestQueue = singleton.getRequestQueue();

        MetodoRecuperarUsuarioActual(); MetodoPoblarCodigosAdopciones();

        ListadoAlianzasCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityListadoCampanas.class);
                startActivity(intent);
            }
        });

        ListadoAnimalesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityListadoAnimales.class);
                startActivity(intent);
            }
        });

        SubirSeguimientoCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivitySubirSeguimiento.class);
                intent.putExtra("CodigoAdopcion",spAdopciones.getSelectedItem().toString());
                startActivity(intent);
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

    private void MetodoPoblarCodigosAdopciones(){
        cliente.post(ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerCodigosAdopcionesCliente.php?IdUsuario=" + IdUsuarioActual + "", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){MetodoCargarCodigosAdopciones(new String((responseBody)));}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void MetodoCargarCodigosAdopciones(String respuesta){
        ArrayList<AdquirirAdopciones> lista = new ArrayList<AdquirirAdopciones>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){
                AdquirirAdopciones rz = new AdquirirAdopciones();
                rz.setCodigoAdop(jsonArreglo.getJSONObject(i).getString("CodigoAdopcion"));
                lista.add(rz);
            }
            ArrayAdapter<AdquirirAdopciones> a = new ArrayAdapter <AdquirirAdopciones> (getContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spAdopciones.setAdapter(a);
        } catch (Exception e){
            e.printStackTrace();
        }
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
        IdUsuarioActual = (preferences.getString("IdUsuario",""));
    }
}