Country Code Picker Library
===========================

If you are looking for an android library for Country Selector or Country Spinner or Country Phone Code selector, this is the perfect place for you.

Country Code Picker (CCP) <img src="https://farm6.staticflickr.com/5726/30960801342_6e65c7ddd5_m.jpg" width="100"> or
<img src="https://farm5.staticflickr.com/4468/23591251898_f8c5e8393a_b.jpg" width="230">
  is an android library which provides an easy way to search and select country or country phone code for the telephone number.
  
  ![AwesomeCCPLIbrary](https://i.makeagif.com/media/10-02-2017/RyO2k_.gif)

Introduction
------------
* Give a professional touch to your well designed form like login screen, sign up screen, edit profile screen with CCP. When used as phone code picker, it helps by removing confusion about how to add phone number and making view more understandable. 
	  
* With CCP you can get following views easily without boilerplate code. (Left: Phone code selector. Right: Country Selector)

    - <img src="https://farm6.staticflickr.com/5625/30296514763_e3af239e2c_z.jpg" width="300">     <img src="https://farm5.staticflickr.com/4343/23591138638_45d0f08daf_b.jpg" width="400">    
    
* Tapping on CCP will open a dialog to search and select country (Left: Phone code selector. Right: Country Selector)
    - <img src="https://farm6.staticflickr.com/5686/30982885732_9e91ede573_b.jpg" width="300"> <img src="https://farm5.staticflickr.com/4384/37440899521_d19781dc52_b.jpg" width="300">

How to add to your project
--------------

Add this to your gradle file and sync

  ````groovy
          dependencies {
            implementation 'com.hbb20:ccp:2.2.5'
          }
   ````

  * If you are using version lower than 2.0.0 then please read [update guide](https://github.com/hbb20/CountryCodePickerProject/wiki/Update-Guide-for-v2.0.0) before upgrading to v2.0.0. or above*

Features
--------
If you prefer experience along with explanations, an demo android app is available that demonstrates all the features of this library. Click below button to download from Playstore.
<br/><a href="https://goo.gl/zI2cY2"><img src="https://raw.githubusercontent.com/hbb20/CountryCodePickerProject/master/playstore.jpeg"/></a>

* [Super easy to integrate ](https://github.com/hbb20/CountryCodePickerProject/wiki/How-to-integrate-into-your-project)
* [Full Number Support](https://github.com/hbb20/CountryCodePickerProject/wiki/Full-Number-Support)
	- Auto-Formatting
	- Number Validation
	- Validity Change Listener
	- Read / Load full number
* [Use as Country Selector / Country Spinner](https://github.com/hbb20/CountryCodePickerProject/wiki/Use-as-a-Country-Selector)
* [Country preference](https://github.com/hbb20/CountryCodePickerProject/wiki/Country-Preference)
* [Custom master list](https://github.com/hbb20/CountryCodePickerProject/wiki/Custom-Master-Country-List)
* [Country Selection Change Listener](https://github.com/hbb20/CountryCodePickerProject/wiki/Country-Change-Listener)
* [Multi-language support](https://github.com/hbb20/CountryCodePickerProject/wiki/Multi-Language-Support)
* Customizable [CCP theme](https://github.com/hbb20/CountryCodePickerProject/wiki/CCP-Theme-Customization) and [Dialog Theme](https://github.com/hbb20/CountryCodePickerProject/wiki/CCP-Dialog-Theme-Customization) 
* [Auto detect Country](https://github.com/hbb20/CountryCodePickerProject/wiki/Auto-detect-country)
* [Auto detect Language](https://github.com/hbb20/CountryCodePickerProject/wiki/XML-Properties#appccp_autodetectlanguagetrue-default--false-)
* [Remembers last selection](https://github.com/hbb20/CountryCodePickerProject/wiki/XML-Properties#appccp_rememberlastselectiontrue-default--false-)
* [Fast Scroller](https://github.com/hbb20/CountryCodePickerProject/wiki/XML-Properties#appccpdialog_showfastscrollerfalse-default-true-) 


## Available XML properties
To check all xml properties available for CCP and CCP Selection ppDialog, please visit the [wiki page](https://github.com/hbb20/CountryCodePickerProject/wiki/XML-Properties).

Change log
----------

##### Version 2.2.5 (March 18, 2019)
- Support for Custom Title, Search Hint and No result ack of CCP Dialog. [Read More](https://github.com/hbb20/CountryCodePickerProject/wiki/Custom-Dialog-Title--%7C-Search-Hint-%7C-Empty-result-ACK)
- Flag Emoji Support **(BETA)** 
  - Beware of [problems related to the Emoji Support](https://github.com/hbb20/CountryCodePickerProject/wiki/Flag-Emoji-Support). 
- Option to set arrow color [Read More](https://github.com/hbb20/CountryCodePickerProject/wiki/CCP-Theme-Customization#custom-arrow-color)

##### Version 2.2.4 (January 20, 2019)
- Adds Curacao Country  ([Pull request](https://github.com/hbb20/CountryCodePickerProject/pull/279) by [msalshaikhz](https://github.com/msalshaikhz))
- Updates libphonenumber version to 8.10.1
- Greek language support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/259) by [khanboy1989](https://github.com/khanboy1989))
- Resolves issue #256, #278, #282

##### Version 2.2.3 (October 12, 2018)
- Uzbek language support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/239) by [Mirmuhsin](https://github.com/Mirmuhsin))
- Afrikaans language support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/229) by [marilie](https://github.com/marilie))
- Czech language support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/235) by [Jakub Begera](https://github.com/jakubbegera))
- Distinguish language based on script in addition to language code. ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/236)  by  [Fantasycheese](https://github.com/Fantasycheese))
- Libya Flag Correction. ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/233) by [sreekanth-krishnan](https://github.com/sreekanth-krishnan))
- Keep.xml for correct Proguard configuration
- Option to override click listener [Read More](https://github.com/hbb20/CountryCodePickerProject/wiki/Handle-CCP-ClickListener-Manually)
- Updates [Android PhoneLib port](https://github.com/MichaelRocks/libphonenumber-android) to the current latest version (8.9.14)
- Option to set initial scroll to selected country. [Read More](https://github.com/hbb20/CountryCodePickerProject/wiki/XML-Properties#appccpdialog_initialscrolltoselectiontrue-default-false)

##### Version 2.2.2 (June 24, 2018)
- Option to remove flag from dialog. [Issue#189](https://github.com/hbb20/CountryCodePickerProject/issues/189)
- Adds Farsi Language Support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/202) By [Ayhan Salami](https://github.com/ayhansalami))
- Adds Slovak Language Support ([Pull Request](https://github.com/hbb20/CountryCodePickerProject/pull/207) By [smiesnyrobert](https://github.com/smiesnyrobert))
- Bug fixes. [Issue#199](https://github.com/hbb20/CountryCodePickerProject/issues/199), [Issue#211](https://github.com/hbb20/CountryCodePickerProject/issues/211).
- Adds 9 new countries [Issue#206](https://github.com/hbb20/CountryCodePickerProject/issues/206)

##### Version 2.2.0 (April 20, 2018)
- Improved international phone formatting.
- Removes intermediate solution for national phone hint (ccp_hintExampleNumberFormat is no longer supported).
- Add/Correct some country flags.
- Uses latest [libphonenumber (Optimized for android)](https://github.com/MichaelRocks/libphonenumber-android) library.
- FailureListener interface to listen to onCountryAutoDetectionFailed [Issue#186](https://github.com/hbb20/CountryCodePickerProject/issues/186).

##### Version 2.1.9 (March 21, 2018)
- Adds Swedish language support ([Pull request](https://github.com/hbb20/CountryCodePickerProject/pull/167) by [Tobias Hillén](https://github.com/tobiashillen))
- Adds Kosovo Country  ([Pull request](https://github.com/hbb20/CountryCodePickerProject/pull/166) by [Aleksei Kliuev](https://github.com/aleksei-klv))
- Adds Guadeloupe Countries [Issue#170](https://github.com/hbb20/CountryCodePickerProject/issues/170)
- Understands difference between Isle of Man country and UK numbers. [Issue#168](https://github.com/hbb20/CountryCodePickerProject/issues/168)
- Feature to remove country name code from CCP Dialog [Issue#159](https://github.com/hbb20/CountryCodePickerProject/issues/159)
- Adds ccp_hintExampleNumberFormat to address cases like [Issue#154](https://github.com/hbb20/CountryCodePickerProject/issues/154)

For earlier versions, check [full log](https://github.com/hbb20/CountryCodePickerProject/wiki/Version-Change-Log).

------

## Credits
- [Fast Scroller library](https://github.com/FutureMind/recycler-fast-scroll) by [Future Minds](https://github.com/FutureMind)
- Optimized [Android port](https://github.com/MichaelRocks/libphonenumber-android) of [libphonenumber](https://github.com/googlei18n/libphonenumber) by [Michael Rozumyanskiy](https://github.com/MichaelRocks) 
- Hebrew translation by [David Brownstone](https://github.com/dfbrownstone)
- Chinese translation by [KENNETH2008](https://github.com/kenneth2008)
- Indonesia translation by [Maulana Firdaus](https://github.com/firdausmaulan)
- Spanish translation by [Armando Gomez](https://github.com/ArmandoGomez)
- Turkish translation by [Ugurcan Yildirim](https://github.com/ugurcany)
- Ukrainian language support by [VyacheslavMartynenko](https://github.com/VyacheslavMartynenko)
- Italian language support by [Fabrizio Gueli](https://github.com/fabriziogueli)
- Korean language support by [kduhyun](https://github.com/kduhyun)
- Portuguese translation corrections by [Elifázio Bernardes da Silva](https://github.com/elifazio)
- Flag border color option by [Maulana Firdaus](https://github.com/firdausmaulan)
- Dutch language support by [Bozintan Iuliana](https://github.com/IulianaDiana)
- Punjabi language support by [Dhruv Bhakta](https://github.com/DBB411)
- Arabic translation corrections by [Ahmed Wahdan](https://github.com/WahdanZ)
- Swedish language support by [Tobias Hillén](https://github.com/tobiashillen)
- Addition of Kosovo Country by [Aleksei Kliuev](https://github.com/aleksei-klv)
- Uzbek language support by [Mirmuhsin](https://github.com/Mirmuhsin)
- Afrikaans language support by [marilie](https://github.com/marilie)
- Czech language support by [Jakub Begera](https://github.com/jakubbegera)
- Greek language support by [khanboy1989](https://github.com/khanboy1989)

# Contribution
- To add a new country, follow the [guide to add new country](https://github.com/hbb20/CountryCodePickerProject/wiki/Guide-to-add-new-country-in-list).
- To add a new Language support, follow the [guide to add new language support](https://github.com/hbb20/CountryCodePickerProject/wiki/Add-New-Language-Support).



## License

[Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

    Copyright (C) 2016 Harsh Bhakta

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
