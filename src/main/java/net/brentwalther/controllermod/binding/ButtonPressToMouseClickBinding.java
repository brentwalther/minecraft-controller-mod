package net.brentwalther.controllermod.binding;

import com.google.common.collect.ImmutableList;
import net.brentwalther.controllermod.input.ButtonPressInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;

import java.util.List;

/**
 * A binding that directly maps a controller button press to a mouse click. Controller button down
 * is mouse button down, and vice versa.
 */
public class ButtonPressToMouseClickBinding implements ButtonBinding {

  private final int mouseButton;
  private PressState lastPressState;

  public ButtonPressToMouseClickBinding(int mouseButton) {
    this.mouseButton = mouseButton;
    this.lastPressState = PressState.UNKNOWN;
  }

  @Override
  public List<VirtualInputAction> update(PressState pressState) {
    if (pressState != lastPressState) {
      lastPressState = pressState;
      return ImmutableList.of(ButtonPressInputAction.create(mouseButton, pressState));
    }
    return ImmutableList.of();
  }
}
