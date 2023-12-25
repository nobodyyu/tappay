import 'package:flutter/services.dart';
import 'package:tappay/merchant.dart';
import 'package:tappay/tappay_plugin_exception.dart';
import 'package:tappay/tp_pay_by_prime_model.dart';

import 'server_type.dart';
import 'tappay_platform_interface.dart';

class Tappay {
  static Merchant getMerchantFromPrime(String prime) {
    String merchantCode = prime.split('_')[0];
    switch (merchantCode) {
      case 'ln':
        return Merchant.linePay;
      case 'jk':
        return Merchant.jkoPay;
      case 'ew':
        return Merchant.easyWallet;
      case 'gp':
        return Merchant.googlePay;
      default:
        return Merchant.directPay;
    }
  }

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

  Future<void> linePay({
    required int appId,
    required String appKey,
    required ServerType serverType,
    required TPPayByPrimeModel tpPayByPrimeModel,
  }) async {
    try {
      await TappayPlatform.instance.linePay(
        appId: appId,
        appKey: appKey,
        serverType: serverType,
        tpPayByPrimeModel: tpPayByPrimeModel,
      );
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

  Future<void> jkoPay({
    required int appId,
    required String appKey,
    required ServerType serverType,
    required TPPayByPrimeModel tpPayByPrimeModel,
  }) async {
    try {
      await TappayPlatform.instance.jkoPay(
        appId: appId,
        appKey: appKey,
        serverType: serverType,
        tpPayByPrimeModel: tpPayByPrimeModel,
      );
    } on PlatformException catch (e) {
      throw TappayPluginException(message: e.message ?? e.code);
    }
  }

  Future<void> redirectToJkoPayPage({required String paymentUrl}) async {
    try {
      await TappayPlatform.instance
          .redirectToJkoPayPage(paymentUrl: paymentUrl);
    } on PlatformException catch (e) {
      throw TappayPluginException(message: e.message ?? e.code);
    }
  }

  Future<void> easyWallet({
    required int appId,
    required String appKey,
    required ServerType serverType,
    required TPPayByPrimeModel tpPayByPrimeModel,
  }) async {
    try {
      await TappayPlatform.instance.easyWallet(
        appId: appId,
        appKey: appKey,
        serverType: serverType,
        tpPayByPrimeModel: tpPayByPrimeModel,
      );
    } on PlatformException catch (e) {
      throw TappayPluginException(message: e.message ?? e.code);
    }
  }

  Future<void> redirectToEasyWalletPage({required String paymentUrl}) async {
    try {
      await TappayPlatform.instance
          .redirectToEasyWalletPage(paymentUrl: paymentUrl);
    } on PlatformException catch (e) {
      throw TappayPluginException(message: e.message ?? e.code);
    }
  }

  Future<void> googlePay({
    required int appId,
    required String appKey,
    required ServerType serverType,
    required TPPayByPrimeModel tpPayByPrimeModel,
  }) async {
    try {
      await TappayPlatform.instance.googlePay(
        appId: appId,
        appKey: appKey,
        serverType: serverType,
        tpPayByPrimeModel: tpPayByPrimeModel,
      );
    } on PlatformException catch (e) {
      throw TappayPluginException(message: e.message ?? e.code);
    }
  }

  Stream<Map> get onResultReceived => TappayPlatform.instance.onResultReceived;
}
