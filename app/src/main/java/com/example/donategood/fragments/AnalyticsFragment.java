package com.example.donategood.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.donategood.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragment extends DialogFragment {

    public static final String TAG = "AnalyticsFragment";

    private String analytics;
    private TextView tvAnalytics;
    private Boolean forCharityFragment;
    private PieChart pieChart;

    public static AnalyticsFragment newInstance(String analytics, Boolean charityFragment) {
        AnalyticsFragment fragment = new AnalyticsFragment();
        Bundle args = new Bundle();
        args.putString("analytics", analytics);
        args.putBoolean("charityFragment", charityFragment);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        analytics = getArguments().getString("analytics", "");
        forCharityFragment = getArguments().getBoolean("charityFragment");
        Log.i(TAG, "analytics got: " + analytics + " with forCharityFragment: " + forCharityFragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvAnalytics = view.findViewById(R.id.tvAnalytics);

        List<PieEntry> entries = new ArrayList();
        String[] charityAndMoney = analytics.split("; ");
        for (String str : charityAndMoney) {
            String[] splitCharityAndMoney = str.split("=");
            if (forCharityFragment) {
                tvAnalytics.setText(tvAnalytics.getText() + splitCharityAndMoney[0] + " raised $" + splitCharityAndMoney[1] + ".\n");
            } else {
                tvAnalytics.setText(tvAnalytics.getText() + "Raised $" + splitCharityAndMoney[1] + " for " + splitCharityAndMoney[0] + ".\n");
            }
            entries.add(new PieEntry(Float.parseFloat(splitCharityAndMoney[1]), splitCharityAndMoney[0]));
        }

        tvAnalytics.setText(tvAnalytics.getText() + "Amazing job!");

        pieChart = view.findViewById(R.id.piechartAnalytics);

        PieDataSet dataSet = new PieDataSet(entries, "Money Raised for Charities");
        PieData data = new PieData(dataSet);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        pieChart.setData(data);
        pieChart.animateXY(700, 700);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate(); // refresh
    }
}