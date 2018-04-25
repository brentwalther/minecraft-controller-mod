package net.brentwalther.controllermod.binding;

import com.google.common.collect.ImmutableList;
import net.brentwalther.controllermod.input.ScrollInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;

import java.util.List;

public class ButtonPressToScrollBinding implements ButtonBinding {

  private final int delta;
  private PressState lastPressState;

  public ButtonPressToScrollBinding(int delta) {
    this.delta = delta;
    this.lastPressState = PressState.UNKNOWN;
  }

  @Override
  public List<VirtualInputAction> update(PressState pressState) {
    if (pressState != lastPressState) {
      lastPressState = pressState;
      if (pressState == PressState.IS_BECOMING_PRESSED) {
        return ImmutableList.of(new ScrollInputAction(delta));
        }
    }
    return ImmutableList.of();
  }
}
