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
class CustomMasterFragment: Fragment() {

	private lateinit var editTextCountryCustomMaster: EditText
	private lateinit var buttonSetCustomMaster: Button
	private lateinit var ccp: CountryCodePicker
	private lateinit var buttonNext: Button

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_custom_master_list, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		assignViews()
		editTextWatcher()
		addClickListeners()
	}

	private fun addClickListeners() {
		buttonSetCustomMaster.setOnClickListener {
			val customMasterCountries: String
			try {
				customMasterCountries = editTextCountryCustomMaster.text.toString()
				ccp.setCustomMasterCountries(customMasterCountries)
				Toast.makeText(activity, "Master list has been changed. Tap on ccp to see the changes.", Toast.LENGTH_LONG).show()
			} catch (ex: Exception) {
			}
		}

		buttonNext.setOnClickListener { (activity as ExampleActivity).viewPager.currentItem = (activity as ExampleActivity).viewPager.currentItem + 1 }
	}

	private fun editTextWatcher() {
		editTextCountryCustomMaster.addTextChangedListener(object: TextWatcher {
			override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
			}

			override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
				buttonSetCustomMaster.text = "set '$s' as Custom Master List."
			}

			override fun afterTextChanged(s: Editable) {
			}
		})
	}

	private fun assignViews() {
		editTextCountryCustomMaster = view!!.findViewById(R.id.editText_countryPreference)
		ccp = view!!.findViewById(R.id.ccp)
		buttonSetCustomMaster = view!!.findViewById(R.id.button_setCustomMaster)

		buttonNext = view!!.findViewById(R.id.button_next)
	}
}
