package com.example.appmovdoggepediaversion2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class FragmentDifusionAdministradores extends Fragment {

    private CardView ListadoCampañasCard, OperacionesAlianzasCard, OperacionesCampañasCard;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_difusion_administradores, container, false);

        ListadoCampañasCard = view.findViewById(R.id.VistaUsuCampañasCard);
        OperacionesAlianzasCard = view.findViewById(R.id.OperacionesAlianzasCard);
        OperacionesCampañasCard = view.findViewById(R.id.OperacionesCampañasCard);

        ListadoCampañasCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityListadoCampanas.class);
                startActivity(intent);
            }
        });

        OperacionesAlianzasCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityOperacionesAlianzas.class);
                startActivity(intent);
            }
        });

        OperacionesCampañasCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityOperacionesCampanas.class);
                startActivity(intent);
            }
        });

        return view;
    }
}