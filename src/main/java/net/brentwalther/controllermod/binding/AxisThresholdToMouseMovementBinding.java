package net.brentwalther.controllermod.binding;

import com.google.common.collect.ImmutableList;
import net.brentwalther.controllermod.ControllerMod;
import net.brentwalther.controllermod.input.MouseMovementInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.Axis;

import java.util.List;

/**
 * A binding that maps an axis to a mouse movement directory if over the passed in threshold and
 * scaling by the passed in sensitivity.
 */
public class AxisThresholdToMouseMovementBinding implements AxisBinding {

  private final Axis axis;
  private final float threshold;
  private final int sensitivity;

  public AxisThresholdToMouseMovementBinding(Axis axis, float threshold, int sensitivity) {
    this.axis = axis;
    this.threshold = threshold;
    this.sensitivity = sensitivity;
    ControllerMod.getLogger()
        .info(
            "Init AxisThresholdToMouseMovementBinding: "
                + axis
                + " "
                + threshold
                + " "
                + sensitivity);
  }

  @Override
  public List<VirtualInputAction> update(float value) {
    if (Math.abs(value) < threshold) {
      return ImmutableList.of();
    }
    Delta delta = getDelta(value);
    return ImmutableList.of(new MouseMovementInputAction(delta.x, delta.y));
  }

  protected Delta getDelta(float value) {
    float dx = 0;
    float dy = 0;
    switch (axis) {
      case X:
        dx = value * sensitivity;
        break;
      case Y:
        dy = value * sensitivity;
    }
    return new Delta((int) dx, (int) dy);
  }

  class Delta {
    public final int x;
    public final int y;

    public Delta(int x, int y) {
      this.x = x;
      this.y = y;
    }
  }
}
