package com.example.appmovdoggepediaversion2;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
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

public class ActivityOperacionesAlianzas extends ActivityBasic {

    private AsyncHttpClient cliente;
    private Button btBuscarAlianza, btEliminarAlianza, btInsertarAlianza, btLimpiarCampos, btModificarAlianza;
    private EditText edCalle, edDescripcion, edEmpresa, edMunicipio, edNumeroExterior, edNumeroInterior, edTelefonoPrincipal, edTelefonoSecundario;
    private RequestQueue requestQueue;
    private SearchableSpinner spBuscarEmpresas;
    private Spinner spCodigosPostales, spColonias;


    private String CodigoPostalBusqueda, CodigoPostalSpinner, ColoniaBusqueda, EmpresaAyuda, MunicipioCodificado;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operaciones_alianzas);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        cliente = new AsyncHttpClient();

        btBuscarAlianza = findViewById(R.id.BtCampañaBuscarOperacionesAlianzas); btInsertarAlianza = findViewById(R.id.BtInsertarOperacionesAlianzas);
        btLimpiarCampos = findViewById(R.id.BtLimpiarCamposOperacionesAlianzas); btModificarAlianza = findViewById(R.id.BtModificarOperacionesAlianzas);
        btEliminarAlianza = findViewById(R.id.BtEliminarOperacionesAlianzas);

        edCalle = findViewById(R.id.EditTCalleOperacionesAlianzas);
        edEmpresa = findViewById(R.id.EditTCodigoCampañaOperacionesAlianzas);
        edDescripcion = findViewById(R.id.EditTDescripcionOperacionesAlianzas);
        edMunicipio = findViewById(R.id.EditTMunicipioOperacionesAlianzas);
        edNumeroExterior = findViewById(R.id.EditTNumExtOperacionesAlianzas); edNumeroInterior = findViewById(R.id.EditTNumIntOperacionesAlianzas);
        edTelefonoPrincipal = findViewById(R.id.EditTTelefonoUnoOperacionesAlianzas); edTelefonoSecundario = findViewById(R.id.EditTTelefonoDosOperacionesAlianzas);

        spBuscarEmpresas = findViewById(R.id.SpinnerEmpresasBuscar);
        spCodigosPostales = findViewById(R.id.SpCodigoPostalOperacionesAlianzas);
        spColonias = findViewById(R.id.SpColoniaOperacionesAlianzas);

        spBuscarEmpresas.setTitle("Selecciona una empresa.");
        spBuscarEmpresas.setPositiveButton("Cerrar.");

        final ProgressDialog loading = ProgressDialog.show(this, "Cargando información...", "Espere por favor");

        MetodoPoblarCodigosPostales(); MetodoPoblarEmpresas();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.dismiss();
            }
        }, 2000);

        btBuscarAlianza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoBuscarAlianza(ActivityAutentificacion.URLGeneral + "/OperacionesAlianzas/BuscarGET.php?Empresa=" + spBuscarEmpresas.getSelectedItem() + "");
            }
        });

        btLimpiarCampos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoLimpiarCampos();
            }
        });

        spCodigosPostales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CodigoPostalSpinner = adapterView.getSelectedItem()+"";
                MetodoPoblarMunicipiosYColonias(ActivityAutentificacion.URLGeneral + "/Componentes/EditTextMunicipios.php?CodigoPostal=" + CodigoPostalSpinner + "");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        btInsertarAlianza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoValidarAlianzaInexistenteModificar(ActivityAutentificacion.URLGeneral + "/OperacionesAlianzas/ValidarAlianzaInexistente.php?Empresa="+edEmpresa.getText()+"");
            }
        });

        btModificarAlianza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edEmpresa.getText().toString().isEmpty() && !edDescripcion.getText().toString().isEmpty() && !edCalle.getText().toString().isEmpty()
                        && !edTelefonoPrincipal.getText().toString().isEmpty() && !edTelefonoSecundario.getText().toString().isEmpty()) {
                            MetodoActualizarAlianza(ActivityAutentificacion.URLGeneral + "/OperacionesAlianzas/EditarPOST.php");
                            MetodoPoblarMunicipiosYColonias(ActivityAutentificacion.URLGeneral + "/Componentes/EditTextMunicipios.php?CodigoPostal=" + CodigoPostalSpinner + "");
                } else if (edEmpresa.getText().toString().isEmpty()){
                    edEmpresa.setError("¡Define una empresa!");
                } else {
                    Toast.makeText(getApplicationContext(), "¡Faltan campos por llenar!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btEliminarAlianza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edEmpresa.getText().toString().isEmpty()){
                    MetodoEliminarAlianza(ActivityAutentificacion.URLGeneral + "/OperacionesAlianzas/EliminarPOST.php");
                } else {
                    edEmpresa.setError("¡Define una empresa!");
                }
            }
        });
    }

    private void MetodoPoblarEmpresas(){
        String url = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerEmpresasAlianzas.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){MetodoCargarEmpresas(new String((responseBody)));}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void MetodoCargarEmpresas(String respuesta){
        ArrayList<AdquirirAlianzas> lista = new ArrayList<AdquirirAlianzas>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){
                AdquirirAlianzas rz = new AdquirirAlianzas();
                rz.setEmpresa(jsonArreglo.getJSONObject(i).getString("Empresa"));
                lista.add(rz);
            }
            ArrayAdapter<AdquirirAlianzas> a = new ArrayAdapter <AdquirirAlianzas> (getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spBuscarEmpresas.setAdapter(a);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void MetodoPoblarCodigosPostales(){
        String url = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerCodigosPostales.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){MetodoCargarCodigosPostales(new String((responseBody)));}
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
                        url = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerColonias.php?CodigoPostal=" + CodigoPostalSpinner +"&Municipio=" + MunicipioCodificado + "";
                        cliente.post(url, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if (statusCode == 200 && responseBody != null && responseBody.length > 0){MetodoCargarMunicipiosYColonias(new String((responseBody)));}
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
                Toast.makeText(getApplicationContext(), "Información inexistente", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoCargarMunicipiosYColonias(String respuesta){
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

    private void MetodoBuscarAlianza (String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        EmpresaAyuda = jsonObject.getString("IdAlianza");
                        edEmpresa.setText(jsonObject.getString("Empresa"));
                        edDescripcion.setText(jsonObject.getString("Descripcion"));
                        edCalle.setText(jsonObject.getString("Calle"));
                        edNumeroInterior.setText(jsonObject.getString("NumInt"));
                        edNumeroExterior.setText(jsonObject.getString("NumExt"));
                        CodigoPostalBusqueda = jsonObject.getString("CodigoPostal");
                        edTelefonoPrincipal.setText(jsonObject.getString("Telefono1"));
                        edTelefonoSecundario.setText(jsonObject.getString("Telefono2"));
                        ColoniaBusqueda = jsonObject.getString("Colonia");
                        spBuscarEmpresas.setSelection(0);

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
                Toast.makeText(getApplicationContext(), "¡Alianza inexistente!", Toast.LENGTH_SHORT).show();
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

    private void MetodoValidarAlianzaInexistenteModificar (String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        edEmpresa.setError("¡Intenta con una empresa distinta!");
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!edEmpresa.getText().toString().isEmpty() && !edDescripcion.getText().toString().isEmpty() && !edCalle.getText().toString().isEmpty()
                        && !edTelefonoPrincipal.getText().toString().isEmpty() && !edTelefonoSecundario.getText().toString().isEmpty()) {
                    MetodoInsertarAlianza(ActivityAutentificacion.URLGeneral + "/OperacionesAlianzas/InsertarPOST.php");
                    MetodoPoblarMunicipiosYColonias(ActivityAutentificacion.URLGeneral + "/Componentes/EditTextMunicipios.php?CodigoPostal=" + CodigoPostalSpinner + "");
                } else {
                    Toast.makeText(getApplicationContext(), "¡Faltan campos por llenar!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoInsertarAlianza (String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarEmpresas(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("Empresa", edEmpresa.getText().toString());
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

    private void MetodoActualizarAlianza (String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarEmpresas(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("IdAlianza", EmpresaAyuda);
                parametros.put("Empresa", edEmpresa.getText().toString());
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

    private void MetodoEliminarAlianza(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarEmpresas(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("Empresa", edEmpresa.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoLimpiarCampos (){
        edCalle.setText("");
        edDescripcion.setText("");
        edEmpresa.setText("");
        edNumeroExterior.setText(""); edNumeroInterior.setText("");
        edTelefonoPrincipal.setText(""); edTelefonoSecundario.setText("");
        spBuscarEmpresas.setSelection(0);
        spCodigosPostales.setSelection(0);
    }
}