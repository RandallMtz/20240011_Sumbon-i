package com.example.appmovdoggepediaversion2;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ActivityRecuperarContrasena extends AppCompatActivity {

    private Button btEnviarCorreo;
    private EditText edCorreoElectronico;
    private RequestQueue requestQueue;
    private Session session;
    
    private String CorreoAyuda, ContraseñaAyuda, ContraseñaBusqueda, NombreBusqueda, ApellidoPaternoBusqueda, ApellidoMaternoBusqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contrasena);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        btEnviarCorreo = findViewById(R.id.BtEnviarCorreoRecuperarContraseña);

        CorreoAyuda = "jose.randall.principal.360@gmail.com";
        ContraseñaAyuda = "idjg yogd yvtc zssh";

        edCorreoElectronico = findViewById(R.id.EditTCorreoRecuperarContraseña);

        btEnviarCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MetodoVerificarExistenciaCorreo(ActivityAutentificacion.URLGeneral + "/RecuperacionContrasena.php?Correo=" + edCorreoElectronico.getText().toString());
            }
        });
    }

    private void MetodoVerificarExistenciaCorreo (String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        NombreBusqueda = jsonObject.getString("Nombre");
                        ApellidoPaternoBusqueda = jsonObject.getString("ApePat");
                        ApellidoMaternoBusqueda = jsonObject.getString("ApeMat");
                        ContraseñaBusqueda = jsonObject.getString("Contraseña");

                        if (!jsonObject.getString("Correo").equals("")) {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                            Properties properties = new Properties();
                            properties.put("mail.smtp.host", "smtp.googlemail.com");
                            properties.put("mail.smtp.socketFactory.port", "465");
                            properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                            properties.put("mail.smtp.auth", "true");
                            properties.put("mail.smtp.port", "465");
                            try {
                                session = javax.mail.Session.getDefaultInstance(properties, new Authenticator() {
                                    @Override
                                    protected PasswordAuthentication getPasswordAuthentication() {
                                        return new PasswordAuthentication(CorreoAyuda, ContraseñaAyuda);
                                    }
                                });

                                if (session != null) {
                                    Message message = new MimeMessage(session);
                                    message.setFrom(new InternetAddress(CorreoAyuda));
                                    message.setSubject("Recuperación de contraseña");
                                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(edCorreoElectronico.getText().toString()));
                                    message.setContent("Desde el equipo de Doggepedia en asociación con En Busca de la Paz y el Bien A.C.\n\n" +
                                            "Estimado " + NombreBusqueda + " " + ApellidoPaternoBusqueda + " " + ApellidoMaternoBusqueda + " \n\n" +
                                            "Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en nuestra aplicación de adopción de animales. \n" +
                                            "Conforme a lo solicitado, te enviamos la nueva contraseña de tu cuenta, que podrás utilizar para acceder a la aplicación:\n\n" +
                                            "Nueva contraseña: " + ContraseñaBusqueda + " \n\n" +
                                            "Por favor, asegúrate de cambiar tu contraseña inmediatamente después de iniciar sesión por primera vez. \n" +
                                            "Si no solicitaste un restablecimiento de contraseña, ponte en contacto con nosotros inmediatamente en jose.randall.principal.360@gmail.com", "text/plain");
                                    Transport.send(message);
                                    Toast.makeText(getApplicationContext(), "Correo enviado", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), ActivityAutentificacion.class);
                                    startActivity(intent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "¡Correo no vinculado!", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(getApplicationContext(), ActivityAutentificacion.class);
        startActivity(intent);
        finish();
    }
}