package com.example.appmovdoggepediaversion2;

import androidx.annotation.Nullable;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
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
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ActivityOperacionesAnimales extends ActivityBasic {

    Bitmap bitmap;
    private AsyncHttpClient cliente;
    private Button btBuscarAnimal, btElegirFotografia, btEliminarAnimal,btInsertarAnimal, btLimpiarCampos, btModificarAnimal;
    private ImageView imFotografia;
    private EditText edCodigo, edEdad, edNombre, edTamaño;
    private RequestQueue requestQueue;
    private SearchableSpinner spCodigosAnimales;
    private Spinner spCondiciones, spEspecies, spInstituciones, spRazasCaninas, spRazasMininas, spSexos;

    private int PICK_IMAGE_REQUEST = 1;
    private String CodigoAnimalAyuda, CorroborarSeleccionImagen = "Hola", Especie, IdCondicion, IdRazaCanina, IdRazaMinina, InstitucionActual, InstitucionBusqueda, Sexo, URLCodigosAnimales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operaciones_animales);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        cliente = new AsyncHttpClient();

        btBuscarAnimal = findViewById(R.id.BtBuscarAnimalOperacionesAnimales); btElegirFotografia = findViewById(R.id.BtSeleccionarImagenOperacionesAnimales);
        btEliminarAnimal = findViewById(R.id.BtEliminarOperacionesAnimales); btInsertarAnimal = findViewById(R.id.BtInsertarOperacionesAnimales);
        btLimpiarCampos = findViewById(R.id.BtLimpiarCamposOperacionesAnimales); btModificarAnimal = findViewById(R.id.BtModificarOperacionesAnimales);

        edCodigo = findViewById(R.id.EditTCodigoAnimalOperacionesAnimales);
        edEdad = findViewById(R.id.EditTEdadOperacionesAnimales);
        edNombre = findViewById(R.id.EditTNombreOperacionesAnimales);
        edTamaño = findViewById(R.id.EditTTamañoOperacionesAnimales);

        imFotografia = findViewById(R.id.ImgVFotografiaOperacionesAnimales);

        spCodigosAnimales = findViewById(R.id.SpinnerAnimalesBuscar);
        spCondiciones = findViewById(R.id.SpCondicionOperacionesAnimales);
        spEspecies = findViewById(R.id.SpEspecieOperacionesAnimales);
        spInstituciones = findViewById(R.id.SpInstitucionOperacionesAnimales);
        spRazasCaninas = findViewById(R.id.SpRazaCaninaOperacionesAnimales); spRazasMininas = findViewById(R.id.SpRazaMininaOperacionesAnimales);
        spSexos = findViewById(R.id.SpSexoOperacionesAnimales);

        spCodigosAnimales.setTitle("Selecciona un codígo de animal.");
        spCodigosAnimales.setPositiveButton("Cerrar.");

        ArrayAdapter<CharSequence> adapterEspecies = ArrayAdapter.createFromResource(this, R.array.spinnerEspeciesAnimalesOp, android.R.layout.simple_dropdown_item_1line);
        spEspecies.setAdapter(adapterEspecies);

        ArrayAdapter<CharSequence> adapterSexos = ArrayAdapter.createFromResource(this, R.array.spinnerSexosAnimalesOp, android.R.layout.simple_dropdown_item_1line);
        spSexos.setAdapter(adapterSexos);

        MetodoRecuperarInstitucionActual();

        if (ActivityAutentificacion.NivelAdmin.equals("Desarrollador")){
            spInstituciones.setEnabled(true);
            spInstituciones.setVisibility(View.VISIBLE);
            URLCodigosAnimales = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerCodigosAnimalesDesarrollador.php";
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
            URLCodigosAnimales = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerCodigosAnimalesAdministrativo.php?IdInstitucion=" + InstitucionActual + "";
        }

        final ProgressDialog loading = ProgressDialog.show(this, "Cargando información...", "Espere por favor");

        MetodoPoblarInstituciones(); MetodoPoblarCondiciones(); MetodoPoblarRazasMininas(); MetodoPoblarRazasCaninas(); MetodoPoblarCodigosAnimales();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.dismiss();
            }
        }, 2000);

        btBuscarAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoBuscarAnimal(ActivityAutentificacion.URLGeneral + "/OperacionesAnimales/GlobalBuscarGET.php?CodigoAnimal=" + spCodigosAnimales.getSelectedItem() + "");
            }
        });

        btLimpiarCampos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoLimpiarCampos();
            }
        });

        btElegirFotografia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        spEspecies.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getItemIdAtPosition(i) == 0){
                    spRazasCaninas.setEnabled(true);
                    spRazasMininas.setSelection(0);
                    spRazasMininas.setEnabled(false);
                } else {
                    spRazasCaninas.setSelection(0);
                    spRazasCaninas.setEnabled(false);
                    spRazasMininas.setEnabled(true);
                }
                Especie = adapterView.getItemAtPosition(i)+"";
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spRazasCaninas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                IdRazaCanina = adapterView.getItemIdAtPosition(i+1)+"";
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spRazasMininas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                IdRazaMinina = adapterView.getItemIdAtPosition(i+1)+"";
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spCondiciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                IdCondicion = adapterView.getItemIdAtPosition(i+1)+"";
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spSexos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Sexo = adapterView.getItemAtPosition(i)+"";
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        btInsertarAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoValidarAnimalInexistenteInsertar(ActivityAutentificacion.URLGeneral + "/OperacionesAnimales/GlobalValidarAnimalInexistente.php?CodigoAnimal="+edCodigo.getText()+"");
            }
        });

        btModificarAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edCodigo.getText().toString().isEmpty() && !edNombre.getText().toString().isEmpty() && !edTamaño.getText().toString().isEmpty() && !edEdad.getText().toString().isEmpty()) {
                    MetodoValidarAnimalInexistenteModificar(ActivityAutentificacion.URLGeneral + "/OperacionesAnimales/GlobalValidarAnimalInexistenteModificar.php?CodigoAnimal=" + edCodigo.getText().toString() + "&CodigoAnimal1=" + CodigoAnimalAyuda + "");
                } else if (edCodigo.getText().toString().isEmpty()){
                    edCodigo.setError("¡Define un código de animal!");
                } else {
                    Toast.makeText(getApplicationContext(), "¡Faltan campos por llenar!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btEliminarAnimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edCodigo.getText().toString().isEmpty()) {
                    MetodoEliminarAnimal(ActivityAutentificacion.URLGeneral + "/OperacionesAnimales/GlobalEliminarPOST.php");
                } else {
                    edCodigo.setError("¡Define un código de animal!");
                }
            }
        });
    }

    private void MetodoPoblarCodigosAnimales(){
        cliente.post(URLCodigosAnimales, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){MetodoCargarCodigosAnimales(new String((responseBody)));}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void MetodoCargarCodigosAnimales(String respuesta){
        ArrayList<AdquirirAnimales> lista = new ArrayList<AdquirirAnimales>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){
                AdquirirAnimales rz = new AdquirirAnimales();
                rz.setCodigoAnimal(jsonArreglo.getJSONObject(i).getString("CodigoAnimal"));
                lista.add(rz);
            }
            ArrayAdapter<AdquirirAnimales> a = new ArrayAdapter <AdquirirAnimales> (getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spCodigosAnimales.setAdapter(a);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void MetodoPoblarRazasCaninas(){
        String url = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerRazasCan.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){MetodoCargarRazasCaninas(new String((responseBody)));}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void MetodoCargarRazasCaninas(String respuesta){
        ArrayList<AdquirirRazasCaninas> lista = new ArrayList<AdquirirRazasCaninas>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){
                AdquirirRazasCaninas rz = new AdquirirRazasCaninas();
                rz.setRaza(jsonArreglo.getJSONObject(i).getString("Raza"));
                lista.add(rz);
            }
            ArrayAdapter<AdquirirRazasCaninas> a = new ArrayAdapter <AdquirirRazasCaninas> (getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spRazasCaninas.setAdapter(a);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void MetodoPoblarRazasMininas(){
        String url = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerRazasMin.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){MetodoCargarRazasMininas(new String((responseBody)));}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void MetodoCargarRazasMininas(String respuesta){
        ArrayList<AdquirirRazasMininas> lista = new ArrayList<AdquirirRazasMininas>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){
                AdquirirRazasMininas rz = new AdquirirRazasMininas();
                rz.setRaza(jsonArreglo.getJSONObject(i).getString("Raza"));
                lista.add(rz);
            }
            ArrayAdapter<AdquirirRazasMininas> a = new ArrayAdapter <AdquirirRazasMininas> (getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spRazasMininas.setAdapter(a);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void MetodoPoblarCondiciones(){
        String url = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerCondiciones.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){MetodoCargarCondiciones(new String((responseBody)));}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void MetodoCargarCondiciones(String respuesta){
        ArrayList<AdquirirCondiciones> lista = new ArrayList<AdquirirCondiciones>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){
                AdquirirCondiciones rz = new AdquirirCondiciones();
                rz.setCondicion(jsonArreglo.getJSONObject(i).getString("Condicion"));
                lista.add(rz);
            }
            ArrayAdapter<AdquirirCondiciones> a = new ArrayAdapter <AdquirirCondiciones> (getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spCondiciones.setAdapter(a);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void MetodoPoblarInstituciones(){
        String url = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerInstituciones.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
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

    private void MetodoBuscarAnimal (String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        CorroborarSeleccionImagen = "";
                        edCodigo.setText(jsonObject.getString("CodigoAnimal"));
                        edNombre.setText(jsonObject.getString("Nombre"));
                        if (jsonObject.getString("Especie").equals("Canino")){
                            spEspecies.setSelection(0);
                            spRazasCaninas.setSelection(Integer.parseInt(jsonObject.getString("IdRazaCanina"))-1);
                        } else if (jsonObject.getString("Especie").equals("Minino")) {
                            spEspecies.setSelection(1);
                            spRazasMininas.setSelection(Integer.parseInt(jsonObject.getString("IdRazaMinina"))-1);
                        }
                        edTamaño.setText(jsonObject.getString("Tamaño"));
                        spCondiciones.setSelection(Integer.parseInt(jsonObject.getString("IdCondicion"))-1);
                        if (jsonObject.getString("Sexo").equals("Hembra")){
                            spSexos.setSelection(0);
                        } else if (jsonObject.getString("Sexo").equals("Macho")) {
                            spSexos.setSelection(1);
                        }
                        edEdad.setText(jsonObject.getString("Edad"));
                        CodigoAnimalAyuda = spCodigosAnimales.getSelectedItem().toString();
                        InstitucionBusqueda = jsonObject.getString("NombreInstitucion");
                        Glide.with(getApplicationContext())
                                .load(jsonObject.getString("Fotografia") + "?timestamp=" + System.currentTimeMillis())
                                .into(imFotografia);
                        spCodigosAnimales.setSelection(0);

                        JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(ActivityAutentificacion.URLGeneral + "/OperacionesAnimales/GlobalIdsTemporalesInstituciones.php", new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                JSONObject jsonObject2 = null;
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        jsonObject2 = response.getJSONObject(i);
                                        if (jsonObject2.getString("Nombre").equals(InstitucionBusqueda)){
                                            int Valor = Integer.parseInt(jsonObject2.getString("id_temporal"))-1;
                                            Handler handler1 = new Handler(Looper.getMainLooper());
                                            handler1.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    spInstituciones.setSelection(Valor);
                                                }
                                            }, 200);
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.getMessage() +"", Toast.LENGTH_SHORT).show();
                            }
                        });
                        requestQueue.add(jsonArrayRequest2);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Animal inexistente", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoValidarAnimalInexistenteInsertar (String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        edCodigo.setError("¡Intenta con un codigo distinto!");
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (ActivityAutentificacion.NivelAdmin.equals("Desarrollador")){
                    if (edCodigo.getText().toString().isEmpty() || edNombre.getText().toString().isEmpty() || edTamaño.getText().toString().isEmpty() || edEdad.getText().toString().isEmpty() || !CorroborarSeleccionImagen.equals("Hola")){
                        Toast.makeText(getApplicationContext(), "¡Faltan campos por llenar!", Toast.LENGTH_SHORT).show();
                    } else if (!edCodigo.getText().toString().isEmpty() && !edNombre.getText().toString().isEmpty() && !edTamaño.getText().toString().isEmpty() && !edEdad.getText().toString().isEmpty()) {
                        MetodoInsertarAnimalDesarrollador(ActivityAutentificacion.URLGeneral + "/OperacionesAnimales/DesarrolladorInsertarAnimalPOST.php");
                    }
                } else {
                    if (edCodigo.getText().toString().isEmpty() || edNombre.getText().toString().isEmpty() || edTamaño.getText().toString().isEmpty() || edEdad.getText().toString().isEmpty() || !CorroborarSeleccionImagen.equals("Hola")){
                        Toast.makeText(getApplicationContext(), "¡Faltan campos por llenar!", Toast.LENGTH_SHORT).show();
                    } else if (!edCodigo.getText().toString().isEmpty() && !edNombre.getText().toString().isEmpty() && !edTamaño.getText().toString().isEmpty() && !edEdad.getText().toString().isEmpty()) {
                        MetodoInsertarAnimalTrabajador(ActivityAutentificacion.URLGeneral + "/OperacionesAnimales/TrabajadorInsertarAnimalPOST.php");
                    }
                }
                CorroborarSeleccionImagen = "";
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoInsertarAnimalDesarrollador(String url){
        final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarCodigosAnimales(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CodigoAnimal", edCodigo.getText().toString());
                parametros.put("Nombre", edNombre.getText().toString());
                parametros.put("Especie", Especie);
                parametros.put("IdRazaCan", IdRazaCanina);
                parametros.put("IdRazaMin", IdRazaMinina);
                parametros.put("Tamaño", edTamaño.getText().toString());
                parametros.put("IdCondicion", IdCondicion);
                parametros.put("Institucion", spInstituciones.getSelectedItem().toString());
                parametros.put("Sexo", Sexo);
                parametros.put("Edad", edEdad.getText().toString());
                parametros.put("Fotografia", getStringImagen(bitmap));
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoInsertarAnimalTrabajador(String url){
        final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarCodigosAnimales(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CodigoAnimal", edCodigo.getText().toString());
                parametros.put("Nombre", edNombre.getText().toString());
                parametros.put("Especie", Especie);
                parametros.put("IdRazaCan", IdRazaCanina);
                parametros.put("IdRazaMin", IdRazaMinina);
                parametros.put("Tamaño", edTamaño.getText().toString());
                parametros.put("IdCondicion", IdCondicion);
                parametros.put("Institucion", ActivityAutentificacion.IdInst);
                parametros.put("Sexo", Sexo);
                parametros.put("Edad", edEdad.getText().toString());
                parametros.put("Fotografia", getStringImagen(bitmap));
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoValidarAnimalInexistenteModificar(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Toast.makeText(getApplicationContext(), "Animal ya registrado.", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (ActivityAutentificacion.NivelAdmin.equals("Desarrollador")) {
                    if (CorroborarSeleccionImagen.equals("")) {
                        MetodoActualizarAnimalSinImagenDesarrollador(ActivityAutentificacion.URLGeneral + "/OperacionesAnimales/DesarrolladorEditarPOST.php");
                    } else {
                        MetodoActualizarAnimalConImagenDesarrollador(ActivityAutentificacion.URLGeneral + "/OperacionesAnimales/DesarrolladorEditarPOSTImagen.php");
                    }
                } else {
                    if (CorroborarSeleccionImagen.equals("")) {
                        MetodoActualizarAnimalSinImagenTrabajador(ActivityAutentificacion.URLGeneral + "/OperacionesAnimales/TrabajadorEditarPOST.php");
                    } else {
                        MetodoActualizarAnimalConImagenTrabajador(ActivityAutentificacion.URLGeneral + "/OperacionesAnimales/TrabajadorEditarPOSTImagen.php");
                    }
                }
                CorroborarSeleccionImagen = "";
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoActualizarAnimalConImagenDesarrollador(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarCodigosAnimales(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CodigoAnimalBus", CodigoAnimalAyuda);
                parametros.put("CodigoAnimal", edCodigo.getText().toString());
                parametros.put("Nombre", edNombre.getText().toString());
                parametros.put("Especie", spEspecies.getSelectedItem().toString());
                parametros.put("IdRazaCan", IdRazaCanina);
                parametros.put("IdRazaMin", IdRazaMinina);
                parametros.put("Tamaño", edTamaño.getText().toString());
                parametros.put("Institucion", spInstituciones.getSelectedItem().toString());
                parametros.put("IdCondicion", IdCondicion);
                parametros.put("Sexo", spSexos.getSelectedItem().toString());
                parametros.put("Edad", edEdad.getText().toString());
                parametros.put("Fotografia", getStringImagen(bitmap));
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoActualizarAnimalConImagenTrabajador(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarCodigosAnimales(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CodigoAnimalBus", CodigoAnimalAyuda);
                parametros.put("CodigoAnimal", edCodigo.getText().toString());
                parametros.put("Nombre", edNombre.getText().toString());
                parametros.put("Especie", spEspecies.getSelectedItem().toString());
                parametros.put("IdRazaCan", IdRazaCanina);
                parametros.put("IdRazaMin", IdRazaMinina);
                parametros.put("Tamaño", edTamaño.getText().toString());
                parametros.put("IdCondicion", IdCondicion);
                parametros.put("Sexo", spSexos.getSelectedItem().toString());
                parametros.put("Edad", edEdad.getText().toString());
                parametros.put("Fotografia", getStringImagen(bitmap));
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoActualizarAnimalSinImagenDesarrollador(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarCodigosAnimales(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CodigoAnimalBus", CodigoAnimalAyuda);
                parametros.put("CodigoAnimal", edCodigo.getText().toString());
                parametros.put("Nombre", edNombre.getText().toString());
                parametros.put("Especie", spEspecies.getSelectedItem().toString());
                parametros.put("IdRazaCan", IdRazaCanina);
                parametros.put("IdRazaMin", IdRazaMinina);
                parametros.put("Tamaño", edTamaño.getText().toString());
                parametros.put("Institucion", spInstituciones.getSelectedItem().toString());
                parametros.put("IdCondicion", IdCondicion);
                parametros.put("Sexo", spSexos.getSelectedItem().toString());
                parametros.put("Edad", edEdad.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoActualizarAnimalSinImagenTrabajador(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarCodigosAnimales(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CodigoAnimalBus", CodigoAnimalAyuda);
                parametros.put("CodigoAnimal", edCodigo.getText().toString());
                parametros.put("Nombre", edNombre.getText().toString());
                parametros.put("Especie", spEspecies.getSelectedItem().toString());
                parametros.put("IdRazaCan", IdRazaCanina);
                parametros.put("IdRazaMin", IdRazaMinina);
                parametros.put("Tamaño", edTamaño.getText().toString());
                parametros.put("IdCondicion", IdCondicion);
                parametros.put("Sexo", spSexos.getSelectedItem().toString());
                parametros.put("Edad", edEdad.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoEliminarAnimal(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarCodigosAnimales(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CodigoAnimal", edCodigo.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleciona imagen"), PICK_IMAGE_REQUEST);
        CorroborarSeleccionImagen = "Hola";
    }

    public String getStringImagen(Bitmap bmp) {
        if (bmp != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] imageBytes = baos.toByteArray();
            String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            CorroborarSeleccionImagen = "Hola";
            return encodedImage;
        } else {
            CorroborarSeleccionImagen = "";
            return "";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imFotografia.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void MetodoRecuperarInstitucionActual(){
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        InstitucionActual = (preferences.getString("IdInstitucion",""));
    }

    private void MetodoLimpiarCampos (){
        CorroborarSeleccionImagen = "";
        edCodigo.setText("");
        edEdad.setText("");
        edNombre.setText("");
        edTamaño.setText("");
        imFotografia.setImageBitmap(null);
        spCodigosAnimales.setSelection(0);
        spCondiciones.setSelection(0);
        spEspecies.setSelection(0);
        spInstituciones.setSelection(0);
        spRazasCaninas.setSelection(0); spRazasMininas.setSelection(0);
        spSexos.setSelection(0);
    }
}