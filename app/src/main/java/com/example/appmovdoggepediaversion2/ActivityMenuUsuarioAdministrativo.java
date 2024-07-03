package com.example.appmovdoggepediaversion2;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appmovdoggepediaversion2.databinding.ActivityMenuUsuarioAdministrativoBinding;

public class ActivityMenuUsuarioAdministrativo extends ActivityBasic {

    ActivityMenuUsuarioAdministrativoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuUsuarioAdministrativoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MetodoRemplazarFragmento(new FragmentGestionAdministradores());
        binding.bottomNavigationViewAdministrativo.setBackground(null);

        binding.bottomNavigationViewAdministrativo.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.Gestion) {
                MetodoRemplazarFragmento(new FragmentGestionAdministradores());
            } else if (item.getItemId() == R.id.Difusion) {
                MetodoRemplazarFragmento(new FragmentDifusionAdministradores());
            } else if (item.getItemId() == R.id.Adopciones) {
                MetodoRemplazarFragmento(new FragmentAdopcionesAdministradores());
            }
            return true;
        });
    }

    private void MetodoRemplazarFragmento(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_administrativo, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityAutentificacion.class);
        startActivity(intent);
        finish();
    }
}