package net.brentwalther.controllermod.device;

import net.brentwalther.controllermod.proto.ConfigurationProto.XInputAxis;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputButton;

import javax.annotation.Nullable;

/** A specific control on a device. */
public class Control {

  /** The type of the control. Use in a switch statement! */
  public enum ControlType {
    BUTTON,
    AXIS,
  }

  public static Control button(XInputButton button) {
    return new Control(button, null);
  }

  public static Control axis(XInputAxis axis) {
    return new Control(null, axis);
  }

  private final XInputButton button;
  private final XInputAxis axis;

  private Control(XInputButton button, XInputAxis axis) {
    this.button = button;
    this.axis = axis;
  }

  public ControlType getType() {
    if (button != null) {
      return ControlType.BUTTON;
    } else {
      return ControlType.AXIS;
    }
  }

  /**
   * @return the button that represents this control. Null if {@link #getType()} !=
   *     ControlType.BUTTON
   */
  @Nullable
  public XInputButton getButton() {
    return button;
  }

  /**
   * @return the axis that represents this control. Null if {@link #getType()} != ControlType.AXIS
   */
  @Nullable
  public XInputAxis getAxis() {
    return axis;
  }

  @Override
  public String toString() {
    switch (getType()) {
      case AXIS:
        return getAxis().toString();
      case BUTTON:
        return getButton().toString();
    }
    return "Unknown control";
  }
}
