package in.hbb20.countrycodepickerproject;


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
public class LanguageSupportFragment extends Fragment {

    RadioGroup radioGroup;
    RadioButton radioEnglish;
    RadioButton radioJapanese,radioSpanish;
    CountryCodePicker ccp;
    Button buttonNext;
    View rootView;
    public LanguageSupportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_language_support, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews();
        setClickListener();
    }

    private void setClickListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioEnglish:
                        ccp.changeDefaultLanguage(CountryCodePicker.Language.ENGLISH);
                        Toast.makeText(getContext(),"Language is updated to ENGLISH",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radioJapanese:
                        ccp.changeDefaultLanguage(CountryCodePicker.Language.JAPANESE);
                        Toast.makeText(getContext(),"Language is updated to JAPANESE",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.radioSpanish:
                        ccp.changeDefaultLanguage(CountryCodePicker.Language.SPANISH);
                        Toast.makeText(getContext(),"Language is updated to SPANISH",Toast.LENGTH_SHORT).show();
                        break;
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

    private void assignViews() {
        ccp=(CountryCodePicker)rootView.findViewById(R.id.ccp);
        radioGroup=(RadioGroup) rootView.findViewById(R.id.radioGroup);
        radioEnglish=(RadioButton) rootView.findViewById(R.id.radioEnglish);
        radioJapanese=(RadioButton) rootView.findViewById(R.id.radioJapanese);
        radioSpanish=(RadioButton)rootView.findViewById(R.id.radioSpanish);
        buttonNext = (Button) getView().findViewById(R.id.button_next);
    }
}
