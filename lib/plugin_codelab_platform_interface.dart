import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'plugin_codelab_method_channel.dart';

abstract class PluginCodelabPlatform extends PlatformInterface {
  /// Constructs a PluginCodelabPlatform.
  PluginCodelabPlatform() : super(token: _token);

  static final Object _token = Object();

  static PluginCodelabPlatform _instance = MethodChannelPluginCodelab();

  /// The default instance of [PluginCodelabPlatform] to use.
  ///
  /// Defaults to [MethodChannelPluginCodelab].
  static PluginCodelabPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [PluginCodelabPlatform] when
  /// they register themselves.
  static set instance(PluginCodelabPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> getAllDevices() {
    throw UnimplementedError('getAllDevices() has not been implemented.');
  }

  Future<String?> getNumberDevices() {
    throw UnimplementedError('getNumberDevices() has not been implemented.');
  }

  Future<void> initDiscoveryManager() {
    throw UnimplementedError(
        'initDiscoveryManager() has not been implemented.');
  }

  Future<void> setupPicker() {
    throw UnimplementedError('setupPicker() has not been implemented.');
  }

  Future<void> getPickerDialog() {
    throw UnimplementedError('getPickerDialog() has not been implemented.');
  }
}
