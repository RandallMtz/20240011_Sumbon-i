package com.example.appmovdoggepediaversion2;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appmovdoggepediaversion2.databinding.ActivityMenuUsuarioEmpleadoBinding;

public class ActivityMenuUsuarioEmpleado extends ActivityBasic {

    ActivityMenuUsuarioEmpleadoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuUsuarioEmpleadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MetodoRemplazarFragmento(new FragmentGestionAdministradores());
        binding.bottomNavigationViewTrabajador.setBackground(null);

        binding.bottomNavigationViewTrabajador.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.AnimalesTrabajador) {
                MetodoRemplazarFragmento(new FragmentGestionAdministradores());
            } else if (item.getItemId() == R.id.ConsultaAdopciones) {
                MetodoRemplazarFragmento(new FragmentAdopcionesAdministradores());
            }
            return true;
        });
    }

    private void MetodoRemplazarFragmento(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_trabajador, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityAutentificacion.class);
        startActivity(intent);
        finish();
    }
}