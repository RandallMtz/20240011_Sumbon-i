package com.example.appmovdoggepediaversion2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ActivitySubirSeguimiento extends ActivityBasic {

    Bitmap bitmap;
    private Button btElegirPrimero, btElegirSegundo, btElegirTercero, btSubirPrimero, btSubirSegundo, btSubirTercero;
    private ImageView FotografiaUno, FotografiaDos, FotografiaTres;
    private RequestQueue requestQueue;
    private String CodigoAdopcionActivityAnterior;

    private static final int PICK_IMAGE_REQUEST1 = 1, PICK_IMAGE_REQUEST2 = 2, PICK_IMAGE_REQUEST3 = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_seguimiento);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        Intent intent = getIntent();
        CodigoAdopcionActivityAnterior = intent.getExtras().getString("CodigoAdopcion");

        btElegirPrimero = findViewById(R.id.BtElegirPrimerSeguimiento);
        btElegirSegundo = findViewById(R.id.BtElegirSegundoSeguimiento);
        btElegirTercero = findViewById(R.id.BtElegirTercerSeguimiento);
        btSubirPrimero = findViewById(R.id.BtPrimerSeguimiento);
        btSubirSegundo = findViewById(R.id.BtSegundoSeguimiento);
        btSubirTercero = findViewById(R.id.BtTercerSeguimiento);

        FotografiaUno = findViewById(R.id.PrimerSeguimientoImage);
        FotografiaDos = findViewById(R.id.SegundoSeguimientoImage);
        FotografiaTres = findViewById(R.id.TercerSeguimientoImage);

        MetodoBuscarSeguimiento(ActivityAutentificacion.URLGeneral + "/OperacionesAdopciones/BuscarImagenesSeguimientoGET.php?CodigoAdopcion=" + MetodoCodificarTexto(CodigoAdopcionActivityAnterior) + "");

        btElegirPrimero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser(PICK_IMAGE_REQUEST1);
            }
        });

        btSubirPrimero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoSubirSeguimiento("https://randallmtzpz.000webhostapp.com/AplicacionMovilDoggepedia/OperacionesAdopciones/SubirSeguimiento1POST.php");
            }
        });

        btElegirSegundo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser(PICK_IMAGE_REQUEST2);
            }
        });

        btSubirSegundo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoSubirSeguimiento("https://randallmtzpz.000webhostapp.com/AplicacionMovilDoggepedia/OperacionesAdopciones/SubirSeguimiento2POST.php");
            }
        });

        btElegirTercero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser(PICK_IMAGE_REQUEST3);
            }
        });

        btSubirTercero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodoSubirSeguimiento("https://randallmtzpz.000webhostapp.com/AplicacionMovilDoggepedia/OperacionesAdopciones/SubirSeguimiento3POST.php");
            }
        });
    }

    private void MetodoBuscarSeguimiento (String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        if (!jsonObject.getString("FotografiaUno").equals("Inexistente")){
                            btElegirPrimero.setEnabled(false);
                            btSubirPrimero.setEnabled(false);
                        }
                        if (!jsonObject.getString("FotografiaDos").equals("Inexistente")){
                            btElegirSegundo.setEnabled(false);
                            btSubirSegundo.setEnabled(false);
                        }
                        if (!jsonObject.getString("FotografiaTres").equals("Inexistente")){
                            btElegirTercero.setEnabled(false);
                            btSubirTercero.setEnabled(false);
                        }
                        Glide.with(getApplicationContext())
                                .load(jsonObject.getString("FotografiaUno") + "?timestamp=" + System.currentTimeMillis())
                                .into(FotografiaUno);
                        Glide.with(getApplicationContext())
                                .load(jsonObject.getString("FotografiaDos") + "?timestamp=" + System.currentTimeMillis())
                                .into(FotografiaDos);
                        Glide.with(getApplicationContext())
                                .load(jsonObject.getString("FotografiaTres") + "?timestamp=" + System.currentTimeMillis())
                                .into(FotografiaTres);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {}
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoSubirSeguimiento (String url){
        final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "¡Carga exitosa!", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("CodigoAdopcion", CodigoAdopcionActivityAnterior);
                parametros.put("Fotografia", getStringImagen(bitmap));
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void showFileChooser(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleciona imagen"), requestCode);
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
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                switch (requestCode) {
                    case PICK_IMAGE_REQUEST1:
                        FotografiaUno.setImageBitmap(bitmap);
                        break;
                    case PICK_IMAGE_REQUEST2:
                        FotografiaDos.setImageBitmap(bitmap);
                        break;
                    case PICK_IMAGE_REQUEST3:
                        FotografiaTres.setImageBitmap(bitmap);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}