package net.brentwalther.controllermod.device;

import com.google.common.collect.ImmutableList;
import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.enums.XInputAxis;
import com.ivan.xinput.enums.XInputButton;
import com.ivan.xinput.exceptions.XInputNotLoadedException;
import net.brentwalther.controllermod.ControllerMod;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DeviceManager {

  public static DeviceManager createNew() {
    DeviceManager manager = new DeviceManager();
    manager.setCurrentDevice(0);
    return manager;
  }

  private XInputDeviceWrapper currentDevice;
  private int currentDeviceIndex;
  private final boolean[] lastButtonPressStates = new boolean[XInputButton.values().length];
  private final Object currentDeviceLock = new Object();

  private final ImmutableList<XInputDeviceWrapper> devices;

  private DeviceManager() {
    XInputDevice[] devicesArr = new XInputDevice[0];
    try {
      devicesArr = XInputDevice.getAllDevices();
    } catch (XInputNotLoadedException e) {
      ControllerMod.getLogger()
          .fatal("XInput is not loaded yet DeviceManager tried to retrieve all devices.");
    }
    devices =
        Stream.of(devicesArr)
            .map((xInputDevice) -> new XInputDeviceWrapper(xInputDevice))
            .collect(ImmutableList.toImmutableList());
    currentDeviceIndex = 0;
    currentDevice = devices.get(currentDeviceIndex);
  }

  private void setCurrentDevice(int index) {
    // Clamp the index value to the range of valid values.
    int newIndex = Math.max(0, Math.min(index, devices.size() - 1));
    synchronized (currentDeviceLock) {
      currentDevice = devices.get(newIndex);
      Arrays.fill(lastButtonPressStates, false);
      this.currentDeviceIndex = newIndex;
    }
  }

  /** Returns the button changes that have occurred since the last call to this function. */
  public List<ButtonChange> getButtonChanges() {
    ImmutableList.Builder<ButtonChange> changes = ImmutableList.builder();
    synchronized (currentDeviceLock) {
      for (XInputButton button : XInputButton.values()) {
        boolean isPressed = currentDevice.isButtonPressed(button);
        if (isPressed && !lastButtonPressStates[button.ordinal()]) {
          lastButtonPressStates[button.ordinal()] = true;
          changes.add(new ButtonChange(button, true));
        } else if (!isPressed && lastButtonPressStates[button.ordinal()]) {
          lastButtonPressStates[button.ordinal()] = false;
          changes.add(new ButtonChange(button, false));
        }
      }
    }
    return changes.build();
  }

  public List<AxisValue> getAxisValues() {
    ImmutableList.Builder<AxisValue> axisValues = ImmutableList.builder();
    synchronized (currentDeviceLock) {
      for (XInputAxis axis : XInputAxis.values()) {
        axisValues.add(new AxisValue(axis, currentDevice.getAxisValue(axis)));
      }
    }
    return axisValues.build();
  }

  public String getCurrentDeviceName() {
    return currentDevice.toString();
  }

  /**
   * Polls the active device for new button and axis values. This should be called between calls to
   * getButtonChanges() and getAxisValues()
   */
  public void poll() {
    currentDevice.poll();
  }

  private void logCurrentDeviceState() {
    StringBuilder builder = new StringBuilder();
    synchronized (currentDevice) {
      for (XInputButton button : XInputButton.values()) {
        builder.append(currentDevice.isButtonPressed(button));
        builder.append(" ");
      }
      for (XInputAxis axis : XInputAxis.values()) {
        builder.append(String.format("%.2f", currentDevice.getAxisValue(axis)));
        builder.append(" ");
      }
    }
    ControllerMod.getLogger().info(builder.toString());
  }

  public void previousDevice() {
    setCurrentDevice(--currentDeviceIndex);
  }

  public void nextDevice() {
    setCurrentDevice(++currentDeviceIndex);
  }

  public static class ButtonChange {
    public final XInputButton button;
    public final boolean wasJustPressed;

    public ButtonChange(XInputButton button, boolean wasJustPressed) {
      this.button = button;
      this.wasJustPressed = wasJustPressed;
    }
  }

  public static class AxisValue {
    public final XInputAxis axis;
    public final float value;

    public AxisValue(XInputAxis axis, float value) {
      this.axis = axis;
      this.value = value;
    }
  }
}
