package `in`.hbb20.countrycodepickerproject

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.hbb20.CountryCodePicker

/**
 * A simple [Fragment] subclass.
 */
class CustomColorFragment: Fragment() {

	private lateinit var buttonNext: Button
	private lateinit var textViewTitle: TextView
	private lateinit var editTextPhone: EditText
	private lateinit var ccp: CountryCodePicker
	private lateinit var relativeColor1: RelativeLayout
	private lateinit var relativeColor2: RelativeLayout
	private lateinit var relativeColor3: RelativeLayout
	private var selectedColor = -1

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_custom_color, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		assignViews()
		setClickListener()
	}

	private fun setClickListener() {
		relativeColor1.setOnClickListener { setColor(1, getColor(context!!, R.color.color1)) }

		relativeColor2.setOnClickListener { setColor(2, getColor(context!!, R.color.color2)) }

		relativeColor3.setOnClickListener { setColor(3, getColor(context!!, R.color.color3)) }

		buttonNext.setOnClickListener { (activity as ExampleActivity).viewPager.currentItem = (activity as ExampleActivity).viewPager.currentItem + 1 }

		ccp.setOnClickListener {
			ccp.launchCountrySelectionDialog()
			if (selectedColor != -1) {
				ccp.contentColor = selectedColor
			}
		}
	}

	private fun setColor(selection: Int, color: Int) {
		ccp.contentColor = color
		//textView
		textViewTitle.setTextColor(color)

		//editText
		editTextPhone.setTextColor(color)
		editTextPhone.setHintTextColor(color)
		editTextPhone.background.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)

		//remove selected bg
		resetBG()

		//set selected bg
		val selectedBGColor = getColor(context!!, R.color.selectedTile)
		when (selection) {
			1 -> relativeColor1.setBackgroundColor(selectedBGColor)
			2 -> relativeColor2.setBackgroundColor(selectedBGColor)
			3 -> relativeColor3.setBackgroundColor(selectedBGColor)
		}

	}

	private fun resetBG() {
		relativeColor1.setBackgroundColor(getColor(context!!, R.color.dullBG))
		relativeColor2.setBackgroundColor(getColor(context!!, R.color.dullBG))
		relativeColor3.setBackgroundColor(getColor(context!!, R.color.dullBG))
	}

	private fun assignViews() {
		textViewTitle = view!!.findViewById(R.id.textView_title)
		editTextPhone = view!!.findViewById(R.id.editText_phone)
		ccp = view!!.findViewById(R.id.ccp)
		relativeColor1 = view!!.findViewById(R.id.relative_color1)
		relativeColor2 = view!!.findViewById(R.id.relative_color2)
		relativeColor3 = view!!.findViewById(R.id.relative_color3)
		buttonNext = view!!.findViewById(R.id.button_next)
	}
}
