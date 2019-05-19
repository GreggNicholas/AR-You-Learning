package com.example.aryoulearning.augmented;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aryoulearning.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ARFragmentFragment extends Fragment {

    public ARFragmentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_arfragment, container, false);
    }
}
