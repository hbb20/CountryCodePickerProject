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
    public static List<Country> readXMLofCountries(Context context){
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
     * @param preferredCountries is list of preference countries.
     * @param code phone code. i.e "91" or "1"
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     * if same code (e.g. +1) available for more than one country ( US, canada) , this function will return preferred country.
     */
    private static Country getCountryForCode(List<Country> preferredCountries, String code){

        /**
         * check in preferred countries
         */
        if(preferredCountries!=null && !preferredCountries.isEmpty()) {
            for (Country country : preferredCountries) {
                if (country.getPhoneCode().equals(code)) {
                    return country;
                }
            }
        }

        for(Country country:getMasterCountries()){
            if(country.getPhoneCode().equals(code)){
                return country;
            }
        }
        return null;
    }

    /**
     * Search a country which matches @param nameCode.
     * @param nameCode country name code. i.e US or us or Au. See countries.xml for all code names.
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     */
    public static Country getCountryForNameCode(String nameCode){
        List<Country> countries=Country.getMasterCountries();
        for(Country country:countries){
            if(country.getNameCode().equalsIgnoreCase(nameCode)){
                return country;
            }
        }
        return null;
    }

    /**
     * Search a country which matches @param code.
     * @param preferredCountries list of country with priority,
     * @param code phone code. i.e 91 or 1
     * @return Country that has phone code as @param code.
     * or returns null if no country matches given code.
     */
    static Country getCountryForCode(List<Country> preferredCountries, int code){
        return getCountryForCode(preferredCountries, code + "");
    }
    /**
     * Finds country code by matching substring from left to right from full number.
     * For example. if full number is +819017901357
     * function will ignore "+" and try to find match for first character "8"
     * if any country found for code "8", will return that country. If not, then it will
     * try to find country for "81". and so on till first 3 characters ( maximum number of characters in country code is 3).
     * @param preferredCountries countries of preference
     * @param fullNumber full number ( "+" (optional)+ country code + carrier number) i.e. +819017901357 / 819017901357 / 918866667722
     * @return Country JP +81(Japan) for +819017901357 or 819017901357
     * Country IN +91(India) for  918866667722
     * null for 2956635321 ( as neither of "2", "29" and "295" matches any country code)
     */
    static Country getCountryForNumber(List<Country> preferredCountries,String fullNumber) {
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
                country = Country.getCountryForCode(preferredCountries, code);
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

    /**
     * This will return all the countries. No preference is manages.
     * Anytime new country need to be added, add it
     * @return
     */
    public static List<Country> getMasterCountries(){
        List<Country> countries=new ArrayList<>();
        countries.add(new Country("af","93","Afghanistan"));
        countries.add(new Country("al","355","Albania"));
        countries.add(new Country("dz","213","Algeria"));
        countries.add(new Country("ad","376","Andorra"));
        countries.add(new Country("ao","244","Angola"));
        countries.add(new Country("aq","672","Antarctica"));
        countries.add(new Country("ar","54","Argentina"));
        countries.add(new Country("am","374","Armenia"));
        countries.add(new Country("aw","297","Aruba"));
        countries.add(new Country("au","61","Australia"));
        countries.add(new Country("at","43","Austria"));
        countries.add(new Country("az","994","Azerbaijan"));
        countries.add(new Country("bh","973","Bahrain"));
        countries.add(new Country("bd","880","Bangladesh"));
        countries.add(new Country("by","375","Belarus"));
        countries.add(new Country("be","32","Belgium"));
        countries.add(new Country("bz","501","Belize"));
        countries.add(new Country("bj","229","Benin"));
        countries.add(new Country("bt","975","Bhutan"));
        countries.add(new Country("bo","591","Bolivia, Plurinational State Of"));
        countries.add(new Country("ba","387","Bosnia And Herzegovina"));
        countries.add(new Country("bw","267","Botswana"));
        countries.add(new Country("br","55","Brazil"));
        countries.add(new Country("bn","673","Brunei Darussalam"));
        countries.add(new Country("bg","359","Bulgaria"));
        countries.add(new Country("bf","226","Burkina Faso"));
        countries.add(new Country("mm","95","Myanmar"));
        countries.add(new Country("bi","257","Burundi"));
        countries.add(new Country("kh","855","Cambodia"));
        countries.add(new Country("cm","237","Cameroon"));
        countries.add(new Country("ca","1","Canada"));
        countries.add(new Country("cv","238","Cape Verde"));
        countries.add(new Country("cf","236","Central African Republic"));
        countries.add(new Country("td","235","Chad"));
        countries.add(new Country("cl","56","Chile"));
        countries.add(new Country("cn","86","China"));
        countries.add(new Country("cx","61","Christmas Island"));
        countries.add(new Country("cc","61","Cocos (keeling) Islands"));
        countries.add(new Country("co","57","Colombia"));
        countries.add(new Country("km","269","Comoros"));
        countries.add(new Country("cg","242","Congo"));
        countries.add(new Country("cd","243","Congo, The Democratic Republic Of The"));
        countries.add(new Country("ck","682","Cook Islands"));
        countries.add(new Country("cr","506","Costa Rica"));
        countries.add(new Country("hr","385","Croatia"));
        countries.add(new Country("cu","53","Cuba"));
        countries.add(new Country("cy","357","Cyprus"));
        countries.add(new Country("cz","420","Czech Republic"));
        countries.add(new Country("dk","45","Denmark"));
        countries.add(new Country("dj","253","Djibouti"));
        countries.add(new Country("tl","670","Timor-leste"));
        countries.add(new Country("ec","593","Ecuador"));
        countries.add(new Country("eg","20","Egypt"));
        countries.add(new Country("sv","503","El Salvador"));
        countries.add(new Country("gq","240","Equatorial Guinea"));
        countries.add(new Country("er","291","Eritrea"));
        countries.add(new Country("ee","372","Estonia"));
        countries.add(new Country("et","251","Ethiopia"));
        countries.add(new Country("fk","500","Falkland Islands (malvinas)"));
        countries.add(new Country("fo","298","Faroe Islands"));
        countries.add(new Country("fj","679","Fiji"));
        countries.add(new Country("fi","358","Finland"));
        countries.add(new Country("fr","33","France"));
        countries.add(new Country("pf","689","French Polynesia"));
        countries.add(new Country("ga","241","Gabon"));
        countries.add(new Country("gm","220","Gambia"));
        countries.add(new Country("ge","995","Georgia"));
        countries.add(new Country("de","49","Germany"));
        countries.add(new Country("gh","233","Ghana"));
        countries.add(new Country("gi","350","Gibraltar"));
        countries.add(new Country("gr","30","Greece"));
        countries.add(new Country("gl","299","Greenland"));
        countries.add(new Country("gt","502","Guatemala"));
        countries.add(new Country("gn","224","Guinea"));
        countries.add(new Country("gw","245","Guinea-bissau"));
        countries.add(new Country("gy","592","Guyana"));
        countries.add(new Country("ht","509","Haiti"));
        countries.add(new Country("hn","504","Honduras"));
        countries.add(new Country("hk","852","Hong Kong"));
        countries.add(new Country("hu","36","Hungary"));
        countries.add(new Country("in","91","India"));
        countries.add(new Country("id","62","Indonesia"));
        countries.add(new Country("ir","98","Iran, Islamic Republic Of"));
        countries.add(new Country("iq","964","Iraq"));
        countries.add(new Country("ie","353","Ireland"));
        countries.add(new Country("im","44","Isle Of Man"));
        countries.add(new Country("il","972","Israel"));
        countries.add(new Country("it","39","Italy"));
        countries.add(new Country("ci","225","Côte D&apos;ivoire"));
        countries.add(new Country("jp","81","Japan"));
        countries.add(new Country("jo","962","Jordan"));
        countries.add(new Country("kz","7","Kazakhstan"));
        countries.add(new Country("ke","254","Kenya"));
        countries.add(new Country("ki","686","Kiribati"));
        countries.add(new Country("kw","965","Kuwait"));
        countries.add(new Country("kg","996","Kyrgyzstan"));
        countries.add(new Country("la","856","Lao People&apos;s Democratic Republic"));
        countries.add(new Country("lv","371","Latvia"));
        countries.add(new Country("lb","961","Lebanon"));
        countries.add(new Country("ls","266","Lesotho"));
        countries.add(new Country("lr","231","Liberia"));
        countries.add(new Country("ly","218","Libya"));
        countries.add(new Country("li","423","Liechtenstein"));
        countries.add(new Country("lt","370","Lithuania"));
        countries.add(new Country("lu","352","Luxembourg"));
        countries.add(new Country("mo","853","Macao"));
        countries.add(new Country("mk","389","Macedonia, The Former Yugoslav Republic Of"));
        countries.add(new Country("mg","261","Madagascar"));
        countries.add(new Country("mw","265","Malawi"));
        countries.add(new Country("my","60","Malaysia"));
        countries.add(new Country("mv","960","Maldives"));
        countries.add(new Country("ml","223","Mali"));
        countries.add(new Country("mt","356","Malta"));
        countries.add(new Country("mh","692","Marshall Islands"));
        countries.add(new Country("mr","222","Mauritania"));
        countries.add(new Country("mu","230","Mauritius"));
        countries.add(new Country("yt","262","Mayotte"));
        countries.add(new Country("mx","52","Mexico"));
        countries.add(new Country("fm","691","Micronesia, Federated States Of"));
        countries.add(new Country("md","373","Moldova, Republic Of"));
        countries.add(new Country("mc","377","Monaco"));
        countries.add(new Country("mn","976","Mongolia"));
        countries.add(new Country("me","382","Montenegro"));
        countries.add(new Country("ma","212","Morocco"));
        countries.add(new Country("mz","258","Mozambique"));
        countries.add(new Country("na","264","Namibia"));
        countries.add(new Country("nr","674","Nauru"));
        countries.add(new Country("np","977","Nepal"));
        countries.add(new Country("nl","31","Netherlands"));
        countries.add(new Country("nc","687","New Caledonia"));
        countries.add(new Country("nz","64","New Zealand"));
        countries.add(new Country("ni","505","Nicaragua"));
        countries.add(new Country("ne","227","Niger"));
        countries.add(new Country("ng","234","Nigeria"));
        countries.add(new Country("nu","683","Niue"));
        countries.add(new Country("kp","850","Korea, Democratic People&apos;s Republic Of"));
        countries.add(new Country("no","47","Norway"));
        countries.add(new Country("om","968","Oman"));
        countries.add(new Country("pk","92","Pakistan"));
        countries.add(new Country("pw","680","Palau"));
        countries.add(new Country("pa","507","Panama"));
        countries.add(new Country("pg","675","Papua New Guinea"));
        countries.add(new Country("py","595","Paraguay"));
        countries.add(new Country("pe","51","Peru"));
        countries.add(new Country("ph","63","Philippines"));
        countries.add(new Country("pn","870","Pitcairn"));
        countries.add(new Country("pl","48","Poland"));
        countries.add(new Country("pt","351","Portugal"));
        countries.add(new Country("pr","1","Puerto Rico"));
        countries.add(new Country("qa","974","Qatar"));
        countries.add(new Country("ro","40","Romania"));
        countries.add(new Country("ru","7","Russian Federation"));
        countries.add(new Country("rw","250","Rwanda"));
        countries.add(new Country("bl","590","Saint Barthélemy"));
        countries.add(new Country("ws","685","Samoa"));
        countries.add(new Country("sm","378","San Marino"));
        countries.add(new Country("st","239","Sao Tome And Principe"));
        countries.add(new Country("sa","966","Saudi Arabia"));
        countries.add(new Country("sn","221","Senegal"));
        countries.add(new Country("rs","381","Serbia"));
        countries.add(new Country("sc","248","Seychelles"));
        countries.add(new Country("sl","232","Sierra Leone"));
        countries.add(new Country("sg","65","Singapore"));
        countries.add(new Country("sk","421","Slovakia"));
        countries.add(new Country("si","386","Slovenia"));
        countries.add(new Country("sb","677","Solomon Islands"));
        countries.add(new Country("so","252","Somalia"));
        countries.add(new Country("za","27","South Africa"));
        countries.add(new Country("kr","82","Korea, Republic Of"));
        countries.add(new Country("es","34","Spain"));
        countries.add(new Country("lk","94","Sri Lanka"));
        countries.add(new Country("sh","290","Saint Helena, Ascension And Tristan Da Cunha"));
        countries.add(new Country("pm","508","Saint Pierre And Miquelon"));
        countries.add(new Country("sd","249","Sudan"));
        countries.add(new Country("sr","597","Suriname"));
        countries.add(new Country("sz","268","Swaziland"));
        countries.add(new Country("se","46","Sweden"));
        countries.add(new Country("ch","41","Switzerland"));
        countries.add(new Country("sy","963","Syrian Arab Republic"));
        countries.add(new Country("tw","886","Taiwan, Province Of China"));
        countries.add(new Country("tj","992","Tajikistan"));
        countries.add(new Country("tz","255","Tanzania, United Republic Of"));
        countries.add(new Country("th","66","Thailand"));
        countries.add(new Country("tg","228","Togo"));
        countries.add(new Country("tk","690","Tokelau"));
        countries.add(new Country("to","676","Tonga"));
        countries.add(new Country("tn","216","Tunisia"));
        countries.add(new Country("tr","90","Turkey"));
        countries.add(new Country("tm","993","Turkmenistan"));
        countries.add(new Country("tv","688","Tuvalu"));
        countries.add(new Country("ae","971","United Arab Emirates"));
        countries.add(new Country("ug","256","Uganda"));
        countries.add(new Country("gb","44","United Kingdom"));
        countries.add(new Country("ua","380","Ukraine"));
        countries.add(new Country("uy","598","Uruguay"));
        countries.add(new Country("us","1","United States"));
        countries.add(new Country("uz","998","Uzbekistan"));
        countries.add(new Country("vu","678","Vanuatu"));
        countries.add(new Country("va","39","Holy See (vatican City State)"));
        countries.add(new Country("ve","58","Venezuela, Bolivarian Republic Of"));
        countries.add(new Country("vn","84","Viet Nam"));
        countries.add(new Country("wf","681","Wallis And Futuna"));
        countries.add(new Country("ye","967","Yemen"));
        countries.add(new Country("zm","260","Zambia"));
        countries.add(new Country("zw","263","Zimbabwe"));
        return countries;
    }

    public boolean isEligibleForQuery(String query) {
        query=query.toLowerCase();
        if (getName().toLowerCase().contains(query) || getNameCode().toLowerCase().contains(query) || getPhoneCode().toLowerCase().contains(query)){
            return true;
        }else {
            return false;
        }
    }
}
