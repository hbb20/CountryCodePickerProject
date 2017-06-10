package com.hbb20;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hbb20 on 10/6/17.
 */

public class CountyListPrinter {
    static HashMap<String, String> masterHashMap;

    public static void main(String[] args) {
        masterHashMap = new HashMap<>();
        loadInitialMap();
        addHasFlagBool();
        addCountryNamesFromList(Country.getLibraryMasterCountriesEnglish());
        addCountryNamesFromList(Country.getLibraryMasterCountriesHindi());
        addCountryNamesFromList(Country.getLibraryMasterCountriesFrench());
        addCountryNamesFromList(Country.getLibraryMasterCountriesGerman());
        addCountryNamesFromList(Country.getLibraryMasterCountriesJapanese());
        addCountryNamesFromList(Country.getLibraryMasterCountriesSpanish());
        addCountryNamesFromList(Country.getLibraryMasterCountriesSimplifiedChinese());
        addCountryNamesFromList(Country.getLibraryMasterCountriesTraditionalChinese());
        addCountryNamesFromList(Country.getLibraryMasterCountriesArabic());
        addCountryNamesFromList(Country.getLibraryMasterCountriesPortuguese());
        addCountryNamesFromList(Country.getLibraryMasterCountriesBengali());
        addCountryNamesFromList(Country.getLibraryMasterCountriesRussian());
        addCountryNamesFromList(Country.getLibraryMasterCountriesIndonesia());
        addCountryNamesFromList(Country.getLibraryMasterCountriesKorean());
        addCountryNamesFromList(Country.getLibraryMasterCountriesGujarati());
        addCountryNamesFromList(Country.getLibraryMasterCountriesHebrew());
        printFinalHashMap();
    }

    private static void addHasFlagBool() {
        for (String key : masterHashMap.keySet()) {
            masterHashMap.put(key, masterHashMap.get(key) + countryHasFlag(key) + "|");
        }
    }

    private static boolean countryHasFlag(String nameCode) {
        Country country = new Country();
        country.setNameCode(nameCode);
        return Country.getFlagResID(country) != -1;
    }

    private static void addCountryNamesFromList(List<Country> targetList) {
        for (String key : masterHashMap.keySet()) {
            masterHashMap.put(key, masterHashMap.get(key) + getPrintableCountryNameFromListForNameCode(key, targetList) + "|");
        }
    }

    private static String getPrintableCountryNameFromListForNameCode(String key, List<Country> targetList) {
        for (Country country : targetList) {
            if (country.getNameCode().toLowerCase().equals(key.toLowerCase())) {
                return country.getName();
            }
        }
        return "~~~~";
    }

    private static void printFinalHashMap() {
        for (String key : masterHashMap.keySet()) {
            System.out.println("country|" + key + "|" + masterHashMap.get(key));

        }
    }

    private static void loadInitialMap() {
        for (Country country : Country.getLibraryMasterCountriesEnglish()) {
            masterHashMap.put(country.getNameCode(), country.getPhoneCode() + "|");
        }
    }
}
