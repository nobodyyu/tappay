import 'package:flutter_test/flutter_test.dart';
import 'package:tappay/server_type.dart';
import 'package:tappay/tappay_platform_interface.dart';
import 'package:tappay/tappay_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'package:tappay/tp_pay_by_prime_model.dart';

class MockTappayPlatform
    with MockPlatformInterfaceMixin
    implements TappayPlatform {
  @override
  Future<void> initialize(
      {required int appId,
      required String appKey,
      required ServerType serverType}) async {}

  @override
  Future<void> showPayment(
      {required int appId,
      required String appKey,
      required ServerType serverType}) async {}

  @override
  Future<void> linePay({
    required int appId,
    required String appKey,
    required ServerType serverType,
    required TPPayByPrimeModel tpPayByPrimeModel,
  }) async {}

  @override
  Future<void> redirectToLinePayPage({required String paymentUrl}) async {}

  @override
  Future<void> jkoPay({
    required int appId,
    required String appKey,
    required ServerType serverType,
    required TPPayByPrimeModel tpPayByPrimeModel,
  }) async {}

  @override
  Future<void> redirectToJkoPayPage({required String paymentUrl}) async {}

  @override
  Stream<Map> get onResultReceived => throw UnimplementedError();
}

void main() {
  final TappayPlatform initialPlatform = TappayPlatform.instance;

  test('$MethodChannelTappay is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelTappay>());
  });
}
