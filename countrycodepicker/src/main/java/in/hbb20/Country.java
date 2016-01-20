package in.hbb20;

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
    static String TAG="Class Country";
    String nameCode;
    String phoneCode;
    String name;

    public Country() {

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

    public Country(String nameCode, String phoneCode, String name) {
        this.nameCode = nameCode;
        this.phoneCode = phoneCode;
        this.name = name;
    }

    public void log(){
        try{
            Log.d(TAG,"Country->" +nameCode + ":" + phoneCode + ":" + name);
        }catch (NullPointerException ex){
            Log.d(TAG, "Null");
        }
    }


    /**
     * This function parses the raw/countries.xml file, and get list of all the countries.
     * @param context: required to access application resources (where country.xml is).
     * @return List of all the countries available in xml file.
     */
    public static List<Country> getAllCountries(Context context){
        List<Country> countries=new ArrayList<Country>();
        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlFactoryObject.newPullParser();
            InputStream ins = context.getResources().openRawResource(R.raw.countries);
            xmlPullParser.setInput(ins, null);
            int event = xmlPullParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)
            {
                String name=xmlPullParser.getName();
                switch (event){
                    case XmlPullParser.START_TAG:
                        break;
                    case XmlPullParser.END_TAG:
                        if(name.equals("country")){
                            Country country=new Country();
                            country.setNameCode(xmlPullParser.getAttributeValue(null,"code").toUpperCase());
                            country.setPhoneCode(xmlPullParser.getAttributeValue(null,"phoneCode"));
                            country.setName(xmlPullParser.getAttributeValue(null,"name"));
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
     * @param context to access raw file of countries list.
     * @param code phone code. i.e "91" or "1"
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     */
    private static Country getCountryForCode(Context context, String code){
        List<Country> countries=Country.getAllCountries(context);
        for(Country country:countries){
            if(country.getPhoneCode().equals(code)){
                return country;
            }
        }
        return null;
    }

    /**
     * Search a country which matches @param code.
     * @param context to access raw file of countries list.
     * @param code phone code. i.e 91 or 1
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     */
    static Country getCountryForCode(Context context, int code){
        return getCountryForCode(context,code+"");
    }

    /**
     * Finds country code by matching substring from left to right from full number.
     * For example. if full number is +819017901357
     * function will ignore "+" and try to find match for first character "8"
     * if any country found for code "8", will return that country. If not, then it will
     * try to find country for "81". and so on till first 3 characters ( maximum number of characters in country code is 3).
     * @param context to access resource file of
     * @param fullNumber full number ( "+" (optional)+ country code + carrier number) i.e. +819017901357 / 819017901357 / 918866667722
     * @return Country JP +81(Japan) for +819017901357 or 819017901357
     * Country IN +91(India) for  918866667722
     * null for 2956635321 ( as neither of "2", "29" and "295" matches any country code)
     */
    static Country getCountryForNumber(Context context,String fullNumber) {
        int firstDigit;
        if(fullNumber.length()!=0) {
            if (fullNumber.charAt(0) == '+') {
                firstDigit = 1;
            } else {
                firstDigit = 0;
            }
            Country country = null;
            for (int i = firstDigit; i < firstDigit + 4; i++) {
                String code = fullNumber.substring(firstDigit, i);
                country = Country.getCountryForCode(context, code);
                if (country != null) {
                    return country;
                }
            }
        }
        return null;
    }

    public String logString() {
        return nameCode.toUpperCase()+" +"+phoneCode+"("+name+")";
    }
}

