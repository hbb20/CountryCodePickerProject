package in.hbb20.countrycodepickerproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    TextView textIntro, textDefaultCountry, textPreference, textCustomMaster, textsetCountry, textGetCountry, textFullNumber, textCustomColor, textCustomSize, textCustomFont, textCustomLanguage;
    Button startDemo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        assignViews();
    }

    private void assignViews() {
        textIntro = (TextView) findViewById(R.id.textIntro);
        setclick(textIntro, 0);

        textDefaultCountry = (TextView) findViewById(R.id.textDefaultCountry);
        setclick(textDefaultCountry, 1);

        textPreference = (TextView) findViewById(R.id.textCountryPreference);
        setclick(textPreference, 2);

        textCustomMaster = (TextView) findViewById(R.id.textCustomMaster);
        setclick(textCustomMaster, 3);

        textsetCountry = (TextView) findViewById(R.id.textSetCountry);
        setclick(textsetCountry, 4);

        textGetCountry = (TextView) findViewById(R.id.textGetCountry);
        setclick(textGetCountry, 5);

        textFullNumber = (TextView) findViewById(R.id.textFullNumber);
        setclick(textFullNumber, 6);

        textCustomColor = (TextView) findViewById(R.id.textCustomColor);
        setclick(textCustomColor, 7);

        textCustomSize = (TextView) findViewById(R.id.textCustomSize);
        setclick(textCustomSize, 8);

        textCustomFont = (TextView) findViewById(R.id.textCustomFont);
        setclick(textCustomFont, 9);

        textCustomLanguage = (TextView) findViewById(R.id.textCustomLanguage);
        setclick(textCustomLanguage, 10);

        startDemo = (Button) findViewById(R.id.buttonGo);
        startDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ExampleActivity.class);
                i.putExtra(ExampleActivity.EXTRA_INIT_TAB, 0);
                startActivity(i);
            }
        });
    }

    private void setclick(TextView text, final int tabIndex) {
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getBaseContext(), ExampleActivity.class);
                i.putExtra(ExampleActivity.EXTRA_INIT_TAB, tabIndex);
                startActivity(i);
            }
        });
    }
}
