package com.hbb20;

import android.content.Context;
import android.util.SparseArray;

import java.util.HashMap;
import java.util.Map;

/**
 * This all those countries that shares the same country code but can be differentiated based on area code
 */
public class CCPCountryGroup {
    private static SparseArray<CCPCountryGroup> countryGroups = null;
    String defaultNameCode;
    int areaCodeLength;
    private HashMap<String, String> nameCodeToAreaCodesMap;

    private CCPCountryGroup(String defaultNameCode, int areaCodeLength, HashMap<String, String> nameCodeToAreaCodesMap) {
        this.defaultNameCode = defaultNameCode;
        this.areaCodeLength = areaCodeLength;
        this.nameCodeToAreaCodesMap = nameCodeToAreaCodesMap;
    }

    private static void initializeGroups() {

        countryGroups = new SparseArray<>();

        //create group for +1
        addGroupForPhoneCode1();

        //create group for +44
        addGroupForPhoneCode44();

        //create group for +358
        addGroupForPhoneCode358();
    }

    private static void addGroupForPhoneCode358() {
        HashMap<String, String> nameCodeToAreaCodes = new HashMap<>();
        nameCodeToAreaCodes.put("ax", "18"); //Aland Islands
        countryGroups.put(358, new CCPCountryGroup("fi", 2, nameCodeToAreaCodes)); // Finland
    }

    /**
     * +44 group
     */
    private static void addGroupForPhoneCode44() {
        HashMap<String, String> nameCodeToAreaCodes = new HashMap<>();
        nameCodeToAreaCodes.put("gg", "1481"); //Guernsey
        nameCodeToAreaCodes.put("im", "1624"); //ISLE_OF_MAN
        nameCodeToAreaCodes.put("je", "1534"); //Jersey
        countryGroups.put(44, new CCPCountryGroup("gb", 4, nameCodeToAreaCodes)); // UK
    }


    /**
     * NANP countries (+1)
     */
    private static void addGroupForPhoneCode1() {
        HashMap<String, String> nameCodeToAreaCodes = new HashMap<>();
        nameCodeToAreaCodes.put("ag", "268"); //ANTIGUA_AND_BARBUDA_AREA_CODES
        nameCodeToAreaCodes.put("ai", "264"); //ANGUILLA_AREA_CODES
        nameCodeToAreaCodes.put("as", "684"); //American Samoa
        nameCodeToAreaCodes.put("bb", "246"); //BARBADOS_AREA_CODES
        nameCodeToAreaCodes.put("bm", "441"); //BERMUDA_AREA_CODES
        nameCodeToAreaCodes.put("bs", "242"); //BAHAMAS_AREA_CODES
        nameCodeToAreaCodes.put("ca", "204/226/236/249/250/289/306/343/365/403/416/418/431/437/438/450/506/514/519/579/581/587/600/601/604/613/639/647/705/709/769/778/780/782/807/819/825/867/873/902/905/"); //CANADA_AREA_CODES
        nameCodeToAreaCodes.put("dm", "767"); //DOMINICA_AREA_CODES
        nameCodeToAreaCodes.put("do", "809/829/849"); //DOMINICAN_REPUBLIC_AREA_CODES
        nameCodeToAreaCodes.put("gd", "473"); //GRENADA_AREA_CODES
        nameCodeToAreaCodes.put("gu", "671"); //Guam
        nameCodeToAreaCodes.put("jm", "876"); //JAMAICA_AREA_CODES
        nameCodeToAreaCodes.put("kn", "869"); //SAINT_KITTS_AND_NEVIS_AREA_CODES
        nameCodeToAreaCodes.put("ky", "345"); //CAYMAN_ISLANDS_AREA_CODES
        nameCodeToAreaCodes.put("lc", "758"); //SAINT_LUCIA_AREA_CODES
        nameCodeToAreaCodes.put("mp", "670"); //Northern Mariana Islands
        nameCodeToAreaCodes.put("ms", "664"); //MONTSERRAT_AREA_CODES
        nameCodeToAreaCodes.put("pr", "787"); //PUERTO_RICO_AREA_CODES
        nameCodeToAreaCodes.put("sx", "721"); //SINT_MAARTEN_AREA_CODES
        nameCodeToAreaCodes.put("tc", "649"); //TURKS_AND_CAICOS_ISLANDS_AREA_CODES
        nameCodeToAreaCodes.put("tt", "868"); //TRINIDAD_AND_TOBAGO_AREA_CODES
        nameCodeToAreaCodes.put("vc", "784"); //SAINT_VINCENT_AND_THE_GRENADINES_AREA_CODES
        nameCodeToAreaCodes.put("vg", "284"); //BRITISH_VIRGIN_ISLANDS_AREA_CODES
        nameCodeToAreaCodes.put("vi", "340"); //US_VIRGIN_ISLANDS_AREA_CODES
        countryGroups.put(1, new CCPCountryGroup("us", 3, nameCodeToAreaCodes)); // USA
    }

    public static CCPCountryGroup getCountryGroupForPhoneCode(int countryCode) {
        if (countryGroups == null) {
            initializeGroups();
        }
        return countryGroups.get(countryCode);
    }

    /**
     * Go though nameCodeToAreaCodesMap entries to find name code of country.
     *
     * @param context
     * @param language
     * @param areaCode for which we are looking for country
     * @return country that matches areaCode. If no country matched, returns default country.
     */
    public CCPCountry getCountryForAreaCode(Context context, CountryCodePicker.Language language, String areaCode) {
        String nameCode = defaultNameCode;
        for (Map.Entry<String, String> entry : nameCodeToAreaCodesMap.entrySet()) {
            if (entry.getValue().contains(areaCode)) {
                nameCode = entry.getKey();
            }
        }
        return CCPCountry.getCountryForNameCodeFromLibraryMasterList(context, language, nameCode);
    }
}
