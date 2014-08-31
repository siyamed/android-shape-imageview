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


public class SampleBubbleFragment extends Fragment {

    private static final String ARG_LAYOUT_1 = "arg_layout_1";
    private static final String ARG_LAYOUT_2 = "arg_layout_2";
    public static SampleBubbleFragment newInstance(int layout1, int layout2) {
        SampleBubbleFragment fragment = new SampleBubbleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_1, layout1);
        args.putInt(ARG_LAYOUT_2, layout2);
        fragment.setArguments(args);
        return fragment;
    }

    public SampleBubbleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_sample, container, false);
        final Picasso picasso = Picasso.with(getActivity());
        picasso.setLoggingEnabled(true);
        picasso.setIndicatorsEnabled(false);

        int listLayout1 = getArguments().getInt(ARG_LAYOUT_1);
        int listLayout2 = getArguments().getInt(ARG_LAYOUT_2);
        final ListView listView = (ListView) view.findViewById(R.id.list);
        Adapter adapter = new Adapter(getActivity(), picasso, listLayout1, listLayout2);
        listView.setAdapter(adapter);

        return view;
    }

    private static class Adapter extends ArrayAdapter {
        private static final int MULTIPLY = 10;

        private static final String[][] IMAGES = new String[][] {
                {"https://farm4.staticflickr.com/3871/15090945282_28a77fdf13_z.jpg", "Morpheus"},
                {"https://farm4.staticflickr.com/3883/15068310256_891b454952_z.jpg", "Neo"},
        };

        private static final String[] MESSAGES = new String[] {
                "wake up, Neo...",
                "matrix has you",
                "follow the white rabbit",
                "knock, knock, Neo.",
                "whuaat?"
        };

        Picasso picasso;
        int layout1;
        int layout2;

        private Adapter(Context context, Picasso picasso, int layout1, int layout2) {
            super(context, 0);
            this.picasso = picasso;
            this.layout1 = layout1;
            this.layout2 = layout2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            int itemViewType = getItemViewType(position);
            int layout = layout1;
            if(itemViewType == 1) {
                layout = layout2;
            }

            if(convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView.findViewById(R.id.image);
                holder.text = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            position = position % MESSAGES.length;

            String url;
            String text = MESSAGES[position];
            if(position < 4) {
                url = IMAGES[0][0];
            } else {
                url = IMAGES[1][0];
            }

            holder.text.setText(text);
            picasso.load(url)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.image);
            return convertView;
        }

        @Override
        public int getItemViewType(int position) {
            if(position % MESSAGES.length == 4) {
                return 1;
            } else {
                return 0;
            }
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public int getCount() {
            return MESSAGES.length * MULTIPLY;
        }
    }

    private static class ViewHolder {
        ImageView image;
        TextView text;
    }
}
