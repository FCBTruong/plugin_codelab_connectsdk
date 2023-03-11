import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'plugin_codelab_platform_interface.dart';

/// An implementation of [PluginCodelabPlatform] that uses method channels.
class MethodChannelPluginCodelab extends PluginCodelabPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('discovery');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> getAllDevices() async {
    final version = await methodChannel.invokeMethod<String>('getAllDevices');
    return version;
  }

  @override
  Future<String?> getNumberDevices() async {
    final number = await methodChannel.invokeMethod<String>('getNumberDevices');
    return number;
  }

  @override
  Future<void> initDiscoveryManager() async {
    await methodChannel.invokeMethod<void>('initDiscoveryManager');
  }

  @override
  Future<void> setupPicker() async {
    await methodChannel.invokeMethod<void>('setupPicker');
  }

  @override
  Future<void> getPickerDialog() async {
    await methodChannel.invokeMethod<void>('getPickerDialog');
  }
}
