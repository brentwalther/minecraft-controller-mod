package net.brentwalther.controllermod.binding;

import com.google.common.collect.ImmutableList;
import net.brentwalther.controllermod.ControllerMod;
import net.brentwalther.controllermod.input.KeyPressInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;

import java.util.List;

/**
 * A binding that directly maps a controller button press to a keyboard key press. ButtonLayout down
 * is key down, and vice versa.
 */
public class ButtonPressToKeypressBinding implements ButtonBinding {

  private final int keycode;
  private PressState lastPressState;

  public ButtonPressToKeypressBinding(int keycode) {
    this.keycode = keycode;
    this.lastPressState = PressState.UNKNOWN;
  }

  @Override
  public List<VirtualInputAction> update(PressState pressState) {
    ControllerMod.getLogger()
        .info(
            "Receiving state "
                + pressState
                + " to "
                + this.hashCode()
                + " with last state "
                + lastPressState);
    if (pressState != lastPressState) {
      lastPressState = pressState;
      return ImmutableList.of(new KeyPressInputAction(keycode, pressState));
    }
    return ImmutableList.of();
  }
}
