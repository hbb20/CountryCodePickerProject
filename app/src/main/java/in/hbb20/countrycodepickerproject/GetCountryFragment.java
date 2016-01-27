package in.hbb20.countrycodepickerproject;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;


/**
 * A simple {@link Fragment} subclass.
 */
public class GetCountryFragment extends Fragment {


    TextView textViewCountryName,textViewCountryCode,textViewCountryNameCode;
    Button buttonReadCountry;
    CountryCodePicker ccp;
    Button buttonNext;
    public GetCountryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_country, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews();
        setClickListener();
    }

    private void setClickListener() {
        buttonReadCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewCountryName.setText(ccp.getSelectedCountryName());
                textViewCountryCode.setText(ccp.getSelectedCountryCode());
                textViewCountryNameCode.setText(ccp.getSelectedCountryNameCode());
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExampleActivity) getActivity()).viewPager.setCurrentItem(((ExampleActivity) getActivity()).viewPager.getCurrentItem() + 1);
            }
        });
    }

    private void assignViews() {
        ccp=(CountryCodePicker)getView().findViewById(R.id.ccp);
        textViewCountryCode=(TextView)getView().findViewById(R.id.textView_countryCode);
        textViewCountryName=(TextView)getView().findViewById(R.id.textView_countryName);
        textViewCountryNameCode=(TextView)getView().findViewById(R.id.textView_countryNameCode);
        buttonReadCountry=(Button)getView().findViewById(R.id.button_readCountry);
        buttonNext=(Button)getView().findViewById(R.id.button_next);
    }
}
