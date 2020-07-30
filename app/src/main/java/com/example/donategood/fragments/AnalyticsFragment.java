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

        String[] charityAndMoney = analytics.split("; ");
        for (String str : charityAndMoney) {
            String[] splitCharityAndMoney = str.split("=");
            if (forCharityFragment) {
                tvAnalytics.setText(tvAnalytics.getText() + splitCharityAndMoney[0] + " raised $" + splitCharityAndMoney[1] + ".\n");
            } else {
                tvAnalytics.setText(tvAnalytics.getText() + "Raised $" + splitCharityAndMoney[1] + " for " + splitCharityAndMoney[0] + ".\n");
            }
        }

        tvAnalytics.setText(tvAnalytics.getText() + "Amazing job!");


        pieChart = view.findViewById(R.id.piechartAnalytics);
        createPieChart();
    }

    public void createPieChart() {
        List<PieEntry> NoOfEmp = new ArrayList();

        NoOfEmp.add(new PieEntry(945f, 0));
        NoOfEmp.add(new PieEntry(1040f, 1));
        NoOfEmp.add(new PieEntry(1133f, 2));
        NoOfEmp.add(new PieEntry(1240f, 3));
        NoOfEmp.add(new PieEntry(1369f, 4));
        NoOfEmp.add(new PieEntry(1487f, 5));
        NoOfEmp.add(new PieEntry(1501f, 6));
        NoOfEmp.add(new PieEntry(1645f, 7));
        NoOfEmp.add(new PieEntry(1578f, 8));
        NoOfEmp.add(new PieEntry(1695f, 9));
        PieDataSet dataSet = new PieDataSet(NoOfEmp, "Number Of Employees");

        List<String> year = new ArrayList();
        year.add("2008");
        year.add("2009");
        year.add("2010");
        year.add("2011");
        year.add("2012");
        year.add("2013");
        year.add("2014");
        year.add("2015");
        year.add("2016");
        year.add("2017");

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(5000, 5000);
        pieChart.invalidate(); // refresh
    }
}