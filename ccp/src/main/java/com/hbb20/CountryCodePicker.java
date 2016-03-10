package com.hbb20;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.support.annotation.DimenRes;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by hbb20 on 11/1/16.
 */
public class CountryCodePicker extends RelativeLayout {

    static String TAG = "CCP";
    static String BUNDLE_SELECTED_CODE = "selctedCode";
    static int LIB_DEFAULT_COUNTRY_CODE = 91;
    int defaultCountryCode;
    Context context;
    View holderView;
    LayoutInflater mInflater;
    TextView textView_selectedCountry;
    EditText editText_registeredCarrierNumber;
    RelativeLayout relative_holder;
    ImageView imageViewArrow;
    Country selectedCountry;
    Country defaultCountry;
    CountryCodePicker codePicker;
    int contentColor;

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
        Log.d(TAG, "Initialization of CCP");
        mInflater = LayoutInflater.from(context);
        holderView = mInflater.inflate(R.layout.layout_code_picker, this, true);
        textView_selectedCountry = (TextView) holderView.findViewById(R.id.textView_selectedCountry);
        relative_holder = (RelativeLayout) holderView.findViewById(R.id.relative_countryCodeHolder);
        imageViewArrow = (ImageView) holderView.findViewById(R.id.imageView_arrow);
        codePicker = this;

        applyCustomProperty(attrs);
        relative_holder.setOnClickListener(countryCodeHolderClickListener);
    }


    private void applyCustomProperty(AttributeSet attrs) {
        Log.d(TAG, "Applying custom property");
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CountryCodePicker,
                0, 0);
        //default country code

        try {
            //default country
            //if no country code is specified, 1 will be the default countryCode.
            if (!isInEditMode()) {
                int defaultCountryCode = a.getInteger(R.styleable.CountryCodePicker_defaultCode, LIB_DEFAULT_COUNTRY_CODE);

                //if invalid country is set using xml, it will be replaced with LIB_DEFAULT_COUNTRY_CODE
                if (Country.getCountryForCode(context, defaultCountryCode) == null) {
                    defaultCountryCode = LIB_DEFAULT_COUNTRY_CODE;
                }
                setDefaultCountryCode(defaultCountryCode);
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

            //text size
            int textSize = a.getDimensionPixelSize(R.styleable.CountryCodePicker_textSize, 0);
            if (textSize > 0) {
                textView_selectedCountry.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }

            //arrow size
            int arrowSize = a.getDimensionPixelSize(R.styleable.CountryCodePicker_arrowSize, 0);
            if (arrowSize > 0) {
                LayoutParams params = (LayoutParams) imageViewArrow.getLayoutParams();
                params.width = arrowSize;
                params.height=arrowSize;
                imageViewArrow.setLayoutParams(params);
            }

            //preview country code
            if (isInEditMode()) {
                int previewCode = a.getInteger(R.styleable.CountryCodePicker_defaultCode, 91);
                String previewNameCode = a.getString(R.styleable.CountryCodePicker_previewNameCode);
                if (previewCode != 91 && previewNameCode != null && previewNameCode.length() == 2) {
                    textView_selectedCountry.setText("(" + previewNameCode.toUpperCase() + ") +" + previewCode);
                }
            }

        } finally {
            a.recycle();
        }

    }

    private Country getDefaultCountry() {
        return defaultCountry;
    }

    private void setDefaultCountry(Country defaultCountry) {
        this.defaultCountry = defaultCountry;
        Log.d(TAG, "Setting default country:" + defaultCountry.logString());
    }

    View.OnClickListener countryCodeHolderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CountryCodeDialog.openCountryCodeDialog(codePicker);
        }
    };

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
            selectedCountry = Country.getCountryForCode(context, defaultCountryCode);
        }
        textView_selectedCountry.setText("(" + selectedCountry.getNameCode() + ")  +" + selectedCountry.getPhoneCode());
        Log.d(TAG, "Setting selected country:" + selectedCountry.logString());
    }

    private View getHolderView() {
        return holderView;
    }

    private void setHolderView(View holderView) {
        this.holderView = holderView;
    }

    private RelativeLayout getRelative_holder() {
        return relative_holder;
    }

    private void setRelative_holder(RelativeLayout relative_holder) {
        this.relative_holder = relative_holder;
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

    /**
     * Publicly available functions from library
     */

    /**
     * Related to default country
     */

    /**
     * Default country code defines your default country.
     * Whenever invalid / improper number is found in setCountryForCode() /  setFullNumber(), it CCP will set to default country.
     * This function will not set default country as selected in CCP. To set default country in CCP call resetToDefaultCountry() right after this call.
     * If invalid defaultCountryCode is applied, it won't be changed.
     *
     * @param defaultCountryCode code of your default country
     *                           if you want to set IN +91(India) as default country, defaultCountryCode =  91
     *                           if you want to set JP +81(Japan) as default country, defaultCountryCode =  81
     */
    public void setDefaultCountryCode(int defaultCountryCode) {
        Country defaultCountry = Country.getCountryForCode(context, defaultCountryCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (defaultCountry == null) { //if no correct country is found
            Log.d(TAG, "No country for code " + defaultCountryCode + " is found");
        } else { //if correct country is found, set the country
            this.defaultCountryCode = defaultCountryCode;
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
        return 0;
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
     * reset the default country as selected country.
     */
    public void resetToDefaultCountry() {
        setSelectedCountry(defaultCountry);
    }

    /**
     * Related to selected country
     */

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
        return 0;
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
    public void setCountryForCode(int countryCode) {
        Country country = Country.getCountryForCode(context, countryCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (defaultCountry == null) {
            defaultCountry = Country.getCountryForCode(context, defaultCountryCode);
        }
        setSelectedCountry(country);
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
     * This function combines selected country code from CCP and carrier number from @param editTextCarrierNumber and prefix "+"
     *
     * @return Full number is countryCode + carrierNumber i.e countryCode= 91 and carrier number= 8866667722, this will return "+918866667722"
     */
    public String getFullNumberWithPlus() {
        String fullNumber = "+" + getFullNumber();
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
        Country country = Country.getCountryForNumber(context, fullNumber);
        setSelectedCountry(country);
        String carrierNumber = detectCarrierNumber(fullNumber, country);
        if (getEditText_registeredCarrierNumber() != null) {
            getEditText_registeredCarrierNumber().setText(carrierNumber);
        } else {
            Log.w(TAG, "EditText for carrier number is not registered. Register it using registerCarrierNumberEditText() before getFullNumber() or setFullNumber().");
        }
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
     * Modifies size of text in side CCP view.
     * @param textSize size of text in pixels
     */
    public void setTextSize(int textSize){
        if ( textSize > 0) {
            textView_selectedCountry.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
    }

    /**
     * Modifies size of downArrow in CCP view
     * @param arrowSize size in pixels
     */
    public void setArrowSize(int arrowSize){
        if(arrowSize>0){
            LayoutParams params = (LayoutParams) imageViewArrow.getLayoutParams();
            params.width = arrowSize;
            params.height=arrowSize;
            imageViewArrow.setLayoutParams(params);
        }
    }
}
