import 'dart:async';

import 'package:flutter/services.dart';

class FlutterUmp {
  static const platform = const MethodChannel('com.app.jffc/flutter_ump');

  static Future<void> getUserConsent({String testDeviceID = ""}) async {
    try {
      // If you pass an Test Device ID the function will assume you
      // what to test the UMP and will simulate an device locates in EEA.
      if (testDeviceID.trim().isNotEmpty)
        await platform.invokeMethod(
          'getUserConsent',
          <String, dynamic>{
            'testDeviceID': testDeviceID.trim(),
          },
        );
      else
        await platform.invokeMethod('getUserConsent');
    } on PlatformException catch (e) {
      print(e.message);
    }
  }

  static Future<void> resetUserConsent() async {
    try {
      await platform.invokeMethod('resetUserConsent');
    } on PlatformException catch (e) {
      print(e.message);
    }
  }

  static Future<String> getConsentStatus() async {
    Map<int, String> consentStatus = {
      0: 'UNKNOWN',
      1: 'NOT_REQUIRED',
      2: 'REQUIRED',
      3: 'OBTAINED'
    };
    int status;

    try {
      status = await platform.invokeMethod('getConsentStatus');
      return consentStatus[status];
    } on PlatformException catch (e) {
      return e.message;
    }
  }
}
