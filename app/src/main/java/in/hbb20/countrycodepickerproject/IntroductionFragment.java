package in.hbb20.countrycodepickerproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.hbb20.CountryCodePicker;


/**
 * A simple {@link Fragment} subclass.
 */
public class IntroductionFragment extends Fragment {


    Button buttonGo;
    CountryCodePicker countryCodePicker;
    EditText etPhone;
    boolean isFirst = true;


    public IntroductionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_introduction, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews();
        setCCPValidityListener();
        setClickListener();
    }

    private void setCCPValidityListener() {

    }

    private void setClickListener() {
        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExampleActivity)getActivity()).viewPager.setCurrentItem(1);
            }
        });
    }

    private void assignViews() {
        buttonGo=(Button)getView().findViewById(R.id.button_letsGo);
        etPhone = (EditText) getView().findViewById(R.id.et_phone);
        countryCodePicker = (CountryCodePicker) getView().findViewById(R.id.ccp);
        countryCodePicker.registerCarrierNumberEditText(etPhone);
    }
}
