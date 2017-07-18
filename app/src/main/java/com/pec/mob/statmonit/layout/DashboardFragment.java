package com.pec.mob.statmonit.layout;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.pec.mob.statmonit.R;

public class DashboardFragment extends Fragment {

    private DashboardFragment.OnFragmentInteractionListener mListener;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dashboard,container, false);

        Button btnBanks = (Button) rootView.findViewById(R.id.btnBanks);
        btnBanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(BankActivity.class);
            }
        });

        Button btnTransStat = (Button) rootView.findViewById(R.id.btnTransStat);
        btnTransStat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(TransactionActivity.class);
            }
        });

        Button btnMobile = (Button) rootView.findViewById(R.id.btnMobile);
        btnMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(MobileActivity.class);
            }
        });

        Button btnAgency = (Button) rootView.findViewById(R.id.btnAgencies);
        btnAgency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(AgencyActivity.class);
            }
        });

        Button btnInspection = (Button) rootView.findViewById(R.id.btnInspection);
        btnInspection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(InspectionActivity.class);
            }
        });

        Button btnInstallation = (Button) rootView.findViewById(R.id.btnInstallation);
        btnInstallation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity(InstallationActivity.class);
            }
        });

        return rootView;
    }

    private void goToActivity(Class<?> cls) {
        try {
            Intent intent = new Intent(getActivity(),cls);
            getActivity().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DashboardFragment.OnFragmentInteractionListener) {
            mListener = (DashboardFragment.OnFragmentInteractionListener) context;
        } else {
            System.err.println(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof DashboardFragment.OnFragmentInteractionListener) {
            mListener = (DashboardFragment.OnFragmentInteractionListener) activity;
        } else {
            System.err.println(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}