package com.hbb20;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hbb20 on 11/1/16.
 */
public class CountryCodePicker extends RelativeLayout {

    static String TAG = "CCP";
    static String BUNDLE_SELECTED_CODE = "selectedCode";
    static int LIB_DEFAULT_COUNTRY_CODE = 91;
    int defaultCountryCode;
    String defaultCountryNameCode;
    Context context;
    View holderView;
    LayoutInflater mInflater;
    TextView textView_selectedCountry;
    EditText editText_registeredCarrierNumber;
    RelativeLayout holder;
    ImageView imageViewArrow;
    ImageView imageViewFlag;
    LinearLayout linearFlagBorder;
    LinearLayout linearFlagHolder;
    Country selectedCountry;
    Country defaultCountry;
    RelativeLayout relativeClickConsumer;
    CountryCodePicker codePicker;
    boolean hideNameCode = false;
    boolean showPhoneCode = true;
    boolean showFlag = true;
    boolean showFullName = false;
    boolean useFullName = false;
    boolean selectionDialogShowSearch = true;
    int contentColor;
    int borderFlagColor;
    boolean showFastScroller;
    List<Country> preferredCountries;
    //this will be "AU,IN,US"
    String countryPreference;
    int fastScrollerBubbleColor = -1;
    List<Country> customMasterCountriesList;
    //this will be "AU,IN,US"
    String customMasterCountries;
    Language customLanguage = Language.ENGLISH;
    boolean keyboardAutoPopOnSearch = true;
    boolean ccpClickable = true;
    private OnCountryChangeListener onCountryChangeListener;
    private int fastScrollerHandleColor;
    private int fastScrollerBubbleTextAppearance;
    View.OnClickListener countryCodeHolderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isCcpClickable()) {
                CountryCodeDialog.openCountryCodeDialog(codePicker);
            }
        }
    };

    public CountryCodePicker(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public CountryCodePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public CountryCodePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        //        Log.d(TAG, "Initialization of CCP");
        mInflater = LayoutInflater.from(context);
        holderView = mInflater.inflate(R.layout.layout_code_picker, this, true);
        textView_selectedCountry = (TextView) holderView.findViewById(R.id.textView_selectedCountry);
        holder = (RelativeLayout) holderView.findViewById(R.id.countryCodeHolder);
        imageViewArrow = (ImageView) holderView.findViewById(R.id.imageView_arrow);
        imageViewFlag = (ImageView) holderView.findViewById(R.id.image_flag);
        linearFlagHolder = (LinearLayout) holderView.findViewById(R.id.linear_flag_holder);
        linearFlagBorder = (LinearLayout) holderView.findViewById(R.id.linear_flag_border);
        relativeClickConsumer = (RelativeLayout) holderView.findViewById(R.id.rlClickConsumer);
        codePicker = this;
        applyCustomProperty(attrs);
        relativeClickConsumer.setOnClickListener(countryCodeHolderClickListener);
    }

    private void applyCustomProperty(AttributeSet attrs) {
        //        Log.d(TAG, "Applying custom property");
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CountryCodePicker, 0, 0);
        //default country code
        try {
            //hide nameCode. If someone wants only phone code to avoid name collision for same country phone code.
            hideNameCode = a.getBoolean(R.styleable.CountryCodePicker_hideNameCode, false);

            //show phone code.
            showPhoneCode = a.getBoolean(R.styleable.CountryCodePicker_ccp_showPhoneCode, true);

            //show full name
            showFullName = a.getBoolean(R.styleable.CountryCodePicker_showFullName, false);

            //show fast scroller
            showFastScroller = a.getBoolean(R.styleable.CountryCodePicker_ccpDialog_showFastScroller, true);

            //bubble color
            fastScrollerBubbleColor = a.getColor(R.styleable.CountryCodePicker_ccpDialog_fastScroller_bubbleColor, -1);

            //scroller handle color
            fastScrollerHandleColor = a.getColor(R.styleable.CountryCodePicker_ccpDialog_fastScroller_handleColor, -1);

            //scroller text appearance
            fastScrollerBubbleTextAppearance = a.getResourceId(R.styleable.CountryCodePicker_ccpDialog_fastScroller_bubbleTextAppearance, -1);

            //show flag
            showFlag(a.getBoolean(R.styleable.CountryCodePicker_showFlag, true));

            //autopop keyboard
            setKeyboardAutoPopOnSearch(a.getBoolean(R.styleable.CountryCodePicker_keyboardAutoPopOnSearch, true));

            //if custom language is specified, then set it as custom
            int attrLanguage = 3; //for english
            if (a.hasValue(R.styleable.CountryCodePicker_ccpLanguage)) {
                attrLanguage = a.getInt(R.styleable.CountryCodePicker_ccpLanguage, 1);
            }
            customLanguage = getLanguageEnum(attrLanguage);

            //custom master list
            customMasterCountries = a.getString(R.styleable.CountryCodePicker_customMasterCountries);
            refreshCustomMasterList();

            //preference
            countryPreference = a.getString(R.styleable.CountryCodePicker_countryPreference);
            refreshPreferredCountries();

            //default country
            defaultCountryNameCode = a.getString(R.styleable.CountryCodePicker_defaultNameCode);
            boolean setUsingNameCode = false;
            if (defaultCountryNameCode != null && defaultCountryNameCode.length() != 0) {
                if (Country.getCountryForNameCodeFromLibraryMasterList(getContext(), customLanguage, defaultCountryNameCode) != null) {
                    setUsingNameCode = true;
                    setDefaultCountry(Country.getCountryForNameCodeFromLibraryMasterList(getContext(), customLanguage, defaultCountryNameCode));
                    setSelectedCountry(defaultCountry);
                }
            }


            //if default country is not set using name code.
            if (!setUsingNameCode) {
                int defaultCountryCode = a.getInteger(R.styleable.CountryCodePicker_defaultCode, -1);

                //if invalid country is set using xml, it will be replaced with LIB_DEFAULT_COUNTRY_CODE
                if (Country.getCountryForCode(getContext(), customLanguage, preferredCountries, defaultCountryCode) == null) {
                    defaultCountryCode = LIB_DEFAULT_COUNTRY_CODE;
                }
                setDefaultCountryUsingPhoneCode(defaultCountryCode);
                setSelectedCountry(defaultCountry);
            }


            //content color
            int contentColor;
            if (isInEditMode()) {
                contentColor = a.getColor(R.styleable.CountryCodePicker_contentColor, 0);
            } else {
                contentColor = a.getColor(R.styleable.CountryCodePicker_contentColor, context.getResources().getColor(R.color.defaultContentColor));
            }
            if (contentColor != 0) {
                setContentColor(contentColor);
            }

            // flag border color
            int borderFlagColor;
            if (isInEditMode()) {
                borderFlagColor = a.getColor(R.styleable.CountryCodePicker_ccp_flagBorderColor, 0);
            } else {
                borderFlagColor = a.getColor(R.styleable.CountryCodePicker_ccp_flagBorderColor, context.getResources().getColor(R.color.defaultBorderFlagColor));
            }
            if (borderFlagColor != 0) {
                setFlagBorderColor(borderFlagColor);
            }

            //text size
            int textSize = a.getDimensionPixelSize(R.styleable.CountryCodePicker_textSize, 0);
            if (textSize > 0) {
                textView_selectedCountry.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                setFlagSize(textSize);
                setArrowSize(textSize);
            } else { //no textsize specified
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                int defaultSize = Math.round(18 * (dm.xdpi / DisplayMetrics.DENSITY_DEFAULT));
                setTextSize(defaultSize);
            }

            //if arrow arrow size is explicitly defined
            int arrowSize = a.getDimensionPixelSize(R.styleable.CountryCodePicker_arrowSize, 0);
            if (arrowSize > 0) {
                setArrowSize(arrowSize);
            }

            selectionDialogShowSearch = a.getBoolean(R.styleable.CountryCodePicker_selectionDialogShowSearch, true);
            setCcpClickable(a.getBoolean(R.styleable.CountryCodePicker_ccpClickable, true));

        } catch (Exception e) {
            textView_selectedCountry.setText(e.getMessage());
        } finally {
            a.recycle();
        }

    }


    private Country getDefaultCountry() {
        return defaultCountry;
    }

    private void setDefaultCountry(Country defaultCountry) {
        this.defaultCountry = defaultCountry;
        //        Log.d(TAG, "Setting default country:" + defaultCountry.logString());
    }

    private TextView getTextView_selectedCountry() {
        return textView_selectedCountry;
    }

    private void setTextView_selectedCountry(TextView textView_selectedCountry) {
        this.textView_selectedCountry = textView_selectedCountry;
    }

    private Country getSelectedCountry() {
        return selectedCountry;
    }

    void setSelectedCountry(Country selectedCountry) {
        this.selectedCountry = selectedCountry;
        //as soon as country is selected, textView should be updated
        if (selectedCountry == null) {
            selectedCountry = Country.getCountryForCode(getContext(), customLanguage, preferredCountries, defaultCountryCode);
        }

        String displayText = "";

        // add full name to if required
        if (showFullName) {
            displayText = displayText + selectedCountry.getName();
        }

        // adds name code if required
        if (!hideNameCode) {
            if (showFullName) {
                displayText += " (" + selectedCountry.getNameCode().toUpperCase() + ")";
            } else {
                displayText += " " + selectedCountry.getNameCode().toUpperCase();
            }
        }

        // hide phone code if required
        if (showPhoneCode) {
            if (displayText.length() > 0) {
                displayText += "  ";
            }
            displayText += "+" + selectedCountry.getPhoneCode();
        }

        textView_selectedCountry.setText(displayText);

        //avoid blank state of ccp
        if (showFlag == false && displayText.length() == 0) {
            displayText += "+" + selectedCountry.getPhoneCode();
            textView_selectedCountry.setText(displayText);
        }

        if (onCountryChangeListener != null) {
            onCountryChangeListener.onCountrySelected();
        }

        imageViewFlag.setImageResource(selectedCountry.getFlagID());
        //        Log.d(TAG, "Setting selected country:" + selectedCountry.logString());
    }

    Language getCustomLanguage() {
        return customLanguage;
    }

    private void setCustomLanguage(Language customLanguage) {
        this.customLanguage = customLanguage;
    }

    private View getHolderView() {
        return holderView;
    }

    private void setHolderView(View holderView) {
        this.holderView = holderView;
    }

    private RelativeLayout getHolder() {
        return holder;
    }

    private void setHolder(RelativeLayout holder) {
        this.holder = holder;
    }

    boolean isKeyboardAutoPopOnSearch() {
        return keyboardAutoPopOnSearch;
    }

    /**
     * By default, keyboard is poped every time ccp is clicked and selection dialog is opened.
     *
     * @param keyboardAutoPopOnSearch true: to open keyboard automatically when selection dialog is opened
     *                                false: to avoid auto pop of keyboard
     */
    public void setKeyboardAutoPopOnSearch(boolean keyboardAutoPopOnSearch) {
        this.keyboardAutoPopOnSearch = keyboardAutoPopOnSearch;
    }

    EditText getEditText_registeredCarrierNumber() {
        return editText_registeredCarrierNumber;
    }

    void setEditText_registeredCarrierNumber(EditText editText_registeredCarrierNumber) {
        this.editText_registeredCarrierNumber = editText_registeredCarrierNumber;
    }

    private LayoutInflater getmInflater() {
        return mInflater;
    }

    private OnClickListener getCountryCodeHolderClickListener() {
        return countryCodeHolderClickListener;
    }

    /**
     * this will load preferredCountries based on countryPreference
     */
    void refreshPreferredCountries() {
        if (countryPreference == null || countryPreference.length() == 0) {
            preferredCountries = null;
        } else {
            List<Country> localCountryList = new ArrayList<>();
            for (String nameCode : countryPreference.split(",")) {
                Country country = Country.getCountryForNameCodeFromCustomMasterList(getContext(), customMasterCountriesList, customLanguage, nameCode);
                if (country != null) {
                    if (!isAlreadyInList(country, localCountryList)) { //to avoid duplicate entry of country
                        localCountryList.add(country);
                    }
                }
            }

            if (localCountryList.size() == 0) {
                preferredCountries = null;
            } else {
                preferredCountries = localCountryList;
            }
        }
        if (preferredCountries != null) {
            //            Log.d("preference list", preferredCountries.size() + " countries");
            for (Country country : preferredCountries) {
                country.log();
            }
        } else {
            //            Log.d("preference list", " has no country");
        }
    }

    /**
     * this will load preferredCountries based on countryPreference
     */
    void refreshCustomMasterList() {
        if (customMasterCountries == null || customMasterCountries.length() == 0) {
            customMasterCountriesList = null;
        } else {
            List<Country> localCountryList = new ArrayList<>();
            for (String nameCode : customMasterCountries.split(",")) {
                Country country = Country.getCountryForNameCodeFromLibraryMasterList(getContext(), customLanguage, nameCode);
                if (country != null) {
                    if (!isAlreadyInList(country, localCountryList)) { //to avoid duplicate entry of country
                        localCountryList.add(country);
                    }
                }
            }

            if (localCountryList.size() == 0) {
                customMasterCountriesList = null;
            } else {
                customMasterCountriesList = localCountryList;
            }
        }
        if (customMasterCountriesList != null) {
            //            Log.d("custom master list:", customMasterCountriesList.size() + " countries");
            for (Country country : customMasterCountriesList) {
                country.log();
            }
        } else {
            //            Log.d("custom master list", " has no country");
        }
    }

    List<Country> getCustomMasterCountriesList() {
        return customMasterCountriesList;
    }

    void setCustomMasterCountriesList(List<Country> customMasterCountriesList) {
        this.customMasterCountriesList = customMasterCountriesList;
    }

    String getCustomMasterCountries() {
        return customMasterCountries;
    }

    /**
     * To provide definite set of countries when selection dialog is opened.
     * Only custom master countries, if defined, will be there is selection dialog to select from.
     * To set any country in preference, it must be included in custom master countries, if defined
     * When not defined or null or blank is set, it will use library's default master list
     * Custom master list will only limit the visibility of irrelevant country from selection dialog. But all other functions like setCountryForCodeName() or setFullNumber() will consider all the countries.
     *
     * @param customMasterCountries is country name codes separated by comma. e.g. "us,in,nz"
     *                              if null or "" , will remove custom countries and library default will be used.
     */
    public void setCustomMasterCountries(String customMasterCountries) {
        this.customMasterCountries = customMasterCountries;
    }

    /**
     * This will match name code of all countries of list against the country's name code.
     *
     * @param country
     * @param countryList list of countries against which country will be checked.
     * @return if country name code is found in list, returns true else return false
     */
    private boolean isAlreadyInList(Country country, List<Country> countryList) {
        if (country != null && countryList != null) {
            for (Country iterationCountry : countryList) {
                if (iterationCountry.getNameCode().equalsIgnoreCase(country.getNameCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This function removes possible country code from fullNumber and set rest of the number as carrier number.
     *
     * @param fullNumber combination of country code and carrier number.
     * @param country    selected country in CCP to detect country code part.
     */
    private String detectCarrierNumber(String fullNumber, Country country) {
        String carrierNumber;
        if (country == null || fullNumber == null) {
            carrierNumber = fullNumber;
        } else {
            int indexOfCode = fullNumber.indexOf(country.getPhoneCode());
            if (indexOfCode == -1) {
                carrierNumber = fullNumber;
            } else {
                carrierNumber = fullNumber.substring(indexOfCode + country.getPhoneCode().length());
            }
        }
        return carrierNumber;
    }

    //add entry here
    private Language getLanguageEnum(int index) {
        if (index < Language.values().length) {
            return Language.values()[index];
        } else {
            return Language.ENGLISH;
        }
    }

    String getDialogTitle() {
        return Country.getDialogTitle(context, customLanguage);
    }

    String getSearchHintText() {
        return Country.getSearchHintMessage(context, customLanguage);
    }

    /**
     * Related to default country
     */

    String getNoResultFoundText() {
        return Country.getNoResultFoundAckMessage(context, customLanguage);
    }

    /**
     * Publicly available functions from library
     */


    /**
     * This method is not encouraged because this might set some other country which have same country code as of yours. e.g 1 is common for US and canada.
     * If you are trying to set US ( and countryPreference is not set) and you pass 1 as @param defaultCountryCode, it will set canada (prior in list due to alphabetical order)
     * Rather use setDefaultCountryUsingNameCode("us"); or setDefaultCountryUsingNameCode("US");
     * <p>
     * Default country code defines your default country.
     * Whenever invalid / improper number is found in setCountryForPhoneCode() /  setFullNumber(), it CCP will set to default country.
     * This function will not set default country as selected in CCP. To set default country in CCP call resetToDefaultCountry() right after this call.
     * If invalid defaultCountryCode is applied, it won't be changed.
     *
     * @param defaultCountryCode code of your default country
     *                           if you want to set IN +91(India) as default country, defaultCountryCode =  91
     *                           if you want to set JP +81(Japan) as default country, defaultCountryCode =  81
     */
    @Deprecated
    public void setDefaultCountryUsingPhoneCode(int defaultCountryCode) {
        Country defaultCountry = Country.getCountryForCode(getContext(), customLanguage, preferredCountries, defaultCountryCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (defaultCountry == null) { //if no correct country is found
            //            Log.d(TAG, "No country for code " + defaultCountryCode + " is found");
        } else { //if correct country is found, set the country
            this.defaultCountryCode = defaultCountryCode;
            setDefaultCountry(defaultCountry);
        }
    }

    /**
     * Default country name code defines your default country.
     * Whenever invalid / improper name code is found in setCountryForNameCode(), CCP will set to default country.
     * This function will not set default country as selected in CCP. To set default country in CCP call resetToDefaultCountry() right after this call.
     * If invalid defaultCountryCode is applied, it won't be changed.
     *
     * @param defaultCountryNameCode code of your default country
     *                               if you want to set IN +91(India) as default country, defaultCountryCode =  "IN" or "in"
     *                               if you want to set JP +81(Japan) as default country, defaultCountryCode =  "JP" or "jp"
     */
    public void setDefaultCountryUsingNameCode(String defaultCountryNameCode) {
        Country defaultCountry = Country.getCountryForNameCodeFromLibraryMasterList(getContext(), customLanguage, defaultCountryNameCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (defaultCountry == null) { //if no correct country is found
            //            Log.d(TAG, "No country for nameCode " + defaultCountryNameCode + " is found");
        } else { //if correct country is found, set the country
            this.defaultCountryNameCode = defaultCountry.getNameCode();
            setDefaultCountry(defaultCountry);
        }
    }

    /**
     * @return: Country Code of default country
     * i.e if default country is IN +91(India)  returns: "91"
     * if default country is JP +81(Japan) returns: "81"
     */
    public String getDefaultCountryCode() {
        return defaultCountry.phoneCode;
    }

    /**
     * * To get code of default country as Integer.
     *
     * @return integer value of default country code in CCP
     * i.e if default country is IN +91(India)  returns: 91
     * if default country is JP +81(Japan) returns: 81
     */
    public int getDefaultCountryCodeAsInt() {
        int code = 0;
        try {
            code = Integer.parseInt(getDefaultCountryCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * To get code of default country with prefix "+".
     *
     * @return String value of default country code in CCP with prefix "+"
     * i.e if default country is IN +91(India)  returns: "+91"
     * if default country is JP +81(Japan) returns: "+81"
     */
    public String getDefaultCountryCodeWithPlus() {
        return "+" + getDefaultCountryCode();
    }

    /**
     * To get name of default country.
     *
     * @return String value of country name, default in CCP
     * i.e if default country is IN +91(India)  returns: "India"
     * if default country is JP +81(Japan) returns: "Japan"
     */
    public String getDefaultCountryName() {
        return getDefaultCountry().name;
    }

    /**
     * To get name code of default country.
     *
     * @return String value of country name, default in CCP
     * i.e if default country is IN +91(India)  returns: "IN"
     * if default country is JP +81(Japan) returns: "JP"
     */
    public String getDefaultCountryNameCode() {
        return getDefaultCountry().nameCode.toUpperCase();
    }

    /**
     * Related to selected country
     */

    /**
     * reset the default country as selected country.
     */
    public void resetToDefaultCountry() {
        setSelectedCountry(defaultCountry);
    }

    /**
     * To get code of selected country.
     *
     * @return String value of selected country code in CCP
     * i.e if selected country is IN +91(India)  returns: "91"
     * if selected country is JP +81(Japan) returns: "81"
     */
    public String getSelectedCountryCode() {
        return getSelectedCountry().phoneCode;
    }

    /**
     * To get code of selected country with prefix "+".
     *
     * @return String value of selected country code in CCP with prefix "+"
     * i.e if selected country is IN +91(India)  returns: "+91"
     * if selected country is JP +81(Japan) returns: "+81"
     */
    public String getSelectedCountryCodeWithPlus() {
        return "+" + getSelectedCountryCode();
    }

    /**
     * * To get code of selected country as Integer.
     *
     * @return integer value of selected country code in CCP
     * i.e if selected country is IN +91(India)  returns: 91
     * if selected country is JP +81(Japan) returns: 81
     */
    public int getSelectedCountryCodeAsInt() {
        int code = 0;
        try {
            code = Integer.parseInt(getSelectedCountryCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * To get name of selected country.
     *
     * @return String value of country name, selected in CCP
     * i.e if selected country is IN +91(India)  returns: "India"
     * if selected country is JP +81(Japan) returns: "Japan"
     */
    public String getSelectedCountryName() {
        return getSelectedCountry().name;
    }

    /**
     * To get name code of selected country.
     *
     * @return String value of country name, selected in CCP
     * i.e if selected country is IN +91(India)  returns: "IN"
     * if selected country is JP +81(Japan) returns: "JP"
     */
    public String getSelectedCountryNameCode() {
        return getSelectedCountry().nameCode.toUpperCase();
    }

    /**
     * This will set country with @param countryCode as country code, in CCP
     *
     * @param countryCode a valid country code.
     *                    If you want to set IN +91(India), countryCode= 91
     *                    If you want to set JP +81(Japan), countryCode= 81
     */
    public void setCountryForPhoneCode(int countryCode) {
        Country country = Country.getCountryForCode(getContext(), customLanguage, preferredCountries, countryCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (country == null) {
            if (defaultCountry == null) {
                defaultCountry = Country.getCountryForCode(getContext(), customLanguage, preferredCountries, defaultCountryCode);
            }
            setSelectedCountry(defaultCountry);
        } else {
            setSelectedCountry(country);
        }
    }

    /**
     * This will set country with @param countryNameCode as country name code, in CCP
     *
     * @param countryNameCode a valid country name code.
     *                        If you want to set IN +91(India), countryCode= IN
     *                        If you want to set JP +81(Japan), countryCode= JP
     */
    public void setCountryForNameCode(String countryNameCode) {
        Country country = Country.getCountryForNameCodeFromLibraryMasterList(getContext(), customLanguage, countryNameCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (country == null) {
            if (defaultCountry == null) {
                defaultCountry = Country.getCountryForCode(getContext(), customLanguage, preferredCountries, defaultCountryCode);
            }
            setSelectedCountry(defaultCountry);
        } else {
            setSelectedCountry(country);
        }
    }

    /**
     * All functions that work with fullNumber need an editText to write and read carrier number of full number.
     * An editText for carrier number must be registered in order to use functions like setFullNumber() and getFullNumber().
     *
     * @param editTextCarrierNumber - an editText where user types carrier number ( the part of full number other than country code).
     */
    public void registerCarrierNumberEditText(EditText editTextCarrierNumber) {
        setEditText_registeredCarrierNumber(editTextCarrierNumber);
    }

    /**
     * This function combines selected country code from CCP and carrier number from @param editTextCarrierNumber
     *
     * @return Full number is countryCode + carrierNumber i.e countryCode= 91 and carrier number= 8866667722, this will return "918866667722"
     */
    public String getFullNumber() {
        String fullNumber;
        if (editText_registeredCarrierNumber != null) {
            fullNumber = getSelectedCountry().getPhoneCode() + editText_registeredCarrierNumber.getText().toString();
        } else {
            fullNumber = getSelectedCountry().getPhoneCode();
            Log.w(TAG, "EditText for carrier number is not registered. Register it using registerCarrierNumberEditText() before getFullNumber() or setFullNumber().");
        }
        return fullNumber;
    }

    /**
     * Separate out country code and carrier number from fullNumber.
     * Sets country of separated country code in CCP and carrier number as text of editTextCarrierNumber
     * If no valid country code is found from full number, CCP will be set to default country code and full number will be set as carrier number to editTextCarrierNumber.
     *
     * @param fullNumber is combination of country code and carrier number, (country_code+carrier_number) for example if country is India (+91) and carrier/mobile number is 8866667722 then full number will be 9188666667722 or +918866667722. "+" in starting of number is optional.
     */
    public void setFullNumber(String fullNumber) {
        Country country = Country.getCountryForNumber(getContext(), customLanguage, preferredCountries, fullNumber);
        setSelectedCountry(country);
        String carrierNumber = detectCarrierNumber(fullNumber, country);
        if (getEditText_registeredCarrierNumber() != null) {
            getEditText_registeredCarrierNumber().setText(carrierNumber);
        } else {
            Log.w(TAG, "EditText for carrier number is not registered. Register it using registerCarrierNumberEditText() before getFullNumber() or setFullNumber().");
        }
    }

    /**
     * This function combines selected country code from CCP and carrier number from @param editTextCarrierNumber and prefix "+"
     *
     * @return Full number is countryCode + carrierNumber i.e countryCode= 91 and carrier number= 8866667722, this will return "+918866667722"
     */
    public String getFullNumberWithPlus() {
        String fullNumber = "+" + getFullNumber();
        return fullNumber;
    }

    /**
     * @return content color of CCP's text and small downward arrow.
     */
    public int getContentColor() {
        return contentColor;
    }

    /**
     * Sets text and small down arrow color of CCP.
     *
     * @param contentColor color to apply to text and down arrow
     */
    public void setContentColor(int contentColor) {
        this.contentColor = contentColor;
        textView_selectedCountry.setTextColor(this.contentColor);
        imageViewArrow.setColorFilter(this.contentColor, PorterDuff.Mode.SRC_IN);
    }

    /**
     * Sets flag border color of CCP.
     *
     * @param borderFlagColor color to apply to flag border
     */
    public void setFlagBorderColor(int borderFlagColor) {
        this.borderFlagColor = borderFlagColor;
        linearFlagBorder.setBackgroundColor(this.borderFlagColor);
    }

    /**
     * Modifies size of text in side CCP view.
     *
     * @param textSize size of text in pixels
     */
    public void setTextSize(int textSize) {
        if (textSize > 0) {
            textView_selectedCountry.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            setArrowSize(textSize);
            setFlagSize(textSize);
        }
    }

    /**
     * Modifies size of downArrow in CCP view
     *
     * @param arrowSize size in pixels
     */
    private void setArrowSize(int arrowSize) {
        if (arrowSize > 0) {
            LayoutParams params = (LayoutParams) imageViewArrow.getLayoutParams();
            params.width = arrowSize;
            params.height = arrowSize;
            imageViewArrow.setLayoutParams(params);
        }
    }

    /**
     * If nameCode of country in CCP view is not required use this to show/hide country name code of ccp view.
     *
     * @param hideNameCode true will remove country name code from ccp view, it will result  " +91 "
     *                     false will show country name code in ccp view, it will result " (IN) +91 "
     */
    public void hideNameCode(boolean hideNameCode) {
        this.hideNameCode = hideNameCode;
        setSelectedCountry(selectedCountry);
    }

    /**
     * This will set preferred countries using their name code. Prior preferred countries will be replaced by these countries.
     * Preferred countries will be at top of country selection box.
     * If more than one countries have same country code, country in preferred list will have higher priory than others. e.g. Canada and US have +1 as their country code. If "us" is set as preferred country then US will be selected whenever setCountryForPhoneCode(1); or setFullNumber("+1xxxxxxxxx"); is called.
     *
     * @param countryPreference is country name codes separated by comma. e.g. "us,in,nz"
     */
    public void setCountryPreference(String countryPreference) {
        this.countryPreference = countryPreference;
    }

    /**
     * Language will be applied to country select dialog
     *
     * @param language
     */
    public void changeLanguage(Language language) {
        setCustomLanguage(language);
    }

    /**
     * To change font of ccp views
     *
     * @param typeFace
     */
    public void setTypeFace(Typeface typeFace) {
        try {
            textView_selectedCountry.setTypeface(typeFace);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * To change font of ccp views along with style.
     *
     * @param typeFace
     * @param style
     */
    public void setTypeFace(Typeface typeFace, int style) {
        try {
            textView_selectedCountry.setTypeface(typeFace, style);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * To get call back on country selection a onCountryChangeListener must be registered.
     *
     * @param onCountryChangeListener
     */
    public void setOnCountryChangeListener(OnCountryChangeListener onCountryChangeListener) {
        this.onCountryChangeListener = onCountryChangeListener;
    }

    /**
     * Modifies size of flag in CCP view
     *
     * @param flagSize size in pixels
     */
    public void setFlagSize(int flagSize) {
        imageViewFlag.getLayoutParams().height = flagSize;
        imageViewFlag.requestLayout();
    }

    public void showFlag(boolean showFlag) {
        this.showFlag = showFlag;
        if (showFlag) {
            linearFlagHolder.setVisibility(VISIBLE);
        } else {
            linearFlagHolder.setVisibility(GONE);
        }
    }

    public void showFullName(boolean showFullName) {
        this.showFullName = showFullName;
        setSelectedCountry(selectedCountry);
    }

    /**
     * SelectionDialogSearch is the facility to search through the list of country while selecting.
     *
     * @return true if search is set allowed
     */
    public boolean isSelectionDialogShowSearch() {
        return selectionDialogShowSearch;
    }

    /**
     * SelectionDialogSearch is the facility to search through the list of country while selecting.
     *
     * @param selectionDialogShowSearch true will allow search and false will hide search box
     */
    public void setSelectionDialogShowSearch(boolean selectionDialogShowSearch) {
        this.selectionDialogShowSearch = selectionDialogShowSearch;
    }

    public boolean isCcpClickable() {
        return ccpClickable;
    }

    /**
     * Allow click and open dialog
     *
     * @param ccpClickable
     */
    public void setCcpClickable(boolean ccpClickable) {
        this.ccpClickable = ccpClickable;
        if (!ccpClickable) {
            relativeClickConsumer.setOnClickListener(null);
            relativeClickConsumer.setClickable(false);
            relativeClickConsumer.setEnabled(false);
        } else {
            relativeClickConsumer.setOnClickListener(countryCodeHolderClickListener);
            relativeClickConsumer.setClickable(true);
            relativeClickConsumer.setEnabled(true);
        }
    }


    public boolean isShowPhoneCode() {
        return showPhoneCode;
    }

    public void setShowPhoneCode(boolean showPhoneCode) {
        this.showPhoneCode = showPhoneCode;
        setSelectedCountry(selectedCountry);
    }

    int getFastScrollerHandleColor() {
        return fastScrollerHandleColor;
    }

    /**
     * This should be the color for fast scroller handle.
     *
     * @param fastScrollerHandleColor
     */
    public void setFastScrollerHandleColor(int fastScrollerHandleColor) {
        this.fastScrollerHandleColor = fastScrollerHandleColor;
    }

    int getFastScrollerBubbleTextAppearance() {
        return fastScrollerBubbleTextAppearance;
    }

    /**
     * This sets text appearance for fast scroller index character
     *
     * @param fastScrollerBubbleTextAppearance should be reference id of textappereance style. i.e. R.style.myBubbleTextAppearance
     */
    public void setFastScrollerBubbleTextAppearance(int fastScrollerBubbleTextAppearance) {
        this.fastScrollerBubbleTextAppearance = fastScrollerBubbleTextAppearance;
    }

    boolean isShowFastScroller() {
        return showFastScroller;
    }

    /**
     * Set visibility of fast scroller.
     *
     * @param showFastScroller
     */
    public void setShowFastScroller(boolean showFastScroller) {
        this.showFastScroller = showFastScroller;
    }

    int getFastScrollerBubbleColor() {
        return fastScrollerBubbleColor;
    }

    /**
     * Sets bubble color for fast scroller
     *
     * @param fastScrollerBubbleColor
     */
    public void setFastScrollerBubbleColor(int fastScrollerBubbleColor) {
        this.fastScrollerBubbleColor = fastScrollerBubbleColor;
    }

    /**
     * Update every time new language is supported #languageSupport
     */
    //add an entry for your language in attrs.xml's <attr name="language" format="enum"> enum.
    //add getMasterListForLanguage() to Country.java

    //add here so that language can be set programmatically
    public enum Language {
        ARABIC, BENGALI, CHINESE_SIMPLIFIED, ENGLISH, FRENCH, GERMAN, GUJARATI, HINDI, JAPANESE, INDONESIA, PORTUGUESE, RUSSIAN, SPANISH, HEBREW, CHINESE_TRADITIONAL, KOREAN
    }

    /*
    interface to set change listener
     */
    public interface OnCountryChangeListener {
        void onCountrySelected();
    }
}