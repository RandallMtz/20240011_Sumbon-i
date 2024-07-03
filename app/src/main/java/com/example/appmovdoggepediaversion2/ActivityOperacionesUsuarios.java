package com.example.appmovdoggepediaversion2;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ActivityOperacionesUsuarios extends ActivityBasic {

    private AsyncHttpClient cliente;
    private Button btBuscarUsuario, btEliminarUsuario, btInsertarUsuario, btLimpiarCampos, btModificarUsuario;
    private DatePickerDialog pcFechaNacimiento;
    private EditText edApellidoMaterno, edApellidoPaterno, edCalle, edContraseña, edCorreo, edFechaNacimiento, edMunicipio, edNombres, edNumeroExterior, edNumeroInterior, edTelefonoPrincipal, edTelefonoSecundario, edUsuario;
    private RequestQueue requestQueue;
    private SearchableSpinner spBuscarUsuarios;
    private Spinner spCodigosPostales, spColonias, spInstituciones, spNivelesAdministrativos;
    private TextView lbAuxiliar;

    private boolean edContraseñaVisibility;
    private int IdCodigoPostalBusqueda;
    private String CodigoPostalSpinner, CodigoPostalBusqueda, ColoniaBusqueda, InstitucionActual, InstitucionBusqueda, MunicipioCodificado, URLNivelesAdministracion, URLUsuarios;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operaciones_usuarios);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        cliente = new AsyncHttpClient();

        btBuscarUsuario = findViewById(R.id.BtUsuarioOperacionesUsuarios); btEliminarUsuario = findViewById(R.id.BtEliminarOperacionesUsuarios);
        btInsertarUsuario = findViewById(R.id.BtInsertarOperacionesUsuarios); btModificarUsuario = findViewById(R.id.BtModificarOperacionesUsuarios);
        btLimpiarCampos = findViewById(R.id.BtLimpiarCamposOperacionesUsuarios);

        edApellidoPaterno = findViewById(R.id.EditTApePatOperacionesUsuarios); edApellidoMaterno = findViewById(R.id.EditTApeMatOperacionesUsuarios);
        edCalle = findViewById(R.id.EditTCalleOperacionesUsuarios);
        edContraseña = findViewById(R.id.EditTContraseñaOperacionesUsuarios);
        edCorreo = findViewById(R.id.EditTCorreoOperacionesUsuarios);
        edFechaNacimiento = findViewById(R.id.EditTFechaNacOperacionesUsuarios);
        edMunicipio = findViewById(R.id.EditTMunicipioOperacionesUsuario);
        edNombres = findViewById(R.id.EditTNombreOperacionesUsuarios);
        edNumeroInterior = findViewById(R.id.EditTNuIntOperacionesUsuarios); edNumeroExterior = findViewById(R.id.EditTNuExtOperacionesUsuarios);
        edTelefonoPrincipal = findViewById(R.id.EditT1erTelefonoOperacionesUsuarios); edTelefonoSecundario = findViewById(R.id.EditT2doTelefonoOperacionesUsuarios);
        edUsuario = findViewById(R.id.EditTUsuarioOperacionesUsuarios);

        lbAuxiliar = findViewById(R.id.TextVUsuarioOperacionesUsuarios);

        spBuscarUsuarios = findViewById(R.id.SpinnerUsuariosBuscar);
        spColonias = findViewById(R.id.SpColoniaOperacionesUsuarios);
        spCodigosPostales = findViewById(R.id.SpCodigoPostalOperacionesUsuarios);
        spInstituciones = findViewById(R.id.SpInstitucionOperacionesUsuarios);
        spNivelesAdministrativos = findViewById(R.id.SpNivelAdministrativoOperacionesUsuarios);

        spBuscarUsuarios.setTitle("Selecciona un usuario.");
        spBuscarUsuarios.setPositiveButton("Cerrar.");

        MetodoRecuperarInstitucionActual();

        if (ActivityAutentificacion.NivelAdmin.equals("Desarrollador")){
            spInstituciones.setEnabled(true);
            spInstituciones.setVisibility(View.VISIBLE);
            URLUsuarios = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerUsuariosDesarrollador.php";
            URLNivelesAdministracion = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerNivelesUsuarioDesarrollador.php";
        } else if (ActivityAutentificacion.NivelAdmin.equals("Administrativo")) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            layoutParams.width = 0;
            layoutParams.height = 0;

            spInstituciones.setLayoutParams(layoutParams);
            spInstituciones.setEnabled(false);
            spInstituciones.setVisibility(View.INVISIBLE);
            URLUsuarios = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerUsuariosAdministrativo.php?IdInstitucion=" + InstitucionActual + "";
            URLNivelesAdministracion = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerNivelesUsuarioAdministrativo.php";
        }

        final ProgressDialog loading = ProgressDialog.show(this, "Cargando información...", "Espere por favor");

        MetodoPoblarUsuarios(); MetodoPoblarCodigosPostales(); MetodoPoblarInstituciones(); MetodoPoblarNivelesAdministracion();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.dismiss();
            }
        }, 2000);

        btBuscarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityAutentificacion.NivelAdmin.equals("Desarrollador")){
                    MetodoBuscarUsuarioDesarollador(ActivityAutentificacion.URLGeneral + "/OperacionesUsuarios/GlobalBuscarGET.php?Usuario=" + spBuscarUsuarios.getSelectedItem() + "");
                } else {
                    MetodoBuscarUsuarioAdministrativo(ActivityAutentificacion.URLGeneral + "/OperacionesUsuarios/GlobalBuscarGET.php?Usuario=" + spBuscarUsuarios.getSelectedItem() + "");
                }
            }
        });

        btLimpiarCampos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoLimpiarCampos();
            }
        });

        edFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                int maxYear = calendar.get(Calendar.YEAR) - 18;

                Locale locale = new Locale("es", "ES");
                Locale.setDefault(locale);

                pcFechaNacimiento = new DatePickerDialog(ActivityOperacionesUsuarios.this, R.style.Theme_AppMovDoggepediaVersion2Dialogs, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int i, int i1, int i2) {
                        if (i > maxYear) {
                            Toast.makeText(getApplicationContext(), "Debe tener al menos 18 años", Toast.LENGTH_SHORT).show();
                        } else {
                            edFechaNacimiento.setText(i + "-" + (i1 + 1) + "-" + i2);
                        }
                    }
                }, year, month, day);
                pcFechaNacimiento.getDatePicker().setMaxDate(calendar.getTimeInMillis());
                pcFechaNacimiento.show();
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

        edContraseña.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= edContraseña.getRight()-edContraseña.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = edContraseña.getSelectionEnd();
                        if (edContraseñaVisibility) {
                            edContraseña.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_off_24,0);
                            edContraseña.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            edContraseñaVisibility = false;
                        } else {
                            edContraseña.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_24,0);
                            edContraseña.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            edContraseñaVisibility = true;
                        }
                        edContraseña.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        btInsertarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoValidarUsuarioInexistenteInsertar(ActivityAutentificacion.URLGeneral + "/OperacionesUsuarios/GlobalValidarUsuarioInexistente.php?Usuario="+edUsuario.getText()+"");
            }
        });

        btModificarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MetodoValidarCorreo(edCorreo) == false){
                    if (!edUsuario.getText().toString().isEmpty() &&!edNombres.getText().toString().isEmpty() && !edApellidoPaterno.getText().toString().isEmpty() && !edApellidoMaterno.getText().toString().isEmpty() && !edFechaNacimiento.getText().toString().isEmpty()
                            && !edCorreo.getText().toString().isEmpty() && !edContraseña.getText().toString().isEmpty() && !edCalle.getText().toString().isEmpty() && !edNumeroExterior.getText().toString().isEmpty() && !edTelefonoPrincipal.getText().toString().isEmpty() && !edTelefonoSecundario.getText().toString().isEmpty()) {
                        if (ActivityAutentificacion.NivelAdmin.equals("Desarrollador")){
                            MetodoActualizarUsuarioDesarrollador(ActivityAutentificacion.URLGeneral + "/OperacionesUsuarios/DesarrolladorEditarPOST.php");
                            MetodoPoblarMunicipiosYColonias(ActivityAutentificacion.URLGeneral + "/Componentes/EditTextMunicipios.php?CodigoPostal=" + CodigoPostalSpinner + "");
                        } else if(ActivityAutentificacion.NivelAdmin.equals("Administrativo")){
                            MetodoActualizarUsuarioAdministrativo(ActivityAutentificacion.URLGeneral + "/OperacionesUsuarios/AdministrativoEditarPOST.php");
                            MetodoPoblarMunicipiosYColonias(ActivityAutentificacion.URLGeneral + "/Componentes/EditTextMunicipios.php?CodigoPostal=" + CodigoPostalSpinner + "");
                        }
                    } else if (edUsuario.getText().toString().isEmpty()){
                        edUsuario.setError("¡Define un usuario!");
                    } else {
                        Toast.makeText(getApplicationContext(), "¡Faltan campos por llenar!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btEliminarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edUsuario.getText().toString().isEmpty()) {
                    MetodoEliminarUsuario(ActivityAutentificacion.URLGeneral + "/OperacionesUsuarios/GlobalEliminarPOST.php");
                } else {
                    edUsuario.setError("¡Define un usuario!");
                }
                spNivelesAdministrativos.setSelection(0);
                spCodigosPostales.setSelection(0);
            }
        });
    }

    private void MetodoPoblarUsuarios(){
        cliente.post(URLUsuarios, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){MetodoCargarUsuarios(new String((responseBody)));}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void MetodoCargarUsuarios(String respuesta){
        ArrayList<AdquirirUsuarios> lista = new ArrayList<AdquirirUsuarios>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){
                AdquirirUsuarios rz = new AdquirirUsuarios();
                rz.setUsuario(jsonArreglo.getJSONObject(i).getString("Usuario"));
                lista.add(rz);
            }
            ArrayAdapter<AdquirirUsuarios> a = new ArrayAdapter <AdquirirUsuarios> (getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spBuscarUsuarios.setAdapter(a);
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
        ArrayList <AdquirirCodigosPostales> lista = new ArrayList<AdquirirCodigosPostales>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){
                AdquirirCodigosPostales cp = new AdquirirCodigosPostales();
                cp.setCodigoPostal(jsonArreglo.getJSONObject(i).getString("CodigoPostal"));
                lista.add(cp);
            }
            ArrayAdapter <AdquirirCodigosPostales> a = new ArrayAdapter <AdquirirCodigosPostales> (getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
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
                                if (statusCode == 200 && responseBody != null && responseBody.length > 0){MetodoCargarColonias(new String((responseBody)));}
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
                Toast.makeText(getApplicationContext(), "Usuario inexistente", Toast.LENGTH_SHORT).show();
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

    private void MetodoPoblarNivelesAdministracion(){
        cliente.post(URLNivelesAdministracion, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){MetodoCargarNivelesAdministracion(new String((responseBody)));}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void MetodoCargarNivelesAdministracion(String respuesta){
        ArrayList <AdquirirNivelesAdministracion> lista = new ArrayList<AdquirirNivelesAdministracion>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){
                AdquirirNivelesAdministracion nv = new AdquirirNivelesAdministracion();
                nv.setNivel(jsonArreglo.getJSONObject(i).getString("Nivel"));
                lista.add(nv);
            }
            ArrayAdapter <AdquirirNivelesAdministracion> a = new ArrayAdapter <AdquirirNivelesAdministracion> (getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spNivelesAdministrativos.setAdapter(a);
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

    private void MetodoBuscarUsuarioDesarollador(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            spBuscarUsuarios.setSelection(0);
                            edUsuario.setText(jsonObject.getString("Usuario"));
                            edNombres.setText(jsonObject.getString("Nombre"));
                            edApellidoPaterno.setText(jsonObject.getString("ApePat"));
                            edApellidoMaterno.setText(jsonObject.getString("ApeMat"));
                            edFechaNacimiento.setText(jsonObject.getString("FechaNac"));
                            edContraseña.setText(jsonObject.getString("ContraseñaSimple"));
                            edCalle.setText(jsonObject.getString("Calle"));
                            edNumeroInterior.setText(jsonObject.getString("NumInt"));
                            edNumeroExterior.setText(jsonObject.getString("NumExt"));
                            edTelefonoPrincipal.setText(jsonObject.getString("Telefono1"));
                            edTelefonoSecundario.setText(jsonObject.getString("Telefono2"));
                            edCorreo.setText(jsonObject.getString("Correo"));
                            spNivelesAdministrativos.setSelection(Integer.parseInt(jsonObject.getString("IdNivel")) - 1);
                            IdCodigoPostalBusqueda = jsonObject.getInt("IdCodigoPostal");
                            CodigoPostalBusqueda = jsonObject.getString("CodigoPostal");
                            InstitucionBusqueda = jsonObject.getString("NombreInstitucion");
                            ColoniaBusqueda = jsonObject.getString("Colonia");
                            lbAuxiliar.setText(jsonObject.getString("Usuario"));

                            JsonArrayRequest jsonArrayRequest1 = new JsonArrayRequest(ActivityAutentificacion.URLGeneral + "/Componentes/BuscarCodigosMunicipiosYColonias/BuscarGETCodigoPostalGet.php", new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        JSONObject jsonObject1 = null;
                                        for (int i = 0; i < response.length(); i++) {
                                            try {
                                                jsonObject1 = response.getJSONObject(i);
                                                if (jsonObject1.getString("CodigoPostal").equals(CodigoPostalBusqueda)) {
                                                    int Valor = Integer.parseInt(jsonObject1.getString("id_temporal")) - 1;
                                                    Handler handler1 = new Handler(Looper.getMainLooper());
                                                    handler1.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            try {
                                                                spCodigosPostales.setSelection(Valor);
                                                                Handler handler1 = new Handler(Looper.getMainLooper());
                                                                handler1.postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            MetodoBuscarColoniaAfterCodigoPostal();

                                                                            JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(ActivityAutentificacion.URLGeneral + "/OperacionesUsuarios/GlobalIdsTemporalesInstituciones.php", new Response.Listener<JSONArray>() {
                                                                                @Override
                                                                                public void onResponse(JSONArray response) {
                                                                                    try {
                                                                                        JSONObject jsonObject2 = null;
                                                                                        for (int i = 0; i < response.length(); i++) {
                                                                                            try {
                                                                                                jsonObject2 = response.getJSONObject(i);
                                                                                                if (jsonObject2.getString("Nombre").equals(InstitucionBusqueda)) {
                                                                                                    int Valor = Integer.parseInt(jsonObject2.getString("id_temporal")) - 1;
                                                                                                    Handler handler1 = new Handler(Looper.getMainLooper());
                                                                                                    handler1.postDelayed(new Runnable() {
                                                                                                        @Override
                                                                                                        public void run() {
                                                                                                            try {
                                                                                                                spInstituciones.setSelection(Valor);
                                                                                                            } catch (Exception e) {
                                                                                                                Toast.makeText(getApplicationContext(), "Error al seleccionar institución.", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    }, 1000);
                                                                                                }
                                                                                            } catch (JSONException e) {
                                                                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    } catch (Exception e) {
                                                                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            }, new Response.ErrorListener() {
                                                                                @Override
                                                                                public void onErrorResponse(VolleyError error) {
                                                                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
                                                                            requestQueue.add(jsonArrayRequest2);
                                                                        } catch (Exception e) {
                                                                            Toast.makeText(getApplicationContext(), "Error al realizar la solicitud de instituciones.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                }, 1000);
                                                            } catch (Exception e) {
                                                                Toast.makeText(getApplicationContext(), "Error al seleccionar código postal.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }, 1000);
                                                }
                                            } catch (JSONException e) {
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), "Error al procesar la respuesta del código postal.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            requestQueue.add(jsonArrayRequest1);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error al procesar la respuesta principal.", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "¡Usuario inexistente!", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoBuscarUsuarioAdministrativo (String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        spBuscarUsuarios.setSelection(0);
                        edUsuario.setText(jsonObject.getString("Usuario"));
                        edNombres.setText(jsonObject.getString("Nombre"));
                        edApellidoPaterno.setText(jsonObject.getString("ApePat"));
                        edApellidoMaterno.setText(jsonObject.getString("ApeMat"));
                        edFechaNacimiento.setText(jsonObject.getString("FechaNac"));
                        edContraseña.setText(jsonObject.getString("ContraseñaSimple"));
                        edCalle.setText(jsonObject.getString("Calle"));
                        edNumeroInterior.setText(jsonObject.getString("NumInt"));
                        edNumeroExterior.setText(jsonObject.getString("NumExt"));
                        edTelefonoPrincipal.setText(jsonObject.getString("Telefono1"));
                        edTelefonoSecundario.setText(jsonObject.getString("Telefono2"));
                        edCorreo.setText(jsonObject.getString("Correo"));
                        if(jsonObject.getString("IdNivel").equals("3")){
                            spNivelesAdministrativos.setSelection(1);
                        } else if(jsonObject.getString("IdNivel").equals("2")){
                            spNivelesAdministrativos.setSelection(0);
                        }
                        IdCodigoPostalBusqueda = jsonObject.getInt("IdCodigoPostal");
                        CodigoPostalBusqueda = jsonObject.getString("CodigoPostal");
                        ColoniaBusqueda = jsonObject.getString("Colonia");
                        lbAuxiliar.setText(jsonObject.getString("Usuario"));

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
                Toast.makeText(getApplicationContext(), "¡Usuario inexistente!", Toast.LENGTH_SHORT).show();
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

    private void MetodoValidarUsuarioInexistenteInsertar (String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        edUsuario.setError("¡Intenta con un usuario distinto!");
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!MetodoValidarCorreo(edCorreo) == false){
                    if (edUsuario.getText().toString().isEmpty()) {
                        edUsuario.setError("¡Hace falta un usuario!");
                    } else if (!edUsuario.getText().toString().isEmpty() &&!edNombres.getText().toString().isEmpty() && !edApellidoPaterno.getText().toString().isEmpty() && !edApellidoMaterno.getText().toString().isEmpty() && !edFechaNacimiento.getText().toString().isEmpty()
                            && !edCorreo.getText().toString().isEmpty() && !edContraseña.getText().toString().isEmpty() && !edCalle.getText().toString().isEmpty() && !edNumeroExterior.getText().toString().isEmpty() && !edTelefonoPrincipal.getText().toString().isEmpty() && !edTelefonoSecundario.getText().toString().isEmpty()) {
                        if (ActivityAutentificacion.NivelAdmin.equals("Desarrollador")){
                            MetodoInsertarUsuarioDesarrollador(ActivityAutentificacion.URLGeneral + "/OperacionesUsuarios/DesarrolladorInsertarPOST.php");
                            MetodoPoblarMunicipiosYColonias(ActivityAutentificacion.URLGeneral + "/Componentes/EditTextMunicipios.php?CodigoPostal=" + CodigoPostalSpinner + "");
                        } else if (ActivityAutentificacion.NivelAdmin.equals("Administrativo")){
                            MetodoInsertarUsuarioAdministrativo(ActivityAutentificacion.URLGeneral + "/OperacionesUsuarios/AdministrativoInsertarPOST.php");
                            MetodoPoblarMunicipiosYColonias(ActivityAutentificacion.URLGeneral + "/Componentes/EditTextMunicipios.php?CodigoPostal=" + CodigoPostalSpinner + "");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "¡Faltan campos por llenar!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoInsertarUsuarioDesarrollador (String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarUsuarios(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("Usuario", edUsuario.getText().toString());
                parametros.put("Nombre", edNombres.getText().toString());
                parametros.put("ApePat", edApellidoPaterno.getText().toString());
                parametros.put("ApeMat", edApellidoMaterno.getText().toString());
                parametros.put("FechaNac", edFechaNacimiento.getText().toString());
                parametros.put("Correo", edCorreo.getText().toString());
                parametros.put("Contrasena", edContraseña.getText().toString());
                parametros.put("IdNivel", spNivelesAdministrativos.getSelectedItem().toString());
                parametros.put("Institucion", spInstituciones.getSelectedItem().toString());
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

    private void MetodoInsertarUsuarioAdministrativo (String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarUsuarios(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("Usuario", edUsuario.getText().toString());
                parametros.put("Nombre", edNombres.getText().toString());
                parametros.put("ApePat", edApellidoPaterno.getText().toString());
                parametros.put("ApeMat", edApellidoMaterno.getText().toString());
                parametros.put("FechaNac", edFechaNacimiento.getText().toString());
                parametros.put("Correo", edCorreo.getText().toString());
                parametros.put("Contrasena", edContraseña.getText().toString());
                parametros.put("IdNivel", spNivelesAdministrativos.getSelectedItem().toString());
                parametros.put("Institucion", ActivityAutentificacion.IdInst);
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

    private void MetodoActualizarUsuarioDesarrollador(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarUsuarios(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("UsuarioBus", lbAuxiliar.getText().toString());
                parametros.put("Usuario", edUsuario.getText().toString());
                parametros.put("Nombre", edNombres.getText().toString());
                parametros.put("ApePat", edApellidoPaterno.getText().toString());
                parametros.put("ApeMat", edApellidoMaterno.getText().toString());
                parametros.put("FechaNac", edFechaNacimiento.getText().toString());
                parametros.put("Correo", edCorreo.getText().toString());
                parametros.put("Contrasena", edContraseña.getText().toString());
                parametros.put("IdNivel", spNivelesAdministrativos.getSelectedItem().toString());
                parametros.put("Institucion", spInstituciones.getSelectedItem().toString());
                parametros.put("Calle", edCalle.getText().toString());
                parametros.put("NumInt", edNumeroInterior.getText().toString());
                parametros.put("NumExt", edNumeroExterior.getText().toString());
                parametros.put("Colonia", spColonias.getSelectedItem().toString());
                parametros.put("Telefono", edTelefonoPrincipal.getText().toString());
                parametros.put("Telefono2", edTelefonoSecundario.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoActualizarUsuarioAdministrativo (String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarUsuarios(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("UsuarioBus", lbAuxiliar.getText().toString());
                parametros.put("Usuario", edUsuario.getText().toString());
                parametros.put("Nombre", edNombres.getText().toString());
                parametros.put("ApePat", edApellidoPaterno.getText().toString());
                parametros.put("ApeMat", edApellidoMaterno.getText().toString());
                parametros.put("FechaNac", edFechaNacimiento.getText().toString());
                parametros.put("Correo", edCorreo.getText().toString());
                parametros.put("Contrasena", edContraseña.getText().toString());
                parametros.put("IdNivel", spNivelesAdministrativos.getSelectedItem().toString());
                parametros.put("Calle", edCalle.getText().toString());
                parametros.put("NumInt", edNumeroInterior.getText().toString());
                parametros.put("NumExt", edNumeroExterior.getText().toString());
                parametros.put("Colonia", spColonias.getSelectedItem().toString());
                parametros.put("Telefono", edTelefonoPrincipal.getText().toString());
                parametros.put("Telefono2", edTelefonoSecundario.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoEliminarUsuario(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoPoblarUsuarios(); MetodoLimpiarCampos();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("Usuario", edUsuario.getText().toString());
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
        edApellidoMaterno.setText(""); edApellidoPaterno.setText("");
        edCalle.setText("");
        edContraseña.setText("");
        edCorreo.setText("");
        edFechaNacimiento.setText("");
        edTelefonoPrincipal.setText(""); edTelefonoSecundario.setText("");
        edMunicipio.setText("");
        edNombres.setText("");
        edNumeroInterior.setText(""); edNumeroExterior.setText("");
        edUsuario.setText("");
        lbAuxiliar.setText("");
        spBuscarUsuarios.setSelection(0);
        spCodigosPostales.setSelection(0);
        spInstituciones.setSelection(0);
        spNivelesAdministrativos.setSelection(0);
    }
}