
import 'plugin_codelab_platform_interface.dart';

class PluginCodelab {
  Future<String?> getPlatformVersion() {
    return PluginCodelabPlatform.instance.getPlatformVersion();
  }

  Future<String?> getNumberDevices() {
    return PluginCodelabPlatform.instance.getNumberDevices();
  }

  Future<String?> getAllDevices() {
    return PluginCodelabPlatform.instance.getAllDevices();
  }

  Future<void> initDiscoveryManager() {
    return PluginCodelabPlatform.instance.initDiscoveryManager();
  }
}
