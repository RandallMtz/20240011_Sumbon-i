package com.example.appmovdoggepediaversion2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ActivityRegUsuarioNuevoPart2 extends AppCompatActivity {

    private AsyncHttpClient cliente;
    private Button btFinalizarRegistro;
    private EditText edCalle, edContraseña, edMunicipio, edNumeroExterior, edNumeroInterior;
    private RequestQueue requestQueue;
    private Spinner spCodigosPostales, spColonias;


    private boolean ContraseñaVisible;
    private String ApellidoMaterno, ApellidoPaterno, CodigoPostal, Colonia, Correo, FechaNacimiento, IdCodigoPostalBusqueda, MunicipioCodificado, Nombres, TelefonoPrincipal, TelefonoSecundario, Usuario;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_usuario_nuevo_part2);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        cliente = new AsyncHttpClient();

        btFinalizarRegistro = findViewById(R.id.BtFinalizarRegistroUsuarioNuevoParte2);

        edCalle = findViewById((R.id.EditTCalleUsuarioNuevoParte2));
        edNumeroExterior = findViewById((R.id.EditTNumExteriorUsuarioNuevoParte2));
        edNumeroInterior = findViewById((R.id.EditTNumInteriorUsuarioNuevoParte2));
        edMunicipio = findViewById(R.id.EditTMunicipioUsuarioNuevoParte2);
        edContraseña = findViewById((R.id.EditTContraseñaUsuarioNuevoParte2));

        spCodigosPostales = findViewById(R.id.SpCodigoPostalUsuarioNuevoParte2);
        spColonias = findViewById(R.id.SpColoniaUsuarioNuevoParte2);

        ApellidoMaterno = getIntent().getStringExtra("ApeMat"); ApellidoPaterno = getIntent().getStringExtra("ApePat");
        Correo = getIntent().getStringExtra("Correo");
        FechaNacimiento = getIntent().getStringExtra("FechaNac");
        Nombres = getIntent().getStringExtra("Nombre");
        TelefonoPrincipal = getIntent().getStringExtra("Telefono"); TelefonoSecundario = getIntent().getStringExtra("Telefono2");
        Usuario = getIntent().getStringExtra("Usuario");

        MetodoPoblarCodigosPostales();

        edContraseña.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= edContraseña.getRight()-edContraseña.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = edContraseña.getSelectionEnd();
                        if (ContraseñaVisible) {
                            edContraseña.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_off_24,0);
                            edContraseña.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            ContraseñaVisible = false;
                        } else {
                            edContraseña.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_24,0);
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

        spCodigosPostales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CodigoPostal = adapterView.getSelectedItem()+"";
                MetodoPoblarMunicipiosYColonias(ActivityAutentificacion.URLGeneral + "/Componentes/EditTextMunicipios.php?CodigoPostal=" + CodigoPostal + "");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spColonias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Colonia = adapterView.getSelectedItem()+"";
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        btFinalizarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edCalle.getText().toString().isEmpty() && !edContraseña.getText().toString().isEmpty()) {
                    JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(ActivityAutentificacion.URLGeneral + "/Componentes/BuscarCodigosMunicipiosYColonias/InsertarPOSTBuscarCodigoPostalGET.php?CodigoPostal=" + CodigoPostal + "&Municipio=" + MunicipioCodificado + "&Colonia=" + MetodoCodificarTexto(Colonia) + "", new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jsonObject = null;
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    jsonObject = response.getJSONObject(i);
                                    IdCodigoPostalBusqueda = jsonObject.getInt("IdCodigoPostal") + "";
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
                            MetodoInsertarDireccionYTelefono(ActivityAutentificacion.URLGeneral + "/InsertarUsuariosNuevos/InsertarUsuariosNuevosUltimaPartePOST.php");
                        }
                    }, 1000);
                } else {
                    Toast.makeText(getApplicationContext(), "¡Faltan campos por llenar!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                        url = ActivityAutentificacion.URLGeneral + "/Componentes/SpinnerColonias.php?CodigoPostal=" + CodigoPostal +"&Municipio=" + MunicipioCodificado + "";
                        cliente.post(url, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                if (statusCode == 200 && responseBody != null && responseBody.length > 0){MetodoCargarMunicipiosYColonias(new String((responseBody)));}
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
                Toast.makeText(getApplicationContext(), "Evento inexistente", Toast.LENGTH_SHORT).show();
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

    public void MetodoInsertarDireccionYTelefono (String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                edCalle.setText("");
                spCodigosPostales.setSelection(0);
                edContraseña.setText("");
                edNumeroExterior.setText(""); edNumeroInterior.setText("");
                Toast.makeText(getApplicationContext(),"¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ActivityAutentificacion.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("Usuario", Usuario);
                parametros.put("Nombre", Nombres);
                parametros.put("ApePat", ApellidoPaterno);
                parametros.put("ApeMat", ApellidoMaterno);
                parametros.put("FechaNac", FechaNacimiento);
                parametros.put("Correo", Correo);
                parametros.put("Contrasena", edContraseña.getText().toString());
                parametros.put("Calle", edCalle.getText().toString());
                parametros.put("NumInt", edNumeroInterior.getText().toString());
                parametros.put("NumExt", edNumeroExterior.getText().toString());
                parametros.put("IdCodigoPostal", IdCodigoPostalBusqueda);
                parametros.put("Telefono1", TelefonoPrincipal);
                parametros.put("Telefono2", TelefonoSecundario);
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    public String MetodoCodificarTexto (String texto) {
        try {
            return URLEncoder.encode(texto, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityRegUsuarioNuevoPart1.class);
        intent.putExtra("ApeMat", ApellidoMaterno);
        intent.putExtra("ApePat", ApellidoPaterno);
        intent.putExtra("Correo", Correo);
        intent.putExtra("FechaNac", FechaNacimiento);
        intent.putExtra("Nombre", Nombres);
        intent.putExtra("Telefono", TelefonoPrincipal);
        intent.putExtra("Telefono2", TelefonoSecundario);
        intent.putExtra("Usuario", Usuario);
        startActivity(intent);
        finish();
    }
}