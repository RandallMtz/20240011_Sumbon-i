package com.example.appmovdoggepediaversion2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdaptadorAdopcionesRevision extends RecyclerView.Adapter<AdaptadorAdopcionesRevision.AdopcionViewHolder> {

    private Context mCtx;
    private List<AdquirirAdopciones> AdopcionesList;
    private RequestQueue requestQueue;

    public AdaptadorAdopcionesRevision(Context mCtx, List<AdquirirAdopciones> adopciones) {
        this.mCtx = mCtx;
        this.AdopcionesList = adopciones;

        MySingleton singleton = MySingleton.getInstance(mCtx.getApplicationContext());
        requestQueue = singleton.getRequestQueue();
    }

    @NonNull
    @Override
    public AdaptadorAdopcionesRevision.AdopcionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.custom_lista_adopciones_revision,null);
        return new AdopcionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorAdopcionesRevision.AdopcionViewHolder holder, int position) {
        AdquirirAdopciones Adopcion = AdopcionesList.get(position);

        holder.Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, ActivityAutentificacion.URLGeneral + "/OperacionesAdopciones/ConfirmarAdopcion.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(view.getContext(), "Adopcion completa. Recarga la pantalla.", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(view.getContext(),error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map <String, String> parametros = new HashMap<String,String>();
                        parametros.put("CodigoAdop", Adopcion.getCodigoAdop());
                        return parametros;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });

        holder.Denegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, ActivityAutentificacion.URLGeneral + "/OperacionesAdopciones/DenegarAdopcion.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(view.getContext(), "Adopcion denegada. Recarga la pantalla.", Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(view.getContext(),error.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map <String, String> parametros = new HashMap<String,String>();
                        parametros.put("CodigoAdop", Adopcion.getCodigoAdop());
                        return parametros;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });

        holder.Informacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(ActivityAutentificacion.URLGeneral + "/OperacionesAdopciones/InformacionAdopcion.php?ClaveAdop=" + MetodoCodificarTexto(Adopcion.getCodigoAdop()) + "");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                view.getContext().startActivity(intent);

            }
        });

        holder.Nombre.setText(Adopcion.getCodigoAdop());
    }

    @Override
    public int getItemCount() {
        return AdopcionesList.size();
    }

    class AdopcionViewHolder extends RecyclerView.ViewHolder {

        Button Aceptar, Denegar, Informacion;
        LinearLayout linearLayout;
        TextView Nombre;
        public AdopcionViewHolder(@NonNull View itemView) {
            super(itemView);

            Aceptar = itemView.findViewById(R.id.btSi); Denegar = itemView.findViewById(R.id.btNo);
            Informacion = itemView.findViewById(R.id.btInfo);
            linearLayout = itemView.findViewById(R.id.LinearLayoutBotones);
            Nombre = itemView.findViewById(R.id.TextVNombreListadoAdopcionesBotones);
        }
    }

    public String MetodoCodificarTexto (String texto) {
        try {
            return URLEncoder.encode(texto, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
