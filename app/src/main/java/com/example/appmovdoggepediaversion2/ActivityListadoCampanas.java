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

public class ActivityListadoCampanas extends ActivityBasic {

    private List<AdquirirCampanas> CampañasLista;
    private RecyclerView rcCampañas;
    private RequestQueue requestQueue;

    private String URLCampañas = ActivityAutentificacion.URLGeneral + "/Componentes/RecyclerCampanas.php";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_campanas);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        CampañasLista = new ArrayList<>();

        rcCampañas = (RecyclerView) findViewById(R.id.RecCampañasListadoCampañas);
        rcCampañas.setHasFixedSize(true);
        rcCampañas.setLayoutManager(new LinearLayoutManager(this));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLCampañas, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject Campana = array.getJSONObject(i);
                        CampañasLista.add(new AdquirirCampanas(
                                Campana.getString("Calle"),
                                Campana.getString("Descripcion"),
                                Campana.getString("Fecha"),
                                Campana.getString("Finalidad"),
                                Campana.getString("FotoPromocional"),
                                Campana.getString("Municipio"),
                                Campana.getString("Telefono1"),
                                Campana.getString("Telefono2")
                        ));
                    }
                    AdaptadorCampañas adapterCampañas = new AdaptadorCampañas(ActivityListadoCampanas.this, CampañasLista);
                    rcCampañas.setAdapter(adapterCampañas);
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