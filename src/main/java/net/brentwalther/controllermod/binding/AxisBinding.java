package net.brentwalther.controllermod.binding;

import net.brentwalther.controllermod.input.VirtualInputAction;

import java.util.List;

/**
 * A binding that maps an arbitrary controller axis value to to a specific {@link
 * VirtualInputAction}.
 */
public interface AxisBinding {
  /**
   * Called when there is an update to the value of an axis.
   *
   * @param value the new value of the axis. There is no guarantee that it is different than the
   *     last time {@code update()} was called.
   * @return a list of actions that should be performed as a result of the update. The list can be
   *     empty.
   */
  List<VirtualInputAction> update(float value);
}
