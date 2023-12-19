import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'server_type.dart';
import 'tappay_platform_interface.dart';

/// An implementation of [TappayPlatform] that uses method channels.
class MethodChannelTappay extends TappayPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('tappay');
  final eventChannel = const EventChannel('tappayEvent');

  @override
  Future<void> initialize(
      {required int appId,
      required String appKey,
      required ServerType serverType}) async {
    try {
      await methodChannel.invokeMethod<String>('initialize', {
        'appId': appId,
        'appKey': appKey,
        'serverType': serverType.toString(),
      });
    } on PlatformException catch (_) {
      rethrow;
    }
  }

  @override
  Future<void> showPayment(
      {required int appId,
      required String appKey,
      required ServerType serverType}) async {
    try {
      await methodChannel.invokeMethod<String>('showPayment', {
        'appId': appId,
        'appKey': appKey,
        'serverType': serverType.toString(),
      });
    } on PlatformException catch (_) {
      rethrow;
    }
  }

  @override
  Stream<String> get onPrimeReceived => eventChannel
      .receiveBroadcastStream()
      .map((dynamic prime) => prime as String);
}
