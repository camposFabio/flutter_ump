import 'package:flutter_ump/flutter_ump.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(MaterialApp(
    home: Home(),
  ));
}

class Home extends StatefulWidget {
  @override
  _HomeState createState() => _HomeState();
}

class _HomeState extends State<Home> {
  @override
  void initState() {
    super.initState();
    FlutterUmp.getUserConsent();
  }

  @override
  Widget build(BuildContext context) {
    return Container();
  }
}
