package net.brentwalther.controllermod.binding;

import com.google.common.collect.ImmutableList;
import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.Axis;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;
import net.brentwalther.controllermod.proto.ConfigurationProto.BindingType;
import net.brentwalther.controllermod.ui.MenuPointer;

import java.util.List;

public class BindingFactory {

  private static final ButtonBinding NO_OP_BUTTON_BINDING =
      (PressState pressState) -> {
        return ImmutableList.of();
      };
  private static final AxisBinding NO_OP_AXIS_BINDING =
      (float value) -> {
        return ImmutableList.of();
      };

  private final int defaultCameraSensitivity;
  private final int defaultPointerSensitivity;
  private final MenuPointer pointer;

  public BindingFactory(Configuration config, MenuPointer pointer) {
    defaultCameraSensitivity = config.get().getCameraSensitivity();
    defaultPointerSensitivity = config.get().getPointerSensitivity();
    this.pointer = pointer;
  }

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
    }
    return NO_OP_BUTTON_BINDING;
  }

  public AxisBinding getAxisBinding(BindingType type, float threshold) {
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
        return new AxisThresholdToMouseMovementBinding(Axis.X, threshold, defaultCameraSensitivity);
      case CAMERA_Y:
        return new AxisThresholdToMouseMovementBinding(Axis.Y, threshold, defaultCameraSensitivity);
      case POINTER_X:
        return new AxisThresholdToMouseMovementBinding(Axis.X, threshold, defaultPointerSensitivity);
      case POINTER_Y:
        return new AxisThresholdToMouseMovementBinding(Axis.Y, threshold, defaultPointerSensitivity);
    }
    return NO_OP_AXIS_BINDING;
  }
}
