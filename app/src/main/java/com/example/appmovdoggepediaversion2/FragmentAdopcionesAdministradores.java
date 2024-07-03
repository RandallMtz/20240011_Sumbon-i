package com.example.appmovdoggepediaversion2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class FragmentAdopcionesAdministradores extends Fragment {

    private CardView DashboardCard, ListadoRevisionesCard, OperacionesAdopcionesCard;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adopciones_administradores, container, false);

        DashboardCard = view.findViewById(R.id.DashboardCard);
        ListadoRevisionesCard = view.findViewById(R.id.ListadoRevisionesCard);
        OperacionesAdopcionesCard = view.findViewById(R.id.OperacionesRevisionesCard);

        DashboardCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityDashBoard.class);
                startActivity(intent);
            }
        });

        ListadoRevisionesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityListadoAdopcionesRevision.class);
                startActivity(intent);
            }
        });

        OperacionesAdopcionesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ActivityOperacionesAdopciones.class);
                startActivity(intent);
            }
        });

        return view;
    }
}