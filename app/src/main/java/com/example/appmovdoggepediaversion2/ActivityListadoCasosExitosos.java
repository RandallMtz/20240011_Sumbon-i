package com.example.appmovdoggepediaversion2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

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

public class ActivityListadoCasosExitosos extends AppCompatActivity {

    private List<AdquirirCasosExitosos> CasosLista;
    private RecyclerView rcCasosExitosos;
    private RequestQueue requestQueue;

    private String URLCasosExitosos = ActivityAutentificacion.URLGeneral + "/Componentes/RecyclerCasosExitosos.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_casos_exitosos);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        CasosLista = new ArrayList<>();

        rcCasosExitosos = (RecyclerView) findViewById(R.id.RecCasosExitososListadoCasosExitosos);
        rcCasosExitosos.setHasFixedSize(true);
        rcCasosExitosos.setLayoutManager(new LinearLayoutManager(this));

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLCasosExitosos, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject Caso = array.getJSONObject(i);
                        CasosLista.add(new AdquirirCasosExitosos(
                                Caso.getString("ApeMat"),
                                Caso.getString("ApePat"),
                                Caso.getString("Fecha"),
                                Caso.getString("FotografiaUno"),
                                Caso.getString("FotografiaDos"),
                                Caso.getString("FotografiaTres"),
                                Caso.getString("NombreUsuario"),
                                Caso.getString("Nombre")
                        ));
                    }
                    AdaptadorCasosExitosos adapterCasos = new AdaptadorCasosExitosos(ActivityListadoCasosExitosos.this, CasosLista);
                    rcCasosExitosos.setAdapter(adapterCasos);
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