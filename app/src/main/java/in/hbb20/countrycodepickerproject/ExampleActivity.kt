package `in`.hbb20.countrycodepickerproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager

const val EXTRA_INIT_TAB = "extraInitTab"

class ExampleActivity: AppCompatActivity() {

	internal lateinit var viewPager: ViewPager
	private var pagerAdapter: PagerAdapter? = null
	private var init = 0
	private var initLoaded = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_example)

		init = intent.getIntExtra(EXTRA_INIT_TAB, 0)
		assignViews()
		setUpViewPager()
	}

	/**
	 * Assign adapter to viewPager
	 */
	private fun setUpViewPager() {
		if (pagerAdapter == null) {
			pagerAdapter = PagerAdapter(supportFragmentManager)
		}
		viewPager.adapter = pagerAdapter
		if (!initLoaded) {
			viewPager.currentItem = init
			initLoaded = true
		}
	}

	/**
	 * assign views to object from layout
	 */
	private fun assignViews() {
		viewPager = findViewById(R.id.viewPager)
	}

	private inner class PagerAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
		private val fragments = listOf(
				{ IntroductionFragment() },
				{ DefaultCountryFragment() },
				{ CountryPreferenceFragment() },
				{ CustomMasterFragment() },
				{ SetCountryFragment() },
				{ GetCountryFragment() },
				{ FullNumberFragment() },
				{ CustomColorFragment() },
				{ CustomSizeFragment() },
				{ CustomFontFragment() },
				{ LanguageSupportFragment() }
		)

		override fun getItem(position: Int): Fragment {
			return fragments[position]()
		}

		override fun getCount(): Int {
			return fragments.size
		}
	}
}
