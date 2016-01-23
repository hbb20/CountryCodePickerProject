package in.hbb20.countrycodepickerproject;


import android.graphics.PorterDuff;
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

import in.hbb20.CountryCodePicker;


/**
 * A simple {@link Fragment} subclass.
 */
public class DefaultCountryFragment extends Fragment {


    EditText editTextDefaultCode;
    Button buttonSetNewDefaultCode,buttonResetToDefault;
    CountryCodePicker ccp;
    Button buttonNext;
    public DefaultCountryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_default_country, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews();
        editTextWatcher();
        addClickListeners();
    }

    private void addClickListeners() {
        buttonSetNewDefaultCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int code=-1;
                try{
                    code=Integer.parseInt(editTextDefaultCode.getText().toString());
                    ccp.setDefaultCountryCode(code);
                    Toast.makeText(getActivity(),"Now default country is "+ccp.getDefaultCountryName()+" with phone code "+ccp.getDefaultCountryCode() ,Toast.LENGTH_LONG).show();
                }catch (Exception ex){
                    Toast.makeText(getActivity(),"Invalid number format",Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonResetToDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ccp.resetToDefaultCountry();
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
        editTextDefaultCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSetNewDefaultCode.setText("set "+s+" as Default Country Code");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void assignViews() {
        editTextDefaultCode=(EditText)getView().findViewById(R.id.editText_defaultCode);
        ccp=(CountryCodePicker)getView().findViewById(R.id.ccp);
        buttonSetNewDefaultCode=(Button) getView().findViewById(R.id.button_setDefault);
        buttonResetToDefault=(Button) getView().findViewById(R.id.button_resetToDefault);

        buttonNext=(Button)getView().findViewById(R.id.button_next);
        buttonNext.getBackground().setColorFilter(getActivity().getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
    }
}
