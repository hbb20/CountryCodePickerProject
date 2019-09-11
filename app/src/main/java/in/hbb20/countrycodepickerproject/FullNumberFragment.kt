package `in`.hbb20.countrycodepickerproject

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import com.hbb20.CountryCodePicker

/**
 * A simple [Fragment] subclass.
 */
class FullNumberFragment: Fragment() {

	private lateinit var editTextLoadFullNumber: EditText
	private lateinit var editTextLoadCarrierNumber: EditText
	private lateinit var editTextGetCarrierNumber: EditText
	private lateinit var editTextGetFullNumber: TextView
	private lateinit var ccpLoadNumber: CountryCodePicker
	private lateinit var ccpGetNumber: CountryCodePicker
	private lateinit var tvValidity: TextView
	private lateinit var imgValidity: ImageView
	private lateinit var buttonLoadNumber: Button
	private lateinit var buttonGetNumber: Button
	private lateinit var buttonGetNumberWithPlus: Button
	private lateinit var buttonFormattedFullNumber: Button
	private lateinit var buttonNext: Button

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_full_number, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		assignView()
		registerCarrierEditText()
		setClickListener()
		addTextWatcher()
	}

	private fun addTextWatcher() {
		editTextLoadFullNumber.addTextChangedListener(object: TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				buttonLoadNumber.text = "Load $s to CCP."
			}

			override fun afterTextChanged(s: Editable) {
			}
		})
	}

	private fun setClickListener() {
		buttonLoadNumber.setOnClickListener { ccpLoadNumber.fullNumber = editTextLoadFullNumber.text.toString() }

		buttonFormattedFullNumber.setOnClickListener { editTextGetFullNumber.text = ccpGetNumber.formattedFullNumber }

		buttonGetNumber.setOnClickListener { editTextGetFullNumber.text = ccpGetNumber.fullNumber }

		buttonGetNumberWithPlus.setOnClickListener { editTextGetFullNumber.text = ccpGetNumber.fullNumberWithPlus }

		buttonNext.setOnClickListener { (activity as ExampleActivity).viewPager.currentItem = (activity as ExampleActivity).viewPager.currentItem + 1 }
	}

	private fun registerCarrierEditText() {
		ccpLoadNumber.registerCarrierNumberEditText(editTextLoadCarrierNumber)
		ccpGetNumber.registerCarrierNumberEditText(editTextGetCarrierNumber)
		ccpGetNumber.setPhoneNumberValidityChangeListener { isValidNumber ->
			if (isValidNumber) {
				imgValidity.setImageDrawable(getDrawable(context!!, R.drawable.ic_assignment_turned_in_black_24dp))
				tvValidity.text = "Valid Number"
			} else {
				imgValidity.setImageDrawable(getDrawable(context!!, R.drawable.ic_assignment_late_black_24dp))
				tvValidity.text = "Invalid Number"
			}
		}

		ccpLoadNumber.registerCarrierNumberEditText(editTextLoadCarrierNumber)
	}

	private fun assignView() {
		//load number
		editTextLoadFullNumber = view!!.findViewById(R.id.editText_loadFullNumber)
		editTextLoadCarrierNumber = view!!.findViewById(R.id.editText_loadCarrierNumber)
		ccpLoadNumber = view!!.findViewById(R.id.ccp_loadFullNumber)
		buttonLoadNumber = view!!.findViewById(R.id.button_loadFullNumber)

		//get number
		editTextGetCarrierNumber = view!!.findViewById(R.id.editText_getCarrierNumber)
		editTextGetFullNumber = view!!.findViewById(R.id.textView_getFullNumber)
		buttonGetNumber = view!!.findViewById(R.id.button_getFullNumber)
		buttonGetNumberWithPlus = view!!.findViewById(R.id.button_getFullNumberWithPlus)
		ccpGetNumber = view!!.findViewById(R.id.ccp_getFullNumber)
		buttonFormattedFullNumber = view!!.findViewById(R.id.button_getFormattedFullNumberWithPlus)
		tvValidity = view!!.findViewById(R.id.tv_validity)
		imgValidity = view!!.findViewById(R.id.img_validity)

		buttonNext = view!!.findViewById(R.id.button_next)
	}
}
