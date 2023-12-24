import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'package:tappay/server_type.dart';
import 'package:tappay/tp_pay_by_prime_model.dart';

import 'tappay_method_channel.dart';

abstract class TappayPlatform extends PlatformInterface {
  /// Constructs a TappayPlatform.
  TappayPlatform() : super(token: _token);

  static final Object _token = Object();

  static TappayPlatform _instance = MethodChannelTappay();

  /// The default instance of [TappayPlatform] to use.
  ///
  /// Defaults to [MethodChannelTappay].
  static TappayPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [TappayPlatform] when
  /// they register themselves.
  static set instance(TappayPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<void> initialize(
      {required int appId,
      required String appKey,
      required ServerType serverType}) {
    throw UnimplementedError('initialize() has not been implemented.');
  }

  Future<void> showPayment(
      {required int appId,
      required String appKey,
      required ServerType serverType}) {
    throw UnimplementedError('showPayment() has not been implemented.');
  }

  Future<void> linePay({
    required int appId,
    required String appKey,
    required ServerType serverType,
    required TPPayByPrimeModel tpPayByPrimeModel,
  }) {
    throw UnimplementedError('linePay() has not been implemented.');
  }

  Future<void> redirectToLinePayPage({required String paymentUrl}) {
    throw UnimplementedError(
        'redirectToLinePayPage() has not been implemented.');
  }

  Future<void> jkoPay({
    required int appId,
    required String appKey,
    required ServerType serverType,
    required TPPayByPrimeModel tpPayByPrimeModel,
  }) {
    throw UnimplementedError('jkoPay() has not been implemented.');
  }

  Future<void> redirectToJkoPayPage({required String paymentUrl}) {
    throw UnimplementedError(
        'redirectToJkoPayPage() has not been implemented.');
  }

  Future<void> easyWallet({
    required int appId,
    required String appKey,
    required ServerType serverType,
    required TPPayByPrimeModel tpPayByPrimeModel,
  }) {
    throw UnimplementedError('easyWallet() has not been implemented.');
  }

  Future<void> redirectToEasyWalletPage({required String paymentUrl}) {
    throw UnimplementedError(
        'redirectToEasyWalletPage() has not been implemented.');
  }

  Future<void> googlePay({
    required int appId,
    required String appKey,
    required ServerType serverType,
    required TPPayByPrimeModel tpPayByPrimeModel,
  }) {
    throw UnimplementedError('googlePay() has not been implemented.');
  }

  Stream<Map> get onResultReceived;
}
