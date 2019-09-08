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
class CountryPreferenceFragment: Fragment() {

	private lateinit var editTextCountryPreference: EditText
	private lateinit var buttonSetCountryPreference: Button
	private lateinit var ccp: CountryCodePicker
	private lateinit var buttonNext: Button

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_country_preference, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		assignViews()
		editTextWatcher()
		addClickListeners()
	}

	private fun addClickListeners() {
		buttonSetCountryPreference.setOnClickListener {
			try {
				val countryPreference = editTextCountryPreference.text.toString()
				ccp.setCountryPreference(countryPreference)

				Toast.makeText(activity, "Country preference list has been changed, click on CCP to see them at top of list.", Toast.LENGTH_LONG).show()
			} catch (ex: Exception) {
			}
		}

		buttonNext.setOnClickListener { (activity as ExampleActivity).viewPager.currentItem = (activity as ExampleActivity).viewPager.currentItem + 1 }
	}

	private fun editTextWatcher() {
		editTextCountryPreference.addTextChangedListener(object: TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				buttonSetCountryPreference.text = "set '$s' as Country preference."
			}

			override fun afterTextChanged(s: Editable) {
			}
		})
	}

	private fun assignViews() {
		editTextCountryPreference = view!!.findViewById(R.id.editText_countryPreference)
		ccp = view!!.findViewById(R.id.ccp)
		buttonSetCountryPreference = view!!.findViewById(R.id.button_setCountryPreference)


		buttonNext = view!!.findViewById(R.id.button_next)
	}
}
