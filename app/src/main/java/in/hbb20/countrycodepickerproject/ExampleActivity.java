package in.hbb20.countrycodepickerproject;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

public class ExampleActivity extends AppCompatActivity {

    public static final String EXTRA_INIT_TAB = "extraInitTab";
    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    int init = 0;
    boolean initLoaded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        init = getIntent().getIntExtra(EXTRA_INIT_TAB, 0);
        assignViews();
        setUpViewPager();
    }

    /**
     * Assign adapter to viewPager
     */
    private void setUpViewPager() {
        if(pagerAdapter==null){
            pagerAdapter=new PagerAdapter(getSupportFragmentManager());
        }
        viewPager.setAdapter(pagerAdapter);
        if (!initLoaded) {
            viewPager.setCurrentItem(init);
            initLoaded = true;
        }
    }

    /**
     * assign views to object from layout
     */
    private void assignViews() {
        viewPager=(ViewPager)findViewById(R.id.viewPager);
    }

    class PagerAdapter extends FragmentPagerAdapter{

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new IntroductionFragment();
                case 1:
                    return new DefaultCountryFragment();
                case 2:
                    return new CountryPreferenceFragment();
                case 3:
                    return new CustomMasterFragment();
                case 4:
                    return new SetCountryFragment();
                case 5:
                    return new GetCountryFragment();
                case 6:
                    return new FullNumberFragment();
                case 7:
                    return new CustomColorFragment();
                case 8:
                    return new CustomSizeFragment();
                case 9:
                    return new CustomFontFragment();
                case 10:
                    return new LanguageSupportFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 11;
        }
    }
}
