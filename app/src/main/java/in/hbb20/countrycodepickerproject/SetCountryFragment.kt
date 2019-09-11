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
 * create an instance of this fragment.
 */
class SetCountryFragment: Fragment() {

	private lateinit var editTextCode: EditText
	private lateinit var editTextNameCode: EditText
	private lateinit var buttonSetCode: Button
	private lateinit var buttonSetNameCode: Button
	private lateinit var ccp: CountryCodePicker
	private lateinit var buttonNext: Button

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_set_country, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		assignViews()
		editTextWatcher()
		addClickListeners()
	}

	private fun addClickListeners() {
		buttonSetCode.setOnClickListener {
			try {
				val code = Integer.parseInt(editTextCode.text.toString())
				ccp.setCountryForPhoneCode(code)
			} catch (ex: Exception) {
				Toast.makeText(activity, "Invalid number format", Toast.LENGTH_LONG).show()
			}
		}

		buttonSetNameCode.setOnClickListener {
			try {
				val code = editTextNameCode.text.toString()
				ccp.setCountryForNameCode(code)
			} catch (ex: Exception) {
			}
		}

		buttonNext.setOnClickListener { (activity as ExampleActivity).viewPager.currentItem = (activity as ExampleActivity).viewPager.currentItem + 1 }
	}

	private fun editTextWatcher() {
		editTextCode.addTextChangedListener(object: TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				buttonSetCode.text = "Set country with code $s"
			}

			override fun afterTextChanged(s: Editable) {
			}
		})

		editTextNameCode.addTextChangedListener(object: TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				buttonSetNameCode.text = "Set country with name code '$s'"
			}

			override fun afterTextChanged(s: Editable) {
			}
		})

		ccp.setOnCountryChangeListener {
			Toast.makeText(context, "This is from OnCountryChangeListener. \n Country updated to " + ccp.selectedCountryName + "(" + ccp.selectedCountryCodeWithPlus + ")", Toast.LENGTH_SHORT).show()
		}
	}

	private fun assignViews() {
		editTextNameCode = view!!.findViewById(R.id.editText_countryNameCode)
		editTextCode = view!!.findViewById(R.id.editText_countryCode)
		ccp = view!!.findViewById(R.id.ccp)
		buttonSetCode = view!!.findViewById(R.id.button_setCountry)
		buttonSetNameCode = view!!.findViewById(R.id.button_setCountryNameCode)
		buttonNext = view!!.findViewById(R.id.button_next)
	}
}
