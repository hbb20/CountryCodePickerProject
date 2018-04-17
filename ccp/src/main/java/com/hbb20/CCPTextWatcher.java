package com.hbb20;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;

import io.michaelrocks.libphonenumber.android.AsYouTypeFormatter;
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;


//Majorly adopted from https://stackoverflow.com/questions/32661363/using-phonenumberformattingtextwatcher-without-typing-country-calling-code to solve formatting issue
public class CCPTextWatcher implements TextWatcher {

    /**
     * Indicates the change was caused by ourselves.
     */
    private boolean mSelfChange = false;

    /**
     * Indicates the formatting has been stopped.
     */
    private boolean mStopFormatting;

    private AsYouTypeFormatter mFormatter;
    PhoneNumberUtil phoneNumberUtil;
    CountryCodePicker attachedCcp;
    private String countryCode;

    /**
     * The formatting is based on the given <code>countryCode</code>.
     *
     * @param countryCodePicker to detect the ISO 3166-1 two-letter country code that indicates the country/region
     *                          where the phone number is being entered.
     * @hide
     */
    public CCPTextWatcher(Context context, CountryCodePicker countryCodePicker) {
        if (countryCodePicker == null || countryCodePicker.getSelectedCountryNameCode() == null || countryCodePicker.getSelectedCountryNameCode().length() == 0)
            throw new IllegalArgumentException();
        phoneNumberUtil = PhoneNumberUtil.createInstance(context);
        this.countryCode = countryCodePicker.getSelectedCountryNameCode();
        mFormatter = phoneNumberUtil.getAsYouTypeFormatter(countryCode);
        attachedCcp = countryCodePicker;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        if (mSelfChange || mStopFormatting) {
            return;
        }
        // If the user manually deleted any non-dialable characters, stop formatting
        if (count > 0 && hasSeparator(s, start, count)) {
            stopFormatting();
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mSelfChange || mStopFormatting) {
            return;
        }
        // If the user inserted any non-dialable characters, stop formatting
        if (count > 0 && hasSeparator(s, start, count)) {
            stopFormatting();
        }
    }

    @Override
    public synchronized void afterTextChanged(Editable s) {
        if (mStopFormatting) {
            // Restart the formatting when all texts were clear.
            mStopFormatting = !(s.length() == 0);
            return;
        }
        if (mSelfChange) {
            // Ignore the change caused by s.replace().
            return;
        }

        //this comes from additional answer on question (link at top of the class)
        InputFormatted inputFormatted = reformat(s, Selection.getSelectionEnd(s));
        String formatted = inputFormatted.getFormatted();
        if (formatted != null) {
            int rememberedPos = formatted.length();
            mSelfChange = true;
            s.replace(0, s.length(), formatted, 0, formatted.length());


            // The text could be changed by other TextWatcher after we changed it. If we found the
            // text is not the one we were expecting, just give up calling setSelection().
            if (formatted.equals(s.toString())) {
                Selection.setSelection(s, rememberedPos);
            }
            mSelfChange = false;
        }

        if (inputFormatted.getPosition() < s.length()) {
            Selection.setSelection(s, inputFormatted.getPosition());
        }

    }

    /**
     * Generate the formatted number by ignoring all non-dialable chars and stick the cursor to the
     * nearest dialable char to the left. For instance, if the number is  (650) 123-45678 and '4' is
     * removed then the cursor should be behind '3' instead of '-'.
     */
    private InputFormatted reformat(CharSequence s, int cursor) {
        // The index of char to the leftward of the cursor.
        int curIndex = cursor - 1;
        String internationalFormatted = "";
        mFormatter.clear();
        char lastNonSeparator = 0;
        boolean hasCursor = false;

        String countryCallingCode = attachedCcp.getSelectedCountryCodeWithPlus();
        s = countryCallingCode + s;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            if (PhoneNumberUtils.isNonSeparator(c)) {
                if (lastNonSeparator != 0) {
                    internationalFormatted = getFormattedNumber(lastNonSeparator, hasCursor);
                    hasCursor = false;
                }
                lastNonSeparator = c;
            }
            if (i == curIndex) {
                hasCursor = true;
            }
        }
        if (lastNonSeparator != 0) {
            internationalFormatted = getFormattedNumber(lastNonSeparator, hasCursor);
        }

        internationalFormatted = internationalFormatted.trim();
        if (internationalFormatted.length() > countryCallingCode.length()) {
            if (internationalFormatted.charAt(countryCallingCode.length()) == ' ')
                internationalFormatted = internationalFormatted.substring(countryCallingCode.length() + 1);
            else
                internationalFormatted = internationalFormatted.substring(countryCallingCode.length());
        } else {
            internationalFormatted = "";
        }

        InputFormatted internationalInputFormatted = new InputFormatted(TextUtils.isEmpty(internationalFormatted) ? "" : internationalFormatted,
                mFormatter.getRememberedPosition());

        return internationalInputFormatted;
    }

    private String getFormattedNumber(char lastNonSeparator, boolean hasCursor) {
        return hasCursor ? mFormatter.inputDigitAndRememberPosition(lastNonSeparator)
                : mFormatter.inputDigit(lastNonSeparator);
    }

    private void stopFormatting() {
        mStopFormatting = true;
        mFormatter.clear();
    }

    private boolean hasSeparator(final CharSequence s, final int start, final int count) {
        for (int i = start; i < start + count; i++) {
            char c = s.charAt(i);
            if (!PhoneNumberUtils.isNonSeparator(c)) {
                return true;
            }
        }
        return false;
    }

    class InputFormatted {
        String formatted;
        int position;

        public InputFormatted(String formatted, int position) {
            this.formatted = formatted;
            this.position = position;
        }

        public String getFormatted() {
            return formatted;
        }

        public void setFormatted(String formatted) {
            this.formatted = formatted;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }
    }
}
