AndroidCountryPicker
====================

## Features
CountryPicker is a simple fragment that can be embedded or shown as dialog. See the example to see more detail.


<img src="https://raw.github.com/roomorama/AndroidCountryPicker/master/screenshot/1.png" width="250">
<img src="https://raw.github.com/roomorama/AndroidCountryPicker/master/screenshot/2.png" width="250">

The functions are simple:
 
1) Allow user to search the country

2) Inform client which country user has selected

3) Convenient function to get country code, country name, max/min phone number length allowed, country exit code, flag name

## How to use

Add maven repository to your project's build.gradle

```
allprojects {
    repositories {
        jcenter()
    }
    repositories {
        maven {
            url "https://jitpack.io"
        }
    }
}
```

Add dependency to your module's build.gradle

```
compile 'com.github.softwarejoint:AndroidCountryPicker:v1.0'
```

To embed CountryPicker in your own view:

```java
FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
CountryPicker picker = new CountryPicker();
transaction.replace(R.id.home, picker);
transaction.commit();
```

To show CountryPicker as a dialog:

```java
CountryPicker picker = CountryPicker.newInstance("Select Country");
picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
```

When user selects a country, client can listen to that event:

```java
picker.setListener(new CountryPickerListener() {

	@Override
	public void onSelectCountry(CountryModel countryModel) {
		// Invoke your function here
		countryModel.mCountryCode;
		countryModel.mCountryName;
		countryModel.mMaxLength;
		countryModel.mMinLength;
		countryModel.exitCode;
		countryModel.mFlagName;
	}
});
				
```

## About

Library gets its data from country_list.sqilte in res/raw folder. It can be extended with more columns/data as needed.

## License
See LICENSE.md
