import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:ump/ump.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _consentStatus = '';

  @override
  void initState() {
    super.initState();
    getUserConsent(test: true);
  }

  Future<void> getConsentStatus() async {
    String consentStatus = '';
    try {
      consentStatus = await Ump.getConsentStatus();
    } on PlatformException {}

    setState(() {
      _consentStatus = consentStatus;
    });

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> getUserConsent({bool test = false}) async {
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      if (test) {
        // Reset user constent and send test Device ID
        Ump.resetUserConsent();

        Ump.getUserConsent(
          testDeviceID: '<YOUR TEST DEVICE ID>',
        );
      } else
        Ump.getUserConsent();
    } on PlatformException {}

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('UMP Consent example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              Text(
                'Consent Status: \n $_consentStatus',
                textAlign: TextAlign.center,
              ),
              RaisedButton(
                color: Colors.lightBlueAccent,
                onPressed: getConsentStatus,
                child: Text('Update Status'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
