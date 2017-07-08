package com.hbb20;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by hbb20 on 11/1/16.
 */
class Country implements Comparable<Country> {
    static String TAG = "Class Country";
    static CountryCodePicker.Language loadedLibraryMasterListLanguage;
    static String dialogTitle, searchHintMessage, noResultFoundAckMessage;
    static List<Country> loadedLibraryMaterList;
    String nameCode;
    String phoneCode;
    String name, englishName;

    public Country() {

    }

    public Country(String nameCode, String phoneCode, String name) {
        this.nameCode = nameCode;
        this.phoneCode = phoneCode;
        this.name = name;
    }

    public static CountryCodePicker.Language getLoadedLibraryMasterListLanguage() {
        return loadedLibraryMasterListLanguage;
    }

    public static void setLoadedLibraryMasterListLanguage(CountryCodePicker.Language loadedLibraryMasterListLanguage) {
        Country.loadedLibraryMasterListLanguage = loadedLibraryMasterListLanguage;
    }

    public static List<Country> getLoadedLibraryMaterList() {
        return loadedLibraryMaterList;
    }

    public static void setLoadedLibraryMaterList(List<Country> loadedLibraryMaterList) {
        Country.loadedLibraryMaterList = loadedLibraryMaterList;
    }

    /**
     * This function parses the raw/countries.xml file, and get list of all the countries.
     *
     * @param context: required to access application resources (where country.xml is).
     * @return List of all the countries available in xml file.
     */
    public static void loadDataFromXML(Context context, CountryCodePicker.Language language) {
        List<Country> countries = new ArrayList<Country>();
        String tempDialogTitle = "", tempSeachHint = "", tempNoResultAck = "";
        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlFactoryObject.newPullParser();
            InputStream ins = context.getResources().openRawResource(context.getResources().getIdentifier(language.toString().toLowerCase(), "raw", context.getPackageName()));
            xmlPullParser.setInput(ins, null);
            int event = xmlPullParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = xmlPullParser.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.END_TAG:
                        if (name.equals("country")) {
                            Country country = new Country();
                            country.setNameCode(xmlPullParser.getAttributeValue(null, "name_code").toUpperCase());
                            country.setPhoneCode(xmlPullParser.getAttributeValue(null, "phone_code"));
                            country.setEnglishName(xmlPullParser.getAttributeValue(null, "english_name"));
                            country.setName(xmlPullParser.getAttributeValue(null, "name"));
                            countries.add(country);
                        } else if (name.equals("ccp_dialog_title")) {
                            tempDialogTitle = xmlPullParser.getAttributeValue(null, "translation");
                        } else if (name.equals("ccp_dialog_search_hint_message")) {
                            tempSeachHint = xmlPullParser.getAttributeValue(null, "translation");
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
        searchHintMessage = tempSeachHint.length() > 0 ? tempSeachHint : "Search...";
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
    private static Country getCountryForCode(Context context, CountryCodePicker.Language language, List<Country> preferredCountries, String code) {

        /**
         * check in preferred countries
         */
        if (preferredCountries != null && !preferredCountries.isEmpty()) {
            for (Country country : preferredCountries) {
                if (country.getPhoneCode().equals(code)) {
                    return country;
                }
            }
        }

        for (Country country : getLibraryMasterCountryList(context, language)) {
            if (country.getPhoneCode().equals(code)) {
                return country;
            }
        }
        return null;
    }

    public static List<Country> getCustomMasterCountryList(Context context, CountryCodePicker codePicker) {
        codePicker.refreshCustomMasterList();
        if (codePicker.customMasterCountriesList != null && codePicker.customMasterCountriesList.size() > 0) {
            return codePicker.getCustomMasterCountriesList();
        } else {
            return getLibraryMasterCountryList(context, codePicker.getCustomLanguage());
        }
    }

    /**
     * Search a country which matches @param nameCode.
     *
     * @param context
     * @param customMasterCountriesList
     * @param nameCode                  country name code. i.e US or us or Au. See countries.xml for all code names.  @return Country that has phone code as @param code.
     */
    public static Country getCountryForNameCodeFromCustomMasterList(Context context, List<Country> customMasterCountriesList, CountryCodePicker.Language language, String nameCode) {
        if (customMasterCountriesList == null || customMasterCountriesList.size() == 0) {
            return getCountryForNameCodeFromLibraryMasterList(context, language, nameCode);
        } else {
            for (Country country : customMasterCountriesList) {
                if (country.getNameCode().equalsIgnoreCase(nameCode)) {
                    return country;
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
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     */
    public static Country getCountryForNameCodeFromLibraryMasterList(Context context, CountryCodePicker.Language language, String nameCode) {
        List<Country> countries = Country.getLibraryMasterCountryList(context, language);
        for (Country country : countries) {
            if (country.getNameCode().equalsIgnoreCase(nameCode)) {
                return country;
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
    static Country getCountryForCode(Context context, CountryCodePicker.Language language, List<Country> preferredCountries, int code) {
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
    static Country getCountryForNumber(Context context, CountryCodePicker.Language language, List<Country> preferredCountries, String fullNumber) {
        int firstDigit;
        if (fullNumber.length() != 0) {
            if (fullNumber.charAt(0) == '+') {
                firstDigit = 1;
            } else {
                firstDigit = 0;
            }
            Country country = null;
            for (int i = firstDigit; i < firstDigit + 4; i++) {
                String code = fullNumber.substring(firstDigit, i);
                country = Country.getCountryForCode(context, language, preferredCountries, code);
                if (country != null) {
                    return country;
                }
            }
        }
        return null;
    }

    /**
     * Returns image res based on country name code
     *
     * @param country
     * @return
     */
    static int getFlagResID(Country country) {
        switch (country.getNameCode().toLowerCase()) {
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
            case "at": //austria
                return R.drawable.flag_austria;
            case "au": //australia
                return R.drawable.flag_australia;
            case "aw": //aruba
                return R.drawable.flag_aruba;
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
            case "gq": //equatorial guinea
                return R.drawable.flag_equatorial_guinea;
            case "gr": //greece
                return R.drawable.flag_greece;
            case "gt": //guatemala
                return R.drawable.flag_guatemala;
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
            case "in": //india
                return R.drawable.flag_india;
            case "iq": //iraq
                return R.drawable.flag_iraq_new;
            case "ir": //iran, islamic republic of
                return R.drawable.flag_iran;
            case "it": //italy
                return R.drawable.flag_italy;
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
     * This will return all the countries. No preference is manages.
     * Anytime new country need to be added, add it
     *
     * @return
     */
    public static List<Country> getLibraryMasterCountryList(Context context, CountryCodePicker.Language language) {
        if (loadedLibraryMasterListLanguage == null || language != loadedLibraryMasterListLanguage || loadedLibraryMaterList == null || loadedLibraryMaterList.size() == 0) { //when it is required to load country in country list
            loadDataFromXML(context, language);
        }
        return loadedLibraryMaterList;
    }

    public static List<Country> getLibraryMasterCountriesEnglish() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "Afghanistan"));
        countries.add(new Country("al", "355", "Albania"));
        countries.add(new Country("dz", "213", "Algeria"));
        countries.add(new Country("ad", "376", "Andorra"));
        countries.add(new Country("ao", "244", "Angola"));
        countries.add(new Country("ai", "1", "Anguilla"));
        countries.add(new Country("aq", "672", "Antarctica"));
        countries.add(new Country("ag", "1", "Antigua and Barbuda"));
        countries.add(new Country("ar", "54", "Argentina"));
        countries.add(new Country("am", "374", "Armenia"));
        countries.add(new Country("aw", "297", "Aruba"));
        countries.add(new Country("au", "61", "Australia"));
        countries.add(new Country("at", "43", "Austria"));
        countries.add(new Country("az", "994", "Azerbaijan"));
        countries.add(new Country("bs", "1", "Bahamas"));
        countries.add(new Country("bh", "973", "Bahrain"));
        countries.add(new Country("bd", "880", "Bangladesh"));
        countries.add(new Country("bb", "1", "Barbados"));
        countries.add(new Country("by", "375", "Belarus"));
        countries.add(new Country("be", "32", "Belgium"));
        countries.add(new Country("bz", "501", "Belize"));
        countries.add(new Country("bj", "229", "Benin"));
        countries.add(new Country("bm", "1", "Bermuda"));
        countries.add(new Country("bt", "975", "Bhutan"));
        countries.add(new Country("bo", "591", "Bolivia, Plurinational State Of"));
        countries.add(new Country("ba", "387", "Bosnia And Herzegovina"));
        countries.add(new Country("bw", "267", "Botswana"));
        countries.add(new Country("br", "55", "Brazil"));
        countries.add(new Country("vg", "1", "British Virgin Islands"));
        countries.add(new Country("bn", "673", "Brunei Darussalam"));
        countries.add(new Country("bg", "359", "Bulgaria"));
        countries.add(new Country("bf", "226", "Burkina Faso"));
        countries.add(new Country("mm", "95", "Myanmar"));
        countries.add(new Country("bi", "257", "Burundi"));
        countries.add(new Country("kh", "855", "Cambodia"));
        countries.add(new Country("cm", "237", "Cameroon"));
        countries.add(new Country("ca", "1", "Canada"));
        countries.add(new Country("cv", "238", "Cape Verde"));
        countries.add(new Country("ky", "1", "Cayman Islands"));
        countries.add(new Country("cf", "236", "Central African Republic"));
        countries.add(new Country("td", "235", "Chad"));
        countries.add(new Country("cl", "56", "Chile"));
        countries.add(new Country("cn", "86", "China"));
        countries.add(new Country("cx", "61", "Christmas Island"));
        countries.add(new Country("cc", "61", "Cocos (keeling) Islands"));
        countries.add(new Country("co", "57", "Colombia"));
        countries.add(new Country("km", "269", "Comoros"));
        countries.add(new Country("cg", "242", "Congo"));
        countries.add(new Country("cd", "243", "Congo, The Democratic Republic Of The"));
        countries.add(new Country("ck", "682", "Cook Islands"));
        countries.add(new Country("cr", "506", "Costa Rica"));
        countries.add(new Country("hr", "385", "Croatia"));
        countries.add(new Country("cu", "53", "Cuba"));
        countries.add(new Country("cy", "357", "Cyprus"));
        countries.add(new Country("cz", "420", "Czech Republic"));
        countries.add(new Country("dk", "45", "Denmark"));
        countries.add(new Country("dj", "253", "Djibouti"));
        countries.add(new Country("dm", "1", "Dominica"));
        countries.add(new Country("do", "1", "Dominican Republic"));
        countries.add(new Country("tl", "670", "Timor-leste"));
        countries.add(new Country("ec", "593", "Ecuador"));
        countries.add(new Country("eg", "20", "Egypt"));
        countries.add(new Country("sv", "503", "El Salvador"));
        countries.add(new Country("gq", "240", "Equatorial Guinea"));
        countries.add(new Country("er", "291", "Eritrea"));
        countries.add(new Country("ee", "372", "Estonia"));
        countries.add(new Country("et", "251", "Ethiopia"));
        countries.add(new Country("fk", "500", "Falkland Islands (malvinas)"));
        countries.add(new Country("fo", "298", "Faroe Islands"));
        countries.add(new Country("fj", "679", "Fiji"));
        countries.add(new Country("fi", "358", "Finland"));
        countries.add(new Country("fr", "33", "France"));
        countries.add(new Country("gf", "594", "French Guyana"));
        countries.add(new Country("pf", "689", "French Polynesia"));
        countries.add(new Country("ga", "241", "Gabon"));
        countries.add(new Country("gm", "220", "Gambia"));
        countries.add(new Country("ge", "995", "Georgia"));
        countries.add(new Country("de", "49", "Germany"));
        countries.add(new Country("gh", "233", "Ghana"));
        countries.add(new Country("gi", "350", "Gibraltar"));
        countries.add(new Country("gr", "30", "Greece"));
        countries.add(new Country("gl", "299", "Greenland"));
        countries.add(new Country("gd", "1", "Grenada"));
        countries.add(new Country("gt", "502", "Guatemala"));
        countries.add(new Country("gn", "224", "Guinea"));
        countries.add(new Country("gw", "245", "Guinea-bissau"));
        countries.add(new Country("gy", "592", "Guyana"));
        countries.add(new Country("ht", "509", "Haiti"));
        countries.add(new Country("hn", "504", "Honduras"));
        countries.add(new Country("hk", "852", "Hong Kong"));
        countries.add(new Country("hu", "36", "Hungary"));
        countries.add(new Country("in", "91", "India"));
        countries.add(new Country("id", "62", "Indonesia"));
        countries.add(new Country("ir", "98", "Iran, Islamic Republic Of"));
        countries.add(new Country("iq", "964", "Iraq"));
        countries.add(new Country("ie", "353", "Ireland"));
        countries.add(new Country("im", "44", "Isle Of Man"));
        countries.add(new Country("il", "972", "Israel"));
        countries.add(new Country("ps", "970", "Palestine"));
        countries.add(new Country("it", "39", "Italy"));
        countries.add(new Country("ci", "225", "Côte D\'ivoire"));
        countries.add(new Country("jm", "1", "Jamaica"));
        countries.add(new Country("jp", "81", "Japan"));
        countries.add(new Country("jo", "962", "Jordan"));
        countries.add(new Country("kz", "7", "Kazakhstan"));
        countries.add(new Country("ke", "254", "Kenya"));
        countries.add(new Country("ki", "686", "Kiribati"));
        countries.add(new Country("kw", "965", "Kuwait"));
        countries.add(new Country("kg", "996", "Kyrgyzstan"));
        countries.add(new Country("la", "856", "Lao People\'s Democratic Republic"));
        countries.add(new Country("lv", "371", "Latvia"));
        countries.add(new Country("lb", "961", "Lebanon"));
        countries.add(new Country("ls", "266", "Lesotho"));
        countries.add(new Country("lr", "231", "Liberia"));
        countries.add(new Country("ly", "218", "Libya"));
        countries.add(new Country("li", "423", "Liechtenstein"));
        countries.add(new Country("lt", "370", "Lithuania"));
        countries.add(new Country("lu", "352", "Luxembourg"));
        countries.add(new Country("mo", "853", "Macao"));
        countries.add(new Country("mk", "389", "Macedonia, The Former Yugoslav Republic Of"));
        countries.add(new Country("mg", "261", "Madagascar"));
        countries.add(new Country("mw", "265", "Malawi"));
        countries.add(new Country("my", "60", "Malaysia"));
        countries.add(new Country("mv", "960", "Maldives"));
        countries.add(new Country("ml", "223", "Mali"));
        countries.add(new Country("mt", "356", "Malta"));
        countries.add(new Country("mh", "692", "Marshall Islands"));
        countries.add(new Country("mq", "596", "Martinique"));
        countries.add(new Country("mr", "222", "Mauritania"));
        countries.add(new Country("mu", "230", "Mauritius"));
        countries.add(new Country("yt", "262", "Mayotte"));
        countries.add(new Country("mx", "52", "Mexico"));
        countries.add(new Country("fm", "691", "Micronesia, Federated States Of"));
        countries.add(new Country("md", "373", "Moldova, Republic Of"));
        countries.add(new Country("mc", "377", "Monaco"));
        countries.add(new Country("mn", "976", "Mongolia"));
        countries.add(new Country("ms", "1", "Montserrat"));
        countries.add(new Country("me", "382", "Montenegro"));
        countries.add(new Country("ma", "212", "Morocco"));
        countries.add(new Country("mz", "258", "Mozambique"));
        countries.add(new Country("na", "264", "Namibia"));
        countries.add(new Country("nr", "674", "Nauru"));
        countries.add(new Country("np", "977", "Nepal"));
        countries.add(new Country("nl", "31", "Netherlands"));
        countries.add(new Country("nc", "687", "New Caledonia"));
        countries.add(new Country("nz", "64", "New Zealand"));
        countries.add(new Country("ni", "505", "Nicaragua"));
        countries.add(new Country("ne", "227", "Niger"));
        countries.add(new Country("ng", "234", "Nigeria"));
        countries.add(new Country("nu", "683", "Niue"));
        countries.add(new Country("kp", "850", "North Korea"));
        countries.add(new Country("no", "47", "Norway"));
        countries.add(new Country("om", "968", "Oman"));
        countries.add(new Country("pk", "92", "Pakistan"));
        countries.add(new Country("pw", "680", "Palau"));
        countries.add(new Country("pa", "507", "Panama"));
        countries.add(new Country("pg", "675", "Papua New Guinea"));
        countries.add(new Country("py", "595", "Paraguay"));
        countries.add(new Country("pe", "51", "Peru"));
        countries.add(new Country("ph", "63", "Philippines"));
        countries.add(new Country("pn", "870", "Pitcairn"));
        countries.add(new Country("pl", "48", "Poland"));
        countries.add(new Country("pt", "351", "Portugal"));
        countries.add(new Country("pr", "1", "Puerto Rico"));
        countries.add(new Country("qa", "974", "Qatar"));
        countries.add(new Country("re", "262", "Réunion"));
        countries.add(new Country("ro", "40", "Romania"));
        countries.add(new Country("ru", "7", "Russian Federation"));
        countries.add(new Country("rw", "250", "Rwanda"));
        countries.add(new Country("bl", "590", "Saint Barthélemy"));
        countries.add(new Country("kn", "1", "Saint Kitts and Nevis"));
        countries.add(new Country("lc", "1", "Saint Lucia"));
        countries.add(new Country("vc", "1", "Saint Vincent & The Grenadines"));
        countries.add(new Country("ws", "685", "Samoa"));
        countries.add(new Country("sm", "378", "San Marino"));
        countries.add(new Country("st", "239", "Sao Tome And Principe"));
        countries.add(new Country("sa", "966", "Saudi Arabia"));
        countries.add(new Country("sn", "221", "Senegal"));
        countries.add(new Country("rs", "381", "Serbia"));
        countries.add(new Country("sc", "248", "Seychelles"));
        countries.add(new Country("sl", "232", "Sierra Leone"));
        countries.add(new Country("sg", "65", "Singapore"));
        countries.add(new Country("sx", "1", "Sint Maarten"));
        countries.add(new Country("sk", "421", "Slovakia"));
        countries.add(new Country("si", "386", "Slovenia"));
        countries.add(new Country("sb", "677", "Solomon Islands"));
        countries.add(new Country("so", "252", "Somalia"));
        countries.add(new Country("za", "27", "South Africa"));
        countries.add(new Country("kr", "82", "South Korea"));
        countries.add(new Country("es", "34", "Spain"));
        countries.add(new Country("lk", "94", "Sri Lanka"));
        countries.add(new Country("sh", "290", "Saint Helena, Ascension And Tristan Da Cunha"));
        countries.add(new Country("pm", "508", "Saint Pierre And Miquelon"));
        countries.add(new Country("sd", "249", "Sudan"));
        countries.add(new Country("sr", "597", "Suriname"));
        countries.add(new Country("sz", "268", "Swaziland"));
        countries.add(new Country("se", "46", "Sweden"));
        countries.add(new Country("ch", "41", "Switzerland"));
        countries.add(new Country("sy", "963", "Syrian Arab Republic"));
        countries.add(new Country("tw", "886", "Taiwan, Province Of China"));
        countries.add(new Country("tj", "992", "Tajikistan"));
        countries.add(new Country("tz", "255", "Tanzania, United Republic Of"));
        countries.add(new Country("th", "66", "Thailand"));
        countries.add(new Country("tg", "228", "Togo"));
        countries.add(new Country("tk", "690", "Tokelau"));
        countries.add(new Country("to", "676", "Tonga"));
        countries.add(new Country("tt", "1", "Trinidad & Tobago"));
        countries.add(new Country("tn", "216", "Tunisia"));
        countries.add(new Country("tr", "90", "Turkey"));
        countries.add(new Country("tm", "993", "Turkmenistan"));
        countries.add(new Country("tc", "1", "Turks and Caicos Islands"));
        countries.add(new Country("tv", "688", "Tuvalu"));
        countries.add(new Country("ae", "971", "United Arab Emirates"));
        countries.add(new Country("ug", "256", "Uganda"));
        countries.add(new Country("gb", "44", "United Kingdom"));
        countries.add(new Country("ua", "380", "Ukraine"));
        countries.add(new Country("uy", "598", "Uruguay"));
        countries.add(new Country("us", "1", "United States"));
        countries.add(new Country("vi", "1", "US Virgin Islands"));
        countries.add(new Country("uz", "998", "Uzbekistan"));
        countries.add(new Country("vu", "678", "Vanuatu"));
        countries.add(new Country("va", "39", "Holy See (vatican City State)"));
        countries.add(new Country("ve", "58", "Venezuela, Bolivarian Republic Of"));
        countries.add(new Country("vn", "84", "Viet Nam"));
        countries.add(new Country("wf", "681", "Wallis And Futuna"));
        countries.add(new Country("ye", "967", "Yemen"));
        countries.add(new Country("zm", "260", "Zambia"));
        countries.add(new Country("zw", "263", "Zimbabwe"));
        return countries;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public int getFlagID() {
        return getFlagResID(this);
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

    public String logString() {
        return nameCode.toUpperCase() + " +" + phoneCode + "(" + name + ")";
    }


    /*public static List<Country> getLibraryMasterCountriesSpanish() {
        List<Country> countries = new ArrayList<>();

        return countries;
    }*/


/*public static List<Country> getLibraryMasterCountriesSpanish() {
        List<Country> countries = new ArrayList<>();

        return countries;
    }*/

    /**
     * If country have query word in name or name code or phone code, this will return true.
     *
     * @param query
     * @return
     */
    public boolean isEligibleForQuery(String query) {
        query = query.toLowerCase();
        return getName().toLowerCase().contains(query) || getNameCode().toLowerCase().contains(query) || getPhoneCode().toLowerCase().contains(query) || getEnglishName().toLowerCase().contains(query);
    }


    @Override
    public int compareTo(@NonNull Country o) {
        return getName().compareTo(o.getName());
    }
}
