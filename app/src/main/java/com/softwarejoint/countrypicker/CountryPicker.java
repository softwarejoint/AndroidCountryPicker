package com.softwarejoint.countrypicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class CountryPicker extends DialogFragment {
    static private Context mContext;
    /**
     * View components
     */
    private EditText searchEditText;
    private ListView countryListView;
    /**
     * Adapter for the listview
     */
    private CountryListAdapter adapter;

    /**
     * Hold all countries, sorted by country name
     */


    /**
     * Hold countries that matched user query
     */


    /**
     * Listener to which country user selected
     */
    private CountryPickerListener listener;

    /**
     * Convenient function to get currency code from country code currency code
     * is in English locale
     *
     * @param countryCode
     * @return
     */
    public static Currency getCurrencyCode(String countryCode) {
        try {
            return Currency.getInstance(new Locale("en", countryCode));
        } catch (Exception e) {

        }
        return null;
    }

    /**
     * To support show as dialog
     *
     * @param dialogTitle
     * @return
     */
    public static CountryPicker newInstance(Context context, String dialogTitle) {
        mContext = context;
        CountryPicker picker = new CountryPicker();
        Bundle bundle = new Bundle();
        bundle.putString("dialogTitle", dialogTitle);
        picker.setArguments(bundle);
        return picker;
    }

    public static CountryModel getCountryModelByISO(Context context, String countryISO) {
        Cursor cursor = null;
        CountryModel flgBean = null;
        try {
            cursor = CountryListDbHelper.getInstance(context).fetchQuery("SELECT CountryEnglishName, CountryExitCode, " +
                    "ISOCode, CountryCode, Max_NSN, Min_NSN FROM country WHERE ISOCode='"
                    + countryISO.toUpperCase(Locale.ENGLISH)
                    + "' ORDER BY CountryEnglishName ASC");

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                flgBean = new CountryModel();
                flgBean.mCountryCode = cursor.getString(cursor.getColumnIndexOrThrow("CountryCode"));
                flgBean.mCountryName = cursor.getString(cursor.getColumnIndexOrThrow("CountryEnglishName"));
                flgBean.mFlagName = cursor.getString(cursor.getColumnIndexOrThrow("ISOCode"));
                flgBean.exitCode = cursor.getString(cursor.getColumnIndexOrThrow("CountryExitCode"));
                flgBean.mMaxLength = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("Max_NSN")));
                flgBean.mMinLength = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("Min_NSN")));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return flgBean;
    }

    /**
     * Set listener
     *
     * @param listener
     */
    public void setListener(CountryPickerListener listener) {
        this.listener = listener;
    }

    public EditText getSearchEditText() {
        return searchEditText;
    }

    public ListView getCountryListView() {
        return countryListView;
    }

    /**
     * initilize country list
     *
     * @return
     */
    private ArrayList<CountryModel> getCountriesList() {

        // Get countries from the json
        //                         String isoCode = getIso();

        CountryModel flgBean;
        Cursor cursor = null;
        ArrayList allCountryList = new ArrayList<>();
        try {
            cursor = CountryListDbHelper.getInstance(mContext).fetchQuery("SELECT CountryEnglishName, CountryExitCode,ISOCode,CountryCode,Max_NSN,Min_NSN FROM country ORDER BY CountryEnglishName ASC");
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    flgBean = new CountryModel();
                    flgBean.mCountryCode = cursor.getString(cursor.getColumnIndexOrThrow("CountryCode"));
                    flgBean.mCountryName = cursor.getString(cursor.getColumnIndexOrThrow("CountryEnglishName"));
                    flgBean.mFlagName = cursor.getString(cursor.getColumnIndexOrThrow("ISOCode"));
                    flgBean.exitCode = cursor.getString(cursor.getColumnIndexOrThrow("CountryExitCode"));
                    flgBean.mMaxLength = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("Max_NSN")));
                    flgBean.mMinLength = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("Min_NSN")));
                    allCountryList.add(flgBean);
                } while (cursor.moveToNext());

            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }

        return allCountryList;
    }

    @SuppressLint("DefaultLocale")
    private String getIso() {
        String isoCode = "in";
        try {
            isoCode = ((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)).getSimCountryIso();
            isoCode = isoCode.toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isoCode;
    }

    /**
     * Create view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate view
        View view = inflater.inflate(R.layout.country_picker, null);

        Bundle args = getArguments();
        if (args != null) {
            String dialogTitle = args.getString("dialogTitle");
            getDialog().setTitle(dialogTitle);

            int width = getResources().getDimensionPixelSize(
                    R.dimen.cp_dialog_width);
            int height = getResources().getDimensionPixelSize(
                    R.dimen.cp_dialog_height);
            getDialog().getWindow().setLayout(width, height);
        }

        // Get view components
        searchEditText = (EditText) view
                .findViewById(R.id.country_picker_search);
        countryListView = (ListView) view
                .findViewById(R.id.country_picker_listview);

        // Set adapter
        adapter = new CountryListAdapter(getActivity(), getCountriesList());
        countryListView.setAdapter(adapter);

        // Inform listener
        countryListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (listener != null) {
                    CountryModel countryModel = (CountryModel) parent.getItemAtPosition(position);
                    listener.onSelectCountry(countryModel);
                }
            }
        });

        // Search for which countries matched user query
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.filterCountriesDisplayed(s.toString());
            }
        });

        return view;
    }
}
