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
public class InternationalPhoneTextWatcher implements TextWatcher {

    PhoneNumberUtil phoneNumberUtil;
    CountryCodePicker attachedCcp;
    /**
     * Indicates the change was caused by ourselves.
     */
    private boolean mSelfChange = false;
    /**
     * Indicates the formatting has been stopped.
     */
    private boolean mStopFormatting;
    private AsYouTypeFormatter mFormatter;
    private String countryCode;

    /**
     * The formatting is based on the given <code>countryCode</code>.
     *
     * @param countryCodePicker to detect the ISO 3166-1 two-letter country code that indicates the country/region
     *                          where the phone number is being entered.
     * @hide
     */
    public InternationalPhoneTextWatcher(Context context, CountryCodePicker countryCodePicker) {
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
        int selectionEnd = Selection.getSelectionEnd(s);
        boolean isCursorAtEnd = (selectionEnd == s.length());
        int digitsBeforeCursor = 0;

        for (int i = 0; i < s.length(); i++) {

            if (i >= selectionEnd) {
                break;
            }
            if (PhoneNumberUtils.isNonSeparator(s.charAt(i))) {
                digitsBeforeCursor++;
            }
        }

        String formatted = reformat(s, Selection.getSelectionEnd(s));

        int finalCursorPosition = 0;
        if (formatted.equals(s.toString())) {
            //means there is no change while formatting don't move cursor
            finalCursorPosition = selectionEnd;
        } else if (isCursorAtEnd) {
            finalCursorPosition = formatted.length();
        } else {
            for (int i = 0, digitPassed = 0; i < s.length(); i++) {

                if (digitPassed == digitsBeforeCursor) {
                    finalCursorPosition = i;
                    break;
                }

                if (PhoneNumberUtils.isNonSeparator(formatted.charAt(i))) {
                    digitPassed++;
                }
            }

        }

        if (formatted != null) {
            mSelfChange = true;
            s.replace(0, s.length(), formatted, 0, formatted.length());
            mSelfChange = false;
            Selection.setSelection(s, finalCursorPosition);
        }
    }

    /**
     * Generate the formatted number by ignoring all non-dialable chars and stick the cursor to the
     * nearest dialable char to the left. For instance, if the number is  (650) 123-45678 and '4' is
     * removed then the cursor should be behind '3' instead of '-'.
     */
    private String reformat(CharSequence s, int cursor) {
        // The index of char to the leftward of the cursor.
        int curIndex = cursor - 1;
        String internationalFormatted = "";
        mFormatter.clear();

        if (s.length() > curIndex && curIndex != -1 && !PhoneNumberUtils.isNonSeparator(s.charAt(curIndex))) {
            curIndex--;
            if (curIndex != 0) {
                curIndex--;
            }
        }

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
        return TextUtils.isEmpty(internationalFormatted) ? "" : internationalFormatted;
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
}
