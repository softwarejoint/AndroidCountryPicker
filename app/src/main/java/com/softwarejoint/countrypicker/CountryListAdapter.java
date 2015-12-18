package com.softwarejoint.countrypicker;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.softwarejoint.countrypicker.R.drawable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class CountryListAdapter extends BaseAdapter {

    private final static Integer NOT_FOUND = Integer.MIN_VALUE;
    private Context context;
    private ArrayList<CountryModel> countriesAll;
    private ArrayList<CountryModel> countriesDisplay;
    private LayoutInflater inflater;

    public CountryListAdapter(Context context, ArrayList<CountryModel> countries) {
        this.context = context;
        this.countriesAll = countries;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Iterator<CountryModel> itr = countries.iterator();

        while (itr.hasNext()) {
            String drawableName = "flag_"
                    + itr.next().mFlagName.toLowerCase(Locale.ENGLISH);
            if (getResId(drawableName) == NOT_FOUND) {
                itr.remove();
            }
        }

        filterCountriesDisplayed(null);
    }

    private int getResId(String drawableName) {

        try {
            Class<drawable> res = R.drawable.class;
            Field field = res.getField(drawableName);
            int drawableId = field.getInt(null);
            return drawableId;
        } catch (Exception e) {
            Log.e("COUNTRYPICKER", "Failure to get drawable id." + drawableName, e);
        }
        return NOT_FOUND;
    }

    @Override
    public int getCount() {
        return countriesDisplay.size();
    }

    @Override
    public CountryModel getItem(int position) {
        return countriesDisplay.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cellView = convertView;
        Cell cell;
        CountryModel countryModel = getItem(position);

        if (convertView == null) {
            cell = new Cell();
            cellView = inflater.inflate(R.layout.row, null);
            cell.textView = (TextView) cellView.findViewById(R.id.row_title);
            cell.imageView = (ImageView) cellView.findViewById(R.id.row_icon);
            cellView.setTag(cell);
        } else {
            cell = (Cell) cellView.getTag();
        }

        cell.textView.setText(countryModel.mCountryName);

        // Load drawable dynamically from country code
        String drawableName = "flag_"
                + countryModel.mFlagName.toLowerCase(Locale.ENGLISH);
        cell.imageView.setImageResource(getResId(drawableName));
        return cellView;
    }

    public void filterCountriesDisplayed(String name) {
        if (countriesDisplay == null) {
            countriesDisplay = new ArrayList<>();
        } else {
            countriesDisplay.clear();
        }
        if (name == null) {
            countriesDisplay.addAll(countriesAll);
            return;
        }

        for (CountryModel model : countriesAll) {
            if (model.mCountryName.toLowerCase().contains(name)) {
                countriesDisplay.add(model);
            }
        }

        notifyDataSetChanged();
    }

    /**
     * Holder for the cell
     */
    static class Cell {
        public TextView textView;
        public ImageView imageView;
    }
}