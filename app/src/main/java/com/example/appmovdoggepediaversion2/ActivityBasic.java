package com.example.appmovdoggepediaversion2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Patterns;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ActivityBasic extends AppCompatActivity {

    private CountDownTimer TemporizadorInactividad;
    private long TiempoUltimaActividad = 0;
    private String CuentaActual = "";
    private static final long TiempoLimiteInactivo = 1 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MetodoRecuperarPreferencias();
    }

    @Override
    protected void onResume() {
        super.onResume();
        iniciarTemporizadorInactividad();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelarTemporizadorInactividad();
        TiempoUltimaActividad = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TiempoUltimaActividad = System.currentTimeMillis();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        long tiempoInactividad = System.currentTimeMillis() - TiempoUltimaActividad;
        if (tiempoInactividad > TiempoLimiteInactivo) {
            cerrarSesionPorInactividad();
        } else {
            iniciarTemporizadorInactividad();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        reiniciarTemporizadorInactividad();
        return super.dispatchTouchEvent(event);
    }

    private void iniciarTemporizadorInactividad() {
        cancelarTemporizadorInactividad();
        TemporizadorInactividad = new CountDownTimer(TiempoLimiteInactivo, TiempoLimiteInactivo) {
            @Override
            public void onTick(long millisUntilFinished) {}
            @Override
            public void onFinish() {
                cerrarSesionPorInactividad();
            }
        }.start();
    }

    private void cancelarTemporizadorInactividad() {
        if (TemporizadorInactividad != null) {TemporizadorInactividad.cancel();}
    }

    private void reiniciarTemporizadorInactividad() {
        cancelarTemporizadorInactividad();
        iniciarTemporizadorInactividad();
    }

    private void cerrarSesionPorInactividad() {
        queryCerrarSesionPOST("https://randallmtzpz.000webhostapp.com/AplicacionMovilDoggepedia/CerrarSesionPOST.php");
        MetodoGuardarUsuarioYContraseña();
        Intent intent = new Intent(getApplicationContext(), ActivityAutentificacion.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void queryCerrarSesionPOST(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "" + response.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("Usuario", CuentaActual);
                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void MetodoGuardarUsuarioYContraseña(){
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Usuario", "");
        editor.putString("Contrasena","");
        editor.putString("IdNivel", "");
        editor.putBoolean("sesion", true);
        editor.commit();
    }

    private void MetodoRecuperarPreferencias(){
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        CuentaActual = (preferences.getString("Usuario",""));
    }

    public String MetodoCodificarTexto (String texto) {
        try {
            return URLEncoder.encode(texto, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean MetodoValidarCorreo (EditText email){
        if (email.getText().toString().isEmpty()){
            email.setError("¡Debes llenar este campo!");
            return false;
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            email.setError("¡Se necesita un correo valido!");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }
}