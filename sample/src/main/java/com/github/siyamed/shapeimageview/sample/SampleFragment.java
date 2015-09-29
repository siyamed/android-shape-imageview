package com.github.siyamed.shapeimageview.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


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
        final ImageView imageView = (ImageView) view.findViewById(R.id.relative_test_img_1);
        if(imageView != null) {
            imageView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(getActivity()).load(Constants.IMAGES[0][0]).into(imageView);
                }
            }, 3000);
        }

        return view;
    }
}
