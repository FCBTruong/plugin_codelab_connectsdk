
import 'plugin_codelab_platform_interface.dart';

class PluginCodelab {
  Future<String?> getPlatformVersion() {
    return PluginCodelabPlatform.instance.getPlatformVersion();
  }
}
