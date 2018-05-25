package net.brentwalther.controllermod.binding;

import com.google.common.collect.ImmutableList;
import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.Axis;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;
import net.brentwalther.controllermod.proto.ConfigurationProto.BindingType;
import net.brentwalther.controllermod.ui.MenuPointer;

import java.util.List;

/**
 * A factory that returns either button or axis bindings for various types of bindings. If a new
 * bindings is added to the proto, the first step is to decide whether it could be a button or axis
 * binding (or both!) and then add it here.
 */
public class BindingFactory {

  private static final ButtonBinding NO_OP_BUTTON_BINDING =
      (PressState pressState) -> ImmutableList.of();
  private static final AxisBinding NO_OP_AXIS_BINDING = (float value) -> ImmutableList.of();

  private final Configuration config;
  private final MenuPointer pointer;

  public BindingFactory(Configuration config, MenuPointer pointer) {
    this.config = config;
    this.pointer = pointer;
  }

  // Keycodes can be found here:
  // https://minecraft.gamepedia.com/Key_codes
  public ButtonBinding getButtonBinding(BindingType type) {
    switch (type) {
      case MENU_CLICK:
        return new ButtonPressToMouseClickBinding(0);
      case JUMP:
        return new ButtonPressToKeypressBinding(57);
      case OPEN_CLOSE_INVENTORY:
        return new ButtonPressToKeypressBinding(18);
      case SWAP_ITEM_IN_HANDS:
        return new ButtonPressToKeypressBinding(33);
      case TOGGLE_MENU:
        return new ButtonPressToKeypressBinding(1);
      case SWITCH_SELECTED_ITEM_LEFT:
        return new ButtonPressToScrollBinding(1);
      case SWITCH_SELECTED_ITEM_RIGHT:
        return new ButtonPressToScrollBinding(-1);
      case MENU_SCROLL_UP:
        return new ButtonPressToScrollBinding(10);
      case MENU_SCROLL_DOWN:
        return new ButtonPressToScrollBinding(-10);
    }
    return NO_OP_BUTTON_BINDING;
  }

  public AxisBinding getAxisBinding(BindingType type, float threshold) {
    final int cameraSensitivity = config.get().getCameraSensitivity();
    final int pointerSensitivity = config.get().getPointerSensitivity();
    switch (type) {
      case WALK:
        return new AxisBinding() {
          private final AxisBinding forward = new AxisThresholdToKeypressBinding(threshold, 17);
          private final AxisBinding backward = new AxisThresholdToKeypressBinding(threshold, 31);

          @Override
          public List<VirtualInputAction> update(float value) {
            // We negate the value for the backward binding because it expects a positive value.
            return ImmutableList.<VirtualInputAction>builder()
                .addAll(forward.update(value))
                .addAll(backward.update(-value))
                .build();
          }
        };
      case STRAFE:
        return new AxisBinding() {
          private final AxisBinding right = new AxisThresholdToKeypressBinding(threshold, 32);
          private final AxisBinding left = new AxisThresholdToKeypressBinding(threshold, 30);

          @Override
          public List<VirtualInputAction> update(float value) {
            // We negate the value for the left binding because it expects a positive value.
            return ImmutableList.<VirtualInputAction>builder()
                .addAll(right.update(value))
                .addAll(left.update(-value))
                .build();
          }
        };
      case ATTACK_DESTROY:
        return new AxisThresholdToButtonPressBinding(threshold, 0);
      case USE_ITEM_PLACE_BLOCK:
        return new AxisThresholdToButtonPressBinding(threshold, 1);
      case CAMERA_X:
        return new AxisThresholdToNormalizedMouseMovementBinding(
            Axis.X, threshold, cameraSensitivity);
      case CAMERA_Y:
        return new AxisThresholdToNormalizedMouseMovementBinding(
            Axis.Y, threshold, cameraSensitivity);
      case POINTER_X:
        return new AxisThresholdToNormalizedMouseMovementBinding(
            Axis.X, threshold, pointerSensitivity);
      case POINTER_Y:
        return new AxisThresholdToNormalizedMouseMovementBinding(
            Axis.Y, threshold, pointerSensitivity);
    }
    return NO_OP_AXIS_BINDING;
  }
}
