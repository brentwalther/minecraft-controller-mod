package net.brentwalther.controllermod.binding;

import com.google.common.collect.ImmutableList;
import net.brentwalther.controllermod.input.MouseMovementInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.Axis;
import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * A binding that maps an axis with a value greater or equal to the passed in threshold to a mouse
 * movement in the same direction as the axis scaled by the passed sensitivity. The sensitivity gets
 * normalized over time.
 */
public class AxisThresholdToNormalizedMouseMovementBinding implements AxisBinding {

  private static final long NEVER_BEEN_UPDATED = -1;

  /**
   * The amount of ms that should pass before {@link #sensitivity} is fully applied to the axis
   * movement.
   */
  private static final long TIME_BETWEEN_UPDATES_MS = 25;

  private final Axis axis;
  private final float threshold;
  private final int sensitivity;
  private long lastUpdateTime;

  public AxisThresholdToNormalizedMouseMovementBinding(
      Axis axis, float threshold, int sensitivity) {
    this.axis = axis;
    this.threshold = threshold;
    this.sensitivity = sensitivity;
    this.lastUpdateTime = NEVER_BEEN_UPDATED;
  }

  @Override
  public List<VirtualInputAction> update(float value) {
    if (lastUpdateTime == NEVER_BEEN_UPDATED) {
      lastUpdateTime = Minecraft.getSystemTime() - TIME_BETWEEN_UPDATES_MS;
    }
    long timeSinceLastUpdateMs = Minecraft.getSystemTime() - lastUpdateTime;
    lastUpdateTime = Minecraft.getSystemTime();
    if (Math.abs(value) < threshold) {
      return ImmutableList.of();
    }
    float timeScaleFactor = timeSinceLastUpdateMs / (1.0f * TIME_BETWEEN_UPDATES_MS);
    Delta delta = getDelta(value).scaleBy(sensitivity).scaleBy(timeScaleFactor);
    return ImmutableList.of(new MouseMovementInputAction(delta.x, delta.y));
  }

  protected Delta getDelta(float value) {
    float dx = 0;
    float dy = 0;
    switch (axis) {
      case X:
        dx = value;
        break;
      case Y:
        dy = value;
    }
    return new Delta(dx, dy);
  }

  class Delta {
    public final float x;
    public final float y;

    public Delta(float x, float y) {
      this.x = x;
      this.y = y;
    }

    public Delta scaleBy(float scaleFactor) {
      return new Delta(this.x * scaleFactor, this.y * scaleFactor);
    }
  }
}
