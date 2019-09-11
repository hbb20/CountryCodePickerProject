package `in`.hbb20.countrycodepickerproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hbb20.CountryCodePicker
import com.hbb20.CountryCodePicker.Language.*

/**
 * A simple [Fragment] subclass.
 */
class LanguageSupportFragment: Fragment() {

	private lateinit var radioGroup: RadioGroup
	private lateinit var radioEnglish: RadioButton
	private lateinit var radioJapanese: RadioButton
	private lateinit var radioSpanish: RadioButton
	private lateinit var ccp: CountryCodePicker
	private lateinit var buttonNext: Button
	private lateinit var rootView: View

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		rootView = inflater.inflate(R.layout.fragment_language_support, container, false)
		return rootView
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		assignViews()
		setClickListener()
	}

	private fun setClickListener() {
		radioGroup.setOnCheckedChangeListener { _, checkedId ->
			when (checkedId) {
				R.id.radioEnglish  -> {
					ccp.changeDefaultLanguage(ENGLISH)
					Toast.makeText(context, "Language is updated to ENGLISH", Toast.LENGTH_SHORT).show()
				}
				R.id.radioJapanese -> {
					ccp.changeDefaultLanguage(JAPANESE)
					Toast.makeText(context, "Language is updated to JAPANESE", Toast.LENGTH_SHORT).show()
				}
				R.id.radioSpanish  -> {
					ccp.changeDefaultLanguage(SPANISH)
					Toast.makeText(context, "Language is updated to SPANISH", Toast.LENGTH_SHORT).show()
				}
			}
		}

		buttonNext.setOnClickListener { (activity as ExampleActivity).viewPager.currentItem = (activity as ExampleActivity).viewPager.currentItem + 1 }
	}

	private fun assignViews() {
		ccp = rootView.findViewById(R.id.ccp)
		radioGroup = rootView.findViewById(R.id.radioGroup)
		radioEnglish = rootView.findViewById(R.id.radioEnglish)
		radioJapanese = rootView.findViewById(R.id.radioJapanese)
		radioSpanish = rootView.findViewById(R.id.radioSpanish)
		buttonNext = view!!.findViewById(R.id.button_next)
	}
}