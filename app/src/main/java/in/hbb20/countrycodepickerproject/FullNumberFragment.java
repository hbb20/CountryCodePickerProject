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
import android.widget.ImageView;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;


/**
 * A simple {@link Fragment} subclass.
 */
public class FullNumberFragment extends Fragment {


    EditText editTextLoadFullNumber, editTextLoadCarrierNumber, editTextGetCarrierNumber;
    TextView editTextGetFullNumber;
    CountryCodePicker ccpLoadNumber, ccpGetNumber;
    TextView tvValidity;
    ImageView imgValidity;
    Button buttonLoadNumber, buttonGetNumber, buttonGetNumberWithPlus, buttonFormattedFullNumber;
    Button buttonNext;

    public FullNumberFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_full_number, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignView();
        registerCarrierEditText();
        setClickListener();
        addTextWatcher();
    }

    private void addTextWatcher() {
        editTextLoadFullNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buttonLoadNumber.setText("Load " + s + " to CCP.");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setClickListener() {
        buttonLoadNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ccpLoadNumber.setFullNumber(editTextLoadFullNumber.getText().toString());
            }
        });

        buttonFormattedFullNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextGetFullNumber.setText(ccpGetNumber.getFormattedFullNumber());
            }
        });

        buttonGetNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextGetFullNumber.setText(ccpGetNumber.getFullNumber());
            }
        });

        buttonGetNumberWithPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextGetFullNumber.setText(ccpGetNumber.getFullNumberWithPlus());
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExampleActivity) getActivity()).viewPager.setCurrentItem(((ExampleActivity) getActivity()).viewPager.getCurrentItem() + 1);
            }
        });
    }

    private void registerCarrierEditText() {
        ccpLoadNumber.registerCarrierNumberEditText(editTextLoadCarrierNumber);
        ccpGetNumber.registerCarrierNumberEditText(editTextGetCarrierNumber);
        ccpLoadNumber.getFullNumberWithPlus();
        ccpGetNumber.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {
                if (isValidNumber) {
                    imgValidity.setImageDrawable(getResources().getDrawable(R.drawable.ic_assignment_turned_in_black_24dp));
                    tvValidity.setText("Valid Number");
                } else {
                    imgValidity.setImageDrawable(getResources().getDrawable(R.drawable.ic_assignment_late_black_24dp));
                    tvValidity.setText("Invalid Number");
                }
            }
        });
    }

    private void assignView() {
        //load number
        editTextLoadFullNumber = (EditText) getView().findViewById(R.id.editText_loadFullNumber);
        editTextLoadCarrierNumber = (EditText) getView().findViewById(R.id.editText_loadCarrierNumber);
        ccpLoadNumber = (CountryCodePicker) getView().findViewById(R.id.ccp_loadFullNumber);
        buttonLoadNumber = (Button) getView().findViewById(R.id.button_loadFullNumber);

        //get number
        editTextGetCarrierNumber = (EditText) getView().findViewById(R.id.editText_getCarrierNumber);
        editTextGetFullNumber = (TextView) getView().findViewById(R.id.textView_getFullNumber);
        buttonGetNumber = (Button) getView().findViewById(R.id.button_getFullNumber);
        buttonGetNumberWithPlus = (Button) getView().findViewById(R.id.button_getFullNumberWithPlus);
        ccpGetNumber = (CountryCodePicker) getView().findViewById(R.id.ccp_getFullNumber);
        buttonFormattedFullNumber = (Button) getView().findViewById(R.id.button_getFormattedFullNumberWithPlus);
        tvValidity = (TextView) getView().findViewById(R.id.tv_validity);
        imgValidity = (ImageView) getView().findViewById(R.id.img_validity);

        buttonNext = (Button) getView().findViewById(R.id.button_next);

    }
}
