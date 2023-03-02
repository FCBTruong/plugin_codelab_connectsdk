package com.connectPlugin.plugin_codelab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.connectsdk.device.ConnectableDevice;
import com.connectsdk.device.ConnectableDeviceListener;
import com.connectsdk.device.DevicePicker;
import com.connectsdk.discovery.DiscoveryManager;
import com.connectsdk.discovery.DiscoveryManager.PairingLevel;
import com.connectsdk.discovery.DiscoveryProvider;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.DeviceService.PairingType;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.command.ServiceCommandError;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** PluginCodelabPlugin */
public class PluginCodelabPlugin
  extends FlutterActivity
  implements FlutterPlugin, MethodCallHandler {

  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private DiscoveryManager mDiscoveryManager;

  @Override
  public void onAttachedToEngine(
    @NonNull FlutterPluginBinding flutterPluginBinding
  ) {
    DiscoveryManager.init(getApplicationContext());
    mDiscoveryManager = DiscoveryManager.getInstance();
    mDiscoveryManager.registerDefaultDeviceTypes();
    mDiscoveryManager.setPairingLevel(PairingLevel.ON);
    DiscoveryManager.getInstance().start();

    channel =
      new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "discovery");
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    System.out.println("calling getAllDevices");
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("getNumberDevices")) {
      Log.d("Log:", "Calling getNumberDevices...");
      System.out.println("calling getNumberDevices");

      Map<String, ConnectableDevice> devices = mDiscoveryManager.getAllDevices();

      result.success(Integer.toString(devices.size()));
    } else if (call.method.equals("getAllDevices")) {
      List<ConnectableDevice> imageDevices = new ArrayList<ConnectableDevice>();
      for (ConnectableDevice device : DiscoveryManager
        .getInstance()
        .getAllDevices()
        .values()) {
        imageDevices.add(device);
      }

      result.success(imageDevices);
    } else if (call.method.equals("initDiscoveryManager")) {} else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}
