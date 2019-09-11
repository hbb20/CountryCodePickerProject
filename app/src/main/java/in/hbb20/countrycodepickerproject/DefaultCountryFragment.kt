package `in`.hbb20.countrycodepickerproject

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hbb20.CountryCodePicker

/**
 * A simple [Fragment] subclass.
 */
class DefaultCountryFragment: Fragment() {

	private lateinit var editTextDefaultPhoneCode: EditText
	private lateinit var editTextDefaultNameCode: EditText
	private lateinit var buttonSetNewDefaultPhoneCode: Button
	private lateinit var buttonSetNewDefaultNameCode: Button
	private lateinit var buttonResetToDefault: Button
	private lateinit var ccp: CountryCodePicker
	private lateinit var buttonNext: Button

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_default_country, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		assignViews()
		editTextWatcher()
		addClickListeners()
	}

	private fun addClickListeners() {
		buttonSetNewDefaultPhoneCode.setOnClickListener {
			try {
				val code = Integer.parseInt(editTextDefaultPhoneCode.text.toString())
				@Suppress("DEPRECATION")
				ccp.setDefaultCountryUsingPhoneCode(code)
				Toast.makeText(activity, "Now default country is " + ccp.defaultCountryName + " with phone code " + ccp.defaultCountryCode, Toast.LENGTH_LONG).show()
			} catch (ex: Exception) {
				Toast.makeText(activity, "Invalid number format", Toast.LENGTH_LONG).show()
			}
		}

		buttonSetNewDefaultNameCode.setOnClickListener {
			val nameCode: String
			try {
				nameCode = editTextDefaultNameCode.text.toString()
				ccp.setDefaultCountryUsingNameCode(nameCode)
				Toast.makeText(activity, "Now default country is " + ccp.defaultCountryName + " with phone code " + ccp.defaultCountryCode, Toast.LENGTH_LONG).show()
			} catch (ex: Exception) {

			}
		}

		buttonResetToDefault.setOnClickListener { ccp.resetToDefaultCountry() }

		buttonNext.setOnClickListener { (activity as ExampleActivity).viewPager.currentItem = (activity as ExampleActivity).viewPager.currentItem + 1 }
	}

	private fun editTextWatcher() {
		editTextDefaultPhoneCode.addTextChangedListener(object: TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				buttonSetNewDefaultPhoneCode.text = "set $s as Default Country Code"
			}

			override fun afterTextChanged(s: Editable) {
			}
		})

		editTextDefaultNameCode.addTextChangedListener(object: TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				buttonSetNewDefaultNameCode.text = "set '$s' as Default Country Name Code"
			}

			override fun afterTextChanged(s: Editable) {
			}
		})
	}

	private fun assignViews() {
		editTextDefaultPhoneCode = view!!.findViewById(R.id.editText_defaultCode)
		ccp = view!!.findViewById(R.id.ccp)
		buttonSetNewDefaultPhoneCode = view!!.findViewById(R.id.button_setDefaultCode)
		buttonResetToDefault = view!!.findViewById(R.id.button_resetToDefault)

		editTextDefaultNameCode = view!!.findViewById(R.id.editText_defaultNameCode)
		buttonSetNewDefaultNameCode = view!!.findViewById(R.id.button_setDefaultNameCode)

		buttonNext = view!!.findViewById(R.id.button_next)
	}
}