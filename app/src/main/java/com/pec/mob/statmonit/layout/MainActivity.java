package com.pec.mob.statmonit.layout;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.util.Message;
import com.pec.mob.statmonit.util.Network;
import com.pec.mob.statmonit.util.RESTExecutor;
import com.pec.mob.statmonit.util.Rest;

import java.io.File;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private HomeFragment homeFragment=null;
    private DashboardFragment dashboardFragment=null;
    private NotificationFragment notificationFragment=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        boolean b = true;
        if(savedInstanceState!=null) {
            b = false;
        }

        try {
            setupNavigationView(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!checkLogin()){
            finish();
        }
    }

    private void setupNavigationView(boolean first) {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        if (bottomNavigationView != null) {
            if(first) {
                // Select first menu item by default and show Fragment accordingly.
                Menu menu = bottomNavigationView.getMenu();
                menu.getItem(1).setChecked(true);
                selectFragment(menu.getItem(1));
            }

            // Set action to perform when any menu-item is selected.
            bottomNavigationView.setOnNavigationItemSelectedListener(
                    new BottomNavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                            selectFragment(item);
                            return false;
                        }
                    });
        }
    }

    /**
     * Perform action when any item is selected.
     *
     * @param item Item that is selected.
     */
    protected void selectFragment(MenuItem item) {

        item.setChecked(true);

        switch (item.getItemId()) {
            case R.id.navigation_dashboard:
                // Action to perform when Bag Menu item is selected.
                if(dashboardFragment==null) {
                    dashboardFragment = new DashboardFragment();
                }
                pushFragment(dashboardFragment);
                break;
            case R.id.navigation_home:
                // Action to perform when Home Menu item is selected.
                if(homeFragment==null) {
                    homeFragment = new HomeFragment();
                }
                pushFragment(homeFragment);
                break;
            case R.id.navigation_notify:
                // Action to perform when Account Menu item is selected.
                if(notificationFragment==null) {
                    notificationFragment = new NotificationFragment();
                }
                pushFragment(notificationFragment);
                break;
        }
    }


    /**
     * Method to push any fragment into given id.
     *
     * @param fragment An instance of Fragment to show into the given id.
     */
    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (ft != null) {
                ft.replace(R.id.frame_content, fragment);
                ft.commit();
            }
        }
    }

    private void logOut() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token",null);
        if(token!=null) {
            new UnregisterTask(token).execute();
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        finish();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        MainActivity.this.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        Menu menu = bottomNavigationView.getMenu();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            if (bottomNavigationView.getSelectedItemId() == R.id.navigation_home) {
                final Snackbar snackBar = Snackbar.make(findViewById(R.id.frame_content), R.string.msg_exit, Snackbar.LENGTH_LONG);
                snackBar.setAction(R.string.exit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).show();
            } else {
                selectFragment(menu.getItem(1));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_custom_query) {
            try {
                Intent intent = new Intent(this,AgentItemListActivity.class);
                this.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_rise_fall) {
            Intent intent = new Intent(MainActivity.this, RiseFallActivity.class);
            MainActivity.this.startActivity(intent);
        } else if (id == R.id.nav_message) {
            Intent intent = new Intent(MainActivity.this, MessageActivity.class);
            MainActivity.this.startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_logout) {
            logOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private boolean checkLogin() {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        String username = sharedPref.getString("username", null);
        String password = sharedPref.getString("password", null);

        if(!Network.isNetworkConnected(getSystemService(Context.CONNECTIVITY_SERVICE))) {
            Message.showError(MainActivity.this,"Network is unreachable!");
            return false;
        }else if(!Network.isInternetAvailable()) {
            Message.showError(MainActivity.this,"No internet access!");
            return false;
        }
        else
        if (username != null && password != null) {
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onFragmentInteraction() {
        try {
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LineChart lineChart = (LineChart) findViewById(R.id.lineChartMain);
                    if (lineChart != null && lineChart.getData()!=null) {
                        lineChart.getData().notifyDataChanged();
                        lineChart.notifyDataSetChanged();
                        lineChart.invalidate();
                    }
                    PieChart pieChartTotal = (PieChart) findViewById(R.id.pieChartTotal);
                    if (pieChartTotal != null && pieChartTotal.getData()!=null) {
                        pieChartTotal.getData().notifyDataChanged();
                        pieChartTotal.notifyDataSetChanged();
                        pieChartTotal.invalidate();
                    }
                    PieChart pieChartAmount = (PieChart) findViewById(R.id.pieChartAmount);
                    if (pieChartAmount != null && pieChartAmount.getData()!=null) {
                        pieChartAmount.getData().notifyDataChanged();
                        pieChartAmount.notifyDataSetChanged();
                        pieChartAmount.invalidate();
                    }
                }
            });
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class UnregisterTask extends AsyncTask<String, Integer, Boolean> {
        String token;

        public UnregisterTask(String token) {
            this.token = token;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String url = "https://report.pec.ir:444/api/acc/logout?token=" + token;
                String response = Rest.get(url);
                boolean b = Boolean.parseBoolean(response.trim());
                if (b) {
                    Log.d(TAG,"Unregister Token Successfully");
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, e.getMessage());
                return false;
            }
        }
    }
}
