package com.example.donategood;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.io.IOException;

public class PieChart02 extends AppCompatActivity {

        /**
     * Creates a simple Chart using QuickChart
     */

        @Override
        public void onCreate(Bundle savedInstanceState) {


                super.onCreate(savedInstanceState);
                double[] yData = new double[]{2.0, 1.0, 0.0};

                // Create Chart
                XYChart chart = new XYChart(500, 400);
                chart.setTitle("Sample Chart");
                chart.setXAxisTitle("X");
                chart.setXAxisTitle("Y");
                XYSeries series = chart.addSeries("y(x)", null, yData);
                series.setMarker(SeriesMarkers.CIRCLE);

                try {
                        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.PNG);
                        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.JPG);
                        BitmapEncoder.saveJPGWithQuality(chart, "./Sample_Chart_With_Quality.jpg", 0.95f);
                        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.BMP);
                        BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.GIF);

                        BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.PNG, 300);
                        BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.JPG, 300);
                        BitmapEncoder.saveBitmapWithDPI(chart, "./Sample_Chart_300_DPI", BitmapEncoder.BitmapFormat.GIF, 300);

                        VectorGraphicsEncoder.saveVectorGraphic(chart, "./Sample_Chart", VectorGraphicsEncoder.VectorGraphicsFormat.EPS);
                        VectorGraphicsEncoder.saveVectorGraphic(chart, "./Sample_Chart", VectorGraphicsEncoder.VectorGraphicsFormat.PDF);
                        VectorGraphicsEncoder.saveVectorGraphic(chart, "./Sample_Chart", VectorGraphicsEncoder.VectorGraphicsFormat.SVG);
                } catch (IOException e) {
                        e.printStackTrace();
                }


        }

}
