package com.example.appmovdoggepediaversion2;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityListadoAlianzas extends ActivityBasic {

    private List<AdquirirAlianzas> AlianzasLista;
    private RecyclerView rcAlianzas;
    private RequestQueue requestQueue;

    private String URLAlianzas = ActivityAutentificacion.URLGeneral + "/Componentes/RecyclerAlianzas.php";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_alianzas);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        AlianzasLista = new ArrayList<>();

        rcAlianzas = (RecyclerView) findViewById(R.id.RecAlianzasListadoAlianzas);
        rcAlianzas.setHasFixedSize(true);
        rcAlianzas.setLayoutManager(new LinearLayoutManager(this));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLAlianzas, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject Alianza = array.getJSONObject(i);
                        AlianzasLista.add(new AdquirirAlianzas(
                                Alianza.getString("Calle"),
                                Alianza.getString("Colonia"),
                                Alianza.getString("Empresa"),
                                Alianza.getString("Municipio"),
                                Alianza.getString("Telefono1"),
                                Alianza.getString("Telefono2")
                        ));
                    }
                    AdaptadorAlianzas adaptadorAlianzas = new AdaptadorAlianzas(ActivityListadoAlianzas.this, AlianzasLista);
                    rcAlianzas.setAdapter(adaptadorAlianzas);
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
}