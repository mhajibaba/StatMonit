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
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.object.Agent;
import com.pec.mob.statmonit.object.BankInfo;
import com.pec.mob.statmonit.object.ChartType;
import com.pec.mob.statmonit.object.ValueType;
import com.pec.mob.statmonit.util.DataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BankActivity extends AppCompatActivity {

    private static final String TAG = BankActivity.class.getSimpleName();
    private final int timeInterval=60000;
    private Timer timer;
    private boolean firstShow=true;

    private List<BankInfo> banks = new ArrayList<>();

    private BarChart barChart;
    private LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);
        layout = (LinearLayout) findViewById(R.id.progressbar_view);
        barChart = (BarChart) findViewById(R.id.barChartBanks);
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
            jsonData = (String) dataSet.getItemValue(Agent.getItemidForChart(ChartType.Bar_Bank), ValueType._String);
            if (jsonData != null) {
                editor.putString("data", jsonData);
                editor.putLong("date", System.currentTimeMillis());
            }
        } else {
            Log.i(TAG, "Get data from shared preferences");
            jsonData = sharedPreferences.getString("data", null);
        }

        Gson gson = new GsonBuilder().create();
        BankInfo[] bankInfos = gson.fromJson(jsonData, BankInfo[].class);
        if (bankInfos != null && bankInfos.length > 0) {
            banks = Arrays.asList(bankInfos);
            editor.commit();
        }
    }

    private void initChart() throws Exception{
        List<BarEntry> entries = new ArrayList<>();
        List<String> lables = new ArrayList<>();
        Collections.sort(banks);

        int i=0;
        for (BankInfo bankInfo:banks) {
            String label;
            if(bankInfo.getPersianTitle().length()<=5)
                label = bankInfo.getPersianTitle();
            else
                label = bankInfo.getPersianTitle().substring(0,5)+"...";

            lables.add(i,label);
            entries.add(new BarEntry(i++, bankInfo.getTotcnt()));
        }

        BarDataSet dataset = new BarDataSet(entries, null);
        dataset.setColors(ColorTemplate.JOYFUL_COLORS);
        dataset.setAxisDependency(YAxis.AxisDependency.RIGHT); //right is bottom in horizontal

        BarData data = new BarData(dataset);
        data.setDrawValues(true);
        data.setValueTextSize(10);

        if(banks!=null && banks.size()>0)
            barChart.setData(data);

        barChart.getDescription().setText("# تراکنش ها");
        barChart.setPinchZoom(true);
        barChart.setTouchEnabled(true);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getLegend().setEnabled(false);
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
                initiateDialogWindow(banks.get((int)e.getX()));
            }

            @Override
            public void onNothingSelected() {
            }
        });
    }

    private void initiateDialogWindow(BankInfo data) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("اطلاعات تفکیکی بانک "+data.getPersianTitle());
            final CharSequence[] items = {
                    getString(R.string.bank_iin)+ " : " + data.getIin(),
                    getString(R.string.bank_sccnt)+ " : " + data.getSccnt(),
                    getString(R.string.bank_rejcnt)+ " : " + data.getRejcnt(),
                    getString(R.string.bank_scperc)+ " : " + "%" + new DecimalFormat("#.##").format(data.getScperc()),
                    getString(R.string.bank_rejperc)+ " : " + "%"+ new DecimalFormat("#.##").format(data.getRejperc()),
                    getString(R.string.bank_USrejcnt)+ " : " +data.getUSrejcnt(),
                    getString(R.string.bank_issrejcnt)+ " : " +data.getIssrejcnt(),
                    getString(R.string.bank_issStableperc)+ " : " + "%"+ new DecimalFormat("#.##").format(data.getIssStableperc()),
                    getString(R.string.bank_avgtxntime)+ " : " + data.getAvgtxntime() + " ms",
                    getString(R.string.bank_mintxntime)+ " : " +data.getMintxntime() + " ms",
                    getString(R.string.bank_maxtxntime)+ " : " + data.getMaxtxntime() + " ms"};

            builder.setItems(items, null);
            builder.setPositiveButton(getString(R.string.exit), null);

            AlertDialog dialog = builder.create();
            dialog.show();  //<-- See This!

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
