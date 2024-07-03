package com.example.appmovdoggepediaversion2;

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

public class ActivityListadoAdopcionesRevision extends ActivityBasic {

    private List<AdquirirAdopciones> AdopcionesLista;
    private RecyclerView rcAdopciones;
    private RequestQueue requestQueue;
    
    private String URLAdopciones = ActivityAutentificacion.URLGeneral + "/Componentes/RecyclerAdopciones.php?IdInstitucion=" + ActivityAutentificacion.IdInst + "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_adopciones_revision);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        AdopcionesLista = new ArrayList<>();

        rcAdopciones = (RecyclerView) findViewById(R.id.RecyclerAdopcionesRevision);
        rcAdopciones.setHasFixedSize(true);
        rcAdopciones.setLayoutManager(new LinearLayoutManager(this));

        if (ActivityAutentificacion.NivelAdmin.equals("Desarrollador")){
            URLAdopciones = ActivityAutentificacion.URLGeneral + "/Componentes/RecyclerAdopcionesDesarrollador.php";
        } else if (ActivityAutentificacion.NivelAdmin.equals("Administrativo")) {
            URLAdopciones = ActivityAutentificacion.URLGeneral + "/Componentes/RecyclerAdopcionesAdministrativo.php?IdInstitucion=" + ActivityAutentificacion.IdInst + "";
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLAdopciones, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject lugar = array.getJSONObject(i);
                        AdopcionesLista.add(new AdquirirAdopciones(
                                lugar.getInt("IdAdopcion"),
                                lugar.getString("CodigoAdopcion")
                        ));
                    }
                    AdaptadorAdopcionesRevision adapterAdopciones = new AdaptadorAdopcionesRevision(ActivityListadoAdopcionesRevision.this, AdopcionesLista);
                    rcAdopciones.setAdapter(adapterAdopciones);
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