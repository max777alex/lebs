package com.lebs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class SongTextArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public SongTextArrayAdapter(Context context, String[] values) {
        super(context, R.layout.textline, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.textline, parent, false);

        TextView textLineView = (TextView) itemView.findViewById(R.id.textLine);
        textLineView.setText(values[position]);

        Spinner answerSpinner = (Spinner) itemView.findViewById(R.id.answerSpinner);

        String[] data = {"one", "two", "three", "four", "five"}; // TODO: change this
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, data);

        answerSpinner.setAdapter(spinnerArrayAdapter);

        if(values[position].trim().length() == 0)
            answerSpinner.setVisibility(View.INVISIBLE);

        return itemView;
    }
}