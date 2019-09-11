package `in`.hbb20.countrycodepickerproject

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.hbb20.CountryCodePicker

/**
 * A simple [Fragment] subclass.
 */
class CustomFontFragment: Fragment() {

	private lateinit var buttonNext: Button
	private lateinit var rootView: View
	private lateinit var ccp6: CountryCodePicker
	private lateinit var ccp5: CountryCodePicker
	private lateinit var ccp4: CountryCodePicker
	private lateinit var ccp3: CountryCodePicker
	private lateinit var ccp2: CountryCodePicker
	private lateinit var ccp1: CountryCodePicker

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		rootView = inflater.inflate(R.layout.fragment_custom_font, container, false)
		return rootView
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		assignViews()
		applyCustomFonts()
		setClickListener()
	}

	private fun applyCustomFonts() {
		setTTFfont(ccp2, "bookos.ttf")
		setTTFfont(ccp3, "hack.ttf")
		setTTFfont(ccp4, "playfair.ttf")
		setTTFfont(ccp5, "raleway.ttf")
		setTTFfont(ccp6, "titillium.ttf")
	}

	private fun setTTFfont(ccp: CountryCodePicker, fontFileName: String) {
		val typeFace = Typeface.createFromAsset(context!!.assets, fontFileName)
		ccp.setTypeFace(typeFace)
	}

	private fun setClickListener() {

		buttonNext.setOnClickListener { (activity as ExampleActivity).viewPager.currentItem = (activity as ExampleActivity).viewPager.currentItem + 1 }
	}

	private fun assignViews() {
		ccp1 = rootView.findViewById(R.id.ccp1)
		ccp2 = rootView.findViewById(R.id.ccp2)
		ccp3 = rootView.findViewById(R.id.ccp3)
		ccp4 = rootView.findViewById(R.id.ccp4)
		ccp5 = rootView.findViewById(R.id.ccp5)
		ccp6 = rootView.findViewById(R.id.ccp6)
		buttonNext = view!!.findViewById(R.id.button_next)
	}
}
