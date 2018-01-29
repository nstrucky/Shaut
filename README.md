# Shaut


Firebase Realtime Database implemented:
* You will need to provide your own google-services.json file linking to a Firebase project

App signing:
* Store the .jks file in the root directory of this project to sign a version of the app
* Create a keystore.properties file which includes:
    * storePassword=yourPassword
    * keyPassword=yourOtherPassword
    * keyAlias=aliasName
    * storeFile=yourKeystoreName.jks
