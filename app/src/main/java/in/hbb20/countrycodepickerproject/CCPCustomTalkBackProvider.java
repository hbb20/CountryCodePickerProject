package in.hbb20.countrycodepickerproject;

import com.hbb20.CCPCountry;
import com.hbb20.CCPTalkBackTextProvider;

class CCPCustomTalkBackProvider implements CCPTalkBackTextProvider {
    @Override
    public String getTalkBackTextForCountry(CCPCountry country) {
        if (country != null) {
            return "Country code is +" + country.getPhoneCode();
        } else {
            return null;
        }
    }
}
