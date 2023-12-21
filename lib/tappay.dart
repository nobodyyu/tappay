import 'package:flutter/services.dart';
import 'package:tappay/tappay_plugin_exception.dart';

import 'server_type.dart';
import 'tappay_platform_interface.dart';

class Tappay {
  Future<void> initialize(
      {required int appId,
      required String appKey,
      required ServerType serverType}) async {
    try {
      await TappayPlatform.instance
          .initialize(appId: appId, appKey: appKey, serverType: serverType);
    } on PlatformException catch (e) {
      throw TappayPluginException(message: e.message ?? e.code);
    }
  }

  Future<void> showPayment(
      {required int appId,
      required String appKey,
      required ServerType serverType}) async {
    try {
      await TappayPlatform.instance
          .showPayment(appId: appId, appKey: appKey, serverType: serverType);
    } on PlatformException catch (e) {
      throw TappayPluginException(message: e.message ?? e.code);
    }
  }

  Future<void> linePay(
      {required int appId,
      required String appKey,
      required ServerType serverType}) async {
    try {
      await TappayPlatform.instance
          .linePay(appId: appId, appKey: appKey, serverType: serverType);
    } on PlatformException catch (e) {
      throw TappayPluginException(message: e.message ?? e.code);
    }
  }

  Future<void> redirectToLinePayPage({required String paymentUrl}) async {
    try {
      await TappayPlatform.instance
          .redirectToLinePayPage(paymentUrl: paymentUrl);
    } on PlatformException catch (e) {
      throw TappayPluginException(message: e.message ?? e.code);
    }
  }

  Stream<Map> get onResultReceived => TappayPlatform.instance.onResultReceived;
}
