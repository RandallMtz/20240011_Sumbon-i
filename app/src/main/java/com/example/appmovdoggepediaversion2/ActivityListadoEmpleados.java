package com.example.appmovdoggepediaversion2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import kotlin.collections.IntIterator;

public class ActivityListadoEmpleados extends ActivityBasic {

    private AsyncHttpClient cliente;
    private List<AdquirirUsuarios> EmpleadosLista;
    private RecyclerView rcEmpleados;
    private RequestQueue requestQueue;
    private SearchableSpinner spInstituciones;

    private String Institucion = "Sin pertenencia", InstitucionActual, URLEmpleados;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_empleados);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        cliente = new AsyncHttpClient();

        EmpleadosLista = new ArrayList<>();

        rcEmpleados = (RecyclerView) findViewById(R.id.RecEmpleadosListadoEmpleados);
        rcEmpleados.setHasFixedSize(true);
        rcEmpleados.setLayoutManager(new LinearLayoutManager(this));

        spInstituciones = findViewById(R.id.SpInstitucionListadoEmpleados);

        spInstituciones.setTitle("Selecciona una institución.");
        spInstituciones.setPositiveButton("Cerrar.");

        MetodoRecuperarInstitucionActual();

        if (ActivityAutentificacion.NivelAdmin.equals("Desarrollador")){
            spInstituciones.setEnabled(true);
            spInstituciones.setVisibility(View.VISIBLE);
        } else if (ActivityAutentificacion.NivelAdmin.equals("Administrativo") || ActivityAutentificacion.NivelAdmin.equals("Empleado")) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            layoutParams.width = 0;
            layoutParams.height = 0;

            spInstituciones.setLayoutParams(layoutParams);
            spInstituciones.setEnabled(false);
            spInstituciones.setVisibility(View.INVISIBLE);
        }

        MetodoPoblarInstituciones();

        spInstituciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                EmpleadosLista.clear();
                Institucion = adapterView.getSelectedItem().toString();
                if (ActivityAutentificacion.NivelAdmin.equals("Desarrollador")){
                    URLEmpleados = ActivityAutentificacion.URLGeneral + "/Componentes/RecyclerUsuariosDesarrollador.php?Institucion=" + MetodoCodificarTexto(Institucion) + "";
                } else if (ActivityAutentificacion.NivelAdmin.equals("Administrativo") || ActivityAutentificacion.NivelAdmin.equals("Empleado")) {
                    URLEmpleados = ActivityAutentificacion.URLGeneral + "/Componentes/RecyclerUsuariosTrabajador.php?Institucion=" + InstitucionActual + "";
                }
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URLEmpleados, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject usuario = array.getJSONObject(i);
                                EmpleadosLista.add(new AdquirirUsuarios(
                                        usuario.getString("ApeMat"),
                                        usuario.getString("ApePat"),
                                        usuario.getString("Correo"),
                                        usuario.getString("Nombre"),
                                        usuario.getString("Telefono1"),
                                        usuario.getString("Usuario")
                                ));
                            }
                            AdaptadorEmpleados adapterEmpleados = new AdaptadorEmpleados(ActivityListadoEmpleados.this, EmpleadosLista);
                            rcEmpleados.setAdapter(adapterEmpleados);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                });
                requestQueue.add(stringRequest);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void MetodoPoblarInstituciones(){
        cliente.post(ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerInstituciones.php", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){MetodoCargarInstituciones(new String((responseBody)));}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void MetodoCargarInstituciones(String respuesta){
        ArrayList<AdquirirInstituciones> lista = new ArrayList<AdquirirInstituciones>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){
                AdquirirInstituciones rz = new AdquirirInstituciones();
                rz.setNombre(jsonArreglo.getJSONObject(i).getString("Nombre"));
                lista.add(rz);
            }
            ArrayAdapter<AdquirirInstituciones> a = new ArrayAdapter <AdquirirInstituciones> (getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spInstituciones.setAdapter(a);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void MetodoRecuperarInstitucionActual(){
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        InstitucionActual = (preferences.getString("IdInstitucion",""));
    }
}