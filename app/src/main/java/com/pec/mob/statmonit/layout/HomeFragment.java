package com.pec.mob.statmonit.layout;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.object.Agent;
import com.pec.mob.statmonit.object.ChartType;
import com.pec.mob.statmonit.object.Item;
import com.pec.mob.statmonit.util.DataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private final int timeInterval=60000;
    private Timer timer;
    private boolean firstShow=true;
    private OnFragmentInteractionListener mListener;
    private ArrayList<String> labelsLineChart = new ArrayList<>();
    private ArrayList<Entry> entriesLineUnsuc = new ArrayList<>(),entriesLineTotal= new ArrayList<>();
    private ArrayList<PieEntry> entriesPieTotal= new ArrayList<>(),entriesPieAmount = new ArrayList<>();
    private String lineChartDesc="";
    private LineChart lineChart;
    private PieChart pieChartTotal,pieChartAmount;
    private LinearLayout layout;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        layout = (LinearLayout) rootView.findViewById(R.id.progressbar_view);
        lineChart = (LineChart) rootView.findViewById(R.id.lineChartMain);
        pieChartTotal = (PieChart) rootView.findViewById(R.id.pieChartTotal);
        pieChartAmount = (PieChart) rootView.findViewById(R.id.pieChartAmount);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            System.err.println(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            System.err.println(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(timer!=null) {
            timer.cancel();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        new Task().execute();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    getData();
                    initPieChartTotal();
                    initPieChartAmount();
                    initLineChart();
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
        SharedPreferences sharedPreferences = getActivity().getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long updateDate = sharedPreferences.getLong("date",0);
        boolean needUpdate = false;
        if(updateDate!=0) {
            if(System.currentTimeMillis()-updateDate>timeInterval) {
                needUpdate = true;
            }
        }else {
            needUpdate = true;
        }

        Long succTotal=null, unsuccTotal = null, succAmount=null, unsuccAmount=null;
        Item[] unSuccessArray=null, successArray=null;
        Gson gson = new GsonBuilder().create();

        if(needUpdate) {
            Log.i(TAG, "Connect to server to retrieve data");
            DataSet dataset = new DataSet();
            //================Update Total Pie========================//
            /*Rest.setCredential("reza@pec.ir", "Aa123456.");
            String agentItemsJson = dataset.authenticateAndGetAgentItems("reza@pec.ir", "Aa123456.");

            Gson gson2 = new GsonBuilder().create();
            AgentItem[] agentItems = gson2.fromJson(agentItemsJson, AgentItem[].class);
            if(agentItems!=null) {
                Agent.setItems(agentItems);
            }
*/
            int itemPieSucc = Agent.getItemidForChart(ChartType.Pie_Left_succ);
            if(itemPieSucc>0) {
                succTotal = dataset.getItemValue(itemPieSucc);
            }
            int itemPieUnsucc = Agent.getItemidForChart(ChartType.Pie_Left_unsucc);
            if (itemPieUnsucc > 0) {
                unsuccTotal = dataset.getItemValue(itemPieUnsucc);
            }
            if (succTotal != null && unsuccTotal != null) {
                editor.putLong("SucTotal", succTotal);
                editor.putLong("UnsucTotal", unsuccTotal);
                editor.commit();
            }

            //================Update Amount Pie========================//
            int itemPieSuccAmount = Agent.getItemidForChart(ChartType.Pie_Right_succ);
            if(itemPieSuccAmount>0) {
                succAmount = dataset.getItemValue(itemPieSuccAmount);
            }
            int itemPieUnsuccAmount = Agent.getItemidForChart(ChartType.Pie_Right_unsucc);
            if(itemPieUnsuccAmount>0) {
                unsuccAmount = dataset.getItemValue(itemPieUnsuccAmount);
            }
            if (succAmount != null && unsuccAmount != null) {
                editor.putLong("SucAmount", succAmount);
                editor.putLong("UnsucAmount", unsuccAmount);
                editor.commit();
            }

            //================Update Line chart========================//
            int itemBarUnsucc = Agent.getItemidForChart(ChartType.Bar_Unsucc);
            if(itemBarUnsucc>0) {
                unSuccessArray = dataset.getItemValues(itemBarUnsucc, 10);
            }
            int itemBarSucc = Agent.getItemidForChart(ChartType.Bar_Succ);
            if(itemBarSucc>0) {
                successArray = dataset.getItemValues(itemBarSucc, 10);
            }
            if (unSuccessArray != null && successArray!=null) {
                editor.putString("SucArray", gson.toJson(successArray));
                editor.putString("UnsucArray", gson.toJson(unSuccessArray));
                editor.putLong("date", System.currentTimeMillis());
                editor.commit();
            }

        }
        else {
            Log.i(TAG, "Get data from shared preferences");

            succTotal = sharedPreferences.getLong("SucTotal", 0);
            unsuccTotal = sharedPreferences.getLong("UnsucTotal", 0);

            succAmount = sharedPreferences.getLong("SucAmount", 0);
            unsuccAmount = sharedPreferences.getLong("UnsucAmount", 0);

            successArray = gson.fromJson(sharedPreferences.getString("SucArray", null), Item[].class);
            unSuccessArray = gson.fromJson(sharedPreferences.getString("UnsucArray", null), Item[].class);
        }

        if (succTotal != null && unsuccTotal != null) {
            // Removes all values from this DataSet and recalculates min and max value.
            entriesPieTotal.clear();
            entriesPieTotal.add(new PieEntry(succTotal,0));
            entriesPieTotal.add(new PieEntry(unsuccTotal,1));
        }

        if (succAmount != null && unsuccAmount != null) {
            // Removes all values from this DataSet and recalculates min and max value.
            entriesPieAmount.clear();
            entriesPieAmount.add(new PieEntry(succAmount,0));
            entriesPieAmount.add(new PieEntry(unsuccAmount,1));
        }

        if (unSuccessArray != null && successArray!=null) {
            entriesLineTotal.clear();
            entriesLineUnsuc.clear();
            labelsLineChart.clear();

            for (int i = 0; i < unSuccessArray.length; i++) {
                int index = unSuccessArray.length - i - 1;

                entriesLineUnsuc.add(new BarEntry(i, unSuccessArray[index].getBintvalue()));
                entriesLineTotal.add(new BarEntry(i, unSuccessArray[index].getBintvalue() + successArray[index].getBintvalue()));
                labelsLineChart.add(i, unSuccessArray[index].getTimeString());
            }
            lineChartDesc = unSuccessArray[0].getDateString();
        }
    }

    private void initPieChartTotal() throws Exception{
        PieDataSet dataset = new PieDataSet(entriesPieTotal, null);
        dataset.setValueFormatter(new LargeValueFormatter());

        PieData data = new PieData(dataset);
        data.setValueTextSize(10);
        dataset.setColors(getResources().getIntArray(R.array.blue_red_colors));
        pieChartTotal.setData(data);
        pieChartTotal.getDescription().setText("کل تراکنش ها");
        pieChartTotal.getLegend().setEnabled(false);
        if(firstShow) {
            pieChartTotal.animateY(3000);
        }
    }

    private void initPieChartAmount() throws Exception{

        PieDataSet dataset = new PieDataSet(entriesPieAmount, null);
        dataset.setValueFormatter(new LargeValueFormatter());

        PieData data = new PieData(dataset);
        data.setValueTextSize(10);
        dataset.setColors(getResources().getIntArray(R.array.blue_red_colors));
        pieChartAmount.setData(data);
        pieChartAmount.getDescription().setText("حجم مبلغ");
        pieChartAmount.getLegend().setEnabled(false);
        if(firstShow) {
            pieChartAmount.animateY(3000);
        }
    }

    private void initLineChart() throws Exception{

        List<ILineDataSet> dataSets = new ArrayList<>();
        LineDataSet dataSetUnTrans = new LineDataSet(entriesLineUnsuc, "تراکنش های ناموفق");
        LineDataSet dataSetSuccTrans = new LineDataSet(entriesLineTotal, "تراکنش های موفق");
        dataSets.add(dataSetUnTrans);
        dataSets.add(dataSetSuccTrans);

        LineData data = new LineData(dataSets);
        data.setDrawValues(false);

        dataSetUnTrans.setColor(ContextCompat.getColor(getActivity(), R.color.red2));
        dataSetUnTrans.setCircleColor(ContextCompat.getColor(getActivity(), R.color.red2));
        dataSetUnTrans.setFillColor(ContextCompat.getColor(getActivity(), R.color.red2));
        //dataSetUnTrans.setValueFormatter(new DefaultValueFormatter(0));
        //dataSetUnTrans.setDrawCubic(true);
        dataSetUnTrans.setDrawFilled(true);

        dataSetSuccTrans.setColor(ContextCompat.getColor(getActivity(), R.color.blue_green));
        dataSetSuccTrans.setFillColor(ContextCompat.getColor(getActivity(), R.color.blue_green));
        dataSetSuccTrans.setCircleColor(ContextCompat.getColor(getActivity(), R.color.blue_green));
        //dataSetSuccTrans.setValueFormatter(new DecimalValueFormatter());
        //dataSetSuccTrans.setDrawCubic(true);
        dataSetSuccTrans.setDrawFilled(true);

        lineChart.setData(data);
        lineChart.getDescription().setText(lineChartDesc);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelsLineChart));
        lineChart.getAxisLeft().setAxisMinimum(0);
        lineChart.setTouchEnabled(true);
        CustomMarkerView mv = new CustomMarkerView (getActivity(), R.layout.marker_view);
        lineChart.setMarkerView(mv);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);
        if(firstShow) {
            lineChart.animateY(3000);
            firstShow = false;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction();
    }

    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {

            try {
                layout.setVisibility(View.VISIBLE);
                pieChartAmount.setVisibility(View.GONE);
                pieChartTotal.setVisibility(View.GONE);
                lineChart.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                layout.setVisibility(View.GONE);
                pieChartAmount.setVisibility(View.VISIBLE);
                pieChartTotal.setVisibility(View.VISIBLE);
                lineChart.setVisibility(View.VISIBLE);
                initPieChartAmount();
                initPieChartTotal();
                initLineChart();
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
