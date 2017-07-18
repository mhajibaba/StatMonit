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
import com.pec.mob.statmonit.object.Agent;
import com.pec.mob.statmonit.object.ChartType;
import com.pec.mob.statmonit.object.Installation;
import com.pec.mob.statmonit.object.ValueType;
import com.pec.mob.statmonit.util.DataSet;
import com.pec.mob.statmonit.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class InstallationActivity extends AppCompatActivity {

    private static final String TAG = InstallationActivity.class.getSimpleName();
    private final int timeInterval=60000;
    private Timer timer;
    private boolean firstShow=true;

    private List<Installation> installations = new ArrayList<>();

    private BarChart barChart;
    private LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installation);
        layout = (LinearLayout) findViewById(R.id.progressbar_view);
        barChart = (BarChart) findViewById(R.id.barChartInstallation);
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
            jsonData = (String) dataSet.getItemValue(Agent.getItemidForChart(ChartType.Bar_Installation), ValueType._String);
            if (jsonData != null) {
                editor.putString("data", jsonData);
                editor.putLong("date", System.currentTimeMillis());
            }
        } else {
            Log.i(TAG, "Get data from shared preferences");
            jsonData = sharedPreferences.getString("data", null);
        }

        Gson gson = new GsonBuilder().create();
        Installation[] installationInf = gson.fromJson(jsonData, Installation[].class);
        if (installationInf != null && installationInf.length > 0) {
            installations = new LinkedList<>(Arrays.asList(installationInf));
            editor.commit();
        }
    }

    private void initChart() throws Exception{
        ArrayList<BarEntry> yValues = new ArrayList<>();
        ArrayList<String> lables = new ArrayList<>();

        Collections.sort(installations);

        int i=0;
        List<Installation> installationsTemp = new ArrayList<>();
        for (Installation installationInf: installations) {
            if(installationInf.getAgencyName()==null) continue;
            installationsTemp.add(installationInf);
            String label;
            if(installationInf.getAgencyName().length()<=5)
                label = installationInf.getAgencyName();
            else
                label = installationInf.getAgencyName().substring(0,5)+"...";

            lables.add(i, label);
            yValues.add(new BarEntry(i++, new float[]{ installationInf.getInstallCount(), installationInf.getUninstallCount()}));
        }

        installations.clear();
        installations.addAll(installationsTemp);

        BarDataSet dataset = new BarDataSet(yValues, "تعداد ");
        dataset.setStackLabels(new String[]{
                "نصب ها", "فسخ ها"
        });
        dataset.setAxisDependency(YAxis.AxisDependency.RIGHT); //right is bottom in horizontal
        dataset.setColors(getResources().getIntArray(R.array.blue_red_colors));

        BarData data = new BarData(dataset);
        data.setDrawValues(true);
        data.setValueTextSize(10);

        if(installations !=null && installations.size()>0)
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
        barChart.setDrawValueAboveBar(false);

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
                initiateDialogWindow(installations.get((int)e.getX()));
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void initiateDialogWindow(Installation data) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("اطلاعات تفکیکی");
            final CharSequence[] items = {
                    getString(R.string.install_title)+ " : " + data.getAgencyName(),
                    getString(R.string.install_install)+ " : " + data.getInstallCount(),
                    getString(R.string.install_uninstall)+ " : " + data.getUninstallCount()
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
