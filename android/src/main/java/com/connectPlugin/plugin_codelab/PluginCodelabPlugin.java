package com.connectPlugin.plugin_codelab;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
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
import com.connectsdk.service.DIALService;
import com.connectsdk.service.DeviceService;
import com.connectsdk.service.DeviceService.PairingType;
import com.connectsdk.service.capability.MediaPlayer;
import com.connectsdk.service.command.ServiceCommandError;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** PluginCodelabPlugin */
public class PluginCodelabPlugin
  implements FlutterPlugin, MethodCallHandler, ActivityAware {

  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private DiscoveryManager mDiscoveryManager;
  DevicePicker dp;
  ConnectableDevice mTV;
  AlertDialog dialog;
  AlertDialog pairingAlertDialog;
  AlertDialog pairingCodeDialog;
  MenuItem connectItem;
  Activity activity;
  Context context;
  private BinaryMessenger binaryMessenger;

  //SectionsPagerAdapter mSectionsPagerAdapter;

  @Override
  public void onAttachedToEngine(
    @NonNull FlutterPluginBinding flutterPluginBinding
  ) {
    binaryMessenger = flutterPluginBinding.getBinaryMessenger();
    context = flutterPluginBinding.getApplicationContext();
    DIALService.registerApp("Levak");
    DiscoveryManager.init(context);
    mDiscoveryManager = DiscoveryManager.getInstance();
    mDiscoveryManager.registerDefaultDeviceTypes();
    mDiscoveryManager.setPairingLevel(PairingLevel.ON);
    DiscoveryManager.getInstance().start();
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    System.out.println("On method call -----------");
    if (call.method.equals("getNumberDevices")) {
      Map<String, ConnectableDevice> devices = mDiscoveryManager.getAllDevices();

      result.success(Integer.toString(devices.size()));
    } else if (call.method.equals("getAllDevices")) {
      result.success(0);
    } else if (call.method.equals("setupPicker")) {
      this.setupPicker();
      result.success(0);
    } else if (call.method.equals("getPickerDialog")) {
      dp.getPickerDialog("this is a test", null);
      result.success(0);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromActivity() {
    // TODO("Not yet implemented")
  }

  @Override
  public void onReattachedToActivityForConfigChanges(
    @NonNull ActivityPluginBinding binding
  ) {
    // TODO("Not yet implemented")
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    Log.d("Log", "updateActivity --------");
    activity = binding.getActivity();

    channel = new MethodChannel(binaryMessenger, "discovery");
    channel.setMethodCallHandler(this);
    this.setupPicker();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    // TODO("Not yet implemented")
  }

  // Connect SDK API:
  private void setupPicker() {
    Log.d("Log:", "Setup Picker Device -------");
    dp = new DevicePicker(activity);
    dialog =
      dp.getPickerDialog(
        "Device List",
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(
            AdapterView<?> arg0,
            View arg1,
            int arg2,
            long arg3
          ) {
            mTV = (ConnectableDevice) arg0.getItemAtPosition(arg2);
            mTV.addListener(deviceListener);
            mTV.setPairingType(null);
            mTV.connect();
            connectItem.setTitle(mTV.getFriendlyName());

            dp.pickDevice(mTV);
          }
        }
      );

    pairingAlertDialog =
      new AlertDialog.Builder(context)
        .setTitle("Pairing with TV")
        .setMessage("Please confirm the connection on your TV")
        .setPositiveButton("Okay", null)
        .setNegativeButton(
          "Cancel",
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dp.cancelPicker();

              hConnectToggle();
            }
          }
        )
        .create();

    final EditText input = new EditText(context);
    input.setInputType(InputType.TYPE_CLASS_TEXT);

    final InputMethodManager imm = (InputMethodManager) context.getSystemService(
      Context.INPUT_METHOD_SERVICE
    );

    pairingCodeDialog =
      new AlertDialog.Builder(context)
        .setTitle("Enter Pairing Code on TV")
        .setView(input)
        .setPositiveButton(
          android.R.string.ok,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
              if (mTV != null) {
                String value = input.getText().toString().trim();
                mTV.sendPairingKey(value);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
              }
            }
          }
        )
        .setNegativeButton(
          android.R.string.cancel,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
              dp.cancelPicker();

              hConnectToggle();
              imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
          }
        )
        .create();

    Log.d("Log: ", "Done setupPicker");
  }

  public List<ConnectableDevice> getImageDevices() {
    List<ConnectableDevice> imageDevices = new ArrayList<ConnectableDevice>();

    for (ConnectableDevice device : DiscoveryManager
      .getInstance()
      .getCompatibleDevices()
      .values()) {
      if (device.hasCapability(MediaPlayer.Display_Image)) imageDevices.add(
        device
      );
    }

    return imageDevices;
  }

  private ConnectableDeviceListener deviceListener = new ConnectableDeviceListener() {
    @Override
    public void onPairingRequired(
      ConnectableDevice device,
      DeviceService service,
      PairingType pairingType
    ) {
      Log.d("2ndScreenAPP", "Connected to " + mTV.getIpAddress());

      switch (pairingType) {
        case FIRST_SCREEN:
          Log.d("2ndScreenAPP", "First Screen");
          pairingAlertDialog.show();
          break;
        case PIN_CODE:
        case MIXED:
          Log.d("2ndScreenAPP", "Pin Code");
          pairingCodeDialog.show();
          break;
        case NONE:
        default:
          break;
      }
    }

    @Override
    public void onConnectionFailed(
      ConnectableDevice device,
      ServiceCommandError error
    ) {
      Log.d("2ndScreenAPP", "onConnectFailed");
      connectFailed(mTV);
    }

    @Override
    public void onDeviceReady(ConnectableDevice device) {
      Log.d("2ndScreenAPP", "onPairingSuccess");
      if (pairingAlertDialog.isShowing()) {
        pairingAlertDialog.dismiss();
      }
      if (pairingCodeDialog.isShowing()) {
        pairingCodeDialog.dismiss();
      }
      registerSuccess(mTV);
    }

    @Override
    public void onDeviceDisconnected(ConnectableDevice device) {
      Log.d("2ndScreenAPP", "Device Disconnected");
      connectEnded(mTV);
      connectItem.setTitle("Connect");
      /*
      BaseFragment frag = mSectionsPagerAdapter.getFragment(
        mViewPager.getCurrentItem()
      );
      if (frag != null) {
        Toast
          .makeText(
            getApplicationContext(),
            "Device Disconnected",
            Toast.LENGTH_SHORT
          )
          .show();
        frag.disableButtons();
      } */// TODO check later
    }

    @Override
    public void onCapabilityUpdated(
      ConnectableDevice device,
      List<String> added,
      List<String> removed
    ) {}
  };

  void connectFailed(ConnectableDevice device) {
    if (device != null) Log.d(
      "2ndScreenAPP",
      "Failed to connect to " + device.getIpAddress()
    );

    if (mTV != null) {
      mTV.removeListener(deviceListener);
      mTV.disconnect();
      mTV = null;
    }
  }

  void connectEnded(ConnectableDevice device) {
    if (pairingAlertDialog.isShowing()) {
      pairingAlertDialog.dismiss();
    }
    if (pairingCodeDialog.isShowing()) {
      pairingCodeDialog.dismiss();
    }

    if (mTV.isConnecting == false) {
      mTV.removeListener(deviceListener);
      mTV = null;
    }
  }

  void registerSuccess(ConnectableDevice device) {
    Log.d("2ndScreenAPP", "successful register");
    /*
    BaseFragment frag = mSectionsPagerAdapter.getFragment(
      mViewPager.getCurrentItem()
    );
    if (frag != null) frag.setTv(mTV); */// TODO check later
  }

  public void hConnectToggle() {
    /* if (!activity.isFinishing()) {
      if (mTV != null) {
        if (mTV.isConnected()) mTV.disconnect();

        connectItem.setTitle("Connect");
        mTV.removeListener(deviceListener);
        mTV = null;
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
          if (mSectionsPagerAdapter.getFragment(i) != null) {
            mSectionsPagerAdapter.getFragment(i).setTv(null);
          }
        }
      } else {
        dialog.show();
      } 
    }*/// TODO later
  }
}
