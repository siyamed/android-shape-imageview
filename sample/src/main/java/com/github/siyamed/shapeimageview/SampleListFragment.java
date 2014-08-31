package com.github.siyamed.shapeimageview;

import android.com.ext.circular_imageview.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class SampleListFragment extends Fragment {

    private static final String ARG_LAYOUT = "arg_layout";
    public static SampleListFragment newInstance(int layout) {
        SampleListFragment fragment = new SampleListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layout);
        fragment.setArguments(args);
        return fragment;
    }

    public SampleListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_sample, container, false);
        final Picasso picasso = Picasso.with(getActivity());
        picasso.setLoggingEnabled(true);
        picasso.setIndicatorsEnabled(false);

        int listLayout = getArguments().getInt(ARG_LAYOUT);
        final ListView listView = (ListView) view.findViewById(R.id.list);
        Adapter adapter = new Adapter(getActivity(), picasso, listLayout);
        listView.setAdapter(adapter);

        return view;
    }

    private static class Adapter extends ArrayAdapter {
        private static final int MULTIPLY = 10;

        private static final String[][] IMAGES = new String[][] {
                {"https://farm4.staticflickr.com/3883/15068310256_891b454952_z.jpg", "Neo"},
                {"https://farm6.staticflickr.com/5577/15068309796_1e0a5432cc_z.jpg", "Trinity"},
                {"https://farm4.staticflickr.com/3871/15090945282_28a77fdf13_z.jpg", "Morpheus"},
                {"https://farm4.staticflickr.com/3920/14904728998_4ea0126f6c_z.jpg", "Merovingian"},
                {"https://farm6.staticflickr.com/5582/15088289881_ea1f961056_z.jpg", "Seraph"},
                {"https://farm6.staticflickr.com/5566/15088290961_ce3ef590bf_z.jpg", "Architect"},
                {"https://farm6.staticflickr.com/5551/14904729458_c160044da4_z.jpg", "Persephone"},
                {"https://farm4.staticflickr.com/3877/14904657620_dca7f86632_z.jpg", "Spoon Boy"},
                {"https://farm6.staticflickr.com/5553/15068310526_bc4ac2dae1_z.jpg", "Engineer"},
                {"https://farm4.staticflickr.com/3843/14904747157_a02c252a5a_z.jpg", "Mifune"},
                {"https://farm4.staticflickr.com/3907/15090945042_4f81659a40_z.jpg", "Oracle"},
                {"https://farm4.staticflickr.com/3840/14904728468_5790cbdf89_z.jpg", "Cypher"}
        };

        Picasso picasso;
        int layout;

        private Adapter(Context context, Picasso picasso, int layout) {
            super(context, 0);
            this.picasso = picasso;
            this.layout = layout;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            position = position % IMAGES.length;

            String title = IMAGES[position][1];
            String url = IMAGES[position][0];
            holder.title.setText(title);
            picasso.load(url)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.image);
            return convertView;
        }

        @Override
        public int getCount() {
            return IMAGES.length * MULTIPLY;
        }
    }

    private static class ViewHolder {
        ImageView image;
        TextView title;
    }
}
