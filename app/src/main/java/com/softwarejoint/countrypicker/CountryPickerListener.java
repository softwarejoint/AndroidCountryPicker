package com.softwarejoint.countrypicker;

/**
 * Inform the client which country has been selected
 *
 */
public interface CountryPickerListener {
	void onSelectCountry(CountryModel countryModel);
}
