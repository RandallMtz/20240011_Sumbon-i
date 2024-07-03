package com.example.appmovdoggepediaversion2;

import androidx.appcompat.app.AlertDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ActivityInformacionDelAnimal extends ActivityBasic {

    private Button btIniciarProcesoAdopcion;
    private ImageView imImagen;
    private RequestQueue requestQueue;
    private TextView lbCondicion, lbEdad, lbEspecie, lbNombre,  lbRaza, lbSexo, lbTamaño;

    private String CodigoAnimal, FechaActual, IdInstitucionBusqueda, IdUsuarioActual;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_del_animal);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        btIniciarProcesoAdopcion = findViewById(R.id.ButtonComenzarDetallesAnimal);

        imImagen = findViewById(R.id.ImgVFotografiaDetallesAnimal);

        lbCondicion = findViewById(R.id.TextVCondicionDetallesAnimal);
        lbEdad = findViewById(R.id.TextVEdadDetallesAnimal);
        lbEspecie = findViewById(R.id.TextVEspecieDetallesAnimal);
        lbNombre = findViewById(R.id.TextVNombreDetallesAnimal);
        lbRaza = findViewById(R.id.TextVRazaDetallesAnimal);
        lbSexo = findViewById(R.id.TextVSexoDetallesAnimal);
        lbTamaño = findViewById(R.id.TextVTamañoDetallesAnimal);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        FechaActual = dateFormat.format(calendar.getTime());

        Intent intent = getIntent();
        CodigoAnimal = intent.getExtras().getString("CodigoAnimal");

        MetodoBuscarAnimal(ActivityAutentificacion.URLGeneral + "/OperacionesAnimales/GlobalBuscarDetallesGET.php?CodigoAnimal=" + CodigoAnimal + " ");
        MetodoRecuperarIdUsuario();

        btIniciarProcesoAdopcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoMostrarInformacionPreviaAdopcion();
            }
        });
    }

    private void MetodoBuscarAnimal (String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        lbNombre.setText(jsonObject.getString("Nombre"));
                        lbEspecie.setText(jsonObject.getString("Especie"));
                        if (jsonObject.getString("Especie").equals("Canino")){
                            lbRaza.setText(jsonObject.getString("RazaCan"));
                        } else if (jsonObject.getString("Especie").equals("Minino")) {
                            lbRaza.setText(jsonObject.getString("RazaMin"));
                        }
                        lbTamaño.setText(jsonObject.getString("Tamaño"));
                        lbCondicion.setText(jsonObject.getString("Condicion"));
                        lbSexo.setText(jsonObject.getString("Sexo"));
                        lbEdad.setText(jsonObject.getString("Edad"));
                        IdInstitucionBusqueda = jsonObject.getString("IdInstitucion");
                        Glide.with(getApplicationContext())
                                .load(jsonObject.getString("Fotografia") + "?timestamp=" + System.currentTimeMillis())
                                .into(imImagen);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoMostrarInformacionPreviaAdopcion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialogo_adopcion, null);

        TextView textViewInformacion = dialogView.findViewById(R.id.textViewInformacionAntesAdopcion);
        String textoInformacion = "Debe tener en cuenta que después de esto, el animal entrara en un proceso de adopción. <br><br>" +
                "Proceso en el que deberá asistir a la asociación en los próximos 5 días, presentando los siguientes documentos: <br><br>" +
                "&nbsp;&nbsp;1. Identificación oficial <br>" +
                "&nbsp;&nbsp;2. Comprobante de domicilio <br>" +
                "&nbsp;&nbsp;3. Formato de adopción <br><br>" +
                "Es obligatorio presentar estos documento, de lo contrario se negará la adopción. <br><br>" +
                "De igual forma, si no se presenta en los 5 días mencionados, el animal volverá a ser puesto en adopción automáticamente.<br><br>" +
                "Además, posteriormente a finalizar la adopción, usted deberá subir fotografías sobre el estado del animal, de no llevarse a cabo este proceso se le notificará vía correo electrónico la acción requerida, esto con la finalidad de llevar un seguimiento de la adopción.";
        textViewInformacion.setText(Html.fromHtml(textoInformacion));

        builder.setView(dialogView)
                .setTitle("Antes de continuar...")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MetodoInsertarAdopcion(ActivityAutentificacion.URLGeneral + "/OperacionesAdopciones/InsertarAdopcionProceso.php");
                        Uri uri = Uri.parse(ActivityAutentificacion.URLGeneral + "/OperacionesAdopciones/AcuerdoAdopcion.php?IdUsuario=" + IdUsuarioActual + "");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/pdf");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void MetodoInsertarAdopcion(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {}
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CodigoAnimal", CodigoAnimal.toString());
                parametros.put("IdUsuario", IdUsuarioActual);
                parametros.put("Fecha", FechaActual);
                parametros.put("IdInstitucion", IdInstitucionBusqueda);
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void MetodoRecuperarIdUsuario(){
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        IdUsuarioActual = (preferences.getString("IdUsuario",""));
    }
}