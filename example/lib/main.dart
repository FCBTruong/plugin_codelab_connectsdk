import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:plugin_codelab/plugin_codelab.dart';
import 'package:flutter/foundation.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _pluginCodelabPlugin = PluginCodelab();
  String _numDevices = "null.";

  @override
  void initState() {
    super.initState();
    _test(); // get number of devices detected
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> _test() async {
    String numDevices;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      numDevices = await _pluginCodelabPlugin.getNumberDevices() ??
          'Unknown TV devices';
    } on PlatformException {
      numDevices = 'Failed to get Devices.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.

    setState(() {
      _numDevices = numDevices;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Number of TV detected: $_numDevices\n'),
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: _test,
          tooltip: 'Increment',
          child: const Icon(Icons.add),
        ),
      ),
    );
  }
}
