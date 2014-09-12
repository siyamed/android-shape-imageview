package com.github.siyamed.shapeimageview.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SampleFragment extends Fragment {
    private static final String ARG_LAYOUT = "layout";
    private int layout;

    public static SampleFragment newInstance(int layout) {
        SampleFragment fragment = new SampleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layout);
        fragment.setArguments(args);
        return fragment;
    }

    public SampleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            layout = getArguments().getInt(ARG_LAYOUT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(layout, container, false);
        return view;
    }
}
