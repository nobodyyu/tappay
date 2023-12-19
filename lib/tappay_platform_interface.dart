import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'package:tappay/server_type.dart';

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

  Future<void> showLinePay(
      {required int appId,
      required String appKey,
      required ServerType serverType}) {
    throw UnimplementedError('showLinePay() has not been implemented.');
  }

  Stream<String> get onPrimeReceived;
}
