package com.pec.mob.statmonit.layout;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.object.Agent;
import com.pec.mob.statmonit.object.ChartType;
import com.pec.mob.statmonit.object.Inspection;
import com.pec.mob.statmonit.object.ValueType;
import com.pec.mob.statmonit.util.DataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class InspectionActivity extends AppCompatActivity {

    private static final String TAG = InspectionActivity.class.getSimpleName();
    private final int timeInterval=60000;
    private Timer timer;
    private boolean firstShow=true;

    private List<Inspection> inspections = new ArrayList<>();

    private BarChart barChart;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection);
        layout = (LinearLayout) findViewById(R.id.progressbar_view);
        barChart = (BarChart) findViewById(R.id.barChartInspection);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Task().execute();

        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    getData();
                    initChart();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, timeInterval, timeInterval);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(timer!=null) {
            timer.cancel();
        }
        firstShow = true;
    }


    private void getData() throws Exception{
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long updateDate = sharedPreferences.getLong("date", 0);
        boolean needUpdate = false;
        if (updateDate != 0) {
            if (System.currentTimeMillis() - updateDate > timeInterval) {
                needUpdate = true;
            }
        } else {
            needUpdate = true;
        }

        String jsonData;
        if (needUpdate) {
            Log.i(TAG, "Connect to server to retrieve data");
            DataSet dataSet = new DataSet();
            jsonData = (String) dataSet.getItemValue(Agent.getItemidForChart(ChartType.Bar_Inspection), ValueType._String);
            if (jsonData != null) {
                editor.putString("data", jsonData);
                editor.putLong("date", System.currentTimeMillis());
            }
        } else {
            Log.i(TAG, "Get data from shared preferences");
            jsonData = sharedPreferences.getString("data", null);
        }

        Gson gson = new GsonBuilder().create();
        Inspection[] inspectionsInf = gson.fromJson(jsonData, Inspection[].class);
        if (inspectionsInf != null && inspectionsInf.length > 0) {
            inspections = new LinkedList<>(Arrays.asList(inspectionsInf));
            editor.commit();
        }
    }

    private void initChart() throws Exception{
        ArrayList<BarEntry> yValues = new ArrayList<>();
        ArrayList<String> lables = new ArrayList<>();

        Collections.sort(inspections);

        int i=0;
        List<Inspection> inspectionsTemp = new ArrayList<>();
        for (Inspection inspectionsInf: inspections) {
            if(inspectionsInf.getTitle()==null) continue;
            inspectionsTemp.add(inspectionsInf);
            String label;
            if(inspectionsInf.getTitle().length()<=5)
                label = inspectionsInf.getTitle();
            else
                label = inspectionsInf.getTitle().substring(0,5)+"...";

            lables.add(i, label);
            yValues.add(new BarEntry(i++, new float[]{ inspectionsInf.getDone(), inspectionsInf.getNodone()}));
        }

        inspections.clear();
        inspections.addAll(inspectionsTemp);

        BarDataSet dataset = new BarDataSet(yValues, "# بازرسی ها");
        dataset.setStackLabels(new String[]{
                "انجام شده", "انجام نشده"
        });
        dataset.setAxisDependency(YAxis.AxisDependency.RIGHT); //right is bottom in horizontal
        dataset.setColors(getResources().getIntArray(R.array.blue_red_colors));

        BarData data = new BarData(dataset);
        data.setDrawValues(false);
        data.setValueTextSize(10);

        if(inspections !=null && inspections.size()>0)
            barChart.setData(data);

        barChart.setTouchEnabled(true);
        barChart.getAxisLeft().setEnabled(false);
        barChart.setScaleMinima(1f, 2f);
        barChart.moveViewTo(1,1,YAxis.AxisDependency.RIGHT);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getDescription().setText("نمایندگی ها");
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(lables));
        barChart.getXAxis().setLabelCount(lables.size());
        barChart.getAxisRight().setAxisMinimum(0);

        if(!(barChart instanceof HorizontalBarChart)) {
            XAxis xAxis=barChart.getXAxis();
            xAxis.setLabelRotationAngle(-90);
        }

        final BarChart finalBarChart = barChart;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(firstShow) {
                    finalBarChart.animateY(3000);
                    firstShow = false;
                }
                finalBarChart.notifyDataSetChanged();
                finalBarChart.getData().notifyDataChanged();
                finalBarChart.invalidate();
            }
        });

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                initiateDialogWindow(inspections.get((int)e.getX()));
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void initiateDialogWindow(Inspection data) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("اطلاعات تفکیکی");
            final CharSequence[] items = {
                    getString(R.string.inspect_title)+ " : " + data.getTitle(),
                    getString(R.string.inspect_nodone)+ " : " + data.getNodone(),
                    getString(R.string.inspect_done)+ " : " + data.getDone(),
                    getString(R.string.inspect_total)+ " : " + data.getTotal()
            };

            builder.setItems(items, null);
            builder.setPositiveButton(getString(R.string.exit), null);

            AlertDialog dialog = builder.create();
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            try {
                layout.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                layout.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);

                initChart();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
