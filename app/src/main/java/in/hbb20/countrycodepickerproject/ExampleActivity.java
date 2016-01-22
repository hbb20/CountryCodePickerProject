package in.hbb20.countrycodepickerproject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ExampleActivity extends AppCompatActivity {

    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
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
                    return new SetCountryFragment();
                case 3:
                    return new GetCountryFragment();
                case 4:
                    return new FullNumberFragment();
                case 5:
                    return new CustomColorFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 6;
        }
    }
}
