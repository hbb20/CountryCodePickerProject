package in.hbb20.countrycodepickerproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CountryPreferenceFragment extends Fragment {

    EditText editTextCountryPreference;
    Button buttonSetCountryPreference;
    CountryCodePicker ccp;
    Button buttonNext;

    public CountryPreferenceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_country_preference, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews();
        editTextWatcher();
        addClickListeners();
    }

    private void addClickListeners() {

        buttonSetCountryPreference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countryPreference;
                try {
                    countryPreference = editTextCountryPreference.getText().toString();
                    ccp.setCountryPreference(countryPreference);
                    Toast.makeText(getActivity(), "Country preference list has been changed, click on CCP to see them at top of list.", Toast.LENGTH_LONG).show();
                } catch (Exception ex) {

                }
            }
        });


        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExampleActivity) getActivity()).viewPager.setCurrentItem(((ExampleActivity) getActivity()).viewPager.getCurrentItem() + 1);
            }
        });
    }

    private void editTextWatcher() {
        editTextCountryPreference.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSetCountryPreference.setText("set '" + s + "' as Country preference.");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void assignViews() {
        editTextCountryPreference = (EditText) getView().findViewById(R.id.editText_countryPreference);
        ccp = (CountryCodePicker) getView().findViewById(R.id.ccp);
        buttonSetCountryPreference = (Button) getView().findViewById(R.id.button_setCountryPreference);


        buttonNext = (Button) getView().findViewById(R.id.button_next);
    }
}
