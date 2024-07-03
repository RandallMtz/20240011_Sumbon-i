package com.example.appmovdoggepediaversion2;

import androidx.annotation.Nullable;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ActivityOperacionesCampanas extends ActivityBasic {

    Bitmap bitmap;
    private AsyncHttpClient cliente;
    private Button btBuscarCampaña, btInsertarCampaña, btEliminarCampaña, btLimpiarCampos, btModificarCampaña, btSeleccionarImagen;
    private DatePickerDialog pcFecha;
    private EditText edCalle, edCodigo, edDescripcion, edFecha, edFinalidad, edMunicipio, edNumeroExterior, edNumeroInterior, edTelefonoPrincipal, edTelefonoSecundario;
    private ImageView imFotografiaPromocional;
    private RequestQueue requestQueue;
    private SearchableSpinner spCodigosCampañas;
    private Spinner spCodigosPostales, spColonias;

    private int PICK_IMAGE_REQUEST = 1;
    private String CodigoCampañaBusqueda, CodigoPostal, CodigoPostalBusqueda, ColoniaBusqueda, CorroborarSeleccionImagen = "Hola", MunicipioCodificado;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operaciones_campanas);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        cliente = new AsyncHttpClient();

        btBuscarCampaña = findViewById(R.id.BtCampañaBuscarOperacionesCampañas); btEliminarCampaña = findViewById(R.id.BtEliminarOperacionesCampañas);
        btInsertarCampaña = findViewById(R.id.BtInsertarOperacionesCampañas); btLimpiarCampos = findViewById(R.id.BtLimpiarCamposOperacionesCampañas);
        btModificarCampaña = findViewById(R.id.BtModificarOperacionesCampañas); btSeleccionarImagen = findViewById(R.id.BtSeleccionarImagenOperacionesCampañas);

        edCalle = findViewById(R.id.EditTCalleOperacionesCampañas);
        edCodigo = findViewById(R.id.EditTCodigoCampañaOperacionesCampañas);
        edDescripcion = findViewById(R.id.EditTDescripcionOperacionesCampañas);
        edFecha = findViewById(R.id.EditTFechaRealizacionOperacionesCampañas);
        edFinalidad = findViewById(R.id.EditTFinalidadOperacionesCampañas);
        edMunicipio = findViewById(R.id.EditTMunicipioOperacionesCampañas);
        edNumeroExterior = findViewById(R.id.EditTNumExtOperacionesCampañas); edNumeroInterior = findViewById(R.id.EditTNumIntOperacionesCampañas);
        edTelefonoPrincipal = findViewById(R.id.EditTTelefonoUnoOperacionesCampañas); edTelefonoSecundario = findViewById(R.id.EditTTelefonoDosOperacionesCampañas);
        
        imFotografiaPromocional = findViewById(R.id.ImgVFotografiaOperacionesCampañas);

        spCodigosCampañas = findViewById(R.id.SpinnerCampañasBuscar);
        spCodigosPostales = findViewById(R.id.SpCodigoPostalOperacionesCampañas); spColonias = findViewById(R.id.SpColoniaOperacionesCampañas);

        spCodigosCampañas.setTitle("Selecciona un codígo de campaña.");
        spCodigosCampañas.setPositiveButton("Cerrar.");

        final ProgressDialog loading = ProgressDialog.show(this, "Cargando información...", "Espere por favor");

        MetodoPoblarCodigosCampañas(); MetodoPoblarCodigosPostales();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.dismiss();
            }
        }, 2000);

        btBuscarCampaña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoBuscarCampaña(ActivityAutentificacion.URLGeneral + "/OperacionesCampanas/BuscarGET.php?CodigoCampaña=" + spCodigosCampañas.getSelectedItem() + "");
            }
        });

        btLimpiarCampos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoLimpiarCampos();
            }
        });

        btSeleccionarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        spCodigosPostales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CodigoPostal = adapterView.getSelectedItem()+"";
                MetodoPoblarMunicipiosYColonias(ActivityAutentificacion.URLGeneral + "/Componentes/EditTextMunicipios.php?CodigoPostal=" + CodigoPostal + "");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        edFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                Locale locale = new Locale("es", "ES");
                Locale.setDefault(locale);

                pcFecha = new DatePickerDialog(ActivityOperacionesCampanas.this, R.style.Theme_AppMovDoggepediaVersion2Dialogs, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int i, int i1, int i2) {
                        edFecha.setText(i + "-" + (i1+1) + "-" + i2);
                    }
                }, year, month, day);
                pcFecha.show();
            }
        });

        btInsertarCampaña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoValidarCampañaInexistenteInsertar(ActivityAutentificacion.URLGeneral + "/OperacionesCampanas/ValidarCampanaInexistente.php?CodigoCampaña="+edCodigo.getText()+"");
            }
        });

        btModificarCampaña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edCodigo.getText().toString().isEmpty() && !edFinalidad.getText().toString().isEmpty() && !edFecha.getText().toString().isEmpty() && !edDescripcion.getText().toString().isEmpty() && !edCalle.getText().toString().isEmpty()
                        && !edTelefonoPrincipal.getText().toString().isEmpty() && !edTelefonoSecundario.getText().toString().isEmpty()) {
                    MetodoValidarCampañaInexistenteModificar(ActivityAutentificacion.URLGeneral + "/OperacionesCampanas/ValidarCampanaInexistenteModificar.php?CodigoCampaña=" + edCodigo.getText().toString() + "&CodigoCampaña1=" + CodigoCampañaBusqueda + "");
                    MetodoPoblarMunicipiosYColonias(ActivityAutentificacion.URLGeneral + "/Componentes/EditTextMunicipios.php?CodigoPostal=" + CodigoPostal + "");
                } else if (edCodigo.getText().toString().isEmpty()){
                    edCodigo.setError("¡Define un código de campaña!");
                } else {
                    Toast.makeText(getApplicationContext(), "¡Faltan campos por llenar!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btEliminarCampaña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edCodigo.getText().toString().isEmpty()){
                    MetodoEliminarCampaña(ActivityAutentificacion.URLGeneral + "/OperacionesCampanas/EliminarPOST.php");
                } else {
                    edCodigo.setError("¡Define un código de campaña!");
                }
            }
        });
    }

    private void MetodoPoblarCodigosCampañas(){
        String url = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerCodigosCampanas.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    MetodoCargarCodigosCampañas(new String((responseBody)));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void MetodoCargarCodigosCampañas(String respuesta){
        ArrayList<AdquirirCampanas> lista = new ArrayList<AdquirirCampanas>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){
                AdquirirCampanas rz = new AdquirirCampanas();
                rz.setCodigoCampaña(jsonArreglo.getJSONObject(i).getString("CodigoCampaña"));
                lista.add(rz);
            }
            ArrayAdapter<AdquirirCampanas> a = new ArrayAdapter <AdquirirCampanas> (getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spCodigosCampañas.setAdapter(a);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void MetodoPoblarCodigosPostales(){
        String url = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerCodigosPostales.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    MetodoCargarCodigosPostales(new String((responseBody)));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void MetodoCargarCodigosPostales(String respuesta){
        ArrayList<AdquirirCodigosPostales> lista = new ArrayList<AdquirirCodigosPostales>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){
                AdquirirCodigosPostales cp = new AdquirirCodigosPostales();
                cp.setCodigoPostal(jsonArreglo.getJSONObject(i).getString("CodigoPostal"));
                lista.add(cp);
            }
            ArrayAdapter<AdquirirCodigosPostales> a = new ArrayAdapter <AdquirirCodigosPostales> (getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spCodigosPostales.setAdapter(a);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void MetodoPoblarMunicipiosYColonias(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        edMunicipio.setText(jsonObject.getString("Municipio"));
                        MunicipioCodificado = MetodoCodificarTexto(edMunicipio.getText().toString());

                        String url = null;
                        url = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerColonias.php?CodigoPostal=" + CodigoPostal +"&Municipio=" + MunicipioCodificado + "";
                        cliente.post(url, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if (statusCode == 200 && responseBody != null && responseBody.length > 0){
                                    MetodoCargarColonias(new String((responseBody)));
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
                        });
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Evento inexistente", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoCargarColonias(String respuesta){
        ArrayList <AdquirirColonias> lista = new ArrayList<AdquirirColonias>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){
                AdquirirColonias cn = new AdquirirColonias();
                cn.setColonia(jsonArreglo.getJSONObject(i).getString("Colonia"));
                lista.add(cn);
            }
            ArrayAdapter <AdquirirColonias> a = new ArrayAdapter <AdquirirColonias> (getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spColonias.setAdapter(a);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void MetodoBuscarCampaña (String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        CorroborarSeleccionImagen = "";
                        CodigoCampañaBusqueda = spCodigosCampañas.getSelectedItem().toString();
                        edCodigo.setText(jsonObject.getString("CodigoCampaña"));
                        edFinalidad.setText(jsonObject.getString("Finalidad"));
                        edDescripcion.setText(jsonObject.getString("Descripcion"));
                        edFecha.setText(jsonObject.getString("Fecha"));
                        edCalle.setText(jsonObject.getString("Calle"));
                        edNumeroInterior.setText(jsonObject.getString("NumInt"));
                        edNumeroExterior.setText(jsonObject.getString("NumExt"));
                        CodigoPostalBusqueda = jsonObject.getString("CodigoPostal");
                        edTelefonoPrincipal.setText(jsonObject.getString("Telefono1"));
                        edTelefonoSecundario.setText(jsonObject.getString("Telefono2"));
                        ColoniaBusqueda = jsonObject.getString("Colonia");
                        spCodigosCampañas.setSelection(0);
                        Glide.with(getApplicationContext())
                                .load(jsonObject.getString("FotoPromocional") + "?timestamp=" + System.currentTimeMillis())
                                .into(imFotografiaPromocional);

                        JsonArrayRequest jsonArrayRequest1 = new JsonArrayRequest(ActivityAutentificacion.URLGeneral + "/Componentes/BuscarCodigosMunicipiosYColonias/BuscarGETCodigoPostalGet.php", new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                JSONObject jsonObject1 = null;
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        jsonObject1 = response.getJSONObject(i);
                                        if (jsonObject1.getString("CodigoPostal").equals(CodigoPostalBusqueda)){
                                            int Valor = Integer.parseInt(jsonObject1.getString("id_temporal"))-1;
                                            Handler handler1 = new Handler(Looper.getMainLooper());
                                            handler1.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    spCodigosPostales.setSelection(Valor);
                                                    Handler handler1 = new Handler(Looper.getMainLooper());
                                                    handler1.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            MetodoBuscarColoniaAfterCodigoPostal();
                                                        }
                                                    }, 1000);
                                                }
                                            }, 1000);
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
                        requestQueue.add(jsonArrayRequest1);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "¡Campaña inexistente!", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoBuscarColoniaAfterCodigoPostal (){
        JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(ActivityAutentificacion.URLGeneral + "/Componentes/BuscarCodigosMunicipiosYColonias/IdsTemporalesCodigosMunicipiosColoniasGET.php?CodigoPostal=" + CodigoPostalBusqueda + "", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject2 = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject2 = response.getJSONObject(i);
                        if (jsonObject2.getString("Colonia").equals(ColoniaBusqueda)){
                            spColonias.setSelection(Integer.parseInt(jsonObject2.getString("id_temporal"))-1);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage() + "" + CodigoPostalBusqueda, Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest2);
    }

    private void MetodoValidarCampañaInexistenteInsertar (String url){
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
                if (!edCodigo.getText().toString().isEmpty() && !edFinalidad.getText().toString().isEmpty() && !edFecha.getText().toString().isEmpty() && !edDescripcion.getText().toString().isEmpty() && !edCalle.getText().toString().isEmpty()
                        && !edTelefonoPrincipal.getText().toString().isEmpty() && !edTelefonoSecundario.getText().toString().isEmpty()) {
                    MetodoInsertarCampaña(ActivityAutentificacion.URLGeneral + "/OperacionesCampanas/InsertarPOST.php");
                    MetodoPoblarMunicipiosYColonias(ActivityAutentificacion.URLGeneral + "/Componentes/EditTextMunicipios.php?CodigoPostal=" + CodigoPostal + "");
                } else {
                    Toast.makeText(getApplicationContext(), "¡Faltan campos por llenar!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoInsertarCampaña (String url){
        final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarCodigosCampañas(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CodigoCampaña", edCodigo.getText().toString());
                parametros.put("Finalidad", edFinalidad.getText().toString());
                parametros.put("Fecha", edFecha.getText().toString());
                parametros.put("Descripcion", edDescripcion.getText().toString());
                parametros.put("Calle", edCalle.getText().toString());
                parametros.put("NumInt", edNumeroInterior.getText().toString());
                parametros.put("NumExt", edNumeroExterior.getText().toString());
                parametros.put("Colonia", spColonias.getSelectedItem().toString());
                parametros.put("Telefono1", edTelefonoPrincipal.getText().toString());
                parametros.put("Telefono2", edTelefonoSecundario.getText().toString());
                parametros.put("Fotografia", getStringImagen(bitmap));
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoValidarCampañaInexistenteModificar(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Toast.makeText(getApplicationContext(), "Campaña ya registrada.", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (CorroborarSeleccionImagen.equals("")) {
                    MetodoActualizarCampaña(ActivityAutentificacion.URLGeneral + "/OperacionesCampanas/EditarPOST.php");
                } else {
                    MetodoActualizarCampañaImagen(ActivityAutentificacion.URLGeneral + "/OperacionesCampanas/EditarPOSTImagen.php");
                }
                CorroborarSeleccionImagen = "";
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoActualizarCampaña (String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarCodigosCampañas(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CampañaBus", CodigoCampañaBusqueda);
                parametros.put("CodigoCampaña", edCodigo.getText().toString());
                parametros.put("Finalidad", edFinalidad.getText().toString());
                parametros.put("Fecha", edFecha.getText().toString());
                parametros.put("Descripcion", edDescripcion.getText().toString());
                parametros.put("Calle", edCalle.getText().toString());
                parametros.put("NumInt", edNumeroInterior.getText().toString());
                parametros.put("NumExt", edNumeroExterior.getText().toString());
                parametros.put("Colonia", spColonias.getSelectedItem().toString());
                parametros.put("Telefono1", edTelefonoPrincipal.getText().toString());
                parametros.put("Telefono2", edTelefonoSecundario.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoActualizarCampañaImagen (String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarCodigosCampañas(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CampañaBus", CodigoCampañaBusqueda);
                parametros.put("CodigoCampaña", edCodigo.getText().toString());
                parametros.put("Finalidad", edFinalidad.getText().toString());
                parametros.put("Fecha", edFecha.getText().toString());
                parametros.put("Descripcion", edDescripcion.getText().toString());
                parametros.put("Calle", edCalle.getText().toString());
                parametros.put("NumInt", edNumeroInterior.getText().toString());
                parametros.put("NumExt", edNumeroExterior.getText().toString());
                parametros.put("Colonia", spColonias.getSelectedItem().toString());
                parametros.put("Telefono1", edTelefonoPrincipal.getText().toString());
                parametros.put("Telefono2", edTelefonoSecundario.getText().toString());
                parametros.put("Fotografia", getStringImagen(bitmap));
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoEliminarCampaña(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarCodigosCampañas(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CodigoCampaña", edCodigo.getText().toString());
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
            return encodedImage;
        } else {
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
                imFotografiaPromocional.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void MetodoLimpiarCampos (){
        edCalle.setText("");
        edCodigo.setText("");
        edDescripcion.setText("");
        edFecha.setText("");
        edFinalidad.setText("");
        edNumeroExterior.setText(""); edNumeroInterior.setText("");
        edTelefonoPrincipal.setText(""); edTelefonoSecundario.setText("");
        imFotografiaPromocional.setImageBitmap(null);
        spCodigosCampañas.setSelection(0);
        spCodigosPostales.setSelection(0);
    }
}