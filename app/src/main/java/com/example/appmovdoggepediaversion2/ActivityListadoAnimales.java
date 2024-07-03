package com.example.appmovdoggepediaversion2;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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

public class ActivityListadoAnimales extends ActivityBasic {

    private List<AdquirirAnimales> AnimalesLista;
    private RecyclerView rcAnimales;
    private RequestQueue requestQueue;
    private Spinner spEspecies;

    private String EspecieSeleccionada;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_animales);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        AnimalesLista = new ArrayList<>();

        rcAnimales = (RecyclerView) findViewById(R.id.RecAnimalesListadoAnimales);
        rcAnimales.setHasFixedSize(true);
        rcAnimales.setLayoutManager(new LinearLayoutManager(this));

        spEspecies = findViewById(R.id.SpEspeciesListadoAnimales);

        ArrayAdapter<CharSequence> adapterEspecies = ArrayAdapter.createFromResource(this, R.array.spinnerEspeciesAnimalesOp, android.R.layout.simple_dropdown_item_1line);
        spEspecies.setAdapter(adapterEspecies);

        spEspecies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                AnimalesLista.clear();
                if (adapterView.getItemIdAtPosition(i) == 0){
                    EspecieSeleccionada = "Canino";
                } else {
                    EspecieSeleccionada = "Minino";
                }
                StringRequest stringRequest = new StringRequest(Request.Method.GET, ActivityAutentificacion.URLGeneral + "/Componentes/RecyclerAnimales.php?Especie=" + EspecieSeleccionada + "", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject Animal = array.getJSONObject(i);
                                AnimalesLista.add(new AdquirirAnimales(
                                        Animal.getInt("Edad"),
                                        Animal.getString("CodigoAnimal"),
                                        Animal.getString("Fotografia"),
                                        Animal.getString("NombreInstitucion"),
                                        Animal.getString("Nombre"),
                                        Animal.getString("RazaCanina"),
                                        Animal.getString("RazaMinina"),
                                        Animal.getString("Sexo"),
                                        Animal.getString("Tamaño")
                                ));
                            }
                            AdaptadorAnimales adapterAnimales = new AdaptadorAnimales(ActivityListadoAnimales.this, AnimalesLista);
                            rcAnimales.setAdapter(adapterAnimales);
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
}