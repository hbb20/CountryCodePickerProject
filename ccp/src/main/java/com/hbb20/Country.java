package com.hbb20;

import android.content.Context;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by hbb20 on 11/1/16.
 */
class Country {
    static String TAG = "Class Country";
    String nameCode;
    String phoneCode;
    String name;

    public Country() {

    }


    public Country(String nameCode, String phoneCode, String name) {
        this.nameCode = nameCode;
        this.phoneCode = phoneCode;
        this.name = name;
    }

    /**
     * This function parses the raw/countries.xml file, and get list of all the countries.
     *
     * @param context: required to access application resources (where country.xml is).
     * @return List of all the countries available in xml file.
     */
    public static List<Country> readXMLofCountries(Context context) {
        List<Country> countries = new ArrayList<Country>();
        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlFactoryObject.newPullParser();
            InputStream ins = context.getResources().openRawResource(R.raw.countries);
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
                            country.setNameCode(xmlPullParser.getAttributeValue(null, "code").toUpperCase());
                            country.setPhoneCode(xmlPullParser.getAttributeValue(null, "phoneCode"));
                            country.setName(xmlPullParser.getAttributeValue(null, "name"));
                            countries.add(country);
                        }
                        break;
                }
                event = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return countries;
    }

    /**
     * Search a country which matches @param code.
     *
     * @param preferredCountries is list of preference countries.
     * @param code               phone code. i.e "91" or "1"
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     * if same code (e.g. +1) available for more than one country ( US, canada) , this function will return preferred country.
     */
    private static Country getCountryForCode(CountryCodePicker.Language language, List<Country> preferredCountries, String code) {

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

        for (Country country : getLibraryMasterCountryList(language)) {
            if (country.getPhoneCode().equals(code)) {
                return country;
            }
        }
        return null;
    }

    public static List<Country> getCustomMasterCountryList(CountryCodePicker codePicker) {
        codePicker.refreshCustomMasterList();
        if (codePicker.customMasterCountriesList != null && codePicker.customMasterCountriesList.size() > 0) {
            return codePicker.getCustomMasterCountriesList();
        } else {
            return getLibraryMasterCountryList(codePicker.getCustomLanguage());
        }
    }

    /**
     * Search a country which matches @param nameCode.
     *
     *
     * @param customMasterCountriesList
     * @param nameCode country name code. i.e US or us or Au. See countries.xml for all code names.
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     */
    public static Country getCountryForNameCodeFromCustomMasterList(List<Country> customMasterCountriesList, CountryCodePicker.Language language, String nameCode) {
        if (customMasterCountriesList == null || customMasterCountriesList.size() == 0) {
            return getCountryForNameCodeFromLibraryMasterList(language, nameCode);
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
     * @param nameCode country name code. i.e US or us or Au. See countries.xml for all code names.
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     */
    public static Country getCountryForNameCodeFromLibraryMasterList(CountryCodePicker.Language language, String nameCode) {
        List<Country> countries = Country.getLibraryMasterCountryList(language);
        for (Country country : countries) {
            if (country.getNameCode().equalsIgnoreCase(nameCode)) {
                return country;
            }
        }
        return null;
    }

    /**
     * Search a country which matches @param nameCode.
     *
     * @param nameCode country name code. i.e US or us or Au. See countries.xml for all code names.
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     */
    public static Country getCountryForNameCodeTrial(String nameCode, Context context) {
        List<Country> countries = Country.readXMLofCountries(context);
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
     * @param preferredCountries list of country with priority,
     * @param code               phone code. i.e 91 or 1
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     */
    static Country getCountryForCode(CountryCodePicker.Language language, List<Country> preferredCountries, int code) {
        return getCountryForCode(language, preferredCountries, code + "");
    }

    /**
     * Finds country code by matching substring from left to right from full number.
     * For example. if full number is +819017901357
     * function will ignore "+" and try to find match for first character "8"
     * if any country found for code "8", will return that country. If not, then it will
     * try to find country for "81". and so on till first 3 characters ( maximum number of characters in country code is 3).
     *
     * @param preferredCountries countries of preference
     * @param fullNumber         full number ( "+" (optional)+ country code + carrier number) i.e. +819017901357 / 819017901357 / 918866667722
     * @return Country JP +81(Japan) for +819017901357 or 819017901357
     * Country IN +91(India) for  918866667722
     * null for 2956635321 ( as neither of "2", "29" and "295" matches any country code)
     */
    static Country getCountryForNumber(CountryCodePicker.Language language, List<Country> preferredCountries, String fullNumber) {
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
                country = Country.getCountryForCode(language, preferredCountries, code);
                if (country != null) {
                    return country;
                }
            }
        }
        return null;
    }

    /**
     * Returns image res based on country name code
     * @param country
     * @return
     */
    static int getFlagResID(Country country) {
        switch (country.getNameCode()) {
            case "af": //afghanistan
                return R.drawable.flag_afghanistan;
            case "al": //albania
                return R.drawable.flag_albania;
            case "dz": //algeria
                return R.drawable.flag_algeria;
            case "ad": //andorra
                return R.drawable.flag_andorra;
            case "ao": //angola
                return R.drawable.flag_angola;
            case "aq": //antarctica // custom
                return R.drawable.flag_antarctica;
            case "ar": //argentina
                return R.drawable.flag_argentina;
            case "am": //armenia
                return R.drawable.flag_armenia;
            case "aw": //aruba
                return R.drawable.flag_aruba;
            case "au": //australia
                return R.drawable.flag_australia;
            case "at": //austria
                return R.drawable.flag_austria;
            case "az": //azerbaijan
                return R.drawable.flag_azerbaijan;
            case "bh": //bahrain
                return R.drawable.flag_bahrain;
            case "bd": //bangladesh
                return R.drawable.flag_bangladesh;
            case "by": //belarus
                return R.drawable.flag_belarus;
            case "be": //belgium
                return R.drawable.flag_belgium;
            case "bz": //belize
                return R.drawable.flag_belize;
            case "bj": //benin
                return R.drawable.flag_benin;
            case "bt": //bhutan
                return R.drawable.flag_bhutan;
            case "bo": //bolivia, plurinational state of
                return R.drawable.flag_bolivia;
            case "ba": //bosnia and herzegovina
                return R.drawable.flag_bosnia;
            case "bw": //botswana
                return R.drawable.flag_botswana;
            case "br": //brazil
                return R.drawable.flag_brazil;
            case "bn": //brunei darussalam // custom
                return R.drawable.flag_brunei;
            case "bg": //bulgaria
                return R.drawable.flag_bulgaria;
            case "bf": //burkina faso
                return R.drawable.flag_burkina_faso;
            case "mm": //myanmar
                return R.drawable.flag_myanmar;
            case "bi": //burundi
                return R.drawable.flag_burundi;
            case "kh": //cambodia
                return R.drawable.flag_cambodia;
            case "cm": //cameroon
                return R.drawable.flag_cameroon;
            case "ca": //canada
                return R.drawable.flag_canada;
            case "cv": //cape verde
                return R.drawable.flag_cape_verde;
            case "cf": //central african republic
                return R.drawable.flag_central_african_republic;
            case "td": //chad
                return R.drawable.flag_chad;
            case "cl": //chile
                return R.drawable.flag_chile;
            case "cn": //china
                return R.drawable.flag_china;
            case "cx": //christmas island
                return R.drawable.flag_christmas_island;
            case "cc": //cocos (keeling) islands
                return R.drawable.flag_cocos;// custom
            case "co": //colombia
                return R.drawable.flag_colombia;
            case "km": //comoros
                return R.drawable.flag_comoros;
            case "cg": //congo
                return R.drawable.flag_republic_of_the_congo;
            case "cd": //congo, the democratic republic of the
                return R.drawable.flag_democratic_republic_of_the_congo;
            case "ck": //cook islands
                return R.drawable.flag_cook_islands;
            case "cr": //costa rica
                return R.drawable.flag_costa_rica;
            case "hr": //croatia
                return R.drawable.flag_croatia;
            case "cu": //cuba
                return R.drawable.flag_cuba;
            case "cy": //cyprus
                return R.drawable.flag_cyprus;
            case "cz": //czech republic
                return R.drawable.flag_czech_republic;
            case "dk": //denmark
                return R.drawable.flag_denmark;
            case "dj": //djibouti
                return R.drawable.flag_djibouti;
            case "tl": //timor-leste
                return R.drawable.flag_timor_leste;
            case "ec": //ecuador
                return R.drawable.flag_ecuador;
            case "eg": //egypt
                return R.drawable.flag_egypt;
            case "sv": //el salvador
                return R.drawable.flag_el_salvador;
            case "gq": //equatorial guinea
                return R.drawable.flag_equatorial_guinea;
            case "er": //eritrea
                return R.drawable.flag_eritrea;
            case "ee": //estonia
                return R.drawable.flag_estonia;
            case "et": //ethiopia
                return R.drawable.flag_ethiopia;
            case "fk": //falkland islands (malvinas)
                return R.drawable.flag_falkland_islands;
            case "fo": //faroe islands
                return R.drawable.flag_faroe_islands;
            case "fj": //fiji
                return R.drawable.flag_fiji;
            case "fi": //finland
                return R.drawable.flag_finland;
            case "fr": //france
                return R.drawable.flag_france;
            case "pf": //french polynesia
                return R.drawable.flag_french_polynesia;
            case "ga": //gabon
                return R.drawable.flag_gabon;
            case "gm": //gambia
                return R.drawable.flag_gambia;
            case "ge": //georgia
                return R.drawable.flag_georgia;
            case "de": //germany
                return R.drawable.flag_germany;
            case "gh": //ghana
                return R.drawable.flag_ghana;
            case "gi": //gibraltar
                return R.drawable.flag_gibraltar;
            case "gr": //greece
                return R.drawable.flag_greece;
            case "gl": //greenland
                return R.drawable.flag_greenland;
            case "gt": //guatemala
                return R.drawable.flag_guatemala;
            case "gn": //guinea
                return R.drawable.flag_guinea;
            case "gw": //guinea-bissau
                return R.drawable.flag_guinea_bissau;
            case "gy": //guyana
                return R.drawable.flag_guyana;
            case "ht": //haiti
                return R.drawable.flag_haiti;
            case "hn": //honduras
                return R.drawable.flag_honduras;
            case "hk": //hong kong
                return R.drawable.flag_hong_kong;
            case "hu": //hungary
                return R.drawable.flag_hungary;
            case "in": //india
                return R.drawable.flag_india;
            case "id": //indonesia
                return R.drawable.flag_indonesia;
            case "ir": //iran, islamic republic of
                return R.drawable.flag_iran;
            case "iq": //iraq
                return R.drawable.flag_iraq;
            case "ie": //ireland
                return R.drawable.flag_ireland;
            case "im": //isle of man
                return R.drawable.flag_isleof_man; // custom
            case "il": //israel
                return R.drawable.flag_israel;
            case "it": //italy
                return R.drawable.flag_italy;
            case "ci": //côte d\'ivoire
                return R.drawable.flag_cote_divoire;
            case "jp": //japan
                return R.drawable.flag_japan;
            case "jo": //jordan
                return R.drawable.flag_jordan;
            case "kz": //kazakhstan
                return R.drawable.flag_kazakhstan;
            case "ke": //kenya
                return R.drawable.flag_kenya;
            case "ki": //kiribati
                return R.drawable.flag_kiribati;
            case "kw": //kuwait
                return R.drawable.flag_kuwait;
            case "kg": //kyrgyzstan
                return R.drawable.flag_kyrgyzstan;
            case "la": //lao people\'s democratic republic
                return R.drawable.flag_laos;
            case "lv": //latvia
                return R.drawable.flag_latvia;
            case "lb": //lebanon
                return R.drawable.flag_lebanon;
            case "ls": //lesotho
                return R.drawable.flag_lesotho;
            case "lr": //liberia
                return R.drawable.flag_liberia;
            case "ly": //libya
                return R.drawable.flag_libya;
            case "li": //liechtenstein
                return R.drawable.flag_liechtenstein;
            case "lt": //lithuania
                return R.drawable.flag_lithuania;
            case "lu": //luxembourg
                return R.drawable.flag_luxembourg;
            case "mo": //macao
                return R.drawable.flag_macao;
            case "mk": //macedonia, the former yugoslav republic of
                return R.drawable.flag_macedonia;
            case "mg": //madagascar
                return R.drawable.flag_madagascar;
            case "mw": //malawi
                return R.drawable.flag_malawi;
            case "my": //malaysia
                return R.drawable.flag_malaysia;
            case "mv": //maldives
                return R.drawable.flag_maldives;
            case "ml": //mali
                return R.drawable.flag_mali;
            case "mt": //malta
                return R.drawable.flag_malta;
            case "mh": //marshall islands
                return R.drawable.flag_marshall_islands;
            case "mr": //mauritania
                return R.drawable.flag_mauritania;
            case "mu": //mauritius
                return R.drawable.flag_mauritius;
            case "yt": //mayotte
                return R.drawable.flag_martinique; // no exact flag found
            case "mx": //mexico
                return R.drawable.flag_mexico;
            case "fm": //micronesia, federated states of
                return R.drawable.flag_micronesia;
            case "md": //moldova, republic of
                return R.drawable.flag_moldova;
            case "mc": //monaco
                return R.drawable.flag_monaco;
            case "mn": //mongolia
                return R.drawable.flag_mongolia;
            case "me": //montenegro
                return R.drawable.flag_of_montenegro;// custom
            case "ma": //morocco
                return R.drawable.flag_morocco;
            case "mz": //mozambique
                return R.drawable.flag_mozambique;
            case "na": //namibia
                return R.drawable.flag_namibia;
            case "nr": //nauru
                return R.drawable.flag_nauru;
            case "np": //nepal
                return R.drawable.flag_nepal;
            case "nl": //netherlands
                return R.drawable.flag_netherlands;
            case "nc": //new caledonia
                return R.drawable.flag_new_caledonia;// custom
            case "nz": //new zealand
                return R.drawable.flag_new_zealand;
            case "ni": //nicaragua
                return R.drawable.flag_nicaragua;
            case "ne": //niger
                return R.drawable.flag_niger;
            case "ng": //nigeria
                return R.drawable.flag_nigeria;
            case "nu": //niue
                return R.drawable.flag_niue;
            case "kp": //north korea
                return R.drawable.flag_north_korea;
            case "no": //norway
                return R.drawable.flag_norway;
            case "om": //oman
                return R.drawable.flag_oman;
            case "pk": //pakistan
                return R.drawable.flag_pakistan;
            case "pw": //palau
                return R.drawable.flag_palau;
            case "pa": //panama
                return R.drawable.flag_panama;
            case "pg": //papua new guinea
                return R.drawable.flag_papua_new_guinea;
            case "py": //paraguay
                return R.drawable.flag_paraguay;
            case "pe": //peru
                return R.drawable.flag_peru;
            case "ph": //philippines
                return R.drawable.flag_philippines;
            case "pn": //pitcairn
                return R.drawable.flag_pitcairn_islands;
            case "pl": //poland
                return R.drawable.flag_poland;
            case "pt": //portugal
                return R.drawable.flag_portugal;
            case "pr": //puerto rico
                return R.drawable.flag_puerto_rico;
            case "qa": //qatar
                return R.drawable.flag_qatar;
            case "ro": //romania
                return R.drawable.flag_romania;
            case "ru": //russian federation
                return R.drawable.flag_russian_federation;
            case "rw": //rwanda
                return R.drawable.flag_rwanda;
            case "bl": //saint barthélemy
                return R.drawable.flag_saint_barthelemy;// custom
            case "ws": //samoa
                return R.drawable.flag_samoa;
            case "sm": //san marino
                return R.drawable.flag_san_marino;
            case "st": //sao tome and principe
                return R.drawable.flag_sao_tome_and_principe;
            case "sa": //saudi arabia
                return R.drawable.flag_saudi_arabia;
            case "sn": //senegal
                return R.drawable.flag_senegal;
            case "rs": //serbia
                return R.drawable.flag_serbia; // custom
            case "sc": //seychelles
                return R.drawable.flag_seychelles;
            case "sl": //sierra leone
                return R.drawable.flag_sierra_leone;
            case "sg": //singapore
                return R.drawable.flag_singapore;
            case "sk": //slovakia
                return R.drawable.flag_slovakia;
            case "si": //slovenia
                return R.drawable.flag_slovenia;
            case "sb": //solomon islands
                return R.drawable.flag_soloman_islands;
            case "so": //somalia
                return R.drawable.flag_somalia;
            case "za": //south africa
                return R.drawable.flag_south_africa;
            case "kr": //south korea
                return R.drawable.flag_south_korea;
            case "es": //spain
                return R.drawable.flag_spain;
            case "lk": //sri lanka
                return R.drawable.flag_sri_lanka;
            case "sh": //saint helena, ascension and tristan da cunha
                return R.drawable.flag_saint_helena; // custom
            case "pm": //saint pierre and miquelon
                return R.drawable.flag_saint_pierre;
            case "sd": //sudan
                return R.drawable.flag_sudan;
            case "sr": //suriname
                return R.drawable.flag_suriname;
            case "sz": //swaziland
                return R.drawable.flag_swaziland;
            case "se": //sweden
                return R.drawable.flag_sweden;
            case "ch": //switzerland
                return R.drawable.flag_switzerland;
            case "sy": //syrian arab republic
                return R.drawable.flag_syria;
            case "tw": //taiwan, province of china
                return R.drawable.flag_taiwan;
            case "tj": //tajikistan
                return R.drawable.flag_tajikistan;
            case "tz": //tanzania, united republic of
                return R.drawable.flag_tanzania;
            case "th": //thailand
                return R.drawable.flag_thailand;
            case "tg": //togo
                return R.drawable.flag_togo;
            case "tk": //tokelau
                return R.drawable.flag_tokelau; // custom
            case "to": //tonga
                return R.drawable.flag_tonga;
            case "tn": //tunisia
                return R.drawable.flag_tunisia;
            case "tr": //turkey
                return R.drawable.flag_turkey;
            case "tm": //turkmenistan
                return R.drawable.flag_turkmenistan;
            case "tv": //tuvalu
                return R.drawable.flag_tuvalu;
            case "ae": //united arab emirates
                return R.drawable.flag_uae;
            case "ug": //uganda
                return R.drawable.flag_uganda;
            case "gb": //united kingdom
                return R.drawable.flag_united_kingdom;
            case "ua": //ukraine
                return R.drawable.flag_ukraine;
            case "uy": //uruguay
                return R.drawable.flag_uruguay;
            case "us": //united states
                return R.drawable.flag_united_states_of_america;
            case "uz": //uzbekistan
                return R.drawable.flag_uzbekistan;
            case "vu": //vanuatu
                return R.drawable.flag_vanuatu;
            case "va": //holy see (vatican city state)
                return R.drawable.flag_vatican_city;
            case "ve": //venezuela, bolivarian republic of
                return R.drawable.flag_venezuela;
            case "vn": //vietnam
                return R.drawable.flag_vietnam;
            case "wf": //wallis and futuna
                return R.drawable.flag_wallis_and_futuna;
            case "ye": //yemen
                return R.drawable.flag_yemen;
            case "zm": //zambia
                return R.drawable.flag_zambia;
            case "zw": //zimbabwe
                return R.drawable.flag_zimbabwe;

            // Caribbean Islands
            case "ai": //anguilla
                return R.drawable.flag_anguilla;
            case "ag": //antigua & barbuda
                return R.drawable.flag_antigua_and_barbuda;
            case "bs": //bahamas
                return R.drawable.flag_bahamas;
            case "bb": //barbados
                return R.drawable.flag_barbados;
            case "bm": //bermuda
                return R.drawable.flag_bermuda;
            case "vg": //british virgin islands
                return R.drawable.flag_british_virgin_islands;
            case "dm": //dominica
                return R.drawable.flag_dominica;
            case "do": //dominican republic
                return R.drawable.flag_dominican_republic;
            case "gd": //grenada
                return R.drawable.flag_grenada;
            case "jm": //jamaica
                return R.drawable.flag_jamaica;
            case "ms": //montserrat
                return R.drawable.flag_montserrat;
            case "kn": //st kitts & nevis
                return R.drawable.flag_saint_kitts_and_nevis;
            case "lc": //st lucia
                return R.drawable.flag_saint_lucia;
            case "vc": //st vincent & the grenadines
                return R.drawable.flag_saint_vicent_and_the_grenadines;
            case "tt": //trinidad & tobago
                return R.drawable.flag_trinidad_and_tobago;
            case "tc": //turks & caicos islands
                return R.drawable.flag_turks_and_caicos_islands;
            case "vi": //us virgin islands
                return R.drawable.flag_us_virgin_islands;
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
    public static List<Country> getLibraryMasterCountryList(CountryCodePicker.Language language) {
        switch (language) {
            case ARABIC:
                return getLibraryMasterCountriesArabic();
            case BENGALI:
                return getLibraryMasterCountriesBengali();
            case CHINESE:
                return getLibraryMasterCountriesChinese();
            case ENGLISH:
                return getLibraryMasterCountriesEnglish();
            case FRENCH:
                return getLibraryMasterCountriesFrench();
            case GERMAN:
                return getLibraryMasterCountriesGerman();
            case GUJARATI:
                return getLibraryMasterCountriesGujarati();
            case HINDI:
                return getLibraryMasterCountriesHindi();
            case JAPANESE:
                return getLibraryMasterCountriesJapanese();
            case JAVANESE:
                return getLibraryMasterCountriesJavanese();
            case PORTUGUESE:
                return getLibraryMasterCountriesPortuguese();
            case RUSSIAN:
                return getLibraryMasterCountriesRussian();
            case SPANISH:
                return getLibraryMasterCountriesSpanish();
            default:
                return getLibraryMasterCountriesEnglish();
        }
    }

    public static List<Country> getLibraryMasterCountriesEnglish() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "Afghanistan"));
        countries.add(new Country("al", "355", "Albania"));
        countries.add(new Country("dz", "213", "Algeria"));
        countries.add(new Country("ad", "376", "Andorra"));
        countries.add(new Country("ao", "244", "Angola"));
        countries.add(new Country("ai", "1264", "Anguilla"));
        countries.add(new Country("aq", "672", "Antarctica"));
        countries.add(new Country("ag", "1268", "Antigua and Barbuda"));
        countries.add(new Country("ar", "54", "Argentina"));
        countries.add(new Country("am", "374", "Armenia"));
        countries.add(new Country("aw", "297", "Aruba"));
        countries.add(new Country("au", "61", "Australia"));
        countries.add(new Country("at", "43", "Austria"));
        countries.add(new Country("az", "994", "Azerbaijan"));
        countries.add(new Country("bs", "1242", "Bahamas"));
        countries.add(new Country("bh", "973", "Bahrain"));
        countries.add(new Country("bd", "880", "Bangladesh"));
        countries.add(new Country("bb", "1246", "Barbados"));
        countries.add(new Country("by", "375", "Belarus"));
        countries.add(new Country("be", "32", "Belgium"));
        countries.add(new Country("bz", "501", "Belize"));
        countries.add(new Country("bj", "229", "Benin"));
        countries.add(new Country("bm", "1441", "Bermuda"));
        countries.add(new Country("bt", "975", "Bhutan"));
        countries.add(new Country("bo", "591", "Bolivia, Plurinational State Of"));
        countries.add(new Country("ba", "387", "Bosnia And Herzegovina"));
        countries.add(new Country("bw", "267", "Botswana"));
        countries.add(new Country("br", "55", "Brazil"));
        countries.add(new Country("vg", "1284", "British Virgin Islands"));
        countries.add(new Country("bn", "673", "Brunei Darussalam"));
        countries.add(new Country("bg", "359", "Bulgaria"));
        countries.add(new Country("bf", "226", "Burkina Faso"));
        countries.add(new Country("mm", "95", "Myanmar"));
        countries.add(new Country("bi", "257", "Burundi"));
        countries.add(new Country("kh", "855", "Cambodia"));
        countries.add(new Country("cm", "237", "Cameroon"));
        countries.add(new Country("ca", "1", "Canada"));
        countries.add(new Country("cv", "238", "Cape Verde"));
        countries.add(new Country("ky", "1345", "Cayman Islands"));
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
        countries.add(new Country("dm", "1767", "Dominica"));
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
        countries.add(new Country("pf", "689", "French Polynesia"));
        countries.add(new Country("ga", "241", "Gabon"));
        countries.add(new Country("gm", "220", "Gambia"));
        countries.add(new Country("ge", "995", "Georgia"));
        countries.add(new Country("de", "49", "Germany"));
        countries.add(new Country("gh", "233", "Ghana"));
        countries.add(new Country("gi", "350", "Gibraltar"));
        countries.add(new Country("gr", "30", "Greece"));
        countries.add(new Country("gl", "299", "Greenland"));
        countries.add(new Country("gd", "1473", "Grenada"));
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
        countries.add(new Country("it", "39", "Italy"));
        countries.add(new Country("ci", "225", "Côte D\'ivoire"));
        countries.add(new Country("jm", "1876", "Jamaica"));
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
        countries.add(new Country("mr", "222", "Mauritania"));
        countries.add(new Country("mu", "230", "Mauritius"));
        countries.add(new Country("yt", "262", "Mayotte"));
        countries.add(new Country("mx", "52", "Mexico"));
        countries.add(new Country("fm", "691", "Micronesia, Federated States Of"));
        countries.add(new Country("md", "373", "Moldova, Republic Of"));
        countries.add(new Country("mc", "377", "Monaco"));
        countries.add(new Country("mn", "976", "Mongolia"));
        countries.add(new Country("ms", "1664", "Montserrat"));
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
        countries.add(new Country("ro", "40", "Romania"));
        countries.add(new Country("ru", "7", "Russian Federation"));
        countries.add(new Country("rw", "250", "Rwanda"));
        countries.add(new Country("bl", "590", "Saint Barthélemy"));
        countries.add(new Country("kn", "1869", "Saint Kitts and Nevis"));
        countries.add(new Country("lc", "1758", "Saint Lucia"));
        countries.add(new Country("vc", "1784", "Saint Vincent & The Grenadines"));
        countries.add(new Country("ws", "685", "Samoa"));
        countries.add(new Country("sm", "378", "San Marino"));
        countries.add(new Country("st", "239", "Sao Tome And Principe"));
        countries.add(new Country("sa", "966", "Saudi Arabia"));
        countries.add(new Country("sn", "221", "Senegal"));
        countries.add(new Country("rs", "381", "Serbia"));
        countries.add(new Country("sc", "248", "Seychelles"));
        countries.add(new Country("sl", "232", "Sierra Leone"));
        countries.add(new Country("sg", "65", "Singapore"));
        countries.add(new Country("sxm", "1721", "Sint Maarten"));
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
        countries.add(new Country("tt", "1868", "Trinidad & Tobago"));
        countries.add(new Country("tn", "216", "Tunisia"));
        countries.add(new Country("tr", "90", "Turkey"));
        countries.add(new Country("tm", "993", "Turkmenistan"));
        countries.add(new Country("tc", "1649", "Turks and Caicos Islands"));
        countries.add(new Country("tv", "688", "Tuvalu"));
        countries.add(new Country("ae", "971", "United Arab Emirates"));
        countries.add(new Country("ug", "256", "Uganda"));
        countries.add(new Country("gb", "44", "United Kingdom"));
        countries.add(new Country("ua", "380", "Ukraine"));
        countries.add(new Country("uy", "598", "Uruguay"));
        countries.add(new Country("us", "1", "United States"));
        countries.add(new Country("vi", "1340", "US Virgin Islands"));
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

    public static List<Country> getLibraryMasterCountriesHindi() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "अफ़ग़ानिस्तान"));
        countries.add(new Country("al", "355", "अल्बानिया"));
        countries.add(new Country("dz", "213", "एलजीरिया"));
        countries.add(new Country("ad", "376", "अंडोरा"));
        countries.add(new Country("ao", "244", "अंगोला"));
        countries.add(new Country("aq", "672", "अंटार्कटिका"));
        countries.add(new Country("ar", "54", "अर्जेंटीना"));
        countries.add(new Country("am", "374", "आर्मीनिया"));
        countries.add(new Country("aw", "297", "अरूबा"));
        countries.add(new Country("au", "61", "ऑस्ट्रेलिया"));
        countries.add(new Country("at", "43", "ऑस्ट्रिया"));
        countries.add(new Country("az", "994", "आज़रबाइजान"));
        countries.add(new Country("bh", "973", "बहरीन"));
        countries.add(new Country("bd", "880", "बांग्लादेश"));
        countries.add(new Country("by", "375", "बेलोरूस"));
        countries.add(new Country("be", "32", "बेल्जियम"));
        countries.add(new Country("bz", "501", "बेलीज"));
        countries.add(new Country("bj", "229", "बेनिन"));
        countries.add(new Country("bt", "975", "भूटान"));
        countries.add(new Country("bo", "591", "बोलीविया, प्लूरिनेशनल स्टेट ऑफ़"));
        countries.add(new Country("ba", "387", "बोस्निया और हर्जेगोविना"));
        countries.add(new Country("bw", "267", "बोत्सवाना"));
        countries.add(new Country("br", "55", "ब्राज़िल"));
        countries.add(new Country("bn", "673", "ब्रुनेई दारुस्सलाम"));
        countries.add(new Country("bg", "359", "बुल्गारिया"));
        countries.add(new Country("bf", "226", "बुर्किना फासो"));
        countries.add(new Country("mm", "95", "म्यांमार"));
        countries.add(new Country("bi", "257", "बुस्र्न्दी"));
        countries.add(new Country("kh", "855", "कंबोडिया"));
        countries.add(new Country("cm", "237", "कैमरून"));
        countries.add(new Country("ca", "1", "कनाडा"));
        countries.add(new Country("cv", "238", "केप वर्दे"));
        countries.add(new Country("cf", "236", "केंद्रीय अफ्रीकन गणराज्य"));
        countries.add(new Country("td", "235", "काग़ज़ का टुकड़ा"));
        countries.add(new Country("cl", "56", "चिली"));
        countries.add(new Country("cn", "86", "चीन"));
        countries.add(new Country("cx", "61", "क्रिसमस द्वीप"));
        countries.add(new Country("cc", "61", "कोकोस (कीलिंग) द्वीप"));
        countries.add(new Country("co", "57", "कोलम्बिया"));
        countries.add(new Country("km", "269", "कोमोरोस"));
        countries.add(new Country("cg", "242", "कांगो"));
        countries.add(new Country("cd", "243", "कांगो लोकतांत्रिक गणराज्य"));
        countries.add(new Country("ck", "682", "कुक द्वीपसमूह"));
        countries.add(new Country("cr", "506", "कोस्टा रिका"));
        countries.add(new Country("hr", "385", "क्रोएशिया"));
        countries.add(new Country("cu", "53", "क्यूबा"));
        countries.add(new Country("cy", "357", "साइप्रस"));
        countries.add(new Country("cz", "420", "चेक गणतंत्र"));
        countries.add(new Country("dk", "45", "डेनमार्क"));
        countries.add(new Country("dj", "253", "जिबूती"));
        countries.add(new Country("tl", "670", "तिमोर-लेस्ते"));
        countries.add(new Country("ec", "593", "इक्वेडोर"));
        countries.add(new Country("eg", "20", "मिस्र"));
        countries.add(new Country("sv", "503", "एल साल्वाडोर"));
        countries.add(new Country("gq", "240", "भूमध्यवर्ती गिनी"));
        countries.add(new Country("er", "291", "इरिट्रिया"));
        countries.add(new Country("ee", "372", "एस्तोनिया"));
        countries.add(new Country("et", "251", "इथियोपिया"));
        countries.add(new Country("fk", "500", "फ़ॉकलैंड द्वीप (माल्विनास)"));
        countries.add(new Country("fo", "298", "फ़ैरो द्वीप"));
        countries.add(new Country("fj", "679", "फ़िजी"));
        countries.add(new Country("fi", "358", "फिनलैंड"));
        countries.add(new Country("fr", "33", "फ्रांस"));
        countries.add(new Country("pf", "689", "फ्रेंच पॉलीनेशिया"));
        countries.add(new Country("ga", "241", "गैबॉन"));
        countries.add(new Country("gm", "220", "गाम्बिया"));
        countries.add(new Country("ge", "995", "जॉर्जिया"));
        countries.add(new Country("de", "49", "जर्मनी"));
        countries.add(new Country("gh", "233", "घाना"));
        countries.add(new Country("gi", "350", "जिब्राल्टर"));
        countries.add(new Country("gr", "30", "यूनान"));
        countries.add(new Country("gl", "299", "ग्रीनलैंड"));
        countries.add(new Country("gt", "502", "ग्वाटेमाला"));
        countries.add(new Country("gn", "224", "गिन्नी"));
        countries.add(new Country("gw", "245", "गिनी-बिसाऊ"));
        countries.add(new Country("gy", "592", "गुयाना"));
        countries.add(new Country("ht", "509", "हैती"));
        countries.add(new Country("hn", "504", "होंडुरस"));
        countries.add(new Country("hk", "852", "हॉगकॉग"));
        countries.add(new Country("hu", "36", "हंगरी"));
        countries.add(new Country("in", "91", "भारत"));
        countries.add(new Country("id", "62", "इंडोनेशिया"));
        countries.add(new Country("ir", "98", "ईरान (इस्लामिक रिपब्लिक ऑफ)"));
        countries.add(new Country("iq", "964", "इराक"));
        countries.add(new Country("ie", "353", "आयरलैंड"));
        countries.add(new Country("im", "44", "मैन द्वीप"));
        countries.add(new Country("il", "972", "इजराइल"));
        countries.add(new Country("it", "39", "इटली"));
        countries.add(new Country("ci", "225", "कोटे डी एंड apos; आइवर"));
        countries.add(new Country("jp", "81", "जापान"));
        countries.add(new Country("jo", "962", "जॉर्डन"));
        countries.add(new Country("kz", "7", "कजाखस्तान"));
        countries.add(new Country("ke", "254", "केन्या"));
        countries.add(new Country("ki", "686", "किरिबाती"));
        countries.add(new Country("kw", "965", "कुवैट"));
        countries.add(new Country("kg", "996", "किर्गिज़स्तान"));
        countries.add(new Country("la", "856", "लाओ पीपुल्स & apos की लोकतांत्रिक गणराज्य"));
        countries.add(new Country("lv", "371", "लातविया"));
        countries.add(new Country("lb", "961", "लेबनान"));
        countries.add(new Country("ls", "266", "लिसोटो"));
        countries.add(new Country("lr", "231", "लाइबेरिया"));
        countries.add(new Country("ly", "218", "लीबिया"));
        countries.add(new Country("li", "423", "लिकटेंस्टीन"));
        countries.add(new Country("lt", "370", "लिथुआनिया"));
        countries.add(new Country("lu", "352", "लक्जमबर्ग"));
        countries.add(new Country("mo", "853", "मकाऊ"));
        countries.add(new Country("mk", "389", "मैसिडोनिया, पूर्व युगोस्लाव गणराज्य"));
        countries.add(new Country("mg", "261", "मेडागास्कर"));
        countries.add(new Country("mw", "265", "मलावी"));
        countries.add(new Country("my", "60", "मलेशिया"));
        countries.add(new Country("mv", "960", "मालदीव"));
        countries.add(new Country("ml", "223", "माली"));
        countries.add(new Country("mt", "356", "माल्टा"));
        countries.add(new Country("mh", "692", "मार्शल द्वीप समूह"));
        countries.add(new Country("mr", "222", "मॉरिटानिया"));
        countries.add(new Country("mu", "230", "मॉरीशस"));
        countries.add(new Country("yt", "262", "मैयट"));
        countries.add(new Country("mx", "52", "मेक्सिको"));
        countries.add(new Country("fm", "691", "माइक्रोनेशिया Federated के राज्य अमेरिका"));
        countries.add(new Country("md", "373", "मोल्दोवा, गणराज्य के"));
        countries.add(new Country("mc", "377", "मोनाको"));
        countries.add(new Country("mn", "976", "मंगोलिया"));
        countries.add(new Country("me", "382", "मोंटेनेग्रो"));
        countries.add(new Country("ma", "212", "मोरक्को"));
        countries.add(new Country("mz", "258", "मोजाम्बिक"));
        countries.add(new Country("na", "264", "नामीबिया"));
        countries.add(new Country("nr", "674", "नाउरू"));
        countries.add(new Country("np", "977", "नेपाल"));
        countries.add(new Country("nl", "31", "नीदरलैंड्स"));
        countries.add(new Country("nc", "687", "न्यू कैलेडोनिया"));
        countries.add(new Country("nz", "64", "न्यूजीलैंड"));
        countries.add(new Country("ni", "505", "निकारागुआ"));
        countries.add(new Country("ne", "227", "नाइजर"));
        countries.add(new Country("ng", "234", "नाइजीरिया में"));
        countries.add(new Country("nu", "683", "नियू"));
        countries.add(new Country("kp", "850", "कोरिया"));
        countries.add(new Country("no", "47", "नॉर्वे"));
        countries.add(new Country("om", "968", "ओमान"));
        countries.add(new Country("pk", "92", "पाकिस्तान"));
        countries.add(new Country("pw", "680", "पलाऊ"));
        countries.add(new Country("pa", "507", "पनामा"));
        countries.add(new Country("pg", "675", "पापुआ न्यू गिनी"));
        countries.add(new Country("py", "595", "परागुआ"));
        countries.add(new Country("pe", "51", "पेरू"));
        countries.add(new Country("ph", "63", "फिलीपींस"));
        countries.add(new Country("pn", "870", "पिटकेर्न"));
        countries.add(new Country("pl", "48", "पोलैंड"));
        countries.add(new Country("pt", "351", "पुर्तगाल"));
        countries.add(new Country("pr", "1", "प्यूर्टो रिको"));
        countries.add(new Country("qa", "974", "कतर"));
        countries.add(new Country("ro", "40", "रोमानिया"));
        countries.add(new Country("ru", "7", "रूसी संघ"));
        countries.add(new Country("rw", "250", "रवांडा"));
        countries.add(new Country("bl", "590", "सेंट Barthélemy"));
        countries.add(new Country("ws", "685", "समोआ"));
        countries.add(new Country("sm", "378", "सैन मैरीनो"));
        countries.add(new Country("st", "239", "साओ टोमे और प्रिंसिपे"));
        countries.add(new Country("sa", "966", "सऊदी अरब"));
        countries.add(new Country("sn", "221", "सेनेगल"));
        countries.add(new Country("rs", "381", "सर्बिया"));
        countries.add(new Country("sc", "248", "सेशेल्स"));
        countries.add(new Country("sl", "232", "सियरा लिओन"));
        countries.add(new Country("sg", "65", "सिंगापुर"));
        countries.add(new Country("sk", "421", "स्लोवाकिया"));
        countries.add(new Country("si", "386", "स्लोवेनिया"));
        countries.add(new Country("sb", "677", "सोलोमन द्वीप"));
        countries.add(new Country("so", "252", "सोमालिया"));
        countries.add(new Country("za", "27", "दक्षिण अफ्रीका"));
        countries.add(new Country("kr", "82", "कोरिया गणतंत्र"));
        countries.add(new Country("es", "34", "स्पेन"));
        countries.add(new Country("lk", "94", "श्री लंका"));
        countries.add(new Country("sh", "290", "सेंट हेलेना, उदगम और ट्रिस्टन दा कुन्हा"));
        countries.add(new Country("pm", "508", "सेंट पियरे और मिकेलॉन"));
        countries.add(new Country("sd", "249", "सूडान"));
        countries.add(new Country("sr", "597", "सूरीनाम"));
        countries.add(new Country("sz", "268", "स्वाजीलैंड"));
        countries.add(new Country("se", "46", "स्वीडन"));
        countries.add(new Country("ch", "41", "स्विट्जरलैंड"));
        countries.add(new Country("sy", "963", "सीरिया अरब गणतंत्र"));
        countries.add(new Country("tw", "886", "ताइवान, चीन के प्रांत"));
        countries.add(new Country("tj", "992", "तजाकिस्तान"));
        countries.add(new Country("tz", "255", "तंजानिया, संयुक्त गणराज्य"));
        countries.add(new Country("th", "66", "थाईलैंड"));
        countries.add(new Country("tg", "228", "जाना"));
        countries.add(new Country("tk", "690", "टोकेलाऊ"));
        countries.add(new Country("to", "676", "टोंगा"));
        countries.add(new Country("tn", "216", "ट्यूनीशिया"));
        countries.add(new Country("tr", "90", "तुर्की"));
        countries.add(new Country("tm", "993", "तुर्कमेनिस्तान"));
        countries.add(new Country("tv", "688", "तुवालु"));
        countries.add(new Country("ae", "971", "संयुक्त अरब अमीरात"));
        countries.add(new Country("ug", "256", "युगांडा"));
        countries.add(new Country("gb", "44", "यूनाइटेड किंगडम"));
        countries.add(new Country("ua", "380", "यूक्रेन"));
        countries.add(new Country("uy", "598", "उरुग्वे"));
        countries.add(new Country("us", "1", "संयुक्त राज्य अमेरिका"));
        countries.add(new Country("uz", "998", "उज़्बेकिस्तान"));
        countries.add(new Country("vu", "678", "वानुअतु"));
        countries.add(new Country("va", "39", "पवित्र देखो (वैटिकन सिटी राज्य)"));
        countries.add(new Country("ve", "58", "वेनेजुएला, के Bolivarian गणराज्य"));
        countries.add(new Country("vn", "84", "वियतनाम"));
        countries.add(new Country("wf", "681", "वाली और फ़्युटुना"));
        countries.add(new Country("ye", "967", "यमन"));
        countries.add(new Country("zm", "260", "जाम्बिया"));
        countries.add(new Country("zw", "263", "जिम्बाब्वे"));
        return countries;
    }

    public static List<Country> getLibraryMasterCountriesFrench() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "Afghanistan"));
        countries.add(new Country("al", "355", "Albanie"));
        countries.add(new Country("dz", "213", "Algérie"));
        countries.add(new Country("ad", "376", "Andorre"));
        countries.add(new Country("ao", "244", "Angola"));
        countries.add(new Country("aq", "672", "Antarctique"));
        countries.add(new Country("ar", "54", "Argentine"));
        countries.add(new Country("am", "374", "Arménie"));
        countries.add(new Country("aw", "297", "Aruba"));
        countries.add(new Country("au", "61", "Australie"));
        countries.add(new Country("at", "43", "Autriche"));
        countries.add(new Country("az", "994", "Azerbaïdjan"));
        countries.add(new Country("bh", "973", "Bahreïn"));
        countries.add(new Country("bd", "880", "Bangladesh"));
        countries.add(new Country("by", "375", "Belarus"));
        countries.add(new Country("be", "32", "Belgique"));
        countries.add(new Country("bz", "501", "Belize"));
        countries.add(new Country("bj", "229", "Bénin"));
        countries.add(new Country("bt", "975", "Bhoutan"));
        countries.add(new Country("bo", "591", "Bolivie, Etat plurinational de"));
        countries.add(new Country("ba", "387", "Bosnie Herzégovine"));
        countries.add(new Country("bw", "267", "Botswana"));
        countries.add(new Country("br", "55", "Brésil"));
        countries.add(new Country("bn", "673", "Brunei Darussalam"));
        countries.add(new Country("bg", "359", "Bulgarie"));
        countries.add(new Country("bf", "226", "Burkina Faso"));
        countries.add(new Country("mm", "95", "Myanmar"));
        countries.add(new Country("bi", "257", "Burundi"));
        countries.add(new Country("kh", "855", "Cambodge"));
        countries.add(new Country("cm", "237", "Cameroun"));
        countries.add(new Country("ca", "1", "Canada"));
        countries.add(new Country("cv", "238", "Cap-Vert"));
        countries.add(new Country("cf", "236", "République centrafricaine"));
        countries.add(new Country("td", "235", "Tchad"));
        countries.add(new Country("cl", "56", "Chili"));
        countries.add(new Country("cn", "86", "Chine"));
        countries.add(new Country("cx", "61", "L'île de noël"));
        countries.add(new Country("cc", "61", "Cocos (Keeling)"));
        countries.add(new Country("co", "57", "Colombie"));
        countries.add(new Country("km", "269", "Comores"));
        countries.add(new Country("cg", "242", "Congo"));
        countries.add(new Country("cd", "243", "Congo, La République Démocratique Du"));
        countries.add(new Country("ck", "682", "les Îles Cook"));
        countries.add(new Country("cr", "506", "Costa Rica"));
        countries.add(new Country("hr", "385", "Croatie"));
        countries.add(new Country("cu", "53", "Cuba"));
        countries.add(new Country("cy", "357", "Chypre"));
        countries.add(new Country("cz", "420", "République Tchèque"));
        countries.add(new Country("dk", "45", "Danemark"));
        countries.add(new Country("dj", "253", "Djibouti"));
        countries.add(new Country("tl", "670", "Timor-leste"));
        countries.add(new Country("ec", "593", "Equateur"));
        countries.add(new Country("eg", "20", "Egypte"));
        countries.add(new Country("sv", "503", "Le Salvador"));
        countries.add(new Country("gq", "240", "Guinée Équatoriale"));
        countries.add(new Country("er", "291", "Érythrée"));
        countries.add(new Country("ee", "372", "Estonie"));
        countries.add(new Country("et", "251", "Ethiopie"));
        countries.add(new Country("fk", "500", "Îles Falkland (liste Malvinas)"));
        countries.add(new Country("fo", "298", "Îles Féroé"));
        countries.add(new Country("fj", "679", "Fidji"));
        countries.add(new Country("fi", "358", "Finlande"));
        countries.add(new Country("fr", "33", "France"));
        countries.add(new Country("pf", "689", "Polynésie française"));
        countries.add(new Country("ga", "241", "Gabon"));
        countries.add(new Country("gm", "220", "Gambie"));
        countries.add(new Country("ge", "995", "Géorgie"));
        countries.add(new Country("de", "49", "Allemagne"));
        countries.add(new Country("gh", "233", "Ghana"));
        countries.add(new Country("gi", "350", "Gibraltar"));
        countries.add(new Country("gr", "30", "Grèce"));
        countries.add(new Country("gl", "299", "Groenland"));
        countries.add(new Country("gt", "502", "Guatemala"));
        countries.add(new Country("gn", "224", "Guinée"));
        countries.add(new Country("gw", "245", "Guinée-Bissau"));
        countries.add(new Country("gy", "592", "Guyane"));
        countries.add(new Country("ht", "509", "Haïti"));
        countries.add(new Country("hn", "504", "Honduras"));
        countries.add(new Country("hk", "852", "Hong Kong"));
        countries.add(new Country("hu", "36", "Hongrie"));
        countries.add(new Country("in", "91", "Inde"));
        countries.add(new Country("id", "62", "Indonésie"));
        countries.add(new Country("ir", "98", "Iran (République islamique d"));
        countries.add(new Country("iq", "964", "Irak"));
        countries.add(new Country("ie", "353", "Irlande"));
        countries.add(new Country("im", "44", "Isle Of Man"));
        countries.add(new Country("il", "972", "Israël"));
        countries.add(new Country("it", "39", "Italie"));
        countries.add(new Country("ci", "225", "Côte D & apos; ivoire"));
        countries.add(new Country("jp", "81", "Japon"));
        countries.add(new Country("jo", "962", "Jordanie"));
        countries.add(new Country("kz", "7", "Kazakhstan"));
        countries.add(new Country("ke", "254", "Kenya"));
        countries.add(new Country("ki", "686", "Kiribati"));
        countries.add(new Country("kw", "965", "Koweit"));
        countries.add(new Country("kg", "996", "Kirghizistan"));
        countries.add(new Country("la", "856", "République démocratique; Lao People & apos"));
        countries.add(new Country("lv", "371", "Lettonie"));
        countries.add(new Country("lb", "961", "Liban"));
        countries.add(new Country("ls", "266", "Lesotho"));
        countries.add(new Country("lr", "231", "Libéria"));
        countries.add(new Country("ly", "218", "Libye"));
        countries.add(new Country("li", "423", "Liechtenstein"));
        countries.add(new Country("lt", "370", "Lituanie"));
        countries.add(new Country("lu", "352", "Luxembourg"));
        countries.add(new Country("mo", "853", "Macao"));
        countries.add(new Country("mk", "389", "Macédoine, Ex-République yougoslave de"));
        countries.add(new Country("mg", "261", "Madagascar"));
        countries.add(new Country("mw", "265", "Malawi"));
        countries.add(new Country("my", "60", "Malaisie"));
        countries.add(new Country("mv", "960", "Maldives"));
        countries.add(new Country("ml", "223", "Mali"));
        countries.add(new Country("mt", "356", "Malte"));
        countries.add(new Country("mh", "692", "Iles Marshall"));
        countries.add(new Country("mr", "222", "Mauritanie"));
        countries.add(new Country("mu", "230", "Ile Maurice"));
        countries.add(new Country("yt", "262", "Mayotte"));
        countries.add(new Country("mx", "52", "Mexique"));
        countries.add(new Country("fm", "691", "Micronésie, États fédérés de"));
        countries.add(new Country("md", "373", "Moldova, République de"));
        countries.add(new Country("mc", "377", "Monaco"));
        countries.add(new Country("mn", "976", "Mongolie"));
        countries.add(new Country("me", "382", "Monténégro"));
        countries.add(new Country("ma", "212", "Maroc"));
        countries.add(new Country("mz", "258", "Mozambique"));
        countries.add(new Country("na", "264", "Namibie"));
        countries.add(new Country("nr", "674", "Nauru"));
        countries.add(new Country("np", "977", "Népal"));
        countries.add(new Country("nl", "31", "Pays-Bas"));
        countries.add(new Country("nc", "687", "Nouvelle Calédonie"));
        countries.add(new Country("nz", "64", "nouvelle Zélande"));
        countries.add(new Country("ni", "505", "Nicaragua"));
        countries.add(new Country("ne", "227", "Niger"));
        countries.add(new Country("ng", "234", "Nigeria"));
        countries.add(new Country("nu", "683", "Niue"));
        countries.add(new Country("kp", "850", "Corée"));
        countries.add(new Country("no", "47", "Norvège"));
        countries.add(new Country("om", "968", "Oman"));
        countries.add(new Country("pk", "92", "Pakistan"));
        countries.add(new Country("pw", "680", "Palau"));
        countries.add(new Country("pa", "507", "Panama"));
        countries.add(new Country("pg", "675", "Papouasie Nouvelle Guinée"));
        countries.add(new Country("py", "595", "Paraguay"));
        countries.add(new Country("pe", "51", "Pérou"));
        countries.add(new Country("ph", "63", "Philippines"));
        countries.add(new Country("pn", "870", "Pitcairn"));
        countries.add(new Country("pl", "48", "Pologne"));
        countries.add(new Country("pt", "351", "le Portugal"));
        countries.add(new Country("pr", "1", "Porto Rico"));
        countries.add(new Country("qa", "974", "Qatar"));
        countries.add(new Country("ro", "40", "Roumanie"));
        countries.add(new Country("ru", "7", "Fédération Russe"));
        countries.add(new Country("rw", "250", "Rwanda"));
        countries.add(new Country("bl", "590", "Saint Barthélemy"));
        countries.add(new Country("ws", "685", "Samoa"));
        countries.add(new Country("sm", "378", "Saint Marin"));
        countries.add(new Country("st", "239", "Sao Tomé-et-Principe"));
        countries.add(new Country("sa", "966", "Arabie Saoudite"));
        countries.add(new Country("sn", "221", "Sénégal"));
        countries.add(new Country("rs", "381", "Serbie"));
        countries.add(new Country("sc", "248", "les Seychelles"));
        countries.add(new Country("sl", "232", "Sierra Leone"));
        countries.add(new Country("sg", "65", "Singapour"));
        countries.add(new Country("sk", "421", "Slovaquie"));
        countries.add(new Country("si", "386", "Slovénie"));
        countries.add(new Country("sb", "677", "Les îles Salomon"));
        countries.add(new Country("so", "252", "Somalie"));
        countries.add(new Country("za", "27", "Afrique du Sud"));
        countries.add(new Country("kr", "82", "Corée, République de"));
        countries.add(new Country("es", "34", "l'Espagne"));
        countries.add(new Country("lk", "94", "Sri Lanka"));
        countries.add(new Country("sh", "290", "Sainte-Hélène, Ascension et Tristan da Cunha"));
        countries.add(new Country("pm", "508", "Saint-Pierre-et-Miquelon"));
        countries.add(new Country("sd", "249", "Soudan"));
        countries.add(new Country("sr", "597", "Suriname"));
        countries.add(new Country("sz", "268", "Swaziland"));
        countries.add(new Country("se", "46", "Suède"));
        countries.add(new Country("ch", "41", "Suisse"));
        countries.add(new Country("sy", "963", "République arabe syrienne"));
        countries.add(new Country("tw", "886", "Taiwan, Province de Chine"));
        countries.add(new Country("tj", "992", "Tadjikistan"));
        countries.add(new Country("tz", "255", "Tanzanie, République-Unie de"));
        countries.add(new Country("th", "66", "Thaïlande"));
        countries.add(new Country("tg", "228", "Aller"));
        countries.add(new Country("tk", "690", "Tokelau"));
        countries.add(new Country("to", "676", "Tonga"));
        countries.add(new Country("tn", "216", "Tunisie"));
        countries.add(new Country("tr", "90", "dinde"));
        countries.add(new Country("tm", "993", "Turkménistan"));
        countries.add(new Country("tv", "688", "Tuvalu"));
        countries.add(new Country("ae", "971", "Emirats Arabes Unis"));
        countries.add(new Country("ug", "256", "Ouganda"));
        countries.add(new Country("gb", "44", "Royaume-Uni"));
        countries.add(new Country("ua", "380", "Ukraine"));
        countries.add(new Country("uy", "598", "Uruguay"));
        countries.add(new Country("us", "1", "États Unis"));
        countries.add(new Country("uz", "998", "Ouzbékistan"));
        countries.add(new Country("vu", "678", "Vanuatu"));
        countries.add(new Country("va", "39", "Saint-Siège (Cité du Vatican)"));
        countries.add(new Country("ve", "58", "Venezuela, République bolivarienne du"));
        countries.add(new Country("vn", "84", "Viet Nam"));
        countries.add(new Country("wf", "681", "Wallis et Futuna"));
        countries.add(new Country("ye", "967", "Yémen"));
        countries.add(new Country("zm", "260", "Zambie"));
        countries.add(new Country("zw", "263", "Zimbabwe"));
        return countries;
    }

    public static List<Country> getLibraryMasterCountriesGerman() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "Afghanistan"));
        countries.add(new Country("al", "355", "Albanien"));
        countries.add(new Country("dz", "213", "Algerien"));
        countries.add(new Country("ad", "376", "Andorra"));
        countries.add(new Country("ao", "244", "Angola"));
        countries.add(new Country("aq", "672", "Antarktika"));
        countries.add(new Country("ar", "54", "Argentinien"));
        countries.add(new Country("am", "374", "Armenien"));
        countries.add(new Country("aw", "297", "Aruba"));
        countries.add(new Country("au", "61", "Australien"));
        countries.add(new Country("at", "43", "Österreich"));
        countries.add(new Country("az", "994", "Aserbaidschan"));
        countries.add(new Country("bh", "973", "Bahrein"));
        countries.add(new Country("bd", "880", "Bangladesch"));
        countries.add(new Country("by", "375", "Weißrussland"));
        countries.add(new Country("be", "32", "Belgien"));
        countries.add(new Country("bz", "501", "Belize"));
        countries.add(new Country("bj", "229", "Benin"));
        countries.add(new Country("bt", "975", "Bhutan"));
        countries.add(new Country("bo", "591", "Bolivien, Plurinationaler Staat"));
        countries.add(new Country("ba", "387", "Bosnien und Herzegowina"));
        countries.add(new Country("bw", "267", "Botswana"));
        countries.add(new Country("br", "55", "Brasilien"));
        countries.add(new Country("bn", "673", "Brunei Darussalam"));
        countries.add(new Country("bg", "359", "Bulgarien"));
        countries.add(new Country("bf", "226", "Burkina Faso"));
        countries.add(new Country("mm", "95", "Myanmar"));
        countries.add(new Country("bi", "257", "Burundi"));
        countries.add(new Country("kh", "855", "Kambodscha"));
        countries.add(new Country("cm", "237", "Kamerun"));
        countries.add(new Country("ca", "1", "Kanada"));
        countries.add(new Country("cv", "238", "Kap Verde"));
        countries.add(new Country("cf", "236", "Zentralafrikanische Republik"));
        countries.add(new Country("td", "235", "Tschad"));
        countries.add(new Country("cl", "56", "Chile"));
        countries.add(new Country("cn", "86", "China"));
        countries.add(new Country("cx", "61", "Weihnachtsinsel"));
        countries.add(new Country("cc", "61", "Cocos (Keeling) Islands"));
        countries.add(new Country("co", "57", "Kolumbien"));
        countries.add(new Country("km", "269", "Komoren"));
        countries.add(new Country("cg", "242", "Kongo"));
        countries.add(new Country("cd", "243", "Kongo, Demokratische Republik der"));
        countries.add(new Country("ck", "682", "Cookinseln"));
        countries.add(new Country("cr", "506", "Costa Rica"));
        countries.add(new Country("hr", "385", "Kroatien"));
        countries.add(new Country("cu", "53", "Kuba"));
        countries.add(new Country("cy", "357", "Zypern"));
        countries.add(new Country("cz", "420", "Tschechien"));
        countries.add(new Country("dk", "45", "Dänemark"));
        countries.add(new Country("dj", "253", "Dschibuti"));
        countries.add(new Country("tl", "670", "Timor-Leste"));
        countries.add(new Country("ec", "593", "Ecuador"));
        countries.add(new Country("eg", "20", "Ägypten"));
        countries.add(new Country("sv", "503", "El Salvador"));
        countries.add(new Country("gq", "240", "Äquatorialguinea"));
        countries.add(new Country("er", "291", "Eritrea"));
        countries.add(new Country("ee", "372", "Estland"));
        countries.add(new Country("et", "251", "Äthiopien"));
        countries.add(new Country("fk", "500", "Falklandinseln (Malvinas)"));
        countries.add(new Country("fo", "298", "Färöer Inseln"));
        countries.add(new Country("fj", "679", "Fidschi"));
        countries.add(new Country("fi", "358", "Finnland"));
        countries.add(new Country("fr", "33", "Frankreich"));
        countries.add(new Country("pf", "689", "Französisch Polynesien"));
        countries.add(new Country("ga", "241", "Gabun"));
        countries.add(new Country("gm", "220", "Gambia"));
        countries.add(new Country("ge", "995", "Georgia"));
        countries.add(new Country("de", "49", "Deutschland"));
        countries.add(new Country("gh", "233", "Ghana"));
        countries.add(new Country("gi", "350", "Gibraltar"));
        countries.add(new Country("gr", "30", "Griechenland"));
        countries.add(new Country("gl", "299", "Grönland"));
        countries.add(new Country("gt", "502", "Guatemala"));
        countries.add(new Country("gn", "224", "Guinea"));
        countries.add(new Country("gw", "245", "Guinea-Bissaus"));
        countries.add(new Country("gy", "592", "Guyana"));
        countries.add(new Country("ht", "509", "Haiti"));
        countries.add(new Country("hn", "504", "Honduras"));
        countries.add(new Country("hk", "852", "Hongkong"));
        countries.add(new Country("hu", "36", "Ungarn"));
        countries.add(new Country("in", "91", "Indien"));
        countries.add(new Country("id", "62", "Indonesien"));
        countries.add(new Country("ir", "98", "Iran, Islamische Republik"));
        countries.add(new Country("iq", "964", "Irak"));
        countries.add(new Country("ie", "353", "Irland"));
        countries.add(new Country("im", "44", "Isle of Man"));
        countries.add(new Country("il", "972", "Israel"));
        countries.add(new Country("it", "39", "Italien"));
        countries.add(new Country("ci", "225", "Côte D & apos; ivoire"));
        countries.add(new Country("jp", "81", "Japan"));
        countries.add(new Country("jo", "962", "Jordanien"));
        countries.add(new Country("kz", "7", "Kasachstan"));
        countries.add(new Country("ke", "254", "Kenia"));
        countries.add(new Country("ki", "686", "Kiribati"));
        countries.add(new Country("kw", "965", "Kuwait"));
        countries.add(new Country("kg", "996", "Kirgisistan"));
        countries.add(new Country("la", "856", "Lao Menschen & apos; s Demokratischen Republik"));
        countries.add(new Country("lv", "371", "Lettland"));
        countries.add(new Country("lb", "961", "Libanon"));
        countries.add(new Country("ls", "266", "Lesotho"));
        countries.add(new Country("lr", "231", "Liberia"));
        countries.add(new Country("ly", "218", "Libyen"));
        countries.add(new Country("li", "423", "Liechtenstein"));
        countries.add(new Country("lt", "370", "Litauen"));
        countries.add(new Country("lu", "352", "Luxemburg"));
        countries.add(new Country("mo", "853", "Macao"));
        countries.add(new Country("mk", "389", "Mazedonien, die ehemalige jugoslawische Republik"));
        countries.add(new Country("mg", "261", "Madagaskar"));
        countries.add(new Country("mw", "265", "Malawi"));
        countries.add(new Country("my", "60", "Malaysia"));
        countries.add(new Country("mv", "960", "Malediven"));
        countries.add(new Country("ml", "223", "Mali"));
        countries.add(new Country("mt", "356", "Malta"));
        countries.add(new Country("mh", "692", "Marshallinseln"));
        countries.add(new Country("mr", "222", "Mauretanien"));
        countries.add(new Country("mu", "230", "Mauritius"));
        countries.add(new Country("yt", "262", "Mayotte"));
        countries.add(new Country("mx", "52", "Mexiko"));
        countries.add(new Country("fm", "691", "Mikronesien, Föderierte Staaten von"));
        countries.add(new Country("md", "373", "Moldawien, Republik"));
        countries.add(new Country("mc", "377", "Monaco"));
        countries.add(new Country("mn", "976", "Mongolei"));
        countries.add(new Country("me", "382", "Montenegro"));
        countries.add(new Country("ma", "212", "Marokko"));
        countries.add(new Country("mz", "258", "Mosambik"));
        countries.add(new Country("na", "264", "Namibia"));
        countries.add(new Country("nr", "674", "Nauru"));
        countries.add(new Country("np", "977", "Nepal"));
        countries.add(new Country("nl", "31", "Niederlande"));
        countries.add(new Country("nc", "687", "Neu-Kaledonien"));
        countries.add(new Country("nz", "64", "Neuseeland"));
        countries.add(new Country("ni", "505", "Nicaragua"));
        countries.add(new Country("ne", "227", "Niger"));
        countries.add(new Country("ng", "234", "Nigeria"));
        countries.add(new Country("nu", "683", "Niue"));
        countries.add(new Country("kp", "850", "Korea"));
        countries.add(new Country("no", "47", "Norwegen"));
        countries.add(new Country("om", "968", "Oman"));
        countries.add(new Country("pk", "92", "Pakistan"));
        countries.add(new Country("pw", "680", "Palau"));
        countries.add(new Country("pa", "507", "Panama"));
        countries.add(new Country("pg", "675", "Papua-Neuguinea"));
        countries.add(new Country("py", "595", "Paraguay"));
        countries.add(new Country("pe", "51", "Peru"));
        countries.add(new Country("ph", "63", "Philippinen"));
        countries.add(new Country("pn", "870", "Pitcairn"));
        countries.add(new Country("pl", "48", "Polen"));
        countries.add(new Country("pt", "351", "Portugal"));
        countries.add(new Country("pr", "1", "Puerto Rico"));
        countries.add(new Country("qa", "974", "Katar"));
        countries.add(new Country("ro", "40", "Rumänien"));
        countries.add(new Country("ru", "7", "Russische Föderation"));
        countries.add(new Country("rw", "250", "Ruanda"));
        countries.add(new Country("bl", "590", "saint Barthélemy"));
        countries.add(new Country("ws", "685", "Samoa"));
        countries.add(new Country("sm", "378", "San Marino"));
        countries.add(new Country("st", "239", "Sao Tome und Principe"));
        countries.add(new Country("sa", "966", "Saudi Arabien"));
        countries.add(new Country("sn", "221", "Senegal"));
        countries.add(new Country("rs", "381", "Serbien"));
        countries.add(new Country("sc", "248", "Seychellen"));
        countries.add(new Country("sl", "232", "Sierra Leone"));
        countries.add(new Country("sg", "65", "Singapur"));
        countries.add(new Country("sk", "421", "Slowakei"));
        countries.add(new Country("si", "386", "Slowenien"));
        countries.add(new Country("sb", "677", "Salomon-Inseln"));
        countries.add(new Country("so", "252", "Somalia"));
        countries.add(new Country("za", "27", "Südafrika"));
        countries.add(new Country("kr", "82", "Korea, Republik von"));
        countries.add(new Country("es", "34", "Spanien"));
        countries.add(new Country("lk", "94", "Sri Lanka"));
        countries.add(new Country("sh", "290", "St. Helena, Ascension und Tristan da Cunha"));
        countries.add(new Country("pm", "508", "St. Pierre und Miquelon"));
        countries.add(new Country("sd", "249", "Sudan"));
        countries.add(new Country("sr", "597", "Suriname"));
        countries.add(new Country("sz", "268", "Swasiland"));
        countries.add(new Country("se", "46", "Schweden"));
        countries.add(new Country("ch", "41", "Schweiz"));
        countries.add(new Country("sy", "963", "Syrische Arabische Republik"));
        countries.add(new Country("tw", "886", "Taiwan, Province of China"));
        countries.add(new Country("tj", "992", "Tadschikistan"));
        countries.add(new Country("tz", "255", "Tansania, Vereinigte Republik"));
        countries.add(new Country("th", "66", "Thailand"));
        countries.add(new Country("tg", "228", "Gehen"));
        countries.add(new Country("tk", "690", "Tokelau"));
        countries.add(new Country("to", "676", "Tonga"));
        countries.add(new Country("tn", "216", "Tunesien"));
        countries.add(new Country("tr", "90", "Truthahn"));
        countries.add(new Country("tm", "993", "Turkmenistan"));
        countries.add(new Country("tv", "688", "Tuvalu"));
        countries.add(new Country("ae", "971", "Vereinigte Arabische Emirate"));
        countries.add(new Country("ug", "256", "Uganda"));
        countries.add(new Country("gb", "44", "Großbritannien"));
        countries.add(new Country("ua", "380", "Ukraine"));
        countries.add(new Country("uy", "598", "Uruguay"));
        countries.add(new Country("us", "1", "Vereinigte Staaten"));
        countries.add(new Country("uz", "998", "Usbekistan"));
        countries.add(new Country("vu", "678", "Vanuatu"));
        countries.add(new Country("va", "39", "Heiliger Stuhl (Vatikanstadt)"));
        countries.add(new Country("ve", "58", "Venezuela, Bolivarische Republik"));
        countries.add(new Country("vn", "84", "Vietnam"));
        countries.add(new Country("wf", "681", "Wallis und Futuna"));
        countries.add(new Country("ye", "967", "Jemen"));
        countries.add(new Country("zm", "260", "Sambia"));
        countries.add(new Country("zw", "263", "Simbabwe"));
        return countries;
    }

    public static List<Country> getLibraryMasterCountriesJapanese() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "アフガニスタン"));
        countries.add(new Country("al", "355", "アルバニア"));
        countries.add(new Country("dz", "213", "アルジェリア"));
        countries.add(new Country("ad", "376", "アンドラ"));
        countries.add(new Country("ao", "244", "アンゴラ"));
        countries.add(new Country("aq", "672", "南極大陸"));
        countries.add(new Country("ar", "54", "アルゼンチン"));
        countries.add(new Country("am", "374", "アルメニア"));
        countries.add(new Country("aw", "297", "アルバ"));
        countries.add(new Country("au", "61", "オーストラリア"));
        countries.add(new Country("at", "43", "オーストリア"));
        countries.add(new Country("az", "994", "アゼルバイジャン"));
        countries.add(new Country("bh", "973", "バーレーン"));
        countries.add(new Country("bd", "880", "バングラデシュ"));
        countries.add(new Country("by", "375", "ベラルーシ"));
        countries.add(new Country("be", "32", "ベルギー"));
        countries.add(new Country("bz", "501", "ベリーズ"));
        countries.add(new Country("bj", "229", "ベニン"));
        countries.add(new Country("bt", "975", "ブータン"));
        countries.add(new Country("bo", "591", "ボリビア、多民族国"));
        countries.add(new Country("ba", "387", "ボスニア・ヘルツェゴビナ"));
        countries.add(new Country("bw", "267", "ボツワナ"));
        countries.add(new Country("br", "55", "ブラジル"));
        countries.add(new Country("bn", "673", "ブルネイ・ダルサラーム国"));
        countries.add(new Country("bg", "359", "ブルガリア"));
        countries.add(new Country("bf", "226", "ブルキナファソ"));
        countries.add(new Country("mm", "95", "ミャンマー"));
        countries.add(new Country("bi", "257", "ブルンジ"));
        countries.add(new Country("kh", "855", "カンボジア"));
        countries.add(new Country("cm", "237", "カメルーン"));
        countries.add(new Country("ca", "1", "カナダ"));
        countries.add(new Country("cv", "238", "カーボベルデ"));
        countries.add(new Country("cf", "236", "中央アフリカ共和国"));
        countries.add(new Country("td", "235", "チャド"));
        countries.add(new Country("cl", "56", "チリ"));
        countries.add(new Country("cn", "86", "中国"));
        countries.add(new Country("cx", "61", "クリスマス島"));
        countries.add(new Country("cc", "61", "ココス（キーリング）諸島"));
        countries.add(new Country("co", "57", "コロンビア"));
        countries.add(new Country("km", "269", "コモロ"));
        countries.add(new Country("cg", "242", "コンゴ"));
        countries.add(new Country("cd", "243", "コンゴ民主共和国"));
        countries.add(new Country("ck", "682", "クック諸島"));
        countries.add(new Country("cr", "506", "コスタリカ"));
        countries.add(new Country("hr", "385", "クロアチア"));
        countries.add(new Country("cu", "53", "キューバ"));
        countries.add(new Country("cy", "357", "キプロス"));
        countries.add(new Country("cz", "420", "チェコ共和国"));
        countries.add(new Country("dk", "45", "デンマーク"));
        countries.add(new Country("dj", "253", "ジブチ"));
        countries.add(new Country("tl", "670", "東ティモール"));
        countries.add(new Country("ec", "593", "エクアドル"));
        countries.add(new Country("eg", "20", "エジプト"));
        countries.add(new Country("sv", "503", "エルサルバドル"));
        countries.add(new Country("gq", "240", "赤道ギニア"));
        countries.add(new Country("er", "291", "エリトリア"));
        countries.add(new Country("ee", "372", "エストニア"));
        countries.add(new Country("et", "251", "エチオピア"));
        countries.add(new Country("fk", "500", "フォークランド諸島（マルビナス諸島）"));
        countries.add(new Country("fo", "298", "フェロー諸島"));
        countries.add(new Country("fj", "679", "フィジー"));
        countries.add(new Country("fi", "358", "フィンランド"));
        countries.add(new Country("fr", "33", "フランス"));
        countries.add(new Country("pf", "689", "フランス領ポリネシア"));
        countries.add(new Country("ga", "241", "ガボン"));
        countries.add(new Country("gm", "220", "ガンビア"));
        countries.add(new Country("ge", "995", "ジョージア"));
        countries.add(new Country("de", "49", "ドイツ"));
        countries.add(new Country("gh", "233", "ガーナ"));
        countries.add(new Country("gi", "350", "ジブラルタル"));
        countries.add(new Country("gr", "30", "ギリシャ"));
        countries.add(new Country("gl", "299", "グリーンランド"));
        countries.add(new Country("gt", "502", "グアテマラ"));
        countries.add(new Country("gn", "224", "ギニア"));
        countries.add(new Country("gw", "245", "ギニアビサウ"));
        countries.add(new Country("gy", "592", "ガイアナ"));
        countries.add(new Country("ht", "509", "ハイチ"));
        countries.add(new Country("hn", "504", "ホンジュラス"));
        countries.add(new Country("hk", "852", "香港"));
        countries.add(new Country("hu", "36", "ハンガリー"));
        countries.add(new Country("in", "91", "インド"));
        countries.add(new Country("id", "62", "インドネシア"));
        countries.add(new Country("ir", "98", "イラン、イスラム共和国"));
        countries.add(new Country("iq", "964", "イラク"));
        countries.add(new Country("ie", "353", "アイルランド"));
        countries.add(new Country("im", "44", "マン島"));
        countries.add(new Country("il", "972", "イスラエル"));
        countries.add(new Country("it", "39", "イタリア"));
        countries.add(new Country("ci", "225", "コー​​ト= D 'は、コートジボワール"));
        countries.add(new Country("jp", "81", "日本"));
        countries.add(new Country("jo", "962", "ヨルダン"));
        countries.add(new Country("kz", "7", "カザフスタン"));
        countries.add(new Country("ke", "254", "ケニア"));
        countries.add(new Country("ki", "686", "キリバス"));
        countries.add(new Country("kw", "965", "クウェート"));
        countries.add(new Country("kg", "996", "キルギスタン"));
        countries.add(new Country("la", "856", "ラオス人民 'は、sの民主共和国"));
        countries.add(new Country("lv", "371", "ラトビア"));
        countries.add(new Country("lb", "961", "レバノン"));
        countries.add(new Country("ls", "266", "レソト"));
        countries.add(new Country("lr", "231", "リベリア"));
        countries.add(new Country("ly", "218", "リビア"));
        countries.add(new Country("li", "423", "リヒテンシュタイン"));
        countries.add(new Country("lt", "370", "リトアニア"));
        countries.add(new Country("lu", "352", "ルクセンブルク"));
        countries.add(new Country("mo", "853", "マカオ"));
        countries.add(new Country("mk", "389", "マケドニア旧ユーゴスラビア共和国"));
        countries.add(new Country("mg", "261", "マダガスカル"));
        countries.add(new Country("mw", "265", "マラウイ"));
        countries.add(new Country("my", "60", "マレーシア"));
        countries.add(new Country("mv", "960", "モルディブ"));
        countries.add(new Country("ml", "223", "マリ"));
        countries.add(new Country("mt", "356", "マルタ"));
        countries.add(new Country("mh", "692", "マーシャル諸島"));
        countries.add(new Country("mr", "222", "モーリタニア"));
        countries.add(new Country("mu", "230", "モーリシャス"));
        countries.add(new Country("yt", "262", "マヨット島"));
        countries.add(new Country("mx", "52", "メキシコ"));
        countries.add(new Country("fm", "691", "ミクロネシア連邦"));
        countries.add(new Country("md", "373", "モルドバ共和国"));
        countries.add(new Country("mc", "377", "モナコ"));
        countries.add(new Country("mn", "976", "モンゴル"));
        countries.add(new Country("me", "382", "モンテネグロ"));
        countries.add(new Country("ma", "212", "モロッコ"));
        countries.add(new Country("mz", "258", "モザンビーク"));
        countries.add(new Country("na", "264", "ナミビア"));
        countries.add(new Country("nr", "674", "ナウル"));
        countries.add(new Country("np", "977", "ネパール"));
        countries.add(new Country("nl", "31", "オランダ"));
        countries.add(new Country("nc", "687", "ニューカレドニア"));
        countries.add(new Country("nz", "64", "ニュージーランド"));
        countries.add(new Country("ni", "505", "ニカラグア"));
        countries.add(new Country("ne", "227", "ニジェール"));
        countries.add(new Country("ng", "234", "ナイジェリア"));
        countries.add(new Country("nu", "683", "ニウエ"));
        countries.add(new Country("kp", "850", "韓国、民主主義人民 'は、うちの共和国"));
        countries.add(new Country("no", "47", "ノルウェー"));
        countries.add(new Country("om", "968", "オマーン"));
        countries.add(new Country("pk", "92", "パキスタン"));
        countries.add(new Country("pw", "680", "パラオ"));
        countries.add(new Country("pa", "507", "パナマ"));
        countries.add(new Country("pg", "675", "パプアニューギニア"));
        countries.add(new Country("py", "595", "パラグアイ"));
        countries.add(new Country("pe", "51", "ペルー"));
        countries.add(new Country("ph", "63", "フィリピン"));
        countries.add(new Country("pn", "870", "ピトケアン"));
        countries.add(new Country("pl", "48", "ポーランド"));
        countries.add(new Country("pt", "351", "ポルトガル"));
        countries.add(new Country("pr", "1", "プエルトリコ"));
        countries.add(new Country("qa", "974", "カタール"));
        countries.add(new Country("ro", "40", "ルーマニア"));
        countries.add(new Country("ru", "7", "ロシア連邦"));
        countries.add(new Country("rw", "250", "ルワンダ"));
        countries.add(new Country("bl", "590", "セントバーツ"));
        countries.add(new Country("ws", "685", "サモア"));
        countries.add(new Country("sm", "378", "サン・マリノ"));
        countries.add(new Country("st", "239", "サントメプリンシペ"));
        countries.add(new Country("sa", "966", "サウジアラビア"));
        countries.add(new Country("sn", "221", "セネガル"));
        countries.add(new Country("rs", "381", "セルビア"));
        countries.add(new Country("sc", "248", "セイシェル"));
        countries.add(new Country("sl", "232", "シエラレオネ"));
        countries.add(new Country("sg", "65", "シンガポール"));
        countries.add(new Country("sk", "421", "スロバキア"));
        countries.add(new Country("si", "386", "スロベニア"));
        countries.add(new Country("sb", "677", "ソロモン諸島"));
        countries.add(new Country("so", "252", "ソマリア"));
        countries.add(new Country("za", "27", "南アフリカ"));
        countries.add(new Country("kr", "82", "韓国"));
        countries.add(new Country("es", "34", "スペイン"));
        countries.add(new Country("lk", "94", "スリランカ"));
        countries.add(new Country("sh", "290", "セントヘレナ・アセンションおよびトリスタンダクーニャ"));
        countries.add(new Country("pm", "508", "サンピエール島・ミクロン島"));
        countries.add(new Country("sd", "249", "スーダン"));
        countries.add(new Country("sr", "597", "スリナム"));
        countries.add(new Country("sz", "268", "スワジランド"));
        countries.add(new Country("se", "46", "スウェーデン"));
        countries.add(new Country("ch", "41", "スイス"));
        countries.add(new Country("sy", "963", "シリアアラブ共和国"));
        countries.add(new Country("tw", "886", "台湾、中国の省"));
        countries.add(new Country("tj", "992", "タジキスタン"));
        countries.add(new Country("tz", "255", "タンザニア連合共和国"));
        countries.add(new Country("th", "66", "タイ"));
        countries.add(new Country("tg", "228", "行く"));
        countries.add(new Country("tk", "690", "トケラウ諸島"));
        countries.add(new Country("to", "676", "トンガ"));
        countries.add(new Country("tn", "216", "チュニジア"));
        countries.add(new Country("tr", "90", "七面鳥"));
        countries.add(new Country("tm", "993", "トルクメニスタン"));
        countries.add(new Country("tv", "688", "ツバル"));
        countries.add(new Country("ae", "971", "アラブ首長国連邦"));
        countries.add(new Country("ug", "256", "ウガンダ"));
        countries.add(new Country("gb", "44", "イギリス"));
        countries.add(new Country("ua", "380", "ウクライナ"));
        countries.add(new Country("uy", "598", "ウルグアイ"));
        countries.add(new Country("us", "1", "アメリカ"));
        countries.add(new Country("uz", "998", "ウズベキスタン"));
        countries.add(new Country("vu", "678", "バヌアツ"));
        countries.add(new Country("va", "39", "ローマ法王庁（バチカン市国）"));
        countries.add(new Country("ve", "58", "ベネズエラ、ボリバル共和国"));
        countries.add(new Country("vn", "84", "ベトナム"));
        countries.add(new Country("wf", "681", "ワリー・エ・フトゥーナ"));
        countries.add(new Country("ye", "967", "イエメン"));
        countries.add(new Country("zm", "260", "ザンビア"));
        countries.add(new Country("zw", "263", "ジンバブエ"));
        return countries;
    }

    public static List<Country> getLibraryMasterCountriesSpanish() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "Afganistán"));
        countries.add(new Country("al", "355", "Albania"));
        countries.add(new Country("dz", "213", "Argelia"));
        countries.add(new Country("ad", "376", "Andorra"));
        countries.add(new Country("ao", "244", "Angola"));
        countries.add(new Country("aq", "672", "Antártida"));
        countries.add(new Country("ar", "54", "Argentina"));
        countries.add(new Country("am", "374", "Armenia"));
        countries.add(new Country("aw", "297", "Aruba"));
        countries.add(new Country("au", "61", "Australia"));
        countries.add(new Country("at", "43", "Austria"));
        countries.add(new Country("az", "994", "Azerbaiyán"));
        countries.add(new Country("bh", "973", "Bahrein"));
        countries.add(new Country("bd", "880", "Bangladesh"));
        countries.add(new Country("by", "375", "Belarús"));
        countries.add(new Country("be", "32", "Bélgica"));
        countries.add(new Country("bz", "501", "Belice"));
        countries.add(new Country("bj", "229", "Benin"));
        countries.add(new Country("bt", "975", "Bhután"));
        countries.add(new Country("bo", "591", "Bolivia, Estado Plurinacional de"));
        countries.add(new Country("ba", "387", "Bosnia y Herzegovina"));
        countries.add(new Country("bw", "267", "Botswana"));
        countries.add(new Country("br", "55", "Brasil"));
        countries.add(new Country("bn", "673", "Brunei Darussalam"));
        countries.add(new Country("bg", "359", "Bulgaria"));
        countries.add(new Country("bf", "226", "Burkina Faso"));
        countries.add(new Country("mm", "95", "Myanmar"));
        countries.add(new Country("bi", "257", "Burundi"));
        countries.add(new Country("kh", "855", "Camboya"));
        countries.add(new Country("cm", "237", "Camerún"));
        countries.add(new Country("ca", "1", "Canadá"));
        countries.add(new Country("cv", "238", "Cabo Verde"));
        countries.add(new Country("cf", "236", "República Centroafricana"));
        countries.add(new Country("td", "235", "Chad"));
        countries.add(new Country("cl", "56", "Chile"));
        countries.add(new Country("cn", "86", "China"));
        countries.add(new Country("cx", "61", "Isla de Navidad"));
        countries.add(new Country("cc", "61", "Cocos (Keeling)"));
        countries.add(new Country("co", "57", "Colombia"));
        countries.add(new Country("km", "269", "Comoras"));
        countries.add(new Country("cg", "242", "Congo"));
        countries.add(new Country("cd", "243", "Congo, La República Democrática Del"));
        countries.add(new Country("ck", "682", "Islas Cook"));
        countries.add(new Country("cr", "506", "Costa Rica"));
        countries.add(new Country("hr", "385", "Croacia"));
        countries.add(new Country("cu", "53", "Cuba"));
        countries.add(new Country("cy", "357", "Chipre"));
        countries.add(new Country("cz", "420", "República Checa"));
        countries.add(new Country("dk", "45", "Dinamarca"));
        countries.add(new Country("dj", "253", "Djibouti"));
        countries.add(new Country("tl", "670", "Timor Oriental"));
        countries.add(new Country("ec", "593", "Ecuador"));
        countries.add(new Country("eg", "20", "Egipto"));
        countries.add(new Country("sv", "503", "El Salvador"));
        countries.add(new Country("gq", "240", "Guinea Ecuatorial"));
        countries.add(new Country("er", "291", "Eritrea"));
        countries.add(new Country("ee", "372", "Estonia"));
        countries.add(new Country("et", "251", "Etiopía"));
        countries.add(new Country("fk", "500", "Islas Malvinas (Falkland)"));
        countries.add(new Country("fo", "298", "Islas Faroe"));
        countries.add(new Country("fj", "679", "Fiji"));
        countries.add(new Country("fi", "358", "Finlandia"));
        countries.add(new Country("fr", "33", "Francia"));
        countries.add(new Country("pf", "689", "Polinesia francés"));
        countries.add(new Country("ga", "241", "Gabón"));
        countries.add(new Country("gm", "220", "Gambia"));
        countries.add(new Country("ge", "995", "Georgia"));
        countries.add(new Country("de", "49", "Alemania"));
        countries.add(new Country("gh", "233", "Ghana"));
        countries.add(new Country("gi", "350", "Gibraltar"));
        countries.add(new Country("gr", "30", "Grecia"));
        countries.add(new Country("gl", "299", "Tierra Verde"));
        countries.add(new Country("gt", "502", "Guatemala"));
        countries.add(new Country("gn", "224", "Guinea"));
        countries.add(new Country("gw", "245", "Guinea-Bissau"));
        countries.add(new Country("gy", "592", "Guayana"));
        countries.add(new Country("ht", "509", "Haití"));
        countries.add(new Country("hn", "504", "Honduras"));
        countries.add(new Country("hk", "852", "Hong Kong"));
        countries.add(new Country("hu", "36", "Hungría"));
        countries.add(new Country("in", "91", "India"));
        countries.add(new Country("id", "62", "Indonesia"));
        countries.add(new Country("ir", "98", "Irán (República Islámica de"));
        countries.add(new Country("iq", "964", "Irak"));
        countries.add(new Country("ie", "353", "Irlanda"));
        countries.add(new Country("im", "44", "Isla de Man"));
        countries.add(new Country("il", "972", "Israel"));
        countries.add(new Country("it", "39", "Italia"));
        countries.add(new Country("ci", "225", "Côte D & apos; Marfil"));
        countries.add(new Country("jp", "81", "Japón"));
        countries.add(new Country("jo", "962", "Jordán"));
        countries.add(new Country("kz", "7", "Kazajstán"));
        countries.add(new Country("ke", "254", "Kenia"));
        countries.add(new Country("ki", "686", "Kiribati"));
        countries.add(new Country("kw", "965", "Kuwait"));
        countries.add(new Country("kg", "996", "Kirguistán"));
        countries.add(new Country("la", "856", "República Democrática s; Lao y apos"));
        countries.add(new Country("lv", "371", "Letonia"));
        countries.add(new Country("lb", "961", "Líbano"));
        countries.add(new Country("ls", "266", "Lesoto"));
        countries.add(new Country("lr", "231", "Liberia"));
        countries.add(new Country("ly", "218", "Libia"));
        countries.add(new Country("li", "423", "Liechtenstein"));
        countries.add(new Country("lt", "370", "Lituania"));
        countries.add(new Country("lu", "352", "Luxemburgo"));
        countries.add(new Country("mo", "853", "macao"));
        countries.add(new Country("mk", "389", "Macedonia, Ex-República Yugoslava de"));
        countries.add(new Country("mg", "261", "Madagascar"));
        countries.add(new Country("mw", "265", "Malawi"));
        countries.add(new Country("my", "60", "Malasia"));
        countries.add(new Country("mv", "960", "Maldivas"));
        countries.add(new Country("ml", "223", "mali"));
        countries.add(new Country("mt", "356", "Malta"));
        countries.add(new Country("mh", "692", "Islas Marshall"));
        countries.add(new Country("mr", "222", "Mauritania"));
        countries.add(new Country("mu", "230", "Mauricio"));
        countries.add(new Country("yt", "262", "Mayotte"));
        countries.add(new Country("mx", "52", "Méjico"));
        countries.add(new Country("fm", "691", "Micronesia, Estados Federados de"));
        countries.add(new Country("md", "373", "Moldavia"));
        countries.add(new Country("mc", "377", "Mónaco"));
        countries.add(new Country("mn", "976", "Mongolia"));
        countries.add(new Country("me", "382", "Montenegro"));
        countries.add(new Country("ma", "212", "Marruecos"));
        countries.add(new Country("mz", "258", "Mozambique"));
        countries.add(new Country("na", "264", "Namibia"));
        countries.add(new Country("nr", "674", "Nauru"));
        countries.add(new Country("np", "977", "Nepal"));
        countries.add(new Country("nl", "31", "Países Bajos"));
        countries.add(new Country("nc", "687", "Nueva Caledonia"));
        countries.add(new Country("nz", "64", "Nueva Zelanda"));
        countries.add(new Country("ni", "505", "Nicaragua"));
        countries.add(new Country("ne", "227", "Níger"));
        countries.add(new Country("ng", "234", "Nigeria"));
        countries.add(new Country("nu", "683", "Niue"));
        countries.add(new Country("kp", "850", "República de s; Corea, Personas & apos Democrática"));
        countries.add(new Country("no", "47", "Noruega"));
        countries.add(new Country("om", "968", "Omán"));
        countries.add(new Country("pk", "92", "Pakistán"));
        countries.add(new Country("pw", "680", "Palau"));
        countries.add(new Country("pa", "507", "Panamá"));
        countries.add(new Country("pg", "675", "Papúa Nueva Guinea"));
        countries.add(new Country("py", "595", "Paraguay"));
        countries.add(new Country("pe", "51", "Perú"));
        countries.add(new Country("ph", "63", "Filipinas"));
        countries.add(new Country("pn", "870", "Pitcairn"));
        countries.add(new Country("pl", "48", "Polonia"));
        countries.add(new Country("pt", "351", "Portugal"));
        countries.add(new Country("pr", "1", "Puerto Rico"));
        countries.add(new Country("qa", "974", "Katar"));
        countries.add(new Country("ro", "40", "Rumania"));
        countries.add(new Country("ru", "7", "Federación Rusa"));
        countries.add(new Country("rw", "250", "Ruanda"));
        countries.add(new Country("bl", "590", "San Bartolomé"));
        countries.add(new Country("ws", "685", "Samoa"));
        countries.add(new Country("sm", "378", "San Marino"));
        countries.add(new Country("st", "239", "Santo Tomé y Príncipe"));
        countries.add(new Country("sa", "966", "Arabia Saudita"));
        countries.add(new Country("sn", "221", "Senegal"));
        countries.add(new Country("rs", "381", "Serbia"));
        countries.add(new Country("sc", "248", "Seychelles"));
        countries.add(new Country("sl", "232", "Sierra Leona"));
        countries.add(new Country("sg", "65", "Singapur"));
        countries.add(new Country("sk", "421", "Eslovaquia"));
        countries.add(new Country("si", "386", "Eslovenia"));
        countries.add(new Country("sb", "677", "Islas Salomón"));
        countries.add(new Country("so", "252", "Somalia"));
        countries.add(new Country("za", "27", "Sudáfrica"));
        countries.add(new Country("kr", "82", "Corea"));
        countries.add(new Country("es", "34", "España"));
        countries.add(new Country("lk", "94", "Sri Lanka"));
        countries.add(new Country("sh", "290", "Santa Helena, Ascensión y Tristán da Cunha"));
        countries.add(new Country("pm", "508", "San Pedro y Miquelón"));
        countries.add(new Country("sd", "249", "Sudán"));
        countries.add(new Country("sr", "597", "Surinam"));
        countries.add(new Country("sz", "268", "Swazilandia"));
        countries.add(new Country("se", "46", "Suecia"));
        countries.add(new Country("ch", "41", "Suiza"));
        countries.add(new Country("sy", "963", "República Árabe Siria"));
        countries.add(new Country("tw", "886", "Taiwan, provincia de China"));
        countries.add(new Country("tj", "992", "Tayikistán"));
        countries.add(new Country("tz", "255", "Tanzania, República Unida de"));
        countries.add(new Country("th", "66", "Tailandia"));
        countries.add(new Country("tg", "228", "Ir"));
        countries.add(new Country("tk", "690", "Tokelau"));
        countries.add(new Country("to", "676", "Tonga"));
        countries.add(new Country("tn", "216", "Túnez"));
        countries.add(new Country("tr", "90", "Turquía"));
        countries.add(new Country("tm", "993", "Turkmenistán"));
        countries.add(new Country("tv", "688", "Tuvalu"));
        countries.add(new Country("ae", "971", "Emiratos Árabes Unidos"));
        countries.add(new Country("ug", "256", "Uganda"));
        countries.add(new Country("gb", "44", "Reino Unido"));
        countries.add(new Country("ua", "380", "Ucrania"));
        countries.add(new Country("uy", "598", "Uruguay"));
        countries.add(new Country("us", "1", "Estados Unidos"));
        countries.add(new Country("uz", "998", "Uzbekistán"));
        countries.add(new Country("vu", "678", "Vanuatu"));
        countries.add(new Country("va", "39", "Santa Sede (Ciudad del Vaticano)"));
        countries.add(new Country("ve", "58", "Venezuela, República Bolivariana de"));
        countries.add(new Country("vn", "84", "Vietnam"));
        countries.add(new Country("wf", "681", "Wallis y Futuna"));
        countries.add(new Country("ye", "967", "Yemen"));
        countries.add(new Country("zm", "260", "Zambia"));
        countries.add(new Country("zw", "263", "Zimbabue"));
        return countries;
    }

    public static List<Country> getLibraryMasterCountriesChinese() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "阿富汗"));
        countries.add(new Country("al", "355", "阿尔巴尼亚"));
        countries.add(new Country("dz", "213", "阿尔及利亚"));
        countries.add(new Country("ad", "376", "安道​​尔"));
        countries.add(new Country("ao", "244", "安哥拉"));
        countries.add(new Country("aq", "672", "南极洲"));
        countries.add(new Country("ar", "54", "阿根廷"));
        countries.add(new Country("am", "374", "亚美尼亚"));
        countries.add(new Country("aw", "297", "阿鲁巴"));
        countries.add(new Country("au", "61", "澳大利亚"));
        countries.add(new Country("at", "43", "奥地利"));
        countries.add(new Country("az", "994", "阿塞拜疆"));
        countries.add(new Country("bh", "973", "巴林"));
        countries.add(new Country("bd", "880", "孟加拉国"));
        countries.add(new Country("by", "375", "白俄罗斯"));
        countries.add(new Country("be", "32", "比利时"));
        countries.add(new Country("bz", "501", "伯利兹"));
        countries.add(new Country("bj", "229", "贝宁"));
        countries.add(new Country("bt", "975", "不丹"));
        countries.add(new Country("bo", "591", "玻利维亚多民族国"));
        countries.add(new Country("ba", "387", "波斯尼亚和黑塞哥维那"));
        countries.add(new Country("bw", "267", "博茨瓦纳"));
        countries.add(new Country("br", "55", "巴西"));
        countries.add(new Country("bn", "673", "文莱达鲁萨兰国"));
        countries.add(new Country("bg", "359", "保加利亚"));
        countries.add(new Country("bf", "226", "布基纳法索"));
        countries.add(new Country("mm", "95", "缅甸"));
        countries.add(new Country("bi", "257", "布隆迪"));
        countries.add(new Country("kh", "855", "柬埔寨"));
        countries.add(new Country("cm", "237", "喀麦隆"));
        countries.add(new Country("ca", "1", "加拿大"));
        countries.add(new Country("cv", "238", "佛得角"));
        countries.add(new Country("cf", "236", "中非共和国"));
        countries.add(new Country("td", "235", "乍得"));
        countries.add(new Country("cl", "56", "智利"));
        countries.add(new Country("cn", "86", "中国"));
        countries.add(new Country("cx", "61", "圣诞岛"));
        countries.add(new Country("cc", "61", "科科斯"));
        countries.add(new Country("co", "57", "哥伦比亚"));
        countries.add(new Country("km", "269", "科摩罗"));
        countries.add(new Country("cg", "242", "刚果"));
        countries.add(new Country("cd", "243", "刚果民主共和国中"));
        countries.add(new Country("ck", "682", "库克群岛"));
        countries.add(new Country("cr", "506", "哥斯达黎加"));
        countries.add(new Country("hr", "385", "克罗地亚"));
        countries.add(new Country("cu", "53", "古巴"));
        countries.add(new Country("cy", "357", "塞浦路斯"));
        countries.add(new Country("cz", "420", "捷克共和国"));
        countries.add(new Country("dk", "45", "丹麦"));
        countries.add(new Country("dj", "253", "吉布提"));
        countries.add(new Country("tl", "670", "东帝汶"));
        countries.add(new Country("ec", "593", "厄瓜多尔"));
        countries.add(new Country("eg", "20", "埃及"));
        countries.add(new Country("sv", "503", "萨尔瓦多"));
        countries.add(new Country("gq", "240", "赤道几内亚"));
        countries.add(new Country("er", "291", "厄立特里亚"));
        countries.add(new Country("ee", "372", "爱沙尼亚"));
        countries.add(new Country("et", "251", "埃塞俄比亚"));
        countries.add(new Country("fk", "500", "福克兰群岛（马尔维纳斯群岛）"));
        countries.add(new Country("fo", "298", "法罗群岛"));
        countries.add(new Country("fj", "679", "斐"));
        countries.add(new Country("fi", "358", "芬兰"));
        countries.add(new Country("fr", "33", "法国"));
        countries.add(new Country("pf", "689", "法属波利尼西亚"));
        countries.add(new Country("ga", "241", "加蓬"));
        countries.add(new Country("gm", "220", "冈比亚"));
        countries.add(new Country("ge", "995", "格鲁吉亚"));
        countries.add(new Country("de", "49", "德国"));
        countries.add(new Country("gh", "233", "加纳"));
        countries.add(new Country("gi", "350", "直布罗陀"));
        countries.add(new Country("gr", "30", "希腊"));
        countries.add(new Country("gl", "299", "格陵兰"));
        countries.add(new Country("gt", "502", "危地马拉"));
        countries.add(new Country("gn", "224", "几内亚"));
        countries.add(new Country("gw", "245", "几内亚比绍"));
        countries.add(new Country("gy", "592", "圭亚那"));
        countries.add(new Country("ht", "509", "海地"));
        countries.add(new Country("hn", "504", "洪都拉斯"));
        countries.add(new Country("hk", "852", "香港"));
        countries.add(new Country("hu", "36", "匈牙利"));
        countries.add(new Country("in", "91", "印度"));
        countries.add(new Country("id", "62", "印度尼西亚"));
        countries.add(new Country("ir", "98", "伊朗伊斯兰共和国"));
        countries.add(new Country("iq", "964", "伊拉克"));
        countries.add(new Country("ie", "353", "爱尔兰"));
        countries.add(new Country("im", "44", "马恩岛"));
        countries.add(new Country("il", "972", "以色列"));
        countries.add(new Country("it", "39", "意大利"));
        countries.add(new Country("ci", "225", "科特迪瓦者;科特迪瓦"));
        countries.add(new Country("jp", "81", "日本"));
        countries.add(new Country("jo", "962", "约旦"));
        countries.add(new Country("kz", "7", "哈萨克斯坦"));
        countries.add(new Country("ke", "254", "肯尼亚"));
        countries.add(new Country("ki", "686", "基里巴斯"));
        countries.add(new Country("kw", "965", "科威特"));
        countries.add(new Country("kg", "996", "吉尔吉斯斯坦"));
        countries.add(new Country("la", "856", "老挝人民氏民主共和国"));
        countries.add(new Country("lv", "371", "拉脱维亚"));
        countries.add(new Country("lb", "961", "黎巴嫩"));
        countries.add(new Country("ls", "266", "莱索托"));
        countries.add(new Country("lr", "231", "利比里亚"));
        countries.add(new Country("ly", "218", "利比亚"));
        countries.add(new Country("li", "423", "列支敦士登"));
        countries.add(new Country("lt", "370", "立陶宛"));
        countries.add(new Country("lu", "352", "卢森堡"));
        countries.add(new Country("mo", "853", "澳门"));
        countries.add(new Country("mk", "389", "马其顿，前南斯拉夫共和国"));
        countries.add(new Country("mg", "261", "马达加斯加"));
        countries.add(new Country("mw", "265", "马拉维"));
        countries.add(new Country("my", "60", "马来西亚"));
        countries.add(new Country("mv", "960", "马尔代夫"));
        countries.add(new Country("ml", "223", "马里"));
        countries.add(new Country("mt", "356", "马耳他"));
        countries.add(new Country("mh", "692", "马绍尔群岛"));
        countries.add(new Country("mr", "222", "毛里塔尼亚"));
        countries.add(new Country("mu", "230", "毛里求斯"));
        countries.add(new Country("yt", "262", "马约特"));
        countries.add(new Country("mx", "52", "墨西哥"));
        countries.add(new Country("fm", "691", "密克罗尼西亚联邦"));
        countries.add(new Country("md", "373", "摩尔多瓦共和国"));
        countries.add(new Country("mc", "377", "摩纳哥"));
        countries.add(new Country("mn", "976", "蒙古"));
        countries.add(new Country("me", "382", "黑山"));
        countries.add(new Country("ma", "212", "摩洛哥"));
        countries.add(new Country("mz", "258", "莫桑比克"));
        countries.add(new Country("na", "264", "纳米比亚"));
        countries.add(new Country("nr", "674", "瑙鲁"));
        countries.add(new Country("np", "977", "尼泊尔"));
        countries.add(new Country("nl", "31", "荷兰"));
        countries.add(new Country("nc", "687", "新喀里多尼亚"));
        countries.add(new Country("nz", "64", "新西兰"));
        countries.add(new Country("ni", "505", "尼加拉瓜"));
        countries.add(new Country("ne", "227", "尼日尔"));
        countries.add(new Country("ng", "234", "尼日利亚"));
        countries.add(new Country("nu", "683", "纽埃"));
        countries.add(new Country("kp", "850", "韩国，朝鲜民主主义人民共和国氏共和国"));
        countries.add(new Country("no", "47", "挪威"));
        countries.add(new Country("om", "968", "阿曼"));
        countries.add(new Country("pk", "92", "巴基斯坦"));
        countries.add(new Country("pw", "680", "帕劳"));
        countries.add(new Country("pa", "507", "巴拿马"));
        countries.add(new Country("pg", "675", "巴布亚新几内亚"));
        countries.add(new Country("py", "595", "巴拉圭"));
        countries.add(new Country("pe", "51", "秘鲁"));
        countries.add(new Country("ph", "63", "菲律宾"));
        countries.add(new Country("pn", "870", "皮特凯恩"));
        countries.add(new Country("pl", "48", "波兰"));
        countries.add(new Country("pt", "351", "葡萄牙"));
        countries.add(new Country("pr", "1", "波多黎各"));
        countries.add(new Country("qa", "974", "卡塔尔"));
        countries.add(new Country("ro", "40", "罗马尼亚"));
        countries.add(new Country("ru", "7", "俄罗斯联邦"));
        countries.add(new Country("rw", "250", "卢旺达"));
        countries.add(new Country("bl", "590", "圣巴泰勒米"));
        countries.add(new Country("ws", "685", "萨摩亚"));
        countries.add(new Country("sm", "378", "圣马力诺"));
        countries.add(new Country("st", "239", "圣多美和普林西比"));
        countries.add(new Country("sa", "966", "沙特阿拉伯"));
        countries.add(new Country("sn", "221", "塞内加尔"));
        countries.add(new Country("rs", "381", "塞尔维亚"));
        countries.add(new Country("sc", "248", "塞舌尔"));
        countries.add(new Country("sl", "232", "塞拉利昂"));
        countries.add(new Country("sg", "65", "新加坡"));
        countries.add(new Country("sk", "421", "斯洛伐克"));
        countries.add(new Country("si", "386", "斯洛文尼亚"));
        countries.add(new Country("sb", "677", "所罗门群岛"));
        countries.add(new Country("so", "252", "索马里"));
        countries.add(new Country("za", "27", "南非"));
        countries.add(new Country("kr", "82", "韩国"));
        countries.add(new Country("es", "34", "西班牙"));
        countries.add(new Country("lk", "94", "斯里兰卡"));
        countries.add(new Country("sh", "290", "圣赫勒拿岛，阿森松和特里斯坦 - 达库尼亚群岛"));
        countries.add(new Country("pm", "508", "圣皮埃尔和密克隆"));
        countries.add(new Country("sd", "249", "苏丹"));
        countries.add(new Country("sr", "597", "苏里南"));
        countries.add(new Country("sz", "268", "斯威士兰"));
        countries.add(new Country("se", "46", "瑞典"));
        countries.add(new Country("ch", "41", "瑞士"));
        countries.add(new Country("sy", "963", "阿拉伯叙利亚共和国"));
        countries.add(new Country("tw", "886", "台湾省中国"));
        countries.add(new Country("tj", "992", "塔吉克斯坦"));
        countries.add(new Country("tz", "255", "坦桑尼亚联合共和国"));
        countries.add(new Country("th", "66", "泰国"));
        countries.add(new Country("tg", "228", "多哥"));
        countries.add(new Country("tk", "690", "托克劳"));
        countries.add(new Country("to", "676", "汤加"));
        countries.add(new Country("tn", "216", "突尼斯"));
        countries.add(new Country("tr", "90", "火鸡"));
        countries.add(new Country("tm", "993", "土库曼斯坦"));
        countries.add(new Country("tv", "688", "图瓦卢"));
        countries.add(new Country("ae", "971", "阿拉伯联合酋长国"));
        countries.add(new Country("ug", "256", "乌干达"));
        countries.add(new Country("gb", "44", "英国"));
        countries.add(new Country("ua", "380", "乌克兰"));
        countries.add(new Country("uy", "598", "乌拉圭"));
        countries.add(new Country("us", "1", "美国"));
        countries.add(new Country("uz", "998", "乌兹别克斯坦"));
        countries.add(new Country("vu", "678", "瓦努阿图"));
        countries.add(new Country("va", "39", "教廷（梵蒂冈城国）"));
        countries.add(new Country("ve", "58", "委内瑞拉玻利瓦尔共和国"));
        countries.add(new Country("vn", "84", "越南"));
        countries.add(new Country("wf", "681", "瓦利斯群岛和富图纳群岛"));
        countries.add(new Country("ye", "967", "也门"));
        countries.add(new Country("zm", "260", "赞比亚"));
        countries.add(new Country("zw", "263", "津巴布韦"));
        return countries;
    }

    public static List<Country> getLibraryMasterCountriesArabic() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "أفغانستان"));
        countries.add(new Country("al", "355", "ألبانيا"));
        countries.add(new Country("dz", "213", "الجزائر"));
        countries.add(new Country("ad", "376", "أندورا"));
        countries.add(new Country("ao", "244", "أنغولا"));
        countries.add(new Country("aq", "672", "القطب الجنوبي"));
        countries.add(new Country("ar", "54", "الأرجنتين"));
        countries.add(new Country("am", "374", "أرمينيا"));
        countries.add(new Country("aw", "297", "أروبا"));
        countries.add(new Country("au", "61", "أستراليا"));
        countries.add(new Country("at", "43", "النمسا"));
        countries.add(new Country("az", "994", "أذربيجان"));
        countries.add(new Country("bh", "973", "البحرين"));
        countries.add(new Country("bd", "880", "بنغلاديش"));
        countries.add(new Country("by", "375", "روسيا البيضاء"));
        countries.add(new Country("be", "32", "بلجيكا"));
        countries.add(new Country("bz", "501", "بليز"));
        countries.add(new Country("bj", "229", "بنين"));
        countries.add(new Country("bt", "975", "بوتان"));
        countries.add(new Country("bo", "591", "دولة بوليفيا المتعددة القوميات"));
        countries.add(new Country("ba", "387", "البوسنة والهرسك"));
        countries.add(new Country("bw", "267", "بوتسوانا"));
        countries.add(new Country("br", "55", "البرازيل"));
        countries.add(new Country("bn", "673", "بروناي دار السلام"));
        countries.add(new Country("bg", "359", "بلغاريا"));
        countries.add(new Country("bf", "226", "بوركينا فاسو"));
        countries.add(new Country("mm", "95", "ميانمار"));
        countries.add(new Country("bi", "257", "بوروندي"));
        countries.add(new Country("kh", "855", "كمبوديا"));
        countries.add(new Country("cm", "237", "الكاميرون"));
        countries.add(new Country("ca", "1", "كندا"));
        countries.add(new Country("cv", "238", "الرأس الأخضر"));
        countries.add(new Country("cf", "236", "جمهورية افريقيا الوسطى"));
        countries.add(new Country("td", "235", "تشاد"));
        countries.add(new Country("cl", "56", "تشيلي"));
        countries.add(new Country("cn", "86", "الصين"));
        countries.add(new Country("cx", "61", "جزيرة الكريسماس"));
        countries.add(new Country("cc", "61", "جزر كوكوس (كيلينغ)"));
        countries.add(new Country("co", "57", "كولومبيا"));
        countries.add(new Country("km", "269", "جزر القمر"));
        countries.add(new Country("cg", "242", "الكونغو"));
        countries.add(new Country("cd", "243", "الكونغو، جمهورية الكونغو الديمقراطية لل"));
        countries.add(new Country("ck", "682", "جزر كوك"));
        countries.add(new Country("cr", "506", "كوستا ريكا"));
        countries.add(new Country("hr", "385", "كرواتيا"));
        countries.add(new Country("cu", "53", "كوبا"));
        countries.add(new Country("cy", "357", "قبرص"));
        countries.add(new Country("cz", "420", "جمهورية التشيك"));
        countries.add(new Country("dk", "45", "الدنمارك"));
        countries.add(new Country("dj", "253", "جيبوتي"));
        countries.add(new Country("tl", "670", "تيمور ليشتي"));
        countries.add(new Country("ec", "593", "الإكوادور"));
        countries.add(new Country("eg", "20", "مصر"));
        countries.add(new Country("sv", "503", "السلفادور"));
        countries.add(new Country("gq", "240", "غينيا الإستوائية"));
        countries.add(new Country("er", "291", "إريتريا"));
        countries.add(new Country("ee", "372", "استونيا"));
        countries.add(new Country("et", "251", "أثيوبيا"));
        countries.add(new Country("fk", "500", "جزر فوكلاند (مالفيناس)"));
        countries.add(new Country("fo", "298", "جزر صناعية"));
        countries.add(new Country("fj", "679", "فيجي"));
        countries.add(new Country("fi", "358", "فنلندا"));
        countries.add(new Country("fr", "33", "فرنسا"));
        countries.add(new Country("pf", "689", "بولينيزيا الفرنسية"));
        countries.add(new Country("ga", "241", "الغابون"));
        countries.add(new Country("gm", "220", "غامبيا"));
        countries.add(new Country("ge", "995", "جورجيا"));
        countries.add(new Country("de", "49", "ألمانيا"));
        countries.add(new Country("gh", "233", "غانا"));
        countries.add(new Country("gi", "350", "جبل طارق"));
        countries.add(new Country("gr", "30", "اليونان"));
        countries.add(new Country("gl", "299", "الأرض الخضراء"));
        countries.add(new Country("gt", "502", "غواتيمالا"));
        countries.add(new Country("gn", "224", "غينيا"));
        countries.add(new Country("gw", "245", "غينيا بيساو"));
        countries.add(new Country("gy", "592", "غيانا"));
        countries.add(new Country("ht", "509", "هايتي"));
        countries.add(new Country("hn", "504", "هندوراس"));
        countries.add(new Country("hk", "852", "هونغ كونغ"));
        countries.add(new Country("hu", "36", "هنغاريا"));
        countries.add(new Country("in", "91", "الهند"));
        countries.add(new Country("id", "62", "أندونيسيا"));
        countries.add(new Country("ir", "98", "إيران، الجمهورية الإسلامية"));
        countries.add(new Country("iq", "964", "العراق"));
        countries.add(new Country("ie", "353", "أيرلندا"));
        countries.add(new Country("im", "44", "جزيرة آيل أوف مان"));
        countries.add(new Country("il", "972", "إسرائيل"));
        countries.add(new Country("it", "39", "إيطاليا"));
        countries.add(new Country("ci", "225", "كوت D & أبوس]؛ ديفوار"));
        countries.add(new Country("jp", "81", "اليابان"));
        countries.add(new Country("jo", "962", "الأردن"));
        countries.add(new Country("kz", "7", "كازاخستان"));
        countries.add(new Country("ke", "254", "كينيا"));
        countries.add(new Country("ki", "686", "كيريباس"));
        countries.add(new Country("kw", "965", "الكويت"));
        countries.add(new Country("kg", "996", "قرغيزستان"));
        countries.add(new Country("la", "856", "جمهورية الكونغو الديمقراطية ثانية؛ لاو & أبوس]"));
        countries.add(new Country("lv", "371", "لاتفيا"));
        countries.add(new Country("lb", "961", "لبنان"));
        countries.add(new Country("ls", "266", "ليسوتو"));
        countries.add(new Country("lr", "231", "ليبيريا"));
        countries.add(new Country("ly", "218", "ليبيا"));
        countries.add(new Country("li", "423", "ليختنشتاين"));
        countries.add(new Country("lt", "370", "ليتوانيا"));
        countries.add(new Country("lu", "352", "لوكسمبورغ"));
        countries.add(new Country("mo", "853", "ماكاو"));
        countries.add(new Country("mk", "389", "مقدونيا، جمهورية يوغوسلافيا السابقة"));
        countries.add(new Country("mg", "261", "مدغشقر"));
        countries.add(new Country("mw", "265", "ملاوي"));
        countries.add(new Country("my", "60", "ماليزيا"));
        countries.add(new Country("mv", "960", "جزر المالديف"));
        countries.add(new Country("ml", "223", "مالي"));
        countries.add(new Country("mt", "356", "مالطا"));
        countries.add(new Country("mh", "692", "جزر مارشال"));
        countries.add(new Country("mr", "222", "موريتانيا"));
        countries.add(new Country("mu", "230", "موريشيوس"));
        countries.add(new Country("yt", "262", "مايوت"));
        countries.add(new Country("mx", "52", "المكسيك"));
        countries.add(new Country("fm", "691", "ولايات ميكرونيزيا الموحدة من"));
        countries.add(new Country("md", "373", "جمهورية مولدوفا"));
        countries.add(new Country("mc", "377", "موناكو"));
        countries.add(new Country("mn", "976", "منغوليا"));
        countries.add(new Country("me", "382", "الجبل الأسود"));
        countries.add(new Country("ma", "212", "المغرب"));
        countries.add(new Country("mz", "258", "موزمبيق"));
        countries.add(new Country("na", "264", "ناميبيا"));
        countries.add(new Country("nr", "674", "ناورو"));
        countries.add(new Country("np", "977", "نيبال"));
        countries.add(new Country("nl", "31", "هولندا"));
        countries.add(new Country("nc", "687", "كاليدونيا الجديدة"));
        countries.add(new Country("nz", "64", "نيوزيلاندا"));
        countries.add(new Country("ni", "505", "نيكاراغوا"));
        countries.add(new Country("ne", "227", "النيجر"));
        countries.add(new Country("ng", "234", "نيجيريا"));
        countries.add(new Country("nu", "683", "نيوي"));
        countries.add(new Country("kp", "850", "كوريا الشعبية الديمقراطية تضمينه في جمهورية"));
        countries.add(new Country("no", "47", "النرويج"));
        countries.add(new Country("om", "968", "سلطنة عمان"));
        countries.add(new Country("pk", "92", "باكستان"));
        countries.add(new Country("pw", "680", "بالاو"));
        countries.add(new Country("pa", "507", "بناما"));
        countries.add(new Country("pg", "675", "بابوا غينيا الجديدة"));
        countries.add(new Country("py", "595", "باراغواي"));
        countries.add(new Country("pe", "51", "بيرو"));
        countries.add(new Country("ph", "63", "الفلبين"));
        countries.add(new Country("pn", "870", "بيتكيرن"));
        countries.add(new Country("pl", "48", "بولندا"));
        countries.add(new Country("pt", "351", "البرتغال"));
        countries.add(new Country("pr", "1", "بورتوريكو"));
        countries.add(new Country("qa", "974", "قطر"));
        countries.add(new Country("ro", "40", "رومانيا"));
        countries.add(new Country("ru", "7", "الفيدرالية الروسية"));
        countries.add(new Country("rw", "250", "رواندا"));
        countries.add(new Country("bl", "590", "سانت بارتيليمي"));
        countries.add(new Country("ws", "685", "ساموا"));
        countries.add(new Country("sm", "378", "سان مارينو"));
        countries.add(new Country("st", "239", "ساو تومي وبرينسيبي"));
        countries.add(new Country("sa", "966", "المملكة العربية السعودية"));
        countries.add(new Country("sn", "221", "السنغال"));
        countries.add(new Country("rs", "381", "صربيا"));
        countries.add(new Country("sc", "248", "سيشيل"));
        countries.add(new Country("sl", "232", "سيرا ليون"));
        countries.add(new Country("sg", "65", "سنغافورة"));
        countries.add(new Country("sk", "421", "سلوفاكيا"));
        countries.add(new Country("si", "386", "سلوفينيا"));
        countries.add(new Country("sb", "677", "جزر سليمان"));
        countries.add(new Country("so", "252", "الصومال"));
        countries.add(new Country("za", "27", "جنوب أفريقيا"));
        countries.add(new Country("kr", "82", "كوريا"));
        countries.add(new Country("es", "34", "إسبانيا"));
        countries.add(new Country("lk", "94", "سيريلانكا"));
        countries.add(new Country("sh", "290", "سانت هيلانة وأسنسيون وتريستان دا كونها"));
        countries.add(new Country("pm", "508", "سان بيار وميكلون"));
        countries.add(new Country("sd", "249", "سودان"));
        countries.add(new Country("sr", "597", "سورينام"));
        countries.add(new Country("sz", "268", "سوازيلاند"));
        countries.add(new Country("se", "46", "السويد"));
        countries.add(new Country("ch", "41", "سويسرا"));
        countries.add(new Country("sy", "963", "الجمهورية العربية السورية"));
        countries.add(new Country("tw", "886", "مقاطعة تايوان الصينية"));
        countries.add(new Country("tj", "992", "طاجيكستان"));
        countries.add(new Country("tz", "255", "جمهورية تنزانيا المتحدة"));
        countries.add(new Country("th", "66", "تايلاند"));
        countries.add(new Country("tg", "228", "توغو"));
        countries.add(new Country("tk", "690", "توكيلاو"));
        countries.add(new Country("to", "676", "تونغا"));
        countries.add(new Country("tn", "216", "تونس"));
        countries.add(new Country("tr", "90", "ديك رومي"));
        countries.add(new Country("tm", "993", "تركمانستان"));
        countries.add(new Country("tv", "688", "توفالو"));
        countries.add(new Country("ae", "971", "الإمارات العربية المتحدة"));
        countries.add(new Country("ug", "256", "أوغندا"));
        countries.add(new Country("gb", "44", "المملكة المتحدة"));
        countries.add(new Country("ua", "380", "أوكرانيا"));
        countries.add(new Country("uy", "598", "أوروغواي"));
        countries.add(new Country("us", "1", "الولايات المتحدة"));
        countries.add(new Country("uz", "998", "أوزبكستان"));
        countries.add(new Country("vu", "678", "فانواتو"));
        countries.add(new Country("va", "39", "الكرسي الرسولي (دولة الفاتيكان)"));
        countries.add(new Country("ve", "58", "فنزويلا، جمهورية البوليفارية"));
        countries.add(new Country("vn", "84", "فيتنام"));
        countries.add(new Country("wf", "681", "واليس وفوتونا"));
        countries.add(new Country("ye", "967", "اليمن"));
        countries.add(new Country("zm", "260", "زامبيا"));
        countries.add(new Country("zw", "263", "زيمبابوي"));
        return countries;
    }

    public static List<Country> getLibraryMasterCountriesPortuguese() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "Afeganistão"));
        countries.add(new Country("al", "355", "Albânia"));
        countries.add(new Country("dz", "213", "Argélia"));
        countries.add(new Country("ad", "376", "Andorra"));
        countries.add(new Country("ao", "244", "Angola"));
        countries.add(new Country("aq", "672", "Antártica"));
        countries.add(new Country("ar", "54", "Argentina"));
        countries.add(new Country("am", "374", "Armênia"));
        countries.add(new Country("aw", "297", "Aruba"));
        countries.add(new Country("au", "61", "Austrália"));
        countries.add(new Country("at", "43", "Áustria"));
        countries.add(new Country("az", "994", "Azerbaijão"));
        countries.add(new Country("bh", "973", "Bahrain"));
        countries.add(new Country("bd", "880", "Bangladesh"));
        countries.add(new Country("by", "375", "Belarus"));
        countries.add(new Country("be", "32", "Bélgica"));
        countries.add(new Country("bz", "501", "Belize"));
        countries.add(new Country("bj", "229", "Benin"));
        countries.add(new Country("bt", "975", "Butão"));
        countries.add(new Country("bo", "591", "Bolívia, Estado Plurinacional da"));
        countries.add(new Country("ba", "387", "Bósnia e Herzegovina"));
        countries.add(new Country("bw", "267", "Botswana"));
        countries.add(new Country("br", "55", "Brasil"));
        countries.add(new Country("bn", "673", "Brunei Darussalam"));
        countries.add(new Country("bg", "359", "Bulgária"));
        countries.add(new Country("bf", "226", "Burkina Faso"));
        countries.add(new Country("mm", "95", "Myanmar"));
        countries.add(new Country("bi", "257", "Burundi"));
        countries.add(new Country("kh", "855", "Camboja"));
        countries.add(new Country("cm", "237", "Camarões"));
        countries.add(new Country("ca", "1", "Canadá"));
        countries.add(new Country("cv", "238", "cabo Verde"));
        countries.add(new Country("cf", "236", "Central Africano República"));
        countries.add(new Country("td", "235", "Chade"));
        countries.add(new Country("cl", "56", "Chile"));
        countries.add(new Country("cn", "86", "China"));
        countries.add(new Country("cx", "61", "Ilha do Natal"));
        countries.add(new Country("cc", "61", "Ilhas Cocos (Keeling)"));
        countries.add(new Country("co", "57", "Colômbia"));
        countries.add(new Country("km", "269", "Comores"));
        countries.add(new Country("cg", "242", "Congo"));
        countries.add(new Country("cd", "243", "Congo, República Democrática do"));
        countries.add(new Country("ck", "682", "Ilhas Cook"));
        countries.add(new Country("cr", "506", "Costa Rica"));
        countries.add(new Country("hr", "385", "Croácia"));
        countries.add(new Country("cu", "53", "Cuba"));
        countries.add(new Country("cy", "357", "Chipre"));
        countries.add(new Country("cz", "420", "República Checa"));
        countries.add(new Country("dk", "45", "Dinamarca"));
        countries.add(new Country("dj", "253", "Djibouti"));
        countries.add(new Country("tl", "670", "Timor-Leste"));
        countries.add(new Country("ec", "593", "Equador"));
        countries.add(new Country("eg", "20", "Egito"));
        countries.add(new Country("sv", "503", "El Salvador"));
        countries.add(new Country("gq", "240", "Guiné Equatorial"));
        countries.add(new Country("er", "291", "Eritrea"));
        countries.add(new Country("ee", "372", "Estônia"));
        countries.add(new Country("et", "251", "Etiópia"));
        countries.add(new Country("fk", "500", "Ilhas Falkland (Malvinas)"));
        countries.add(new Country("fo", "298", "ilhas Faroe"));
        countries.add(new Country("fj", "679", "Fiji"));
        countries.add(new Country("fi", "358", "Finlândia"));
        countries.add(new Country("fr", "33", "França"));
        countries.add(new Country("pf", "689", "Polinésia Francesa"));
        countries.add(new Country("ga", "241", "Gabão"));
        countries.add(new Country("gm", "220", "Gâmbia"));
        countries.add(new Country("ge", "995", "Georgia"));
        countries.add(new Country("de", "49", "Alemanha"));
        countries.add(new Country("gh", "233", "Gana"));
        countries.add(new Country("gi", "350", "Gibraltar"));
        countries.add(new Country("gr", "30", "Grécia"));
        countries.add(new Country("gl", "299", "Groenlândia"));
        countries.add(new Country("gt", "502", "Guatemala"));
        countries.add(new Country("gn", "224", "Guiné"));
        countries.add(new Country("gw", "245", "Guiné-Bissau"));
        countries.add(new Country("gy", "592", "Guiana"));
        countries.add(new Country("ht", "509", "Haiti"));
        countries.add(new Country("hn", "504", "Honduras"));
        countries.add(new Country("hk", "852", "Hong Kong"));
        countries.add(new Country("hu", "36", "Hungria"));
        countries.add(new Country("in", "91", "Índia"));
        countries.add(new Country("id", "62", "Indonésia"));
        countries.add(new Country("ir", "98", "Irão, República Islâmica do"));
        countries.add(new Country("iq", "964", "Iraque"));
        countries.add(new Country("ie", "353", "Irlanda"));
        countries.add(new Country("im", "44", "Isle Of Man"));
        countries.add(new Country("il", "972", "Israel"));
        countries.add(new Country("it", "39", "Itália"));
        countries.add(new Country("ci", "225", "Côte D & apos; ivoire"));
        countries.add(new Country("jp", "81", "Japão"));
        countries.add(new Country("jo", "962", "Jordânia"));
        countries.add(new Country("kz", "7", "Cazaquistão"));
        countries.add(new Country("ke", "254", "Quênia"));
        countries.add(new Country("ki", "686", "Kiribati"));
        countries.add(new Country("kw", "965", "Kuweit"));
        countries.add(new Country("kg", "996", "Quirguistão"));
        countries.add(new Country("la", "856", "República Democrática s; Lao People & apos"));
        countries.add(new Country("lv", "371", "Letônia"));
        countries.add(new Country("lb", "961", "Líbano"));
        countries.add(new Country("ls", "266", "Lesoto"));
        countries.add(new Country("lr", "231", "Libéria"));
        countries.add(new Country("ly", "218", "Líbia"));
        countries.add(new Country("li", "423", "Liechtenstein"));
        countries.add(new Country("lt", "370", "Lituânia"));
        countries.add(new Country("lu", "352", "Luxemburgo"));
        countries.add(new Country("mo", "853", "Macau"));
        countries.add(new Country("mk", "389", "Macedónia, Antiga República Jugoslava da"));
        countries.add(new Country("mg", "261", "Madagáscar"));
        countries.add(new Country("mw", "265", "Malavi"));
        countries.add(new Country("my", "60", "Malásia"));
        countries.add(new Country("mv", "960", "Maldivas"));
        countries.add(new Country("ml", "223", "Mali"));
        countries.add(new Country("mt", "356", "Malta"));
        countries.add(new Country("mh", "692", "Ilhas Marshall"));
        countries.add(new Country("mr", "222", "Mauritânia"));
        countries.add(new Country("mu", "230", "Mauritius"));
        countries.add(new Country("yt", "262", "Mayotte"));
        countries.add(new Country("mx", "52", "México"));
        countries.add(new Country("fm", "691", "Micronésia, Estados Federados da"));
        countries.add(new Country("md", "373", "Moldávia, República da"));
        countries.add(new Country("mc", "377", "Monaco"));
        countries.add(new Country("mn", "976", "Mongólia"));
        countries.add(new Country("me", "382", "Montenegro"));
        countries.add(new Country("ma", "212", "Marrocos"));
        countries.add(new Country("mz", "258", "Moçambique"));
        countries.add(new Country("na", "264", "Namíbia"));
        countries.add(new Country("nr", "674", "Nauru"));
        countries.add(new Country("np", "977", "Nepal"));
        countries.add(new Country("nl", "31", "países Baixos"));
        countries.add(new Country("nc", "687", "Nova Caledônia"));
        countries.add(new Country("nz", "64", "Nova Zelândia"));
        countries.add(new Country("ni", "505", "Nicarágua"));
        countries.add(new Country("ne", "227", "Níger"));
        countries.add(new Country("ng", "234", "Nigéria"));
        countries.add(new Country("nu", "683", "Niue"));
        countries.add(new Country("kp", "850", "República Popular da; Coréia, Pessoas & apos Democrática"));
        countries.add(new Country("no", "47", "Noruega"));
        countries.add(new Country("om", "968", "Omã"));
        countries.add(new Country("pk", "92", "Paquistão"));
        countries.add(new Country("pw", "680", "Palau"));
        countries.add(new Country("pa", "507", "Panamá"));
        countries.add(new Country("pg", "675", "Papua Nova Guiné"));
        countries.add(new Country("py", "595", "Paraguai"));
        countries.add(new Country("pe", "51", "Peru"));
        countries.add(new Country("ph", "63", "Filipinas"));
        countries.add(new Country("pn", "870", "Pitcairn"));
        countries.add(new Country("pl", "48", "Polônia"));
        countries.add(new Country("pt", "351", "Portugal"));
        countries.add(new Country("pr", "1", "Porto Rico"));
        countries.add(new Country("qa", "974", "Catar"));
        countries.add(new Country("ro", "40", "Romênia"));
        countries.add(new Country("ru", "7", "Federação Russa"));
        countries.add(new Country("rw", "250", "Ruanda"));
        countries.add(new Country("bl", "590", "São Bartolomeu"));
        countries.add(new Country("ws", "685", "Samoa"));
        countries.add(new Country("sm", "378", "San Marino"));
        countries.add(new Country("st", "239", "São Tomé e Príncipe"));
        countries.add(new Country("sa", "966", "Arábia Saudita"));
        countries.add(new Country("sn", "221", "Senegal"));
        countries.add(new Country("rs", "381", "Sérvia"));
        countries.add(new Country("sc", "248", "Seychelles"));
        countries.add(new Country("sl", "232", "Serra Leoa"));
        countries.add(new Country("sg", "65", "Cingapura"));
        countries.add(new Country("sk", "421", "Eslováquia"));
        countries.add(new Country("si", "386", "Eslovenia"));
        countries.add(new Country("sb", "677", "Ilhas Salomão"));
        countries.add(new Country("so", "252", "Somália"));
        countries.add(new Country("za", "27", "África do Sul"));
        countries.add(new Country("kr", "82", "Coréia"));
        countries.add(new Country("es", "34", "Espanha"));
        countries.add(new Country("lk", "94", "Sri Lanka"));
        countries.add(new Country("sh", "290", "Santa Helena, Ascensão e Tristão da Cunha"));
        countries.add(new Country("pm", "508", "Saint Pierre e Miquelon"));
        countries.add(new Country("sd", "249", "Sudão"));
        countries.add(new Country("sr", "597", "Suriname"));
        countries.add(new Country("sz", "268", "Suazilândia"));
        countries.add(new Country("se", "46", "Suécia"));
        countries.add(new Country("ch", "41", "Suíça"));
        countries.add(new Country("sy", "963", "República Árabe da Síria"));
        countries.add(new Country("tw", "886", "Taiwan, Província da China"));
        countries.add(new Country("tj", "992", "Tajiquistão"));
        countries.add(new Country("tz", "255", "Tanzânia, República Unida da"));
        countries.add(new Country("th", "66", "Tailândia"));
        countries.add(new Country("tg", "228", "Ir"));
        countries.add(new Country("tk", "690", "Tokelau"));
        countries.add(new Country("to", "676", "Tonga"));
        countries.add(new Country("tn", "216", "Tunísia"));
        countries.add(new Country("tr", "90", "Peru"));
        countries.add(new Country("tm", "993", "Turquemenistão"));
        countries.add(new Country("tv", "688", "Tuvalu"));
        countries.add(new Country("ae", "971", "Emirados Árabes Unidos"));
        countries.add(new Country("ug", "256", "Uganda"));
        countries.add(new Country("gb", "44", "Reino Unido"));
        countries.add(new Country("ua", "380", "Ucrânia"));
        countries.add(new Country("uy", "598", "Uruguai"));
        countries.add(new Country("us", "1", "Estados Unidos"));
        countries.add(new Country("uz", "998", "Uzbequistão"));
        countries.add(new Country("vu", "678", "Vanuatu"));
        countries.add(new Country("va", "39", "Santa Sé (Cidade do Vaticano Estado)"));
        countries.add(new Country("ve", "58", "Venezuela, República Bolivariana da"));
        countries.add(new Country("vn", "84", "Viet Nam"));
        countries.add(new Country("wf", "681", "Wallis e Futuna"));
        countries.add(new Country("ye", "967", "Iémen"));
        countries.add(new Country("zm", "260", "Zâmbia"));
        countries.add(new Country("zw", "263", "Zimbábue"));
        return countries;
    }

    public static List<Country> getLibraryMasterCountriesBengali() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "আফগানিস্তান"));
        countries.add(new Country("al", "355", "আল্বেনিয়া"));
        countries.add(new Country("dz", "213", "আলজেরিয়া"));
        countries.add(new Country("ad", "376", "এ্যান্ডোরা"));
        countries.add(new Country("ao", "244", "অ্যাঙ্গোলা"));
        countries.add(new Country("aq", "672", "আইল অব ম্যান"));
        countries.add(new Country("ar", "54", "আর্জিণ্টিনা"));
        countries.add(new Country("am", "374", "আরমেনিয়া"));
        countries.add(new Country("aw", "297", "আরুবা"));
        countries.add(new Country("au", "61", "অস্ট্রেলিয়া"));
        countries.add(new Country("at", "43", "অস্ট্রিয়া"));
        countries.add(new Country("az", "994", "আজেরবাইজান"));
        countries.add(new Country("bh", "973", "বাহরাইন"));
        countries.add(new Country("bd", "880", "বাংলাদেশ"));
        countries.add(new Country("by", "375", "বেলারুশ"));
        countries.add(new Country("be", "32", "বেলজিয়াম"));
        countries.add(new Country("bz", "501", "বেলিজ"));
        countries.add(new Country("bj", "229", "বেনিন"));
        countries.add(new Country("bt", "975", "ভুটান"));
        countries.add(new Country("bo", "591", "বলিভিয়া, বহুজাতিক রাষ্ট্র"));
        countries.add(new Country("ba", "387", "বসনিয়া ও হার্জেগোভিনা"));
        countries.add(new Country("bw", "267", "বোট্স্বানা"));
        countries.add(new Country("br", "55", "ব্রাজিল"));
        countries.add(new Country("bn", "673", "ব্রুনাই দারুসসালাম"));
        countries.add(new Country("bg", "359", "বুলগেরিয়া"));
        countries.add(new Country("bf", "226", "বুর্কিনা ফাসো"));
        countries.add(new Country("mm", "95", "মায়ানমার"));
        countries.add(new Country("bi", "257", "বুরুন্ডি"));
        countries.add(new Country("kh", "855", "কাম্বোডিয়া"));
        countries.add(new Country("cm", "237", "ক্যামেরুন"));
        countries.add(new Country("ca", "1", "কানাডা"));
        countries.add(new Country("cv", "238", "কেপ ভার্দে"));
        countries.add(new Country("cf", "236", "মধ্য আফ্রিকান প্রজাতন্ত্র"));
        countries.add(new Country("td", "235", "মত্স্যবিশেষ"));
        countries.add(new Country("cl", "56", "চিলি"));
        countries.add(new Country("cn", "86", "চীন"));
        countries.add(new Country("cx", "61", "ক্রিস্টমাস দ্বীপ"));
        countries.add(new Country("cc", "61", "কোকোস (কিলিং) দ্বীপপুঞ্জ"));
        countries.add(new Country("co", "57", "কলোমবিয়া"));
        countries.add(new Country("km", "269", "কমোরোস"));
        countries.add(new Country("cg", "242", "কঙ্গো"));
        countries.add(new Country("cd", "243", "কঙ্গো, গণতান্ত্রিক প্রজাতন্ত্র"));
        countries.add(new Country("ck", "682", "কুক দ্বীপপুঞ্জ"));
        countries.add(new Country("cr", "506", "কোস্টারিকা"));
        countries.add(new Country("hr", "385", "ক্রোয়েশিয়া"));
        countries.add(new Country("cu", "53", "কুবা"));
        countries.add(new Country("cy", "357", "সাইপ্রাসদ্বিপ"));
        countries.add(new Country("cz", "420", "চেক প্রজাতন্ত্র"));
        countries.add(new Country("dk", "45", "ডেন্মার্ক্"));
        countries.add(new Country("dj", "253", "জিবুতি"));
        countries.add(new Country("tl", "670", "টিমর-লেস্টে"));
        countries.add(new Country("ec", "593", "ইকোয়াডর"));
        countries.add(new Country("eg", "20", "মিশর"));
        countries.add(new Country("sv", "503", "এল সালভাদর"));
        countries.add(new Country("gq", "240", "নিরক্ষীয় গিনি"));
        countries.add(new Country("er", "291", "ইরিত্রিয়া"));
        countries.add(new Country("ee", "372", "এস্তোনিয়াদেশ"));
        countries.add(new Country("et", "251", "ইথিওপিয়া"));
        countries.add(new Country("fk", "500", "ফকল্যান্ড দ্বীপপুঞ্জ (মালভিনাস)"));
        countries.add(new Country("fo", "298", "ফারো দ্বীপপুঞ্জ"));
        countries.add(new Country("fj", "679", "ফিজি"));
        countries.add(new Country("fi", "358", "ফিনল্যাণ্ড"));
        countries.add(new Country("fr", "33", "ফ্রান্স"));
        countries.add(new Country("pf", "689", "ফরাসি পলিনেশিয়া"));
        countries.add(new Country("ga", "241", "গাবোনবাদ্যযন্ত্র"));
        countries.add(new Country("gm", "220", "গাম্বিয়াদেশ"));
        countries.add(new Country("ge", "995", "জর্জিয়া"));
        countries.add(new Country("de", "49", "জার্মানি"));
        countries.add(new Country("gh", "233", "ঘানা"));
        countries.add(new Country("gi", "350", "জিব্রালটার"));
        countries.add(new Country("gr", "30", "গ্রীস"));
        countries.add(new Country("gl", "299", "গ্রীনল্যাণ্ড"));
        countries.add(new Country("gt", "502", "গুয়াটেমালা"));
        countries.add(new Country("gn", "224", "গিনি"));
        countries.add(new Country("gw", "245", "গিনি বিসাউ"));
        countries.add(new Country("gy", "592", "গিয়ানা"));
        countries.add(new Country("ht", "509", "হাইতি"));
        countries.add(new Country("hn", "504", "হন্ডুরাস"));
        countries.add(new Country("hk", "852", "হংকং"));
        countries.add(new Country("hu", "36", "হাঙ্গেরি"));
        countries.add(new Country("in", "91", "ভারত"));
        countries.add(new Country("id", "62", "ইন্দোনেশিয়া"));
        countries.add(new Country("ir", "98", "ইরান, ইসলামী প্রজাতন্ত্রের"));
        countries.add(new Country("iq", "964", "ইরাক"));
        countries.add(new Country("ie", "353", "আয়ারল্যাণ্ড"));
        countries.add(new Country("im", "44", "আইল অব ম্যান"));
        countries.add(new Country("il", "972", "ইসরাইল"));
        countries.add(new Country("it", "39", "ইতালি"));
        countries.add(new Country("ci", "225", "কোত ডি সমেত Ivoire"));
        countries.add(new Country("jp", "81", "জাপান"));
        countries.add(new Country("jo", "962", "জর্দান"));
        countries.add(new Country("kz", "7", "কাজাকস্থান"));
        countries.add(new Country("ke", "254", "কেনিয়া"));
        countries.add(new Country("ki", "686", "কিরিবাতি"));
        countries.add(new Country("kw", "965", "কুয়েত"));
        countries.add(new Country("kg", "996", "কিরগিজস্তান"));
        countries.add(new Country("la", "856", "লাও পিপলস সমেত গুলি ডেমোক্রেটিক রিপাবলিক"));
        countries.add(new Country("lv", "371", "ল্যাট্ভিআ"));
        countries.add(new Country("lb", "961", "লেবানন"));
        countries.add(new Country("ls", "266", "লেসোথো"));
        countries.add(new Country("lr", "231", "লাইবেরিয়া"));
        countries.add(new Country("ly", "218", "লিবিয়া"));
        countries.add(new Country("li", "423", "লিচেনস্টেইন"));
        countries.add(new Country("lt", "370", "লিত্ভা"));
        countries.add(new Country("lu", "352", "লাক্সেমবার্গ"));
        countries.add(new Country("mo", "853", "ম্যাকাও"));
        countries.add(new Country("mk", "389", "ম্যাসেডোনিয়া, সাবেক যুগোস্লাভ প্রজাতন্ত্র"));
        countries.add(new Country("mg", "261", "ম্যাডাগ্যাস্কার"));
        countries.add(new Country("mw", "265", "মালাউই"));
        countries.add(new Country("my", "60", "মালয়েশিয়া"));
        countries.add(new Country("mv", "960", "মালদ্বীপ"));
        countries.add(new Country("ml", "223", "মালি"));
        countries.add(new Country("mt", "356", "মালটা"));
        countries.add(new Country("mh", "692", "মার্শাল দ্বীপপুঞ্জ"));
        countries.add(new Country("mr", "222", "মৌরিতানিয়া"));
        countries.add(new Country("mu", "230", "মরিশাস"));
        countries.add(new Country("yt", "262", "মায়োত্তে"));
        countries.add(new Country("mx", "52", "মেক্সিকো"));
        countries.add(new Country("fm", "691", "মাইক্রোনেশিয়া, সংযুক্ত রাষ্ট্র"));
        countries.add(new Country("md", "373", "মোল্দাভিয়া, প্রজাতন্ত্রের"));
        countries.add(new Country("mc", "377", "মোনাকো"));
        countries.add(new Country("mn", "976", "মঙ্গোলিআ"));
        countries.add(new Country("me", "382", "মন্টিনিগ্রো"));
        countries.add(new Country("ma", "212", "মরক্কো"));
        countries.add(new Country("mz", "258", "মোজাম্বিক"));
        countries.add(new Country("na", "264", "নামিবিয়া"));
        countries.add(new Country("nr", "674", "নাউরু"));
        countries.add(new Country("np", "977", "নেপাল"));
        countries.add(new Country("nl", "31", "নেদারল্যান্ডস"));
        countries.add(new Country("nc", "687", "নতুন ক্যালেডোনিয়া"));
        countries.add(new Country("nz", "64", "নিউজিল্যান্ড"));
        countries.add(new Country("ni", "505", "নিক্যার্যাগিউআদেশ"));
        countries.add(new Country("ne", "227", "নাইজারনদী"));
        countries.add(new Country("ng", "234", "নাইজিরিয়াদেশ"));
        countries.add(new Country("nu", "683", "নিউয়ে"));
        countries.add(new Country("kp", "850", "কোরিয়া, গণতান্ত্রিক সমেত গুলি প্রজাতন্ত্র"));
        countries.add(new Country("no", "47", "নরত্তএদেশ"));
        countries.add(new Country("om", "968", "ওমান"));
        countries.add(new Country("pk", "92", "পাকিস্তান"));
        countries.add(new Country("pw", "680", "পালাও"));
        countries.add(new Country("pa", "507", "পানামা"));
        countries.add(new Country("pg", "675", "পাপুয়া নিউ গিনি"));
        countries.add(new Country("py", "595", "প্যারাগুয়ে"));
        countries.add(new Country("pe", "51", "পেরু"));
        countries.add(new Country("ph", "63", "ফিলিপাইন"));
        countries.add(new Country("pn", "870", "পিটকেয়ার্ন"));
        countries.add(new Country("pl", "48", "পোল্যান্ড"));
        countries.add(new Country("pt", "351", "পর্তুগাল"));
        countries.add(new Country("pr", "1", "পুয়ের্তো রিকো"));
        countries.add(new Country("qa", "974", "কাতার"));
        countries.add(new Country("ro", "40", "রুমানিয়া"));
        countries.add(new Country("ru", "7", "রাশিয়ান ফেডারেশন"));
        countries.add(new Country("rw", "250", "রুয়ান্ডা"));
        countries.add(new Country("bl", "590", "সেন্ট বারথেলিমি"));
        countries.add(new Country("ws", "685", "সামোয়া"));
        countries.add(new Country("sm", "378", "সান মারিনো"));
        countries.add(new Country("st", "239", "সাও টোমে এবং প্রিনসিপে"));
        countries.add(new Country("sa", "966", "সৌদি আরব"));
        countries.add(new Country("sn", "221", "সেনেগাল"));
        countries.add(new Country("rs", "381", "সার্বিয়া"));
        countries.add(new Country("sc", "248", "সিসিলি"));
        countries.add(new Country("sl", "232", "সিয়েরা লিওন"));
        countries.add(new Country("sg", "65", "সিঙ্গাপুর"));
        countries.add(new Country("sk", "421", "স্লোভাকিয়া"));
        countries.add(new Country("si", "386", "স্লোভেনিয়া"));
        countries.add(new Country("sb", "677", "সলোমান দ্বীপপুঞ্জ"));
        countries.add(new Country("so", "252", "সোমালিয়া"));
        countries.add(new Country("za", "27", "দক্ষিন আফ্রিকা"));
        countries.add(new Country("kr", "82", "কোরিয়া"));
        countries.add(new Country("es", "34", "স্পেন"));
        countries.add(new Country("lk", "94", "শ্রীলংকা"));
        countries.add(new Country("sh", "290", "সেন্ট হেলেনা, অ্যাসেনশন ও ত্রিস্তান দা কুনহা"));
        countries.add(new Country("pm", "508", "সেন্ট পিয়ের ও মিকুয়েলন"));
        countries.add(new Country("sd", "249", "সুদান"));
        countries.add(new Country("sr", "597", "সুরিনাম"));
        countries.add(new Country("sz", "268", "সোয়াজিল্যান্ড"));
        countries.add(new Country("se", "46", "সুইডেন"));
        countries.add(new Country("ch", "41", "সুইজারল্যান্ড"));
        countries.add(new Country("sy", "963", "সিরিয় আরব প্রজাতন্ত্র"));
        countries.add(new Country("tw", "886", "চীন এর তাইওয়ান, প্রদেশ"));
        countries.add(new Country("tj", "992", "তাজিকিস্তান"));
        countries.add(new Country("tz", "255", "তাঞ্জানিয়া, ইউনাইটেড প্রজাতন্ত্রের"));
        countries.add(new Country("th", "66", "থাইল্যান্ড"));
        countries.add(new Country("tg", "228", "যাও"));
        countries.add(new Country("tk", "690", "টোকেলাউ"));
        countries.add(new Country("to", "676", "টাঙ্গা"));
        countries.add(new Country("tn", "216", "টিউনিস্"));
        countries.add(new Country("tr", "90", "তুরস্ক"));
        countries.add(new Country("tm", "993", "তুর্কমেনিস্তান"));
        countries.add(new Country("tv", "688", "টুভালু"));
        countries.add(new Country("ae", "971", "সংযুক্ত আরব আমিরাত"));
        countries.add(new Country("ug", "256", "উগান্ডা"));
        countries.add(new Country("gb", "44", "যুক্তরাজ্য"));
        countries.add(new Country("ua", "380", "ইউক্রেইন্"));
        countries.add(new Country("uy", "598", "উরুগুয়ে"));
        countries.add(new Country("us", "1", "যুক্তরাষ্ট্র"));
        countries.add(new Country("uz", "998", "উজ্বেকিস্থান"));
        countries.add(new Country("vu", "678", "ভানুয়াতু"));
        countries.add(new Country("va", "39", "হোলি সি (ভ্যাটিকান সিটি)"));
        countries.add(new Country("ve", "58", "ভেনেজুয়েলা, ভার্জিন দ্বীপপুঞ্জের"));
        countries.add(new Country("vn", "84", "ভিয়েতনামে"));
        countries.add(new Country("wf", "681", "ওয়ালিস এবং ফুটুনা"));
        countries.add(new Country("ye", "967", "ইমেন"));
        countries.add(new Country("zm", "260", "জাম্বিয়া"));
        countries.add(new Country("zw", "263", "জিম্বাবুয়ে"));
        return countries;
    }

    public static List<Country> getLibraryMasterCountriesRussian() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "Афганистан"));
        countries.add(new Country("al", "355", "Албания"));
        countries.add(new Country("dz", "213", "Алжир"));
        countries.add(new Country("ad", "376", "андорра"));
        countries.add(new Country("ao", "244", "Ангола"));
        countries.add(new Country("aq", "672", "Антарктида"));
        countries.add(new Country("ar", "54", "Аргентина"));
        countries.add(new Country("am", "374", "Армения"));
        countries.add(new Country("aw", "297", "Аруба"));
        countries.add(new Country("au", "61", "Австралия"));
        countries.add(new Country("at", "43", "Австрия"));
        countries.add(new Country("az", "994", "Азербайджан"));
        countries.add(new Country("bh", "973", "Бахрейн"));
        countries.add(new Country("bd", "880", "Бангладеш"));
        countries.add(new Country("by", "375", "Беларусь"));
        countries.add(new Country("be", "32", "Бельгия"));
        countries.add(new Country("bz", "501", "Белиз"));
        countries.add(new Country("bj", "229", "Бенин"));
        countries.add(new Country("bt", "975", "Бутан"));
        countries.add(new Country("bo", "591", "Боливия, Многонациональное Государство"));
        countries.add(new Country("ba", "387", "Босния и Герцеговина"));
        countries.add(new Country("bw", "267", "Ботсвана"));
        countries.add(new Country("br", "55", "Бразилия"));
        countries.add(new Country("bn", "673", "Бруней-Даруссалам"));
        countries.add(new Country("bg", "359", "Болгария"));
        countries.add(new Country("bf", "226", "Буркина-Фасо"));
        countries.add(new Country("mm", "95", "Мьянма"));
        countries.add(new Country("bi", "257", "Бурунди"));
        countries.add(new Country("kh", "855", "Камбоджа"));
        countries.add(new Country("cm", "237", "Камерун"));
        countries.add(new Country("ca", "1", "Канада"));
        countries.add(new Country("cv", "238", "Кабо-Верде"));
        countries.add(new Country("cf", "236", "Центрально-Африканская Республика"));
        countries.add(new Country("td", "235", "Чад"));
        countries.add(new Country("cl", "56", "Чили"));
        countries.add(new Country("cn", "86", "Китай"));
        countries.add(new Country("cx", "61", "Остров Рождества"));
        countries.add(new Country("cc", "61", "Кокосовые (Килинг) острова"));
        countries.add(new Country("co", "57", "Колумбия"));
        countries.add(new Country("km", "269", "Коморские острова"));
        countries.add(new Country("cg", "242", "Конго"));
        countries.add(new Country("cd", "243", "Конго, Демократическая Республика"));
        countries.add(new Country("ck", "682", "Острова Кука"));
        countries.add(new Country("cr", "506", "Коста-Рика"));
        countries.add(new Country("hr", "385", "Хорватия"));
        countries.add(new Country("cu", "53", "Куба"));
        countries.add(new Country("cy", "357", "Кипр"));
        countries.add(new Country("cz", "420", "Чешская Республика"));
        countries.add(new Country("dk", "45", "Дания"));
        countries.add(new Country("dj", "253", "Джибути"));
        countries.add(new Country("tl", "670", "Тимор-Лешти"));
        countries.add(new Country("ec", "593", "Эквадор"));
        countries.add(new Country("eg", "20", "Египет"));
        countries.add(new Country("sv", "503", "Сальвадор"));
        countries.add(new Country("gq", "240", "Экваториальная Гвинея"));
        countries.add(new Country("er", "291", "Эритрея"));
        countries.add(new Country("ee", "372", "Эстония"));
        countries.add(new Country("et", "251", "Эфиопия"));
        countries.add(new Country("fk", "500", "Фолклендские (Мальвинские)"));
        countries.add(new Country("fo", "298", "Фарерские острова"));
        countries.add(new Country("fj", "679", "Фиджи"));
        countries.add(new Country("fi", "358", "Финляндия"));
        countries.add(new Country("fr", "33", "Франция"));
        countries.add(new Country("pf", "689", "Французская Полинезия"));
        countries.add(new Country("ga", "241", "Габон"));
        countries.add(new Country("gm", "220", "Гамбия"));
        countries.add(new Country("ge", "995", "Грузия"));
        countries.add(new Country("de", "49", "Германия"));
        countries.add(new Country("gh", "233", "Гана"));
        countries.add(new Country("gi", "350", "Гибралтар"));
        countries.add(new Country("gr", "30", "Греция"));
        countries.add(new Country("gl", "299", "Гренландия"));
        countries.add(new Country("gt", "502", "Гватемала"));
        countries.add(new Country("gn", "224", "Гвинея"));
        countries.add(new Country("gw", "245", "Гвинея-Бисау"));
        countries.add(new Country("gy", "592", "Гайана"));
        countries.add(new Country("ht", "509", "Гаити"));
        countries.add(new Country("hn", "504", "Гондурас"));
        countries.add(new Country("hk", "852", "Гонконг"));
        countries.add(new Country("hu", "36", "Венгрия"));
        countries.add(new Country("in", "91", "Индия"));
        countries.add(new Country("id", "62", "Индонезия"));
        countries.add(new Country("ir", "98", "Иран, Исламская Республика"));
        countries.add(new Country("iq", "964", "Ирак"));
        countries.add(new Country("ie", "353", "Ирландия"));
        countries.add(new Country("im", "44", "Остров Мэн"));
        countries.add(new Country("il", "972", "Израиль"));
        countries.add(new Country("it", "39", "Италия"));
        countries.add(new Country("ci", "225", "Кот D & APOS; Ивуар"));
        countries.add(new Country("jp", "81", "Япония"));
        countries.add(new Country("jo", "962", "Иордания"));
        countries.add(new Country("kz", "7", "Казахстан"));
        countries.add(new Country("ke", "254", "Кения"));
        countries.add(new Country("ki", "686", "Кирибати"));
        countries.add(new Country("kw", "965", "Кувейт"));
        countries.add(new Country("kg", "996", "Киргизия"));
        countries.add(new Country("la", "856", "Демократическая Республика с; Lao People & ина"));
        countries.add(new Country("lv", "371", "Латвия"));
        countries.add(new Country("lb", "961", "Ливан"));
        countries.add(new Country("ls", "266", "Лесото"));
        countries.add(new Country("lr", "231", "Либерия"));
        countries.add(new Country("ly", "218", "Ливия"));
        countries.add(new Country("li", "423", "Лихтенштейн"));
        countries.add(new Country("lt", "370", "Литва"));
        countries.add(new Country("lu", "352", "Люксембург"));
        countries.add(new Country("mo", "853", "Macao"));
        countries.add(new Country("mk", "389", "Бывшая югославская Республика Македония,"));
        countries.add(new Country("mg", "261", "Мадагаскар"));
        countries.add(new Country("mw", "265", "Малави"));
        countries.add(new Country("my", "60", "Малайзия"));
        countries.add(new Country("mv", "960", "Мальдивы"));
        countries.add(new Country("ml", "223", "Мали"));
        countries.add(new Country("mt", "356", "Мальта"));
        countries.add(new Country("mh", "692", "Маршалловы острова"));
        countries.add(new Country("mr", "222", "Мавритания"));
        countries.add(new Country("mu", "230", "Маврикий"));
        countries.add(new Country("yt", "262", "Майотта"));
        countries.add(new Country("mx", "52", "Мексика"));
        countries.add(new Country("fm", "691", "Микронезия, Федеративные Штаты"));
        countries.add(new Country("md", "373", "Молдова"));
        countries.add(new Country("mc", "377", "Монако"));
        countries.add(new Country("mn", "976", "Монголия"));
        countries.add(new Country("me", "382", "Черногория"));
        countries.add(new Country("ma", "212", "Марокко"));
        countries.add(new Country("mz", "258", "Мозамбик"));
        countries.add(new Country("na", "264", "Намибия"));
        countries.add(new Country("nr", "674", "Науру"));
        countries.add(new Country("np", "977", "Непал"));
        countries.add(new Country("nl", "31", "Нидерланды"));
        countries.add(new Country("nc", "687", "Новая Каледония"));
        countries.add(new Country("nz", "64", "Новая Зеландия"));
        countries.add(new Country("ni", "505", "Никарагуа"));
        countries.add(new Country("ne", "227", "Нигер"));
        countries.add(new Country("ng", "234", "Нигерия"));
        countries.add(new Country("nu", "683", "Niue"));
        countries.add(new Country("kp", "850", "Корейская Народно-Демократическая & ина Республика"));
        countries.add(new Country("no", "47", "Норвегия"));
        countries.add(new Country("om", "968", "Оман"));
        countries.add(new Country("pk", "92", "Пакистан"));
        countries.add(new Country("pw", "680", "Palau"));
        countries.add(new Country("pa", "507", "Панама"));
        countries.add(new Country("pg", "675", "Папуа - Новая Гвинея"));
        countries.add(new Country("py", "595", "Парагвай"));
        countries.add(new Country("pe", "51", "Перу"));
        countries.add(new Country("ph", "63", "Филиппины"));
        countries.add(new Country("pn", "870", "Питкэрн"));
        countries.add(new Country("pl", "48", "Польша"));
        countries.add(new Country("pt", "351", "Португалия"));
        countries.add(new Country("pr", "1", "Пуэрто-Рико"));
        countries.add(new Country("qa", "974", "Катар"));
        countries.add(new Country("ro", "40", "Румыния"));
        countries.add(new Country("ru", "7", "Российская Федерация"));
        countries.add(new Country("rw", "250", "Руанда"));
        countries.add(new Country("bl", "590", "Сен-Бартельми"));
        countries.add(new Country("ws", "685", "Самоа"));
        countries.add(new Country("sm", "378", "Сан - Марино"));
        countries.add(new Country("st", "239", "Сан-Томе и Принсипи"));
        countries.add(new Country("sa", "966", "Саудовская Аравия"));
        countries.add(new Country("sn", "221", "Сенегал"));
        countries.add(new Country("rs", "381", "Сербия"));
        countries.add(new Country("sc", "248", "Сейшельские острова"));
        countries.add(new Country("sl", "232", "Сьерра-Леоне"));
        countries.add(new Country("sg", "65", "Сингапур"));
        countries.add(new Country("sk", "421", "Словакия"));
        countries.add(new Country("si", "386", "Словения"));
        countries.add(new Country("sb", "677", "Соломоновы острова"));
        countries.add(new Country("so", "252", "Сомали"));
        countries.add(new Country("za", "27", "Южная Африка"));
        countries.add(new Country("kr", "82", "Корея"));
        countries.add(new Country("es", "34", "Испания"));
        countries.add(new Country("lk", "94", "Шри Ланка"));
        countries.add(new Country("sh", "290", "Святой Елены, Вознесения и Тристан-да-Кунья"));
        countries.add(new Country("pm", "508", "Сен-Пьер и Микелон"));
        countries.add(new Country("sd", "249", "Судан"));
        countries.add(new Country("sr", "597", "Суринам"));
        countries.add(new Country("sz", "268", "Свазиленд"));
        countries.add(new Country("se", "46", "Швеция"));
        countries.add(new Country("ch", "41", "Швейцария"));
        countries.add(new Country("sy", "963", "Сирийская Арабская Республика"));
        countries.add(new Country("tw", "886", "Тайвань, провинция Китая"));
        countries.add(new Country("tj", "992", "Таджикистан"));
        countries.add(new Country("tz", "255", "Танзания, Объединенная Республика"));
        countries.add(new Country("th", "66", "Таиланд"));
        countries.add(new Country("tg", "228", "Идти"));
        countries.add(new Country("tk", "690", "Токелау"));
        countries.add(new Country("to", "676", "Тонга"));
        countries.add(new Country("tn", "216", "Тунис"));
        countries.add(new Country("tr", "90", "Турция"));
        countries.add(new Country("tm", "993", "Туркменистан"));
        countries.add(new Country("tv", "688", "Тувалу"));
        countries.add(new Country("ae", "971", "Объединенные Арабские Эмираты"));
        countries.add(new Country("ug", "256", "Уганда"));
        countries.add(new Country("gb", "44", "Великобритания"));
        countries.add(new Country("ua", "380", "Украина"));
        countries.add(new Country("uy", "598", "Уругвай"));
        countries.add(new Country("us", "1", "Соединенные Штаты"));
        countries.add(new Country("uz", "998", "Узбекистан"));
        countries.add(new Country("vu", "678", "Вануату"));
        countries.add(new Country("va", "39", "Святейший Престол (Ватикан)"));
        countries.add(new Country("ve", "58", "Венесуэла, Боливарианская Республика"));
        countries.add(new Country("vn", "84", "Вьетнам"));
        countries.add(new Country("wf", "681", "Уоллис и Футуна"));
        countries.add(new Country("ye", "967", "Йемен"));
        countries.add(new Country("zm", "260", "Замбия"));
        countries.add(new Country("zw", "263", "Зимбабве"));
        return countries;
    }

    public static List<Country> getLibraryMasterCountriesJavanese() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "Afghanistan"));
        countries.add(new Country("al", "355", "Albania"));
        countries.add(new Country("dz", "213", "Algeria"));
        countries.add(new Country("ad", "376", "Andorra"));
        countries.add(new Country("ao", "244", "Angola"));
        countries.add(new Country("aq", "672", "Antartika"));
        countries.add(new Country("ar", "54", "Argentina"));
        countries.add(new Country("am", "374", "Armenia"));
        countries.add(new Country("aw", "297", "Aruba"));
        countries.add(new Country("au", "61", "Australia"));
        countries.add(new Country("at", "43", "Austria"));
        countries.add(new Country("az", "994", "Azerbaijan"));
        countries.add(new Country("bh", "973", "Bahrain"));
        countries.add(new Country("bd", "880", "Bangladesh"));
        countries.add(new Country("by", "375", "Belarus"));
        countries.add(new Country("be", "32", "Belgium"));
        countries.add(new Country("bz", "501", "Belize"));
        countries.add(new Country("bj", "229", "Benin"));
        countries.add(new Country("bt", "975", "Bhutan"));
        countries.add(new Country("bo", "591", "Bolivia, State Plurinational Of"));
        countries.add(new Country("ba", "387", "Bosnia Herzegovina"));
        countries.add(new Country("bw", "267", "Botswana"));
        countries.add(new Country("br", "55", "Brazil"));
        countries.add(new Country("bn", "673", "Brunei Darussalam"));
        countries.add(new Country("bg", "359", "Bulgaria"));
        countries.add(new Country("bf", "226", "Burkina Faso"));
        countries.add(new Country("mm", "95", "Myanmar"));
        countries.add(new Country("bi", "257", "Burundi"));
        countries.add(new Country("kh", "855", "Cambodia"));
        countries.add(new Country("cm", "237", "Kamerun"));
        countries.add(new Country("ca", "1", "Canada"));
        countries.add(new Country("cv", "238", "Cape Verde"));
        countries.add(new Country("cf", "236", "Republik Afrika Tengah"));
        countries.add(new Country("td", "235", "Chad"));
        countries.add(new Country("cl", "56", "Chile"));
        countries.add(new Country("cn", "86", "China"));
        countries.add(new Country("cx", "61", "natal Island"));
        countries.add(new Country("cc", "61", "Cocos (Keeling) Islands"));
        countries.add(new Country("co", "57", "Colombia"));
        countries.add(new Country("km", "269", "Comoros"));
        countries.add(new Country("cg", "242", "Congo"));
        countries.add(new Country("cd", "243", "Congo, Republik Demokratik Of The"));
        countries.add(new Country("ck", "682", "Kepulauan Cook"));
        countries.add(new Country("cr", "506", "Costa Rica"));
        countries.add(new Country("hr", "385", "Croatia"));
        countries.add(new Country("cu", "53", "Cuba"));
        countries.add(new Country("cy", "357", "Cyprus"));
        countries.add(new Country("cz", "420", "Czech Republic"));
        countries.add(new Country("dk", "45", "Denmark"));
        countries.add(new Country("dj", "253", "Djibouti"));
        countries.add(new Country("tl", "670", "Timor-Leste"));
        countries.add(new Country("ec", "593", "Ecuador"));
        countries.add(new Country("eg", "20", "Mesir"));
        countries.add(new Country("sv", "503", "El Salvador"));
        countries.add(new Country("gq", "240", "Equatorial Guinea"));
        countries.add(new Country("er", "291", "Eritrea"));
        countries.add(new Country("ee", "372", "Estonia"));
        countries.add(new Country("et", "251", "Ethiopia"));
        countries.add(new Country("fk", "500", "Kepulauan Falkland (Malvinas)"));
        countries.add(new Country("fo", "298", "Kepulauan Faroe"));
        countries.add(new Country("fj", "679", "Fiji"));
        countries.add(new Country("fi", "358", "Finland"));
        countries.add(new Country("fr", "33", "France"));
        countries.add(new Country("pf", "689", "French Polynesia"));
        countries.add(new Country("ga", "241", "Gabon"));
        countries.add(new Country("gm", "220", "Gambia"));
        countries.add(new Country("ge", "995", "Georgia"));
        countries.add(new Country("de", "49", "Jerman"));
        countries.add(new Country("gh", "233", "Ghana"));
        countries.add(new Country("gi", "350", "Gibraltar"));
        countries.add(new Country("gr", "30", "Yunani"));
        countries.add(new Country("gl", "299", "Greenland"));
        countries.add(new Country("gt", "502", "Guatemala"));
        countries.add(new Country("gn", "224", "Guinea"));
        countries.add(new Country("gw", "245", "Guinea-Bissau"));
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
        countries.add(new Country("im", "44", "Isle of Man"));
        countries.add(new Country("il", "972", "Israel"));
        countries.add(new Country("it", "39", "Italia"));
        countries.add(new Country("ci", "225", "Côte D & apos; Gading"));
        countries.add(new Country("jp", "81", "Japan"));
        countries.add(new Country("jo", "962", "Jordan"));
        countries.add(new Country("kz", "7", "Kazakhstan"));
        countries.add(new Country("ke", "254", "Kenya"));
        countries.add(new Country("ki", "686", "Kiribati"));
        countries.add(new Country("kw", "965", "Kuwait"));
        countries.add(new Country("kg", "996", "Kyrgyzstan"));
        countries.add(new Country("la", "856", "Republik Demokratik s; Lao People & apos"));
        countries.add(new Country("lv", "371", "Latvia"));
        countries.add(new Country("lb", "961", "Lebanon"));
        countries.add(new Country("ls", "266", "Lesotho"));
        countries.add(new Country("lr", "231", "Liberia"));
        countries.add(new Country("ly", "218", "Libya"));
        countries.add(new Country("li", "423", "Liechtenstein"));
        countries.add(new Country("lt", "370", "Lithuania"));
        countries.add(new Country("lu", "352", "Luxembourg"));
        countries.add(new Country("mo", "853", "Macao"));
        countries.add(new Country("mk", "389", "Macedonia, The Mantan Yugoslavia Republic Of"));
        countries.add(new Country("mg", "261", "Madagaskar"));
        countries.add(new Country("mw", "265", "Malawi"));
        countries.add(new Country("my", "60", "Malaysia"));
        countries.add(new Country("mv", "960", "Maldives"));
        countries.add(new Country("ml", "223", "Mali"));
        countries.add(new Country("mt", "356", "Malta"));
        countries.add(new Country("mh", "692", "Kepulauan Marshall"));
        countries.add(new Country("mr", "222", "Mauritania"));
        countries.add(new Country("mu", "230", "Mauritius"));
        countries.add(new Country("yt", "262", "Mayotte"));
        countries.add(new Country("mx", "52", "Mexico"));
        countries.add(new Country("fm", "691", "Micronesia, Federated States Of"));
        countries.add(new Country("md", "373", "Moldova, Republic Of"));
        countries.add(new Country("mc", "377", "Monaco"));
        countries.add(new Country("mn", "976", "Mongolia"));
        countries.add(new Country("me", "382", "Montenegro"));
        countries.add(new Country("ma", "212", "Morocco"));
        countries.add(new Country("mz", "258", "Mozambique"));
        countries.add(new Country("na", "264", "Namibia"));
        countries.add(new Country("nr", "674", "Nauru"));
        countries.add(new Country("np", "977", "Nepal"));
        countries.add(new Country("nl", "31", "Walanda"));
        countries.add(new Country("nc", "687", "Caledonia New"));
        countries.add(new Country("nz", "64", "New Zealand"));
        countries.add(new Country("ni", "505", "Nicaragua"));
        countries.add(new Country("ne", "227", "Niger"));
        countries.add(new Country("ng", "234", "Nigeria"));
        countries.add(new Country("nu", "683", "Niue"));
        countries.add(new Country("kp", "850", "Korea, Wong Demokratik & apos; s Republic Of"));
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
        countries.add(new Country("ro", "40", "Romania"));
        countries.add(new Country("ru", "7", "Russian Federation"));
        countries.add(new Country("rw", "250", "Rwanda"));
        countries.add(new Country("bl", "590", "Saint Barthélemy"));
        countries.add(new Country("ws", "685", "Samoa"));
        countries.add(new Country("sm", "378", "San Marino"));
        countries.add(new Country("st", "239", "Sao Tome lan Principe"));
        countries.add(new Country("sa", "966", "Saudi Arabia"));
        countries.add(new Country("sn", "221", "Senegal"));
        countries.add(new Country("rs", "381", "Serbia"));
        countries.add(new Country("sc", "248", "Seychelles"));
        countries.add(new Country("sl", "232", "Sierra Leone"));
        countries.add(new Country("sg", "65", "Singapore"));
        countries.add(new Country("sk", "421", "Slovakia"));
        countries.add(new Country("si", "386", "Slovenia"));
        countries.add(new Country("sb", "677", "Kepulauan Solomon"));
        countries.add(new Country("so", "252", "Somalia"));
        countries.add(new Country("za", "27", "Afrika Kidul"));
        countries.add(new Country("kr", "82", "Korea"));
        countries.add(new Country("es", "34", "Spanyol"));
        countries.add(new Country("lk", "94", "Sri Lanka"));
        countries.add(new Country("sh", "290", "Saint Helena, Ascension Lan Tristan da Cunha"));
        countries.add(new Country("pm", "508", "Saint Pierre lan Miquelon"));
        countries.add(new Country("sd", "249", "Sudan"));
        countries.add(new Country("sr", "597", "Suriname"));
        countries.add(new Country("sz", "268", "Swaziland"));
        countries.add(new Country("se", "46", "Swedia"));
        countries.add(new Country("ch", "41", "Swiss"));
        countries.add(new Country("sy", "963", "Suriah"));
        countries.add(new Country("tw", "886", "Taiwan, Langkawi Of China"));
        countries.add(new Country("tj", "992", "Tajikistan"));
        countries.add(new Country("tz", "255", "Tansania Of"));
        countries.add(new Country("th", "66", "Thailand"));
        countries.add(new Country("tg", "228", "Togo"));
        countries.add(new Country("tk", "690", "Tokelau"));
        countries.add(new Country("to", "676", "Tonga"));
        countries.add(new Country("tn", "216", "Tunisia"));
        countries.add(new Country("tr", "90", "Turkey"));
        countries.add(new Country("tm", "993", "Turkmenistan"));
        countries.add(new Country("tv", "688", "Tuvalu"));
        countries.add(new Country("ae", "971", "Uni Emirat Arab"));
        countries.add(new Country("ug", "256", "Uganda"));
        countries.add(new Country("gb", "44", "Inggris"));
        countries.add(new Country("ua", "380", "Ukraina"));
        countries.add(new Country("uy", "598", "Uruguay"));
        countries.add(new Country("us", "1", "Amerika Serikat"));
        countries.add(new Country("uz", "998", "Uzbekistan"));
        countries.add(new Country("vu", "678", "Vanuatu"));
        countries.add(new Country("va", "39", "Tahta Suci (Vatikan Negara Bagean)"));
        countries.add(new Country("ve", "58", "Venezuela, Bolivarian Republik"));
        countries.add(new Country("vn", "84", "Viet Nam"));
        countries.add(new Country("wf", "681", "Wallis lan Futuna"));
        countries.add(new Country("ye", "967", "Yaman"));
        countries.add(new Country("zm", "260", "Zambia"));
        countries.add(new Country("zw", "263", "Zimbabwe"));
        return countries;
    }

    public static List<Country> getLibraryMasterCountriesGujarati() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country("af", "93", "અફઘાનિસ્તાન"));
        countries.add(new Country("al", "355", "અલ્બેનિયા"));
        countries.add(new Country("dz", "213", "અલજીર્યા"));
        countries.add(new Country("ad", "376", "ઍંડોરા"));
        countries.add(new Country("ao", "244", "અંગોલા"));
        countries.add(new Country("aq", "672", "એન્ટાર્કટિકા"));
        countries.add(new Country("ar", "54", "અર્જેન્ટીના"));
        countries.add(new Country("am", "374", "આર્મેનિયા"));
        countries.add(new Country("aw", "297", "અરુબા"));
        countries.add(new Country("au", "61", "ઓસ્ટ્રેલિયા"));
        countries.add(new Country("at", "43", "ઓસ્ટ્રિયા"));
        countries.add(new Country("az", "994", "અઝરબૈજાન"));
        countries.add(new Country("bh", "973", "બેહરીન"));
        countries.add(new Country("bd", "880", "બાંગ્લાદેશ"));
        countries.add(new Country("by", "375", "બેલારુસ"));
        countries.add(new Country("be", "32", "બેલ્જીયમ"));
        countries.add(new Country("bz", "501", "બેલીઝ"));
        countries.add(new Country("bj", "229", "બેનિન"));
        countries.add(new Country("bt", "975", "ભુતાન"));
        countries.add(new Country("bo", "591", "બોલિવિયા, પ્લુરિનેશનલ સ્ટેટ"));
        countries.add(new Country("ba", "387", "બોસ્નિયા અને હર્ઝેગોવિના"));
        countries.add(new Country("bw", "267", "બોત્સ્વાના"));
        countries.add(new Country("br", "55", "બ્રાઝીલ"));
        countries.add(new Country("bn", "673", "બ્રુનેઇ દારુસલામ"));
        countries.add(new Country("bg", "359", "બલ્ગેરિયા"));
        countries.add(new Country("bf", "226", "બુર્કિના ફાસો"));
        countries.add(new Country("mm", "95", "મ્યાનમાર"));
        countries.add(new Country("bi", "257", "બરુન્ડી"));
        countries.add(new Country("kh", "855", "કંબોડિયા"));
        countries.add(new Country("cm", "237", "કૅમરૂન"));
        countries.add(new Country("ca", "1", "કેનેડા"));
        countries.add(new Country("cv", "238", "કેપ વર્દ"));
        countries.add(new Country("cf", "236", "સેન્ટ્રલ આફ્રિકન રિપબ્લિક"));
        countries.add(new Country("td", "235", "ચાડ"));
        countries.add(new Country("cl", "56", "ચીલી"));
        countries.add(new Country("cn", "86", "ચાઇના"));
        countries.add(new Country("cx", "61", "ક્રિસ્મસ આઇલેન્ડ"));
        countries.add(new Country("cc", "61", "કોકોસ (કીલીંગ) આઇલેન્ડ"));
        countries.add(new Country("co", "57", "કોલંબિયા"));
        countries.add(new Country("km", "269", "કોમોરોસ"));
        countries.add(new Country("cg", "242", "કોંગો"));
        countries.add(new Country("cd", "243", "કોંગો, ડેમોક્રેટિક રિપબ્લિક ઓફ"));
        countries.add(new Country("ck", "682", "કુક આઇલેન્ડ"));
        countries.add(new Country("cr", "506", "કોસ્ટા રિકા"));
        countries.add(new Country("hr", "385", "ક્રોએશિયા"));
        countries.add(new Country("cu", "53", "ક્યુબા"));
        countries.add(new Country("cy", "357", "સાયપ્રસ"));
        countries.add(new Country("cz", "420", "ઝેક રીપબ્લીક"));
        countries.add(new Country("dk", "45", "ડેનમાર્ક"));
        countries.add(new Country("dj", "253", "જીબુટી"));
        countries.add(new Country("tl", "670", "પૂર્વ તિમોર"));
        countries.add(new Country("ec", "593", "એક્વાડોર"));
        countries.add(new Country("eg", "20", "ઇજીપ્ટ"));
        countries.add(new Country("sv", "503", "એલ સાલ્વાડોર"));
        countries.add(new Country("gq", "240", "ઈક્વેટોરિયલ ગિની"));
        countries.add(new Country("er", "291", "એરિટ્રિયા"));
        countries.add(new Country("ee", "372", "એસ્ટોનિયા"));
        countries.add(new Country("et", "251", "ઇથોપિયા"));
        countries.add(new Country("fk", "500", "ફોકલેન્ડ આઇલેન્ડ (માલવિનસ)"));
        countries.add(new Country("fo", "298", "ફેરો આઇલેન્ડ"));
        countries.add(new Country("fj", "679", "ફીજી"));
        countries.add(new Country("fi", "358", "ફિનલેન્ડ"));
        countries.add(new Country("fr", "33", "ફ્રાન્સ"));
        countries.add(new Country("pf", "689", "ફ્રેન્ચ પોલીનેશિયા"));
        countries.add(new Country("ga", "241", "ગાબોન"));
        countries.add(new Country("gm", "220", "ગેમ્બિયા"));
        countries.add(new Country("ge", "995", "જ્યોર્જિયા"));
        countries.add(new Country("de", "49", "જર્મની"));
        countries.add(new Country("gh", "233", "ઘાના"));
        countries.add(new Country("gi", "350", "જીબ્રાલ્ટર"));
        countries.add(new Country("gr", "30", "ગ્રીસ"));
        countries.add(new Country("gl", "299", "ગ્રીનલેન્ડ"));
        countries.add(new Country("gt", "502", "ગ્વાટેમાલા"));
        countries.add(new Country("gn", "224", "ગિની"));
        countries.add(new Country("gw", "245", "ગિની-બિસ્સાઉ"));
        countries.add(new Country("gy", "592", "ગયાના"));
        countries.add(new Country("ht", "509", "હૈતી"));
        countries.add(new Country("hn", "504", "હોન્ડુરાસ"));
        countries.add(new Country("hk", "852", "હોંગ કોંગ"));
        countries.add(new Country("hu", "36", "હંગેરી"));
        countries.add(new Country("in", "91", "ભારત"));
        countries.add(new Country("id", "62", "ઇન્ડોનેશિયા"));
        countries.add(new Country("ir", "98", "ઈરાન, ઇસ્લામિક રિપબ્લિક"));
        countries.add(new Country("iq", "964", "ઇરાક"));
        countries.add(new Country("ie", "353", "આયર્લેન્ડ"));
        countries.add(new Country("im", "44", "આઇલ ઓફ માં"));
        countries.add(new Country("il", "972", "ઇઝરાયેલ"));
        countries.add(new Country("it", "39", "ઇટાલી"));
        countries.add(new Country("ci", "225", "કોટ ડી સાથેની આઈવોર"));
        countries.add(new Country("jp", "81", "જાપાન"));
        countries.add(new Country("jo", "962", "જોર્ડન"));
        countries.add(new Country("kz", "7", "કઝાકિસ્તાન"));
        countries.add(new Country("ke", "254", "કેન્યા"));
        countries.add(new Country("ki", "686", "કિરીબાટી"));
        countries.add(new Country("kw", "965", "કુવૈત"));
        countries.add(new Country("kg", "996", "કીર્ઘીસ્તાન"));
        countries.add(new Country("la", "856", "લાઓ પીપલ્સ સાથેની ઓ ડેમોક્રેટિક રિપબ્લિક"));
        countries.add(new Country("lv", "371", "લેતવિયા"));
        countries.add(new Country("lb", "961", "લેબનોન"));
        countries.add(new Country("ls", "266", "લેસોથો"));
        countries.add(new Country("lr", "231", "લાઇબેરીયા"));
        countries.add(new Country("ly", "218", "લિબિયા"));
        countries.add(new Country("li", "423", "લૈચટેંસ્ટેઇન"));
        countries.add(new Country("lt", "370", "લીથુનીયા"));
        countries.add(new Country("lu", "352", "લક્ઝમબર્ગ"));
        countries.add(new Country("mo", "853", "મકાઓ"));
        countries.add(new Country("mk", "389", "મેસેડોનિયા, ભૂતપૂર્વ યુગોસ્લાવ રિપબ્લિક"));
        countries.add(new Country("mg", "261", "મેડાગાસ્કર"));
        countries.add(new Country("mw", "265", "મલાવી"));
        countries.add(new Country("my", "60", "મલેશિયા"));
        countries.add(new Country("mv", "960", "માલદીવ"));
        countries.add(new Country("ml", "223", "માલી"));
        countries.add(new Country("mt", "356", "માલ્ટા"));
        countries.add(new Country("mh", "692", "માર્શલ આઈલેન્ડ"));
        countries.add(new Country("mr", "222", "મૌરિટાનિયા"));
        countries.add(new Country("mu", "230", "મોરિશિયસ"));
        countries.add(new Country("yt", "262", "માયોટી"));
        countries.add(new Country("mx", "52", "મેક્સિકો"));
        countries.add(new Country("fm", "691", "માઇક્રોનેશિયા, ઓફ ફેડરેટેડ સ્ટેટ્સ"));
        countries.add(new Country("md", "373", "મોલ્ડોવા, રિપબ્લિક ઓફ"));
        countries.add(new Country("mc", "377", "મોનાકો"));
        countries.add(new Country("mn", "976", "મંગોલિયા"));
        countries.add(new Country("me", "382", "મોન્ટેનેગ્રો"));
        countries.add(new Country("ma", "212", "મોરોક્કો"));
        countries.add(new Country("mz", "258", "મોઝામ્બિક"));
        countries.add(new Country("na", "264", "નામિબિયા"));
        countries.add(new Country("nr", "674", "નાઉરૂ"));
        countries.add(new Country("np", "977", "નેપાળ"));
        countries.add(new Country("nl", "31", "નેધરલેન્ડ"));
        countries.add(new Country("nc", "687", "ન્યુ કેલેડોનીયા"));
        countries.add(new Country("nz", "64", "ન્યૂઝીલેન્ડ"));
        countries.add(new Country("ni", "505", "નિકારાગુઆ"));
        countries.add(new Country("ne", "227", "નાઇજર"));
        countries.add(new Country("ng", "234", "નાઇજીરીયા"));
        countries.add(new Country("nu", "683", "નિયુએ"));
        countries.add(new Country("kp", "850", "કોરિયા, ડેમોક્રેટિક પીપલ્સ સાથેની એસ રિપબ્લિક"));
        countries.add(new Country("no", "47", "નોર્વે"));
        countries.add(new Country("om", "968", "ઓમાન"));
        countries.add(new Country("pk", "92", "પાકિસ્તાન"));
        countries.add(new Country("pw", "680", "પલાઉ"));
        countries.add(new Country("pa", "507", "પનામા"));
        countries.add(new Country("pg", "675", "પપુઆ ન્યુ ગીની"));
        countries.add(new Country("py", "595", "પેરાગ્વે"));
        countries.add(new Country("pe", "51", "પેરુ"));
        countries.add(new Country("ph", "63", "ફિલિપાઇન્સ"));
        countries.add(new Country("pn", "870", "પીટકૈર્ન"));
        countries.add(new Country("pl", "48", "પોલેન્ડ"));
        countries.add(new Country("pt", "351", "પોર્ટુગલ"));
        countries.add(new Country("pr", "1", "પ્યુઅર્ટો રિકો"));
        countries.add(new Country("qa", "974", "કતાર"));
        countries.add(new Country("ro", "40", "રોમાનિયા"));
        countries.add(new Country("ru", "7", "રશિયન ફેડરેશન"));
        countries.add(new Country("rw", "250", "રવાન્ડા"));
        countries.add(new Country("bl", "590", "સેન્ટ બાર્થેલેમી"));
        countries.add(new Country("ws", "685", "સમોઆ"));
        countries.add(new Country("sm", "378", "સૅન મેરિનો"));
        countries.add(new Country("st", "239", "સાઓ ટોમ એન્ડ પ્રિન્સાઇપ"));
        countries.add(new Country("sa", "966", "સાઉદી અરેબિયા"));
        countries.add(new Country("sn", "221", "સેનેગલ"));
        countries.add(new Country("rs", "381", "સર્બિયા"));
        countries.add(new Country("sc", "248", "સીશલ્સ"));
        countries.add(new Country("sl", "232", "સીયેરા લીયોન"));
        countries.add(new Country("sg", "65", "સિંગાપુર"));
        countries.add(new Country("sk", "421", "સ્લોવેકિયા"));
        countries.add(new Country("si", "386", "સાઇપ્રસ"));
        countries.add(new Country("sb", "677", "સોલોમન આઇલેન્ડ"));
        countries.add(new Country("so", "252", "સોમાલિયા"));
        countries.add(new Country("za", "27", "દક્ષિણ આફ્રિકા"));
        countries.add(new Country("kr", "82", "કોરિયા"));
        countries.add(new Country("es", "34", "સ્પેઇન"));
        countries.add(new Country("lk", "94", "શ્રિલંકા"));
        countries.add(new Country("sh", "290", "સેન્ટ હેલેના, એસેન્શન અને ટ્રીસ્ટન દા કુન્હા"));
        countries.add(new Country("pm", "508", "સેન્ટ પીઅર એન્ડ મીક્વેલન"));
        countries.add(new Country("sd", "249", "સુદાન"));
        countries.add(new Country("sr", "597", "સુરીનામ"));
        countries.add(new Country("sz", "268", "સ્વાઝીલેન્ડ"));
        countries.add(new Country("se", "46", "સ્વીડન"));
        countries.add(new Country("ch", "41", "સ્વિટ્ઝર્લૅન્ડ"));
        countries.add(new Country("sy", "963", "સીરીયન આરબ રીપબ્લીક"));
        countries.add(new Country("tw", "886", "તાઇવાન, ચાઇના પ્રાંત"));
        countries.add(new Country("tj", "992", "તજીકીસ્તાન"));
        countries.add(new Country("tz", "255", "તાંઝાનિયા, યુનાઇટેડ રીપબ્લિક"));
        countries.add(new Country("th", "66", "થાઇલેન્ડ"));
        countries.add(new Country("tg", "228", "જાઓ"));
        countries.add(new Country("tk", "690", "તોકેલાઉ"));
        countries.add(new Country("to", "676", "ટોંગા"));
        countries.add(new Country("tn", "216", "ટ્યુનિશિયા"));
        countries.add(new Country("tr", "90", "તુર્કી"));
        countries.add(new Country("tm", "993", "તુર્કમેનિસ્તાન"));
        countries.add(new Country("tv", "688", "તુવાલુ"));
        countries.add(new Country("ae", "971", "સંયુક્ત આરબ અમીરાત"));
        countries.add(new Country("ug", "256", "યુગાન્ડા"));
        countries.add(new Country("gb", "44", "યુનાઇટેડ કિંગડમ"));
        countries.add(new Country("ua", "380", "યુક્રેન"));
        countries.add(new Country("uy", "598", "ઉરુગ્વે"));
        countries.add(new Country("us", "1", "યુનાઈટેડ સ્ટેટ્સ"));
        countries.add(new Country("uz", "998", "ઉઝબેકિસ્તાન"));
        countries.add(new Country("vu", "678", "વેનૌતા"));
        countries.add(new Country("va", "39", "હોલી સી (વેટીકન સીટી સ્ટેટ)"));
        countries.add(new Country("ve", "58", "વેનેઝુએલા, બોલિવેરિઅન રિપબ્લિક"));
        countries.add(new Country("vn", "84", "વેઇત નામ"));
        countries.add(new Country("wf", "681", "વેલીસ એન્ડ ફ્યુટુના"));
        countries.add(new Country("ye", "967", "યેમેન"));
        countries.add(new Country("zm", "260", "ઝામ્બિયા"));
        countries.add(new Country("zw", "263", "ઝિમ્બાબ્વે"));
        return countries;
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
        return getName().toLowerCase().contains(query) || getNameCode().toLowerCase().contains(query) || getPhoneCode().toLowerCase().contains(query);
    }
}
