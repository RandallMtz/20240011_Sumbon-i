package com.example.appmovdoggepediaversion2;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
public class ActivityDashBoard extends AppCompatActivity {

    ArrayList GraficaUnoList, GraficaDosList, GraficaTresList, GraficaCuatroList;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        MySingleton singleton = MySingleton.getInstance(getApplicationContext());
        requestQueue = singleton.getRequestQueue();

        MetodoConteoPorEspecie("https://randallmtzpz.000webhostapp.com/AplicacionMovilDoggepedia/Graficos/ConteoPorEspecie.php");
        MetodoConteoPorSexo("https://randallmtzpz.000webhostapp.com/AplicacionMovilDoggepedia/Graficos/ConteoPorSexo.php");
        MetodoConteoPorCondicion("https://randallmtzpz.000webhostapp.com/AplicacionMovilDoggepedia/Graficos/ConteoPorCondicion.php");
        MetodoConteoPorInstitucion("https://randallmtzpz.000webhostapp.com/AplicacionMovilDoggepedia/Graficos/ConteoPorInstitucion.php");
    }

    private void MetodoConteoPorEspecie(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        GraficaUnoList = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                float mininos = Float.parseFloat(jsonObject.getString("Mininos"));
                                float caninos = Float.parseFloat(jsonObject.getString("Caninos"));
                                GraficaUnoList.add(new BarEntry(1f, mininos));
                                GraficaUnoList.add(new BarEntry(0f, caninos));
                            }
                            CrearGraficoConteoPorEspecies(GraficaUnoList);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Hubo un error", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    public void CrearGraficoConteoPorEspecies (ArrayList<BarEntry> barEntries) {
        BarChart barChart = findViewById(R.id.GraficoPorEspecie);

        final String[] labels = new String[]{"Caninos", "Mininos"};

        BarDataSet barDataSet = new BarDataSet(barEntries, "Especies");
        BarData barData = new BarData(barDataSet);
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.invalidate();
    }

    private void MetodoConteoPorSexo(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        GraficaDosList = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                GraficaDosList.add(new PieEntry(Float.parseFloat(jsonObject.getString("Hembras")), "Hembras"));
                                GraficaDosList.add(new PieEntry(Float.parseFloat(jsonObject.getString("Machos")), "Machos"));
                            }
                            CrearGraficoConteoPorSexo(GraficaDosList);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Hubo un error", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    public void CrearGraficoConteoPorSexo (ArrayList<PieEntry> pieEntris) {
        PieChart pieChart = findViewById(R.id.GraficoPorSexo);

        PieDataSet pieDataSet = new PieDataSet(pieEntris, "");
        PieData pieData = new PieData(pieDataSet);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(12f);
        pieChart.setUsePercentValues(false);
        pieChart.setCenterText("Sexos");
        pieChart.getDescription().setEnabled(false);
        pieChart.animateX(1000);
        pieChart.setData(pieData);

        pieChart.invalidate();
    }

    private void MetodoConteoPorCondicion(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        GraficaTresList = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                GraficaTresList.add(new PieEntry(Float.parseFloat(jsonObject.getString("Excelente")), "Excelente"));
                                GraficaTresList.add(new PieEntry(Float.parseFloat(jsonObject.getString("Herido")), "Herido"));
                                GraficaTresList.add(new PieEntry(Float.parseFloat(jsonObject.getString("Delicado")), "Delicado"));
                            }
                            CrearGraficoConteoPorCondicion(GraficaTresList);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Hubo un error", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    public void CrearGraficoConteoPorCondicion (ArrayList<PieEntry> pieEntris) {
        PieChart pieChart = findViewById(R.id.GraficoPorCondicion);

        PieDataSet pieDataSet = new PieDataSet(pieEntris, "");
        PieData pieData = new PieData(pieDataSet);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextColor(Color.WHITE);
        pieDataSet.setValueTextSize(12f);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText("Condición");
        pieChart.getDescription().setEnabled(false);
        pieChart.animateX(1000);
        pieChart.setData(pieData);

        pieChart.invalidate();
    }

    private void MetodoConteoPorInstitucion(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        GraficaCuatroList = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                float epyb = Float.parseFloat(jsonObject.getString("EPYB"));
                                float huellitas = Float.parseFloat(jsonObject.getString("Huellitas"));
                                float api = Float.parseFloat(jsonObject.getString("API"));
                                GraficaCuatroList.add(new BarEntry(2f, epyb));
                                GraficaCuatroList.add(new BarEntry(1f, huellitas));
                                GraficaCuatroList.add(new BarEntry(0f, api));
                            }
                            CrearGraficoConteoPorInstitucion(GraficaCuatroList);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Hubo un error", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    public void CrearGraficoConteoPorInstitucion (ArrayList<BarEntry> barEntries) {
        BarChart barChart = findViewById(R.id.GraficoPorInstitucion);

        final String[] labels = new String[]{"API", "Huellitas", "EPYB"};

        BarDataSet barDataSet = new BarDataSet(barEntries, "Instituciones");
        BarData barData = new BarData(barDataSet);
        barDataSet.setColors(ColorTemplate.LIBERTY_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        barChart.invalidate();
    }
}
