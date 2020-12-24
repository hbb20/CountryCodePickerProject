package com.hbb20;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * Created by hbb20 on 11/1/16.
 */
public class CCPCountry implements Comparable<CCPCountry> {
    static int DEFAULT_FLAG_RES = -99;
    static String TAG = "Class Country";
    static CountryCodePicker.Language loadedLibraryMasterListLanguage;
    static String dialogTitle, searchHintMessage, noResultFoundAckMessage;
    static List<CCPCountry> loadedLibraryMaterList;
    //countries with +1
    private static String ANTIGUA_AND_BARBUDA_AREA_CODES = "268";
    private static String ANGUILLA_AREA_CODES = "264";
    private static String BARBADOS_AREA_CODES = "246";
    private static String BERMUDA_AREA_CODES = "441";
    private static String BAHAMAS_AREA_CODES = "242";
    private static String CANADA_AREA_CODES = "204/226/236/249/250/289/306/343/365/403/416/418/431/437/438/450/506/514/519/579/581/587/600/604/613/639/647/705/709/769/778/780/782/807/819/825/867/873/902/905/";
    private static String DOMINICA_AREA_CODES = "767";
    private static String DOMINICAN_REPUBLIC_AREA_CODES = "809/829/849";
    private static String GRENADA_AREA_CODES = "473";
    private static String JAMAICA_AREA_CODES = "876";
    private static String SAINT_KITTS_AND_NEVIS_AREA_CODES = "869";
    private static String CAYMAN_ISLANDS_AREA_CODES = "345";
    private static String SAINT_LUCIA_AREA_CODES = "758";
    private static String MONTSERRAT_AREA_CODES = "664";
    private static String PUERTO_RICO_AREA_CODES = "787";
    private static String SINT_MAARTEN_AREA_CODES = "721";
    private static String TURKS_AND_CAICOS_ISLANDS_AREA_CODES = "649";
    private static String TRINIDAD_AND_TOBAGO_AREA_CODES = "868";
    private static String SAINT_VINCENT_AND_THE_GRENADINES_AREA_CODES = "784";
    private static String BRITISH_VIRGIN_ISLANDS_AREA_CODES = "284";
    private static String US_VIRGIN_ISLANDS_AREA_CODES = "340";

    //countries with +44
    private static String ISLE_OF_MAN = "1624";
    String nameCode;
    String phoneCode;
    String name, englishName;
    int flagResID = DEFAULT_FLAG_RES;

    public CCPCountry() {

    }

    public CCPCountry(String nameCode, String phoneCode, String name, int flagResID) {
        this.nameCode = nameCode.toUpperCase(Locale.ROOT);
        this.phoneCode = phoneCode;
        this.name = name;
        this.flagResID = flagResID;
    }

    static CountryCodePicker.Language getLoadedLibraryMasterListLanguage() {
        return loadedLibraryMasterListLanguage;
    }

    static void setLoadedLibraryMasterListLanguage(CountryCodePicker.Language loadedLibraryMasterListLanguage) {
        CCPCountry.loadedLibraryMasterListLanguage = loadedLibraryMasterListLanguage;
    }

    public static List<CCPCountry> getLoadedLibraryMaterList() {
        return loadedLibraryMaterList;
    }

    static void setLoadedLibraryMaterList(List<CCPCountry> loadedLibraryMaterList) {
        CCPCountry.loadedLibraryMaterList = loadedLibraryMaterList;
    }

    /**
     * This function parses the raw/countries.xml file, and get list of all the countries.
     *
     * @param context: required to access application resources (where country.xml is).
     * @return List of all the countries available in xml file.
     */
    static void loadDataFromXML(Context context, CountryCodePicker.Language language) {
        List<CCPCountry> countries = new ArrayList<CCPCountry>();
        String tempDialogTitle = "", tempSearchHint = "", tempNoResultAck = "";
        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlFactoryObject.newPullParser();
            InputStream ins = context.getResources().openRawResource(context.getResources()
                    .getIdentifier("ccp_" + language.toString().toLowerCase(Locale.ROOT),
                            "raw", context.getPackageName()));
            xmlPullParser.setInput(ins, "UTF-8");
            int event = xmlPullParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = xmlPullParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("country")) {
                            CCPCountry ccpCountry = new CCPCountry();
                            ccpCountry.setNameCode(xmlPullParser.getAttributeValue(null, "name_code").toUpperCase());
                            ccpCountry.setPhoneCode(xmlPullParser.getAttributeValue(null, "phone_code"));
                            ccpCountry.setEnglishName(xmlPullParser.getAttributeValue(null, "english_name"));
                            ccpCountry.setName(xmlPullParser.getAttributeValue(null, "name"));
                            countries.add(ccpCountry);
                        } else if (name.equals("ccp_dialog_title")) {
                            tempDialogTitle = xmlPullParser.getAttributeValue(null, "translation");
                        } else if (name.equals("ccp_dialog_search_hint_message")) {
                            tempSearchHint = xmlPullParser.getAttributeValue(null, "translation");
                        } else if (name.equals("ccp_dialog_no_result_ack_message")) {
                            tempNoResultAck = xmlPullParser.getAttributeValue(null, "translation");
                        }
                        break;
                }
                event = xmlPullParser.next();
            }
            loadedLibraryMasterListLanguage = language;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

        //if anything went wrong, countries will be loaded for english language
        if (countries.size() == 0) {
            loadedLibraryMasterListLanguage = CountryCodePicker.Language.ENGLISH;
            countries = getLibraryMasterCountriesEnglish();
        }

        dialogTitle = tempDialogTitle.length() > 0 ? tempDialogTitle : "Select a country";
        searchHintMessage = tempSearchHint.length() > 0 ? tempSearchHint : "Search...";
        noResultFoundAckMessage = tempNoResultAck.length() > 0 ? tempNoResultAck : "Results not found";
        loadedLibraryMaterList = countries;

        // sort list
        Collections.sort(loadedLibraryMaterList);
    }

    public static String getDialogTitle(Context context, CountryCodePicker.Language language) {
        if (loadedLibraryMasterListLanguage == null || loadedLibraryMasterListLanguage != language || dialogTitle == null || dialogTitle.length() == 0) {
            loadDataFromXML(context, language);
        }
        return dialogTitle;
    }

    public static String getSearchHintMessage(Context context, CountryCodePicker.Language language) {
        if (loadedLibraryMasterListLanguage == null || loadedLibraryMasterListLanguage != language || searchHintMessage == null || searchHintMessage.length() == 0) {
            loadDataFromXML(context, language);
        }
        return searchHintMessage;
    }

    public static String getNoResultFoundAckMessage(Context context, CountryCodePicker.Language language) {
        if (loadedLibraryMasterListLanguage == null || loadedLibraryMasterListLanguage != language || noResultFoundAckMessage == null || noResultFoundAckMessage.length() == 0) {
            loadDataFromXML(context, language);
        }
        return noResultFoundAckMessage;
    }

    public static void setDialogTitle(String dialogTitle) {
        CCPCountry.dialogTitle = dialogTitle;
    }

    public static void setSearchHintMessage(String searchHintMessage) {
        CCPCountry.searchHintMessage = searchHintMessage;
    }

    public static void setNoResultFoundAckMessage(String noResultFoundAckMessage) {
        CCPCountry.noResultFoundAckMessage = noResultFoundAckMessage;
    }

    /**
     * Search a country which matches @param code.
     *
     * @param context
     * @param preferredCountries is list of preference countries.
     * @param code               phone code. i.e "91" or "1"
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     * if same code (e.g. +1) available for more than one country ( US, canada) , this function will return preferred country.
     */
    public static CCPCountry getCountryForCode(Context context, CountryCodePicker.Language language, List<CCPCountry> preferredCountries, String code) {

        /**
         * check in preferred countries
         */
        if (preferredCountries != null && !preferredCountries.isEmpty()) {
            for (CCPCountry CCPCountry : preferredCountries) {
                if (CCPCountry.getPhoneCode().equals(code)) {
                    return CCPCountry;
                }
            }
        }

        for (CCPCountry CCPCountry : getLibraryMasterCountryList(context, language)) {
            if (CCPCountry.getPhoneCode().equals(code)) {
                return CCPCountry;
            }
        }
        return null;
    }

    /**
     * @param code phone code. i.e "91" or "1"
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     * if same code (e.g. +1) available for more than one country ( US, canada) , this function will return preferred country.
     * @avoid Search a country which matches @param code. This method is just to support correct preview
     */
    static CCPCountry getCountryForCodeFromEnglishList(String code) {

        List<CCPCountry> countries;
        countries = getLibraryMasterCountriesEnglish();

        for (CCPCountry ccpCountry : countries) {
            if (ccpCountry.getPhoneCode().equals(code)) {
                return ccpCountry;
            }
        }
        return null;
    }

    static List<CCPCountry> getCustomMasterCountryList(Context context, CountryCodePicker codePicker) {
        codePicker.refreshCustomMasterList();
        if (codePicker.customMasterCountriesList != null && codePicker.customMasterCountriesList.size() > 0) {
            return codePicker.getCustomMasterCountriesList();
        } else {
            return getLibraryMasterCountryList(context, codePicker.getLanguageToApply());
        }
    }

    /**
     * Search a country which matches @param nameCode.
     *
     * @param context
     * @param customMasterCountriesList
     * @param nameCode                  country name code. i.e US or us or Au. See countries.xml for all code names.
     * @return Country that has country name code as @param code.
     */
    static CCPCountry getCountryForNameCodeFromCustomMasterList(Context context, List<CCPCountry> customMasterCountriesList, CountryCodePicker.Language language, String nameCode) {
        if (customMasterCountriesList == null || customMasterCountriesList.size() == 0) {
            return getCountryForNameCodeFromLibraryMasterList(context, language, nameCode);
        } else {
            for (CCPCountry ccpCountry : customMasterCountriesList) {
                if (ccpCountry.getNameCode().equalsIgnoreCase(nameCode)) {
                    return ccpCountry;
                }
            }
        }
        return null;
    }

    /**
     * Search a country which matches @param nameCode.
     *
     * @param context
     * @param nameCode country name code. i.e US or us or Au. See countries.xml for all code names.
     * @return Country that has country name code as @param code.
     * or returns null if no country matches given code.
     */
    public static CCPCountry getCountryForNameCodeFromLibraryMasterList(Context context, CountryCodePicker.Language language, String nameCode) {
        List<CCPCountry> countries;
        countries = CCPCountry.getLibraryMasterCountryList(context, language);
        for (CCPCountry ccpCountry : countries) {
            if (ccpCountry.getNameCode().equalsIgnoreCase(nameCode)) {
                return ccpCountry;
            }
        }
        return null;
    }

    /**
     * Search a country which matches @param nameCode.
     * This searches through local english name list. This should be used only for the preview purpose.
     *
     * @param nameCode country name code. i.e US or us or Au. See countries.xml for all code names.
     * @return Country that has country name code as @param code.
     * or returns null if no country matches given code.
     */
    static CCPCountry getCountryForNameCodeFromEnglishList(String nameCode) {
        List<CCPCountry> countries;
        countries = getLibraryMasterCountriesEnglish();
        for (CCPCountry CCPCountry : countries) {
            if (CCPCountry.getNameCode().equalsIgnoreCase(nameCode)) {
                return CCPCountry;
            }
        }
        return null;
    }

    /**
     * Search a country which matches @param code.
     *
     * @param context
     * @param preferredCountries list of country with priority,
     * @param code               phone code. i.e 91 or 1
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     */
    static CCPCountry getCountryForCode(Context context, CountryCodePicker.Language language, List<CCPCountry> preferredCountries, int code) {
        return getCountryForCode(context, language, preferredCountries, code + "");
    }

    /**
     * Finds country code by matching substring from left to right from full number.
     * For example. if full number is +819017901357
     * function will ignore "+" and try to find match for first character "8"
     * if any country found for code "8", will return that country. If not, then it will
     * try to find country for "81". and so on till first 3 characters ( maximum number of characters in country code is 3).
     *
     * @param context
     * @param preferredCountries countries of preference
     * @param fullNumber         full number ( "+" (optional)+ country code + carrier number) i.e. +819017901357 / 819017901357 / 918866667722
     * @return Country JP +81(Japan) for +819017901357 or 819017901357
     * Country IN +91(India) for  918866667722
     * null for 2956635321 ( as neither of "2", "29" and "295" matches any country code)
     */
    static CCPCountry getCountryForNumber(Context context, CountryCodePicker.Language language, List<CCPCountry> preferredCountries, String fullNumber) {
        int firstDigit;
        if (fullNumber == null) {
            return null;
        } else {
            fullNumber = fullNumber.trim();
        }

        if (fullNumber.length() != 0) {
            if (fullNumber.charAt(0) == '+') {
                firstDigit = 1;
            } else {
                firstDigit = 0;
            }
            CCPCountry ccpCountry = null;
            for (int i = firstDigit; i <= fullNumber.length(); i++) {
                String code = fullNumber.substring(firstDigit, i);
                CCPCountryGroup countryGroup = null;
                try {
                    countryGroup = CCPCountryGroup.getCountryGroupForPhoneCode(Integer.parseInt(code));
                } catch (Exception ignored) {
                }
                if (countryGroup != null) {
                    int areaCodeStartsAt = firstDigit + code.length();
                    //when phone number covers area code too.
                    if (fullNumber.length() >= areaCodeStartsAt + countryGroup.areaCodeLength) {
                        String areaCode = fullNumber.substring(areaCodeStartsAt, areaCodeStartsAt + countryGroup.areaCodeLength);
                        return countryGroup.getCountryForAreaCode(context, language, areaCode);
                    } else {
                        return getCountryForNameCodeFromLibraryMasterList(context, language, countryGroup.defaultNameCode);
                    }
                } else {
                    ccpCountry = CCPCountry.getCountryForCode(context, language, preferredCountries, code);
                    if (ccpCountry != null) {
                        return ccpCountry;
                    }
                }
            }
        }
        //it reaches here means, phone number has some problem.
        return null;
    }

    /**
     * Finds country code by matching substring from left to right from full number.
     * For example. if full number is +819017901357
     * function will ignore "+" and try to find match for first character "8"
     * if any country found for code "8", will return that country. If not, then it will
     * try to find country for "81". and so on till first 3 characters ( maximum number of characters in country code is 3).
     *
     * @param context
     * @param fullNumber full number ( "+" (optional)+ country code + carrier number) i.e. +819017901357 / 819017901357 / 918866667722
     * @return Country JP +81(Japan) for +819017901357 or 819017901357
     * Country IN +91(India) for  918866667722
     * null for 2956635321 ( as neither of "2", "29" and "295" matches any country code)
     */
    public static CCPCountry getCountryForNumber(Context context, CountryCodePicker.Language language, String fullNumber) {
        return getCountryForNumber(context, language, null, fullNumber);
    }

    /**
     * Returns image res based on country name code
     *
     * @param CCPCountry
     * @return
     */
    static int getFlagMasterResID(CCPCountry CCPCountry) {
        switch (CCPCountry.getNameCode().toLowerCase()) {
            //this should be sorted based on country name code.
            case "ad": //andorra
                return R.drawable.flag_andorra;
            case "ae": //united arab emirates
                return R.drawable.flag_uae;
            case "af": //afghanistan
                return R.drawable.flag_afghanistan;
            case "ag": //antigua & barbuda
                return R.drawable.flag_antigua_and_barbuda;
            case "ai": //anguilla // Caribbean Islands
                return R.drawable.flag_anguilla;
            case "al": //albania
                return R.drawable.flag_albania;
            case "am": //armenia
                return R.drawable.flag_armenia;
            case "ao": //angola
                return R.drawable.flag_angola;
            case "aq": //antarctica // custom
                return R.drawable.flag_antarctica;
            case "ar": //argentina
                return R.drawable.flag_argentina;
            case "as": //American Samoa
                return R.drawable.flag_american_samoa;
            case "at": //austria
                return R.drawable.flag_austria;
            case "au": //australia
                return R.drawable.flag_australia;
            case "aw": //aruba
                return R.drawable.flag_aruba;
            case "ax": //alan islands
                return R.drawable.flag_aland;
            case "az": //azerbaijan
                return R.drawable.flag_azerbaijan;
            case "ba": //bosnia and herzegovina
                return R.drawable.flag_bosnia;
            case "bb": //barbados
                return R.drawable.flag_barbados;
            case "bd": //bangladesh
                return R.drawable.flag_bangladesh;
            case "be": //belgium
                return R.drawable.flag_belgium;
            case "bf": //burkina faso
                return R.drawable.flag_burkina_faso;
            case "bg": //bulgaria
                return R.drawable.flag_bulgaria;
            case "bh": //bahrain
                return R.drawable.flag_bahrain;
            case "bi": //burundi
                return R.drawable.flag_burundi;
            case "bj": //benin
                return R.drawable.flag_benin;
            case "bl": //saint barthélemy
                return R.drawable.flag_saint_barthelemy;// custom
            case "bm": //bermuda
                return R.drawable.flag_bermuda;
            case "bn": //brunei darussalam // custom
                return R.drawable.flag_brunei;
            case "bo": //bolivia, plurinational state of
                return R.drawable.flag_bolivia;
            case "br": //brazil
                return R.drawable.flag_brazil;
            case "bs": //bahamas
                return R.drawable.flag_bahamas;
            case "bt": //bhutan
                return R.drawable.flag_bhutan;
            case "bw": //botswana
                return R.drawable.flag_botswana;
            case "by": //belarus
                return R.drawable.flag_belarus;
            case "bz": //belize
                return R.drawable.flag_belize;
            case "ca": //canada
                return R.drawable.flag_canada;
            case "cc": //cocos (keeling) islands
                return R.drawable.flag_cocos;// custom
            case "cd": //congo, the democratic republic of the
                return R.drawable.flag_democratic_republic_of_the_congo;
            case "cf": //central african republic
                return R.drawable.flag_central_african_republic;
            case "cg": //congo
                return R.drawable.flag_republic_of_the_congo;
            case "ch": //switzerland
                return R.drawable.flag_switzerland;
            case "ci": //côte d\'ivoire
                return R.drawable.flag_cote_divoire;
            case "ck": //cook islands
                return R.drawable.flag_cook_islands;
            case "cl": //chile
                return R.drawable.flag_chile;
            case "cm": //cameroon
                return R.drawable.flag_cameroon;
            case "cn": //china
                return R.drawable.flag_china;
            case "co": //colombia
                return R.drawable.flag_colombia;
            case "cr": //costa rica
                return R.drawable.flag_costa_rica;
            case "cu": //cuba
                return R.drawable.flag_cuba;
            case "cv": //cape verde
                return R.drawable.flag_cape_verde;
            case "cw": //curaçao
                return R.drawable.flag_curacao;
            case "cx": //christmas island
                return R.drawable.flag_christmas_island;
            case "cy": //cyprus
                return R.drawable.flag_cyprus;
            case "cz": //czech republic
                return R.drawable.flag_czech_republic;
            case "de": //germany
                return R.drawable.flag_germany;
            case "dj": //djibouti
                return R.drawable.flag_djibouti;
            case "dk": //denmark
                return R.drawable.flag_denmark;
            case "dm": //dominica
                return R.drawable.flag_dominica;
            case "do": //dominican republic
                return R.drawable.flag_dominican_republic;
            case "dz": //algeria
                return R.drawable.flag_algeria;
            case "ec": //ecuador
                return R.drawable.flag_ecuador;
            case "ee": //estonia
                return R.drawable.flag_estonia;
            case "eg": //egypt
                return R.drawable.flag_egypt;
            case "er": //eritrea
                return R.drawable.flag_eritrea;
            case "es": //spain
                return R.drawable.flag_spain;
            case "et": //ethiopia
                return R.drawable.flag_ethiopia;
            case "fi": //finland
                return R.drawable.flag_finland;
            case "fj": //fiji
                return R.drawable.flag_fiji;
            case "fk": //falkland islands (malvinas)
                return R.drawable.flag_falkland_islands;
            case "fm": //micronesia, federated states of
                return R.drawable.flag_micronesia;
            case "fo": //faroe islands
                return R.drawable.flag_faroe_islands;
            case "fr": //france
                return R.drawable.flag_france;
            case "ga": //gabon
                return R.drawable.flag_gabon;
            case "gb": //united kingdom
                return R.drawable.flag_united_kingdom;
            case "gd": //grenada
                return R.drawable.flag_grenada;
            case "ge": //georgia
                return R.drawable.flag_georgia;
            case "gf": //guyane
                return R.drawable.flag_guyane;
            case "gg": //Guernsey
                return R.drawable.flag_guernsey;
            case "gh": //ghana
                return R.drawable.flag_ghana;
            case "gi": //gibraltar
                return R.drawable.flag_gibraltar;
            case "gl": //greenland
                return R.drawable.flag_greenland;
            case "gm": //gambia
                return R.drawable.flag_gambia;
            case "gn": //guinea
                return R.drawable.flag_guinea;
            case "gp": //guadeloupe
                return R.drawable.flag_guadeloupe;
            case "gq": //equatorial guinea
                return R.drawable.flag_equatorial_guinea;
            case "gr": //greece
                return R.drawable.flag_greece;
            case "gt": //guatemala
                return R.drawable.flag_guatemala;
            case "gu": //Guam
                return R.drawable.flag_guam;
            case "gw": //guinea-bissau
                return R.drawable.flag_guinea_bissau;
            case "gy": //guyana
                return R.drawable.flag_guyana;
            case "hk": //hong kong
                return R.drawable.flag_hong_kong;
            case "hn": //honduras
                return R.drawable.flag_honduras;
            case "hr": //croatia
                return R.drawable.flag_croatia;
            case "ht": //haiti
                return R.drawable.flag_haiti;
            case "hu": //hungary
                return R.drawable.flag_hungary;
            case "id": //indonesia
                return R.drawable.flag_indonesia;
            case "ie": //ireland
                return R.drawable.flag_ireland;
            case "il": //israel
                return R.drawable.flag_israel;
            case "im": //isle of man
                return R.drawable.flag_isleof_man; // custom
            case "is": //Iceland
                return R.drawable.flag_iceland;
            case "in": //india
                return R.drawable.flag_india;
            case "io": //British indian ocean territory
                return R.drawable.flag_british_indian_ocean_territory;
            case "iq": //iraq
                return R.drawable.flag_iraq_new;
            case "ir": //iran, islamic republic of
                return R.drawable.flag_iran;
            case "it": //italy
                return R.drawable.flag_italy;
            case "je": //Jersey
                return R.drawable.flag_jersey;
            case "jm": //jamaica
                return R.drawable.flag_jamaica;
            case "jo": //jordan
                return R.drawable.flag_jordan;
            case "jp": //japan
                return R.drawable.flag_japan;
            case "ke": //kenya
                return R.drawable.flag_kenya;
            case "kg": //kyrgyzstan
                return R.drawable.flag_kyrgyzstan;
            case "kh": //cambodia
                return R.drawable.flag_cambodia;
            case "ki": //kiribati
                return R.drawable.flag_kiribati;
            case "km": //comoros
                return R.drawable.flag_comoros;
            case "kn": //st kitts & nevis
                return R.drawable.flag_saint_kitts_and_nevis;
            case "kp": //north korea
                return R.drawable.flag_north_korea;
            case "kr": //south korea
                return R.drawable.flag_south_korea;
            case "kw": //kuwait
                return R.drawable.flag_kuwait;
            case "ky": //Cayman_Islands
                return R.drawable.flag_cayman_islands;
            case "kz": //kazakhstan
                return R.drawable.flag_kazakhstan;
            case "la": //lao people\'s democratic republic
                return R.drawable.flag_laos;
            case "lb": //lebanon
                return R.drawable.flag_lebanon;
            case "lc": //st lucia
                return R.drawable.flag_saint_lucia;
            case "li": //liechtenstein
                return R.drawable.flag_liechtenstein;
            case "lk": //sri lanka
                return R.drawable.flag_sri_lanka;
            case "lr": //liberia
                return R.drawable.flag_liberia;
            case "ls": //lesotho
                return R.drawable.flag_lesotho;
            case "lt": //lithuania
                return R.drawable.flag_lithuania;
            case "lu": //luxembourg
                return R.drawable.flag_luxembourg;
            case "lv": //latvia
                return R.drawable.flag_latvia;
            case "ly": //libya
                return R.drawable.flag_libya;
            case "ma": //morocco
                return R.drawable.flag_morocco;
            case "mc": //monaco
                return R.drawable.flag_monaco;
            case "md": //moldova, republic of
                return R.drawable.flag_moldova;
            case "me": //montenegro
                return R.drawable.flag_of_montenegro;// custom
            case "mf":
                return R.drawable.flag_saint_martin;
            case "mg": //madagascar
                return R.drawable.flag_madagascar;
            case "mh": //marshall islands
                return R.drawable.flag_marshall_islands;
            case "mk": //macedonia, the former yugoslav republic of
                return R.drawable.flag_macedonia;
            case "ml": //mali
                return R.drawable.flag_mali;
            case "mm": //myanmar
                return R.drawable.flag_myanmar;
            case "mn": //mongolia
                return R.drawable.flag_mongolia;
            case "mo": //macao
                return R.drawable.flag_macao;
            case "mp": // Northern mariana islands
                return R.drawable.flag_northern_mariana_islands;
            case "mq": //martinique
                return R.drawable.flag_martinique;
            case "mr": //mauritania
                return R.drawable.flag_mauritania;
            case "ms": //montserrat
                return R.drawable.flag_montserrat;
            case "mt": //malta
                return R.drawable.flag_malta;
            case "mu": //mauritius
                return R.drawable.flag_mauritius;
            case "mv": //maldives
                return R.drawable.flag_maldives;
            case "mw": //malawi
                return R.drawable.flag_malawi;
            case "mx": //mexico
                return R.drawable.flag_mexico;
            case "my": //malaysia
                return R.drawable.flag_malaysia;
            case "mz": //mozambique
                return R.drawable.flag_mozambique;
            case "na": //namibia
                return R.drawable.flag_namibia;
            case "nc": //new caledonia
                return R.drawable.flag_new_caledonia;// custom
            case "ne": //niger
                return R.drawable.flag_niger;
            case "nf": //Norfolk
                return R.drawable.flag_norfolk_island;
            case "ng": //nigeria
                return R.drawable.flag_nigeria;
            case "ni": //nicaragua
                return R.drawable.flag_nicaragua;
            case "nl": //netherlands
                return R.drawable.flag_netherlands;
            case "no": //norway
                return R.drawable.flag_norway;
            case "np": //nepal
                return R.drawable.flag_nepal;
            case "nr": //nauru
                return R.drawable.flag_nauru;
            case "nu": //niue
                return R.drawable.flag_niue;
            case "nz": //new zealand
                return R.drawable.flag_new_zealand;
            case "om": //oman
                return R.drawable.flag_oman;
            case "pa": //panama
                return R.drawable.flag_panama;
            case "pe": //peru
                return R.drawable.flag_peru;
            case "pf": //french polynesia
                return R.drawable.flag_french_polynesia;
            case "pg": //papua new guinea
                return R.drawable.flag_papua_new_guinea;
            case "ph": //philippines
                return R.drawable.flag_philippines;
            case "pk": //pakistan
                return R.drawable.flag_pakistan;
            case "pl": //poland
                return R.drawable.flag_poland;
            case "pm": //saint pierre and miquelon
                return R.drawable.flag_saint_pierre;
            case "pn": //pitcairn
                return R.drawable.flag_pitcairn_islands;
            case "pr": //puerto rico
                return R.drawable.flag_puerto_rico;
            case "ps": //palestine
                return R.drawable.flag_palestine;
            case "pt": //portugal
                return R.drawable.flag_portugal;
            case "pw": //palau
                return R.drawable.flag_palau;
            case "py": //paraguay
                return R.drawable.flag_paraguay;
            case "qa": //qatar
                return R.drawable.flag_qatar;
            case "re": //la reunion
                return R.drawable.flag_martinique; // no exact flag found
            case "ro": //romania
                return R.drawable.flag_romania;
            case "rs": //serbia
                return R.drawable.flag_serbia; // custom
            case "ru": //russian federation
                return R.drawable.flag_russian_federation;
            case "rw": //rwanda
                return R.drawable.flag_rwanda;
            case "sa": //saudi arabia
                return R.drawable.flag_saudi_arabia;
            case "sb": //solomon islands
                return R.drawable.flag_soloman_islands;
            case "sc": //seychelles
                return R.drawable.flag_seychelles;
            case "sd": //sudan
                return R.drawable.flag_sudan;
            case "se": //sweden
                return R.drawable.flag_sweden;
            case "sg": //singapore
                return R.drawable.flag_singapore;
            case "sh": //saint helena, ascension and tristan da cunha
                return R.drawable.flag_saint_helena; // custom
            case "si": //slovenia
                return R.drawable.flag_slovenia;
            case "sk": //slovakia
                return R.drawable.flag_slovakia;
            case "sl": //sierra leone
                return R.drawable.flag_sierra_leone;
            case "sm": //san marino
                return R.drawable.flag_san_marino;
            case "sn": //senegal
                return R.drawable.flag_senegal;
            case "so": //somalia
                return R.drawable.flag_somalia;
            case "sr": //suriname
                return R.drawable.flag_suriname;
            case "ss": //south sudan
                return R.drawable.flag_south_sudan;
            case "st": //sao tome and principe
                return R.drawable.flag_sao_tome_and_principe;
            case "sv": //el salvador
                return R.drawable.flag_el_salvador;
            case "sx": //sint maarten
                return R.drawable.flag_sint_maarten;
            case "sy": //syrian arab republic
                return R.drawable.flag_syria;
            case "sz": //swaziland
                return R.drawable.flag_swaziland;
            case "tc": //turks & caicos islands
                return R.drawable.flag_turks_and_caicos_islands;
            case "td": //chad
                return R.drawable.flag_chad;
            case "tg": //togo
                return R.drawable.flag_togo;
            case "th": //thailand
                return R.drawable.flag_thailand;
            case "tj": //tajikistan
                return R.drawable.flag_tajikistan;
            case "tk": //tokelau
                return R.drawable.flag_tokelau; // custom
            case "tl": //timor-leste
                return R.drawable.flag_timor_leste;
            case "tm": //turkmenistan
                return R.drawable.flag_turkmenistan;
            case "tn": //tunisia
                return R.drawable.flag_tunisia;
            case "to": //tonga
                return R.drawable.flag_tonga;
            case "tr": //turkey
                return R.drawable.flag_turkey;
            case "tt": //trinidad & tobago
                return R.drawable.flag_trinidad_and_tobago;
            case "tv": //tuvalu
                return R.drawable.flag_tuvalu;
            case "tw": //taiwan, province of china
                return R.drawable.flag_taiwan;
            case "tz": //tanzania, united republic of
                return R.drawable.flag_tanzania;
            case "ua": //ukraine
                return R.drawable.flag_ukraine;
            case "ug": //uganda
                return R.drawable.flag_uganda;
            case "us": //united states
                return R.drawable.flag_united_states_of_america;
            case "uy": //uruguay
                return R.drawable.flag_uruguay;
            case "uz": //uzbekistan
                return R.drawable.flag_uzbekistan;
            case "va": //holy see (vatican city state)
                return R.drawable.flag_vatican_city;
            case "vc": //st vincent & the grenadines
                return R.drawable.flag_saint_vicent_and_the_grenadines;
            case "ve": //venezuela, bolivarian republic of
                return R.drawable.flag_venezuela;
            case "vg": //british virgin islands
                return R.drawable.flag_british_virgin_islands;
            case "vi": //us virgin islands
                return R.drawable.flag_us_virgin_islands;
            case "vn": //vietnam
                return R.drawable.flag_vietnam;
            case "vu": //vanuatu
                return R.drawable.flag_vanuatu;
            case "wf": //wallis and futuna
                return R.drawable.flag_wallis_and_futuna;
            case "ws": //samoa
                return R.drawable.flag_samoa;
            case "xk": //kosovo
                return R.drawable.flag_kosovo;
            case "ye": //yemen
                return R.drawable.flag_yemen;
            case "yt": //mayotte
                return R.drawable.flag_martinique; // no exact flag found
            case "za": //south africa
                return R.drawable.flag_south_africa;
            case "zm": //zambia
                return R.drawable.flag_zambia;
            case "zw": //zimbabwe
                return R.drawable.flag_zimbabwe;
            default:
                return R.drawable.flag_transparent;
        }
    }


    /**
     * Returns image res based on country name code
     *
     * @param CCPCountry
     * @return
     */
    static String getFlagEmoji(CCPCountry CCPCountry) {
        switch (CCPCountry.getNameCode().toLowerCase()) {
            //this should be sorted based on country name code.
            case "ad":
                return "🇦🇩";
            case "ae":
                return "🇦🇪";
            case "af":
                return "🇦🇫";
            case "ag":
                return "🇦🇬";
            case "ai":
                return "🇦🇮";
            case "al":
                return "🇦🇱";
            case "am":
                return "🇦🇲";
            case "ao":
                return "🇦🇴";
            case "aq":
                return "🇦🇶";
            case "ar":
                return "🇦🇷";
            case "as":
                return "🇦🇸";
            case "at":
                return "🇦🇹";
            case "au":
                return "🇦🇺";
            case "aw":
                return "🇦🇼";
            case "ax":
                return "🇦🇽";
            case "az":
                return "🇦🇿";
            case "ba":
                return "🇧🇦";
            case "bb":
                return "🇧🇧";
            case "bd":
                return "🇧🇩";
            case "be":
                return "🇧🇪";
            case "bf":
                return "🇧🇫";
            case "bg":
                return "🇧🇬";
            case "bh":
                return "🇧🇭";
            case "bi":
                return "🇧🇮";
            case "bj":
                return "🇧🇯";
            case "bl":
                return "🇧🇱";
            case "bm":
                return "🇧🇲";
            case "bn":
                return "🇧🇳";
            case "bo":
                return "🇧🇴";
            case "bq":
                return "🇧🇶";
            case "br":
                return "🇧🇷";
            case "bs":
                return "🇧🇸";
            case "bt":
                return "🇧🇹";
            case "bv":
                return "🇧🇻";
            case "bw":
                return "🇧🇼";
            case "by":
                return "🇧🇾";
            case "bz":
                return "🇧🇿";
            case "ca":
                return "🇨🇦";
            case "cc":
                return "🇨🇨";
            case "cd":
                return "🇨🇩";
            case "cf":
                return "🇨🇫";
            case "cg":
                return "🇨🇬";
            case "ch":
                return "🇨🇭";
            case "ci":
                return "🇨🇮";
            case "ck":
                return "🇨🇰";
            case "cl":
                return "🇨🇱";
            case "cm":
                return "🇨🇲";
            case "cn":
                return "🇨🇳";
            case "co":
                return "🇨🇴";
            case "cr":
                return "🇨🇷";
            case "cu":
                return "🇨🇺";
            case "cv":
                return "🇨🇻";
            case "cw":
                return "🇨🇼";
            case "cx":
                return "🇨🇽";
            case "cy":
                return "🇨🇾";
            case "cz":
                return "🇨🇿";
            case "de":
                return "🇩🇪";
            case "dj":
                return "🇩🇯";
            case "dk":
                return "🇩🇰";
            case "dm":
                return "🇩🇲";
            case "do":
                return "🇩🇴";
            case "dz":
                return "🇩🇿";
            case "ec":
                return "🇪🇨";
            case "ee":
                return "🇪🇪";
            case "eg":
                return "🇪🇬";
            case "eh":
                return "🇪🇭";
            case "er":
                return "🇪🇷";
            case "es":
                return "🇪🇸";
            case "et":
                return "🇪🇹";
            case "fi":
                return "🇫🇮";
            case "fj":
                return "🇫🇯";
            case "fk":
                return "🇫🇰";
            case "fm":
                return "🇫🇲";
            case "fo":
                return "🇫🇴";
            case "fr":
                return "🇫🇷";
            case "ga":
                return "🇬🇦";
            case "gb":
                return "🇬🇧";
            case "gd":
                return "🇬🇩";
            case "ge":
                return "🇬🇪";
            case "gf":
                return "🇬🇫";
            case "gg":
                return "🇬🇬";
            case "gh":
                return "🇬🇭";
            case "gi":
                return "🇬🇮";
            case "gl":
                return "🇬🇱";
            case "gm":
                return "🇬🇲";
            case "gn":
                return "🇬🇳";
            case "gp":
                return "🇬🇵";
            case "gq":
                return "🇬🇶";
            case "gr":
                return "🇬🇷";
            case "gs":
                return "🇬🇸";
            case "gt":
                return "🇬🇹";
            case "gu":
                return "🇬🇺";
            case "gw":
                return "🇬🇼";
            case "gy":
                return "🇬🇾";
            case "hk":
                return "🇭🇰";
            case "hm":
                return "🇭🇲";
            case "hn":
                return "🇭🇳";
            case "hr":
                return "🇭🇷";
            case "ht":
                return "🇭🇹";
            case "hu":
                return "🇭🇺";
            case "id":
                return "🇮🇩";
            case "ie":
                return "🇮🇪";
            case "il":
                return "🇮🇱";
            case "im":
                return "🇮🇲";
            case "in":
                return "🇮🇳";
            case "io":
                return "🇮🇴";
            case "iq":
                return "🇮🇶";
            case "ir":
                return "🇮🇷";
            case "is":
                return "🇮🇸";
            case "it":
                return "🇮🇹";
            case "je":
                return "🇯🇪";
            case "jm":
                return "🇯🇲";
            case "jo":
                return "🇯🇴";
            case "jp":
                return "🇯🇵";
            case "ke":
                return "🇰🇪";
            case "kg":
                return "🇰🇬";
            case "kh":
                return "🇰🇭";
            case "ki":
                return "🇰🇮";
            case "km":
                return "🇰🇲";
            case "kn":
                return "🇰🇳";
            case "kp":
                return "🇰🇵";
            case "kr":
                return "🇰🇷";
            case "kw":
                return "🇰🇼";
            case "ky":
                return "🇰🇾";
            case "kz":
                return "🇰🇿";
            case "la":
                return "🇱🇦";
            case "lb":
                return "🇱🇧";
            case "lc":
                return "🇱🇨";
            case "li":
                return "🇱🇮";
            case "lk":
                return "🇱🇰";
            case "lr":
                return "🇱🇷";
            case "ls":
                return "🇱🇸";
            case "lt":
                return "🇱🇹";
            case "lu":
                return "🇱🇺";
            case "lv":
                return "🇱🇻";
            case "ly":
                return "🇱🇾";
            case "ma":
                return "🇲🇦";
            case "mc":
                return "🇲🇨";
            case "md":
                return "🇲🇩";
            case "me":
                return "🇲🇪";
            case "mf":
                return "🇲🇫";
            case "mg":
                return "🇲🇬";
            case "mh":
                return "🇲🇭";
            case "mk":
                return "🇲🇰";
            case "ml":
                return "🇲🇱";
            case "mm":
                return "🇲🇲";
            case "mn":
                return "🇲🇳";
            case "mo":
                return "🇲🇴";
            case "mp":
                return "🇲🇵";
            case "mq":
                return "🇲🇶";
            case "mr":
                return "🇲🇷";
            case "ms":
                return "🇲🇸";
            case "mt":
                return "🇲🇹";
            case "mu":
                return "🇲🇺";
            case "mv":
                return "🇲🇻";
            case "mw":
                return "🇲🇼";
            case "mx":
                return "🇲🇽";
            case "my":
                return "🇲🇾";
            case "mz":
                return "🇲🇿";
            case "na":
                return "🇳🇦";
            case "nc":
                return "🇳🇨";
            case "ne":
                return "🇳🇪";
            case "nf":
                return "🇳🇫";
            case "ng":
                return "🇳🇬";
            case "ni":
                return "🇳🇮";
            case "nl":
                return "🇳🇱";
            case "no":
                return "🇳🇴";
            case "np":
                return "🇳🇵";
            case "nr":
                return "🇳🇷";
            case "nu":
                return "🇳🇺";
            case "nz":
                return "🇳🇿";
            case "om":
                return "🇴🇲";
            case "pa":
                return "🇵🇦";
            case "pe":
                return "🇵🇪";
            case "pf":
                return "🇵🇫";
            case "pg":
                return "🇵🇬";
            case "ph":
                return "🇵🇭";
            case "pk":
                return "🇵🇰";
            case "pl":
                return "🇵🇱";
            case "pm":
                return "🇵🇲";
            case "pn":
                return "🇵🇳";
            case "pr":
                return "🇵🇷";
            case "ps":
                return "🇵🇸";
            case "pt":
                return "🇵🇹";
            case "pw":
                return "🇵🇼";
            case "py":
                return "🇵🇾";
            case "qa":
                return "🇶🇦";
            case "re":
                return "🇷🇪";
            case "ro":
                return "🇷🇴";
            case "rs":
                return "🇷🇸";
            case "ru":
                return "🇷🇺";
            case "rw":
                return "🇷🇼";
            case "sa":
                return "🇸🇦";
            case "sb":
                return "🇸🇧";
            case "sc":
                return "🇸🇨";
            case "sd":
                return "🇸🇩";
            case "se":
                return "🇸🇪";
            case "sg":
                return "🇸🇬";
            case "sh":
                return "🇸🇭";
            case "si":
                return "🇸🇮";
            case "sj":
                return "🇸🇯";
            case "sk":
                return "🇸🇰";
            case "sl":
                return "🇸🇱";
            case "sm":
                return "🇸🇲";
            case "sn":
                return "🇸🇳";
            case "so":
                return "🇸🇴";
            case "sr":
                return "🇸🇷";
            case "ss":
                return "🇸🇸";
            case "st":
                return "🇸🇹";
            case "sv":
                return "🇸🇻";
            case "sx":
                return "🇸🇽";
            case "sy":
                return "🇸🇾";
            case "sz":
                return "🇸🇿";
            case "tc":
                return "🇹🇨";
            case "td":
                return "🇹🇩";
            case "tf":
                return "🇹🇫";
            case "tg":
                return "🇹🇬";
            case "th":
                return "🇹🇭";
            case "tj":
                return "🇹🇯";
            case "tk":
                return "🇹🇰";
            case "tl":
                return "🇹🇱";
            case "tm":
                return "🇹🇲";
            case "tn":
                return "🇹🇳";
            case "to":
                return "🇹🇴";
            case "tr":
                return "🇹🇷";
            case "tt":
                return "🇹🇹";
            case "tv":
                return "🇹🇻";
            case "tw":
                return "🇹🇼";
            case "tz":
                return "🇹🇿";
            case "ua":
                return "🇺🇦";
            case "ug":
                return "🇺🇬";
            case "um":
                return "🇺🇲";
            case "us":
                return "🇺🇸";
            case "uy":
                return "🇺🇾";
            case "uz":
                return "🇺🇿";
            case "va":
                return "🇻🇦";
            case "vc":
                return "🇻🇨";
            case "ve":
                return "🇻🇪";
            case "vg":
                return "🇻🇬";
            case "vi":
                return "🇻🇮";
            case "vn":
                return "🇻🇳";
            case "vu":
                return "🇻🇺";
            case "wf":
                return "🇼🇫";
            case "ws":
                return "🇼🇸";
            case "xk":
                return "🇽🇰";
            case "ye":
                return "🇾🇪";
            case "yt":
                return "🇾🇹";
            case "za":
                return "🇿🇦";
            case "zm":
                return "🇿🇲";
            case "zw":
                return "🇿🇼";
            default:
                return " ";
        }
    }

    /**
     * This will return all the countries. No preference is manages.
     * Anytime new country need to be added, add it
     *
     * @return
     */
    public static List<CCPCountry> getLibraryMasterCountryList(Context context, CountryCodePicker.Language language) {
        if (loadedLibraryMasterListLanguage == null || language != loadedLibraryMasterListLanguage || loadedLibraryMaterList == null || loadedLibraryMaterList.size() == 0) { //when it is required to load country in country list
            loadDataFromXML(context, language);
        }
        return loadedLibraryMaterList;
    }

    public static List<CCPCountry> getLibraryMasterCountriesEnglish() {
        List<CCPCountry> countries = new ArrayList<>();
        countries.add(new CCPCountry("ad", "376", "Andorra", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ae", "971", "United Arab Emirates (UAE)", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("af", "93", "Afghanistan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ag", "1", "Antigua and Barbuda", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ai", "1", "Anguilla", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("al", "355", "Albania", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("am", "374", "Armenia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ao", "244", "Angola", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("aq", "672", "Antarctica", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ar", "54", "Argentina", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("as", "1", "American Samoa", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("at", "43", "Austria", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("au", "61", "Australia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("aw", "297", "Aruba", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ax", "358", "Åland Islands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("az", "994", "Azerbaijan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ba", "387", "Bosnia And Herzegovina", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bb", "1", "Barbados", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bd", "880", "Bangladesh", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("be", "32", "Belgium", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bf", "226", "Burkina Faso", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bg", "359", "Bulgaria", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bh", "973", "Bahrain", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bi", "257", "Burundi", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bj", "229", "Benin", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bl", "590", "Saint Barthélemy", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bm", "1", "Bermuda", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bn", "673", "Brunei Darussalam", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bo", "591", "Bolivia, Plurinational State Of", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("br", "55", "Brazil", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bs", "1", "Bahamas", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bt", "975", "Bhutan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bw", "267", "Botswana", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("by", "375", "Belarus", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("bz", "501", "Belize", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ca", "1", "Canada", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cc", "61", "Cocos (keeling) Islands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cd", "243", "Congo, The Democratic Republic Of The", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cf", "236", "Central African Republic", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cg", "242", "Congo", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ch", "41", "Switzerland", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ci", "225", "Côte D'ivoire", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ck", "682", "Cook Islands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cl", "56", "Chile", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cm", "237", "Cameroon", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cn", "86", "China", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("co", "57", "Colombia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cr", "506", "Costa Rica", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cu", "53", "Cuba", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cv", "238", "Cape Verde", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cw", "599", "Curaçao", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cx", "61", "Christmas Island", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cy", "357", "Cyprus", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("cz", "420", "Czech Republic", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("de", "49", "Germany", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("dj", "253", "Djibouti", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("dk", "45", "Denmark", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("dm", "1", "Dominica", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("do", "1", "Dominican Republic", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("dz", "213", "Algeria", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ec", "593", "Ecuador", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ee", "372", "Estonia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("eg", "20", "Egypt", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("er", "291", "Eritrea", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("es", "34", "Spain", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("et", "251", "Ethiopia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("fi", "358", "Finland", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("fj", "679", "Fiji", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("fk", "500", "Falkland Islands (malvinas)", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("fm", "691", "Micronesia, Federated States Of", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("fo", "298", "Faroe Islands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("fr", "33", "France", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ga", "241", "Gabon", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gb", "44", "United Kingdom", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gd", "1", "Grenada", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ge", "995", "Georgia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gf", "594", "French Guyana", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gh", "233", "Ghana", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gi", "350", "Gibraltar", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gl", "299", "Greenland", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gm", "220", "Gambia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gn", "224", "Guinea", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gp", "450", "Guadeloupe", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gq", "240", "Equatorial Guinea", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gr", "30", "Greece", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gt", "502", "Guatemala", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gu", "1", "Guam", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gw", "245", "Guinea-bissau", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("gy", "592", "Guyana", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("hk", "852", "Hong Kong", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("hn", "504", "Honduras", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("hr", "385", "Croatia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ht", "509", "Haiti", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("hu", "36", "Hungary", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("id", "62", "Indonesia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ie", "353", "Ireland", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("il", "972", "Israel", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("im", "44", "Isle Of Man", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("is", "354", "Iceland", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("in", "91", "India", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("io", "246", "British Indian Ocean Territory", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("iq", "964", "Iraq", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ir", "98", "Iran, Islamic Republic Of", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("it", "39", "Italy", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("je", "44", "Jersey ", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("jm", "1", "Jamaica", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("jo", "962", "Jordan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("jp", "81", "Japan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ke", "254", "Kenya", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("kg", "996", "Kyrgyzstan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("kh", "855", "Cambodia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ki", "686", "Kiribati", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("km", "269", "Comoros", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("kn", "1", "Saint Kitts and Nevis", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("kp", "850", "North Korea", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("kr", "82", "South Korea", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("kw", "965", "Kuwait", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ky", "1", "Cayman Islands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("kz", "7", "Kazakhstan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("la", "856", "Lao People's Democratic Republic", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("lb", "961", "Lebanon", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("lc", "1", "Saint Lucia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("li", "423", "Liechtenstein", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("lk", "94", "Sri Lanka", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("lr", "231", "Liberia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ls", "266", "Lesotho", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("lt", "370", "Lithuania", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("lu", "352", "Luxembourg", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("lv", "371", "Latvia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ly", "218", "Libya", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ma", "212", "Morocco", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mc", "377", "Monaco", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("md", "373", "Moldova, Republic Of", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("me", "382", "Montenegro", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mf", "590", "Saint Martin", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mg", "261", "Madagascar", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mh", "692", "Marshall Islands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mk", "389", "Macedonia (FYROM)", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ml", "223", "Mali", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mm", "95", "Myanmar", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mn", "976", "Mongolia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mo", "853", "Macau", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mp", "1", "Northern Mariana Islands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mq", "596", "Martinique", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mr", "222", "Mauritania", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ms", "1", "Montserrat", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mt", "356", "Malta", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mu", "230", "Mauritius", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mv", "960", "Maldives", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mw", "265", "Malawi", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mx", "52", "Mexico", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("my", "60", "Malaysia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("mz", "258", "Mozambique", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("na", "264", "Namibia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("nc", "687", "New Caledonia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ne", "227", "Niger", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("nf", "672", "Norfolk Islands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ng", "234", "Nigeria", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ni", "505", "Nicaragua", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("nl", "31", "Netherlands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("no", "47", "Norway", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("np", "977", "Nepal", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("nr", "674", "Nauru", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("nu", "683", "Niue", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("nz", "64", "New Zealand", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("om", "968", "Oman", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("pa", "507", "Panama", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("pe", "51", "Peru", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("pf", "689", "French Polynesia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("pg", "675", "Papua New Guinea", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ph", "63", "Philippines", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("pk", "92", "Pakistan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("pl", "48", "Poland", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("pm", "508", "Saint Pierre And Miquelon", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("pn", "870", "Pitcairn Islands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("pr", "1", "Puerto Rico", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ps", "970", "Palestine", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("pt", "351", "Portugal", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("pw", "680", "Palau", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("py", "595", "Paraguay", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("qa", "974", "Qatar", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("re", "262", "Réunion", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ro", "40", "Romania", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("rs", "381", "Serbia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ru", "7", "Russian Federation", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("rw", "250", "Rwanda", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sa", "966", "Saudi Arabia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sb", "677", "Solomon Islands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sc", "248", "Seychelles", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sd", "249", "Sudan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("se", "46", "Sweden", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sg", "65", "Singapore", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sh", "290", "Saint Helena, Ascension And Tristan Da Cunha", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("si", "386", "Slovenia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sk", "421", "Slovakia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sl", "232", "Sierra Leone", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sm", "378", "San Marino", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sn", "221", "Senegal", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("so", "252", "Somalia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sr", "597", "Suriname", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ss", "211", "South Sudan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("st", "239", "Sao Tome And Principe", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sv", "503", "El Salvador", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sx", "1", "Sint Maarten", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sy", "963", "Syrian Arab Republic", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("sz", "268", "Swaziland", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("tc", "1", "Turks and Caicos Islands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("td", "235", "Chad", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("tg", "228", "Togo", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("th", "66", "Thailand", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("tj", "992", "Tajikistan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("tk", "690", "Tokelau", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("tl", "670", "Timor-leste", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("tm", "993", "Turkmenistan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("tn", "216", "Tunisia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("to", "676", "Tonga", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("tr", "90", "Turkey", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("tt", "1", "Trinidad &amp; Tobago", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("tv", "688", "Tuvalu", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("tw", "886", "Taiwan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("tz", "255", "Tanzania, United Republic Of", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ua", "380", "Ukraine", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ug", "256", "Uganda", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("us", "1", "United States", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("uy", "598", "Uruguay", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("uz", "998", "Uzbekistan", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("va", "379", "Holy See (vatican City State)", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("vc", "1", "Saint Vincent &amp; The Grenadines", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ve", "58", "Venezuela, Bolivarian Republic Of", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("vg", "1", "British Virgin Islands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("vi", "1", "US Virgin Islands", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("vn", "84", "Vietnam", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("vu", "678", "Vanuatu", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("wf", "681", "Wallis And Futuna", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ws", "685", "Samoa", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("xk", "383", "Kosovo", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("ye", "967", "Yemen", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("yt", "262", "Mayotte", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("za", "27", "South Africa", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("zm", "260", "Zambia", DEFAULT_FLAG_RES));
        countries.add(new CCPCountry("zw", "263", "Zimbabwe", DEFAULT_FLAG_RES));
        return countries;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public int getFlagID() {
        if (flagResID == -99) {
            flagResID = getFlagMasterResID(this);
        }
        return flagResID;
    }

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void log() {
        try {
            Log.d(TAG, "Country->" + nameCode + ":" + phoneCode + ":" + name);
        } catch (NullPointerException ex) {
            Log.d(TAG, "Null");
        }
    }

    String logString() {
        return nameCode.toUpperCase() + " +" + phoneCode + "(" + name + ")";
    }

    /**
     * If country have query word in name or name code or phone code, this will return true.
     *
     * @param query
     * @return
     */
    boolean isEligibleForQuery(String query) {
        query = query.toLowerCase();
        return containsQueryWord("Name", getName(), query) ||
                containsQueryWord("NameCode", getNameCode(), query) ||
                containsQueryWord("PhoneCode", getPhoneCode(), query) ||
                containsQueryWord("EnglishName", getEnglishName(), query);
    }

    private boolean containsQueryWord(String fieldName, String fieldValue, String query) {
        try {
            if (fieldValue == null || query == null) {
                return false;
            } else {
                return fieldValue.toLowerCase(Locale.ROOT).contains(query);
            }
        } catch (Exception e) {
            Log.w("CCPCountry", fieldName + ":" + fieldValue +
                    " failed to execute toLowerCase(Locale.ROOT).contains(query) " +
                    "for query:" + query);
            return false;
        }
    }

    @Override
    public int compareTo(@NonNull CCPCountry o) {
        return Collator.getInstance().compare(getName(), o.getName());
    }
}
