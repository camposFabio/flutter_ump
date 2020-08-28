# flutter_ump

Flutter plugin for displaying the required GDPR Consent Message using Google User Messaging Platform (UMP. 


## AndroidManifest changes

UMP requires the App ID to be included in the `AndroidManifest.xml`. Failure
to do so will result in a error message on launch of your app.  The line should look like:

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="[APP_ID]"/>
```

where `[APP_ID]` is your AdMob App ID.  You must pass the same value when you 
initialize the plugin in your Dart code.

See https://goo.gl/fQ2neu for more information about configuring `AndroidManifest.xml`
and setting up your App ID.

## Google Funding Choices

You need to register an consent form for you application on [Google Funding Choices](https://fundingchoices.google.com/) to use this plugin.


## Usage

To use this plugin, add `flutter_ump` as a [dependency in your pubspec.yaml file](https://flutter.dev/platform-plugins/).

It's recomended to call this plugin on the application start, so include an call in the initState of your homepage:

``` dart
 @override
  void initState() {
    super.initState();
    FlutterUmp.getUserConsent();
  }

```
## Test

To test the consent form pass your Test Device ID when getting the user consent to Force Geography to simulate an device located in EEA

``` dart
    FlutterUmp.getUserConsent(testDeviceID: '<YOUR TEST DEVICE ID>');
```

### Reset Consent

You can reset the consent status calling 
``` dart
 Future<void> FlutterUmp.resetUserConsent(); 
 ``` 

 ### Consent Status

 You can get the consent status calling
 
 ``` dart
 Future<String> FlutterUmp.getConsentStatus()
 ```