package com.example.appmovdoggepediaversion2;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

public class ActivityRegUsuarioNuevoPart1 extends AppCompatActivity {


    private Button btSiguientePag;
    private DatePickerDialog pcFechaNacimiento;
    private EditText edApellidoMaterno, edApellidoPaterno, edCorreo, edFechaNacimiento, edNombres, edTelefonoPrincipal, edTelefonoSecundario, edUsuario;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[@#$%^&+=])" +
                    "(?=\\S+$)" +
                    ".{6,}" +
                    "$");
    private RequestQueue requestQueue;
    private String ApellidoMaterno, ApellidoPaterno, Correo, FechaNacimiento, Nombres, TelefonoPrincipal, TelefonoSecundario, Usuario;
    private TextView lbValidarUsuarioInexistente;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_usuario_nuevo_part1);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        btSiguientePag = findViewById(R.id.BtSiguientePaginaUsuarioNuevoParte1);

        edApellidoMaterno = (EditText) findViewById((R.id.EditTApeMatUsuarioNuevoParte1)); edApellidoPaterno = (EditText) findViewById((R.id.EditTApePatUsuarioNuevoParte1));
        edCorreo = (EditText) findViewById((R.id.EditTCorreoUsuarioNuevoParte1));
        edFechaNacimiento = (EditText) findViewById((R.id.EditTFechaNacUsuarioNuevoParte1));
        edNombres = (EditText) findViewById((R.id.EditTNombresUsuarioNuevoParte1));
        edTelefonoPrincipal = (EditText) findViewById((R.id.EditT1erTelefonoUsuarioNuevoParte1)); edTelefonoSecundario = (EditText) findViewById((R.id.EditT2doTelefonoUsuarioNuevoParte1));
        edUsuario = (EditText) findViewById((R.id.EditTUsuarioUsuarioNuevoParte1));

        lbValidarUsuarioInexistente = findViewById(R.id.TextVApoyoUsuarioNuevoParte1);

        ApellidoMaterno = getIntent().getStringExtra("ApeMat"); ApellidoPaterno = getIntent().getStringExtra("ApePat");
        Correo = getIntent().getStringExtra("Correo");
        FechaNacimiento = getIntent().getStringExtra("FechaNac");
        Nombres = getIntent().getStringExtra("Nombre");
        TelefonoPrincipal = getIntent().getStringExtra("Telefono"); TelefonoSecundario = getIntent().getStringExtra("Telefono2");
        Usuario = getIntent().getStringExtra("Usuario");

        edApellidoMaterno.setText(ApellidoMaterno); edApellidoPaterno.setText(ApellidoPaterno);
        edCorreo.setText(Correo);
        edFechaNacimiento.setText(FechaNacimiento);
        edNombres.setText(Nombres);
        edTelefonoPrincipal.setText(TelefonoPrincipal); edTelefonoSecundario.setText(TelefonoSecundario);
        edUsuario.setText(Usuario);

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

                pcFechaNacimiento = new DatePickerDialog(ActivityRegUsuarioNuevoPart1.this, R.style.Theme_AppMovDoggepediaVersion2Dialogs, new DatePickerDialog.OnDateSetListener() {
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

        btSiguientePag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edUsuario.getText().toString().isEmpty() || edNombres.getText().toString().isEmpty() || edApellidoPaterno.getText().toString().isEmpty() || edApellidoMaterno.getText().toString().isEmpty() || edFechaNacimiento.getText().toString().isEmpty() || edTelefonoPrincipal.getText().toString().isEmpty() || edTelefonoSecundario.getText().toString().isEmpty() || edCorreo.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "¡Aun faltan campos por llenar!", Toast.LENGTH_SHORT).show();
                } else if (edTelefonoPrincipal.length() < 10){
                    edTelefonoPrincipal.setError("¡Ingresa un teléfono valido!");
                } else if (edTelefonoSecundario.length() < 10){
                    edTelefonoSecundario.setError("¡Ingresa un teléfono valido!");
                } else {
                    MetodoValidarUsuarioInexistente(ActivityAutentificacion.URLGeneral + "/InsertarUsuariosNuevos/ValidarUsuarioInexistente.php?Usuario=" + edUsuario.getText() + "");
                }
            }
        });
    }

    private void MetodoValidarUsuarioInexistente (String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        lbValidarUsuarioInexistente.setText(jsonObject.getString("Usuario"));
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
                    Usuario = edUsuario.getText().toString();
                    Nombres = edNombres.getText().toString();
                    ApellidoPaterno = edApellidoPaterno.getText().toString();
                    ApellidoMaterno = edApellidoMaterno.getText().toString();
                    FechaNacimiento = edFechaNacimiento.getText().toString();
                    TelefonoPrincipal = edTelefonoPrincipal.getText().toString();
                    TelefonoSecundario = edTelefonoSecundario.getText().toString();
                    Correo = edCorreo.getText().toString();
                    if (!Usuario.isEmpty() && !Nombres.isEmpty() && !ApellidoPaterno.isEmpty() && !ApellidoMaterno.isEmpty() && !FechaNacimiento.isEmpty() && !TelefonoPrincipal.isEmpty() && !TelefonoSecundario.isEmpty() && !Correo.isEmpty()) {
                        Intent intent = new Intent(getApplicationContext(), ActivityRegUsuarioNuevoPart2.class);
                        intent.putExtra("Usuario", Usuario);
                        intent.putExtra("Nombre", Nombres);
                        intent.putExtra("ApePat", ApellidoPaterno);
                        intent.putExtra("ApeMat", ApellidoMaterno);
                        intent.putExtra("FechaNac", FechaNacimiento);
                        intent.putExtra("Telefono", TelefonoPrincipal);
                        intent.putExtra("Telefono2", TelefonoSecundario);
                        intent.putExtra("Correo", Correo);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "¡Aun faltan campos por llenar!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private boolean MetodoValidarCorreo (EditText email){
        if(!Patterns.EMAIL_ADDRESS.matcher(edCorreo.getText().toString()).matches()) {
            edCorreo.setError("¡Se necesita un correo valido!");
            return false;
        } else {
            edCorreo.setError(null);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityAutentificacion.class);
        startActivity(intent);
        finish();
    }
}