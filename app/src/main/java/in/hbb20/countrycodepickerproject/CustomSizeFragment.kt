package `in`.hbb20.countrycodepickerproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass.
 */
class CustomSizeFragment: Fragment() {

	private lateinit var buttonNext: Button

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_custom_size, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		buttonNext = getView()!!.findViewById(R.id.button_next)
		buttonNext.setOnClickListener { (activity as ExampleActivity).viewPager.currentItem = (activity as ExampleActivity).viewPager.currentItem + 1 }
	}
}