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
}
