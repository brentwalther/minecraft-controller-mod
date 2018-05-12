package net.brentwalther.controllermod.util;

import com.ivan.xinput.enums.XInputAxis;
import com.ivan.xinput.enums.XInputButton;
import net.brentwalther.controllermod.proto.ConfigurationProto;

/** A collection of static conversion methods. */
public class Conversions {

  public static ConfigurationProto.XInputButton deviceXInputButtonToProtoXInputButton(
      XInputButton button) {
    switch (button) {
      case A:
        return ConfigurationProto.XInputButton.A;
      case B:
        return ConfigurationProto.XInputButton.B;
      case X:
        return ConfigurationProto.XInputButton.X;
      case Y:
        return ConfigurationProto.XInputButton.Y;
      case BACK:
        return ConfigurationProto.XInputButton.BACK;
      case START:
        return ConfigurationProto.XInputButton.START;
      case LEFT_SHOULDER:
        return ConfigurationProto.XInputButton.LEFT_SHOULDER;
      case RIGHT_SHOULDER:
        return ConfigurationProto.XInputButton.RIGHT_SHOULDER;
      case LEFT_THUMBSTICK:
        return ConfigurationProto.XInputButton.LEFT_THUMBSTICK;
      case RIGHT_THUMBSTICK:
        return ConfigurationProto.XInputButton.RIGHT_THUMBSTICK;
      case DPAD_UP:
        return ConfigurationProto.XInputButton.DPAD_UP;
      case DPAD_DOWN:
        return ConfigurationProto.XInputButton.DPAD_DOWN;
      case DPAD_LEFT:
        return ConfigurationProto.XInputButton.DPAD_LEFT;
      case DPAD_RIGHT:
        return ConfigurationProto.XInputButton.DPAD_RIGHT;
      case GUIDE_BUTTON:
        return ConfigurationProto.XInputButton.GUIDE_BUTTON;
      default:
        return ConfigurationProto.XInputButton.UNKNOWN_BUTTON;
    }
  }

  public static ConfigurationProto.XInputAxis deviceXInputAxisToProtoXInputAxis(XInputAxis axis) {
    switch (axis) {
      case LEFT_THUMBSTICK_X:
        return ConfigurationProto.XInputAxis.LEFT_THUMBSTICK_X;
      case LEFT_THUMBSTICK_Y:
        return ConfigurationProto.XInputAxis.LEFT_THUMBSTICK_Y;
      case RIGHT_THUMBSTICK_X:
        return ConfigurationProto.XInputAxis.RIGHT_THUMBSTICK_X;
      case RIGHT_THUMBSTICK_Y:
        return ConfigurationProto.XInputAxis.RIGHT_THUMBSTICK_Y;
      case LEFT_TRIGGER:
        return ConfigurationProto.XInputAxis.LEFT_TRIGGER;
      case RIGHT_TRIGGER:
        return ConfigurationProto.XInputAxis.RIGHT_TRIGGER;
      case DPAD:
        return ConfigurationProto.XInputAxis.DPAD;
      default:
        return ConfigurationProto.XInputAxis.UNKNOWN_AXIS;
    }
  }

  // Private constructor so this util class can't be instantiated.
  private Conversions() {}
}
