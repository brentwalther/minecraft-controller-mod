package net.brentwalther.controllermod.binding;

import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;

import java.util.List;

/**
 * A binding that maps an arbitrary controller button press to a specific {@link
 * VirtualInputAction}.
 */
public interface ButtonBinding {
  /**
   * Called when there is an update to the press state of the bound button.
   *
   * @param pressState the new state of the button. There is no guarantee that it is different than
   *     the last time {@code update()} was called.
   * @return a list of actions that should be performed from the updated state. The list can be
   *     empty.
   */
  List<VirtualInputAction> update(PressState pressState);
}
