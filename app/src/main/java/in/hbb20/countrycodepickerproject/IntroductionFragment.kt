package `in`.hbb20.countrycodepickerproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

import androidx.fragment.app.Fragment

import com.hbb20.CountryCodePicker

/**
 * A simple [Fragment] subclass.
 */
class IntroductionFragment: Fragment() {


	private lateinit var buttonGo: Button
	private lateinit var countryCodePicker: CountryCodePicker
	private lateinit var etPhone: EditText

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_introduction, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		assignViews()
		setClickListener()
		setCustomTalkBackProvider()
	}

	private fun assignViews() {
		buttonGo = view!!.findViewById(R.id.button_letsGo)
		etPhone = view!!.findViewById(R.id.et_phone)
		countryCodePicker = view!!.findViewById(R.id.ccp)
		countryCodePicker.registerCarrierNumberEditText(etPhone)
	}

	private fun setClickListener() {
		buttonGo.setOnClickListener { (activity as ExampleActivity).viewPager.currentItem = 1 }
	}

	private fun setCustomTalkBackProvider() {
		countryCodePicker.setTalkBackTextProvider(CCPCustomTalkBackProvider())
	}
}
