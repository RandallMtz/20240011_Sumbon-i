package com.example.appmovdoggepediaversion2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;

import com.example.appmovdoggepediaversion2.databinding.ActivityMenuUsuarioClienteBinding;

import java.util.HashMap;
import java.util.Map;

public class ActivityMenuUsuarioCliente extends ActivityBasic {

    ActivityMenuUsuarioClienteBinding binding;
    private AsyncHttpClient cliente;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuUsuarioClienteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();
        
        cliente = new AsyncHttpClient();

        MetodoCancelarAdopcion(ActivityAutentificacion.URLGeneral + "/OperacionesAdopciones/CancelarProceso.php");

        MetodoRemplazarFragmento(new FragmentListadosCliente());
        binding.bottomNavigationViewGenerico.setBackground(null);

        binding.bottomNavigationViewGenerico.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.Galerias) {
                MetodoRemplazarFragmento(new FragmentListadosCliente());
            } else if (item.getItemId() == R.id.Perfil) {
               MetodoRemplazarFragmento(new FragmentMiPerfilCliente());
            }
            return true;
        });
    }

    private void MetodoRemplazarFragmento(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_generico, fragment);
        fragmentTransaction.commit();
    }

    private void MetodoCancelarAdopcion(String url){
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
                parametros.put("IdUsuario", ActivityAutentificacion.IdUsu);
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityAutentificacion.class);
        startActivity(intent);
        finish();
    }
}