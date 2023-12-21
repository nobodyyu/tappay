import 'package:flutter/material.dart';
import 'package:tappay/server_type.dart';
import 'dart:async';
import 'package:tappay/tappay.dart';
import 'package:tappay/tappay_plugin_exception.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _tappayPlugin = Tappay();

  @override
  void initState() {
    super.initState();
    showPayment();
  }

  Future<void> showPayment() async {
    try {
      await _tappayPlugin.linePay(
        appId: 0,
        appKey: '',
        serverType: ServerType.sandbox,
      );
    } on TappayPluginException catch (_) {
      rethrow;
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: const Center(
          child: Text('FUCK'),
        ),
      ),
    );
  }
}
