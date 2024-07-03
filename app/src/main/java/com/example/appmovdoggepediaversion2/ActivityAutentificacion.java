package com.example.appmovdoggepediaversion2;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityAutentificacion extends AppCompatActivity {

    private Button btIniciarSesion;
    private CheckBox chMantenerSesion;
    private EditText edContraseña, edUsuario;
    private FloatingActionButton btFloatContactos, btFloatFacebook, btFloatInstagram, btFloatLeyesOnline;
    private RequestQueue requestQueue;
    private TextView lbNivelAdministracion, lbRecuperarContraseña, lbRegistrarse;

    private boolean ContraseñaVisible;
    private String ContraseñaAyuda, IdNivelAdministracionAyuda, UsuarioAyuda;
    public static String CorreoUsu = "", DireccionUsu = "", IdInst = "", IdUsu = "0", NivelAdmin = "", NombreUsu = "", TelefonoUsu = "", URLGeneral;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autentificacion);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        IdUsu = "0"; NivelAdmin = "";
        URLGeneral = getApplicationContext().getString(R.string.URLBase);

        edContraseña = findViewById(R.id.EditTContraseñaAutentificacion); edUsuario = findViewById(R.id.EditTUsuarioAutentificacion);

        btFloatContactos = findViewById(R.id.fabBotonFlotante3); btFloatFacebook = findViewById(R.id.fabBotonFlotante1);
        btFloatInstagram = findViewById(R.id.fabBotonFlotante2); btFloatLeyesOnline = findViewById(R.id.fabBotonFlotante0);

        btIniciarSesion = (Button) findViewById(R.id.ButtonIniciarSesion);

        chMantenerSesion = findViewById(R.id.checkBoxGuardarContrasena);

        lbNivelAdministracion = findViewById(R.id.TextVNivelAdministrativoAutentificacion);
        lbRecuperarContraseña = findViewById(R.id.TextVRecuperarContraseñaAutentificacion); lbRegistrarse = findViewById(R.id.TextVRegistrarseAutentificacion);

        MetodoRecuperarUsuarioYContraseña();

        edContraseña.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right = 2;
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (motionEvent.getRawX() >= edContraseña.getRight()-edContraseña.getCompoundDrawables()[Right].getBounds().width()) {
                        int selection = edContraseña.getSelectionEnd();
                        if (ContraseñaVisible) {
                            edContraseña.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24,0,R.drawable.baseline_visibility_off_24,0);
                            edContraseña.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            ContraseñaVisible = false;
                        } else {
                            edContraseña.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_lock_24,0,R.drawable.baseline_visibility_24,0);
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

        btIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UsuarioAyuda = edUsuario.getText().toString();
                ContraseñaAyuda = edContraseña.getText().toString();
                if (!edUsuario.getText().toString().isEmpty() && !edContraseña.getText().toString().isEmpty()){
                    if (chMantenerSesion.isChecked()){
                        MetodoAutentificacionPOST(URLGeneral + "/AutentificacionPOSTSesiones.php");
                    } else {
                        MetodoAutentificacionPOST(URLGeneral + "/AutentificacionPOST.php");
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"¡Introduce tu usuario y contraseña!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        lbRecuperarContraseña.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityRecuperarContrasena.class);
                startActivity(intent);
                finish();
            }
        });

        lbRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityRegUsuarioNuevoPart1.class);
                startActivity(intent);
                finish();
            }
        });

        btFloatContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ActivityListadoAlianzas.class);
                startActivity(i);
            }
        });

        btFloatLeyesOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.ordenjuridico.gob.mx/Documentos/Estatal/Puebla/wo96652.pdf";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        btFloatFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://www.facebook.com/EPYBTexmelucan";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        btFloatInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*String url = "https://www.instagram.com/epybtexmelucan/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));*/
                Intent intent = new Intent(getApplicationContext(), ActivityListadoCasosExitosos.class);
                startActivity(intent);
            }
        });
    }

    private void MetodoAutentificacionPOST(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d(TAG, "Response: " + response.toString());
                    boolean guardarContrasena = chMantenerSesion.isChecked();
                    CorreoUsu = jsonObject.getString("Correo");
                    DireccionUsu = jsonObject.getString("Calle") + " " + jsonObject.getString("NumInt") + " " + jsonObject.getString("NumExt");
                    lbNivelAdministracion.setText(jsonObject.getString("IdNivel"));
                    IdInst = jsonObject.getString("IdInstitucion");
                    IdNivelAdministracionAyuda = lbNivelAdministracion.getText().toString();
                    NombreUsu = jsonObject.getString("Nombre") + " " + jsonObject.getString("ApePat") + " " + jsonObject.getString("ApeMat");
                    TelefonoUsu = jsonObject.getString("Telefono1");
                    if (lbNivelAdministracion.getText().equals("4")) {
                        Intent intent = new Intent(getApplicationContext(), ActivityMenuUsuarioAdministrativo.class);
                        IdUsu = jsonObject.getString("IdUsuario"); NivelAdmin = "Desarrollador";
                        if (guardarContrasena){
                            MetodoGuardarUsuarioYContraseña(edUsuario.getText().toString(),edContraseña.getText().toString(), guardarContrasena);
                        } else {
                            MetodoGuardarUsuarioYContraseña("","", false);
                        }
                        startActivity(intent);
                        finish();
                    }
                    if (lbNivelAdministracion.getText().equals("3")) {
                        Intent intent = new Intent(getApplicationContext(), ActivityMenuUsuarioAdministrativo.class);
                        IdUsu = jsonObject.getString("IdUsuario"); NivelAdmin = "Administrativo";
                        if (guardarContrasena){
                            MetodoGuardarUsuarioYContraseña(edUsuario.getText().toString(),edContraseña.getText().toString(), guardarContrasena);
                        } else {
                            MetodoGuardarUsuarioYContraseña("","", false);
                        }
                        startActivity(intent);
                        finish();
                    }
                    if (lbNivelAdministracion.getText().equals("2")) {
                        Intent intent = new Intent(getApplicationContext(), ActivityMenuUsuarioEmpleado.class);
                        IdUsu = jsonObject.getString("IdUsuario"); NivelAdmin = "Empleado";
                        if (guardarContrasena){
                            MetodoGuardarUsuarioYContraseña(edUsuario.getText().toString(),edContraseña.getText().toString(), guardarContrasena);
                        } else {
                            MetodoGuardarUsuarioYContraseña("","",false);
                        }
                        startActivity(intent);
                        finish();
                    }
                    if (lbNivelAdministracion.getText().equals("1")) {
                        Intent intent = new Intent(getApplicationContext(), ActivityMenuUsuarioCliente.class);
                        IdUsu = jsonObject.getString("IdUsuario"); NivelAdmin = "Generico";
                        if (guardarContrasena){
                            MetodoGuardarUsuarioYContraseña(edUsuario.getText().toString(),edContraseña.getText().toString(), guardarContrasena);
                        } else {
                            MetodoGuardarUsuarioYContraseña("","", false);
                        }
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "" + response.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "¡Revisa tu conexión!", Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("Usuario", edUsuario.getText().toString());
                parametros.put("Contrasena", edContraseña.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void MetodoGuardarUsuarioYContraseña(String Usuario1, String Contraseña1, boolean GuardarContra){
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Usuario", Usuario1);
        editor.putString("Contrasena", Contraseña1);
        editor.putBoolean("GuardarContra", GuardarContra);
        editor.putString("IdUsuario", IdUsu);
        editor.putString("IdNivel", IdNivelAdministracionAyuda);
        editor.putString("IdInstitucion", IdInst);
        editor.putBoolean("sesion", true);
        editor.commit();
    }

    private void MetodoRecuperarUsuarioYContraseña(){
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        edUsuario.setText(preferences.getString("Usuario",""));
        edContraseña.setText(preferences.getString("Contrasena",""));
        chMantenerSesion.setChecked(preferences.getBoolean("GuardarContra",true));
        if (preferences.getBoolean("GuardarContra", true)){
            if (preferences.getString("IdNivel", "4").equals("4")){
                Intent intent = new Intent(getApplicationContext(), ActivityMenuUsuarioAdministrativo.class);
                NivelAdmin = "Desarrollador";
                startActivity(intent);
                finish();
            }
            if (preferences.getString("IdNivel", "4").equals("3")){
                Intent intent = new Intent(getApplicationContext(), ActivityMenuUsuarioAdministrativo.class);
                NivelAdmin = "Administrativo";
                startActivity(intent);
                finish();
            }
            if (preferences.getString("IdNivel", "4").equals("2")){
                Intent intent = new Intent(getApplicationContext(), ActivityMenuUsuarioEmpleado.class);
                NivelAdmin = "Empleado";
                startActivity(intent);
                finish();
            }
            if (preferences.getString("IdNivel", "4").equals("1")){
                Intent intent = new Intent(getApplicationContext(), ActivityMenuUsuarioCliente.class);
                NivelAdmin = "Generico";
                startActivity(intent);
                finish();
            }
        }
    }
}