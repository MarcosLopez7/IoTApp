package com.bytesandgears.iotapp;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by marco on 17/05/2017.
 */

public class OutputCustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ArrayList<String>> output;

    public OutputCustomAdapter(Context context, ArrayList<ArrayList<String>> output) {
        this.context = context;
        this.output = output;
    }

    @Override
    public int getCount() {
        return output.size();
    }

    @Override
    public Object getItem(int position) {
        return output.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(output.get(position).get(0));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView = View.inflate(context, R.layout.status_list_view, null);

        TextView outputText = (TextView) customView.findViewById(R.id.outputTextView);
        TextView statusText = (TextView) customView.findViewById(R.id.statusTextView);

        int status = Integer.parseInt(output.get(position).get(1));

        outputText.setText("output: " + output.get(position).get(0));

        if (status == 1) {
            statusText.setText("Encendido");
            statusText.setTextColor(Color.GREEN);
        } else {
            statusText.setText("Apagado");
            statusText.setTextColor(Color.RED);
        }

        return customView;
    }
}
