package com.ventoray.shaut.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ventoray.shaut.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShautsFragment extends Fragment {


    public ShautsFragment() {
        // Required empty public constructor
    }

    public static ShautsFragment newInstance(int position) {

        Bundle args = new Bundle();

        ShautsFragment fragment = new ShautsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shauts, container, false);
    }

}
