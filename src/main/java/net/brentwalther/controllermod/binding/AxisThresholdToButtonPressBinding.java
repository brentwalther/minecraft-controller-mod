package net.brentwalther.controllermod.binding;

import com.google.common.collect.ImmutableList;
import net.brentwalther.controllermod.input.ButtonPressInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;

import java.util.List;

/**
 * A binding that maps an axis to a mouse button press. The passed in threshold is used to determine
 * where the line is drawn between mouse down and mouse up.
 */
public class AxisThresholdToButtonPressBinding implements AxisBinding {

  private final float threshold;
  private final int buttonNum;
  private float lastValue;

  public AxisThresholdToButtonPressBinding(float threshold, int buttonNum) {
    this.threshold = threshold;
    this.buttonNum = buttonNum;
  }

  @Override
  public List<VirtualInputAction> update(float value) {
    boolean isPastThreshold = value > threshold;
    boolean wasPastThreshold = lastValue > threshold;
    lastValue = value;
    if (isPastThreshold && !wasPastThreshold) {
      return ImmutableList.of(
          ButtonPressInputAction.create(buttonNum, PressState.IS_BECOMING_PRESSED));
    } else if (!isPastThreshold && wasPastThreshold) {
      return ImmutableList.of(
          ButtonPressInputAction.create(buttonNum, PressState.IS_BECOMING_UNPRESSED));
    }
    return ImmutableList.of();
  }
}
