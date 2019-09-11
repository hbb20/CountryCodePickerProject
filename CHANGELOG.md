# Changelog

## Version 2.3.1 (August 5, 2019)
- **AndroidX** 
- Fixes [#287](https://github.com/hbb20/CountryCodePickerProject/issues/287), [#299](https://github.com/hbb20/CountryCodePickerProject/issues/299), [#310](https://github.com/hbb20/CountryCodePickerProject/issues/310), [#311](https://github.com/hbb20/CountryCodePickerProject/issues/311), [#314](https://github.com/hbb20/CountryCodePickerProject/issues/314), [#316](https://github.com/hbb20/CountryCodePickerProject/issues/316), [#328](https://github.com/hbb20/CountryCodePickerProject/issues/328), [#332](https://github.com/hbb20/CountryCodePickerProject/issues/332), [#333](https://github.com/hbb20/CountryCodePickerProject/issues/333)
- Formats in national format 

## Version 2.2.9 (April 24, 2019)
- Solves [issue](https://github.com/hbb20/CountryCodePickerProject/issues/305) regarding valid numbers with leading 0 

## Version 2.2.8 (April 23, 2019)
- Vietnamese language support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/304) by [Ricardo Markiewicz](https://github.com/Gazer))

## Version 2.2.7 (April 4, 2019)
- Danish language support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/300) by [porkyhead](https://github.com/porkyhead))

## Version 2.2.6 (March 23, 2019)
- Option to deregister exitText.

## Version 2.2.5 (March 18, 2019)
- Support for Custom Title, Search Hint and No result ack of CCP Dialog. [Read More](https://github.com/hbb20/CountryCodePickerProject/wiki/Custom-Dialog-Title--%7C-Search-Hint-%7C-Empty-result-ACK)
- Flag Emoji Support **(BETA)** 
  - Beware of [problems related to the Emoji Support](https://github.com/hbb20/CountryCodePickerProject/wiki/Flag-Emoji-Support). 
- Option to set arrow color [Read More](https://github.com/hbb20/CountryCodePickerProject/wiki/CCP-Theme-Customization#custom-arrow-color)

## Version 2.2.4 (January 20, 2019)
- Adds Curacao Country  ([Pull request](https://github.com/hbb20/CountryCodePickerProject/pull/279) by [msalshaikhz](https://github.com/msalshaikhz))
- Updates libphonenumber version to 8.10.1
- Greek language support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/259) by [khanboy1989](https://github.com/khanboy1989))
- Resolves issue #256, #278, #282

## Version 2.2.3 (October 12, 2018)
- Uzbek language support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/239) by [Mirmuhsin](https://github.com/Mirmuhsin))
- Afrikaans language support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/229) by [marilie](https://github.com/marilie))
- Czech language support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/235) by [Jakub Begera](https://github.com/jakubbegera))
- Distinguish language based on script in addition to language code. ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/236)  by  [Fantasycheese](https://github.com/Fantasycheese))
- Libya Flag Correction. ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/233) by [sreekanth-krishnan](https://github.com/sreekanth-krishnan))
- Keep.xml for correct Proguard configuration
- Option to override click listener [Read More](https://github.com/hbb20/CountryCodePickerProject/wiki/Handle-CCP-ClickListener-Manually)
- Updates [Android PhoneLib port](https://github.com/MichaelRocks/libphonenumber-android) to the current latest version (8.9.14)
- Option to set initial scroll to selected country. [Read More](https://github.com/hbb20/CountryCodePickerProject/wiki/XML-Properties#appccpdialog_initialscrolltoselectiontrue-default-false)

## Version 2.2.2 (June 24, 2018)
- Option to remove flag from dialog. [Issue#189](https://github.com/hbb20/CountryCodePickerProject/issues/189)
- Adds Farsi Language Support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/202) By [Ayhan Salami](https://github.com/ayhansalami))
- Adds Slovak Language Support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/207) By [smiesnyrobert](https://github.com/smiesnyrobert))
- Bug fixes. [Issue#199](https://github.com/hbb20/CountryCodePickerProject/issues/199), [Issue#211](https://github.com/hbb20/CountryCodePickerProject/issues/211).
- Adds 9 new countries [Issue#206](https://github.com/hbb20/CountryCodePickerProject/issues/206)

## Version 2.2.0 (April 20, 2018)
- Improved international phone formatting.
- Removes intermediate solution for national phone hint (ccp_hintExampleNumberFormat is no longer supported).
- Add/Correct some country flags.
- Uses latest [libphonenumber (Optimized for android)](https://github.com/MichaelRocks/libphonenumber-android) library.
- FailureListener interface to listen to onCountryAutoDetectionFailed [Issue#186](https://github.com/hbb20/CountryCodePickerProject/issues/186).

## Version 2.1.9 (March 21, 2018)
- Adds Swedish language support ([Pull request](https://github.com/hbb20/CountryCodePickerProject/pull/167) by [Tobias Hillén](https://github.com/tobiashillen))
- Adds Kosovo Country  ([Pull request](https://github.com/hbb20/CountryCodePickerProject/pull/166) by [Aleksei Kliuev](https://github.com/aleksei-klv))
- Adds Guadeloupe Countries [Issue#170](https://github.com/hbb20/CountryCodePickerProject/issues/170)
- Understands difference between Isle of Man country and UK numbers. [Issue#168](https://github.com/hbb20/CountryCodePickerProject/issues/168)
- Feature to remove country name code from CCP Dialog [Issue#159](https://github.com/hbb20/CountryCodePickerProject/issues/159)
- Adds ccp_hintExampleNumberFormat to address cases like [Issue#154](https://github.com/hbb20/CountryCodePickerProject/issues/154)

For earlier versions, check [full log](https://github.com/hbb20/CountryCodePickerProject/wiki/Version-Change-Log).
