# Shaut
Shaut (pronounced "Shout") is a Twitter meets Tinder Android application.  The idea is that you can connect with people you could potentially meet in person. Also you can "Shaut" in your city and only your neighbors will hear it ;-).  Users can sign in with their email or Google accounts to set up a profile and "Move" to a city (set their location).  Only users in the same city will be able to see each other's "Shauts" and be able to send friend requests.  

Once two users have connected, a chat conversation starts between them.  When one user moves to another city, they retain all of their new friends, but enjoy the benefits of seeing people only in their area. 

## Project Build

**Firebase Features:**
* You will need to provide your own google-services.json file linking to a Firebase project
    * Authentication
        * Enable Google Sign-in for this project
    * Database (Firestore)
    * Storage 

**App signing**
* Store the .jks file in the root directory of this project to sign a version of the app
* Create a keystore.properties file in the root directory containing the following:
```
    storePassword=yourPassword
    keyPassword=yourOtherPassword
    keyAlias=aliasName
    storeFile=yourKeystoreName.jks
```
**API Keys**

Keys should all be saved in the keystore.properties file (created in above step).  
For the required keys listed below, use the variable name specified with "yourKeyName" 
replaced with the API key you receive for your project.

Required keys:
* Google Places API: ```googlePlacesApiKey=yourKeyName```
