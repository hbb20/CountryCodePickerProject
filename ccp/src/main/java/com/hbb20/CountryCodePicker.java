package com.hbb20;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.michaelrocks.libphonenumber.android.NumberParseException;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

/**
 * Created by hbb20 on 11/1/16.
 */
public class CountryCodePicker extends RelativeLayout {

    static final int DEFAULT_UNSET = -99;
    static String TAG = "CCP";
    static String BUNDLE_SELECTED_CODE = "selectedCode";
    static int LIB_DEFAULT_COUNTRY_CODE = 91;
    private static int TEXT_GRAVITY_LEFT = -1, TEXT_GRAVITY_RIGHT = 1, TEXT_GRAVITY_CENTER = 0;
    private static String ANDROID_NAME_SPACE = "http://schemas.android.com/apk/res/android";
    String CCP_PREF_FILE = "CCP_PREF_FILE";
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
    CCPCountry selectedCCPCountry;
    CCPCountry defaultCCPCountry;
    RelativeLayout relativeClickConsumer;
    CountryCodePicker codePicker;
    TextGravity currentTextGravity;
    // see attr.xml to see corresponding values for pref
    AutoDetectionPref selectedAutoDetectionPref;
    PhoneNumberUtil phoneUtil;
    boolean showNameCode = false;
    boolean showPhoneCode = true;
    boolean ccpDialogShowPhoneCode = true;
    boolean showFlag = true;
    boolean showFullName = false;
    boolean showFastScroller;
    boolean ccpDialogShowTitle = true;
    boolean ccpDialogShowFlag = true;
    boolean searchAllowed = true;
    boolean showArrow = true;
    boolean showCloseIcon = false;
    boolean rememberLastSelection = false;
    boolean detectCountryWithAreaCode = true;
    boolean ccpDialogShowNameCode = true;
    PhoneNumberType hintExampleNumberType = PhoneNumberType.MOBILE;
    String selectionMemoryTag = "ccp_last_selection";
    int contentColor;
    int borderFlagColor;
    Typeface dialogTypeFace;
    int dialogTypeFaceStyle;
    List<CCPCountry> preferredCountries;
    int ccpTextgGravity = TEXT_GRAVITY_CENTER;
    //this will be "AU,IN,US"
    String countryPreference;
    int fastScrollerBubbleColor = 0;
    List<CCPCountry> customMasterCountriesList;
    //this will be "AU,IN,US"
    String customMasterCountriesParam, excludedCountriesParam;
    Language customDefaultLanguage = Language.ENGLISH;
    Language languageToApply = Language.ENGLISH;

    boolean dialogKeyboardAutoPopup = true;
    boolean ccpClickable = true;
    boolean autoDetectLanguageEnabled, autoDetectCountryEnabled, numberAutoFormattingEnabled, hintExampleNumberEnabled;
    String xmlWidth = "notSet";
    TextWatcher validityTextWatcher;
    InternationalPhoneTextWatcher formattingTextWatcher;
    boolean reportedValidity;
    TextWatcher areaCodeCountryDetectorTextWatcher;
    boolean countryDetectionBasedOnAreaAllowed;
    String lastCheckedAreaCode = null;
    private OnCountryChangeListener onCountryChangeListener;
    private PhoneNumberValidityChangeListener phoneNumberValidityChangeListener;
    private FailureListener failureListener;
    private DialogEventsListener dialogEventsListener;
    private int fastScrollerHandleColor;
    private int dialogBackgroundColor, dialogTextColor, dialogSearchEditTextTintColor;
    private int fastScrollerBubbleTextAppearance;
    private CCPCountryGroup currentCountryGroup;
    View.OnClickListener countryCodeHolderClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isCcpClickable()) {
                launchCountrySelectionDialog();
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

    private boolean isNumberAutoFormattingEnabled() {
        return numberAutoFormattingEnabled;
    }

    /**
     * This will set boolean for numberAutoFormattingEnabled and refresh formattingTextWatcher
     *
     * @param numberAutoFormattingEnabled
     */
    public void setNumberAutoFormattingEnabled(boolean numberAutoFormattingEnabled) {
        this.numberAutoFormattingEnabled = numberAutoFormattingEnabled;
        if (editText_registeredCarrierNumber != null) {
            updateFormattingTextWatcher();
        }
    }

    private void init(AttributeSet attrs) {
        mInflater = LayoutInflater.from(context);

        xmlWidth = attrs.getAttributeValue(ANDROID_NAME_SPACE, "layout_width");
        Log.d(TAG, "init:xmlWidth " + xmlWidth);
        removeAllViewsInLayout();
        //at run time, match parent value returns LayoutParams.MATCH_PARENT ("-1"), for some android xml preview it returns "fill_parent"
        if (xmlWidth != null && (xmlWidth.equals(LayoutParams.MATCH_PARENT + "") || xmlWidth.equals(LayoutParams.FILL_PARENT + "") || xmlWidth.equals("fill_parent") || xmlWidth.equals("match_parent"))) {
            holderView = mInflater.inflate(R.layout.layout_full_width_code_picker, this, true);
        } else {
            holderView = mInflater.inflate(R.layout.layout_code_picker, this, true);
        }

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
            showNameCode = a.getBoolean(R.styleable.CountryCodePicker_ccp_showNameCode, true);

            //number auto formatting
            numberAutoFormattingEnabled = a.getBoolean(R.styleable.CountryCodePicker_ccp_autoFormatNumber, true);

            //show phone code.
            showPhoneCode = a.getBoolean(R.styleable.CountryCodePicker_ccp_showPhoneCode, true);

            //show phone code on dialog
            ccpDialogShowPhoneCode = a.getBoolean(R.styleable.CountryCodePicker_ccpDialog_showPhoneCode, showPhoneCode);

            //show name code on dialog
            ccpDialogShowNameCode = a.getBoolean(R.styleable.CountryCodePicker_ccpDialog_showNameCode, true);

            //show title on dialog
            ccpDialogShowTitle = a.getBoolean(R.styleable.CountryCodePicker_ccpDialog_showTitle, true);

            //show flag on dialog
            ccpDialogShowFlag = a.getBoolean(R.styleable.CountryCodePicker_ccpDialog_showFlag, true);

            //show full name
            showFullName = a.getBoolean(R.styleable.CountryCodePicker_ccp_showFullName, false);

            //show fast scroller
            showFastScroller = a.getBoolean(R.styleable.CountryCodePicker_ccpDialog_showFastScroller, true);

            //bubble color
            fastScrollerBubbleColor = a.getColor(R.styleable.CountryCodePicker_ccpDialog_fastScroller_bubbleColor, 0);

            //scroller handle color
            fastScrollerHandleColor = a.getColor(R.styleable.CountryCodePicker_ccpDialog_fastScroller_handleColor, 0);

            //scroller text appearance
            fastScrollerBubbleTextAppearance = a.getResourceId(R.styleable.CountryCodePicker_ccpDialog_fastScroller_bubbleTextAppearance, 0);

            //auto detect language
            autoDetectLanguageEnabled = a.getBoolean(R.styleable.CountryCodePicker_ccp_autoDetectLanguage, false);

            //detect country from area code
            detectCountryWithAreaCode = a.getBoolean(R.styleable.CountryCodePicker_ccp_areaCodeDetectedCountry, true);

            //remember last selection
            rememberLastSelection = a.getBoolean(R.styleable.CountryCodePicker_ccp_rememberLastSelection, false);

            //example number hint enabled?
            hintExampleNumberEnabled = a.getBoolean(R.styleable.CountryCodePicker_ccp_hintExampleNumber, false);

            //example number hint type
            int hintNumberTypeIndex = a.getInt(R.styleable.CountryCodePicker_ccp_hintExampleNumberType, 0);
            hintExampleNumberType = PhoneNumberType.values()[hintNumberTypeIndex];

            //memory tag name for selection
            selectionMemoryTag = a.getString(R.styleable.CountryCodePicker_ccp_selectionMemoryTag);
            if (selectionMemoryTag == null) {
                selectionMemoryTag = "CCP_last_selection";
            }

            //country auto detection pref
            int autoDetectionPrefValue = a.getInt(R.styleable.CountryCodePicker_ccp_countryAutoDetectionPref, 123);
            selectedAutoDetectionPref = AutoDetectionPref.getPrefForValue(String.valueOf(autoDetectionPrefValue));

            //auto detect county
            autoDetectCountryEnabled = a.getBoolean(R.styleable.CountryCodePicker_ccp_autoDetectCountry, false);

            //show arrow
            showArrow = a.getBoolean(R.styleable.CountryCodePicker_ccp_showArrow, true);
            refreshArrowViewVisibility();

            //show close icon
            showCloseIcon = a.getBoolean(R.styleable.CountryCodePicker_ccpDialog_showCloseIcon, false);

            //show flag
            showFlag(a.getBoolean(R.styleable.CountryCodePicker_ccp_showFlag, true));

            //autopop keyboard
            setDialogKeyboardAutoPopup(a.getBoolean(R.styleable.CountryCodePicker_ccpDialog_keyboardAutoPopup, true));

            //if custom default language is specified, then set it as custom else sets english as custom
            int attrLanguage;
            attrLanguage = a.getInt(R.styleable.CountryCodePicker_ccp_defaultLanguage, Language.ENGLISH.ordinal());
            customDefaultLanguage = getLanguageEnum(attrLanguage);
            updateLanguageToApply();

            //custom master list
            customMasterCountriesParam = a.getString(R.styleable.CountryCodePicker_ccp_customMasterCountries);
            excludedCountriesParam = a.getString(R.styleable.CountryCodePicker_ccp_excludedCountries);
            if (!isInEditMode()) {
                refreshCustomMasterList();
            }

            //preference
            countryPreference = a.getString(R.styleable.CountryCodePicker_ccp_countryPreference);
            //as3 is raising problem while rendering preview. to avoid such issue, it will update preferred list only on run time.
            if (!isInEditMode()) {
                refreshPreferredCountries();
            }

            //text gravity
            if (a.hasValue(R.styleable.CountryCodePicker_ccp_textGravity)) {
                ccpTextgGravity = a.getInt(R.styleable.CountryCodePicker_ccp_textGravity, TEXT_GRAVITY_RIGHT);
            }
            applyTextGravity(ccpTextgGravity);

            //default country
            //AS 3 has some problem with reading list so this is to make CCP preview work
            defaultCountryNameCode = a.getString(R.styleable.CountryCodePicker_ccp_defaultNameCode);
            boolean setUsingNameCode = false;
            if (defaultCountryNameCode != null && defaultCountryNameCode.length() != 0) {
                if (!isInEditMode()) {
                    if (CCPCountry.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), defaultCountryNameCode) != null) {
                        setUsingNameCode = true;
                        setDefaultCountry(CCPCountry.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), defaultCountryNameCode));
                        setSelectedCountry(defaultCCPCountry);
                    }
                } else {
                    if (CCPCountry.getCountryForNameCodeFromEnglishList(defaultCountryNameCode) != null) {
                        setUsingNameCode = true;
                        setDefaultCountry(CCPCountry.getCountryForNameCodeFromEnglishList(defaultCountryNameCode));
                        setSelectedCountry(defaultCCPCountry);
                    }
                }

                //when it was not set means something was wrong with name code
                if (!setUsingNameCode) {
                    setDefaultCountry(CCPCountry.getCountryForNameCodeFromEnglishList("IN"));
                    setSelectedCountry(defaultCCPCountry);
                    setUsingNameCode = true;
                }
            }

            //if default country is not set using name code.
            int defaultCountryCode = a.getInteger(R.styleable.CountryCodePicker_ccp_defaultPhoneCode, -1);
            if (!setUsingNameCode && defaultCountryCode != -1) {
                if (!isInEditMode()) {
                    //if invalid country is set using xml, it will be replaced with LIB_DEFAULT_COUNTRY_CODE
                    if (defaultCountryCode != -1 && CCPCountry.getCountryForCode(getContext(), getLanguageToApply(), preferredCountries, defaultCountryCode) == null) {
                        defaultCountryCode = LIB_DEFAULT_COUNTRY_CODE;
                    }
                    setDefaultCountryUsingPhoneCode(defaultCountryCode);
                    setSelectedCountry(defaultCCPCountry);
                } else {
                    //when it is in edit mode, we will check in english list only.
                    CCPCountry defaultCountry = CCPCountry.getCountryForCodeFromEnglishList(defaultCountryCode + "");
                    if (defaultCountry == null) {
                        defaultCountry = CCPCountry.getCountryForCodeFromEnglishList(LIB_DEFAULT_COUNTRY_CODE + "");
                    }
                    setDefaultCountry(defaultCountry);
                    setSelectedCountry(defaultCountry);
                }
            }

            //if default country is not set using nameCode or phone code, let's set library default as default
            if (getDefaultCountry() == null) {
                setDefaultCountry(CCPCountry.getCountryForNameCodeFromEnglishList("IN"));
                if (getSelectedCountry() == null) {
                    setSelectedCountry(defaultCCPCountry);
                }
            }


            //set auto detected country
            if (isAutoDetectCountryEnabled() && !isInEditMode()) {
                setAutoDetectedCountry(true);
            }

            //set last selection
            if (rememberLastSelection && !isInEditMode()) {
                loadLastSelectedCountryInCCP();
            }

            //content color
            int contentColor;
            if (isInEditMode()) {
                contentColor = a.getColor(R.styleable.CountryCodePicker_ccp_contentColor, 0);
            } else {
                contentColor = a.getColor(R.styleable.CountryCodePicker_ccp_contentColor, context.getResources().getColor(R.color.defaultContentColor));
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

            //dialog colors
            setDialogBackgroundColor(a.getColor(R.styleable.CountryCodePicker_ccpDialog_backgroundColor, 0));
            setDialogTextColor(a.getColor(R.styleable.CountryCodePicker_ccpDialog_textColor, 0));
            setDialogSearchEditTextTintColor(a.getColor(R.styleable.CountryCodePicker_ccpDialog_searchEditTextTint, 0));

            //text size
            int textSize = a.getDimensionPixelSize(R.styleable.CountryCodePicker_ccp_textSize, 0);
            if (textSize > 0) {
                textView_selectedCountry.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                setFlagSize(textSize);
                setArrowSize(textSize);
            }

            //if arrow size is explicitly defined
            int arrowSize = a.getDimensionPixelSize(R.styleable.CountryCodePicker_ccp_arrowSize, 0);
            if (arrowSize > 0) {
                setArrowSize(arrowSize);
            }

            searchAllowed = a.getBoolean(R.styleable.CountryCodePicker_ccpDialog_allowSearch, true);
            setCcpClickable(a.getBoolean(R.styleable.CountryCodePicker_ccp_clickable, true));

        } catch (Exception e) {
            textView_selectedCountry.setTextSize(10);
            textView_selectedCountry.setText(e.toString());
        } finally {
            a.recycle();
        }
        Log.d(TAG, "end:xmlWidth " + xmlWidth);
    }

    private void refreshArrowViewVisibility() {
        if (showArrow) {
            imageViewArrow.setVisibility(VISIBLE);
        } else {
            imageViewArrow.setVisibility(GONE);
        }
    }

    /**
     * this will read last selected country name code from the shared pref.
     * if that name code is not null, load that country in the CCP
     * else leaves as it is.(when used for the first time)
     */
    private void loadLastSelectedCountryInCCP() {
        //get the shared pref
        SharedPreferences sharedPref = context.getSharedPreferences(
                CCP_PREF_FILE, Context.MODE_PRIVATE);

        // read last selection value
        String lastSelectedCountryNameCode = sharedPref.getString(selectionMemoryTag, null);

        //if last selection value is not null, load it into the CCP
        if (lastSelectedCountryNameCode != null) {
            setCountryForNameCode(lastSelectedCountryNameCode);
        }
    }

    /**
     * This will store the selected name code in the preferences
     *
     * @param selectedCountryNameCode name code of the selected country
     */
    void storeSelectedCountryNameCode(String selectedCountryNameCode) {
        //get the shared pref
        SharedPreferences sharedPref = context.getSharedPreferences(
                CCP_PREF_FILE, Context.MODE_PRIVATE);

        //we want to write in shared pref, so lets get editor for it
        SharedPreferences.Editor editor = sharedPref.edit();

        // add our last selection country name code in pref
        editor.putString(selectionMemoryTag, selectedCountryNameCode);

        //finally save it...
        editor.apply();
    }

    boolean isCcpDialogShowPhoneCode() {
        return ccpDialogShowPhoneCode;
    }

    /**
     * To show/hide phone code from country selection dialog
     *
     * @param ccpDialogShowPhoneCode
     */
    public void setCcpDialogShowPhoneCode(boolean ccpDialogShowPhoneCode) {
        this.ccpDialogShowPhoneCode = ccpDialogShowPhoneCode;
    }

    /**
     * To show/hide name code from country selection dialog
     */
    public boolean getCcpDialogShowNameCode() {
        return this.ccpDialogShowNameCode;
    }

    /**
     * To show/hide title from country selection dialog
     *
     * @param ccpDialogShowTitle
     */
    public void setCcpDialogShowTitle(boolean ccpDialogShowTitle) {
        this.ccpDialogShowTitle = ccpDialogShowTitle;
    }

    /**
     * To show/hide name code from country selection dialog
     */
    public boolean getCcpDialogShowTitle() {
        return this.ccpDialogShowTitle;
    }

    /**
     * To show/hide flag from country selection dialog
     */
    public boolean getCcpDialogShowFlag() {
        return this.ccpDialogShowFlag;
    }

    /**
     * To show/hide flag from country selection dialog
     *
     * @param ccpDialogShowFlag
     */
    public void setCcpDialogShowFlag(boolean ccpDialogShowFlag) {
        this.ccpDialogShowFlag = ccpDialogShowFlag;
    }

    /**
     * To show/hide name code from country selection dialog
     *
     * @param ccpDialogShowNameCode
     */
    public void setCcpDialogShowNameCode(boolean ccpDialogShowNameCode) {
        this.ccpDialogShowNameCode = ccpDialogShowNameCode;
    }

    boolean isShowPhoneCode() {
        return showPhoneCode;
    }

    /**
     * To show/hide phone code from ccp view
     *
     * @param showPhoneCode
     */
    public void setShowPhoneCode(boolean showPhoneCode) {
        this.showPhoneCode = showPhoneCode;
        setSelectedCountry(selectedCCPCountry);
    }

    /**
     * @return registered dialog event listener
     */
    protected DialogEventsListener getDialogEventsListener() {
        return dialogEventsListener;
    }

    /**
     * Dialog events listener will give call backs on various dialog events
     *
     * @param dialogEventsListener
     */
    public void setDialogEventsListener(DialogEventsListener dialogEventsListener) {
        this.dialogEventsListener = dialogEventsListener;
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

    TextGravity getCurrentTextGravity() {
        return currentTextGravity;
    }

    /**
     * When width is set "match_parent", this gravity will set placement of text (Between flag and down arrow).
     *
     * @param textGravity expected placement
     */
    public void setCurrentTextGravity(TextGravity textGravity) {
        this.currentTextGravity = textGravity;
        applyTextGravity(textGravity.enumIndex);
    }

    private void applyTextGravity(int enumIndex) {
        if (enumIndex == TextGravity.LEFT.enumIndex) {
            textView_selectedCountry.setGravity(Gravity.LEFT);
        } else if (enumIndex == TextGravity.CENTER.enumIndex) {
            textView_selectedCountry.setGravity(Gravity.CENTER);
        } else {
            textView_selectedCountry.setGravity(Gravity.RIGHT);
        }
    }

    /**
     * which language to show is decided based on
     * autoDetectLanguage flag
     * if autoDetectLanguage is true, then it should check language based on locale, if no language is found based on locale, customDefault language will returned
     * else autoDetectLanguage is false, then customDefaultLanguage will be returned.
     *
     * @return
     */
    private void updateLanguageToApply() {
        //when in edit mode, it will return default language only
        if (isInEditMode()) {
            if (customDefaultLanguage != null) {
                languageToApply = customDefaultLanguage;
            } else {
                languageToApply = Language.ENGLISH;
            }
        } else {
            if (isAutoDetectLanguageEnabled()) {
                Language localeBasedLanguage = getCCPLanguageFromLocale();
                if (localeBasedLanguage == null) { //if no language is found from locale
                    if (getCustomDefaultLanguage() != null) { //and custom language is defined
                        languageToApply = getCustomDefaultLanguage();
                    } else {
                        languageToApply = Language.ENGLISH;
                    }
                } else {
                    languageToApply = localeBasedLanguage;
                }
            } else {
                if (getCustomDefaultLanguage() != null) {
                    languageToApply = customDefaultLanguage;
                } else {
                    languageToApply = Language.ENGLISH;  //library default
                }
            }
        }
        Log.d(TAG, "updateLanguageToApply: " + languageToApply);
    }

    private Language getCCPLanguageFromLocale() {
        Locale currentLocale = context.getResources().getConfiguration().locale;
        Log.d(TAG, "getCCPLanguageFromLocale: current locale language" + currentLocale.getLanguage());
        for (Language language : Language.values()) {
            if (language.getCode().equalsIgnoreCase(currentLocale.getLanguage())) {
                return language;
            }
        }
        return null;
    }

    private CCPCountry getDefaultCountry() {
        return defaultCCPCountry;
    }

    private void setDefaultCountry(CCPCountry defaultCCPCountry) {
        this.defaultCCPCountry = defaultCCPCountry;
        //        Log.d(TAG, "Setting default country:" + defaultCountry.logString());
    }

    public TextView getTextView_selectedCountry() {
        return textView_selectedCountry;
    }

    public void setTextView_selectedCountry(TextView textView_selectedCountry) {
        this.textView_selectedCountry = textView_selectedCountry;
    }

    public ImageView getImageViewFlag() {
        return imageViewFlag;
    }

    public void setImageViewFlag(ImageView imageViewFlag) {
        this.imageViewFlag = imageViewFlag;
    }

    private CCPCountry getSelectedCountry() {
        if (selectedCCPCountry == null) {
            setSelectedCountry(getDefaultCountry());
        }
        return selectedCCPCountry;
    }

    void setSelectedCountry(CCPCountry selectedCCPCountry) {

        //force disable area code country detection
        countryDetectionBasedOnAreaAllowed = false;
        lastCheckedAreaCode = "";

        //as soon as country is selected, textView should be updated
        if (selectedCCPCountry == null) {
            selectedCCPCountry = CCPCountry.getCountryForCode(getContext(), getLanguageToApply(), preferredCountries, defaultCountryCode);
        }

        this.selectedCCPCountry = selectedCCPCountry;

        String displayText = "";

        // add full name to if required
        if (showFullName) {
            displayText = displayText + selectedCCPCountry.getName();
        }

        // adds name code if required
        if (showNameCode) {
            if (showFullName) {
                displayText += " (" + selectedCCPCountry.getNameCode().toUpperCase() + ")";
            } else {
                displayText += " " + selectedCCPCountry.getNameCode().toUpperCase();
            }
        }

        // hide phone code if required
        if (showPhoneCode) {
            if (displayText.length() > 0) {
                displayText += "  ";
            }
            displayText += "+" + selectedCCPCountry.getPhoneCode();
        }

        textView_selectedCountry.setText(displayText);

        //avoid blank state of ccp
        if (showFlag == false && displayText.length() == 0) {
            displayText += "+" + selectedCCPCountry.getPhoneCode();
            textView_selectedCountry.setText(displayText);
        }

        if (onCountryChangeListener != null) {
            onCountryChangeListener.onCountrySelected();
        }

        imageViewFlag.setImageResource(selectedCCPCountry.getFlagID());
        //        Log.d(TAG, "Setting selected country:" + selectedCountry.logString());

        updateFormattingTextWatcher();

        updateHint();

        //notify to registered validity listener
        if (editText_registeredCarrierNumber != null && phoneNumberValidityChangeListener != null) {
            reportedValidity = isValidFullNumber();
            phoneNumberValidityChangeListener.onValidityChanged(reportedValidity);
        }

        //once updates are done, this will release lock
        countryDetectionBasedOnAreaAllowed = true;

        //if the country was auto detected based on area code, this will correct the cursor position.
        if (countryChangedDueToAreaCode) {
            try {
                editText_registeredCarrierNumber.setSelection(lastCursorPosition);
                countryChangedDueToAreaCode = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //update country group
        updateCountryGroup();
    }

    /**
     * update country group
     */
    private void updateCountryGroup() {
        currentCountryGroup = CCPCountryGroup.getCountryGroupForPhoneCode(getSelectedCountryCodeAsInt());
    }

    /**
     * updates hint
     */
    private void updateHint() {
        if (editText_registeredCarrierNumber != null && hintExampleNumberEnabled) {
            String formattedNumber = "";
            Phonenumber.PhoneNumber exampleNumber = getPhoneUtil().getExampleNumberForType(getSelectedCountryNameCode(), getSelectedHintNumberType());
            if (exampleNumber != null) {
                    formattedNumber = exampleNumber.getNationalNumber() + "";
                    Log.d(TAG, "updateHint: " + formattedNumber);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        formattedNumber = PhoneNumberUtils.formatNumber(getSelectedCountryCodeWithPlus() + formattedNumber, getSelectedCountryNameCode());
                    } else {
                        formattedNumber = PhoneNumberUtils.formatNumber(getSelectedCountryCodeWithPlus() + formattedNumber + formattedNumber);
                    }
                formattedNumber = formattedNumber.substring(getSelectedCountryCodeWithPlus().length()).trim();
                Log.d(TAG, "updateHint: after format " + formattedNumber + " " + selectionMemoryTag);
            } else {
                Log.w(TAG, "updateHint: No example number found for this country (" + getSelectedCountryNameCode() + ") or this type (" + hintExampleNumberType.name() + ").");
            }
            editText_registeredCarrierNumber.setHint(formattedNumber);
        }
    }

    /**
     * this function maps CountryCodePicker.PhoneNumberType to PhoneNumberUtil.PhoneNumberType.
     *
     * @return respective PhoneNumberUtil.PhoneNumberType based on selected CountryCodePicker.PhoneNumberType.
     */
    private PhoneNumberUtil.PhoneNumberType getSelectedHintNumberType() {
        switch (hintExampleNumberType) {
            case MOBILE:
                return PhoneNumberUtil.PhoneNumberType.MOBILE;
            case FIXED_LINE:
                return PhoneNumberUtil.PhoneNumberType.FIXED_LINE;
            case FIXED_LINE_OR_MOBILE:
                return PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE;
            case TOLL_FREE:
                return PhoneNumberUtil.PhoneNumberType.TOLL_FREE;
            case PREMIUM_RATE:
                return PhoneNumberUtil.PhoneNumberType.PREMIUM_RATE;
            case SHARED_COST:
                return PhoneNumberUtil.PhoneNumberType.SHARED_COST;
            case VOIP:
                return PhoneNumberUtil.PhoneNumberType.VOIP;
            case PERSONAL_NUMBER:
                return PhoneNumberUtil.PhoneNumberType.PERSONAL_NUMBER;
            case PAGER:
                return PhoneNumberUtil.PhoneNumberType.PAGER;
            case UAN:
                return PhoneNumberUtil.PhoneNumberType.UAN;
            case VOICEMAIL:
                return PhoneNumberUtil.PhoneNumberType.VOICEMAIL;
            case UNKNOWN:

                return PhoneNumberUtil.PhoneNumberType.UNKNOWN;
            default:
                return PhoneNumberUtil.PhoneNumberType.MOBILE;
        }
    }

    Language getLanguageToApply() {
        if (languageToApply == null) {
            updateLanguageToApply();
        }
        return languageToApply;
    }

    void setLanguageToApply(Language languageToApply) {
        this.languageToApply = languageToApply;
    }

    private void updateFormattingTextWatcher() {
        if (editText_registeredCarrierNumber != null && selectedCCPCountry != null) {
            Log.d(TAG, "updateFormattingTextWatcher: " + selectionMemoryTag);
            String enteredValue = getEditText_registeredCarrierNumber().getText().toString();
            String digitsValue = PhoneNumberUtil.normalizeDigitsOnly(enteredValue);

            if (formattingTextWatcher != null) {
                editText_registeredCarrierNumber.removeTextChangedListener(formattingTextWatcher);
            }

            if (areaCodeCountryDetectorTextWatcher != null) {
                editText_registeredCarrierNumber.removeTextChangedListener(areaCodeCountryDetectorTextWatcher);
            }

            if (numberAutoFormattingEnabled) {
                formattingTextWatcher = new InternationalPhoneTextWatcher(context, getSelectedCountryNameCode(), getSelectedCountryCodeAsInt());
                editText_registeredCarrierNumber.addTextChangedListener(formattingTextWatcher);
            }

            //if country detection from area code is enabled, then it will add areaCodeCountryDetectorTextWatcher
            if (detectCountryWithAreaCode) {
                areaCodeCountryDetectorTextWatcher = getCountryDetectorTextWatcher();
                editText_registeredCarrierNumber.addTextChangedListener(areaCodeCountryDetectorTextWatcher);
            }

            //text watcher stops working when it finds non digit character in previous phone code. This will reset its function
            editText_registeredCarrierNumber.setText("");
            editText_registeredCarrierNumber.setText(digitsValue);
            editText_registeredCarrierNumber.setSelection(editText_registeredCarrierNumber.getText().length());
        } else {
            if (editText_registeredCarrierNumber == null) {
                Log.d(TAG, "updateFormattingTextWatcher: EditText not registered " + selectionMemoryTag);
            } else {
                Log.d(TAG, "updateFormattingTextWatcher: selected country is null " + selectionMemoryTag);
            }
        }
    }

    int lastCursorPosition = 0;
    boolean countryChangedDueToAreaCode = false;
    /**
     * This updates country dynamically as user types in area code
     *
     * @return
     */
    private TextWatcher getCountryDetectorTextWatcher() {

        if (editText_registeredCarrierNumber != null) {
            if (areaCodeCountryDetectorTextWatcher == null) {
                areaCodeCountryDetectorTextWatcher = new TextWatcher() {
                    String lastCheckedNumber = null;


                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        CCPCountry selectedCountry = getSelectedCountry();
                        if (selectedCountry != null && (lastCheckedNumber == null || !lastCheckedNumber.equals(s.toString())) && countryDetectionBasedOnAreaAllowed) {
                            //possible countries
                            if (currentCountryGroup != null) {
                                String enteredValue = getEditText_registeredCarrierNumber().getText().toString();
                                if (enteredValue.length() >= currentCountryGroup.areaCodeLength) {
                                    String digitsValue = PhoneNumberUtil.normalizeDigitsOnly(enteredValue);
                                    if (digitsValue.length() >= currentCountryGroup.areaCodeLength) {
                                        String currentAreaCode = digitsValue.substring(0, currentCountryGroup.areaCodeLength);
                                        if (!currentAreaCode.equals(lastCheckedAreaCode)) {
                                            CCPCountry detectedCountry = currentCountryGroup.getCountryForAreaCode(context, getLanguageToApply(), currentAreaCode);
                                            if (!detectedCountry.equals(selectedCountry)) {
                                                countryChangedDueToAreaCode = true;
                                                lastCursorPosition = Selection.getSelectionEnd(s);
                                                setSelectedCountry(detectedCountry);
                                            }
                                            lastCheckedAreaCode = currentAreaCode;
                                        }
                                    }
                                }
                            }
                            lastCheckedNumber = s.toString();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {


                    }
                };
            }
        }
        return areaCodeCountryDetectorTextWatcher;
    }

    Language getCustomDefaultLanguage() {
        return customDefaultLanguage;
    }

    private void setCustomDefaultLanguage(Language customDefaultLanguage) {
        this.customDefaultLanguage = customDefaultLanguage;
        updateLanguageToApply();
        setSelectedCountry(CCPCountry.getCountryForNameCodeFromLibraryMasterList(context, getLanguageToApply(), selectedCCPCountry.getNameCode()));
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

    boolean isAutoDetectLanguageEnabled() {
        return autoDetectLanguageEnabled;
    }

    boolean isAutoDetectCountryEnabled() {
        return autoDetectCountryEnabled;
    }

    boolean isDialogKeyboardAutoPopup() {
        return dialogKeyboardAutoPopup;
    }

    /**
     * By default, keyboard pops up every time ccp is clicked and selection dialog is opened.
     *
     * @param dialogKeyboardAutoPopup true: to open keyboard automatically when selection dialog is opened
     *                                false: to avoid auto pop of keyboard
     */
    public void setDialogKeyboardAutoPopup(boolean dialogKeyboardAutoPopup) {
        this.dialogKeyboardAutoPopup = dialogKeyboardAutoPopup;
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

    protected boolean isShowCloseIcon() {
        return showCloseIcon;
    }

    /**
     * if true, this will give explicit close icon in CCP dialog
     *
     * @param showCloseIcon
     */
    public void showCloseIcon(boolean showCloseIcon) {
        this.showCloseIcon = showCloseIcon;
    }

    EditText getEditText_registeredCarrierNumber() {
        Log.d(TAG, "getEditText_registeredCarrierNumber");
        return editText_registeredCarrierNumber;
    }

    /**
     * this will register editText and will apply required text watchers
     *
     * @param editText_registeredCarrierNumber
     */
    void setEditText_registeredCarrierNumber(EditText editText_registeredCarrierNumber) {
        this.editText_registeredCarrierNumber = editText_registeredCarrierNumber;
        Log.d(TAG, "setEditText_registeredCarrierNumber: carrierEditTextAttached " + selectionMemoryTag);
        updateValidityTextWatcher();
        updateFormattingTextWatcher();
        updateHint();
    }

    /**
     * This function will
     * - remove existing, if any, validityTextWatcher
     * - prepare new validityTextWatcher
     * - attach validityTextWatcher
     * - do initial reporting to watcher
     */
    private void updateValidityTextWatcher() {
        try {
            editText_registeredCarrierNumber.removeTextChangedListener(validityTextWatcher);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //initial REPORTING
        reportedValidity = isValidFullNumber();
        if (phoneNumberValidityChangeListener != null) {
            phoneNumberValidityChangeListener.onValidityChanged(reportedValidity);
        }

        validityTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (phoneNumberValidityChangeListener != null) {
                    boolean currentValidity;
                    currentValidity = isValidFullNumber();
                    if (currentValidity != reportedValidity) {
                        reportedValidity = currentValidity;
                        phoneNumberValidityChangeListener.onValidityChanged(reportedValidity);
                    }
                }
            }
        };

        editText_registeredCarrierNumber.addTextChangedListener(validityTextWatcher);
    }

    private LayoutInflater getmInflater() {
        return mInflater;
    }

    private OnClickListener getCountryCodeHolderClickListener() {
        return countryCodeHolderClickListener;
    }

    int getDialogBackgroundColor() {
        return dialogBackgroundColor;
    }

    /**
     * This will be color of dialog background
     *
     * @param dialogBackgroundColor
     */
    public void setDialogBackgroundColor(int dialogBackgroundColor) {
        this.dialogBackgroundColor = dialogBackgroundColor;
    }

    int getDialogSearchEditTextTintColor() {
        return dialogSearchEditTextTintColor;
    }

    /**
     * If device is running above or equal LOLLIPOP version, this will change tint of search edittext background.
     *
     * @param dialogSearchEditTextTintColor
     */
    public void setDialogSearchEditTextTintColor(int dialogSearchEditTextTintColor) {
        this.dialogSearchEditTextTintColor = dialogSearchEditTextTintColor;
    }

    int getDialogTextColor() {
        return dialogTextColor;
    }

    /**
     * This color will be applied to
     * Title of dialog
     * Name of country
     * Phone code of country
     * "X" button to clear query
     * preferred country divider if preferred countries defined (semi transparent)
     *
     * @param dialogTextColor
     */
    public void setDialogTextColor(int dialogTextColor) {
        this.dialogTextColor = dialogTextColor;
    }

    int getDialogTypeFaceStyle() {
        return dialogTypeFaceStyle;
    }

    /**
     * Publicly available functions from library
     */

    Typeface getDialogTypeFace() {
        return dialogTypeFace;
    }

    /**
     * To change font of ccp views
     *
     * @param typeFace
     */
    public void setDialogTypeFace(Typeface typeFace) {
        try {
            dialogTypeFace = typeFace;
            dialogTypeFaceStyle = DEFAULT_UNSET;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * this will load preferredCountries based on countryPreference
     */
    void refreshPreferredCountries() {
        if (countryPreference == null || countryPreference.length() == 0) {
            preferredCountries = null;
        } else {
            List<CCPCountry> localCCPCountryList = new ArrayList<>();
            for (String nameCode : countryPreference.split(",")) {
                CCPCountry ccpCountry = CCPCountry.getCountryForNameCodeFromCustomMasterList(getContext(), customMasterCountriesList, getLanguageToApply(), nameCode);
                if (ccpCountry != null) {
                    if (!isAlreadyInList(ccpCountry, localCCPCountryList)) { //to avoid duplicate entry of country
                        localCCPCountryList.add(ccpCountry);
                    }
                }
            }

            if (localCCPCountryList.size() == 0) {
                preferredCountries = null;
            } else {
                preferredCountries = localCCPCountryList;
            }
        }
        if (preferredCountries != null) {
            //            Log.d("preference list", preferredCountries.size() + " countries");
            for (CCPCountry CCPCountry : preferredCountries) {
                CCPCountry.log();
            }
        } else {
            //            Log.d("preference list", " has no country");
        }
    }

    /**
     * this will load preferredCountries based on countryPreference
     */
    void refreshCustomMasterList() {
        //if no custom list specified then check for exclude list
        if (customMasterCountriesParam == null || customMasterCountriesParam.length() == 0) {
            //if excluded param is also blank, then do nothing
            if (excludedCountriesParam != null && excludedCountriesParam.length() != 0) {
                excludedCountriesParam = excludedCountriesParam.toLowerCase();
                List<CCPCountry> libraryMasterList = CCPCountry.getLibraryMasterCountryList(context, getLanguageToApply());
                List<CCPCountry> localCCPCountryList = new ArrayList<>();
                for (CCPCountry ccpCountry : libraryMasterList) {
                    //if the country name code is in the excluded list, avoid it.
                    if (!excludedCountriesParam.contains(ccpCountry.getNameCode().toLowerCase())) {
                        localCCPCountryList.add(ccpCountry);
                    }
                }

                if (localCCPCountryList.size() > 0) {
                    customMasterCountriesList = localCCPCountryList;
                } else {
                    customMasterCountriesList = null;
                }

            } else {
                customMasterCountriesList = null;
            }
        } else {
            //else add custom list
            List<CCPCountry> localCCPCountryList = new ArrayList<>();
            for (String nameCode : customMasterCountriesParam.split(",")) {
                CCPCountry ccpCountry = CCPCountry.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), nameCode);
                if (ccpCountry != null) {
                    if (!isAlreadyInList(ccpCountry, localCCPCountryList)) { //to avoid duplicate entry of country
                        localCCPCountryList.add(ccpCountry);
                    }
                }
            }

            if (localCCPCountryList.size() == 0) {
                customMasterCountriesList = null;
            } else {
                customMasterCountriesList = localCCPCountryList;
            }
        }

        if (customMasterCountriesList != null) {
            //            Log.d("custom master list:", customMasterCountriesList.size() + " countries");
            for (CCPCountry ccpCountry : customMasterCountriesList) {
                ccpCountry.log();
            }
        } else {
            //            Log.d("custom master list", " has no country");
        }
    }

    List<CCPCountry> getCustomMasterCountriesList() {
        return customMasterCountriesList;
    }

    /**
     * @param customMasterCountriesList is list of countries that we need as custom master list
     */
    void setCustomMasterCountriesList(List<CCPCountry> customMasterCountriesList) {
        this.customMasterCountriesList = customMasterCountriesList;
    }

    /**
     * @return comma separated custom master countries' name code. i.e "gb,us,nz,in,pk"
     */
    String getCustomMasterCountriesParam() {
        return customMasterCountriesParam;
    }

    /**
     * To provide definite set of countries when selection dialog is opened.
     * Only custom master countries, if defined, will be there is selection dialog to select from.
     * To set any country in preference, it must be included in custom master countries, if defined
     * When not defined or null or blank is set, it will use library's default master list
     * Custom master list will only limit the visibility of irrelevant country from selection dialog. But all other functions like setCountryForCodeName() or setFullNumber() will consider all the countries.
     *
     * @param customMasterCountriesParam is country name codes separated by comma. e.g. "us,in,nz"
     *                                   if null or "" , will remove custom countries and library default will be used.
     */
    public void setCustomMasterCountries(String customMasterCountriesParam) {
        this.customMasterCountriesParam = customMasterCountriesParam;
    }

    /**
     * This can be used to remove certain countries from the list by keeping all the others.
     * This will be ignored if you have specified your own country master list.
     *
     * @param excludedCountries is country name codes separated by comma. e.g. "us,in,nz"
     *                          null or "" means no country is excluded.
     */
    public void setExcludedCountries(String excludedCountries) {
        this.excludedCountriesParam = excludedCountries;
        refreshCustomMasterList();
    }

    /**
     * @return true if ccp is enabled for click
     */
    boolean isCcpClickable() {
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

    /**
     * This will match name code of all countries of list against the country's name code.
     *
     * @param CCPCountry
     * @param CCPCountryList list of countries against which country will be checked.
     * @return if country name code is found in list, returns true else return false
     */
    private boolean isAlreadyInList(CCPCountry CCPCountry, List<CCPCountry> CCPCountryList) {
        if (CCPCountry != null && CCPCountryList != null) {
            for (CCPCountry iterationCCPCountry : CCPCountryList) {
                if (iterationCCPCountry.getNameCode().equalsIgnoreCase(CCPCountry.getNameCode())) {
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
     * @param CCPCountry selected country in CCP to detect country code part.
     */
    private String detectCarrierNumber(String fullNumber, CCPCountry CCPCountry) {
        String carrierNumber;
        if (CCPCountry == null || fullNumber == null || fullNumber.isEmpty()) {
            carrierNumber = fullNumber;
        } else {
            int indexOfCode = fullNumber.indexOf(CCPCountry.getPhoneCode());
            if (indexOfCode == -1) {
                carrierNumber = fullNumber;
            } else {
                carrierNumber = fullNumber.substring(indexOfCode + CCPCountry.getPhoneCode().length());
            }
        }
        return carrierNumber;
    }

    /**
     * Related to selected country
     */

    //add entry here
    private Language getLanguageEnum(int index) {
        if (index < Language.values().length) {
            return Language.values()[index];
        } else {
            return Language.ENGLISH;
        }
    }

    String getDialogTitle() {
        return CCPCountry.getDialogTitle(context, getLanguageToApply());
    }

    String getSearchHintText() {
        return CCPCountry.getSearchHintMessage(context, getLanguageToApply());
    }

    /**
     * @return translated text for "No Results Found" message.
     */
    String getNoResultFoundText() {
        return CCPCountry.getNoResultFoundAckMessage(context, getLanguageToApply());
    }

    /**
     * This method is not encouraged because this might set some other country which have same country code as of yours. e.g 1 is common for US and canada.
     * If you are trying to set US ( and countryPreference is not set) and you pass 1 as @param defaultCountryCode, it will set canada (prior in list due to alphabetical order)
     * Rather use @function setDefaultCountryUsingNameCode("us"); or setDefaultCountryUsingNameCode("US");
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
        CCPCountry defaultCCPCountry = CCPCountry.getCountryForCode(getContext(), getLanguageToApply(), preferredCountries, defaultCountryCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (defaultCCPCountry == null) { //if no correct country is found
            //            Log.d(TAG, "No country for code " + defaultCountryCode + " is found");
        } else { //if correct country is found, set the country
            this.defaultCountryCode = defaultCountryCode;
            setDefaultCountry(defaultCCPCountry);
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
        CCPCountry defaultCCPCountry = CCPCountry.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), defaultCountryNameCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (defaultCCPCountry == null) { //if no correct country is found
            //            Log.d(TAG, "No country for nameCode " + defaultCountryNameCode + " is found");
        } else { //if correct country is found, set the country
            this.defaultCountryNameCode = defaultCCPCountry.getNameCode();
            setDefaultCountry(defaultCCPCountry);
        }
    }

    /**
     * @return: Country Code of default country
     * i.e if default country is IN +91(India)  returns: "91"
     * if default country is JP +81(Japan) returns: "81"
     */
    public String getDefaultCountryCode() {
        return defaultCCPCountry.phoneCode;
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
     * reset the default country as selected country.
     */
    public void resetToDefaultCountry() {
        defaultCCPCountry = CCPCountry.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), getDefaultCountryNameCode());
        setSelectedCountry(defaultCCPCountry);
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
     * To get name of selected country in English.
     *
     * @return String value of country name in English language, selected in CCP
     * i.e if selected country is IN +91(India)  returns: "India" no matter what language is currently selected.
     * if selected country is JP +81(Japan) returns: "Japan"
     */
    public String getSelectedCountryEnglishName() {
        return getSelectedCountry().getEnglishName();
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
        CCPCountry ccpCountry = CCPCountry.getCountryForCode(getContext(), getLanguageToApply(), preferredCountries, countryCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (ccpCountry == null) {
            if (defaultCCPCountry == null) {
                defaultCCPCountry = ccpCountry.getCountryForCode(getContext(), getLanguageToApply(), preferredCountries, defaultCountryCode);
            }
            setSelectedCountry(defaultCCPCountry);
        } else {
            setSelectedCountry(ccpCountry);
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
        CCPCountry country = CCPCountry.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), countryNameCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (country == null) {
            if (defaultCCPCountry == null) {
                defaultCCPCountry = country.getCountryForCode(getContext(), getLanguageToApply(), preferredCountries, defaultCountryCode);
            }
            setSelectedCountry(defaultCCPCountry);
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
        String fullNumber = getSelectedCountryCode();
        if (editText_registeredCarrierNumber != null) {
            PhoneNumberUtil phoneNumberUtil = getPhoneUtil();
            try {
                String phoneCode = getSelectedCountryCode();
                String nameCode = getSelectedCountryNameCode();
                String nationalPhone = PhoneNumberUtil.normalizeDigitsOnly(editText_registeredCarrierNumber.getText().toString());
                Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(nationalPhone, nameCode);
                fullNumber = "" + phoneNumber.getCountryCode() + phoneNumber.getNationalNumber();
            } catch (NumberParseException e) {
                e.printStackTrace();
            }
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
        CCPCountry country = CCPCountry.getCountryForNumber(getContext(), getLanguageToApply(), preferredCountries, fullNumber);
        if (country == null)
            country = getDefaultCountry();
        setSelectedCountry(country);
        String carrierNumber = detectCarrierNumber(fullNumber, country);
        if (getEditText_registeredCarrierNumber() != null) {
            getEditText_registeredCarrierNumber().setText(carrierNumber);
            updateFormattingTextWatcher();
        } else {
            Log.w(TAG, "EditText for carrier number is not registered. Register it using registerCarrierNumberEditText() before getFullNumber() or setFullNumber().");
        }
    }

    /**
     * This function combines selected country code from CCP and carrier number from @param editTextCarrierNumber
     * This will return formatted number.
     *
     * @return Full number is countryCode + carrierNumber i.e countryCode= 91 and carrier number= 8866667722, this will return "918866667722"
     */
    public String getFormattedFullNumber() {
        String formattedFullNumber;
        Phonenumber.PhoneNumber phoneNumber;
        if (editText_registeredCarrierNumber != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                formattedFullNumber = PhoneNumberUtils.formatNumber(getFullNumberWithPlus(), getSelectedCountryCode());
            } else {
                formattedFullNumber = PhoneNumberUtils.formatNumber(getFullNumberWithPlus());
            }
        } else {
            formattedFullNumber = getSelectedCountry().getPhoneCode();
            Log.w(TAG, "EditText for carrier number is not registered. Register it using registerCarrierNumberEditText() before getFullNumber() or setFullNumber().");
        }
        return formattedFullNumber;
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
    public void setArrowSize(int arrowSize) {
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
     * @param showNameCode true will show country name code in ccp view, it will result " (IN) +91 "
     *                     false will remove country name code from ccp view, it will result  " +91 "
     */
    public void showNameCode(boolean showNameCode) {
        this.showNameCode = showNameCode;
        setSelectedCountry(selectedCCPCountry);
    }

    /**
     * This can change visility of arrow.
     *
     * @param showArrow true will show arrow and false will hide arrow from there.
     */
    public void showArrow(boolean showArrow) {
        this.showArrow = showArrow;
        refreshArrowViewVisibility();
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
     * If autoDetectCountry is true, ccp will try to detect language from locale.
     * Detected language is supported If no language is detected or detected language is not supported by ccp, it will set default language as set.
     *
     * @param language
     */
    public void changeDefaultLanguage(Language language) {
        setCustomDefaultLanguage(language);
    }

    /**
     * To change font of ccp views
     *
     * @param typeFace
     */
    public void setTypeFace(Typeface typeFace) {
        try {
            textView_selectedCountry.setTypeface(typeFace);
            setDialogTypeFace(typeFace);
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
    public void setDialogTypeFace(Typeface typeFace, int style) {
        try {
            dialogTypeFace = typeFace;
            if (dialogTypeFace == null) {
                style = DEFAULT_UNSET;
            }
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
            setDialogTypeFace(typeFace, style);
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
        setSelectedCountry(selectedCCPCountry);
    }

    /**
     * SelectionDialogSearch is the facility to search through the list of country while selecting.
     *
     * @return true if search is set allowed
     */
    public boolean isSearchAllowed() {
        return searchAllowed;
    }

    /**
     * SelectionDialogSearch is the facility to search through the list of country while selecting.
     *
     * @param searchAllowed true will allow search and false will hide search box
     */
    public void setSearchAllowed(boolean searchAllowed) {
        this.searchAllowed = searchAllowed;
    }

    /**
     * Sets validity change listener.
     * First call back will be sent right away.
     *
     * @param phoneNumberValidityChangeListener
     */
    public void setPhoneNumberValidityChangeListener(PhoneNumberValidityChangeListener phoneNumberValidityChangeListener) {
        this.phoneNumberValidityChangeListener = phoneNumberValidityChangeListener;
        if (editText_registeredCarrierNumber != null) {
            reportedValidity = isValidFullNumber();
            phoneNumberValidityChangeListener.onValidityChanged(reportedValidity);
        }
    }

    /**
     * Sets failure listener.
     *
     * @param failureListener
     */
    public void setPhoneNumberValidityChangeListener(FailureListener failureListener) {
        this.failureListener = failureListener;
    }

    /**
     * Opens country selection dialog.
     * By default this is called from ccp click.
     * Developer can use this to trigger manually.
     */
    public void launchCountrySelectionDialog() {
        CountryCodeDialog.openCountryCodeDialog(codePicker);
    }

    /**
     * This function will check the validity of entered number.
     * It will use PhoneNumberUtil to check validity
     *
     * @return true if entered carrier number is valid else false
     */
    public boolean isValidFullNumber() {
        try {
            if (getEditText_registeredCarrierNumber() != null && getEditText_registeredCarrierNumber().getText().length() != 0) {
                Phonenumber.PhoneNumber phoneNumber = getPhoneUtil().parse("+" + selectedCCPCountry.getPhoneCode() + getEditText_registeredCarrierNumber().getText().toString(), selectedCCPCountry.getNameCode());
                return getPhoneUtil().isValidNumber(phoneNumber);
            } else if (getEditText_registeredCarrierNumber() == null) {
                Toast.makeText(context, "No editText for Carrier number found.", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return false;
            }
        } catch (NumberParseException e) {
            //            when number could not be parsed, its not valid
            return false;
        }
    }

    private PhoneNumberUtil getPhoneUtil() {
        if (phoneUtil == null) {
            phoneUtil = PhoneNumberUtil.createInstance(context);
        }
        return phoneUtil;
    }

    /**
     * loads current country in ccp using locale and telephony manager
     * this will follow specified order in countryAutoDetectionPref
     *
     * @param loadDefaultWhenFails: if all of pref methods fail to detect country then should this
     *                              function load default country or not is decided with this flag
     */
    public void setAutoDetectedCountry(boolean loadDefaultWhenFails) {
        try {
            boolean successfullyDetected = false;
            for (int i = 0; i < selectedAutoDetectionPref.representation.length(); i++) {
                switch (selectedAutoDetectionPref.representation.charAt(i)) {
                    case '1':
                        Log.d(TAG, "setAutoDetectedCountry: Setting using SIM");
                        successfullyDetected = detectSIMCountry(false);
                        Log.d(TAG, "setAutoDetectedCountry: Result of sim country detection:" + successfullyDetected + " current country:" + getSelectedCountryNameCode());
                        break;
                    case '2':
                        Log.d(TAG, "setAutoDetectedCountry: Setting using NETWORK");
                        successfullyDetected = detectNetworkCountry(false);
                        Log.d(TAG, "setAutoDetectedCountry: Result of network country detection:" + successfullyDetected + " current country:" + getSelectedCountryNameCode());
                        break;
                    case '3':
                        Log.d(TAG, "setAutoDetectedCountry: Setting using LOCALE");
                        successfullyDetected = detectLocaleCountry(false);
                        Log.d(TAG, "setAutoDetectedCountry: Result of LOCALE country detection:" + successfullyDetected + " current country:" + getSelectedCountryNameCode());
                        break;
                }
                if (successfullyDetected) {
                    break;
                } else {
                    if (failureListener != null) {
                        failureListener.onCountryAutoDetectionFailed();
                    }
                }
            }

            if (!successfullyDetected && loadDefaultWhenFails) {
                resetToDefaultCountry();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "setAutoDetectCountry: Exception" + e.getMessage());
            if (loadDefaultWhenFails) {
                resetToDefaultCountry();
            }
        }
    }

    /**
     * This will detect country from SIM info and then load it into CCP.
     *
     * @param loadDefaultWhenFails true if want to reset to default country when sim country cannot be detected. if false, then it
     *                             will not change currently selected country
     * @return true if it successfully sets country, false otherwise
     */
    public boolean detectSIMCountry(boolean loadDefaultWhenFails) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String simCountryISO = telephonyManager.getSimCountryIso();
            if (simCountryISO == null || simCountryISO.isEmpty()) {
                if (loadDefaultWhenFails) {
                    resetToDefaultCountry();
                }
                return false;
            }
            setSelectedCountry(CCPCountry.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), simCountryISO));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (loadDefaultWhenFails) {
                resetToDefaultCountry();
            }
            return false;
        }
    }

    /**
     * This will detect country from NETWORK info and then load it into CCP.
     *
     * @param loadDefaultWhenFails true if want to reset to default country when network country cannot be detected. if false, then it
     *                             will not change currently selected country
     * @return true if it successfully sets country, false otherwise
     */
    public boolean detectNetworkCountry(boolean loadDefaultWhenFails) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String networkCountryISO = telephonyManager.getNetworkCountryIso();
            if (networkCountryISO == null || networkCountryISO.isEmpty()) {
                if (loadDefaultWhenFails) {
                    resetToDefaultCountry();
                }
                return false;
            }
            setSelectedCountry(CCPCountry.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), networkCountryISO));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (loadDefaultWhenFails) {
                resetToDefaultCountry();
            }
            return false;
        }
    }

    /**
     * This will detect country from LOCALE info and then load it into CCP.
     *
     * @param loadDefaultWhenFails true if want to reset to default country when locale country cannot be detected. if false, then it
     *                             will not change currently selected country
     * @return true if it successfully sets country, false otherwise
     */
    public boolean detectLocaleCountry(boolean loadDefaultWhenFails) {
        try {
            String localeCountryISO = context.getResources().getConfiguration().locale.getCountry();
            if (localeCountryISO == null || localeCountryISO.isEmpty()) {
                if (loadDefaultWhenFails) {
                    resetToDefaultCountry();
                }
                return false;
            }
            setSelectedCountry(CCPCountry.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), localeCountryISO));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (loadDefaultWhenFails) {
                resetToDefaultCountry();
            }
            return false;
        }
    }

    /**
     * This will update the pref for country auto detection.
     * Remeber, this will not call setAutoDetectedCountry() to update country. This must be called separately.
     *
     * @param selectedAutoDetectionPref new detection pref
     */
    public void setCountryAutoDetectionPref(AutoDetectionPref selectedAutoDetectionPref) {
        this.selectedAutoDetectionPref = selectedAutoDetectionPref;
    }

    protected void onUserTappedCountry(CCPCountry CCPCountry) {
        if (codePicker.rememberLastSelection) {
            codePicker.storeSelectedCountryNameCode(CCPCountry.getNameCode());
        }
        setSelectedCountry(CCPCountry);
    }

    public void setDetectCountryWithAreaCode(boolean detectCountryWithAreaCode) {
        this.detectCountryWithAreaCode = detectCountryWithAreaCode;
        updateFormattingTextWatcher();
    }

    public void setHintExampleNumberEnabled(boolean hintExampleNumberEnabled) {
        this.hintExampleNumberEnabled = hintExampleNumberEnabled;
        updateHint();
    }

    public void setHintExampleNumberType(PhoneNumberType hintExampleNumberType) {
        this.hintExampleNumberType = hintExampleNumberType;
        updateHint();
    }

    /**
     * Update every time new language is supported #languageSupport
     */
    //add an entry for your language in attrs.xml's <attr name="language" format="enum"> enum.
    //add here so that language can be set programmatically
    public enum Language {
        ARABIC("ar"),
        BENGALI("bn"),
        CHINESE_SIMPLIFIED("zh"),
        CHINESE_TRADITIONAL("zh"),
        DUTCH("nl"),
        ENGLISH("en"),
        FARSI("fa"),
        FRENCH("fr"),
        GERMAN("de"),
        GUJARATI("gu"),
        HEBREW("iw"),
        HINDI("hi"),
        INDONESIA("in"),
        ITALIAN("it"),
        JAPANESE("ja"),
        KOREAN("ko"),
        POLISH("pl"),
        PORTUGUESE("pt"),
        PUNJABI("pa"),
        RUSSIAN("ru"),
        SLOVAK("sk"),
        SPANISH("es"),
        SWEDISH("sv"),
        TURKISH("tr"),
        UKRAINIAN("uk");

        String code;

        Language(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public enum PhoneNumberType {
        MOBILE,
        FIXED_LINE,
        // In some regions (e.g. the USA), it is impossible to distinguish between fixed-line and
        // mobile numbers by looking at the phone number itself.
        FIXED_LINE_OR_MOBILE,
        // Freephone lines
        TOLL_FREE,
        PREMIUM_RATE,
        // The cost of this call is shared between the caller and the recipient, and is hence typically
        // less than PREMIUM_RATE calls. See // http://en.wikipedia.org/wiki/Shared_Cost_Service for
        // more information.
        SHARED_COST,
        // Voice over IP numbers. This includes TSoIP (Telephony Service over IP).
        VOIP,
        // A personal number is associated with a particular person, and may be routed to either a
        // MOBILE or FIXED_LINE number. Some more information can be found here:
        // http://en.wikipedia.org/wiki/Personal_Numbers
        PERSONAL_NUMBER,
        PAGER,
        // Used for "Universal Access Numbers" or "Company Numbers". They may be further routed to
        // specific offices, but allow one number to be used for a company.
        UAN,
        // Used for "Voice Mail Access Numbers".
        VOICEMAIL,
        // A phone number is of type UNKNOWN when it does not fit any of the known patterns for a
        // specific region.
        UNKNOWN
    }

    public enum AutoDetectionPref {
        SIM_ONLY("1"),
        NETWORK_ONLY("2"),
        LOCALE_ONLY("3"),
        SIM_NETWORK("12"),
        NETWORK_SIM("21"),
        SIM_LOCALE("13"),
        LOCALE_SIM("31"),
        NETWORK_LOCALE("23"),
        LOCALE_NETWORK("32"),
        SIM_NETWORK_LOCALE("123"),
        SIM_LOCALE_NETWORK("132"),
        NETWORK_SIM_LOCALE("213"),
        NETWORK_LOCALE_SIM("231"),
        LOCALE_SIM_NETWORK("312"),
        LOCALE_NETWORK_SIM("321");

        String representation;

        AutoDetectionPref(String representation) {
            this.representation = representation;
        }

        public static AutoDetectionPref getPrefForValue(String value) {
            for (AutoDetectionPref autoDetectionPref : AutoDetectionPref.values()) {
                if (autoDetectionPref.representation.equals(value)) {
                    return autoDetectionPref;
                }
            }
            return SIM_NETWORK_LOCALE;
        }
    }

    /**
     * When width is "match_parent", this gravity will decide the placement of text.
     */
    public enum TextGravity {
        LEFT(-1), CENTER(0), RIGHT(1);

        int enumIndex;

        TextGravity(int i) {
            enumIndex = i;
        }
    }

    /**
     * interface to set change listener
     */
    public interface OnCountryChangeListener {
        void onCountrySelected();
    }

    /**
     * interface to listen to failure events
     */
    public interface FailureListener {
        //when country auto detection failed.
        void onCountryAutoDetectionFailed();
    }

    /**
     * Interface to check phone number validity change listener
     */
    public interface PhoneNumberValidityChangeListener {
        void onValidityChanged(boolean isValidNumber);
    }

    public interface DialogEventsListener {
        void onCcpDialogOpen(Dialog dialog);

        void onCcpDialogDismiss(DialogInterface dialogInterface);

        void onCcpDialogCancel(DialogInterface dialogInterface);
    }


}
