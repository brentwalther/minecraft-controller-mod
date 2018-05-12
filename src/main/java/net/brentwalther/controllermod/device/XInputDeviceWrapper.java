package net.brentwalther.controllermod.device;

import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.enums.XInputAxis;
import com.ivan.xinput.enums.XInputButton;
import com.ivan.xinput.listener.SimpleXInputDeviceListener;
import net.brentwalther.controllermod.ControllerMod;

public class XInputDeviceWrapper {

  private final XInputDevice device;

  private final boolean[] buttonPressedState = new boolean[XInputButton.values().length];

  public XInputDeviceWrapper(XInputDevice device) {
    this.device = device;
    device.addListener(
        new SimpleXInputDeviceListener() {
          @Override
          public void connected() {
            ControllerMod.getLogger().info("Device %d connected", device.getPlayerNum());
          }

          @Override
          public void disconnected() {
            ControllerMod.getLogger().info("Device %d disconnected", device.getPlayerNum());
          }

          @Override
          public void buttonChanged(final XInputButton button, final boolean pressed) {
            buttonPressedState[button.ordinal()] = pressed;
          }
        });
  }

  public void poll() {
    device.poll();
  }

  public float getAxisValue(XInputAxis axis) {
    float value = device.getComponents().getAxes().get(axis);
    switch (axis) {
      case RIGHT_TRIGGER:
      case LEFT_TRIGGER:
        // The triggers emits '1' in their default resting state (where your finger is not pressing
        // it at all). Though there may be good reasons for that, it's a bit counter-intuitive so we
        // flip the value here.
        return value;
      default:
        return value;
    }
  }

  public boolean isButtonPressed(XInputButton button) {
    return buttonPressedState[button.ordinal()];
  }

  public String getAxisName(int index) {
    return XInputAxis.values()[index].toString();
  }

  public String getButtonName(int index) {
    return XInputButton.values()[index].toString();
  }

  public Boolean isConnected() {
    return device.isConnected();
  }

  public int getBatteryLevel() {
    //        if (!XInputNatives14.isLoaded()) {
    return -1;
    //        }
    //        XInputBatteryInformation gamepadBattInfo =
    //                ((XInputDevice14)
    // device).getBatteryInformation(XInputBatteryDeviceType.GAMEPAD);
    //
    //        return gamepadBattInfo.getLevel().ordinal();
  }

  public String toString() {
    return "Player " + (device.getPlayerNum() + 1);
  }
}
