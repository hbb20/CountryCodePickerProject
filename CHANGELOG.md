# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]


## 2.3.9 - 2020-05-08
### Fixed
- US Flag Image to show correctly with border ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/396))

## 2.3.8 - 2020-02-06
### Added
- Slovenian language support by [pastafarianGit](https://github.com/pastafarianGit) ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/373))

## 2.3.7 - 2020-01-23
### Fixed
- Fixed [issue](https://github.com/hbb20/CountryCodePickerProject/issues/370) 

## 2.3.6 - 2020-01-20
### Added
- Marathi language support [Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/368) by [Kaustubh Kulkarni](https://github.com/kaustubhk24)
- `ccp_padding` attribute [Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/369) by [mina george](https://github.com/minageorge5080)
### Fixed
- Typo 

## 2.3.5 - 2019-12-30
### Added
- Adds `ccpDialog_background` property to set custom background resource for CCP Dialog.

## 2.3.4 - 2019-11-13
### Fixes
- Allow to set validity change listener to null ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/357) by [Michael Gray](https://github.com/mgray88))

## 2.3.3 - 2019-09-10
### Added
- Support for Kazakh ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/352) by [Zhanbolat Raimbekov](https://github.com/janbolat))

## 2.3.2 - 2019-09-10
### Added
- Changelog.md file ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/340) by [Antoine Jaury](https://github.com/Ajojo44))
### Changed
- App module converted to Kotlin ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/343) by [JoshuaBradbury](https://github.com/JoshuaBradbury))
- Improve FRENCH translation ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/334) by [m3-ra](https://github.com/m3-ra))
- Update gradle and plugin versions

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

## version 2.1.4 (Jan 29, 2018)
- Handles national trunk prefixes correctly. [Issue#149](https://github.com/hbb20/CountryCodePickerProject/issues/149)
- Feature to add selected country's example number as hint of carrier number edit text. [Wiki Page](https://github.com/hbb20/CountryCodePickerProject/wiki/Example-phone-number-as-edittext-hint)
- Corrected dimension of French Guiana flag ([Pull request](https://github.com/hbb20/CountryCodePickerProject/pull/148) by [Gautier MECHLING](https://github.com/Nilhcem))
- Feature to exclude specific countries from the list. [Wiki Page](https://github.com/hbb20/CountryCodePickerProject/wiki/Exclude-specific-countries)

## version 2.1.2 (Jan 6, 2018)
- Adds Punjabi language support [Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/141) By [Dhruv Bhakta](https://github.com/DBB411)
- Corrected Arabic translation by [Ahmed Wahdan](https://github.com/WahdanZ)
- Country name textView, Flag imageView, Arrow imageView are now publicly accessible through getter-setters.

## version 2.1.1 (Dec 13, 2017)
- Opens Country class (Renamed to "CCPCountry" to avoid naming confusion) ([issue#136](https://github.com/hbb20/CountryCodePickerProject/issues/136))
- Fixes preview in Android Studio 3
- Auto detects country while typing area code (among multiple countries with code "+1") ([issue#140](https://github.com/hbb20/CountryCodePickerProject/issues/140))
- Cursor color follows ccpDialog_searchEditTextTint color ([issue#131](https://github.com/hbb20/CountryCodePickerProject/issues/131))
- Possible fix for crash ([issue#67](https://github.com/hbb20/CountryCodePickerProject/issues/67))


## version 2.1.0 (Nov 17, 2017)
- Introduces countryAutoDetectionPref option (Default: SIM then NETWORK then LOCALE)
- Now default value for ccp_autoDetectCountry is *false* instead of true.
- Option to remember last selection.
- Uses android's port instead of libphonenumber. ([Suggestion](https://github.com/hbb20/CountryCodePickerProject/issues/127))

## version 2.0.9 (Nov 10, 2017)
- Adds dutch language support
- Updates build tool version

## version 2.0.8 (Nov 1, 2017)
- Corrects Myanmar flag
- Applies TypeFace to dialog text as well. [Read wiki.](https://github.com/hbb20/CountryCodePickerProject/wiki/Custom-TypeFace-(FontFamily))

## version 2.0.7 (Oct 28, 2017)
- Dialog events callback listeners
- Option to change visibility of down arrow
- Adds UAE as part of name
- Country name corrections for German language
- Corrects phone code for holy see
- Fixes crash for turkish language (for some devices)
- Option to get country's english name irrespective of selected language
- Corrects Turkey flag

## version 2.0.5
- Adds country "Iceland"
- Updates country auto detection method and order (1. Locale, 2. Network, 3. SIM).

## version 2.0.4

- Ukrainian language support
- Italian language support
- Updated libphonenumber library version to 8.8.2

## version 2.0.3
- Turkish language support
- Issue for initial popped status of keyboard for some specific devices irrespective of ccpDialog_keyboardAutoPopup value.

## version 2.0.2
- Few countries' name corrected for spanish language
- RTL layout [bug](https://github.com/hbb20/CountryCodePickerProject/issues/75) fix for dialog

## version 2.0.1
- Minor bug fix for showing name code
- Allowed customization of ccpDialog theme by changing colors of background, text and editText.

## version 2.0.0
*If you are using version lower than 2.0.0 then please read [update guide](https://github.com/hbb20/CountryCodePickerProject/wiki/Update-Guide-for-v2.0.0) before upgrading to v2.0.0.*

- Clear search query "X" button
- All attributes has prefix of ccp and ccpDialog (to avoid name space clashes) i.e "textSize" is now "ccp_textSize".
- Few attribute name changes to follow convention
    - defaultCode => ccp_default_PhoneCode
    - hideNameCode => ccp_showNameCode
    - keyboardAutoPopOnSearch => ccpDialog_keyboardAutoPopup
    - selectionDialogShowSearch => ccpDialog_allowSearch
    - ccpClickable => ccp_clickable
    - ccpSearchBox_showPhoneCode => ccpDialog_showPhoneCode
- Supports "match_parent" width attribute with text gravity option
- As you type formatting of carrier number in registerCarrierNumberEditText (Enabled by default)
- Feature of auto-detect language (disabled by default)
- Feature of auto-detect country (enabled by default)
- Validity checker for full number
- PhoneNumber validity change listener
- Differentiate countries with "+1" based on area code. Now full number that starts with +1 will set correct country.



## version 1.8
- Fast Scroller with index.

## version 1.7.9
- Lightweight: Long lists of countries are moved to xml from java. Now it consumes only minimum runtime memory.
- Adds missing flags of Sint Maarten and Cayman Islands
- There were some countries which were visible only when english is selected. This is fixed now and all countries will be appearing for all languages.
- Now it is easier to add new languages.
- Countries are now listed in ascending order specific to selected language rather than just English name.

## version 1.7.8
- Option to add border to ccp flag. This gives more clarity to flag if flag and background has same or almost similar color.
- Adds Palestine to country list.
- Change JAVANESE to INDONESIA

## version 1.7.7
- Option to hide phone code. Allows developers to use CCP as Country Selector and not code selector.

## version 1.7.6
- Typo fix for "Search...", "Select a country", "Results not found".
- Now "Results not found" text is shifted to top to avoid it from being hidden under bigger keyboards.

## version 1.7.5
- Fixes a problem which was toasting message for disabled search feature

## version 1.7.4
- Adds French Guyana, Martinique and Réunion to country list

## version 1.7.3
- Adds support for HEBREW
- Two variants of CHINESE are added CHINESE TRADITIONAL and CHINESE SIMPLIFIED
- Correct layout for RTL layouts
- Support to enable/disable click
- Option to hide search bar from selection dialog

## version 1.7.1
- Adds Flag thumbnail
- Adds option for full country name

## version 1.6.1
- Bug fix for getDefaultCountryCodeAsInt() and getDefaultCountryCodeAsInt()

## version 1.6
- Added country change listener

## version 1.5.1
- Changed ccp view size when code name is hidden

## version 1.5
- Custom master list
- Custom font
- Language support
- Optional KeyboardAutoPopup

## version 1.4
- Country preference
- Hide country name code option
- Default country using Country name code


## version 1.2
   - Support for textSize and arrowSize modification


## version 0.1.1
   - First upload with basic functionalities



# New Version Template

## [x.y.z] - 20YY-MM-DD
- PR_Desc ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/) by []())
- Issue_Desc ([Issue](https://github.com/hbb20/CountryCodePickerProject/issues/))
### Added
-
### Changed
-
### Deprecated
-
### Removed
-
### Fixed
-
### Security
-
