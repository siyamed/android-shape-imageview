package com.github.siyamed.shapeimageview.sample;

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

            position = position % Constants.IMAGES.length;

            String title = Constants.IMAGES[position][1];
            String url = Constants.IMAGES[position][0];
            holder.title.setText(title);
            picasso.load(url)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.image);
            return convertView;
        }

        @Override
        public int getCount() {
            return Constants.IMAGES.length * MULTIPLY;
        }
    }

    private static class ViewHolder {
        ImageView image;
        TextView title;
    }
}
