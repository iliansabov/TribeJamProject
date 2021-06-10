package com.optic.tribejam.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.optic.tribejam.R;
import com.optic.tribejam.activities.FiltersActivity;

public class FiltersFragment extends Fragment {

    View mView;
    CardView mCardViewRock;
    CardView mCardViewJazz;
    CardView mCardViewHipHop;
    CardView mCardViewRap;

    public FiltersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_filters, container, false);
        mCardViewRock = mView.findViewById(R.id.cardViewRock);
        mCardViewJazz = mView.findViewById(R.id.cardViewJazz);
        mCardViewHipHop = mView.findViewById(R.id.cardViewHipHop);
        mCardViewRap = mView.findViewById(R.id.cardViewRap);

        mCardViewRock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Rock");
            }
        });
        mCardViewJazz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Jazz");
            }
        });
        mCardViewHipHop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Hip Hop");
            }
        });
        mCardViewRap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFilterActivity("Rap");
            }
        });

        return  mView;
    }

    private void  goToFilterActivity(String category){
        Intent intent = new Intent(getContext(), FiltersActivity.class);
        intent.putExtra("category",category);
        startActivity(intent);

    }

}