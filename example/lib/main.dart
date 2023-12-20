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
      await _tappayPlugin.showLinePay(
        appId: 138112,
        appKey:
            'app_7ugMRqJ1QjAeXoCcEtqVYFwoptf5MPdZ0IO77awLzxv2p3m7ByW2ELjHQppt',
        serverType: ServerType.sandbox,
      );
    } on TappayPluginException catch (e) {
      print(e.message);
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
