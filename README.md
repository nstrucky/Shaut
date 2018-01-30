# Shaut


###Firebase Features:
* You will need to provide your own google-services.json file linking to a Firebase project
    * Authentication
        * Enable Google Sign-in for this project
    * Database
    * Storage

###App signing:
* Store the .jks file in the root directory of this project to sign a version of the app
* Create a keystore.properties file in the root directory which includes:
    * storePassword=yourPassword
    * keyPassword=yourOtherPassword
    * keyAlias=aliasName
    * storeFile=yourKeystoreName.jks

###API Keys

Keys should all be saved in the keystore.properties file (created in above step).  
For the required keys listed below, use the variable name specified with "yourKeyName" 
replaced with the API key you receive for your project.

Required keys:
* Google Places API: googlePlacesApiKey=yourKeyName