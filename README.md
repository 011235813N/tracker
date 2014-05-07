tracker
=======

Traker is a simple Android app that sends your position periodically with alarm management.
In this repo you'll find the interface to a php db to save the posted geolocation


/
+ php - interface
+ GET
++ result.php // generates a json response with the last location.
+ POST
++ cosplayers // receives the incoming json sent from the app and insert it to db.

the apps runs from Android os 2.3+ and uses the Android Google Play Services Lib.

In order to run the project in your enviroment, remember download the Google Play Services Lib and add it as lib.



