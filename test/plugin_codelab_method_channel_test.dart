import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:plugin_codelab/plugin_codelab_method_channel.dart';

void main() {
  MethodChannelPluginCodelab platform = MethodChannelPluginCodelab();
  const MethodChannel channel = MethodChannel('plugin_codelab');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });

  test('getAllDevices', () async {
    expect(await platform.getAllDevices(), '42');
  });
}
