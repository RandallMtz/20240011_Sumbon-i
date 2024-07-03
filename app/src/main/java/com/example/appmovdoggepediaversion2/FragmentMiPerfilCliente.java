package com.example.appmovdoggepediaversion2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FragmentMiPerfilCliente extends Fragment {

    private AlertDialog.Builder builder;
    private AsyncHttpClient cliente;
    private Button btEliminar, btModificar;
    private RequestQueue requestQueue;
    private TextView lbApePat, lbCalle, lbColonia, lbCorreo, lbFechaNac, lbMunicipio, lbNombre, lbNuExt, lbNuInt, lbTelefono, lbTelefono2, lbUsuario;

    private String IdUsuarioAuxiliar;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mi_perfil_cliente, container, false);

        MySingleton singleton = MySingleton.getInstance(getContext());
        requestQueue = singleton.getRequestQueue();

        cliente = new AsyncHttpClient();

        btEliminar = view.findViewById(R.id.buttonEliminarPerfilGenerico); btModificar = view.findViewById(R.id.buttonModificarPerfilGenerico);

        builder = new AlertDialog.Builder(getContext());

        lbApePat = view.findViewById(R.id.TextVApePatPerfilGenerico);
        lbCalle = view.findViewById(R.id.TextVCallePerfilGenerico);
        lbColonia = view.findViewById(R.id.TextVColoniaPerfilGenerico);
        lbCorreo = view.findViewById(R.id.TextVCorreoPerfilGenerico);
        lbFechaNac = view.findViewById(R.id.TextVFechaNacimientoPerfilGenerico);
        lbNombre = view.findViewById(R.id.TextVNombrePerfilGenerico);
        lbMunicipio = view.findViewById(R.id.TextVMunicipioPerfilGenerico);
        lbNuExt = view.findViewById(R.id.TextVNuExtPerfilGenerico); lbNuInt = view.findViewById(R.id.TextVNuIntPerfilGenerico);
        lbTelefono = view.findViewById(R.id.TextVTelefonoPrincipalPerfilGenerico); lbTelefono2 = view.findViewById(R.id.TextVTelefonoSecundarioPerfilGenerico);
        lbUsuario = view.findViewById(R.id.TextVUsuarioPerfilGenerico);

        MetodoRecuperarIdUsuario();

        MetodoBuscarInformacionPerfil(ActivityAutentificacion.URLGeneral + "/PerfilGenerico/BuscarGET.php?IdUsuario=" + IdUsuarioAuxiliar);

        btModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityModificarPerfilGenerico.class);
                intent.putExtra("IdUsuario", IdUsuarioAuxiliar);
                startActivity(intent);
            }
        });

        btEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setMessage("¿Deseas eliminar tu cuenta?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MetodoEliminarPerfil(ActivityAutentificacion.URLGeneral + "/PerfilGenerico/EliminarPOST.php");
                                Intent intent = new Intent(getContext(), ActivityAutentificacion.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        });
        
        return view;
    }

    private void MetodoBuscarInformacionPerfil (String url){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        lbUsuario.setText(jsonObject.getString("Usuario"));
                        lbNombre.setText(jsonObject.getString("Nombre"));
                        lbApePat.setText(jsonObject.getString("ApePat") + " " + jsonObject.getString("ApeMat"));
                        lbFechaNac.setText(jsonObject.getString("FechaNac"));
                        lbCalle.setText(jsonObject.getString("Calle"));
                        lbNuInt.setText(jsonObject.getString("NumInt"));
                        lbNuExt.setText(jsonObject.getString("NumExt"));
                        lbColonia.setText(jsonObject.getString("Colonia"));
                        lbMunicipio.setText(jsonObject.getString("Municipio"));
                        lbTelefono.setText(jsonObject.getString("Telefono1"));
                        lbTelefono2.setText(jsonObject.getString("Telefono2"));
                        lbCorreo.setText(jsonObject.getString("Correo"));
                    } catch (JSONException e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Usuario inexistente", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void MetodoEliminarPerfil(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), "¡Operación exitosa!", Toast.LENGTH_SHORT).show();
                MetodoGuardarUsuarioYContraseña();
                Intent i = new Intent(getContext(), ActivityAutentificacion.class);
                startActivity(i);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),error.toString(), Toast.LENGTH_LONG).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> parametros = new HashMap<String,String>();
                parametros.put("IdUsuario", IdUsuarioAuxiliar);
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void MetodoGuardarUsuarioYContraseña(){
        SharedPreferences preferences = requireContext().getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("Usuario", "");
        editor.putString("Contrasena", "");
        editor.putString("IdNivel", "");
        editor.putBoolean("sesion", true);
        editor.commit();
    }

    private void MetodoRecuperarIdUsuario(){
        SharedPreferences preferences = getActivity().getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        IdUsuarioAuxiliar = (preferences.getString("IdUsuario",""));
    }
}