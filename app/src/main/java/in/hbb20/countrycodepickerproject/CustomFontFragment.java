package in.hbb20.countrycodepickerproject;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomFontFragment extends Fragment {

    Button buttonNext;
    View rootView;
    private CountryCodePicker ccp6,ccp5,ccp4,ccp3,ccp2,ccp1;

    public CustomFontFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_custom_font, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews();
        applyCustomFonts();
        setClickListener();
    }

    private void applyCustomFonts() {
        setTTFfont(ccp2,"bookos.ttf");
        setTTFfont(ccp3,"hack.ttf");
        setTTFfont(ccp4,"playfair.ttf");
        setTTFfont(ccp5,"raleway.ttf");
        setTTFfont(ccp6,"titillium.ttf");
    }

    private void setTTFfont(CountryCodePicker ccp, String fontFileName) {
        Typeface typeFace=Typeface.createFromAsset(getContext().getAssets(),fontFileName);
        ccp.setTypeFace(typeFace);
    }

    private void setClickListener() {

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ExampleActivity) getActivity()).viewPager.setCurrentItem(((ExampleActivity) getActivity()).viewPager.getCurrentItem() + 1);
            }
        });
    }

    private void assignViews() {
        ccp1=(CountryCodePicker)rootView.findViewById(R.id.ccp1);
        ccp2=(CountryCodePicker)rootView.findViewById(R.id.ccp2);
        ccp3=(CountryCodePicker)rootView.findViewById(R.id.ccp3);
        ccp4=(CountryCodePicker)rootView.findViewById(R.id.ccp4);
        ccp5=(CountryCodePicker)rootView.findViewById(R.id.ccp5);
        ccp6=(CountryCodePicker)rootView.findViewById(R.id.ccp6);
        buttonNext = (Button) getView().findViewById(R.id.button_next);
    }
}
