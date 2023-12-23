import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:tappay/tp_pay_by_prime_model.dart';

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
  Future<void> linePay({
    required int appId,
    required String appKey,
    required ServerType serverType,
    required TPPayByPrimeModel tpPayByPrimeModel,
  }) async {
    try {
      await methodChannel.invokeMethod<String>('linePay', {
        'appId': appId,
        'appKey': appKey,
        'serverType': serverType.toString(),
        'tpPayByPrimeModel': tpPayByPrimeModel.toMap(),
      });
    } on PlatformException catch (_) {
      rethrow;
    }
  }

  @override
  Future<void> redirectToLinePayPage({required String paymentUrl}) async {
    try {
      await methodChannel.invokeMethod<String>('redirectToLinePayPage', {
        'paymentUrl': paymentUrl,
      });
    } on PlatformException catch (_) {
      rethrow;
    }
  }

  @override
  Future<void> jkoPay({
    required int appId,
    required String appKey,
    required ServerType serverType,
    required TPPayByPrimeModel tpPayByPrimeModel,
  }) async {
    try {
      await methodChannel.invokeMethod<String>('jkoPay', {
        'appId': appId,
        'appKey': appKey,
        'serverType': serverType.toString(),
        'tpPayByPrimeModel': tpPayByPrimeModel.toMap(),
      });
    } on PlatformException catch (_) {
      rethrow;
    }
  }

  @override
  Future<void> redirectToJkoPayPage({required String paymentUrl}) async {
    try {
      await methodChannel.invokeMethod<String>('redirectToJkoPayPage', {
        'paymentUrl': paymentUrl,
      });
    } on PlatformException catch (_) {
      rethrow;
    }
  }

  @override
  Future<void> easyWallet({
    required int appId,
    required String appKey,
    required ServerType serverType,
    required TPPayByPrimeModel tpPayByPrimeModel,
  }) async {
    try {
      await methodChannel.invokeMethod<String>('easyWallet', {
        'appId': appId,
        'appKey': appKey,
        'serverType': serverType.toString(),
        'tpPayByPrimeModel': tpPayByPrimeModel.toMap(),
      });
    } on PlatformException catch (_) {
      rethrow;
    }
  }

  @override
  Future<void> redirectToEasyWalletPage({required String paymentUrl}) async {
    try {
      await methodChannel.invokeMethod<String>('redirectToEasyWalletPage', {
        'paymentUrl': paymentUrl,
      });
    } on PlatformException catch (_) {
      rethrow;
    }
  }

  @override
  Stream<Map> get onResultReceived => eventChannel
      .receiveBroadcastStream()
      .map((dynamic event) => event as Map);
}
