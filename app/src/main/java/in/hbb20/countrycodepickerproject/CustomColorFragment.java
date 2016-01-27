package in.hbb20.countrycodepickerproject;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;


/**
 * A simple {@link Fragment} subclass.
 */
public class CustomColorFragment extends Fragment {

    public CustomColorFragment() {
    }

    TextView textViewTitle;
    EditText editTextPhone;
    CountryCodePicker ccp;
    RelativeLayout relativeColor1,relativeColor2,relativeColor3;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_color, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        assignViews();
        setClickListener();
    }

    private void setClickListener() {
        relativeColor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor(1, getActivity().getResources().getColor(R.color.color1));
            }
        });

        relativeColor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor(2, getActivity().getResources().getColor(R.color.color2));
            }
        });

        relativeColor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor(3,getActivity().getResources().getColor(R.color.color3));
            }
        });
    }

    private void setColor(int selection,int color) {
        ccp.setContentColor(color);

        //textView
        textViewTitle.setTextColor(color);

        //editText
        editTextPhone.setTextColor(color);
        editTextPhone.setHintTextColor(color);
        editTextPhone.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        //remove selected bg
        resetBG();

        //set selected bg
        int selectedBGColor=getActivity().getResources().getColor(R.color.selectedTile);
        switch (selection){
            case 1:
                relativeColor1.setBackgroundColor(selectedBGColor);
                break;
            case 2:
                relativeColor2.setBackgroundColor(selectedBGColor);
                break;
            case 3:
                relativeColor3.setBackgroundColor(selectedBGColor);
                break;
        }
    }

    private void resetBG() {
        relativeColor1.setBackgroundColor(getActivity().getResources().getColor(R.color.dullBG));
        relativeColor2.setBackgroundColor(getActivity().getResources().getColor(R.color.dullBG));
        relativeColor3.setBackgroundColor(getActivity().getResources().getColor(R.color.dullBG));
    }

    private void assignViews() {
        textViewTitle =(TextView)getView().findViewById(R.id.textView_title);
        editTextPhone =(EditText)getView().findViewById(R.id.editText_phone);
        ccp=(CountryCodePicker)getView().findViewById(R.id.ccp);
        relativeColor1=(RelativeLayout)getView().findViewById(R.id.relative_color1);
        relativeColor2=(RelativeLayout)getView().findViewById(R.id.relative_color2);
        relativeColor3=(RelativeLayout)getView().findViewById(R.id.relative_color3);
    }
}
