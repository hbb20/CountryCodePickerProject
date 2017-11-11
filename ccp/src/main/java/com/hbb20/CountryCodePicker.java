package com.hbb20;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    TextGravity currentTextGravity;
    boolean showNameCode = false;
    boolean showPhoneCode = true;
    boolean ccpDialogShowPhoneCode = true;
    boolean showFlag = true;
    boolean showFullName = false;
    boolean showFastScroller;
    boolean searchAllowed = true;
    boolean showArrow = true;
    boolean showCloseIcon = false;
    int contentColor;
    int borderFlagColor;
    Typeface dialogTypeFace;
    int dialogTypeFaceStyle;
    List<Country> preferredCountries;
    int ccpTextgGravity = TEXT_GRAVITY_CENTER;
    //this will be "AU,IN,US"
    String countryPreference;
    int fastScrollerBubbleColor = 0;
    List<Country> customMasterCountriesList;
    //this will be "AU,IN,US"
    String customMasterCountries;
    Language customDefaultLanguage = Language.ENGLISH;
    Language languageToApply = Language.ENGLISH;

    boolean dialogKeyboardAutoPopup = true;
    boolean ccpClickable = true;
    boolean autoDetectLanguageEnabled, autoDetectCountryEnabled, numberAutoFormattingEnabled;
    String xmlWidth = "notSet";
    TextWatcher validityTextWatcher;
    PhoneNumberFormattingTextWatcher textWatcher;
    boolean reportedValidity;
    private OnCountryChangeListener onCountryChangeListener;
    private PhoneNumberValidityChangeListener phoneNumberValidityChangeListener;
    private DialogEventsListener dialogEventsListener;
    private int fastScrollerHandleColor;
    private int dialogBackgroundColor, dialogTextColor, dialogSearchEditTextTintColor;
    private int fastScrollerBubbleTextAppearance;
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

            //show phone code.
            showPhoneCode = a.getBoolean(R.styleable.CountryCodePicker_ccp_showPhoneCode, true);

            //show phone code on dialog
            ccpDialogShowPhoneCode = a.getBoolean(R.styleable.CountryCodePicker_ccpDialog_showPhoneCode, showPhoneCode);

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

            //auto detect county
            autoDetectCountryEnabled = a.getBoolean(R.styleable.CountryCodePicker_ccp_autoDetectCountry, true);

            //show arrow
            showArrow = a.getBoolean(R.styleable.CountryCodePicker_ccp_showArrow, true);
            refreshArrowViewVisibility();

            //show close icon
            showCloseIcon = a.getBoolean(R.styleable.CountryCodePicker_ccpDialog_showCloseIcon, false);

            //show flag
            showFlag(a.getBoolean(R.styleable.CountryCodePicker_ccp_showFlag, true));

            //number auto formatting
            numberAutoFormattingEnabled = a.getBoolean(R.styleable.CountryCodePicker_ccp_autoFormatNumber, true);

            //autopop keyboard
            setDialogKeyboardAutoPopup(a.getBoolean(R.styleable.CountryCodePicker_ccpDialog_keyboardAutoPopup, true));

            //if custom default language is specified, then set it as custom else sets english as custom
            int attrLanguage;
            attrLanguage = a.getInt(R.styleable.CountryCodePicker_ccp_defaultLanguage, Language.ENGLISH.ordinal());
            customDefaultLanguage = getLanguageEnum(attrLanguage);
            updateLanguageToApply();

            //custom master list
            customMasterCountries = a.getString(R.styleable.CountryCodePicker_ccp_customMasterCountries);
            refreshCustomMasterList();

            //preference
            countryPreference = a.getString(R.styleable.CountryCodePicker_ccp_countryPreference);
            refreshPreferredCountries();

            //text gravity
            if (a.hasValue(R.styleable.CountryCodePicker_ccp_textGravity)) {
                ccpTextgGravity = a.getInt(R.styleable.CountryCodePicker_ccp_textGravity, TEXT_GRAVITY_RIGHT);
            }
            applyTextGravity(ccpTextgGravity);

            //default country
            defaultCountryNameCode = a.getString(R.styleable.CountryCodePicker_ccp_defaultNameCode);
            boolean setUsingNameCode = false;
            if (defaultCountryNameCode != null && defaultCountryNameCode.length() != 0) {
                if (Country.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), defaultCountryNameCode) != null) {
                    setUsingNameCode = true;
                    setDefaultCountry(Country.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), defaultCountryNameCode));
                    setSelectedCountry(defaultCountry);
                }
            }


            //if default country is not set using name code.
            if (!setUsingNameCode) {
                int defaultCountryCode = a.getInteger(R.styleable.CountryCodePicker_ccp_defaultPhoneCode, -1);

                //if invalid country is set using xml, it will be replaced with LIB_DEFAULT_COUNTRY_CODE
                if (Country.getCountryForCode(getContext(), getLanguageToApply(), preferredCountries, defaultCountryCode) == null) {
                    defaultCountryCode = LIB_DEFAULT_COUNTRY_CODE;
                }
                setDefaultCountryUsingPhoneCode(defaultCountryCode);
                setSelectedCountry(defaultCountry);
            }

            //set auto detected country
            if (isAutoDetectCountryEnabled() && !isInEditMode()) {
                setAutoDetectedCountry();
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
            textView_selectedCountry.setText(e.getMessage());
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
        setSelectedCountry(selectedCountry);
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
        if (selectedCountry == null) {
            setSelectedCountry(getDefaultCountry());
        }
        return selectedCountry;
    }

    void setSelectedCountry(Country selectedCountry) {

        //as soon as country is selected, textView should be updated
        if (selectedCountry == null) {
            selectedCountry = Country.getCountryForCode(getContext(), getLanguageToApply(), preferredCountries, defaultCountryCode);
        }

        this.selectedCountry = selectedCountry;

        String displayText = "";

        // add full name to if required
        if (showFullName) {
            displayText = displayText + selectedCountry.getName();
        }

        // adds name code if required
        if (showNameCode) {
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

        updateFormattingTextWatcher();

        //notify to registered validity listener
        if (editText_registeredCarrierNumber != null && phoneNumberValidityChangeListener != null) {
            reportedValidity = isValidFullNumber();
            phoneNumberValidityChangeListener.onValidityChanged(reportedValidity);
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
        if (getEditText_registeredCarrierNumber() != null && selectedCountry != null) {

            String enteredValue = getEditText_registeredCarrierNumber().getText().toString();
            String digitsValue = PhoneNumberUtil.normalizeDigitsOnly(enteredValue);

            editText_registeredCarrierNumber.removeTextChangedListener(textWatcher);
            if (numberAutoFormattingEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    textWatcher = new PhoneNumberFormattingTextWatcher(selectedCountry.getNameCode());
                } else {
                    textWatcher = new PhoneNumberFormattingTextWatcher();
                }
                editText_registeredCarrierNumber.addTextChangedListener(textWatcher);
            }

            //text watcher stops working when it finds non digit character in previous phone code. This will reset its function
            editText_registeredCarrierNumber.setText("");
            editText_registeredCarrierNumber.setText(digitsValue);
            editText_registeredCarrierNumber.setSelection(editText_registeredCarrierNumber.getText().length());
        }
    }

    Language getCustomDefaultLanguage() {
        return customDefaultLanguage;
    }

    private void setCustomDefaultLanguage(Language customDefaultLanguage) {
        this.customDefaultLanguage = customDefaultLanguage;
        updateLanguageToApply();
        setSelectedCountry(Country.getCountryForNameCodeFromLibraryMasterList(context, getLanguageToApply(), selectedCountry.getNameCode()));
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
        return editText_registeredCarrierNumber;
    }

    /**
     * this will register editText and will apply required text watchers
     *
     * @param editText_registeredCarrierNumber
     */
    void setEditText_registeredCarrierNumber(EditText editText_registeredCarrierNumber) {
        this.editText_registeredCarrierNumber = editText_registeredCarrierNumber;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        updateFormattingTextWatcher();
        updateValidityTextWatcher();
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

    int getDialogTypeFaceStyle() {
        return dialogTypeFaceStyle;
    }

    Typeface getDialogTypeFace() {
        return dialogTypeFace;
    }

    /**
     * Publicly available functions from library
     */

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

    /**
     * this will load preferredCountries based on countryPreference
     */
    void refreshPreferredCountries() {
        if (countryPreference == null || countryPreference.length() == 0) {
            preferredCountries = null;
        } else {
            List<Country> localCountryList = new ArrayList<>();
            for (String nameCode : countryPreference.split(",")) {
                Country country = Country.getCountryForNameCodeFromCustomMasterList(getContext(), customMasterCountriesList, getLanguageToApply(), nameCode);
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
                Country country = Country.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), nameCode);
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

    /**
     * @param customMasterCountriesList is list of countries that we need as custom master list
     */
    void setCustomMasterCountriesList(List<Country> customMasterCountriesList) {
        this.customMasterCountriesList = customMasterCountriesList;
    }

    /**
     * @return comma separated custom master countries' name code. i.e "gb,us,nz,in,pk"
     */
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
        return Country.getDialogTitle(context, getLanguageToApply());
    }

    String getSearchHintText() {
        return Country.getSearchHintMessage(context, getLanguageToApply());
    }

    /**
     * @return translated text for "No Results Found" message.
     */
    String getNoResultFoundText() {
        return Country.getNoResultFoundAckMessage(context, getLanguageToApply());
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
        Country defaultCountry = Country.getCountryForCode(getContext(), getLanguageToApply(), preferredCountries, defaultCountryCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
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
        Country defaultCountry = Country.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), defaultCountryNameCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
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
        Country country = Country.getCountryForCode(getContext(), getLanguageToApply(), preferredCountries, countryCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (country == null) {
            if (defaultCountry == null) {
                defaultCountry = Country.getCountryForCode(getContext(), getLanguageToApply(), preferredCountries, defaultCountryCode);
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
        Country country = Country.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), countryNameCode); //xml stores data in string format, but want to allow only numeric value to country code to user.
        if (country == null) {
            if (defaultCountry == null) {
                defaultCountry = Country.getCountryForCode(getContext(), getLanguageToApply(), preferredCountries, defaultCountryCode);
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
            fullNumber = PhoneNumberUtil.normalizeDigitsOnly(fullNumber);
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
        Country country = Country.getCountryForNumber(getContext(), getLanguageToApply(), preferredCountries, fullNumber);
        setSelectedCountry(country);
        String carrierNumber = detectCarrierNumber(fullNumber, country);
        if (getEditText_registeredCarrierNumber() != null) {
            getEditText_registeredCarrierNumber().setText(carrierNumber);
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
        setSelectedCountry(selectedCountry);
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
        setSelectedCountry(selectedCountry);
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
                Phonenumber.PhoneNumber phoneNumber = PhoneNumberUtil.getInstance().parse("+" + selectedCountry.getPhoneCode() + getEditText_registeredCarrierNumber().getText().toString(), selectedCountry.getNameCode());
                return PhoneNumberUtil.getInstance().isValidNumber(phoneNumber);
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

    /**
     * loads current country in ccp using locale and telephony manager
     * first it will look for locale. Mostly it will get the country.
     * but in case it fails it will look for registered network country.
     * When user's device is not registered to any network then it will try to get country from SIM card details.
     * if it fails to detect country, it will set default country in CCP view
     */
    public void setAutoDetectedCountry() {
        try {

            String currentCountryIso = context.getResources().getConfiguration().locale.getCountry();
            Log.d(TAG, "setAutoDetectedCountry: localeCountry is" + currentCountryIso);
            //if it fails, try networkCountryIso
            if (TextUtils.isEmpty(currentCountryIso)) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                //try to get country using Network ISO
                currentCountryIso = telephonyManager.getNetworkCountryIso();
                Log.d(TAG, "setAutoDetectedCountry: networkCountry:" + currentCountryIso);
            }

            //Network ISO can be null if network is not available and phone is not registered to any network. SIM country can be used as other option.
            if (TextUtils.isEmpty(currentCountryIso)) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                currentCountryIso = telephonyManager.getSimCountryIso();
                Log.d(TAG, "setAutoDetectedCountry: simCountry:" + currentCountryIso);
            }

            //if any of above method found country, then load it.
            if (!TextUtils.isEmpty(currentCountryIso)) {
                Log.d(TAG, "setAutoDetectedCountry: Finally detected country" + currentCountryIso);
                setSelectedCountry(Country.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), currentCountryIso));
            } else {
                Log.d(TAG, "setAutoDetectedCountry:  Could not find the country");
                setSelectedCountry(Country.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), getDefaultCountryNameCode()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "setAutoDetectCountry: Exception" + e.getMessage());
            setSelectedCountry(Country.getCountryForNameCodeFromLibraryMasterList(getContext(), getLanguageToApply(), getDefaultCountryNameCode()));
        }
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
        FRENCH("fr"),
        GERMAN("de"),
        GUJARATI("gu"),
        HEBREW("iw"),
        HINDI("hi"),
        INDONESIA("in"),
        ITALIAN("it"),
        JAPANESE("ja"),
        KOREAN("ko"),
        PORTUGUESE("pt"),
        RUSSIAN("ru"),
        SPANISH("es"),
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
