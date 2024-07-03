package com.example.appmovdoggepediaversion2;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

public class ActivityModificarPerfilGenerico extends ActivityBasic {

    private AsyncHttpClient cliente;
    private Button btModificarPerfil;
    private DatePickerDialog pcFechaNacimiento;
    private EditText edApellidoMaterno, edApellidoPaterno, edCalle, edContraseña, edCorreo, edFechaNacimiento, edMunicipio, edNombres, edNumeroExterior, edNumeroInterior, edTelefonoPrincipal, edTelefonoSecundario, edUsuario;
    private RequestQueue requestQueue;
    private Spinner spCodigosPostales, spColonias;

    private boolean ContraseñaVisible;
    private int IdCodigoPostalBusqueda;
    private String CodigoPostalBusqueda, CodigoPostalSpinner, ColoniaSpinner, IdUsuarioActual, MunicipioCodificado, IdUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_perfil_generico);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        cliente = new AsyncHttpClient();

        Intent intent = getIntent();
        IdUsuario = intent.getExtras().getString("IdUsuario");

        btModificarPerfil = findViewById(R.id.BtModificarModificarPerfilGenerico);

        edApellidoMaterno = findViewById(R.id.EditTApeMatModificarPerfilGenerico);
        edApellidoPaterno = findViewById(R.id.EditTApePatModificarPerfilGenerico);
        edCalle = findViewById(R.id.EditTCalleModificarPerfilGenerico);
        edContraseña = findViewById(R.id.EditTContraseñaModificarPerfilGenerico);
        edCorreo = findViewById(R.id.EditTCorreoModificarPerfilGenerico);
        edFechaNacimiento = findViewById(R.id.EditTFechaNacModificarPerfilGenerico);
        edMunicipio = findViewById(R.id.EditTMunicipioModificarPerfilGenerico);
        edNombres = findViewById(R.id.EditTNombresModificarPerfilGenerico);
        edNumeroExterior = findViewById(R.id.EditTNuExtModificarPerfilGenerico);
        edNumeroInterior = findViewById(R.id.EditTNuIntModificarPerfilGenerico);
        edTelefonoPrincipal = findViewById(R.id.EditT1erTelefonoModificarPerfilGenerico);
        edTelefonoSecundario = findViewById(R.id.EditT2doTelefonoModificarPerfilGenerico);
        edUsuario = findViewById(R.id.EditTUsuarioModificarPerfilGenerico);

        spCodigosPostales = findViewById(R.id.SpCodigoPostalModificarPerfilGenerico);
        spColonias = findViewById(R.id.SpColoniaModificarPerfilGenerico);

        MetodoRecuperarUsuarioActual();
        MetodoPoblarCodigosPostales();
        MetodoBuscarPerfilUsuario(ActivityAutentificacion.URLGeneral + "/PerfilGenerico/BuscarGET.php?IdUsuario=" + IdUsuario);

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

                pcFechaNacimiento = new DatePickerDialog(ActivityModificarPerfilGenerico.this, R.style.Theme_AppMovDoggepediaVersion2Dialogs, new DatePickerDialog.OnDateSetListener() {
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
                CodigoPostalSpinner = adapterView.getSelectedItem() + "";
                MetodoPoblarMunicipiosYColonias(ActivityAutentificacion.URLGeneral + "/Componentes/EditTextMunicipios.php?CodigoPostal=" + CodigoPostalSpinner + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spColonias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ColoniaSpinner = adapterView.getSelectedItem() + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        edContraseña.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= edContraseña.getRight() - edContraseña.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = edContraseña.getSelectionEnd();
                        if (ContraseñaVisible) {
                            edContraseña.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_off_24, 0);
                            edContraseña.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            ContraseñaVisible = false;
                        } else {
                            edContraseña.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.baseline_visibility_24, 0);
                            edContraseña.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            ContraseñaVisible = true;
                        }
                        edContraseña.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        btModificarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!MetodoValidarCorreo(edCorreo) == false) {
                    if (!edUsuario.getText().toString().isEmpty() && !edNombres.getText().toString().isEmpty() && !edApellidoPaterno.getText().toString().isEmpty() && !edApellidoMaterno.getText().toString().isEmpty() && !edFechaNacimiento.getText().toString().isEmpty()
                            && !edCorreo.getText().toString().isEmpty() && !edCalle.getText().toString().isEmpty() && !edNumeroExterior.getText().toString().isEmpty() && !edTelefonoPrincipal.getText().toString().isEmpty() && !edTelefonoSecundario.getText().toString().isEmpty()) {
                        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(ActivityAutentificacion.URLGeneral + "/Componentes/BuscarCodigosMunicipiosYColonias/InsertarPOSTBuscarCodigoPostalGET.php?CodigoPostal=" + CodigoPostalSpinner + "&Municipio=" + MunicipioCodificado + "&Colonia=" + MetodoCodificarTexto(ColoniaSpinner) + "", new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                JSONObject jsonObject = null;
                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        jsonObject = response.getJSONObject(i);
                                    } catch (JSONException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        requestQueue.add(jsonArrayRequest);

                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MetodoActualizarPerfilUsuario(ActivityAutentificacion.URLGeneral + "/PerfilGenerico/EditarPOST.php");
                                MetodoPoblarMunicipiosYColonias(ActivityAutentificacion.URLGeneral + "/Componentes/EditTextMunicipios.php?CodigoPostal=" + CodigoPostalSpinner + "");
                            }
                        }, 200);
                    } else if (edUsuario.getText().toString().isEmpty()) {
                        edUsuario.setError("¡Hace falta un usuario!");
                    } else {
                        Toast.makeText(getApplicationContext(), "¡Faltan campos por llenar!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void MetodoPoblarCodigosPostales() {
        String url = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerCodigosPostales.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    MetodoCargarCodigosPostales(new String((responseBody)));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void MetodoCargarCodigosPostales(String respuesta) {
        ArrayList<AdquirirCodigosPostales> lista = new ArrayList<AdquirirCodigosPostales>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++) {
                AdquirirCodigosPostales cp = new AdquirirCodigosPostales();
                cp.setCodigoPostal(jsonArreglo.getJSONObject(i).getString("CodigoPostal"));
                lista.add(cp);
            }
            ArrayAdapter<AdquirirCodigosPostales> a = new ArrayAdapter<AdquirirCodigosPostales>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spCodigosPostales.setAdapter(a);
        } catch (Exception e) {
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
                        url = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerColonias.php?CodigoPostal=" + CodigoPostalSpinner + "&Municipio=" + MunicipioCodificado + "";
                        cliente.post(url, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if (statusCode == 200 && responseBody != null && responseBody.length > 0) {
                                    MetodoCargarMunicipiosYColonias(new String((responseBody)));
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            }
                        });
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Hubo un error, recarga.", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoCargarMunicipiosYColonias(String respuesta) {
        ArrayList<AdquirirColonias> lista = new ArrayList<AdquirirColonias>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++) {
                AdquirirColonias cn = new AdquirirColonias();
                cn.setColonia(jsonArreglo.getJSONObject(i).getString("Colonia"));
                lista.add(cn);
            }
            ArrayAdapter<AdquirirColonias> a = new ArrayAdapter<AdquirirColonias>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, lista);
            spColonias.setAdapter(a);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void MetodoBuscarPerfilUsuario(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        edUsuario.setText(jsonObject.getString("Usuario"));
                        edNombres.setText(jsonObject.getString("Nombre"));
                        edApellidoPaterno.setText(jsonObject.getString("ApePat"));
                        edApellidoMaterno.setText(jsonObject.getString("ApeMat"));
                        edFechaNacimiento.setText(jsonObject.getString("FechaNac"));
                        edCalle.setText(jsonObject.getString("Calle"));
                        edNumeroInterior.setText(jsonObject.getString("NumInt"));
                        edNumeroExterior.setText(jsonObject.getString("NumExt"));
                        edTelefonoPrincipal.setText(jsonObject.getString("Telefono1"));
                        edTelefonoSecundario.setText(jsonObject.getString("Telefono2"));
                        edCorreo.setText(jsonObject.getString("Correo"));
                        edContraseña.setText(jsonObject.getString("ContraseñaSimple"));
                        IdCodigoPostalBusqueda = jsonObject.getInt("IdCodigoPostal");
                        CodigoPostalBusqueda = jsonObject.getString("CodigoPostal");

                        JsonArrayRequest jsonArrayRequest1 = new JsonArrayRequest(ActivityAutentificacion.URLGeneral + "/Componentes/BuscarCodigosMunicipiosYColonias/BuscarGETCodigoPostalGet.php", new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
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
                                                    spCodigosPostales.setSelection(Valor);
                                                    Handler handler = new Handler(Looper.getMainLooper());
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            MetodoBuscarColonias();
                                                        }
                                                    }, 700);
                                                }
                                            }, 700);
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Usuario inexistente", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoBuscarColonias() {
        JsonArrayRequest jsonArrayRequest2 = new JsonArrayRequest(ActivityAutentificacion.URLGeneral + "/Componentes/BuscarCodigosMunicipiosYColonias/BuscarGETColoniaGET.php?CodigoPostal=" + CodigoPostalBusqueda + "&Municipio=" + MunicipioCodificado + "", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject2 = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject2 = response.getJSONObject(i);
                        if (jsonObject2.getInt("IdCodigoPostal") == IdCodigoPostalBusqueda) {
                            spColonias.setSelection(Integer.parseInt(jsonObject2.getString("id_temporal")) - 1);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Hubo un error, recarga.", Toast.LENGTH_SHORT).show();
                edApellidoMaterno.setText(error.getMessage().toString());
            }
        });
        requestQueue.add(jsonArrayRequest2);
    }

    private void MetodoActualizarPerfilUsuario(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent intent = new Intent(getApplicationContext(), ActivityMenuUsuarioCliente.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("IdUsuario", IdUsuarioActual);
                parametros.put("Nombre", edNombres.getText().toString());
                parametros.put("ApePat", edApellidoPaterno.getText().toString());
                parametros.put("ApeMat", edApellidoMaterno.getText().toString());
                parametros.put("FechaNac", edFechaNacimiento.getText().toString());
                parametros.put("Contrasena", edContraseña.getText().toString());
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

    private void MetodoRecuperarUsuarioActual() {
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        IdUsuarioActual = (preferences.getString("IdUsuario", ""));
    }
}
