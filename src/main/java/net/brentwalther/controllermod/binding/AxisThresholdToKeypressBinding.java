package net.brentwalther.controllermod.binding;

import com.google.common.collect.ImmutableList;
import net.brentwalther.controllermod.input.KeyPressInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;

import java.util.List;

/**
 * A binding that maps an axis to a keyboard key press. The passed in threshold is used to determine
 * where the line is drawn between key being down and up.
 */
public class AxisThresholdToKeypressBinding implements AxisBinding {

  private final float threshold;
  private final int keycode;
  private float lastValue;

  public AxisThresholdToKeypressBinding(float threshold, int keycode) {
    this.threshold = threshold;
    this.keycode = keycode;
  }

  @Override
  public List<VirtualInputAction> update(float value) {
    boolean isPastThreshold = value > threshold;
    boolean wasPastThreshold = lastValue > threshold;
    lastValue = value;
    if (isPastThreshold && !wasPastThreshold) {
      return ImmutableList.of(new KeyPressInputAction(keycode, PressState.IS_BECOMING_PRESSED));
    } else if (!isPastThreshold && wasPastThreshold) {
      return ImmutableList.of(new KeyPressInputAction(keycode, PressState.IS_BECOMING_UNPRESSED));
    }
    return ImmutableList.of();
  }
}
