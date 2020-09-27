import 'dart:async';

import 'package:flutter/services.dart';

class Ump {
  static const platform = const MethodChannel('app.jffc/ump');

  static const Map<int, String> consentStatus = {
    0: 'UNKNOWN', // Consent status is unknown
    1: 'NOT_REQUIRED', // User consent not required.
    2: 'REQUIRED', // User consent required but not yet obtained.
    3: 'OBTAINED' // User consent obtained.
  };

  static Future<String> getUserConsent({String testDeviceID = ""}) async {
    Map<String, dynamic> testIDs;
    String result = consentStatus[0];
    try {
      // If you pass an Test Device ID the function will assume you
      // what to test the UMP and will simulate an device locates in EEA.

      if (testDeviceID.trim().isNotEmpty) {
        testIDs = <String, dynamic>{
          'testDeviceID': testDeviceID.trim(),
        };
      }
      platform.invokeMethod('getUserConsent', testIDs).then((status) {
        result = consentStatus[status ?? 0];
      });
    } on PlatformException catch (e) {
      print(e.message);
    }
    return result;
  }

  static Future<void> resetUserConsent() async {
    try {
      await platform.invokeMethod('resetUserConsent');
    } on PlatformException catch (e) {
      print(e.message);
    }
  }

  static Future<String> getConsentStatus() async {
    String result = consentStatus[0];

    try {
      platform.invokeMethod('getConsentStatus').then((status) {
        result = consentStatus[status ?? 0];
      });
    } on PlatformException catch (e) {
      return e.message;
    }
    return result;
  }
}
