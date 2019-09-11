package `in`.hbb20.countrycodepickerproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class HomeActivity: AppCompatActivity() {

	private lateinit var textIntro: TextView
	private lateinit var textDefaultCountry: TextView
	private lateinit var textPreference: TextView
	private lateinit var textCustomMaster: TextView
	private lateinit var textSetCountry: TextView
	private lateinit var textGetCountry: TextView
	private lateinit var textFullNumber: TextView
	private lateinit var textCustomColor: TextView
	private lateinit var textCustomSize: TextView
	private lateinit var textCustomFont: TextView
	private lateinit var textCustomLanguage: TextView
	private lateinit var startDemo: Button

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_home)

		assignViews()
		printCountries()
	}

	private fun printCountries() {
		val list = Locale.getISOCountries() // Fetch list of ISO 3166 country codes
		for (i in list.indices) {
			list[i] = Locale("es", list[i]).displayName
			Log.d("Country Name", "printCountries: " + list[i])
		}
		//
		//                .map {
		//            // Create a new Locale in users default language, but set country to X
		//            it to Locale("bn", it).displayCountry // then extract the localized name of the country.
		//        }
		//            .toMap() // Turn into Map with ISO 3166 key and Localised country name as value
		//
		//        print(list)
	}

	private fun assignViews() {
		textIntro = findViewById(R.id.textIntro)
		setClick(textIntro, 0)

		textDefaultCountry = findViewById(R.id.textDefaultCountry)
		setClick(textDefaultCountry, 1)

		textPreference = findViewById(R.id.textCountryPreference)
		setClick(textPreference, 2)

		textCustomMaster = findViewById(R.id.textCustomMaster)
		setClick(textCustomMaster, 3)

		textSetCountry = findViewById(R.id.textSetCountry)
		setClick(textSetCountry, 4)

		textGetCountry = findViewById(R.id.textGetCountry)
		setClick(textGetCountry, 5)

		textFullNumber = findViewById(R.id.textFullNumber)
		setClick(textFullNumber, 6)

		textCustomColor = findViewById(R.id.textCustomColor)
		setClick(textCustomColor, 7)

		textCustomSize = findViewById(R.id.textCustomSize)
		setClick(textCustomSize, 8)

		textCustomFont = findViewById(R.id.textCustomFont)
		setClick(textCustomFont, 9)

		textCustomLanguage = findViewById(R.id.textCustomLanguage)
		setClick(textCustomLanguage, 10)

		startDemo = findViewById(R.id.buttonGo)
		startDemo.setOnClickListener {
			val i = Intent(baseContext, ExampleActivity::class.java)
			i.putExtra(EXTRA_INIT_TAB, 0)
			startActivity(i)
		}
	}

	private fun setClick(text: TextView, tabIndex: Int) {
		text.setOnClickListener {
			val i = Intent(baseContext, ExampleActivity::class.java)
			i.putExtra(EXTRA_INIT_TAB, tabIndex)
			startActivity(i)
		}
	}
}
