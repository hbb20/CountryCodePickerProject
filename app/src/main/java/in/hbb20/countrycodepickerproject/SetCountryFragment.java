package in.hbb20.countrycodepickerproject;

import android.content.Context;
import android.net.Uri;
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
 * create an instance of this fragment.
 */
public class SetCountryFragment extends Fragment {

    EditText editTextCode;
    Button buttonSetCode;
    CountryCodePicker ccp;
    Button buttonNext;
    public SetCountryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_country, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews();
        editTextWatcher();
        addClickListeners();
    }

    private void addClickListeners() {
        buttonSetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int code=-1;
                try{
                    code=Integer.parseInt(editTextCode.getText().toString());
                    ccp.setCountryForCode(code);
                }catch (Exception ex){
                    Toast.makeText(getActivity(),"Invalid number format",Toast.LENGTH_LONG).show();
                }
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExampleActivity) getActivity()).viewPager.setCurrentItem(((ExampleActivity) getActivity()).viewPager.getCurrentItem()+1);
            }
        });
    }

    private void editTextWatcher() {
        editTextCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonSetCode.setText("Set country with code "+s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void assignViews() {
        editTextCode=(EditText)getView().findViewById(R.id.editText_countryCode);
        ccp=(CountryCodePicker)getView().findViewById(R.id.ccp);
        buttonSetCode=(Button) getView().findViewById(R.id.button_setCountry);
        buttonNext=(Button)getView().findViewById(R.id.button_next);
    }

}
