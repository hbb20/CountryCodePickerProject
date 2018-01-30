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
public class CustomMasterFragment extends Fragment {

    EditText editTextCountryCustomMaster;
    Button buttonSetCustomMaster;
    CountryCodePicker ccp;
    Button buttonNext;

    public CustomMasterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_master_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews();
        editTextWatcher();
        addClickListeners();
    }

    private void addClickListeners() {

        buttonSetCustomMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customMasterCountries;
                try {
                    customMasterCountries = editTextCountryCustomMaster.getText().toString();
                    ccp.setCustomMasterCountries(customMasterCountries);
                    Toast.makeText(getActivity(), "Master list has been changed. Tap on ccp to see the changes.", Toast.LENGTH_LONG).show();
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
        editTextCountryCustomMaster.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSetCustomMaster.setText("set '" + s + "' as Custom Master List.");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void assignViews() {
        editTextCountryCustomMaster = (EditText) getView().findViewById(R.id.editText_countryPreference);
        ccp = (CountryCodePicker) getView().findViewById(R.id.ccp);
        buttonSetCustomMaster = (Button) getView().findViewById(R.id.button_setCustomMaster);

        buttonNext = (Button) getView().findViewById(R.id.button_next);
    }
}
