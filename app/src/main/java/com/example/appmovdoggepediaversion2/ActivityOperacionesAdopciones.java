package com.example.appmovdoggepediaversion2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ActivityOperacionesAdopciones extends ActivityBasic {

    private AsyncHttpClient cliente;
    private Button btBuscarAdopcion, btLimpiarCampos, btRegresarSeguimiento1, btRegresarSeguimiento2, btRegresarSeguimiento3;
    private EditText  edAdoptante, edCodigoAdopcion, edCodigoAnimal, edDireccion, edFechaAdopcion, edInstitucion, edNombreAnimal;
    private ImageView FotografiaUno, FotografiaDos, FotografiaTres;
    private RequestQueue requestQueue;
    private SearchableSpinner spBuscarAdopciones;

    private String InstitucionActual, URLAdopciones = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerCodigosAdopcionesDesarrollador.php";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operaciones_adopciones);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        cliente = new AsyncHttpClient();

        btBuscarAdopcion = findViewById(R.id.BtAdopcionesBuscarOperacionesAdopciones); btLimpiarCampos = findViewById(R.id.BtLimpiarCamposOperacionesAdopciones);
        btRegresarSeguimiento1 = findViewById(R.id.Regresar1); btRegresarSeguimiento2 = findViewById(R.id.Regresar2); btRegresarSeguimiento3 = findViewById(R.id.Regresar3);

        edAdoptante = findViewById(R.id.EditTAdoptanteOperacionesAdopciones);
        edCodigoAdopcion = findViewById(R.id.EditTCodigoAdopcionOperacionesAdopciones);
        edCodigoAnimal = findViewById(R.id.EditTCodigoAnimalOperacionesAdopciones);
        edDireccion = findViewById(R.id.EditTDireccionOperacionesAdopciones);
        edFechaAdopcion = findViewById(R.id.EditTFechaAdopcionOperacionesAdopciones);
        edInstitucion = findViewById(R.id.EditTInstitucionOperacionesAdopciones);
        edNombreAnimal = findViewById(R.id.EditTNombreAnimalOperacionesAdopciones);

        FotografiaUno = findViewById(R.id.Seguimiento1);
        FotografiaDos = findViewById(R.id.Seguimiento2);
        FotografiaTres = findViewById(R.id.Seguimiento3);

        spBuscarAdopciones = findViewById(R.id.SpinnerAdopcionesBuscar);

        spBuscarAdopciones.setTitle("Selecciona un codígo de adopción.");
        spBuscarAdopciones.setPositiveButton("Cerrar.");

        MetodoRecuperarInstitucionActual();

        if (ActivityAutentificacion.NivelAdmin.equals("Desarrollador")){
            URLAdopciones = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerCodigosAdopcionesDesarrollador.php";
        } else if (ActivityAutentificacion.NivelAdmin.equals("Administrativo") || ActivityAutentificacion.NivelAdmin.equals("Empleado")) {
            URLAdopciones = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerCodigosAdopcionesTrabajador.php?Institucion=" + InstitucionActual + "";
        }

        final ProgressDialog loading = ProgressDialog.show(this, "Cargando información...", "Espere por favor");

        MetodoPoblarCodigosAdopciones();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.dismiss();
            }
        }, 2000);

        btBuscarAdopcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoBuscarAdopciones(ActivityAutentificacion.URLGeneral + "/OperacionesAdopciones/BuscarGET.php?CodigoAdopcion=" + spBuscarAdopciones.getSelectedItem() + "");
            }
        });

        btRegresarSeguimiento1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoEliminarSeguimiento("https://randallmtzpz.000webhostapp.com/AplicacionMovilDoggepedia/OperacionesAdopciones/RechazarSeguimiento1.php");
            }
        });

        btRegresarSeguimiento2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoEliminarSeguimiento("https://randallmtzpz.000webhostapp.com/AplicacionMovilDoggepedia/OperacionesAdopciones/RechazarSeguimiento2.php");
            }
        });

        btRegresarSeguimiento3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoEliminarSeguimiento("https://randallmtzpz.000webhostapp.com/AplicacionMovilDoggepedia/OperacionesAdopciones/RechazarSeguimiento3.php");
            }
        });

        btLimpiarCampos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoLimpiarCampos();
            }
        });
    }

    private void MetodoPoblarCodigosAdopciones(){
        cliente.post(URLAdopciones, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    MetodoCargarCodigosAdopciones(new String((responseBody)));
                }
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
                AdquirirAdopciones adopcion = new AdquirirAdopciones();
                adopcion.setCodigoAdop(jsonArreglo.getJSONObject(i).getString("CodigoAdopcion"));
                lista.add(adopcion);
            }
            ArrayAdapter<AdquirirAdopciones> adopciones = new ArrayAdapter <AdquirirAdopciones> (getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spBuscarAdopciones.setAdapter(adopciones);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void MetodoBuscarAdopciones (String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        edAdoptante.setText(jsonObject.getString("NombreUsuario"));
                        edCodigoAdopcion.setText(jsonObject.getString("CodigoAdopcion"));
                        edCodigoAnimal.setText(jsonObject.getString("CodigoAnimal"));
                        edDireccion.setText(jsonObject.getString("Direccion"));
                        edFechaAdopcion.setText(jsonObject.getString("Fecha"));
                        edInstitucion.setText(jsonObject.getString("Institucion"));
                        edNombreAnimal.setText(jsonObject.getString("Nombre"));
                        Glide.with(getApplicationContext())
                                .load(jsonObject.getString("FotografiaUno") + "?timestamp=" + System.currentTimeMillis())
                                .into(FotografiaUno);
                        Glide.with(getApplicationContext())
                                .load(jsonObject.getString("FotografiaDos") + "?timestamp=" + System.currentTimeMillis())
                                .into(FotografiaDos);
                        Glide.with(getApplicationContext())
                                .load(jsonObject.getString("FotografiaTres") + "?timestamp=" + System.currentTimeMillis())
                                .into(FotografiaTres);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "¡Adopción inexistente!", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoEliminarSeguimiento(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Seguimiento rechazado!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CodigoAdop", edCodigoAdopcion.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoRecuperarInstitucionActual(){
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        InstitucionActual = (preferences.getString("IdInstitucion",""));
    }

    private void MetodoLimpiarCampos (){
        edAdoptante.setText("");
        edCodigoAdopcion.setText("");
        edCodigoAnimal.setText("");
        edDireccion.setText("");
        edFechaAdopcion.setText("");
        edInstitucion.setText("");
        edNombreAnimal.setText("");
    }
}