Country Code Picker Library
===========================

Country Code Picker (CCP) <img src="https://raw.githubusercontent.com/hbb20/CountryCodePickerProject/master/app/src/main/res/drawable/img_f.png" width="70">  is an android library which provides easy way to search and select country phone code for the telephone number.

Introduction
------------
* CCP gives professional touch to your well designed form like login screen, sign up screen, edit profile screen. CCP removes confusion about how to add number and thus make view more understandable. Finally reduces mistakes in user input.
    * Phone number screen without CCP
          - <img src="https://raw.githubusercontent.com/hbb20/CountryCodePickerProject/master/app/src/main/res/drawable/img_c.png" width="300"> 
    * Above view can be transformed by using CCP
          - <img src="https://raw.githubusercontent.com/hbb20/CountryCodePickerProject/master/app/src/main/res/drawable/img_d.png" width="300">

How to add to project
--------------
Add this to your gradle file
  
````groovy
dependencies {
  compile 'com.hbb20:ccp:1.2'
}
````

Features
--------
 - If you prefer experience along with only reads, an demo android app is available that demonstrates all the features of this library. Click below button to download from Playstore.
<br/><a href="https://goo.gl/zI2cY2"><img src="http://www.android.com/images/brand/get_it_on_play_logo_large.png"/></a>

 - If you just want to read them, here you go.
    ### 1. Default country###
    *  Default country is the country where most of your target audience belong.
    *  The default country can be set through xml layout and programmatically as well.
   
   - ####Using xml####
        - add app:defaultCode="81" (replace 81 with your default country code) to xml layout.
        - If you set default country from xml, default country will set to CCP as soon as screen is displayed.
        - app:previewCountryNameCode is not compulsory. It just give you correct preview in layout preview of android studio. app:previewCountryNameCode will only displayed if it’s made up of 2 characters and app:defaultCode is specified. previewNameCode will not affect or will not change name of country name’s code at run time. Default country will be picked only on bases of defaultCode, previewCountryNameCode has nothing to do with it. 

     ````xml
      <com.hbb20.CountryCodePicker
       android:id="@+id/ccp"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"
       app:defaultCode="81" 
       app:previewNameCode="jp"/>
    ````
  - ####Programmatically####
  
      - To set default country programmatically, use ```` setDefaultCountryCode()```` method.
      - ```java setDefaultCountryCode() ``` will not set default country as selected country in CCP view. To set default country as selected country in CCP view, call ```` resetToDefaultCountry() ```` method.
      - ```java resetToDefaultCountry() ``` will set default country as selected country in CCP, it can be used at the time of form reset.
  - If you do not specify default country from xml, IN +91 (India) will be the default country until you update default country programmatically.
    
    
  ###2. Choose and set country
  
   - Choosing and setting country will update selected country in CCP view.
    ####Choose Country
             
      1. In order to choose country, click on CCP view.
      2. Then search country by country name or phone code or name code in dialog. 
      3. Click on county from list to choose
        

        ####Set country programmatically
           - Country in CCP can be using ```` setCountryForCode() ```` method.
           - If specified country code does not match with any country code, default country will be set in to CCP.
                
  
  ###3. Read selected country
  
    - Country's 3 properties (Country name, phone code and name code) can be read individually.
    
    ####Read selected country phone code
    
	 - To get selected country code as String type and without prefix “+”, use ```` getSelectedCountryCode(); ```` method. => “91”
	 - To get selected country code as String type and with prefix “+”, use ```` getSelectedCountryCodeWithPlus(); ```` method. => “+91”
	 - To get selected country code as int (Integer) type, use ```` getSelectedCountryCodeAsInt(); ```` method. => 91
	  
    ####Read selected country name
	 - To get selected country’s name, use ```` getSelectedCountryName();```` => “India”
  
    ####Read selected country name code
	 - To get selected country’s name code, use ```` getSelectedCountryNameCode();```` => “IN”
   
   ###4. Full number support
     
     - Full number is combination of country code and carrier number. for example, if country code is 91 and carrier number is 8866667722 then "918866667722" or "+918866667722" is the full number.

     #### Register carrierNumberEditText
     
	  - CarrierNumberEditText is the supplementary editText in which carrier number part of full number is entered.
	  - A carrierNumberEditText must be registered in order to work with full number.
	  - editText can be registered using ``` registerCarrierNumberEditText()```.
  
     ####Load full number####
       
        - To load full number, use ```` setFullNumber()```` method. In this method you need to pass the full number.
        - Prefix “+” is optional for full number so full number can be “91886667722” or “+918866667722”. Both will set same country and carrier number."
        - This will detect country code from full number and set that county to ccp and carrier number ( remaining part of full number other than country code) will be set as text of registered carrier editText.
        - If no valid country code is found in beginning part of full number, default country will be set to CCP and full number will be set as text of registered carrier editText.
  
     ####Get full number####
     
        - Use ``` getFullNumber();``` for full number without “+” prefix.
        - Use ``` getFullNumberWithPlus();``` for full number with “+” prefix.

    - A carrierNumberEditText must be registered before any function call of full number like ``` setFullNumber()``` or ``` getFullNumber() ```.
  - *None of the above functions validate the number format of phone.*

  ###5. Custom content color
  - Color of CCP content can be changed according to different background.
    
    ####Using XML
    - Add app:contentColor property to xml layout
      ````xml
      <com.hbb20.CountryCodePicker
            android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          app:contentColor="@color/custom_color"/>                        
      ````
    
    - <img src="https://raw.githubusercontent.com/hbb20/CountryCodePickerProject/master/app/src/main/res/drawable/img_a.png" width="300"> 

    ####Programmatically
    - To set color programmatically, use ```` setContentColor() ```` method.
    - <img src="https://raw.githubusercontent.com/hbb20/CountryCodePickerProject/master/app/src/main/res/drawable/img_b.png" width="300"> 


 ###6. Custom textSize
  - Text size of CCP content can be changed in order to match rest of the view of form.
    
    ####Using XML
    - Add app:contentColor property to xml layout
      ````xml
      <com.hbb20.CountryCodePicker
            android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          app:textSize="26sp"/>                        
      ````

    ####Programmatically
    - To set textSize programmatically, use ```` setTextSize() ```` method.
    
 ###7. Custom arrow size
  - Size if Down arrow of CCP view can be modified in order to match rest of the view of form.
    
    ####Using XML
    - Add app:contentColor property to xml layout
      ````xml
      <com.hbb20.CountryCodePicker
            android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          app:arrowSize="26sp"/>                        
      ````

    ####Programmatically
    - To set textSize programmatically, use ```` setArrowSize() ```` method.
   

Change log
--------

##### version 0.1.1
		- First upload with all basic functionalities
	
##### version 1.2
		- Support for textSize and arrowSize modification
